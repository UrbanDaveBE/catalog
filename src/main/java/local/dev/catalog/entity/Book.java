package local.dev.catalog.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Book {
    @Id
    private String isbn;
    private String title;
    private String author;
    private String description;


    public Book() {
      }
    public Book(String isbn, String title, String description, String author) {
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getISBN() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setISBN(String isbn) {
        this.isbn = isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
