package local.dev.catalog.controller;

import local.dev.catalog.entity.Book;
import local.dev.catalog.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers // <--- Docker an
public class BookRestControllerWebEnvTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookRepository bookRepository; // Wir brauchen das Repo, um Daten reinzulegen

    // Container Starten
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @BeforeEach
    void setUp() {
        // Wir müssen sicherstellen, dass Daten da sind, weil der Container LEER startet!
        bookRepository.deleteAll();
        bookRepository.save(new Book("123-456", "Clean Code", "Tech Book", "Robert C. Martin"));
        bookRepository.save(new Book("789-101", "The Hobbit", "Fantasy", "J.R.R. Tolkien"));
    }

    @Test
    void shouldReturnBooksLoadedFromDataSql() {
        // Hinweis: Der Name passt nicht mehr ganz zu init_dev_data.sql, aber die Logik stimmt.
        String url = "http://localhost:" + port + "/api/books";

        ResponseEntity<Book[]> response = this.restTemplate.getForEntity(url, Book[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(1);

        // Wir prüfen auf das Buch, das wir im setUp() erstellt haben
        assertThat(response.getBody()[0].getTitle()).isEqualTo("Clean Code");
    }

    @Test
    void shouldReturnFilteredBooksWhenSearchingWithMultipleKeywords() {
        // Clean Code passt zu "clean" und "martin"
        String url = "http://localhost:" + port + "/api/books/search?query=clean&query=martin";

        ResponseEntity<Book[]> response = this.restTemplate.getForEntity(url, Book[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(1); // Sollte genau 1 sein (Clean Code)

        for (Book book : response.getBody()) {
            assertThat(book.getAuthor()).containsIgnoringCase("Martin");
            assertThat(book.getTitle()).containsIgnoringCase("clean");
        }
    }

    @Test
    void searchShouldHandleSingleAndMultipleKeywordsCorrect() {
        // Einzeln
        String urlSingle = "http://localhost:" + port + "/api/books/search?query=Clean";
        ResponseEntity<Book[]> responseSingle = restTemplate.getForEntity(urlSingle, Book[].class);
        assertThat(responseSingle.getBody().length).isGreaterThan(0);

        // Mehrfach
        String urlMulti = "http://localhost:" + port + "/api/books/search?query=Clean&query=Martin";
        ResponseEntity<Book[]> responseMulti = restTemplate.getForEntity(urlMulti, Book[].class);
        assertThat(responseMulti.getBody().length).isGreaterThan(0);
    }

    @Test
    void getBookByIsbn_ShouldReturnNotFoundForUnknownIsbn() {
        String url = "http://localhost:" + port + "/api/books/999-999-999";
        ResponseEntity<Book> response = restTemplate.getForEntity(url, Book.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}