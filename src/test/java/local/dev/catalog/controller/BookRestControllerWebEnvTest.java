package local.dev.catalog.controller;

import local.dev.catalog.entity.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookRestControllerWebEnvTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnBooksLoadedFromDataSql() {

        String url = "http://localhost:" + port + "/api/books";

        // Die Anfrage an den laufenden Server senden und das Ergebnis als Array von Book Entities erwarten
        // Das resultierende Array muss mindestens die Bücher aus der data.sql enthalten.
        ResponseEntity<Book[]> response = this.restTemplate.getForEntity(url, Book[].class);

        // 1. Erwartung: HTTP Status Code 200 OK
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 2. Erwartung: Die zurückgegebene Liste ist nicht leer
        assertThat(response.getBody()).isNotNull();

        // 3. Erwartung: Überprüfen, ob das Array mindestens die erwartete Anzahl an Büchern enthält
        // (passen Sie die Größe an Ihre data.sql an)
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(1);

        // Optional: Detaillierte Prüfung der Daten (hier Datensatz 1 aus data.sql)
        assertThat(response.getBody()[0].getTitle()).isEqualTo("Clean Code");
    }
    @Test
    void shouldReturnFilteredBooksWhenSearchingWithMultipleKeywords() {
        // Hier testen wir die neue Logik: Mehrere Keywords über die URL
        String url = "http://localhost:" + port + "/api/books/search?query=clean&query=martin";

        ResponseEntity<Book[]> response = this.restTemplate.getForEntity(url, Book[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Überprüfe, ob die Filterung funktioniert (z.B. nur Bücher von Martin, die "clean" im Titel haben)
        for (Book book : response.getBody()) {
            assertThat(book.getAuthor()).containsIgnoringCase("Martin");
            assertThat(book.getTitle()).containsIgnoringCase("clean");
        }
    }


    @Test
    void searchShouldHandleSingleAndMultipleKeywordsCorrect() {
        // Pfad 1: Einzelnes Keyword (nutzt repository.searchBooks)
        String urlSingle = "http://localhost:" + port + "/api/books/search?query=Clean";
        ResponseEntity<Book[]> responseSingle = restTemplate.getForEntity(urlSingle, Book[].class);
        assertThat(responseSingle.getBody().length).isGreaterThan(0);

        // Pfad 2: Mehrere Keywords (nutzt repository.searchBooksByKeywords)
        String urlMulti = "http://localhost:" + port + "/api/books/search?query=Clean&query=Martin";
        ResponseEntity<Book[]> responseMulti = restTemplate.getForEntity(urlMulti, Book[].class);
        assertThat(responseMulti.getBody().length).isGreaterThan(0);
    }


    @Test
    void getBookByIsbn_ShouldReturnNotFoundForUnknownIsbn() {
        String url = "http://localhost:" + port + "/api/books/999-999-999"; // ISBN die es nicht gibt
        ResponseEntity<Book> response = restTemplate.getForEntity(url, Book.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
