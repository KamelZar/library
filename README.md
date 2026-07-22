# libraryApp

Catalogue perso de ma collection de Blu-ray : titre, code-barre, vu/non-vu, type (Film/Documentaire/Série), genre, année, acteurs (optionnel). Projet amical, budget 0€, hébergé sur mon NAS Synology.

- **Backend** : Spring Boot 3 (Java 21), API REST sous `/api/movies`.
- **Base de données** : H2 fichier via Spring Data JPA/Hibernate (facile à remplacer par SQLite/PostgreSQL plus tard, seule la config `application.yml` change).
- **Frontend** : HTML/CSS/JS statique servi par Spring Boot, pas de build step.
- **Sécurité** : un compte admin unique (login formulaire), blocage d'une IP pendant 15 min après 3 échecs de connexion (compteur en mémoire, remis à zéro au redémarrage du conteneur).

## Lancer en local

Prérequis : JDK 21, Maven.

```bash
ADMIN_USERNAME=admin ADMIN_PASSWORD=motdepasse mvn spring-boot:run
```

Puis ouvrir http://localhost:8080/login.html.

Sans variables d'environnement définies, les identifiants par défaut sont `admin` / `changeme` (**à changer avant tout déploiement**, voir `application.yml`).

## Lancer les tests

```bash
mvn test
```

## Build et lancement en Docker (local)

```bash
cp .env.example .env   # puis éditer .env avec un vrai mot de passe
docker compose build
docker compose up
```

L'app est accessible sur http://localhost:8081. Les données H2 sont persistées dans le volume Docker nommé `libraryapp-data`.

## Déploiement sur un NAS Synology

1. **Récupérer le projet sur le NAS** : soit en clonant ce dépôt via Git Server / File Station, soit en copiant le dossier via un partage réseau.
2. **Container Manager** (anciennement Docker) : Panneau de configuration > Paquets > installer *Container Manager* si ce n'est pas déjà fait.
3. Dans Container Manager > **Projet** > Créer, choisir le dossier du projet (celui contenant `docker-compose.yml`), et fournir les variables `ADMIN_USERNAME` / `ADMIN_PASSWORD` (soit via un fichier `.env` déposé à côté du `docker-compose.yml`, soit dans l'écran de config du projet).
4. Lancer le build puis démarrer le projet. Le conteneur écoute sur le port `8081` de l'hôte (modifiable dans `docker-compose.yml`).
5. **Exposer via le domaine** : Panneau de configuration > **Portail de connexion** > onglet **Avancé** > **Proxy inversé** :
   - Source : `https://bibliotheque.mondomaine.tld` (port 443)
   - Destination : `http://localhost:8081`
6. **Certificat HTTPS gratuit** : Panneau de configuration > **Sécurité** > **Certificat** > Ajouter > *Let's Encrypt*, associer le certificat au domaine choisi. Synology renouvelle automatiquement le certificat.
7. Vérifier que le routeur/box redirige les ports 80/443 vers le NAS si le domaine pointe directement dessus.

## Limites connues (acceptées pour un projet perso)

- Le blocage d'IP après échecs de connexion est en mémoire : il est remis à zéro à chaque redémarrage du conteneur.
- Un seul compte admin, pas de gestion multi-utilisateur pour l'instant.

## Pistes d'évolution

- Note d'appréciation, lien vers une fiche externe (allocine, IMDb...) sur chaque film.
- Authentification via un compte Google (remplacerait le login maison).
