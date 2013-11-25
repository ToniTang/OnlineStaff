package us.rpvp.onlinestaff;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database extends JavaPlugin {

    public static boolean startConnection() {
        Connection c = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            c = DriverManager.getConnection("jdbc:mysql://", "root", "password");
        }
        catch (Exception ex) {
            System.out.println("Failed to connect to SQL");
            return false;
        }
        return true;
    }


}
