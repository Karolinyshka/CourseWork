package coursework.dto;

import java.io.Serializable;

public class IssuedBookDto implements Serializable {
    private String studentID;
    private String studentName;
    private String issuedTime;
    private double fee;
    private int days;
    private String bookName;
    private String bookID;
    private int issuedID;
    private String query;
    private String field;

    public IssuedBookDto(String studentID, String studentName, String issuedTime, double fee, int days, String bookName, String bookID, int issuedID, String query, String field) {
        this.studentID = studentID;
        this.studentName = studentName;
        this.issuedTime = issuedTime;
        this.fee = fee;
        this.days = days;
        this.bookName = bookName;
        this.bookID = bookID;
        this.issuedID = issuedID;
        this.query = query;
        this.field = field;
    }

    public IssuedBookDto(String studentID, String studentName, String issuedTime, double fee, int days, String bookName, String bookID, int issuedID, String query) {
        this.studentID = studentID;
        this.studentName = studentName;
        this.issuedTime = issuedTime;
        this.fee = fee;
        this.days = days;
        this.bookName = bookName;
        this.bookID = bookID;
        this.issuedID = issuedID;
        this.query = query;
    }

    public IssuedBookDto(int issuedID, String bookID, String bookName, String studentID, String studentName, String issuedTime, int days, double fee) {
        this.issuedID = issuedID;
        this.bookID = bookID;
        this.bookName = bookName;
        this.studentID = studentID;
        this.studentName = studentName;
        this.issuedTime = issuedTime;
        this.days = days;
        this.fee = fee;
    }

    public IssuedBookDto(String studentID, String studentName, String issuedTime, String bookName) {
        this.studentID = studentID;
        this.studentName = studentName;
        this.issuedTime = issuedTime;
        this.bookName = bookName;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
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

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public int getIssuedID() {
        return issuedID;
    }

    public void setIssuedID(int issuedID) {
        this.issuedID = issuedID;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return "IssuedBookDto{" +
                "studentID='" + studentID + '\'' +
                ", studentName='" + studentName + '\'' +
                ", issuedTime='" + issuedTime + '\'' +
                ", fee=" + fee +
                ", days=" + days +
                ", bookName='" + bookName + '\'' +
                ", bookID='" + bookID + '\'' +
                ", issuedID=" + issuedID +
                ", query='" + query + '\'' +
                ", field='" + field + '\'' +
                '}';
    }


}
