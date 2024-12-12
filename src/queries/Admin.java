package wbs_2103.src.queries;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import wbs_2103.src.connector.DBConnect;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;


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
      
      public List<String[]> fetchPaymentHistoryData() {
    String sql = """
            SELECT c.clientID, c.clientName, SUM(ph.amountPaid) AS totalAmountPaid
            FROM client c
            LEFT JOIN paymenthistory ph ON c.clientID = ph.clientID
            GROUP BY c.clientID, c.clientName
            """;
    List<String[]> paymentHistoryData = new ArrayList<>();

    try (PreparedStatement stmt = connect.prepareStatement(sql)) {
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String[] row = new String[3];  // 3 columns as per the SQL query
            row[0] = rs.getString("clientID");
            row[1] = rs.getString("clientName");
            row[2] = rs.getString("totalAmountPaid"); // Get the summed amount
            paymentHistoryData.add(row);
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error fetching payment history: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return paymentHistoryData;
}
      
      public int fetchTotalClients() {
    String sql = "SELECT COUNT(*) AS total_clients FROM client";
    try (PreparedStatement stmt = connect.prepareStatement(sql)) {
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("total_clients");
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error fetching client count: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return 0;
}
      
     

}
