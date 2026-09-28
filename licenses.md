# TP4 - Audit de conformité des licences

Projet audité : **Weather API** (TP1–TP3), Maven/Spring Boot 4.1.1, Java 21.
Aucune dépendance n'a été ajoutée pour ce TP : l'audit porte sur l'existant.

## 1. Outil et méthode

Écosystème du projet = Java/Maven → outil utilisé : **`org.codehaus.mojo:license-maven-plugin`**,
goal `download-licenses` (résout l'arbre de dépendances complet, directes **et transitives**, et
associe à chaque artefact la ou les licences déclarées dans son POM).

Plugin déclaré dans [`pom.xml`](pom.xml) :

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>license-maven-plugin</artifactId>
    <version>2.5.0</version>
    <configuration>
        <licensesOutputDirectory>${project.build.directory}/generated-resources/licenses</licensesOutputDirectory>
        <licensesOutputFile>${project.build.directory}/generated-resources/licenses.xml</licensesOutputFile>
        <excludedScopes>test</excludedScopes>
        <failOnMissing>false</failOnMissing>
        <sortByGroupIdAndArtifactId>true</sortByGroupIdAndArtifactId>
    </configuration>
</plugin>
```

Commande exécutée :

```bash
mvn license:download-licenses
```

**Fichier de scan brut** : [`licenses/licenses.xml`](licenses/licenses.xml) (copie trackée du fichier
généré dans `target/generated-resources/licenses.xml`, qui est ignoré par git). 46 dépendances
résolues (le scope `test` est exclu, à l'exception de quelques artefacts dont la portée effective la
plus large dans le graphe reste `compile`, par ex. `slf4j-api` via `json-path`).

## 2. Classification par famille

Familles : **Permissive** (MIT, BSD, Apache-2.0…), **Copyleft** (GPL/AGPL/LGPL, EPL en copyleft
faible), **Propriétaire**.

| Package                                                     | Version     | Position (profondeur)                | Licence(s) déclarée(s)      | Famille                   |
| ----------------------------------------------------------- | ----------- | ------------------------------------ | --------------------------- | ------------------------- |
| org.springframework.boot:spring-boot-starter                | 4.1.1       | directe                              | Apache-2.0                  | Permissive                |
| org.projectlombok:lombok                                    | 1.18.46     | directe                              | MIT                         | Permissive                |
| org.springframework.boot:spring-boot-starter-web            | 4.1.1       | directe                              | Apache-2.0                  | Permissive                |
| org.springframework.boot:spring-boot-starter-validation     | 4.1.1       | directe                              | Apache-2.0                  | Permissive                |
| org.springframework.boot:spring-boot-starter-logging        | 4.1.1       | transitive (2)                       | Apache-2.0                  | Permissive                |
| **jakarta.annotation:jakarta.annotation-api**               | 3.0.0       | transitive (2)                       | **EPL-2.0 / GPL2 w/ CPE**   | ⚠️ **Copyleft (signalé)** |
| org.springframework.boot:spring-boot-autoconfigure          | 4.1.1       | transitive (2)                       | Apache-2.0                  | Permissive                |
| org.yaml:snakeyaml                                          | 2.6         | transitive (2)                       | Apache-2.0                  | Permissive                |
| **ch.qos.logback:logback-classic**                          | 1.5.38      | transitive (3)                       | **EPL-2.0 / LGPL-2.1-only** | ⚠️ **Copyleft (signalé)** |
| org.apache.logging.log4j:log4j-to-slf4j                     | 2.25.5      | transitive (3)                       | Apache-2.0                  | Permissive                |
| org.slf4j:jul-to-slf4j                                      | 2.0.18      | transitive (3)                       | MIT                         | Permissive                |
| org.springframework.boot:spring-boot                        | 4.1.1       | transitive (3)                       | Apache-2.0                  | Permissive                |
| **ch.qos.logback:logback-core**                             | 1.5.38      | transitive (4, sous logback-classic) | **EPL-2.0 / LGPL-2.1-only** | ⚠️ **Copyleft (signalé)** |
| org.apache.logging.log4j:log4j-api                          | 2.25.5      | transitive (4)                       | Apache-2.0                  | Permissive                |
| org.springframework:spring-context                          | 7.0.9       | transitive (4)                       | Apache-2.0                  | Permissive                |
| org.springframework.boot:spring-boot-starter-jackson        | 4.1.1       | transitive (2)                       | Apache-2.0                  | Permissive                |
| org.springframework.boot:spring-boot-starter-tomcat         | 4.1.1       | transitive (2)                       | Apache-2.0                  | Permissive                |
| org.springframework.boot:spring-boot-http-converter         | 4.1.1       | transitive (2)                       | Apache-2.0                  | Permissive                |
| org.springframework.boot:spring-boot-webmvc                 | 4.1.1       | transitive (2)                       | Apache-2.0                  | Permissive                |
| org.springframework.boot:spring-boot-jackson                | 4.1.1       | transitive (3)                       | Apache-2.0                  | Permissive                |
| org.springframework.boot:spring-boot-starter-tomcat-runtime | 4.1.1       | transitive (3)                       | Apache-2.0                  | Permissive                |
| org.springframework.boot:spring-boot-tomcat                 | 4.1.1       | transitive (3)                       | Apache-2.0                  | Permissive                |
| org.springframework.boot:spring-boot-servlet                | 4.1.1       | transitive (3)                       | Apache-2.0                  | Permissive                |
| org.springframework:spring-webmvc                           | 7.0.9       | transitive (3)                       | Apache-2.0                  | Permissive                |
| tools.jackson.core:jackson-databind                         | 3.1.5       | transitive (4)                       | Apache-2.0                  | Permissive                |
| org.apache.tomcat.embed:tomcat-embed-core                   | 11.0.24     | transitive (4)                       | Apache-2.0                  | Permissive                |
| org.apache.tomcat.embed:tomcat-embed-websocket              | 11.0.24     | transitive (4)                       | Apache-2.0                  | Permissive                |
| org.springframework:spring-aop                              | 7.0.9       | transitive (4)                       | Apache-2.0                  | Permissive                |
| org.springframework:spring-expression                       | 7.0.9       | transitive (4)                       | Apache-2.0                  | Permissive                |
| com.fasterxml.jackson.core:jackson-annotations              | 2.21        | transitive (5)                       | Apache-2.0                  | Permissive                |
| tools.jackson.core:jackson-core                             | 3.1.5       | transitive (5)                       | Apache-2.0                  | Permissive                |
| org.springframework.boot:spring-boot-validation             | 4.1.1       | transitive (2)                       | Apache-2.0                  | Permissive                |
| org.apache.tomcat.embed:tomcat-embed-el                     | 11.0.24     | transitive (3)                       | Apache-2.0                  | Permissive                |
| org.hibernate.validator:hibernate-validator                 | 9.1.3.Final | transitive (3)                       | Apache-2.0                  | Permissive                |
| jakarta.validation:jakarta.validation-api                   | 3.1.1       | transitive (4)                       | Apache-2.0                  | Permissive                |
| org.jboss.logging:jboss-logging                             | 3.6.3.Final | transitive (4)                       | Apache-2.0                  | Permissive                |
| com.fasterxml:classmate                                     | 1.7.3       | transitive (4)                       | Apache-2.0                  | Permissive                |
| org.springframework:spring-core                             | 7.0.9       | transitive                           | Apache-2.0                  | Permissive                |
| commons-logging:commons-logging                             | 1.3.6       | transitive                           | Apache-2.0                  | Permissive                |
| org.jspecify:jspecify                                       | 1.0.1       | transitive                           | Apache-2.0                  | Permissive                |
| org.springframework:spring-web                              | 7.0.9       | transitive                           | Apache-2.0                  | Permissive                |
| org.springframework:spring-beans                            | 7.0.9       | transitive                           | Apache-2.0                  | Permissive                |
| io.micrometer:micrometer-observation                        | 1.17.1      | transitive                           | Apache-2.0                  | Permissive                |
| io.micrometer:micrometer-commons                            | 1.17.1      | transitive                           | Apache-2.0                  | Permissive                |
| org.slf4j:slf4j-api                                         | 2.0.18      | transitive                           | MIT                         | Permissive                |
| org.springframework.boot:spring-boot-web-server             | 4.1.1       | transitive                           | Apache-2.0                  | Permissive                |

**Bilan** : 46 dépendances résolues (directes + transitives) - 43 permissives (MIT/Apache-2.0),
**3 signalées copyleft** (dual-licence incluant LGPL/GPL), **0 propriétaire**, **0 non identifiée**
par l'outil.

## 3. Fiches de décision - cas copyleft détectés

Deux cas réels sont ressortis du scan (aucun n'est un cas fictif de TD : ce sont les vraies
dépendances transitives du projet).

### Fiche 1 - `ch.qos.logback:logback-classic` et `ch.qos.logback:logback-core`

- **Package et position** : `ch.qos.logback:logback-classic:1.5.38`, transitive, profondeur 3
  (`spring-boot-starter` → `spring-boot-starter-logging` → `logback-classic`) ; entraîne
  `ch.qos.logback:logback-core:1.5.38` à la profondeur 4 (sous `logback-classic`).
- **Licence signalée par l'outil** : `EPL-2.0` **ET** `LGPL-2.1-only` (deux entrées `<license>`
  dans le scan).
- **Analyse** : Logback est officiellement **double-licencié** par l'éditeur (QOS.ch) sous
  _Eclipse Public License v1.0/2.0_ **ou** _GNU Lesser General Public License v2.1_ - c'est un
  choix laissé au redistributeur, pas un cumul d'obligations. `license-maven-plugin` liste les deux
  branches sans indiquer qu'il s'agit d'un OU, d'où le signalement automatique à vérifier
  manuellement (c'est exactement l'objectif de cet audit).
- **Stratégie retenue** : **négocier/exercer l'alternative de licence déjà accordée** - on élit
  formellement la branche **EPL-2.0** (permissive/copyleft faible au niveau fichier, sans
  obligation de copyleft sur le code applicatif qui ne fait qu'utiliser la bibliothèque via API).
  On ne modifie ni ne redistribue le code source de Logback, donc aucune clause EPL ou LGPL n'est
  déclenchée côté Weather API dans les deux cas - mais documenter le choix évite toute ambiguïté
  en cas d'audit externe.
- **Justification vis-à-vis de l'architecture** : Logback n'est utilisé qu'à travers la façade
  `SLF4J` (`org.slf4j.Logger`) déjà en place dans le code - c'est déjà une interface
  d'isolation : aucun appel direct aux classes `ch.qos.logback.*` n'existe dans `src/`. Le
  remplacement de l'implémentation de logging (ex. vers `log4j2`, également Apache-2.0) resterait
  trivial si la politique de licences venait à changer, sans toucher au code métier.
- **Statut CI** : whitelisté explicitement (voir §4) avec référence à cette fiche.

### Fiche 2 - `jakarta.annotation:jakarta.annotation-api`

- **Package et position** : `jakarta.annotation:jakarta.annotation-api:3.0.0`, transitive,
  profondeur 2 (`spring-boot-starter` → `jakarta.annotation-api`).
- **Licence signalée par l'outil** : `EPL 2.0` **ET** `GPL2 w/ CPE`.
- **Analyse** : les API Jakarta EE historiquement issues de GlassFish/Java EE sont double-licenciées
  sous _Eclipse Public License 2.0_ **ou** _GPL v2 avec Classpath Exception (CPE)_. La Classpath
  Exception a précisément été conçue par Sun/Oracle pour permettre l'utilisation de bibliothèques
  d'API GPL (le JDK lui-même en fait usage) **sans propager le copyleft** au code qui les appelle,
  tant qu'il ne s'agit pas d'un dérivé du code de la bibliothèque elle-même. C'est un cas classique
  de faux positif "GPL" pour un outil de scan naïf.
- **Stratégie retenue** : **négocier/exercer l'alternative de licence** - branche **EPL-2.0**
  élue explicitement (et de toute façon la branche GPL2+CPE serait également sans risque grâce à
  la Classpath Exception).
- **Justification vis-à-vis de l'architecture** : `jakarta.annotation-api` ne fournit que des
  annotations (`@PostConstruct`, `@Resource`…) consommées par le conteneur Spring ; le code du
  projet n'étend ni ne modifie aucune classe de cette API. Aucun boundary supplémentaire n'est
  nécessaire.
- **Statut CI** : whitelisté explicitement (voir §4) avec référence à cette fiche.

## 4. Intégration CI - gate sur liste blanche

### Liste blanche du projet

Licences acceptées sans revue au cas par cas : **Apache-2.0**, **MIT**, **BSD**, **EPL** (branche
permissive/copyleft-faible retenue pour les dual-licences ci-dessus).

### Script de vérification

[`scripts/check_license_whitelist.py`](scripts/check_license_whitelist.py) relit
`target/generated-resources/licenses.xml` généré par `mvn license:download-licenses` et échoue
(`exit 1`) dès qu'une licence listée pour une dépendance :

1. ne correspond à aucun terme de la liste blanche, **et**
2. n'a pas d'exception explicitement revue dans `REVIEWED_EXCEPTIONS` (qui pointe vers les fiches
   de décision ci-dessus).

Toute nouvelle dépendance copyleft non documentée fait donc échouer le build immédiatement - seules
les deux exceptions déjà auditées (`logback-*` → `LGPL-2.1-only`, `jakarta.annotation-api` →
`GPL2 w/ CPE`) sont explicitement tolérées, avec justification en dur dans le script.

### Job CI ajouté

Dans [`.github/workflows/ci.yml`](.github/workflows/ci.yml), nouveau job `license-audit` en
parallèle du job `test` :

```yaml
license-audit:
  runs-on: ubuntu-latest
  steps:
    - name: Checkout
      uses: actions/checkout@v4
    - name: Set up JDK 21
      uses: actions/setup-java@v4
      with:
        java-version: "21"
        distribution: "temurin"
        cache: maven
    - name: Scan dependency licenses
      run: mvn -B license:download-licenses
    - name: Set up Python
      uses: actions/setup-python@v5
      with:
        python-version: "3.12"
    - name: Enforce license whitelist
      run: python scripts/check_license_whitelist.py
```

## 5. Vérification du critère de réussite

Conformément à la consigne, un package copyleft de test a été ajouté puis retiré pour valider que
le gate réagit dans les deux sens.

1. **Ajout temporaire** de `com.itextpdf:itextpdf:5.5.13.3` (licence **AGPL-3.0**, mono-licence,
   sans exception de type Classpath) dans `pom.xml`.
2. `mvn license:download-licenses` → le scan rapporte bien
   `GNU Affero General Public License v3` pour cet artefact.
3. `python scripts/check_license_whitelist.py` → **échec** :

   ```
   License whitelist check FAILED:

     - com.itextpdf:itextpdf: 'GNU Affero General Public License v3' (not in whitelist and no reviewed exception)

   1 violation(s). Add a reviewed exception in scripts/check_license_whitelist.py
   (with a matching decision sheet in licenses.md) or remove/replace the dependency.
   ```

4. **Retrait** de la dépendance de test, nouveau scan, nouvelle vérification → **succès** :

   ```
   License whitelist check PASSED: no unreviewed non-whitelisted licenses found.
   ```

Le `pom.xml` final ne contient donc que l'ajout du plugin `license-maven-plugin` ; aucune
dépendance applicative n'a été modifiée par cet audit.

## 6. Livrables

- Fichier de scan brut : [`licenses/licenses.xml`](licenses/licenses.xml)
- Tableau de classification : §2 ci-dessus
- Fiches de décision : §3 ci-dessus
- Configuration CI ajoutée : job `license-audit` dans
  [`.github/workflows/ci.yml`](.github/workflows/ci.yml) +
  [`scripts/check_license_whitelist.py`](scripts/check_license_whitelist.py)
