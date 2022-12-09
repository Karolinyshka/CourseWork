
package coursework.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import coursework.dto.LibrarianDto;
import coursework.model.Book;
import coursework.utils.DBUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import coursework.model.DatabaseConnection;
import coursework.model.Librarian;
import coursework.model.Notification;
import org.apache.commons.codec.digest.DigestUtils;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserController implements Initializable {


    @FXML
    private TextField searchTextField;
    @FXML
    private TableView<Librarian> librarianTable;
    @FXML
    private TableColumn<Librarian, String> firstNameCol;
    @FXML
    private TableColumn<Librarian, String> lastNameCol;
    @FXML
    private TableColumn<Librarian, String> userNameCol;
    @FXML
    private TableColumn<Librarian, String> emailAddressCol;
    @FXML
    private TableColumn<Librarian, String> passwordCol;
    @FXML
    private JFXTextField firstName;
    @FXML
    private JFXTextField lastName;
    @FXML
    private JFXTextField userName;
    @FXML
    private JFXTextField emailAddress;
    @FXML
    private JFXPasswordField password1;
    @FXML
    private JFXPasswordField password2;
    ObservableList<Librarian> data = FXCollections.observableArrayList();
    public static boolean isEditableMode = false;
    private int id;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        password1.setTooltip(new Tooltip("The password must be at least 8 characters long"));
        password2.setTooltip(new Tooltip("The password must be at least 8 characters long"));
        initializeColumns();
        setTextFieldUserData();
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

        task.setOnSucceeded(event -> loadData());
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        firstName.setUserData("firstName");
        lastName.setUserData("lastName");
        userName.setUserData("username");
        emailAddress.setUserData("email");
        password1.setUserData("pass1");
        password2.setUserData("pass2");
        requestFocus(firstName);
        requestFocus(lastName);
        requestFocus(userName);
        requestFocus(emailAddress);
        requestFocus(password1);
        requestFocus(password2);
    }

    private void requestFocus(TextField field) {
        field.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN) {
                if (field.getUserData().equals("firstName")) {
                    lastName.requestFocus();
                }
                if (field.getUserData().equals("lastName")) {
                    userName.requestFocus();
                }
                if (field.getUserData().equals("username")) {
                    emailAddress.requestFocus();
                }
                if (field.getUserData().equals("email")) {
                    password1.requestFocus();
                }
                if (field.getUserData().equals("pass1")) {
                    password2.requestFocus();
                }
            }
            if (event.getCode() == KeyCode.UP) {
                if (field.getUserData().equals("pass2")) {
                    password1.requestFocus();
                }
                if (field.getUserData().equals("pass1")) {
                    emailAddress.requestFocus();
                }
                if (field.getUserData().equals("email")) {
                    userName.requestFocus();
                }
                if (field.getUserData().equals("username")) {
                    lastName.requestFocus();
                }
                if (field.getUserData().equals("lastName")) {
                    firstName.requestFocus();
                }
            }
        });
    }

    @FXML
    private void cancel(ActionEvent event) {
        clearFields();
    }

    @FXML
    private void searchLibrarianDeatails(KeyEvent event) {
        FilteredList<Librarian> filteredList = new FilteredList<>(data, p -> true);
        searchTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            filteredList.setPredicate((Predicate<? super Librarian>) librarian -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String filterToLowerCase = newValue.toLowerCase();
                if (librarian.getFirstName().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (librarian.getLastName().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (librarian.getUserName().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                if (librarian.getEmailAddress().toLowerCase().contains(filterToLowerCase)) {
                    return true;
                }
                librarianTable.setPlaceholder(new Text("No record match your search"));
                return false;
            });
            SortedList<Librarian> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(librarianTable.comparatorProperty());
            librarianTable.setItems(sortedList);
        }));
    }

    private boolean validateName(TextField field) {
        Pattern p = Pattern.compile("[a-z A-Z]+");
        Matcher M = p.matcher(field.getText());
        if (M.find() && M.group().equals(field.getText())) {
            return true;
        } else {
            coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.ERROR, "Name validation", field.getUserData() + " is invalid");
            return false;
        }
    }

    private boolean validateEmail() {
        Pattern p = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);
        Matcher M = p.matcher(emailAddress.getText());
        if (M.find() && M.group().equals(emailAddress.getText())) {
            return true;
        } else {
            coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.ERROR, "Email validation", "Please enter a valid email address!");
            return false;
        }
    }

    private boolean validateFields() {
        if (firstName.getText().isEmpty() || lastName.getText().isEmpty() || userName.getText().isEmpty() || emailAddress.getText().isEmpty() || password1.getText().isEmpty() || password2.getText().isEmpty()) {
            coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.ERROR, "Fields validation", "Please enter in all fields!");
            return false;
        }
        return true;
    }

    //TODO: edit
    @FXML
    private void saveLibrarian(ActionEvent event) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String query = "INSERT INTO User (Firstname, Lastame,Username, Email,Password,Usertype) VALUES (?,?,?,?,?,?)";
        String update = "UPDATE User SET Firstname = ?, Lastame = ?,Username = ?, Email = ?,Password = ? WHERE ID = ?";
        if (validateFields() && validateName(firstName) && validateName(lastName) && validateName(userName) && validateEmail() && validatePasswordLength()) {

            if (isEditableMode) {
                if (validatePasswords(password1, password2)) {
                    LibrarianDto librarianDto = new LibrarianDto(id, firstName.getText(), lastName.getText(), userName.getText(), emailAddress.getText(), password1.getText());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation");
                    alert.setHeaderText(null);
                    alert.setContentText("Save changes ?");
                    Optional<ButtonType> optional = alert.showAndWait();
                    if (optional.get() == ButtonType.OK) {
                        DBUtils.updateLibrarian(librarianDto);
                        Notification notification = new Notification("Information", "Librarian record successfully updated", 3);
                        clearFields();
                        firstName.requestFocus();
                        id = 0;
                        isEditableMode = false;
                    }
                }
            } else {
                if (checkIfUsernameExists(userName.getText()) && validatePasswords(password1, password2)) {
                    LibrarianDto librarianDto = new LibrarianDto(firstName.getText(), lastName.getText(), userName.getText(), emailAddress.getText(), password1.getText());
                    DBUtils.saveLibrarian(librarianDto);
                    Notification notification = new Notification("Information", "Librarian record successfully added", 3);
                    clearFields();
                    firstName.requestFocus();
                }
            }
            loadData();

        }
    }

    private void clearFields() {
        firstName.clear();
        lastName.clear();
        userName.clear();
        emailAddress.clear();
        password1.clear();
        password2.clear();
    }

    private void loadData() {
        data.clear();
        List<LibrarianDto> librarianDtoList = DBUtils.loadDataLibrarian();
        librarianDtoList.forEach(librarianDto -> {
            System.out.println(librarianDto);
            Librarian book = new Librarian(librarianDto.getId(), librarianDto.getFirstName(), librarianDto.getLastName(), librarianDto.getUserName(), librarianDto.getEmailAddress(), librarianDto.getPassword());
            data.add(book);
        });
        librarianTable.setItems(data);
    }

    private void initializeColumns() {
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        emailAddressCol.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
    }

    @FXML
    private void loadDetailsToUpdate(ActionEvent event) {
        Librarian librarian = (Librarian) librarianTable.getSelectionModel().getSelectedItem();
        if (librarian == null) {
            Notification notification = new Notification("Information", "Select librarian record to update", 3);
        } else {
            isEditableMode = true;
            userName.setEditable(false);
            id = librarian.getId();
            firstName.setText(librarian.getFirstName());
            lastName.setText(librarian.getLastName());
            userName.setText(librarian.getUserName());
            emailAddress.setText(librarian.getEmailAddress());
            password1.setText(librarian.getPassword());
            password2.setText(librarian.getPassword());
        }
    }

    @FXML
    private void deleteLibrarian(ActionEvent event) {
        Librarian librarian = (Librarian) librarianTable.getSelectionModel().getSelectedItem();
        if (librarian == null) {
            Notification notification = new Notification("Information", "Select librarian record to delete", 3);
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confimation");
            alert.setContentText("Are you sure you want to delete " + librarian.getFirstName() + " " + librarian.getLastName() + " ?");
            alert.setHeaderText(null);
            Optional<ButtonType> optional = alert.showAndWait();
            if (optional.get() == ButtonType.OK) {
                DBUtils.deleteLibrarian(librarian.getId());
                Notification notification = new Notification("Information", "Libraian record successfully deleted", 3);
                clearFields();
                firstName.requestFocus();
            }
            loadData();
        }
    }

    private void setTextFieldUserData() {
        firstName.setUserData("First Name");
        lastName.setUserData("Last name");
        userName.setUserData("Username");
    }

    private Stage getStage() {
        return (Stage) firstName.getScene().getWindow();
    }

    private boolean checkIfUsernameExists(String username) {
        boolean check = DBUtils.checkIfUsernameExists(username);
        if (!check) {
            coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "Username Validation", "Another Librarian is using this username");
            return false;
        }

        return true;
    }

    private boolean validatePasswords(PasswordField password1, PasswordField password2) {
        if (password1.getText().equals(password2.getText())) {
            return true;
        }
        coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.ERROR, "Information", "Passwords don't match.");
        password1.clear();
        password2.clear();
        password1.requestFocus();
        return false;
    }

    private boolean validatePasswordLength() {
        if (password1.getText().length() < 8 || password2.getText().length() < 8) {
            coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.ERROR, "Password length validation", "The password must be at least 8 characters long");
            return false;
        }
        return true;
    }
}
