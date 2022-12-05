package coursework.controller;

import com.jfoenix.controls.JFXButton;
import coursework.model.Book;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import coursework.model.DatabaseConnection;
import coursework.model.Notification;
import coursework.model.Student;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Clients implements Initializable {



    @FXML
    private TextField searchTextField;
    @FXML
    private TextField studentID;
    @FXML
    private TextField studentName;
    @FXML
    private JFXButton save;
    @FXML
    private JFXButton update;
    @FXML
    private TextField studentEmail;
    @FXML
    private TextField studentPhone;
    @FXML
    private TableView<Student> studentTable;
    @FXML
    private TableColumn<Student, String> stuID;
    @FXML
    private TableColumn<Student, String> stunNme;
    @FXML
    private TableColumn<Student, String> stueEmail;
    @FXML
    private TableColumn<Student, String> stuPhone;
    ObservableList<Student> studentData = FXCollections.observableArrayList();
    ObservableList<Student> students = FXCollections.observableArrayList();
    @FXML
    private JFXButton export2;
    @FXML
    private JFXButton cancel;

    @FXML
    private ContextMenu selectStudentContext;
    @FXML
    private MenuItem selectMenu;

    @FXML


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        initiliazeColumns();
        studentID.setUserData("id");
        studentName.setUserData("name");
        studentEmail.setUserData("email");
        studentPhone.setUserData("phone");
        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                int max = 40;
                for (int i = 0; i <= max; i++) {
                    if (isCancelled()) {
                        break;
                    }
                    updateProgress(i, max);
                    Thread.sleep(25);
                }
                return max;
            }
        };
        loadData();
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        requestFocus(studentID);
        requestFocus(studentName);
        requestFocus(studentEmail);
        requestFocus(studentPhone);
        studentTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


    }

    private void clearFields() {
        studentID.clear();
        studentName.clear();
        studentEmail.clear();
        studentPhone.clear();
    }

    private boolean validateFields() {
        if (studentID.getText().isEmpty() || studentName.getText().isEmpty() || studentEmail.getText().isEmpty() || studentPhone.getText().isEmpty()) {
            coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.ERROR, "Field validation", "Please enter in all fields!");
            return false;
        } else {
            return true;
        }
    }

    private boolean validateID() {
        Pattern p = Pattern.compile("[0-9./a-z A-Z]+");
        Matcher M = p.matcher(studentID.getText());
        if (M.find() && M.group().equals(studentID.getText())) {
            return true;
        } else {
            coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.ERROR, "ID validation", "Please enter a valid student id!");
            return false;
        }
    }

    private boolean validateName() {
        Pattern p = Pattern.compile("[a-z A-Z]+");
        Matcher M = p.matcher(studentName.getText());
        if (M.find() && M.group().equals(studentName.getText())) {
            return true;
        } else {
            coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.ERROR, "Name validation", "Please enter a valid student name!");
            return false;
        }
    }

    private boolean validateEmail() {
        Pattern p = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);
        Matcher M = p.matcher(studentEmail.getText());
        if (M.find() && M.group().equals(studentEmail.getText())) {
            return true;
        } else {
            coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.ERROR, "Email validation", "Please enter a valid email address!");
            return false;
        }
    }

    private boolean validatePhoneNumber() {
        Pattern p1 = Pattern.compile("[+]{1}265[8]{2}[0-9]{7}");
        Pattern p2 = Pattern.compile("[+]{1}265[9]{2}[0-9]{7}");
        Matcher m1 = p1.matcher(studentPhone.getText());
        Matcher m2 = p2.matcher(studentPhone.getText());
        if (m1.find() && m1.group().equals(studentPhone.getText()) || m2.find() && m2.group().equals(studentPhone.getText())) {
            return true;
        } else {
            coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.ERROR, "Phone number validation", "Please enter a valid phone number!");
            return false;
        }
    }

    private boolean checkIFIDExist() {
        PreparedStatement pre = null;
        Connection conn = null;
        ResultSet rs = null;
        String query = "SELECT * FROM Student WHERE studentID = ?";
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query);
            pre.setString(1, studentID.getText());
            rs = pre.executeQuery();
            if (rs.next()) {
                coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "ID validation", "Student id already exist");
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
    private void fetchStudentFeesDetails(MouseEvent event) {
        Connection conn = null;
        PreparedStatement pre = null;
        ResultSet rs = null;
        String query = "SELECT * FROM Student WHERE studentID = ?";
        Student stu = (Student) studentTable.getSelectionModel().getSelectedItem();
        if (stu == null) {
        }
        if (stu != null) {
                save.setDisable(false);
                update.setVisible(true);

            } else {
                try {
                    studentID.setEditable(false);
                    update.setVisible(true);

                    save.setDisable(true);
                    conn = DatabaseConnection.Connect();
                    pre = conn.prepareStatement(query);
                    pre.setString(1, stu.getStudentID());
                    rs = pre.executeQuery();
                    while (rs.next()) {
                        studentID.setText(rs.getString(1));
                        studentName.setText(rs.getString(2));
                        studentEmail.setText(rs.getString(3));
                        studentPhone.setText(rs.getString(4));
                    }
                } catch (SQLException ex) {
                    System.err.println(ex);
                } finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                        if (pre != null) {
                            pre.close();
                        }
                        if (conn != null) {
                            conn.close();
                        }
                    } catch (SQLException ex) {
                        System.err.println(ex);
                    }
                }
            }
        }


    private void requestFocus(TextField field) {
        field.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.DOWN) {
                    if (field.getUserData().equals("id")) {
                        studentName.requestFocus();
                    }
                    if (field.getUserData().equals("name")) {
                        studentEmail.requestFocus();
                    }
                    if (field.getUserData().equals("email")) {
                        studentPhone.requestFocus();
                    }
                }
                if (event.getCode() == KeyCode.UP) {
                    if (field.getUserData().equals("phone")) {
                        studentEmail.requestFocus();
                    }
                    if (field.getUserData().equals("email")) {
                        studentName.requestFocus();
                    }
                    if (field.getUserData().equals("name")) {
                        studentID.requestFocus();
                    }
                }
            }
        });
    }


    @FXML
    private void cancel(ActionEvent event) {
        clearFields();
        update.setVisible(false);
        save.setDisable(false);
        studentID.setEditable(true);
        studentTable.getSelectionModel().clearSelection();
        searchTextField.clear();
    }


    @FXML
    private void saveStudent(ActionEvent event) {
        PreparedStatement pre = null;
        Connection conn = null;
        String query = "INSERT INTO Student (studentID,Name,Email,Phone) VALUES (?,?,?,?)";
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query);
            if (validateFields() && validateID() && checkIFIDExist() && validateName() && validateEmail() && validatePhoneNumber()) {
                pre.setString(1, studentID.getText().trim());
                pre.setString(2, studentName.getText().trim());
                pre.setString(3, studentEmail.getText().trim());
                pre.setString(4, studentPhone.getText().trim());
                pre.executeUpdate();
                loadData();
                Notification notification = new Notification("Message", "Student successfully added", 3);
                clearFields();
                save.setDisable(false);
            }
        } catch (SQLException e) {
            System.err.println(e);
        } finally {
            try {
                if (pre != null) {
                    pre.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e1) {
                System.err.println(e1);
            }
        }
    }

    @FXML
    private void updateStudent(ActionEvent event) {
        PreparedStatement pre = null;
        Connection conn = null;
        String query = "UPDATE Student SET Name = ?,Email = ?,Phone = ? WHERE studentID = ?";
        try {
            conn = DatabaseConnection.Connect();
            if (validateFields() && validateName() && validateEmail() && validatePhoneNumber()) {
                pre = conn.prepareStatement(query);
                pre.setString(1, studentName.getText().trim());
                pre.setString(2, studentEmail.getText().trim());
                pre.setString(3, studentPhone.getText().trim());
                pre.setString(4, studentID.getText().trim());
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText(null);
                alert.setContentText("Save changes ?");
                Optional<ButtonType> choice = alert.showAndWait();
                if (choice.get() == ButtonType.OK) {
                    pre.executeUpdate();
                    loadData();
                    clearFields();
                    Notification notification = new Notification("Message", "Student information successfully updated", 3);
                    studentID.setEditable(true);
                    update.setVisible(false);
                    save.setDisable(false);
                    searchTextField.clear();
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (pre != null) {
                    pre.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
    }

    @FXML
    private void deleteStudent(ActionEvent event) {
        PreparedStatement pre = null;
        PreparedStatement pre2 = null;
        PreparedStatement pre21 = null;
        PreparedStatement pre3 = null;
        PreparedStatement pre4 = null;
        PreparedStatement insert = null;
        Connection conn = null;
        ResultSet rs2 = null;
        ResultSet rs21 = null;
        ResultSet rs3 = null;
        ResultSet rs4 = null;
        String query = "DELETE FROM Student WHERE studentID = ?";
        String query2 = "SELECT * FROM IssueBook WHERE StudentID = ?";
        String query21 = "SELECT * FROM ShortTermBook WHERE StudentID = ?";
        String query3 = "SELECT Name FROM Student WHERE StudentID = ?";
        String select = "SELECT * FROM Student WHERE StudentId = ?";
        String insertQuery = "INSERT INTO ArhieveStudent (studentID,Name,Email,Phone) VALUES (?,?,?,?)";
        try {
            conn = DatabaseConnection.Connect();
            pre2 = conn.prepareStatement(query2);
            pre21 = conn.prepareStatement(query21);
            pre3 = conn.prepareStatement(query3);
            pre4 = conn.prepareStatement(select);
            insert = conn.prepareStatement(insertQuery);
            pre2.setString(1, studentID.getText());
            pre21.setString(1, studentID.getText());
            pre3.setString(1, studentID.getText());
            rs2 = pre2.executeQuery();
            rs21 = pre21.executeQuery();
            if (rs2.next() || rs21.next()) {
                rs3 = pre3.executeQuery();
                String stuName = rs3.getString("Name");
                coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "Information", stuName + " is holding a book");
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to archieve this student record ?");
                Optional<ButtonType> choice = alert.showAndWait();
                if (choice.get() == ButtonType.OK) {
                    pre4.setString(1, studentID.getText());
                    rs4 = pre4.executeQuery();
                    while (rs4.next()) {
                        insert.setString(1, rs4.getString(1));
                        insert.setString(2, rs4.getString(2));
                        insert.setString(3, rs4.getString(3));
                        insert.setString(4, rs4.getString(4));
                    }
                    insert.executeUpdate();
                    pre = conn.prepareStatement(query);
                    pre.setString(1, studentID.getText());
                    pre.executeUpdate();
                    loadData();
                    save.setDisable(false);
                    update.setVisible(false);
                    studentID.setEditable(true);
                    Notification notification = new Notification("Information", "Student record successfully moved to archieved student list", 3);
                    clearFields();
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (rs21 != null) {
                    rs21.close();
                }
                if (pre != null) {
                    pre.close();
                }
                if (pre2 != null) {
                    pre2.close();
                }
                if (pre21 != null) {
                    pre21.close();
                }
                if (rs2 != null) {
                    rs2.close();
                }
                if (pre4 != null) {
                    pre4.close();
                }
                if (insert != null) {
                    insert.close();
                }
                if (rs4 != null) {
                    rs4.close();
                }
                if (rs3 != null) {
                    rs3.close();
                }
                if (pre3 != null) {
                    pre3.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
    }

    @FXML
    private void searchStudentDeatails(KeyEvent event) {
        FilteredList<Student> filteredList = new FilteredList<>(studentData, p -> true);
        searchTextField.textProperty().addListener((ObservableValue, oldValue, newValue) -> {
            filteredList.setPredicate((Predicate<? super Student>) student -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String filterLowerCase = newValue.toLowerCase();
                if (student.getStudentID().toLowerCase().contains(filterLowerCase)) {
                    return true;
                }
                if (student.getStudentName().toLowerCase().contains(filterLowerCase)) {
                    return true;
                }
                studentTable.setPlaceholder(new Text("No record match your search"));
                return false;
            });
            SortedList<Student> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(studentTable.comparatorProperty());
            studentTable.getItems().setAll(sortedList);
        });
    }

    private void loadData() {
        studentData.clear();
        Connection conn = null;
        PreparedStatement pre = null;
        ResultSet rs = null;
        String query = "SELECT * FROM Student order by studentID";
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query);
            rs = pre.executeQuery();
            while (rs.next()) {
                studentData.add(new Student(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
            }
            studentTable.getItems().setAll(studentData);
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pre != null) {
                    pre.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
    }

    private void initiliazeColumns() {
        stuID.setCellValueFactory(new PropertyValueFactory<>("studentID"));
        stunNme.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        stueEmail.setCellValueFactory(new PropertyValueFactory<>("studentEmail"));
        stuPhone.setCellValueFactory(new PropertyValueFactory<>("studentPhone"));
    }

    private void loadArchievedRecord() {
        studentData.clear();
        Connection conn = null;
        PreparedStatement pre = null;
        ResultSet rs = null;
        String query = "SELECT * FROM ArhieveStudent";
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query);
            rs = pre.executeQuery();
            while (rs.next()) {
                studentData.add(new Student(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
            }
            studentTable.getItems().setAll(studentData);
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pre != null) {
                    pre.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
    }





    @FXML
    private void deleteStudentRecord(ActionEvent event) {
        String query = "DELETE FROM Student WHERE studentID = ?";
        String query2 = "SELECT * FROM IssueBook WHERE StudentID = ?";
        String query21 = "SELECT * FROM ShortTermBook WHERE StudentID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatement2 = null;
        PreparedStatement preparedStatement3 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        Student student = (Student) studentTable.getSelectionModel().getSelectedItem();
        if (student == null) {
            Notification notification = new Notification("Information", "No record selected", 3);
        } else {
            try {
                connection = DatabaseConnection.Connect();
                preparedStatement = connection.prepareStatement(query);
                preparedStatement2 = connection.prepareStatement(query2);
                preparedStatement3 = connection.prepareStatement(query21);
                preparedStatement.setString(1, student.getStudentID());
                preparedStatement2.setString(1, student.getStudentID());
                preparedStatement3.setString(1, student.getStudentID());
                rs = preparedStatement2.executeQuery();
                rs1 = preparedStatement3.executeQuery();
                if (rs.next() || rs1.next()) {
                    coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "Information", student.getStudentName() + " is holding a book");
                } else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete record");
                    alert.setHeaderText(null);
                    alert.setContentText("Are you sure you want delete " + student.getStudentName() + " record ?");
                    Optional<ButtonType> optional = alert.showAndWait();
                    if (optional.get() == ButtonType.OK) {
                        preparedStatement.executeUpdate();
                        Notification notification = new Notification("Information", "Student record successfully deleted", 3);
                        cancel(new ActionEvent());
                    } else {
                        cancel(new ActionEvent());
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(Clients.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (rs1 != null) {
                        rs1.close();
                    }
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (preparedStatement2 != null) {
                        preparedStatement2.close();
                    }
                    if (preparedStatement3 != null) {
                        preparedStatement3.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Clients.class.getName()).log(Level.SEVERE, null, ex);
                }
                loadData();
            }
        }
    }
}
