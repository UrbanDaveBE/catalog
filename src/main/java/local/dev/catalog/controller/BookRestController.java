package local.dev.catalog.controller;

import local.dev.catalog.entity.Book;
import local.dev.catalog.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookRestController {

    @Autowired
    private BookRepository bookRepository;

    /**
     * Suche nach Büchern anhand eines Suchbegriffs.
     * GET /api/books/search?query=clean+code
     *
     * @param queries Suchbegriff
     * @return Liste der gefundenen Bücher als JSON
     */
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam("query") List<String> queries) {
        //validieren
        if (queries == null || queries.isEmpty() || queries.stream().allMatch(String::isBlank)) {
            return ResponseEntity.badRequest().build();
        }

        List<String> cleanedQueries= queries.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        if(cleanedQueries.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Book> books;
        if (cleanedQueries.size() == 1) {
            books =  bookRepository.searchBooks(cleanedQueries.get(0));
        } else {
            books = bookRepository.searchBooksByKeywords(cleanedQueries);
        }

        return ResponseEntity.ok(books);
    }

    /**
     * Alle Bücher abrufen
     * GET /api/books
     */
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return ResponseEntity.ok(books);
    }

    /**
     * Ein Buch nach ISBN abrufen
     * GET /api/books/{isbn}
     */
    @GetMapping("/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        return bookRepository.findById(isbn)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
