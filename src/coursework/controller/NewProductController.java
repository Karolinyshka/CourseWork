package coursework.controller;

import com.jfoenix.controls.JFXButton;
import coursework.dto.BookDto;
import coursework.utils.DBUtils;
import de.jensd.fx.glyphs.octicons.OctIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
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
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

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
        task.setOnSucceeded(event -> {
            spinner.setVisible(false);
            loadData();
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


    @FXML
    private void updateStudent(ActionEvent event) {
        BookDto bookDto = new BookDto(Name.getText().trim(), Author.getText().trim(),
                Publisher.getText().trim(), Edition.getText().trim(), Quantity.getText().trim(),
                RemainingBooks.getText().trim(), Availability.getText().trim(),
                Section.getText().trim(), BookID.getText().trim());
        DBUtils.updateStudent(bookDto);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Save changes ?");
        Optional<ButtonType> choice = alert.showAndWait();
        if (choice.get() == ButtonType.OK) {
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

    @FXML
    private void saveBook(ActionEvent event) {
        BookDto bookDto = new BookDto(Name.getText().trim(), Author.getText().trim(),
                Publisher.getText().trim(), Edition.getText().trim(), Quantity.getText().trim(),
                RemainingBooks.getText().trim(), Availability.getText().trim(),
                Section.getText().trim(), BookID.getText().trim());

        if (checkIFIDExist()) {
            DBUtils.saveBook(bookDto);
            loadData();
            Notification notification = new Notification("Message", "Student successfully added", 3);
            clearFields();
            save.setDisable(false);
        }

    }

    @FXML
    private void deleteStudentRecord(ActionEvent event) {
        Book librarian = (Book) tableView.getSelectionModel().getSelectedItem();

        if (librarian == null) {
            Notification notification = new Notification("Information", "Select librarian record to delete", 3);
        } else {
            DBUtils.deleteStudentRecord(librarian.getBookID());

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confimation");
            alert.setContentText("Are you sure you want to delete " + " ?");
            alert.setHeaderText(null);
            Optional<ButtonType> optional = alert.showAndWait();

            Notification notification = new Notification("Information", "Libraian record successfully deleted", 3);
            clearFields();
            Name.requestFocus();

        }
        loadData();
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
    private void deleteStudent() {
        DBUtils.deleteStudent(BookID.getText());
        loadData();
        save.setDisable(false);
        update.setVisible(false);
        delete.setVisible(false);
        BookID.setEditable(true);
        Notification notification = new Notification("Information", "Student record successfully moved to archieved student list", 3);
        clearFields();
    }

    private void loadArchievedRecord() {
        data.clear();
        List<BookDto> bookDtoList = DBUtils.loadArchievedRecord();
        bookDtoList.forEach(bookDto -> {
            System.out.println(bookDto);
            Book book = new Book(bookDto.getBookId(),bookDto.getName(),
                    bookDto.getAuthor(),bookDto.getPublisher(),
                    Integer.parseInt( bookDto.getEdition()),Integer.parseInt(bookDto.getQuantity()),Integer.parseInt(bookDto.getRemainingBooks()),
                    bookDto.getAvailability(),bookDto.getSection());
            data.add(book);
        });
        tableView.getItems().setAll(data);
    }

    private void unArchiveRecord() {
        Book student = (Book) tableView.getSelectionModel().getSelectedItem();

        BookDto bookDto = new BookDto(student.getBookName(), student.getBookAuthor(), student.getBookPublisher(), String.valueOf(student.getBookEdition()),
                String.valueOf(student.getBookQuantity()), String.valueOf(student.getRemainingBooks()), student.getAvailability(), student.getBookSection(), student.getBookID());
        if (student == null) {
            Notification notification = new Notification("Information", "No record selected", 3);
        }
        if (student != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Вернуть к текущим товарам");
            alert.setContentText("Are you sure you want to unarchieve " + student.getBookName() + " record ?");
            alert.setHeaderText(null);
            Optional<ButtonType> optional = alert.showAndWait();
            if (optional.get() == ButtonType.OK) {
                DBUtils.unArchiveRecord(bookDto);
                Notification notification = new Notification("Information", "Student record successfully moved to archieved student records", 3);
            }
        }
        loadArchievedRecord();

    }

    private void loadData() {
        data.clear();
        List<BookDto> bookDtos = DBUtils.loadData();
        bookDtos.forEach(bookDto -> {
            System.out.println(bookDto);
            Book book = new Book(bookDto.getBookId(),bookDto.getName(),
                    bookDto.getAuthor(),bookDto.getPublisher(),
                    Integer.parseInt( bookDto.getEdition()),Integer.parseInt(bookDto.getQuantity()),Integer.parseInt(bookDto.getRemainingBooks()),
                    bookDto.getAvailability(),bookDto.getSection());
            data.add(book);
        });
        tableView.getItems().setAll(data);

    }

    private boolean checkIFIDExist() {
        return DBUtils.checkIFIDExist(BookID.getText());
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

    //TODO:???
    @FXML
    private void fetchStudentFeesDetails(MouseEvent event) {
        Connection conn = null;
        PreparedStatement pre = null;
        ResultSet rs = null;
        String query = "SELECT * FROM Book WHERE BookID = ?";
        Book stu = (Book) tableView.getSelectionModel().getSelectedItem();
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
                    conn = DatabaseConnection.connect();
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
