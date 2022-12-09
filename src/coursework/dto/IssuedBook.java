package coursework.dto;

import java.io.Serializable;

public class IssuedBook implements Serializable {
    private String studentID;
    private String studentName;
    private String issuedTime;
    private double fee;
    private int days;
    private String bookName;
    private String bookID;
    private int issuedID;

    public IssuedBook(int issuedID, String bookID, String bookName, String studentID, String studentName, String issuedTime, int days, double fee) {
        this.issuedID = issuedID;
        this.bookID = bookID;
        this.bookName = bookName;
        this.studentID = studentID;
        this.studentName = studentName;
        this.issuedTime = issuedTime;
        this.days = days;
        this.fee = fee;
    }

    public IssuedBook(String studentID, String studentName, String issuedTime, String bookName) {
        this.studentID = studentID;
        this.studentName = studentName;
        this.issuedTime = issuedTime;
        this.bookName = bookName;
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

    @Override
    public String toString() {
        return "IssuedBook{" +
                "studentID='" + studentID + '\'' +
                ", studentName='" + studentName + '\'' +
                ", issuedTime='" + issuedTime + '\'' +
                ", fee=" + fee +
                ", days=" + days +
                ", bookName='" + bookName + '\'' +
                ", bookID='" + bookID + '\'' +
                ", issuedID=" + issuedID +
                '}';
    }
}
