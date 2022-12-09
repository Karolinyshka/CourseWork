package coursework.dto;

import java.io.Serializable;

public class BookDto implements Serializable {
    private String name;
    private String author;
    private String publisher;
    private String edition;
    private String quantity;
    private String remainingBooks;
    private String availability;
    private String section;
    private String bookId;

    public BookDto() {
    }

    public BookDto(String name, String author, String publisher, String edition, String quantity, String remainingBooks, String availability, String section, String bookId) {
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.edition = edition;
        this.quantity = quantity;
        this.remainingBooks = remainingBooks;
        this.availability = availability;
        this.section = section;
        this.bookId = bookId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getRemainingBooks() {
        return remainingBooks;
    }

    public void setRemainingBooks(String remainingBooks) {
        this.remainingBooks = remainingBooks;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    @Override
    public String toString() {
        return "BookDto{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", edition='" + edition + '\'' +
                ", quantity='" + quantity + '\'' +
                ", remainingBooks='" + remainingBooks + '\'' +
                ", availability='" + availability + '\'' +
                ", section='" + section + '\'' +
                ", bookId='" + bookId + '\'' +
                '}';
    }
}
