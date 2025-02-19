# DAA - L4 - 2024-11-22

Authors: REDACTED, REDACTED, Quentin Surdez

# Questions complémentaires

## 6.1 Persistance des préférences de tri

Pour sauvegarder le choix de l'option de tri des notes de manière persistante, nous recommandons l'utilisation des `SharedPreferences`. Cette solution s'impose comme la plus pertinente pour plusieurs raisons :

### Architecture et Performance

Les `SharedPreferences` offrent un excellent compromis entre simplicité d'implémentation et performances :

- Système de stockage clé-valeur natif d'Android optimisé pour les petites données
- Persistance garantie même après redémarrage de l'appareil
- Empreinte mémoire minimale comparée à une solution basée sur SQLite
- Nettoyage des données automatique en cas de désinstallation de l'application

### Implémentation proposée

```kotlin
@Singleton
class SortPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Sauvegarde l'option avec une transaction asynchrone
    fun saveSortOption(sortOption: SortOption) {
        prefs.edit()
            .putString(KEY_SORT_OPTION, sortOption.name)
            .apply() // Utilisation de apply() pour une opération asynchrone
    }

    // Récupère l'option avec une valeur par défaut
    fun getSavedSortOption(): SortOption = runCatching {
        val savedOption = prefs.getString(KEY_SORT_OPTION, null)
        if (savedOption != null) {
            SortOption.valueOf(savedOption)
        } else {
            SortOption.DATE_DESC
        }
    }.getOrDefault(SortOption.DATE_DESC)

    companion object {
        private const val PREFS_NAME = "notes_preferences"
        private const val KEY_SORT_OPTION = "sort_option"
    }
}
```

Les valeurs de tri peuvent être regroupées dans une enum (tel qu'implémenté dans notre code). Ensuite, nous n'aurions plus qu'à appliquer les changements et récupérer la valeur pour savoir quel est le
choix de l'utilisateur concernant le tri des notes.

## 6.2 Limitations de LiveData et alternatives

### Analyse des limitations de LiveData

LiveData présente plusieurs contraintes significatives :

1. **Limitations architecturales**
   - Synchrone par nature, ce qui peut bloquer le thread principal
   - Fortement couplé au cycle de vie Android
   - Difficile à utiliser dans du code Kotlin puretriever

2. **Limitations techniques**
   - Pas de support natif pour les opérations de transformation complexes
   - Chargement complet des données en mémoire
   - Gestion limitée des erreurs

### Solution alternative : Kotlin Flow

Kotlin Flow apparaît comme une solution plus moderne et flexible :

```kotlin
@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY :sortOrder")
    fun getNotes(sortOrder: String): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteById(id: Long): Flow<Note?>
}

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val sortPreferences: SortPreferences,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    fun getNotesStream(): Flow<List<Note>> = 
        sortPreferences.getSortOptionFlow()
            .flatMapLatest { sortOption ->
                noteDao.getNotes(sortOption.queryString)
            }
            .flowOn(ioDispatcher)
            .catch { exception ->
                Log.e("NoteRepository", "Error fetching notes", exception)
                emit(emptyList())
            }
}
```

Avantages de Flow :

- Intégration naturelle avec les coroutines Kotlin
- Support natif des opérations de transformation
- Meilleure gestion de la mémoire avec le traitement séquentiel
- Excellent support de la gestion des erreurs

## 6.3 Notes et RecyclerView
> Les notes affichées dans la RecyclerView ne sont pas sélectionnables ni cliquables. Comment procéderiez-vous si vous souhaitiez proposer une interface permettant de sélectionner une note pour l’éditer ?

Notre solution actuelle intègre déjà une partie de cette fonctionnalité à travers un pattern Observer personnalisé. L'implémentation repose sur une interface `OnClickListener` qui établit un contrat de communication entre notre `Adapter` et les composants qui l'utilisent.

Concrètement, notre `Adapter` est conçu pour accepter une instance implémentant cette interface via son constructeur, suivant ainsi le principe d'injection de dépendances. Cette approche présente plusieurs avantages :

1. **Découplage** : L'`Adapter` reste agnostique quant à l'action spécifique à effectuer lors d'un clic, respectant ainsi le principe de responsabilité unique (SRP).

2. **Flexibilité** : L'implémentation actuelle, qui affiche un simple `Toast`, peut facilement être remplacée par d'autres comportements sans modifier le code de l'`Adapter`. Par exemple :
   - Navigation vers un fragment d'édition
   - Ouverture d'une boîte de dialogue de confirmation
   - Déclenchement d'une animation
   - Activation d'un mode de sélection multiple

## Choix d'implémentation majeurs

## 1. Architecture MVVM et Organisation du code

#### Choix effectués

- Utilisation stricte du pattern MVVM avec séparation claire des responsabilités
- `ViewModel` comme point central de la logique métier
- Utilisation de `LiveData` pour la communication entre les couches
- `Repository` comme abstraction de la source de donnée

### 2. Gestion des données et Tri

#### Choix effectués

- Implémentation du tri via une enum class `SortType`
- Utilisation de `map` et `switchMap` pour les transformations de données
- Gestion des tris dans le `ViewModel` plutôt que dans l'`Adapter` -> permet le respect du principe de Responsabilité Unique (SRP) + cohérence avec le flux de données unidirectionnel

### 3. Gestion de la `RecyclerView`

#### Choix effectués

- Utilisation de `DiffUtil` pour des mises à jour efficaces
- Pattern `ViewBinding` pour un accès efficace aux vues
- Gestion des clicks via l'interface `OnClickListener`
- Utilisation d'`AsyncListDiffer` pour les opérations de différenciation asynchrones kotlin

### 4. UI adaptive

#### Choix effectués

- Support de différents form factors (smartphone et tablette)
- Interface différente selon le support (menu vs boutons)

### 5. Gestion de l'état

#### Approches choisies

- Utilisation de `LiveData` pour la réactivité
- État centralisé dans le `ViewModel`
- Communication unidirectionnelle des données (ViewModel $\rightarrow$ View)

### 6. Injection de dépendances

#### Choix effecutés

- Factory pattern pour la création du `ViewModel`
- Injection du `Repository` via l'Application
- Accès au `ViewModel` partagé via `activityViewModels`

## Plan de test sur émulateur pour l'application Notes

### 1. Tests de base et initialisation

#### Lancement de l'application

- Vérifier l'affichage initial correct de la liste
- Confirmer la présence des données de test (populate)
- Vérifier le compteur de notes

#### Vérifier la présence du menu avec toutes les options

- Tri par date de création
- Tri par date de réalisation
- Création de note aléatoire
- Suppression de toutes les notes

### 2. Tests de l'interface adaptative

#### Test sur smartphone

- Vérifier que le menu contient tous les boutons de contrôle
- Vérifier l'affichage en mode portrait et paysage
- Confirmer l'adaptation correcte de la RecyclerView
-

#### Test sur tablette

- Vérifier la présence du fragment avec les boutons de contrôle
- Confirmer que le menu ne contient pas les options de création/suppression
- Vérifier l'adaptation de la mise en page

### 3. Tests de la RecyclerView

#### Affichage des notes

- Vérifier les deux layouts différents (avec/sans Schedule)
- Confirmer l'affichage correct des icônes selon le type
- Valider l'affichage des dates au format demandé
- Vérifier la colorisation selon l'état

#### Vérifier le défilement

- Tester le scroll avec peu de notes
- Tester le scroll avec beaucoup de notes
- Vérifier la performance du défilement

### 4. Tests fonctionnels

#### 4.1 Tests de tri

##### Tri par date de création

- Activer le tri
- Vérifier l'ordre chronologique
- Vérifier la mise à jour en temps réel

##### Tri par date de réalisation

- Activer le tri
- Vérifier l'ordre des deadlines
- Vérifier le comportement avec les notes sans Schedule

#### 4.2 Tests de manipulation des données

##### Création de notes

- Créer plusieurs notes aléatoires
- Vérifier l'insertion dans la liste
- Confirmer la mise à jour du compteur
  
##### Suppression

- Supprimer toutes les notes
- Vérifier la mise à jour de l'interface
- Confirmer la mise à jour du compteur

### 5. Tests de persistance

#### Test du cycle de vie

- Quitter l'application
- Vérifier la persistance des données au redémarrage
- Confirmer le maintien de l'ordre de tri

### 6. Tests des cas limites

#### Comportement avec zéro note

- Supprimer toutes les notes
- Vérifier l'affichage
- Tester la création de nouvelles notes
  
