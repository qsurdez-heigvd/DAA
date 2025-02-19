# DAA - L6 - 2025-01-13

Auteurs: REDACTED, REDACTED, Quentin Surdez

## Choix d'implémentation

### Généralités

- Nous avons choisi l'approche de Jetpack Compose pour découvrir expérimenter cette nouvelle approche et cela nous a facilité la vie dans le design des différentes parties, notamment le fait de pouvoir facilement créer des petits composants. Nous avons fait un `PhoneType` pour les radios buttons du choix du type de téléphone.
- A des fins de simplification de la logique, comme il n'était pas demandé de gérer la date d'anniversaire, nous avons changé le type Calendar vers String du champ `birthday`
- Pour toutes les opérations qui font des accès à l'API, nous les avons wrappée via un wrapper écrit dans `NetworkConnectivity.kt` du nom de `withNetworkCheck` permettant de ne lancer un bloc que si l'appareil accède à internet. Il serait notamment possible d'étendre ce wrapper pour vérifier si la connection est métrée ou non.
- Nous avons utilisé `Retrofit` et `okHttp` pour gérer nos requêtes HTTP et les logger dans la console
- Nous avons géré les coroutines en wrappant le contenu de toutes les méthodes de `ContactsViewModel.kt` dans des `viewModelScope.launch`,
- Nous avons stocké le contact sélectionné dans une `MutableLiveData` contenue dans le `ContactsViewModel`. Lors de la création d'un nouveau contact nous créons une instance avec tous les champs null, afin d'avoir un contact à remplir.

    ```kotlin
    private var _selectedContact = MutableLiveData<Contact?>()
    val selectedContact : LiveData<Contact?> get() = _selectedContact

    fun selectContact(contact: Contact?) {
        _selectedContact.value = contact
    }
    ```

- Tous les appels à l'API, implémentés dans `ContactApiServiceImpl.kt` sont emballés dans des directives `withContext(Dispatchers.IO)`. Le type `IO` a été choisi car les coroutines ne font que très peu de calculs CPU et passent la grande partie de leur temps à attendre sur le réseau.

### 4.1 Implémentation de l’inscription (enrollment)

L'UUID récupéré après la requête sur `/enroll` est persisté dans les `SharedPreferences`. Ainsi, l'enrollment survit au redémarrage de l'application. Extrait `ContactsRepository` lié:

```kotlin
/**
  * Shared preferences for storing the UUID.
  */
private val sharedPreferences: SharedPreferences =
    context.getSharedPreferences("contacts_prefs", Context.MODE_PRIVATE)

/**
  * Stores the UUID obtained during enrollment.
  * This is kept in memory and should be persisted for longer term storage.
  */
private var uuid: String?
    get() = sharedPreferences.getString("UUID", null)
    set(value) {
        sharedPreferences.edit().putString("UUID", value).apply()
    }
```

### 4.2 Création, modification et suppression de contacts

La classe `Contact.kt` a été modifée afin d'ajouter 2 champs. Le premier, `remote_id`, permet de stocker l'id distant du contact, et est nullable car nous ne la connaissons pas encore tant que la synchronisation d'un nouveau contact n'a pas eu lieu. Le 2ème champ est une énumération `Status`, décrit ci-dessous, qui permet de connaître l'état du contact par rapport à l'API.

```kotlin
// Extrait de Contact.kt
var remote_id: Long? = null,
var status: Status = Status.OK,

// Extrait de Status.kt
enum class Status {
    OK, MODIFIED, NEW, DELETED
}
```

Les différents états possibles sont les suivants:

1. `OK` signifie que le contact est dans le même état que l'API
1. `MODIFIED` signifie que le contact a été modifié par rapport à l'API
1. `NEW` signifie que le contact a été créé et n'existe pas encore sur l'API. Cela inclut aussi un contact créé et non synchronisé qui aurait été modifié, car vis à vis de l'API cela ne change rien, il n'existe pas encore sur le serveur.
1. `DELETED` signifie que le contact a été supprimé mais existe encore sur l'API.

L'objet `ContactDTO` et sa méthode associée `toDTO` (pour convertir un `Contact` vers un `ContactDTO`) ont étés implémentés. Le champ `id` du DTO est renseigné par le `remote_id` du modèle local `Contact`.

```kotlin
fun toDTO(contact: Contact): ContactDTO {
    return ContactDTO(
        id = contact.remote_id,
        name = contact.name,
        firstname = contact.firstname,
        [...]
```

La méthode `ContactsRepository.save(contact)` permet de synchroniser un contact. Elle est invoquée lors de la création, modification ou suppression d'un contact, ainsi que lors de la synchronisation complète.

```kotlin
when (contact.status) {
    Status.NEW -> tryCreateContact(contact)
    Status.MODIFIED -> tryUpdateContact(contact)
    Status.DELETED -> tryDeleteContact(contact.id)
    Status.OK -> {
        // Do nothing, the contact is already synchronized
        Log.d(TAG, "Contact is already synchronized: $contact")
    }
}
```

Les méthodes `tryCreateContact`, `tryUpdateContact` et `tryDeleteContact` ont toutes la même approche: sauver l'état localement, puis tenter d'effectuer le changement sur l'API. Si la connection n'est pas disponible ou que l'appel échoue, la synchronisation n'est pas re-agendée. Les changement peuvent être resynchronisé plus tard via le bouton de synchronisation.

**Gestion du cas limite d'édition d'un contact créé localement et non synchronisé**

Un cas limite apparait lorsqu'un contact est créé localement, mais que la synchronisation avec l'API échoue (par exemple par manque de connection réseau). La problématique est que le contact doit pouvoir être modifié, mais que son status vis-à-vis du serveur doit rester `NEW`.

La solution est l'implémentation d'une condition dans `ContactsRepository.tryCreateContact()` afin d'appeler `contactsDao.update()` au lieu de `contactsDao.insert()` si le contact existe déjà localement, mais qu'il n'est pas possible de le sauvegarder sur le serveur. De cette manière, l'édition d'un contact non synchronisé est possible sans que le status ne change à `MODIFIED`.

```kotlin
/**
* Try to create a contact remotely or just save changes locally
*/
private suspend fun tryCreateContact(contact: Contact) = withContext(Dispatchers.IO) {
    // If the contact already has an ID, it's only dirty and we just need to save and sync it
    contact.id?.let {
        try {
            contactsDao.update(contact)
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
            Log.d(TAG, "Failed to update contact $contact")
        }
    } ?: run {
        val id = contactsDao.insert(contact)
        contact.id = id
    }

    // appel à l'API...
```

Une autre condition est implémentée dans le callback appelé par le bouton `Save` afin de persister l'état `NEW` d'un contact lorsqu'on le modifie sans qu'il ai été synchronisé avec l'API.

```kotlin
EditContactScreen(
    contactsViewModel = contactsViewModel,
    onSave = { contact ->
        if (contact.status != Status.NEW) {
            contact.status = Status.MODIFIED
        }
        contactsViewModel.save(contact)
```

Si nous changions le status à `MODIFIED`, nous aurions une erreur au moment de la synchronisation avec l'API, puisque nous ferions un `PUT` sans avoir d'id (puisque `remote_id` sera null).

#### Soft delete des contacts

Les contacts sont _soft deleted_, c'est à dire que lorsqu'un contact est supprimé, il n'est pas effacé de la base de données, mais son status est changé à `DELETED`. Une requête `DELETE` est alors envoyée à l'API. En cas de succès, le contact est effacé de la base de données locale. Un filtre est appliqué sur la méthode permettant d'obtenir les contacts du DAO afin de ne pas afficher les contacts qui ont été _soft delete_.

Dans `ContactsDao` :

```kotlin
/**
* Retrieves all contacts that are not marked as deleted.
*
* @return A LiveData list of all non-deleted contacts.
*/
@Query("SELECT * FROM Contact WHERE status != 'DELETED'")
fun getAllContactsLiveData() : LiveData<List<Contact>>
```

### 4.3 Synchronisation de tous les contacts

La synchronisation complète des contacts avec l'API se fait via le bouton de synchronisation. La méthode `ContactsRepository.sync()` est invoquée et itère alors sur tous les contacts dirty (dont l'état diffère de `Status.OK`) afin de les synchroniser avec fonction `ContactsRepository.save(contact)` (déjà présentée). Ce mécanisme rend la synchronisation des contacts possible sur demande.

Fonction associée dans le `ContactsDAO` :

```kotlin
/**
* Retrieves all contacts that have a status other than 'OK'.
* These are considered dirty contacts that need actions towards the API.
*
* @return A list of dirty contacts.
*/
@Query("SELECT * FROM Contact WHERE status != 'OK'")
fun getDirtyContacts() : List<Contact>
```
