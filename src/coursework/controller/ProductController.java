package coursework.controller;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.octicons.OctIconView;
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
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import coursework.model.Book;
import coursework.model.DatabaseConnection;
import coursework.model.Notification;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductController implements Initializable {


    @FXML
    private BorderPane boarderpane;
    @FXML
    private TextField searchTextField;
    @FXML
    private JFXButton allStudents;
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
    private TableColumn<Book, String> avilability;
    @FXML
    private TableColumn<Book, String> sectionCol;
    @FXML
    private Label allBooks;
    @FXML
    private Label rBooks;
    @FXML
    private Label label1;
    @FXML
    private Label label2;
    @FXML
    private OctIconView searchIcon;
    ObservableList<Book> data = FXCollections.observableArrayList();
    ObservableList<Book> books = FXCollections.observableArrayList();
    @FXML
    private HBox controlBox;
    public static HBox box;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializaColumns();
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
                loadData();
                allBooksAndRemainingBooks();
            }
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }
    private boolean validateID() {
        Pattern p = Pattern.compile("[0-9- a-z $]+");
        Matcher m = p.matcher(booKID.getText());
        if (m.find() && m.group().equals(booKID.getText())) {
            return true;
        } else {
            coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.ERROR, "ISBN validation", "Please enter a valid book ISBN!");
            return false;
        }
    }

    private boolean checkIDBookIDAlreadyExist() {
        PreparedStatement pre = null;
        Connection conn = null;
        ResultSet rs = null;
        String query = "SELECT * FROM Book WHERE BookID = ? COLLATE NOCASE";
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query);
            pre.setString(1, booKID.getText().trim());
            rs = pre.executeQuery();
            if (rs.next()) {
                coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.ERROR, "ISBN validation", "Book ISBN already Exist!");
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



    private void initializaColumns() {
        booKID.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        bookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        bookAuthor.setCellValueFactory(new PropertyValueFactory<>("bookAuthor"));
        bookPublisher.setCellValueFactory(new PropertyValueFactory<>("bookPublisher"));
        edition.setCellValueFactory(new PropertyValueFactory<>("bookEdition"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("bookQuantity"));
        remainingBooks.setCellValueFactory(new PropertyValueFactory<>("remainingBooks"));
        sectionCol.setCellValueFactory(new PropertyValueFactory<>("bookSection"));
        avilability.setCellValueFactory(new PropertyValueFactory<>("availability"));
    }

    @FXML
    private void minimize(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void fullscreen(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (!stage.isFullScreen()) {
            stage.setFullScreen(true);
        }
    }

    @FXML
    private void unfullscreen(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (stage.isFullScreen()) {
            stage.setFullScreen(false);
        }
    }

    @FXML
    private void close(MouseEvent event) {
        Platform.exit();
        System.exit(0);
    }

    private boolean validateSearchTextField() {
        if (searchTextField.getText().isEmpty()) {
            coursework.model.Alert alert = new coursework.model.Alert(javafx.scene.control.Alert.AlertType.ERROR, "Field validation", "The field is empty");
            return false;
        } else {
            return true;
        }
    }

    @FXML
    private void loadBookDataentry(ActionEvent event) throws IOException {
        disableFields();
        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("/coursework/view/AddProduct.fxml"));
        boarderpane.setCenter(borderPane);
        controlBox.setVisible(false);
    }

    public void disableFields() {
        searchTextField.setVisible(false);
        allStudents.setVisible(false);
        allBooks.setVisible(false);
        rBooks.setVisible(false);
        label1.setVisible(false);
        label2.setVisible(false);
        searchIcon.setVisible(false);
    }

    private void loadData() {
        data.clear();
        PreparedStatement pre = null;
        Connection conn = null;
        ResultSet rs = null;
        String query = "SELECT * FROM Book";
        try {
            conn = DatabaseConnection.Connect();
            pre = conn.prepareStatement(query);
            rs = pre.executeQuery();
            while (rs.next()) {
                data.add(new Book(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getString(8), rs.getString(9)));
            }
            tableView.getItems().setAll(data);
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
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }

    private void allBooksAndRemainingBooks() {
        Connection conn = null;
        PreparedStatement pre1 = null;
        ResultSet rs1 = null;
        PreparedStatement pre2 = null;
        ResultSet rs2 = null;
        String query1 = "SELECT SUM(Quantity) FROM Book";
        String query2 = "SELECT SUM(RemainingBooks) FROM Book";
        try {
            conn = DatabaseConnection.Connect();
            pre1 = conn.prepareStatement(query1);
            rs1 = pre1.executeQuery();
            int Books = rs1.getInt(1);
            allBooks.setText("" + Books);
            pre2 = conn.prepareStatement(query2);
            rs2 = pre2.executeQuery();
            int rbooks = rs2.getInt(1);
            this.rBooks.setText("" + rbooks);
        } catch (SQLException ex) {
            System.err.println(ex);

        }
    }

    @FXML
    private void DeleteBook(ActionEvent event) {
        PreparedStatement pre = null;
        PreparedStatement pre2 = null;
        PreparedStatement pre3 = null;
        Connection conn = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String query = "DELETE FROM Book WHERE BookID = ?";
        String query2 = "SELECT * FROM IssueBook WHERE BookID = ?";
        String query3 = "SELECT * FROM ShortTermBook WHERE BookID = ?";
        Book selectedBook = (Book) tableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            Notification notification = new Notification("Information", "Select a book to delete", 5);
        } else {
            try {
                conn = DatabaseConnection.Connect();
                pre = conn.prepareStatement(query);
                pre2 = conn.prepareStatement(query2);
                pre3 = conn.prepareStatement(query3);
                pre2.setString(1, selectedBook.getBookID());
                pre3.setString(1, selectedBook.getBookID());
                rs = pre2.executeQuery();
                rs2 = pre3.executeQuery();
                if (rs.next() || rs2.next()) {
                    coursework.model.Alert alert = new coursework.model.Alert(Alert.AlertType.INFORMATION, "Information", "Other students are still holding this book");
                } else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmatiin");
                    alert.setHeaderText(null);
                    alert.setContentText("Are you sure you want to delete this book ?");
                    Optional<ButtonType> choice = alert.showAndWait();
                    if (choice.get() == ButtonType.OK) {
                        pre.setString(1, selectedBook.getBookID());
                        pre.executeUpdate();
                        allBooksAndRemainingBooks();
                        Notification notification = new Notification("Information", "Book successfully deleted", 3);
                        loadData();
                    }
                }
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
    }

    @FXML
    private void loadUpdateBook(ActionEvent event) throws IOException {
        String selectQuery = "SELECT * FROM Book WHERE BookID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/coursework/view/AddProduct.fxml"));
        Parent root = loader.load();
        Book selectedBook = (Book) tableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            Notification notification = new Notification("Information", "Select a book to update", 5);
        } else {
            disableFields();
            AddProductController booktDataEntryController = (AddProductController) loader.getController();
            booktDataEntryController.handleEditAction(selectedBook);
            booktDataEntryController.isinEditMode = true;
            booktDataEntryController.setEditavleBookIdFieldFalse();
            boarderpane.setCenter(root);
            controlBox.setVisible(false);
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


    private Stage getStage() {
        return (Stage) tableView.getScene().getWindow();
    }


            }



