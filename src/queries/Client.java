/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package queries;

import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import connector.DBConnect;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;


public class Client {
    private Connection connect;
    private Component rootPane;

    public Client() {
        DBConnect dbconnect = new DBConnect();
        this.connect = dbconnect.getConnection();
    }

    public List<String[]> getAddresses() {
        List<String[]> addresses = new ArrayList<>();
        String query = "SELECT addressID, addressName FROM Address";

        try (PreparedStatement stmt = connect.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                addresses.add(new String[]{rs.getString("addressID"), rs.getString("addressName")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    public String getMeterType(int clientID) {
        String meterType = "";
        String query = "SELECT meterType FROM Client WHERE clientID = ?";

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, clientID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    meterType = rs.getString("meterType");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meterType;
    }
    
    public String getClientName(int clientID) {
        String query = "SELECT clientName FROM Client WHERE clientID = ?";
        String clientName = null;

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, clientID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    clientName = rs.getString("clientName"); 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }

        return clientName; 
    }
    
    public int getMeterID(int clientID) {
    int meterID = -1;
    String query = "SELECT meterID FROM Meter WHERE clientID = ? LIMIT 1"; // assuming one meter per client for residential

    try (PreparedStatement stmt = connect.prepareStatement(query)) {
        stmt.setInt(1, clientID);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                meterID = rs.getInt("meterID");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return meterID;
}



    public int insertClient(String clientName, String contactNumber, String password, int addressID, String meterType) {
        String query = "INSERT INTO Client (clientName, contactNumber, password, addressID, meterType) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connect.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, clientName);
            stmt.setString(2, contactNumber);
            stmt.setString(3, password);
            stmt.setInt(4, addressID);
            stmt.setString(5, meterType);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

   public Integer insertMeter(int clientID, String meterName, String meterType) {
    String query = "INSERT INTO Meter (clientID, meterName, meterType) VALUES (?, ?, ?)";
    try (PreparedStatement stmt = connect.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
        stmt.setInt(1, clientID);
        stmt.setString(2, meterName);
        stmt.setString(3, meterType);
        stmt.executeUpdate();

        try (ResultSet keys = stmt.getGeneratedKeys()) {
            if (keys.next()) {
                return keys.getInt(1);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
    }


    public void insertSubmeter(int primaryMeterID, String submeterLocation) {
        String query = "INSERT INTO Submeter (primaryMeterID, submeterLocation) VALUES (?, ?)";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, primaryMeterID);
            stmt.setString(2, submeterLocation);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String[]> getSubmeters(int primaryMeterID) {
        List<String[]> submeters = new ArrayList<>();
        String query = "SELECT submeterID, submeterLocation, readingType FROM Submeter WHERE primaryMeterID = ?";

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, primaryMeterID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    submeters.add(new String[]{
                        rs.getString("submeterID"),
                        rs.getString("submeterLocation"),
                        rs.getString("readingType")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return submeters;
    }

    public boolean loginClient(int clientID, String password) {
        String query = "SELECT clientID FROM Client WHERE clientID = ? AND password = ?";
        try (PreparedStatement statement = connect.prepareStatement(query)) {
            statement.setInt(1, clientID);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                UserState.verifiedID = clientID;
                UserState.isVerified = true;
                SharedData.clientID = clientID;

                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        UserState.verifiedID = -1;
        UserState.isVerified = false;
        return false;
    }
    
    public double getCurrentReading(int meterID) {
        double currentReading = 0.0;  // Default value if no reading found
        String query = "SELECT currentReading FROM Meter WHERE meterID = ?";

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, meterID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    currentReading = rs.getDouble("currentReading");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return currentReading;
    }

    
    public void startMeter(int meterID) {
        double currentReading = getCurrentReading(meterID);
        currentReading += 0.1;
        updateCurrentReading(meterID, currentReading);
    }

    public double[] getMeterReadings(int meterID) {
        String query = "SELECT previousReading, currentReading FROM Meter WHERE meterID = ?";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, meterID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new double[]{rs.getDouble("previousReading"), rs.getDouble("currentReading")};
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new double[]{0, 0};
    }

    public void updateCurrentReading(int meterID, double currentReading) {
        String query = "UPDATE Meter SET currentReading = ? WHERE meterID = ?";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setDouble(1, currentReading);
            stmt.setInt(2, meterID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
// for commercial metertype
    public void addMeter(int clientID, String meterName, String meterType) {
        String query = "INSERT INTO Meter (clientID, meterName, meterType) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, clientID);
            stmt.setString(2, meterName);
            stmt.setString(3, meterType);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<String> getMetersByClientID(int clientID) {
        String query = "SELECT meterName FROM Meter WHERE clientID = ?";
        List<String> meterNames = new ArrayList<>();

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, clientID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                meterNames.add(rs.getString("meterName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return meterNames;
    }

    public int getMeterIDByMeterName(int clientID, String meterName) {
        String query = "SELECT meterID FROM Meter WHERE clientID = ? AND meterName = ?";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, clientID);
            stmt.setString(2, meterName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("meterID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

//methods for bulk
    public double[] getSubmeterReadings(int meterID) {
        double[] readings = new double[2]; 
        
        String query = "SELECT previousReading, currentReading FROM Submeter WHERE submeterID = ?";

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, meterID); 

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    readings[0] = rs.getDouble("previousReading");
                    readings[1] = rs.getDouble("currentReading");
                } else {
                    System.out.println("No readings found for submeter associated with meterID " + meterID);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return readings;
    }

    public List<Integer> getSubmeterIDs(int mainMeterID) {
        List<Integer> submeterIDs = new ArrayList<>();
        String query = "SELECT submeterID FROM Submeter WHERE primaryMeterID = ?";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, mainMeterID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    submeterIDs.add(rs.getInt("submeterID"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return submeterIDs;
    }
    
    public String getSubmeterName(int submeterID) {
        String query = "SELECT submeterLocation FROM Submeter WHERE submeterID = ?";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, submeterID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("submeterName");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public int getSubmeterIDByName(int meterID, String submeterName) {
    String query = "SELECT submeterID FROM Submeter WHERE primaryMeterID = ? AND submeterLocation = ?";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, meterID);
            stmt.setString(2, submeterName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("submeterID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void addSubmeter(int meterID, String meterName) {
        String query = "INSERT INTO Submeter (primaryMeterID, submeterLocation) VALUES (?, ?)";

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, meterID);
            stmt.setString(2, meterName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<String> getSubmetersByMeterID(int meterID) {
        String query = "SELECT submeterLocation FROM Submeter WHERE primaryMeterID = ?";
        List<String> meterNames = new ArrayList<>();

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, meterID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                meterNames.add(rs.getString("submeterLocation"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return meterNames;
    }
    
    public void updateSubCurrentReading(int submeterID, double currentReading) {
        String query = "UPDATE Submeter SET currentReading = ? WHERE submeterID = ?";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setDouble(1, currentReading);
            stmt.setInt(2, submeterID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean updateMainMeterReading(int mainMeterID) {
        String sumQuery = "SELECT SUM(currentReading) AS totalSubmeterReading FROM Submeter WHERE primaryMeterID = ?";
        String updateQuery = "UPDATE Meter SET currentReading = ? WHERE meterID = ?";

        try (PreparedStatement sumStmt = connect.prepareStatement(sumQuery)) {
            sumStmt.setInt(1, mainMeterID);
            double totalSubmeterReading = 0;
            try (ResultSet rs = sumStmt.executeQuery()) {
                if (rs.next()) {
                    totalSubmeterReading = rs.getDouble("totalSubmeterReading");
                }
            }

            try (PreparedStatement updateStmt = connect.prepareStatement(updateQuery)) {
                updateStmt.setDouble(1, totalSubmeterReading);
                updateStmt.setInt(2, mainMeterID);
                int rowsUpdated = updateStmt.executeUpdate();
                return rowsUpdated > 0; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //for retrieving bill
        public String loadBillDetails(int clientId) {
        // SQL query to retrieve the bill details for the given clientID
        String query = "SELECT billingPeriod, totalBill, balance, charges, meterUsed, lastUpdated FROM bill WHERE clientID = ? ORDER BY lastUpdated DESC LIMIT 1";
        String billDetails = ""; // Initialize the bill details string

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, clientId); // Set the clientID parameter
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Retrieve data from the result set
                    String billingPeriod = rs.getString("billingPeriod");
                    double totalBill = rs.getDouble("totalBill");
                    double balance = rs.getDouble("balance");
                    double charges = rs.getDouble("charges");
                    double meterUsed = rs.getDouble("meterUsed");
                    Timestamp lastUpdated = rs.getTimestamp("lastUpdated");

                    // Format the data into a readable format
                    billDetails = formatBillDetails(billingPeriod, totalBill, balance, charges, meterUsed, lastUpdated);
                } else {
                    // Handle case when no record is found
                    billDetails = "No bill details found for the client.";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching bill details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return billDetails; // Return the formatted bill details
    }

            private String formatBillDetails(String billingPeriod, double totalBill, double balance, double charges, double meterUsed, Timestamp lastUpdated) {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            String lastUpdatedStr = (lastUpdated != null) ? lastUpdated.toString() : "N/A";

            return "<html>" +
                    "Billing Period: " + billingPeriod + "<br>" +
                    "Total Bill: $" + df.format(totalBill) + "<br>" +
                    "Balance: $" + df.format(balance) + "<br>" +
                    "Charges: $" + df.format(charges) + "<br>" +
                    "Meter Used: " + meterUsed + " units<br>" +
                    "Last Updated: " + lastUpdatedStr + "<br>" +
                    "</html>";
        }

    public void insertPaymentIntoHistory(int clientID, int meterID, double amount, String method, double meterUsed) {
        String query = "INSERT INTO paymenthistory (clientID, meterID, amountPaid, paymentMethod, paymentDate, meterUsed) VALUES (?, ?, ?, ?, NOW(), ?)"; // Adjust as necessary

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, clientID); // Set clientID
            stmt.setInt(2, meterID); // Set meterID
            stmt.setDouble(3, amount); // Set amount
            stmt.setString(4, method); // Set payment method
            stmt.setDouble(5, meterUsed); // Set meterUsed
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error processing payment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public boolean hasOutstandingBill(int clientID) {
        String query = "SELECT COUNT(*) FROM bill WHERE clientID = ? AND balance > 0"; // Adjust as necessary

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, clientID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Return true if there is at least one bill
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error checking outstanding bill: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false; // No outstanding bill found
    }

    public boolean isPaymentSufficient(int clientID, double paymentAmount) {
        String query = "SELECT totalBill FROM bill WHERE clientID = ? AND balance > 0 LIMIT 1"; // Adjust as necessary

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, clientID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double totalBill = rs.getDouble("totalBill");
                    return paymentAmount >= totalBill; // Check if payment covers the bill
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error checking payment sufficiency: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false; // No bill found or payment insufficient
    }


    public void removeBill(int clientID) {
        String query = "DELETE FROM bill WHERE clientID = ? AND balance > 0"; // Adjust as necessary

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, clientID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error removing bill: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public double getMeterUsed(int meterID) {
        String query = "SELECT meterUsed FROM bill WHERE meterID = ?"; // Adjust as necessary

        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, meterID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("meterUsed"); // Adjust according to your actual column name
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving meter used: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return 0; // Default value if not found
    }


}

