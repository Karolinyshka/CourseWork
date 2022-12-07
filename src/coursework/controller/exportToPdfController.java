package coursework.controller;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.FileOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class exportToPdfController implements Initializable {
    PreparedStatement preparedStatement;
    Connection connection;
    boolean flag = false;
    @FXML
    private VBox vbox;
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private boolean exportToPDF(ActionEvent event) {
        try{
            Document my_pdf_report = new Document();
            PdfWriter.getInstance(my_pdf_report,new FileOutputStream("DatabaseData.pdf"));
            my_pdf_report.open();
            my_pdf_report.addTitle("Данные БД");
            Paragraph preface = new Paragraph();
            preface.add(new Paragraph("Data from Database is here!"));
            my_pdf_report.add(preface);

            PdfPTable user_table = new PdfPTable(2);
            PdfPTable results_table = new PdfPTable(3);

            PdfPCell table_cell;

            preparedStatement = connection.prepareStatement("SELECT * from User");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()){

                String result_id = rs.getString("ID");
                table_cell = new PdfPCell(new Phrase(result_id));
                results_table.addCell(table_cell);
                String summary = rs.getString("FirstName");
                table_cell = new PdfPCell(new Phrase(summary));
                results_table.addCell(table_cell);
                String userid = rs.getString("LastName");
                table_cell = new PdfPCell(new Phrase(userid));
                results_table.addCell(table_cell);
            }
            preparedStatement = connection.prepareStatement("SELECT BookID,Name from Book");
            rs = preparedStatement.executeQuery();
            while (rs.next()){

                String BookID = rs.getString("BookID");
                table_cell = new PdfPCell(new Phrase(BookID));
                user_table.addCell(table_cell);
                String Name = rs.getString("Name");
                table_cell = new PdfPCell(new Phrase(Name));
                user_table.addCell(table_cell);

            }
//            PrinterJob job = PrinterJob.createPrinterJob();
//            if(job != null) {
//                job.showPrintDialog(vbox.getScene().getWindow());
//                job.printPage(table_cell);
//                job.endJob();
//            }

            my_pdf_report.add(new Paragraph("Users table"));
            my_pdf_report.add(user_table);
            my_pdf_report.add(new Paragraph("Result table"));
            my_pdf_report.add(results_table);
            my_pdf_report.close();
            flag=true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return flag;
    }



    public static void animatePane(VBox box) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(200), box);
        transition.setFromX(30);
        transition.setToX(-30);
        transition.play();
    }



}
