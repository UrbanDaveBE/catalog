# Catalog Microservice

Bounded Context für die Verwaltung des Buchkatalogs im BookStore-System.

## Features

- Bücher verwalten (ISBN, Titel, Beschreibung, Autor)
- Buchsuche über REST-API mit Multi-Keyword Support
- Case-insensitive Suche in Titel, Beschreibung und Autor
- UND-Verknüpfung bei mehreren Keywords

## Tech Stack

- Java 21
- Spring Boot 3.5.6
- Spring Data JPA
- H2 In-Memory Database
- Maven

---

## Entwicklungs-Workflow (Git & Azure DevOps)

### 1. Setup (einmalig)

```bash
# Repository klonen
git clone https://github.com/<username>/catalog.git
cd catalog

# Projekt in IntelliJ öffnen (als Maven-Projekt)
# File → Open → pom.xml auswählen
```

### 2. Workflow pro Task

Jeder Task bekommt einen eigenen Feature-Branch:

```bash
# 1. Aktuellen Branch prüfen
git branch

# 2. Zu main wechseln und neueste Änderungen holen
git checkout main
git pull

# 3. Neuen Feature-Branch erstellen
git checkout -b feature/<task-name>
# Beispiele:
# - feature/book-entity
# - feature/book-repository
# - bugfix/searchlogic

# 4. Code schreiben...

# 5. Status prüfen (welche Dateien geändert wurden)
git status

# 6. Änderungen stagen und committen
git add .
# oder spezifische Datei: git add src/main/java/...
git commit -m "Beschreibung der Änderung AB#<TaskNummer>"
# Beispiel: git commit -m "Book Entity erstellt AB#64"

# 7. Branch zu GitHub pushen
git push origin feature/<task-name>

# 8. Pull Request auf GitHub erstellen
# - Gehe zu GitHub Repository
# - Klick "Compare & pull request"
# - Title/Description ausfüllen
# - "Create pull request" → "Merge pull request"

# 9. Zurück zu main und aufräumen
git checkout main
git pull
git branch -d feature/<task-name>  # Lokalen Branch löschen
```

### 3. Branch-Management Best Practices

**Branch-Namenskonventionen:**
- `feature/<name>` - Neue Features/Tasks
- `bugfix/<name>` - Bug-Fixes
- `hotfix/<name>` - Dringende Produktions-Fixes

**Commit-Messages:**
- Immer Azure DevOps Task-Nummer referenzieren: `AB#<nummer>`
- Kurz und beschreibend
- Beispiele:
  - ✅ `"Book Entity erstellt AB#64"`
  - ✅ `"Multi-Keyword Suche implementiert AB#71"`
  - ❌ `"Update"` (zu unspezifisch)

**Wichtige Regeln:**
- ✅ **NIE direkt auf `main` committen!** Immer über Feature-Branch + Pull Request
- ✅ Vor neuem Branch: `git pull` auf main
- ✅ Nach Merge: Lokalen Feature-Branch löschen
- ✅ Kleine, atomare Commits (ein Task = ein Branch)

### 4. Typische Probleme & Lösungen

**Problem: "Your branch is behind origin/main"**
```bash
git checkout main
git pull
# Dann neuen Feature-Branch erstellen
```

**Problem: Änderungen verwerfen**
```bash
git checkout .           # Alle Änderungen verwerfen
git clean -fd            # Untracked files löschen
```

**Problem: Branch existiert schon remote**
```bash
# Remote Branch löschen
git push origin --delete feature/<branch-name>
```

**Problem: Falscher Branch aktiv**
```bash
git stash                # Änderungen temporär speichern
git checkout main
git checkout -b feature/correct-name
git stash pop            # Änderungen wiederherstellen
```

---

## Lokale Installation & Entwicklung

### Anwendung starten

```bash
# Mit Maven Wrapper (empfohlen)
./mvnw spring-boot:run

# Oder in IntelliJ
# Rechtsklick auf CatalogApplication.java → Run
```

Die Anwendung läuft auf: `http://localhost:8080`

### H2 Console (Datenbank inspizieren)

URL: `http://localhost:8080/h2-console`

**Login-Daten:**
- JDBC URL: `jdbc:h2:mem:catalogdb`
- Username: `sa`
- Password: _(leer lassen)_

### API Testen

**Option 1: HTTP Client (IntelliJ)**
- Öffne `requests.http`
- Klick auf grünen ▶️ Play-Button neben den Requests

**Option 2: Browser**
```
http://localhost:8080/api/books
http://localhost:8080/api/books/search?query=clean
```

**Option 3: cURL**
```bash
curl http://localhost:8080/api/books
curl "http://localhost:8080/api/books/search?query=clean&query=martin"
```

---

## API Endpoints

### Alle Bücher abrufen
```http
GET http://localhost:8080/api/books
```

**Response:** JSON Array mit allen Büchern

---

### Bücher suchen
```http
GET http://localhost:8080/api/books/search?query=<keyword>
```

**Parameter:**
- `query` (String, required): Suchbegriff(e)
- Mehrere Keywords möglich: `?query=clean&query=martin` (UND-Verknüpfung)

**Beispiele:**
```http
# Ein Keyword
GET http://localhost:8080/api/books/search?query=clean

# Mehrere Keywords (UND-Verknüpfung)
GET http://localhost:8080/api/books/search?query=clean&query=martin

# Case-insensitive
GET http://localhost:8080/api/books/search?query=CLEAN
```

**Response:**
```json
[
  {
    "isbn": "978-0-13-468599-1",
    "title": "Clean Code",
    "description": "A Handbook of Agile Software Craftsmanship...",
    "author": "Robert C. Martin"
  }
]
```

---

### Buch nach ISBN abrufen
```http
GET http://localhost:8080/api/books/{isbn}
```

**Beispiel:**
```http
GET http://localhost:8080/api/books/978-0-13-468599-1
```

---

## Tests ausführen

```bash
# Alle Tests
./mvnw test

# Nur Unit Tests
./mvnw test -Dtest=*Test

# Nur Integration Tests
./mvnw test -Dtest=*IT
```

---

## Projekt-Struktur

```
catalog/
├── src/
│   ├── main/
│   │   ├── java/local/dev/catalog/
│   │   │   ├── entity/           # Domain Models (Book)
│   │   │   ├── repository/       # Data Access Layer
│   │   │   ├── controller/       # REST Endpoints
│   │   │   └── CatalogApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── data.sql          # Testdaten
│   └── test/
│       └── java/local/dev/catalog/
│           ├── repository/        # Repository Tests
│           └── controller/        # Controller Tests
├── requests.http                  # HTTP Client Requests
├── pom.xml                        # Maven Dependencies
└── README.md
```

---

## Domain Model

```
Book (Aggregate Root)
├── isbn (Primary Key)    - String
├── title                 - String
├── description           - String
└── author                - String
```

**Bounded Context Beziehungen:**
- **Catalog → Order**: Published Language (Book-Daten)
- **Catalog → Inventory**: Published Language (Verfügbarkeit)

---

## Konfiguration

Wichtige Properties in `application.properties`:

```properties
# H2 Console
spring.h2.console.enabled=true

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.defer-datasource-initialization=true

# Logging
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG
```

---

## Deployment (später)

```bash
# JAR bauen
./mvnw clean package

# JAR ausführen
java -jar target/catalog-0.0.1-SNAPSHOT.jar
```

---

## Sprint 1 - Completed Tasks ✅

- [x] #63 - Spring Boot Projekt initialisiert
- [x] #64 - Book Entity erstellt
- [x] #65 - BookRepository mit Suchmethode
- [x] #66 - BookRestController implementiert
- [x] #67 - Testdaten und Konfiguration
- [x] #71 - Multi-Keyword Search Bug-Fix
- [x] #70 - Dokumentation (README + Wiki)

---

## Lessons Learned

### Git Workflow
- **Branch pro Task** ermöglicht saubere Historie
- **Pull Requests** erzwingen Code Review (auch solo!)
- **AB#<nummer>** in Commits ermöglicht Tracking in Azure DevOps

### Spring Boot
- `spring.jpa.defer-datasource-initialization=true` wichtig für data.sql
- H2 Console super für schnelles Debugging
- `@RequestParam List<String>` für mehrere Query-Parameter

### Testing
- `.http` Dateien besser als Postman für versionierbares Testing
- Manuelle Tests vor jedem Commit essential

---

## Nächste Schritte (Sprint 2)

- [ ] Automatisierte Unit Tests (JUnit 5)
- [ ] Integration Tests (MockMvc)
- [ ] Service-Layer einführen (Business Logic)
- [ ] Pagination für `/api/books`
- [ ] Docker Container für Deployment

---

## Links

- **GitHub Repository**: https://github.com/<username>/catalog
- **Azure DevOps Board**: [Link zum Board]
- **Wiki - Teststrategie**: [Link zum Wiki]

---

## Kontakt

Bei Fragen: [Dein Name] - [Email/Kontakt]