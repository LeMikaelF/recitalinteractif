*Travis build status*
[![Build Status](https://travis-ci.com/LeMikaelF/recitalinteractif.svg?branch=master)](https://travis-ci.com/LeMikaelF/recitalinteractif)

### Technologies utilisées
* Logique: Java
* Dependency Injection: Google Guice
* Serveur: Jetty (embedded)
* Interface graphique: JavaFX + WebEngine + vis.js
* Build/Dependency management: Maven
* Interface web: Javascript + Bootstrap
* Communication réseau: Websocket
* Persistence/Serialization JSON: jackson

# Récital commenté interactif : logiciel controleur


Ce logiciel, utilisé dans le cadre d'un doctorat en musicologie (concentration recherche-création) à l'Université Laval, contrôle le déroulement d'un récital commenté interactif. Il agit comme un logiciel de présentation (ex. Microsoft Powerpoint ou LibreOffice Impress), mais il permet des interactions en direct avec les auditeurs, au moyen d'une interface web.

Le logiciel génère deux fenêtres sur l'ordinateur du présentateur (il est recommandé d'utiliser un ordinateur portable). Le tableau de bord contient le texte de la présentation (pour les modules textuels) et est destiné à l'artiste-présentateur, tandis que l'écran de présentation doit être projeté sur un grand écran, est destiné au public. Finalement, les membres du public peuvent accéder à une interface web qui permet d'interagir en temps réel avec le logiciel contrôleur.

## Utilitaire d'écriture
Un utilitaire d'écriture de modules et d'édition de graphes est disponible en lançant le programme avec le flag `--builder`. Les modules (nommés « textons » dans le logiciel) doivent être placés dans un dossier avec le fichier du graphe (nommé `graph.json`). Ce dossier est indiqué dans le fichier `config.properties`, et correspond à la propriété `location`.

## 1 - Tableau de bord
![Capture d'écran du tableau de bord](img/Tableau%20de%20bord.png)

## 2 - Écran de présentation
![Capture d'écran de l'écran de présentation](img/Écran%20de%20présentation.png)

## 3 - Interface de vote
![Capture d'écran de l'interface de vote](img/Interface%20de%20vote.png)
