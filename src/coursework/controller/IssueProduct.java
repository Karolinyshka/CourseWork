package coursework.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import coursework.dto.*;
import coursework.model.Book;
import coursework.utils.DBUtils;
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
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
        getData("SELECT * FROM ShortTermBook", list2, "IssuedTime", shortTermBooksTable);
    }

    private void initializeColumns() {

        sStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        sBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        sIssuedTime.setCellValueFactory(new PropertyValueFactory<>("issuedTime"));

    }

    private void getData(String query, ObservableList<IssuedBook> list, String field2, TableView tableView) {
        list.clear();
        List<IssuedBookDto> issuedBookDtoList = DBUtils.getData(new GetDataDto(query, field2));
        List<IssuedBook> collect = issuedBookDtoList.stream()
                .map(issuedBookDto -> {
                    IssuedBook issuedBook = new IssuedBook(issuedBookDto.getStudentID(), issuedBookDto.getStudentName(), issuedBookDto.getBookName(), issuedBookDto.getIssuedTime());
                    return issuedBook;
                }).collect(Collectors.toList());

        tableView.getItems().setAll(collect);
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
        OwnsBookDto ownsBookDto = new OwnsBookDto();
        ownsBookDto.setQuery(query);
        ownsBookDto.setBookSearchField(bookSearchField.getText());
        ownsBookDto.setStudentSearchTextField(studentSearchTextField.getText());
        boolean check = DBUtils.checkIfStudentOwnsBook(ownsBookDto);

        if (!check) {
            coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "Information", studentName.getText() + " is already hoding this book");
            clearFieldsAndLabels();
            bookSearchField.requestFocus();
            return false;
        }

        return true;
    }

    @FXML
    private void searchBook(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            BookDto bookDto = DBUtils.searchBook(bookSearchField.getText().trim());
            boName = "";
            if (bookSearchField.getText().isEmpty()) {
                coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "Field validation", "The field is empty");
            } else {
                if (!bookDto.equals(null)) {
                    boName += bookDto.getName();
                    Notification notification = new Notification("Message", "Book found", 3);
                    String bName = bookDto.getName();
                    String bAuthor = bookDto.getAuthor();
                    String bPublisher = bookDto.getPublisher();
                    String bAvailability = bookDto.getAvailability();
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
        }
    }
    public  StudentDto studentDto;
    @FXML
    private void searchStudent(KeyEvent event) {
        stuName = "";
        if (event.getCode() == KeyCode.ENTER) {
            if (studentSearchTextField.getText().isEmpty()) {
                coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "Field validation", "The field is empty");
            } else {
                     studentDto = DBUtils.searchStudent(studentSearchTextField.getText().trim());

                if (!studentDto.equals(null)) {
                    stuName += studentDto.getStudentName();
                    Notification notification = new Notification("Message", "Student found", 3);
                    String sName = studentDto.getStudentName();
                    String sEmail = studentDto.getUserEmail();
                    String sPhone = studentDto.getStudentPhone();
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
        }
    }

    @FXML
    private void issueBook(ActionEvent event) {
        if (bookSearchField.getText().isEmpty() || studentSearchTextField.getText().isEmpty() || studentName.getText().equals("Student Name") || bookName.getText().equals("Book Name")) {
            coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "Information", "Please enter in all fields");
        } else {
            System.out.println(bookSearchField.getText());
            String section = DBUtils.searchBookByBookId(bookSearchField.getText());
            if (!(section == null)) {
                switch (section) {
                    case "К выдаче": {
                        int borrowedTimes = DBUtils.selectShortTermBorrowedTime(studentSearchTextField.getText());
                        if (borrowedTimes == 0 || borrowedTimes <= 1) {

                            if (checkLateFee()) {
                                DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT);
                                LocalDateTime issuedTime = LocalDateTime.now();
                                LocalDateTime after2Hours = LocalDateTime.now().plusHours(2);
                                System.out.println(issuedTime);
                                System.out.println(after2Hours);

                                int count = DBUtils.countOfRemainingBooks(studentSearchTextField.getText().trim());

                                if (checkIfStudentOwnsBook("SELECT * FROM ShortTermBook WHERE BookID = ? AND StudentID = ? COLLATE NOCASE") && checkIfStudentOwnsBook("SELECT * FROM IssueBook WHERE BookID = ? AND StudentID = ? COLLATE NOCASE")) {
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setTitle("Confirmation");
                                    alert.setContentText("Are you sure you want to issue " + bookName.getText() + " to " + studentName.getText() + " ?");
                                    alert.setHeaderText(null);
                                    Optional<ButtonType> option = alert.showAndWait();
                                    if (option.get() == ButtonType.OK) {
                                        count--;
                                        DBUtils.updateRemainingBooks(new UpdateRemainBookDto(bookSearchField.getText(), count));
                                        ShortBookDto shortBookDto = new ShortBookDto(bookSearchField.getText(), boName, studentSearchTextField.getText(),
                                              studentDto.getStudentName(), formatter.format(issuedTime), formatter.format(after2Hours), 2);
                                        System.out.println(shortBookDto);
                                        DBUtils.insertIntoShortBook(shortBookDto);
                                        if (count == 0) {
                                            DBUtils.changeAvailabilityInBookEntity(bookSearchField.getText().trim());

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
                            } else {
                                coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "Information", "Maximum number of borrowed books reached");
                                clearFieldsAndLabels();
                            }
                            break;
                        }
                    }
                }
            }
        }
    }


    @FXML
    private void cancel(ActionEvent event) {
        clearFieldsAndLabels();
    }

    private boolean checkLateFee() {
        double variable = DBUtils.checkLateFee();
        if (variable == 0.0) {
            coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "Information", "Late fee not set");
            return false;

        }
        return true;
    }
}
