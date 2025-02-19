# DAA - L3 - 2024-10-27

Authors: REDACTED, REDACTED, Quentin Surdez

## Réponses aux questions

### 4.1
> Pour le champ remark, destiné à accueillir un texte pouvant être plus long qu’une seule ligne,
> quelle configuration particulière faut-il faire dans le fichier XML pour que son comportement
> soit correct ? Nous pensons notamment à la possibilité de faire des retours à la ligne, d’activer
> le correcteur orthographique et de permettre au champ de prendre la taille nécessaire.

1. **Permettre les retours à la ligne** : L'attribut `android:inputType="textMultiLine|textCapSentences|textAutoComplete|textAutoCorrect"` inclut `textMultiLine`, ce qui permet à l'utilisateur de saisir du texte sur plusieurs lignes. Cela active aussi les retours automatiques à la ligne et, couplé à `wrap_content` pour la hauteur (`layout_height="wrap_content"`), permet à l’élément de s'agrandir verticalement en fonction du contenu.

2. **Activer la correction orthographique** : Les attributs `textAutoCorrect` et `textAutoComplete` dans `android:inputType` activent le correcteur orthographique et la suggestion de mots si le clavier virtuel et le système le permettent.

3. **Ajuster l’alignement et la présentation du texte** : L'attribut `android:gravity="top|start"` permet au texte de commencer en haut à gauche, ce qui est une configuration typique pour un champ de saisie multi-lignes.

4. **Configurer l'option d'action du clavier** : `android:imeOptions="flagNoEnterAction"` désactive l'action par défaut sur le bouton "Entrée" du clavier, ce qui laisse cette touche libre pour effectuer des sauts de ligne plutôt qu'une action spéciale comme "Envoyer" ou "Rechercher".

5. **Ajouter une barre de défilement verticale** : Pour améliorer l'expérience utilisateur sur des contenus de texte plus longs, `android:scrollbars="vertical"` ajoute une barre de défilement verticale au champ, permettant de naviguer facilement dans le texte.

6. **Activer le défilement même sans atteindre la fin du texte** : `android:overScrollMode="always"` permet de scroller même si le texte n'occupe pas tout l'espace vertical disponible, rendant l’interface plus intuitive lorsque l’utilisateur souhaite vérifier ou modifier du contenu déjà saisi.

7. **Limiter le nombre de lignes de texte** : `android:minLines` et `android:maxLines` permettent de borner notre champ. Au minimum, ce dernier sera composé de deux lignes et au maximum de 5.

En conclusion, afin que l'utilisteur puisse aussi voir ce qu'il écrit, nous avons du ajouter un attribut à l'activité : 
`android:windowSoftInputMode="stateVisible|adjustPan"`. Cet attribut permet au soft keyboard d'être en dessous du champ de l'activité principale. Cela permet à l'utilisateur de voir ce qu'il écrit.


### 4.2
> Pour afficher la date sélectionnée via le DatePicker nous pouvons utiliser un DateFormat
> permettant par exemple d’afficher 12 juin 1996 à partir d’une instance de Date. Le formatage
> des dates peut être relativement différent en fonction des langues, la traduction des mois par
> exemple, mais également des habitudes régionales différentes : la même date en anglais
> britannique serait 12th June 1996 et en anglais américain June 12, 1996. Comment peut-on
> gérer cela au mieux ?

```kotlin
dateEdit.setText(DateUtils.formatDateTime(this, today,
            DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR))
```
Nous utilisons la classe `DateUtils` pour récupérer le format lié au système et l'appliquer à notre date. Cela permet d'avoir un format adapté à la langue configurée sur le téléphone.

### 4.3
> Si vous avez utilisé le MaterialDatePicker2 de la librairie Material. Est-il possible de limiter
> les dates sélectionnables dans le dialogue, en particulier pour une date de naissance il est
> peu probable d’avoir une personne née il y a plus de 110 ans ou à une date dans le futur.
> Comment pouvons-nous mettre cela en place ?

Il est possible d'utiliser les méthodes `setStart()` ainsi que `setEnd()`. Ces dernières vont prendre des dates en paramètres et permettre d'avoir un calendrier avec une date minimale, correspondant à la valeur dans `setStart()`, ainsi qu'une date maximale, correspondant à la valeur dans `setEnd()`.

Ainsi, le choix de la date par l'utilisateur est borné. La limitation de ce composant est que les dates supérieures à la date de fin sur le même mois sont toujours sélectionnable, on en reçoit la date valide via la callback de `addOnPositiveButtonClickListener`. Nous avons du revalider la date avant de l'afficher et de la stocker.

### 4.4
> Lors du remplissage des champs textuels, vous pouvez constater que le bouton « suivant »
> présent sur le clavier virtuel permet de sauter automatiquement au prochain champ à saisir,
> cf. Fig. 2. Est-ce possible de spécifier son propre ordre de remplissage du questionnaire ?
> Arrivé sur le dernier champ, est-il possible de faire en sorte que ce bouton soit lié au bouton
> de validation du questionnaire ?
> Hint : Le champ remark, multilignes, peut provoquer des effets de bords en fonction du clavier
> virtuel utilisé sur votre smartphone. Vous pouvez l’échanger avec le champ e-mail pour faciliter
> vos recherches concernant la réponse à cette question.

Oui, il est possible de spécifier un ordre de remplissage du questionnaire en utilisant l'attribut `android:nextFocusForward` dans le fichier XML. Cet attribut permet de définir le champ suivant qui doit être mis en focus lorsque l'utilisateur appuie sur le bouton "suivant". De plus, assigner l'option `android:imeOptions` à `actionNext` permet de définir le bouton "suivant" du clavier virtuel.

Dans l'exemple suivant, le focus va transiter du `Spinner` au `EditText` lorsque l'utilisateur appuie sur le bouton suivant.

```xml
<Spinner
  android:id="@+id/sectorSpinner"
  android:imeOptions="actionNext"
  android:nextFocusForward="@id/editWorkerCompany"
/>
<EditText
  android:id="@+id/editWorkerCompany"
  android:imeOptions="actionNext"
  anrdoid:nextFocusForward="@id/editWorkerName"
/>
```

Pour lier le bouton "suivant" du clavier virtuel au bouton de validation du questionnaire, il est possible d'utiliser l'attribut `android:imeOptions` avec la valeur `actionDone` sur le dernier champ du questionnaire. Cela permet de définir le bouton "suivant" du clavier virtuel comme un bouton de validation. Le problème est que le champ "Commentaire" est multi-ligne et que l'option `noFlagEnterAction` doit être utilisé pour permettre les retours à la ligne. Si on utilise le champ "Email" à la place, le XML ressemblerait à ceci :

```xml
<EditText
  android:id="@+id/editAdditionalEmail"
  android:imeOptions="actionDone"
/>
```

Il faut ensuite ajouter programmatiquement envoyer un clic au bouton de
validation lorsque le bouton "suivant" est pressé. Pour ce faire, le code
suivant est utilisé dans l'activité:

```kotlin
editAdditionalEmail = findViewById(R.id.editAdditionalEmail)
buttonOk = findViewById(R.id.buttonOk)

editAdditionalEmail.setOnEditorActionListener { v, actionId, event ->
    if (actionId == EditorInfo.IME_ACTION_DONE) {
        buttonOk.performClick()
        true
    } else {
        false
    }
}
```

## 4.5
> Pour les deux Spinners (nationalité et secteur d’activité), comment peut-on faire en sorte que
> le premier choix corresponde au choix null, affichant par exemple le label « Sélectionner » ?
> Comment peut-on gérer cette valeur pour ne pas qu’elle soit confondue avec une réponse ?

Ici, nous avons `extends` la classe `SpinnerAdapter` afin de lui donner un comportement qui s'accorde à nos besoins. 
Dans notre code, nous avons une `string` "Sélectionner" pour tous les `Spinner` comme valeur par défaut. Une amélioration possible serait de passer en paramètre de l'`Adapter` une string pour spécifier "Sélectionner un secteur", "Sélectionner une langue".

La gestion des valeurs montrées à l'utilisateur est relativement simple. Nous n'affichons pas la vue hint et affichons les autres. 
Une amélioration serait de faire en sorte qu'un blanc ne s'affiche pas dans la `DropDownList`. Cependant, le fait de mettre sa visibilité en `GONE` permet de ne pas la montrer à l'utilisateur et ainsi, elle n'est pas considérée comme une valuer potentielle. 

```kotlin
if (position == 0) {
            view.visibility = View.GONE
        } else {
            view.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        }
```

En plus de cette absence dans la `DropDownList`, nous avons décidé de mettre la valeur de ce hint en gris afin d'assurer 
que l'utilisateur comprenne que cela n'est pas une valeur possible.

## 1. Choix d'implémentation majeurs
### 1.1 Gestion des dates

- Utilisation de MaterialDatePicker pour une interface moderne et cohérente avec Material Design
- Implémentation de contraintes de dates (CalendarConstraints) :

  - Date minimum : 01.01.1920
  - Date maximum : aujourd'hui


- Utilisation de DateUtils.formatDateTime() pour l'affichage localisé des dates
- Stockage de la date en millisecondes (Long) pour une manipulation plus simple

### 1.2 Spinners personnalisés

- Création d'un SpinnerAdapter custom pour gérer :

  - Un hint en première position qui disparaît lors de la sélection
  - Coloration différente du hint (gris) par rapport aux autres items (noir)
  - Masquage du hint dans la dropdown list

- Séparation des données (strings.xml) de la logique d'affichage

### 1.3 Gestion des groupes de vues

- Utilisation de Group pour gérer les vues Étudiant/Employé
- Mise à jour dynamique des contraintes avec ConstraintSet pour maintenir un layout cohérent, ici, avoir une barrière aurait été plus simple à implémenter, mais nous sommes allés dans la direction de ConstraintSet
- Toggle de visibilité basé sur la sélection des RadioButtons

### 1.4 Actions et validation

- Bouton Cancel : réinitialisation complète du formulaire
- Bouton OK :

  - Validation de la sélection obligatoire du type (Étudiant/Employé)
  - Création d'objets typés (Student/Worker) selon la sélection
  - Logging des données soumises
  - Nous avons fait le choix de ne pas supprimer les données inscrites lors de l'appui sur le bouton OK. Dans l'idéal, les supprimer pour laisser un formulaire vierge pour un nouvel enregistrement serait plus intéressant. Pour des raisons de temps, nous ne l'avons pas implémenté. (comportment observé à 21h45, trop tard pour un chgmt ^^)

## 2. Plan de tests sur émulateur
### 2.1 Tests de saisie de date

```plaintext
Test DatePicker
1. Vérifier que la date par défaut est aujourd'hui
2. Tester la sélection d'une date valide (entre 1920 et aujourd'hui)
3. Vérifier que la sélection d'une date future n'est pas possible
4. Vérifier que la date s'affiche dans le format local correct
```

### 2.2 Tests des Spinners

```plaintext
Test Spinners (Nationalité et Secteur)
1. Vérifier que le hint est visible par défaut
2. Vérifier que le hint disparaît après sélection
3. Vérifier que le hint n'apparaît pas dans la dropdown list
4. Vérifier que toutes les options sont présentes et sélectionnables
5. Vérifier le comportement lors de la réinitialisation (bouton Cancel)
```

### 2.3 Tests de navigation

```plaintext
Test RadioButtons et Groups
1. Vérifier que rien n'est sélectionné par défaut
2. Vérifier que la sélection "Étudiant" :
   - Affiche les champs spécifiques étudiant
   - Cache les champs travailleur
3. Vérifier que la sélection "Employé" :
   - Affiche les champs spécifiques travailleur 
   - Cache les champs étudiant
4. Vérifier que les contraintes de layout sont correctes après chaque changement
```

### 2.4 Tests de formulaire

```plaintext
Test Validation et Soumission
1. Tester la soumission sans sélection de type (doit afficher une erreur)
2. Tester la soumission avec champs vides
3. Tester la soumission avec tous les champs remplis
4. Vérifier le format des données dans les logs

Test Réinitialisation
1. Remplir tous les champs
2. Appuyer sur Cancel
3. Vérifier que tous les champs sont vides
4. Vérifier que les spinners sont revenus à leur état initial
5. Vérifier que la date est revenue à aujourd'hui
```
### 2.5 Tests de chargement automatique

```plaintext
Test Préchargement
1. Tester le chargement des données étudiant via le menu
2. Tester le chargement des données travailleur via le menu
3. Vérifier que toutes les données sont correctement remplies
4. Vérifier que les spinners sont correctement positionnés
5. Vérifier que le bon type (Étudiant/Employé) est sélectionné
```
