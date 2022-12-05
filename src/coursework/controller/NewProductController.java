package coursework.controller;

import com.jfoenix.controls.JFXButton;
import coursework.model.Student;
import de.jensd.fx.glyphs.octicons.OctIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import coursework.model.Book;
import coursework.model.DatabaseConnection;
import coursework.model.Notification;

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

public class NewProductController implements Initializable {

    @FXML
    private ImageView spinner;
    @FXML
    private TextField searchTextField;
    @FXML
    private JFXButton save;
    @FXML
    private JFXButton update;
    @FXML
    private JFXButton delete;
    @FXML
    private TextField BookID;
    @FXML
    private TextField Name;
    @FXML
    private TextField Author;
    @FXML
    private TextField Publisher;
    @FXML
    private TextField Edition;
    @FXML
    private TextField Quantity;
    @FXML
    private TextField Availability;
    @FXML
    private TextField Section;
    @FXML
    private TextField RemainingBooks;
    @FXML
    private TableView<Book> tableView;
    @FXML
    private TableColumn<Book, String> booKID;
    @FXML
    private TableColumn<Book, String> bookName;
    @FXML
    private TableColumn<Book, String> bookAuthor;
    @FXML
    private TableColumn<Book, String> bookPublisher;
    @FXML
    private TableColumn<Book, String> edition;
    @FXML
    private TableColumn<Book, Integer> quantity;
    @FXML
    private TableColumn<Book, Integer> remainingBooks;
    @FXML
    private TableColumn<Book, String> availability;
    @FXML
    private TableColumn<Book, String> sectionCol;
    @FXML
    private OctIconView searchIcon;
    ObservableList<Book> data = FXCollections.observableArrayList();
    ObservableList<Book> books = FXCollections.observableArrayList();
    @FXML
    private HBox controlBox;
    public static HBox box;
    @FXML
    private ComboBox<String> comboBox;
    ContextMenu contextMenu;
    String comboboxValue;
    @FXML
    private ContextMenu selectStudentContext;
    @FXML
    private MenuItem selectMenu;
    @FXML
    private TableColumn<?, ?> check;
    int selected = 0;
    @FXML
    private JFXButton cancel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initiliazeColumns();
        BookID.setUserData("id");
        Name.setUserData("name");
        Author.setUserData("author");
        Publisher.setUserData("publisher");
        Edition.setUserData("edition");
        Quantity.setUserData("quantity");
        Availability.setUserData("availability");
        Section.setUserData("section");
        RemainingBooks.setUserData("remaining");
        contextMenu = new ContextMenu(new MenuItem("Вернуть к текущим товарам"));
        contextMenu.setOnAction((e) -> {
            unArchiveRecord();
        });
        comboBox.getItems().addAll(FXCollections.observableArrayList("Товары", "Списанные товары"));
        comboBox.setValue("Товары");
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
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                spinner.setVisible(false);
                loadData();
            }
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    private void clearFields() {
        BookID.clear();
        Name.clear();
        Author.clear();
        Publisher.clear();
        Edition.clear();
        Quantity.clear();
        Availability.clear();
        Section.clear();
        RemainingBooks.clear();
    }
    private boolean validateName() {
        Pattern p = Pattern.compile("[a-z A-Z # ; ' & 1-9 +]+");
        Matcher m = p.matcher(bookName.getText());
        if (m.find() && m.group().equals(bookName.getText())) {
            return true;
        } else {
            coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.ERROR, "Book name validation", "Please enter a valid book name!");
            return false;
        }
    }
    private boolean validateEdition() {
        Pattern p = Pattern.compile("[0-9]+");
        Matcher m = p.matcher(edition.getText());
        if (m.find() && m.group().equals(edition.getText())) {
            return true;
        } else {
            coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.ERROR, "Book edition validation", "Please enter a valid book edition!");
            return false;
        }
    }
    @FXML
    private void updateStudent(ActionEvent event) {
        PreparedStatement pre = null;
        Connection conn = null;
        String query = "UPDATE Book SET Name = ?,Author = ?,Publisher = ?,Edition = ?,Quantity = ?,RemainingBooks = ?,Availability = ?,Section = ? WHERE BookID = ?";
        try {
            conn = DatabaseConnection.Connect();
             {
                pre = conn.prepareStatement(query);
                pre.setString(1, Name.getText().trim());
                pre.setString(2, Author.getText().trim());
                pre.setString(3, Publisher.getText().trim());
                pre.setString(4, Edition.getText().trim());
                pre.setString(5, Quantity.getText().trim());
                pre.setString(6, RemainingBooks.getText().trim());
                 pre.setString(7, Availability.getText().trim());
                pre.setString(8, Section.getText().trim());
                pre.setString(9, BookID.getText().trim());
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
                    BookID.setEditable(true);
                    update.setVisible(false);
                    delete.setVisible(false);
                    save.setDisable(false);
                    searchTextField.clear();
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }
    @FXML
    private void saveBook(ActionEvent event) {
        PreparedStatement pre = null;
        Connection conn = null;
        String query = "INSERT INTO Book (BookID,Name,Author,Publisher,Edition,Quantity,RemainingBooks,Availability,Section) VALUES (?,?,?,?,?,?,?,?,?)";
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query);
            if (checkIFIDExist()) {
                pre.setString(1, BookID.getText().trim());
                pre.setString(2, Name.getText().trim());
                pre.setString(3, Author.getText().trim());
                pre.setString(4, Publisher.getText().trim());
                pre.setString(5, Edition.getText().trim());
                pre.setString(6, Quantity.getText().trim());
                pre.setString(7, RemainingBooks.getText().trim());
                pre.setString(8, Availability.getText().trim());
                pre.setString(9, Section.getText().trim());
                pre.executeUpdate();
                loadData();
                Notification notification = new Notification("Message", "Student successfully added", 3);
                clearFields();
                save.setDisable(false);
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
    }
    @FXML
    private void deleteStudentRecord(ActionEvent event) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            Book librarian = (Book) tableView.getSelectionModel().getSelectedItem();
            if (librarian == null) {
                Notification notification = new Notification("Information", "Select librarian record to delete", 3);
            } else {
                try {
                    connection = DatabaseConnection.Connect();
                    preparedStatement = connection.prepareStatement("DELETE FROM Book WHERE BookID = ?");
                    preparedStatement.setString(1, librarian.getBookID());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confimation");
                    alert.setContentText("Are you sure you want to delete " + " ?");
                    alert.setHeaderText(null);
                    Optional<ButtonType> optional = alert.showAndWait();
                    if (optional.get() == ButtonType.OK) {
                        preparedStatement.executeUpdate();
                        Notification notification = new Notification("Information", "Libraian record successfully deleted", 3);
                        clearFields();
                        Name.requestFocus();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        if (preparedStatement != null) {
                            preparedStatement.close();
                        }
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    loadData();
                }
            }
        }

    @FXML
    private void searchBook(KeyEvent event) {
        FilteredList<Book> filteredList = new FilteredList<>(data, p -> true);
        searchTextField.textProperty().addListener((ObservableValue, oldValue, newValue) -> {
            filteredList.setPredicate((Predicate<? super Book>) book -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (book.getBookID().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (book.getBookName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (book.getBookAuthor().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (book.getBookPublisher().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (book.getBookSection().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                tableView.setPlaceholder(new Text("No record match your search"));
                return false;
            });
            SortedList<Book> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.getItems().setAll(sortedList);
        });
    }
    @FXML
    private void cancel(ActionEvent event) {
        clearFields();
        delete.setVisible(false);
        update.setVisible(false);
        save.setDisable(false);
        BookID.setEditable(true);
        tableView.getSelectionModel().clearSelection();
        searchTextField.clear();
    }
    @FXML
    private void deleteStudent(ActionEvent event) {
        PreparedStatement pre = null;
        PreparedStatement pre2 = null;
        PreparedStatement pre3 = null;
        PreparedStatement pre4 = null;
        PreparedStatement insert = null;
        Connection conn = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        ResultSet rs4 = null;
        String query = "DELETE FROM Book WHERE BookID = ?";
        String query2 = "SELECT * FROM IssueBook WHERE BookID = ?";
        String query3 = "SELECT Name FROM Book WHERE BookID = ?";
        String select = "SELECT * FROM Book WHERE BookId = ?";
        String insertQuery = "INSERT INTO Arhieve (BookID,Name,Author,Publisher,Edition,Quantity,RemainingBooks,Availability,Section) VALUES (?,?,?,?,?,?,?,?,?)";
        try {
            conn = DatabaseConnection.Connect();
            pre2 = conn.prepareStatement(query2);
            pre3 = conn.prepareStatement(query3);
            pre4 = conn.prepareStatement(select);
            insert = conn.prepareStatement(insertQuery);
            pre2.setString(1, BookID.getText());
            pre3.setString(1, BookID.getText());
            rs2 = pre2.executeQuery();
            if (rs2.next() ) {
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
                    pre4.setString(1, BookID.getText());
                    rs4 = pre4.executeQuery();
                    while (rs4.next()) {
                        insert.setString(1, rs4.getString(1));
                        insert.setString(2, rs4.getString(2));
                        insert.setString(3, rs4.getString(3));
                        insert.setString(4, rs4.getString(4));
                        insert.setString(5, rs4.getString(5));
                        insert.setString(6, rs4.getString(6));
                        insert.setString(7, rs4.getString(7));
                        insert.setString(8, rs4.getString(8));
                        insert.setString(9, rs4.getString(9));
                    }
                    insert.executeUpdate();
                    pre = conn.prepareStatement(query);
                    pre.setString(1, BookID.getText());
                    pre.executeUpdate();
                    loadData();
                    save.setDisable(false);
                    update.setVisible(false);
                    delete.setVisible(false);
                    BookID.setEditable(true);
                    Notification notification = new Notification("Information", "Student record successfully moved to archieved student list", 3);
                    clearFields();
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }

    private void loadArchievedRecord() {
        data.clear();
        Connection conn = null;
        PreparedStatement pre = null;
        ResultSet rs = null;
        String query = "SELECT * FROM Arhieve";
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query);
            rs = pre.executeQuery();
            while (rs.next()) {
                data.add(new Book(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getString(8), rs.getString(9)));
            }
            tableView.getItems().setAll( data);
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }

    private void unArchiveRecord() {
        String insert = "INSERT INTO Book (BookID,Name,Author,Publisher,Edition,Quantity,RemainingBooks,Availability,Section) VALUES (?,?,?,?,?,?,?,?,?)";
        String deleteQuery = "DELETE FROM Arhieve WHERE BookID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatement1 = null;
        Book student = (Book) tableView.getSelectionModel().getSelectedItem();
        if (student == null) {
            Notification notification = new Notification("Information", "No record selected", 3);
        }
        if (student != null) {
            try {
                connection = DatabaseConnection.Connect();
                preparedStatement = connection.prepareStatement(insert);
                preparedStatement1 = connection.prepareStatement(deleteQuery);
                preparedStatement.setString(1, student.getBookID());
                preparedStatement.setString(2, student.getBookName());
                preparedStatement.setString(3, student.getBookAuthor());
                preparedStatement.setString(4, student.getBookPublisher());
                preparedStatement.setInt(5, student.getBookEdition());
                preparedStatement.setInt(6, student.getBookQuantity());
                preparedStatement.setInt(7, student.getRemainingBooks());
                preparedStatement.setString(8, student.getAvailability());
                preparedStatement.setString(9, student.getBookSection());

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Вернуть к текущим товарам");
                alert.setContentText("Are you sure you want to unarchieve " + student.getBookName() + " record ?");
                alert.setHeaderText(null);
                Optional<ButtonType> optional = alert.showAndWait();
                if (optional.get() == ButtonType.OK) {
                    preparedStatement1.setString(1, student.getBookID());
                    preparedStatement.executeUpdate();
                    preparedStatement1.executeUpdate();
                    Notification notification = new Notification("Information", "Student record successfully moved to archieved student records", 3);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Clients.class.getName()).log(Level.SEVERE, null, ex);
            }
            loadArchievedRecord();
        }
    }

    private void loadData() {
        data.clear();
        PreparedStatement pre = null;
        Connection conn = null;
        ResultSet rs = null;
        String query = "SELECT * FROM Book order by BookID";
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query);
            rs = pre.executeQuery();
            while (rs.next()) {
                data.add(new Book(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getString(8), rs.getString(9)));
            }
            tableView.getItems().setAll( data);
        } catch (SQLException e) {
            System.err.println(e);
        }

    }
    private boolean checkIFIDExist() {
        PreparedStatement pre = null;
        Connection conn = null;
        ResultSet rs = null;
        String query = "SELECT * FROM Book WHERE BookID = ?";
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query);
            pre.setString(1, BookID.getText());
            rs = pre.executeQuery();
            if (rs.next()) {
                coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "ID validation", "Student id already exist");
                return false;
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
        return true;
    }
    @FXML
    private void fetchStudentWithKey(KeyEvent event) {
        if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
            fetchStudentFeesDetails(null);
        }
    }

    private Stage getStage() {
        return (Stage) BookID.getScene().getWindow();
    }

    @FXML
    private void requestMenu(ContextMenuEvent event) {
        if (comboBox.getValue().equals("Списанные товары")) {
            contextMenu.show(tableView.getScene().getWindow(), event.getSceneX() + 90, event.getSceneY() + 40);
        }
    }

    @FXML
    private void selectRecordsType(ActionEvent event) {
        if (comboBox.getValue().equals("Товары")) {
            selectMenu.setVisible(true);
            loadData();
        }
        if (comboBox.getValue().equals("Списанные товары")) {
            selectMenu.setVisible(false);
            loadArchievedRecord();
            cancel(new ActionEvent());
        }
    }

    private void initiliazeColumns() {
        check.setCellValueFactory(new PropertyValueFactory<>("check"));
        booKID.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        bookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        bookAuthor.setCellValueFactory(new PropertyValueFactory<>("bookAuthor"));
        bookPublisher.setCellValueFactory(new PropertyValueFactory<>("bookPublisher"));
        edition.setCellValueFactory(new PropertyValueFactory<>("bookEdition"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("bookQuantity"));
        remainingBooks.setCellValueFactory(new PropertyValueFactory<>("remainingBooks"));
        sectionCol.setCellValueFactory(new PropertyValueFactory<>("bookSection"));
        availability.setCellValueFactory(new PropertyValueFactory<>("availability"));
    }
    @FXML
    private void fetchStudentFeesDetails(MouseEvent event) {
        Connection conn = null;
        PreparedStatement pre = null;
        ResultSet rs = null;
        String query = "SELECT * FROM Book WHERE BookID = ?";
        Book stu =(Book) tableView.getSelectionModel().getSelectedItem();
        if (stu == null) {
        }
        if (stu != null) {
            if (comboBox.getValue().equals("Списанные товары")) {
                save.setDisable(false);
                update.setVisible(false);
                delete.setVisible(false);
            } else {
                try {
                    BookID.setEditable(false);
                    update.setVisible(true);
                    delete.setVisible(true);
                    save.setDisable(true);
                    conn = DatabaseConnection.Connect();
                    pre = conn.prepareStatement(query);
                    pre.setString(1, stu.getBookID());
                    rs = pre.executeQuery();
                    while (rs.next()) {
                        BookID.setText(rs.getString(1));
                        Name.setText(rs.getString(2));
                        Author.setText(rs.getString(3));
                        Publisher.setText(rs.getString(4));
                        Edition.setText(rs.getString(5));
                        Quantity.setText(rs.getString(6));
                        RemainingBooks.setText(rs.getString(7));
                        Availability.setText(rs.getString(8));
                        Section.setText(rs.getString(9));
                    }
                } catch (SQLException ex) {
                    System.err.println(ex);
                }
            }
        }
    }

}
