package local.dev.catalog.repository;

import local.dev.catalog.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

public interface BookRepository extends JpaRepository<Book,String> {

    @Query("SELECT DISTINCT b FROM Book b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> searchBooks(@Param("keyword") String keyword);

    default List<Book> searchBooksByKeywords(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return List.of();
        }

        // -> alle bücher als startmenge
        List<Book> result = findAll();

        // für jedes keyword filtern (&&)
        for(String key : keywords) {
            String loweredKeyword = key.toLowerCase();
            result = result.stream()
                    .filter(book ->
                            book.getTitle().toLowerCase().contains(loweredKeyword) ||
                                    book.getAuthor().toLowerCase().contains(loweredKeyword) ||
                                    book.getDescription().toLowerCase().contains(loweredKeyword)
                    ).toList();
        }
        return result;
    }

}
