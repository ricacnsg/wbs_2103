package wbs_2103.src.queries;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import wbs_2103.src.connector.DBConnect;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import wbs_2103.src.queries.Admin;


/**
 *
 * @author Nhel Hernadez
 */
public class Admin {
    private String username, password;
    private Connection connect;
    private Component rootPane;
    private String name, newUsername,newPassword;
     
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
      
      public List<String[]> fetchClientMeterData() {
    String sql = """
            SELECT c.clientID, c.clientName, m.meterName, m.meterID, c.meterType, m.previousReading, m.currentReading
            FROM client c
            JOIN meter m ON c.clientID = m.clientID
            """;
    List<String[]> clientMeterData = new ArrayList<>();

    try (PreparedStatement stmt = connect.prepareStatement(sql)) {
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String[] row = new String[7];  // 7 columns as per the SQL query
            row[0] = rs.getString("clientID");
            row[1] = rs.getString("clientName");
            row[2] = rs.getString("meterName");
            row[3] = rs.getString("meterID");  // Correctly fetching meterID
            row[4] = rs.getString("meterType");
            row[5] = rs.getString("previousReading");
            row[6] = rs.getString("currentReading");
            clientMeterData.add(row);
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error fetching data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return clientMeterData;
}

      public boolean signUp(int generateAdminId, String name, String newUsername,String newPassword) {
          
          if (isUsernameTaken(newUsername)){
             JOptionPane.showMessageDialog(rootPane, "Username already taken! Please choose a different username.", "Error", JOptionPane.ERROR_MESSAGE);
             return false;
          }
          String query = "INSERT INTO admin (name, username, adminpassword) VALUES (?, ?, ?)";
          try (PreparedStatement stmt = connect.prepareStatement(query)){
              stmt.setString(1, name);
              stmt.setString(2, newUsername);
              stmt.setString(3, newPassword);
              
              int rowsInserted = stmt.executeUpdate();
              
              if (rowsInserted > 0){
                  JOptionPane.showMessageDialog(rootPane, "Sign Up Successful! New admin account created.");
                  return true;
                  
              } else {
                  JOptionPane.showMessageDialog(rootPane, "Failed to create admin account.", "Error", JOptionPane.ERROR_MESSAGE);
              }
              
              
          } catch (Exception e) {
              e.printStackTrace();
              JOptionPane.showMessageDialog(rootPane, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          }
          return false;
      }
      
         public boolean isUsernameTaken(String newUsername) {
         String query = "SELECT COUNT(*) FROM admin WHERE username = ?";
         try (PreparedStatement stmt = connect.prepareStatement(query)) {
             stmt.setString(1, newUsername);
             ResultSet rs = stmt.executeQuery();

             if (rs.next() && rs.getInt(1) > 0) {
             return true; 
        }
         } catch (Exception e) {
             e.printStackTrace();
             JOptionPane.showMessageDialog(rootPane, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
         }
         return false; 
}

         private int generateAdminId(Connection connect){
            int adminId = 0;
            String query = "SELECT MAX(adminid) FROM admin";
            try (PreparedStatement stmt = connect.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {
                    adminId = rs.getInt(1) + 1;
                
            }
                
            } catch (SQLException e) {
                e.printStackTrace();
        JOptionPane.showMessageDialog(rootPane, "Error generating admin ID: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
           return adminId;
        }
      
}
