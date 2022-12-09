package coursework.dto;

import java.io.Serializable;

public class ShortBookDto implements Serializable {
    private String bookId;
    private String bookName;
    private String studentId;
    private String studentName;
    private String issuedTime;
    private String returnTime;
    private int hours;

    public ShortBookDto(String bookId, String bookName, String studentId, String studentName,
                        String issuedTime, String returnTime, int hours) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.studentId = studentId;
        this.studentName = studentName;
        this.issuedTime = issuedTime;
        this.returnTime = returnTime;
        this.hours = hours;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getIssuedTime() {
        return issuedTime;
    }

    public void setIssuedTime(String issuedTime) {
        this.issuedTime = issuedTime;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    @Override
    public String toString() {
        return "ShortBookDto{" +
                "bookId='" + bookId + '\'' +
                ", bookName='" + bookName + '\'' +
                ", studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", issuedTime='" + issuedTime + '\'' +
                ", returnTime='" + returnTime + '\'' +
                ", hours=" + hours +
                '}';
    }
}
