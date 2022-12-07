package coursework.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import jfxtras.scene.control.gauge.linear.SimpleMetroArcGauge;
import coursework.model.DatabaseConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GraphicController implements Initializable {


    @FXML
    private BorderPane graphBoarderpane;
    @FXML
    private SimpleMetroArcGauge allBooksGauge;
    @FXML
    private SimpleMetroArcGauge remainingBooksGauge;
    @FXML
    private SimpleMetroArcGauge issuedBooksGauge;
    @FXML
    private SimpleMetroArcGauge allStudentsGauge;
    @FXML
    private JFXButton allStudentsGaug;
    @FXML
    private SimpleMetroArcGauge bookHoldersGauge;
    @FXML
    private BarChart<?, ?> barchart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;





    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(1000);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        loadBookDetailsGraph();
                        loadStudentDetailsGraph();
                    }
                });
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }


    private void loadBookDetailsGraph() {
        String selectAllBooks = "SELECT SUM(Quantity) FROM Book";
        String remainingBooks = "SELECT SUM(RemainingBooks) FROM Book";
        String allIssuedBooks = "SELECT COUNT(*) FROM IssueBook";
        String allShortTermIssuedBooks = "SELECT COUNT(*) FROM ShortTermBook";
        Connection connection = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        PreparedStatement ps4 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        ResultSet rs4 = null;
        try {
            connection = DatabaseConnection.connect();
            ps1 = connection.prepareStatement(selectAllBooks);
            ps2 = connection.prepareStatement(remainingBooks);
            ps3 = connection.prepareStatement(allIssuedBooks);
            ps4 = connection.prepareStatement(allShortTermIssuedBooks);
            rs1 = ps1.executeQuery();
            rs2 = ps2.executeQuery();
            rs3 = ps3.executeQuery();
            rs4 = ps4.executeQuery();
            int allBooks = rs1.getInt(1);
            int remaining = rs2.getInt(1);
            int issued = rs3.getInt(1);
            int issuedShortTerm = rs4.getInt(1);
            int allIssued  = issued + issuedShortTerm;
            XYChart.Series series1 = new XYChart.Series();
            series1.setName("Статистика о товарах");
            series1.getData().add(new XYChart.Data<>("Все товары", allBooks));
            series1.getData().add(new XYChart.Data<>("Остаток товаров на складе", remaining));
            series1.getData().add(new XYChart.Data<>("Выдано товаров", allIssued));
            barchart.getStylesheets().add("coursework/css/color.css");
            barchart.getData().add(series1);

            for (int i = 0; i < series1.getData().size(); i++) {
                XYChart.Data data = (XYChart.Data) series1.getData().get(i);
                Tooltip.install(data.getNode(), new Tooltip(data.getXValue().toString() + " : " + data.getYValue().toString().replace(".0", "")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DashBoardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadStudentDetailsGraph() {
        String selectAllStudents = "SELECT COUNT(*) FROM Student";
        Connection connection = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        try {
            connection = DatabaseConnection.connect();
            ps1 = connection.prepareStatement(selectAllStudents);

            rs1 = ps1.executeQuery();

            int allStudents = rs1.getInt(1);
            XYChart.Series series2 = new XYChart.Series();
            series2.setName("Статистика о клиентах");
            series2.getData().add(new XYChart.Data<>("Клиенты", allStudents));
            barchart.getData().add(series2);
            for (int i = 0; i < series2.getData().size(); i++) {
                XYChart.Data data = (XYChart.Data) series2.getData().get(i);
                Tooltip.install(data.getNode(), new Tooltip(data.getXValue().toString() + " : " + data.getYValue().toString().replace(".0", "")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DashBoardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
