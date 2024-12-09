package wbs_2103.src.queries;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import wbs_2103.src.connector.DBConnect;


/**
 *
 * @author Nhel Hernadez
 */
public class Admin {
    private String username, password;
    private Connection connect;
    private Component rootPane;
     
    public Admin(){
        DBConnect dbconnect = new DBConnect();
        this.connect = dbconnect.getConnection();
        
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
      public boolean login(String inputUsername, String inputPassword) {
        String sql = "SELECT * FROM admin WHERE username = ? AND adminpassword = ?";
        try (PreparedStatement stmt = connect.prepareStatement(sql)) {
            stmt.setString(1, inputUsername);
            stmt.setString(2, inputPassword);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                this.username = rs.getString("username");
                this.password = rs.getString("adminpassword");
                JOptionPane.showMessageDialog(rootPane, "Sign In Successful! Welcome, " + rs.getString("name") + "!");
                return true;
            } else {
                JOptionPane.showMessageDialog(rootPane, "Invalid Username or Password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(rootPane, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
}
