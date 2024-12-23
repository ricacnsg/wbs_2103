package queries;

import java.sql.*;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import connector.DBConnect;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            m.currentReading,
            ph.paymentDate
        FROM 
            client c
        JOIN 
            meter m ON c.clientID = m.clientID
        JOIN 
            address a ON c.addressID = a.addressID
        LEFT JOIN 
            (SELECT meterID, MAX(paymentDate) AS paymentDate
             FROM paymenthistory
             GROUP BY meterID) ph ON m.meterID = ph.meterID
    """;

    List<String[]> clientMeterData = new ArrayList<>();

    try (PreparedStatement stmt = connect.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            String[] row = new String[9];  // Adjusted the size of the row to match the new column count
            row[0] = String.valueOf(rs.getInt("clientID"));
            row[1] = rs.getString("clientName");
            row[2] = rs.getString("meterName");
            row[3] = String.valueOf(rs.getInt("meterID"));
            row[4] = rs.getString("meterType");
            row[5] = rs.getString("address");
            row[6] = String.valueOf(rs.getDouble("previousReading"));
            row[7] = String.valueOf(rs.getDouble("currentReading"));
            row[8] = rs.getTimestamp("paymentDate") != null 
                    ? rs.getTimestamp("paymentDate").toString() 
                    : "No Payment Recorded";  // Handle null paymentDate
            clientMeterData.add(row);
        }
    } catch (SQLException e) {
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
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Meter ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error updating meter reading: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }
}
   
   public int fetchClientIDByMeterID(String meterId) {
    String query = "SELECT clientID FROM meter WHERE meterID = ?";
    try (PreparedStatement stmt = connect.prepareStatement(query)) {
        stmt.setString(1, meterId);
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            return rs.getInt("clientID");
        } else {
            JOptionPane.showMessageDialog(null, "No meter found with the provided ID.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error fetching client ID: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return -1; 
}

     
public boolean saveBillToDatabase(int clientId, String meterId, double totalBill, double leakCharge, double overdueCharge, double meterUsed) {
    String billingPeriod = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    Timestamp lastUpdated = Timestamp.valueOf(LocalDateTime.now());
    
    double balance = totalBill + leakCharge + overdueCharge;

    String query = "INSERT INTO bill (clientID, meterID, billingPeriod, totalBill, leakCharge, overdueCharge, balance, lastUpdated, meterUsed) " +
                   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement stmt = connect.prepareStatement(query)) {
        stmt.setInt(1, clientId);
        stmt.setString(2, meterId);
        stmt.setString(3, billingPeriod);      
        stmt.setDouble(4, totalBill);          
        stmt.setDouble(5, leakCharge);         
        stmt.setDouble(6, overdueCharge);      
        stmt.setDouble(7, balance);            
        stmt.setTimestamp(8, lastUpdated);     
        stmt.setDouble(9, meterUsed);          

        int rowsAffected = stmt.executeUpdate();

        return rowsAffected > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error saving bill: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }
}




    public LocalDate getLatestPaymentDate(int meterID, int clientID) {
        String query = "SELECT MAX(paymentDate) AS latestPaymentDate FROM paymenthistory WHERE meterID = ? AND clientID = ?";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, meterID);
            stmt.setInt(2, clientID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Date date = rs.getDate("latestPaymentDate");
                    if (date != null) {
                        return date.toLocalDate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LocalDate fetchLastPaymentDate(int clientId) {
    String query = "SELECT MAX(paymentDate) AS lastPaymentDate FROM paymenthistory WHERE clientID = ?";
    
    try (PreparedStatement stmt = connect.prepareStatement(query)) {
        stmt.setInt(1, clientId);  // Bind the clientID to the query
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                Date lastPaymentDate = rs.getDate("lastPaymentDate");
                if (lastPaymentDate != null) {
                    return lastPaymentDate.toLocalDate();  // Convert SQL Date to LocalDate
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, 
            "Error fetching last payment date: " + e.getMessage(), 
            "Database Error", 
            JOptionPane.ERROR_MESSAGE);
    }

    return null;  // Return null if no payment date is found
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
