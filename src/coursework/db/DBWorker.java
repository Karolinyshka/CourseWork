package coursework.db;

import javafx.scene.input.InputMethodTextRun;
import coursework.controller.LoginController;
import coursework.model.Alert;


import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBWorker  {
    private static DBWorker db = null;


    public static DBWorker getInstance() {
        if (db == null) {
            db = new DBWorker();
        }
        return db;
    }



}
