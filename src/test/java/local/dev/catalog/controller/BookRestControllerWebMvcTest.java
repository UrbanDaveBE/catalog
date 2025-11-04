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

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookRestController.class)
public class BookRestControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookRepository bookRepository;

    @Test
    void shouldReturnAllBooksWhenCallingGetAllBooks() throws Exception {
        // Mock-Objekt erstellen -> da data.sql nicht geladen wird, braucht es manuell erstellte objekte
        // -> braucht neuen konstruktor für einfachheit
        Book testBook = new Book("abc123", "Clean Code v2", "abc","Robert C. Martin"); // Angenommene Konstruktoren


        // Mock-Verhalten definieren: findAll() soll das Mock-Buch zurückgeben
        // In Spring-Tests wird given(...).willReturn(...) (aus BDDMockito) oft gegenüber when(...).thenReturn(...) (aus Mockito) bevorzugt, da es besser dem BDD-Muster (Given-When-Then) folgt und die Teststruktur klarer macht. Funktionalität ist jedoch identisch.
        given(bookRepository.findAll()).willReturn(List.of(testBook));

        // Simulierte Anfrage ausführen: GET /api/books
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        [{"isbn": "abc123", "title": "Clean Code v2", "author": "Robert C. Martin"}]
                        """));
    }

}
