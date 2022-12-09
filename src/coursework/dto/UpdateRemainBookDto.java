package coursework.dto;

import java.io.Serializable;

public class UpdateRemainBookDto implements Serializable {
    private String bookId;
    private int count;

    public UpdateRemainBookDto(String bookId, int count) {
        this.bookId = bookId;
        this.count = count;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "UpdateRemainBookDto{" +
                "bookId='" + bookId + '\'' +
                ", count=" + count +
                '}';
    }
}
