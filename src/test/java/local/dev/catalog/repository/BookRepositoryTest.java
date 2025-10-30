package local.dev.catalog.repository;

import local.dev.catalog.entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        // Testdaten erstellen -> aktuell nur über data.sql
        //bookRepository.save(new Book("111", "Clean Code", "A book about clean code", "Robert C. Martin"));
        //bookRepository.save(new Book("222", "Design Patterns", "A book about design patterns", "Gang of Four"));
        //bookRepository.save(new Book("333", "Agile Development", "A book about agile", "Martin Fowler"));
    }

    @Test
    void findAll_returnsAllBooks() {
        // Given - Testdaten in setUp()

        // When
        List<Book> result = bookRepository.findAll();

        // Then
        assertEquals(8, result.size());
    }

    @Test
    void searchBooks_findsByTitle() {
        // When
        List<Book> result = bookRepository.searchBooks("Clean");

        // Then
        assertEquals(2, result.size());

        // Prüfen, ob die erwarteten Titel in der Ergebnisliste vorhanden sind
        List<String> titles = result.stream()
                .map(Book::getTitle)
                .toList();
        assertTrue(titles.contains("Clean Code"));
        assertTrue(titles.contains("Clean Architecture"));
    }

    @Test
    void searchBooks_findsByAuthor() {
        // When
        List<Book> result = bookRepository.searchBooks("Martin");

        // Then
        assertEquals(4, result.size());
    }

    @Test
    void searchBooks_caseInsensitive() {
        // When
        List<Book> result = bookRepository.searchBooks("CLEAN");

        // Then
        assertEquals(2, result.size());
    }

    @Test
    void searchBooks_noResults() {
        // When
        List<Book> result = bookRepository.searchBooks("xyz123");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void searchBooks_partialMatch() {
        // When
        List<Book> result = bookRepository.searchBooks("agi");

        // Then
        assertEquals(2, result.size()); // Stellt sicher, dass es 2 Bücher sind

        // Prüfen, ob die erwarteten Titel vorhanden sind
        List<String> titles = result.stream()
                .map(Book::getTitle)
                .toList();
        assertTrue(titles.contains("Clean Code"));
        assertTrue(titles.contains("Agile Software Development"));
    }

    @Test
    void searchBooksByKeywords_multipleKeywords() {
        // When
        List<Book> result = bookRepository.searchBooksByKeywords(List.of("clean", "martin","agile"));

        // Then
        assertEquals(1, result.size());

        // Prüfen, ob die erwarteten Titel vorhanden sind
        List<String> titles = result.stream()
                .map(Book::getTitle)
                .toList();
        assertTrue(titles.contains("Clean Code"));
        //assertTrue(titles.contains("Clean Architecture"));
    }
}
