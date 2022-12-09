package coursework.dto;

import java.io.Serializable;

public class StudentDto implements Serializable {
    private String userId;
    private String studentName;
    private String userEmail;
    private String studentPhone;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getStudentPhone() {
        return studentPhone;
    }

    public void setStudentPhone(String studentPhone) {
        this.studentPhone = studentPhone;
    }

    @Override
    public String toString() {
        return "StudentDto{" +
                "userId='" + userId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", studentPhone='" + studentPhone + '\'' +
                '}';
    }
}
