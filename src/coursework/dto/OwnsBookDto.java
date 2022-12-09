package coursework.dto;

import java.io.Serializable;

public class OwnsBookDto implements Serializable {
    private String query;
    private String bookSearchField;
    private String studentSearchTextField;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getBookSearchField() {
        return bookSearchField;
    }

    public void setBookSearchField(String bookSearchField) {
        this.bookSearchField = bookSearchField;
    }

    public String getStudentSearchTextField() {
        return studentSearchTextField;
    }

    public void setStudentSearchTextField(String studentSearchTextField) {
        this.studentSearchTextField = studentSearchTextField;
    }

    @Override
    public String toString() {
        return "OwnsBookDto{" +
                "query='" + query + '\'' +
                ", bookSearchField='" + bookSearchField + '\'' +
                ", studentSearchTextField='" + studentSearchTextField + '\'' +
                '}';
    }
}
