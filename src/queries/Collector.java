package queries;

import java.sql.*;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import connector.DBConnect;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Nhel Hernadez
 */
public class Collector {
    private String username, password;
    private Connection connect;
    private Component rootPane;
    
     public Collector(){
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
    
     public boolean login(String username, String password) {
        String query = "SELECT * FROM Collector WHERE CollectUser = ? AND CollectPass = ?";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Login successful
                JOptionPane.showMessageDialog(null, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                // Login failed
                JOptionPane.showMessageDialog(null, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
     
public List<String[]> fetchClientMeterData() {
    String sql = """
        SELECT 
            c.clientID, 
            c.clientName, 
            m.meterName, 
            m.meterID, 
            m.meterType, 
            a.addressName AS address, 
            m.previousReading, 
            m.currentReading
        FROM 
            client c
        JOIN 
            meter m ON c.clientID = m.clientID
        JOIN 
            address a ON c.addressID = a.addressID
    """;

    List<String[]> clientMeterData = new ArrayList<>();

    try (PreparedStatement stmt = connect.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            String[] row = new String[8];  // Adjusted the size of the row to match the columns
            row[0] = String.valueOf(rs.getInt("clientID"));
            row[1] = rs.getString("clientName");
            row[2] = rs.getString("meterName");
            row[3] = String.valueOf(rs.getInt("meterID"));
            row[4] = rs.getString("meterType");
            row[5] = rs.getString("address");
            row[6] = String.valueOf(rs.getDouble("previousReading"));
            row[7] = String.valueOf(rs.getDouble("currentReading"));
            clientMeterData.add(row);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error fetching data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    return clientMeterData;
}



     
     
    /* 
    public List<String[]> fetchClientMeterData() {
    String sql = """
        SELECT c.clientID, c.clientName, m.meterName, m.meterID, m.meterType, a.addressName 
        FROM client c 
        JOIN meter m ON c.clientID = m.clientID 
        JOIN address a ON c.clientID = c.clientID
    """;
    List<String[]> clientMeterData = new ArrayList<>();

    try (PreparedStatement stmt = connect.prepareStatement(sql)) {
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String[] row = new String[6];
            row[0] = String.valueOf(rs.getInt("clientID"));
            row[1] = rs.getString("clientName");
            row[2] = rs.getString("meterName");
            row[3] = String.valueOf(rs.getInt("meterID"));
            row[4] = rs.getString("meterType");
            row[5] = rs.getString("addressName");
            clientMeterData.add(row);  // Add row to list
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error fetching data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return clientMeterData;
}
    
   public Map<String, String> fetchMeterReadings(String meterId) {
    Map<String, String> meterReadings = new HashMap<>();
    String query = "SELECT previousReading, currentReading FROM meter WHERE meterID = ?";
    
    try (PreparedStatement stmt = connect.prepareStatement(query)) {
        stmt.setString(1, meterId);
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            meterReadings.put("previousReading", rs.getString("previousReading"));
            meterReadings.put("currentReading", rs.getString("currentReading"));
        } else {
            JOptionPane.showMessageDialog(null, "No meter found with the provided ID.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error fetching meter readings: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    return meterReadings;
}
   
   public boolean updateMeterReading(String meterId, double newReading) {
    String query = "UPDATE meter SET currentReading = ?, previousReading = currentReading WHERE meterID = ?";
    
    try (PreparedStatement stmt = connect.prepareStatement(query)) {
        stmt.setDouble(1, newReading);
        stmt.setString(2, meterId);
        
        int rowsAffected = stmt.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(null, "Meter reading updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Meter ID not found. No update performed.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error updating meter reading: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }
}

*/
     /*
      public void fetchClientData(ClientRead table) {
        String query = "SELECT c.clientID, c.clientName, m.meterName, m.meterID, m.meterType, a.addressName " +
                       "FROM client c " +
                       "JOIN meter m ON c.clientID = m.clientID " +
                       "JOIN address a ON c.clientID = a.clientID";

        try (PreparedStatement stmt = connect.prepareStatement(query); 
             ResultSet rs = stmt.executeQuery()) {
            
            // Set up the table model to hold the fetched data
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);  // Clear existing rows

            // Loop through the result set and populate the table
            while (rs.next()) {
                Object[] row = new Object[6];
                row[0] = rs.getInt("clientID");
                row[1] = rs.getString("clientName");
                row[2] = rs.getString("meterName");
                row[3] = rs.getInt("meterID");
                row[4] = rs.getString("meterType");
                row[5] = rs.getString("addressName");

                model.addRow(row);  // Add row to table
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
*/
     
   
}
