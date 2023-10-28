package database_interaction;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

//import com.sun.deploy.util.UpdateCheck;
import org.sqlite.*;

import javax.xml.transform.Result;

//Hat für den Crawler keine Funktion. Wurde in den anfängen betrachtet, als noch nicht klar war wie umfänglich das Problem ist.
public class DatabaseDispatcher {
    public static void main(String args[]) {
        try { Class.forName("org.sqlite.JDBC").newInstance(); } catch(Exception e) {e.printStackTrace();};
        DatabaseDispatcher dp = new DatabaseDispatcher();
        try{
            dp.initConnection();
            dp.initTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private Connection connection = null;
    private String url = null;
    private Properties prop;

    public boolean isReset() {
        return reset;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
    }

    private boolean reset = false;

    public DatabaseDispatcher() {
        loadProperties();
        this.url = prop.getProperty("DATABASE_URL");
    }

    private void loadProperties() {
        try {
            String configFilePath = "src/config/config.properties";
            FileInputStream propsInput = new FileInputStream(configFilePath);
            Properties prop = new Properties();
            prop.load(propsInput);
            this.prop = prop;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initConnection() throws SQLException {
        connection = DriverManager.getConnection(prop.getProperty("DATABASE_URL"));
    }

    public void tryCloseConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertUrl(String url) {

    }

    public void initTable() throws SQLException {
        Statement statement = connection.createStatement();
        statement.setQueryTimeout(30);
        if(reset) {
            statement.executeUpdate("DROP TABLE IF EXISTS homepages");
            statement.executeUpdate("CREATE TABLE homepages(id INTEGER PRIMARY KEY AUTOINCREMENT, url STRING UNIQUE, isHomepage INTEGER CHECK(isHomepage == 1 || isHomepage == 0)");
        } else {
            System.out.println("Table already initiated");
        }
    }


}
