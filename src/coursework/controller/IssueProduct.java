package coursework.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import coursework.model.DatabaseConnection;
import coursework.model.IssuedBook;
import coursework.model.Notification;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXML.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IssueProduct implements Initializable {


    @FXML
    private JFXTextField bookSearchField;
    @FXML
    private Text bookName;
    @FXML
    private Text bookAuthor;
    @FXML
    private Text bookPublisher;
    @FXML
    private Text availability;
    @FXML
    private JFXTextField studentSearchTextField;
    @FXML
    private Text studentName;
    @FXML
    private Text studentEmail;
    @FXML
    private TableView<IssuedBook> shortTermBooksTable;
    @FXML
    private TableColumn<IssuedBook, String> sStudentName;
    @FXML
    private TableColumn<IssuedBook, String> sBookName;
    @FXML
    private TableColumn<IssuedBook, String> sIssuedTime;
    @FXML
    private Text contact;
    String boName = "";
    String stuName = "";
    @FXML
    private JFXButton issueBook;

    ObservableList<IssuedBook> list2 = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        initializeColumns();
        getData("SELECT * FROM ShortTermBook", list2, "IssuedTime",  shortTermBooksTable);
    }

    private void initializeColumns() {

        sStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        sBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        sIssuedTime.setCellValueFactory(new PropertyValueFactory<>("issuedTime"));

    }
    private void getData(String query, ObservableList<IssuedBook> list,  String field2, TableView tableView) {
        PreparedStatement pre = null;
        Connection conn = null;
        ResultSet rs = null;
        list.clear();
        try {
            conn = DatabaseConnection.connect();
            pre = conn.prepareStatement(query);
            rs = pre.executeQuery();
            while (rs.next()) {
                list.add(new IssuedBook(rs.getString("StudentID"), rs.getString("StudentName"), rs.getString("BookName"), rs.getString(field2)));
            }
            tableView.getItems().setAll(list);
        } catch (SQLException ex) {
            Logger.getLogger(IssueProduct.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void clearFieldsAndLabels() {
        bookSearchField.clear();
        studentSearchTextField.clear();
        bookName.setText("Название");
        bookAuthor.setText("Book Author");
        bookPublisher.setText("Book Publisher");
        availability.setText("Availability");
        studentName.setText("Student Name");
        studentEmail.setText("Email Address");
        contact.setText("Contact");
        boName = "";
        stuName = "";
    }

    private boolean checkIfStudentOwnsBook(String query) {
        PreparedStatement pre = null;
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.connect();
            pre = conn.prepareStatement(query);
            pre.setString(1, bookSearchField.getText());
            pre.setString(2, studentSearchTextField.getText());
            rs = pre.executeQuery();
            if (rs.next()) {
              coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "Information", studentName.getText() + " is already hoding this book");
                clearFieldsAndLabels();
                bookSearchField.requestFocus();
                return false;
            }
        } catch (SQLException e) {
            System.err.println(e);
        } finally {
            try {
                if (pre != null) {
                    pre.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e1) {
                System.err.println(e1);
            }
        }
        return true;
    }

    @FXML
    private void searchBook(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            boName = "";
            PreparedStatement pre = null;
            Connection conn = null;
            ResultSet rs = null;
            String query = "SELECT * FROM Book WHERE BookID = ? ";
            try {
                conn = DatabaseConnection.connect();
                if (bookSearchField.getText().isEmpty()) {
                  coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "Field validation", "The field is empty");
                } else {
                    pre = conn.prepareStatement(query);
                    pre.setString(1, bookSearchField.getText().trim());
                    rs = pre.executeQuery();
                    if (rs.next()) {
                        boName += rs.getString(2);
                        Notification notification = new Notification("Message", "Book found", 3);
                        String bName = rs.getString("Name");
                        String bAuthor = rs.getString("Author");
                        String bPublisher = rs.getString("Publisher");
                        String bAvailability = rs.getString("Availability");
                        if (bAvailability.equals("Not Available")) {
                            bookSearchField.clear();
                            bookName.setText(bName);
                            bookAuthor.setText(bAuthor);
                            bookPublisher.setText(bPublisher);
                            availability.setText(bAvailability);
                        } else {
                            bookName.setText(bName);
                            bookAuthor.setText(bAuthor);
                            bookPublisher.setText(bPublisher);
                            availability.setText(bAvailability);
                            studentSearchTextField.requestFocus();
                        }
                    } else {
                        bookSearchField.clear();
                        bookName.setText("Название");
                        bookAuthor.setText("Book Author");
                        bookPublisher.setText("Book Publisher");
                        availability.setText("Availability");
                        Notification notification = new Notification("Message", "No such book in the system", 3);
                    }
                }
            } catch (SQLException e) {
                System.err.println(e);
            } finally {
                try {
                    if (pre != null) {
                        pre.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e1) {
                    System.err.println(e1);
                }
            }
        }
    }

    @FXML
    private void searchStudent(KeyEvent event) {
        stuName = "";
        PreparedStatement pre = null;
        Connection conn = null;
        ResultSet rs = null;
        String query = "SELECT * FROM Student WHERE StudentID = ? COLLATE NOCASE";
        if (event.getCode() == KeyCode.ENTER) {
            try {
                conn = DatabaseConnection.connect();
                if (studentSearchTextField.getText().isEmpty()) {
                  coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "Field validation", "The field is empty");
                } else {
                    pre = conn.prepareStatement(query);
                    pre.setString(1, studentSearchTextField.getText().trim());
                    rs = pre.executeQuery();
                    if (rs.next()) {
                        stuName += rs.getString(2);
                        Notification notification = new Notification("Message", "Student found", 3);
                        String sName = rs.getString("Name");
                        String sEmail = rs.getString("Email");
                        String sPhone = rs.getString("Phone");
                        studentName.setText(sName);
                        studentEmail.setText(sEmail);
                        contact.setText(sPhone);
                    } else {
                        studentSearchTextField.clear();
                        studentName.setText("Student Name");
                        studentEmail.setText("Email Address");
                        contact.setText("Contact");
                        Notification notification = new Notification("Message", "Student not found", 3);
                    }
                }
            } catch (SQLException e) {
                System.err.println(e);
            } finally {
                try {
                    if (pre != null) {
                        pre.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e1) {
                    System.err.println(e1);
                }

            }
        }
    }

    @FXML
    private void issueBook(ActionEvent event) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatement1 = null;
        PreparedStatement preparedStatement2 = null;
        ResultSet resultSet = null;
        ResultSet resultSet1 = null;
        ResultSet resultSet2 = null;
        PreparedStatement pre1 = null;
        PreparedStatement pre2 = null;
        PreparedStatement pre3 = null;
        PreparedStatement pre4 = null;
        ResultSet rs = null;
        String query = "SELECT * FROM Book WHERE BookID = ?";
        String selectLongTermBorrowedTime = "SELECT COUNT(StudentID) FROM IssueBook WHERE StudentID = ?";
        String selectShortTermBorrowedTime = "SELECT COUNT(StudentID) FROM ShortTermBook WHERE StudentID = ?";
        if (bookSearchField.getText().isEmpty() || studentSearchTextField.getText().isEmpty() || studentName.getText().equals("Student Name") || bookName.getText().equals("Book Name")) {
          coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "Information", "Please enter in all fields");
        } else {
            try {
                connection = DatabaseConnection.connect();
                preparedStatement = connection.prepareStatement(query);
                preparedStatement1 = connection.prepareStatement(selectLongTermBorrowedTime);
                preparedStatement2 = connection.prepareStatement(selectShortTermBorrowedTime);
                preparedStatement.setString(1, bookSearchField.getText());
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    switch (resultSet.getString("Section")) {
                        case "К выдаче": {
                            preparedStatement2.setString(1, studentSearchTextField.getText());
                            resultSet2 = preparedStatement2.executeQuery();
                            int borrowedTimes = resultSet2.getInt(1);
                            if (borrowedTimes == 0 || borrowedTimes <= 1) {
                                int count;
                                String query1 = "INSERT INTO ShortTermBook (BookID,BookName,StudentID,StudentName,IssuedTime,ReturnTime,Hours) VALUES (?,?,?,?,?,?,?)";
                                String query2 = "UPDATE Book SET Availability = 'Not Available' WHERE BookID = ? COLLATE NOCASE";
                                String query3 = "SELECT RemainingBooks FROM Book WHERE BookID = ? COLLATE NOCASE";
                                String query4 = "UPDATE Book SET RemainingBooks = ? WHERE BookID = ? COLLATE NOCASE";
                                try {
                                    if (checkLateFee()) {
                                        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM,FormatStyle.SHORT);
                                        LocalDateTime issuedTime = LocalDateTime.now();
                                        LocalDateTime after2Hours = LocalDateTime.now().plusHours(2);
                                        pre1 = connection.prepareStatement(query1);
                                        pre2 = connection.prepareStatement(query2);
                                        pre3 = connection.prepareStatement(query3);
                                        pre1.setString(1, bookSearchField.getText().trim());
                                        pre1.setString(2, boName);
                                        pre1.setString(3, studentSearchTextField.getText().trim());
                                        pre1.setString(4, stuName);
                                        pre1.setString(5, formatter.format(issuedTime));
                                        pre1.setString(6, formatter.format(after2Hours));
                                        pre1.setInt(7, 2);
                                        pre2.setString(1, bookSearchField.getText().trim());
                                        pre3.setString(1, bookSearchField.getText().trim());
                                        rs = pre3.executeQuery();
                                        count = rs.getInt("RemainingBooks");
                                        if (checkIfStudentOwnsBook("SELECT * FROM ShortTermBook WHERE BookID = ? AND StudentID = ? COLLATE NOCASE") && checkIfStudentOwnsBook("SELECT * FROM IssueBook WHERE BookID = ? AND StudentID = ? COLLATE NOCASE")) {
                                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                            alert.setTitle("Confirmation");
                                            alert.setContentText("Are you sure you want to issue " + bookName.getText() + " to " + studentName.getText() + " ?");
                                            alert.setHeaderText(null);
                                            Optional<ButtonType> option = alert.showAndWait();
                                            if (option.get() == ButtonType.OK) {
                                                count--;
                                                pre4 = connection.prepareStatement(query4);
                                                pre4.setInt(1, count);
                                                pre4.setString(2, bookSearchField.getText());
                                                pre4.executeUpdate();
                                                pre1.executeUpdate();
                                                if (count == 0) {
                                                    pre2.executeUpdate();
                                                }
                                                Notification notification = new Notification("Message", "Book issue successfully completed", 3);
                                                bookSearchField.clear();
                                                studentSearchTextField.clear();
                                                bookName.setText("Название");
                                                bookAuthor.setText("Book Author");
                                                bookPublisher.setText("Book Publisher");
                                                availability.setText("Availability");
                                                studentName.setText("Student Name");
                                                studentEmail.setText("Email Address");
                                                contact.setText("Contact");
                                                stuName = "";
                                                boName = "";
                                                bookSearchField.requestFocus();
                                            }
                                        }
                                    }
                                } catch (SQLException e) {
                                    System.err.println(e);
                                }
                            } else {
                              coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "Information", "Maximum number of borrowed books reached");
                                clearFieldsAndLabels();
                            }
                            break;
                        }
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(IssueProduct.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        clearFieldsAndLabels();
    }

    private boolean checkLateFee() {
        String query = "SELECT * FROM Account";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.connect();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                double lateFeePerDay = resultSet.getDouble(2);
                if (lateFeePerDay == 0.0) {
                  coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "Information", "Late fee not set");
                    return false;

                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(IssueProduct.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();

                }
            } catch (SQLException ex) {
                Logger.getLogger(IssueProduct.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }
}
