package local.dev.catalog.controller;

import local.dev.catalog.entity.Book;
import local.dev.catalog.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookRestController.class)
public class BookRestControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookRepository bookRepository;

    @Test
    void shouldReturnAllBooksWhenCallingGetAllBooks() throws Exception {
        // Mock-Objekt erstellen
        Book testBook = new Book("abc123", "Clean Code v2", "abc", "Robert C. Martin");

        // Mock-Verhalten definieren
        given(bookRepository.findAll()).willReturn(List.of(testBook));

        // Simulierte Anfrage ausführen: GET /api/books
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        [{"isbn": "abc123", "title": "Clean Code v2", "author": "Robert C. Martin"}]
                        """));
    }

    @Test
    void shouldReturnBadRequestWhenQueryIsBlank() throws Exception {
        // Testet die Validierung: if (queries.stream().allMatch(String::isBlank))
        // Hier schicken wir nur Leerzeichen als Query
        mockMvc.perform(get("/api/books/search").param("query", "   "))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldSearchWithSingleKeyword() throws Exception {
        // Testet den Pfad: if (cleanedQueries.size() == 1)
        String keyword = "Clean";
        Book testBook = new Book("111", "Clean Code", "Tech", "Uncle Bob");

        // Wir erwarten, dass das Repo mit genau diesem String aufgerufen wird
        given(bookRepository.searchBooks(keyword)).willReturn(List.of(testBook));

        mockMvc.perform(get("/api/books/search").param("query", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Code"));

        // Verifizieren, dass wirklich die Einzel-Keyword-Methode gerufen wurde
        verify(bookRepository).searchBooks(keyword);
    }

    @Test
    void shouldSearchWithMultipleKeywords() throws Exception {
        // Testet den Pfad: else { books = bookRepository.searchBooksByKeywords(...) }
        Book testBook = new Book("222", "Java & Spring", "Tech", "Josh Long");

        // Wenn irgendeine Liste kommt, geben wir das Buch zurück
        given(bookRepository.searchBooksByKeywords(anyList())).willReturn(List.of(testBook));

        mockMvc.perform(get("/api/books/search")
                        .param("query", "Java")
                        .param("query", "Spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isbn").value("222"));

        // Verifizieren, dass die Listen-Methode gerufen wurde
        verify(bookRepository).searchBooksByKeywords(anyList());
    }

    @Test
    void shouldReturnNotFoundWhenIsbnDoesNotExist() throws Exception {
        // Testet den Pfad: .orElse(ResponseEntity.notFound().build())
        String isbn = "999-999";
        given(bookRepository.findById(isbn)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/books/{isbn}", isbn))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBookWhenIsbnExists() throws Exception {
        // Testet den Erfolgsfall bei ISBN Suche
        String isbn = "123-456";
        Book testBook = new Book(isbn, "Found Me", "Desc", "Author");
        given(bookRepository.findById(isbn)).willReturn(Optional.of(testBook));

        mockMvc.perform(get("/api/books/{isbn}", isbn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Found Me"));
    }
}