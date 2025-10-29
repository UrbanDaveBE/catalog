# catalog


* PS C:\workspace.idea\catalog> git checkout main
* PS C:\workspace.idea\catalog> git pull
* PS C:\workspace.idea\catalog> git branch -d feature/book-repository
* PS C:\workspace.idea\catalog> git checkout -b feature/book-controller
* PS C:\workspace.idea\catalog> git add .
* PS C:\workspace.idea\catalog> git commit -m "BookRestController mit Suchmethode implementiert AB#66"
* PS C:\workspace.idea\catalog> git push origin feature/book-controller
* PS C:\workspace.idea\catalog> git checkout main
* PS C:\workspace.idea\catalog> git branch -d feature/book-repositor
* PS C:\workspace.idea\catalog> git branch -d feature/book-controller
* PS C:\workspace.idea\catalog> git checkout -b feature/testdaten
* PS C:\workspace.idea\catalog> git add .
* PS C:\workspace.idea\catalog> git commit -m "Testdaten und appl.prop konfiguriert AB#67"
* PS C:\workspace.idea\catalog> git push origin feature/testdaten


## Login H2

* Browser URL: http://localhost:8080/h2-console
* JDBC URL: jdbc:h2:mem:catalogdb

## Rest-Test
* http://localhost:8080/api/books/search?query=clean