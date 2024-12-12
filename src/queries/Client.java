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
        return 0; 
    }
    
    public double getAmountDue(int clientID) {
    String query = "SELECT totalBill FROM bill WHERE clientID = ?";
    double amountDue = 0.0;

    try (PreparedStatement stmt = connect.prepareStatement(query)) {
        stmt.setInt(1, clientID);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            amountDue = rs.getDouble("totalBill"); // Get the amount due from the result set
        }
    } catch (SQLException e) {
        e.printStackTrace(); // Handle exceptions appropriately
    }
    return amountDue; // Return the amount due
}

// icheck kung nagamit
    public double getBillAmount(int meterID) {
    double billAmount = 0.0;
    String query = "SELECT totalBill FROM bill WHERE meterID = ?"; // Adjust the query according to your database schema

    try (PreparedStatement stmt = connect.prepareStatement(query)) {
        stmt.setInt(1, meterID);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                billAmount = rs.getDouble("billAmount");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error retrieving bill amount: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return billAmount;
}
    //for commercial billing
    public String loadComBillDetails(int clientId, int meterId) {
    String query = "SELECT billingPeriod, totalBill, balance, charges, meterUsed, lastUpdated " +
                   "FROM bill WHERE clientID = ? AND meterID = ? ORDER BY lastUpdated DESC LIMIT 1";
    String billDetails = ""; // Initialize the bill details string

    try (PreparedStatement stmt = connect.prepareStatement(query)) {
        stmt.setInt(1, clientId); 
        stmt.setInt(2, meterId); 
        
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                // Retrieve data from the result set
                String billingPeriod = rs.getString("billingPeriod");
                double totalBill = rs.getDouble("totalBill");
                double balance = rs.getDouble("balance");
                double charges = rs.getDouble("charges");
                double meterUsed = rs.getDouble("meterUsed");
                Timestamp lastUpdated = rs.getTimestamp("lastUpdated");

                billDetails = formatBillDetails(billingPeriod, totalBill, balance, charges, meterUsed, lastUpdated);
            } else {
                billDetails = "No bill details found for the client and meter.";
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error fetching bill details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    return billDetails; 
}
    
    public boolean hasOutstandingBill(int clientID, int meterID) {
    String query = "SELECT COUNT(*) FROM bill WHERE clientid = ? AND meterid = ?";
    
    try (PreparedStatement stmt = connect.prepareStatement(query)) {
        stmt.setInt(1, clientID);
        stmt.setInt(2, meterID);
        
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false; 
}

    public void removeBill(int clientID, int meterID) {
    String query = "DELETE FROM bill WHERE clientID = ? AND meterID = ?";
    
    try (PreparedStatement stmt = connect.prepareStatement(query)) {
        stmt.setInt(1, clientID);
        stmt.setInt(2, meterID);
        stmt.executeUpdate(); // Execute the deletion
    } catch (SQLException e) {
        e.printStackTrace(); // Handle exceptions appropriately
    }
}

public boolean isPaymentSufficient(int clientID, int meterID, double paymentAmount) {
    String query = "SELECT totalBill FROM bill WHERE clientID = ? AND meterID = ?";
    
    try (PreparedStatement stmt = connect.prepareStatement(query)) {
        stmt.setInt(1, clientID);
        stmt.setInt(2, meterID);
        
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            double amountDue = rs.getDouble("totalBill");
            return paymentAmount >= amountDue; // Check if payment meets or exceeds the amount due
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false; 
}

public double getAmountDue(int clientID, int meterID) {
    String query = "SELECT totalBill FROM bill WHERE clientID = ? AND meterID = ?";
    double amountDue = 0.0;

    try (PreparedStatement stmt = connect.prepareStatement(query)) {
        stmt.setInt(1, clientID);
        stmt.setInt(2, meterID);
        
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            amountDue = rs.getDouble("totalBill"); // Get the amount due from the result set
        }
    } catch (SQLException e) {
        e.printStackTrace(); // Handle exceptions appropriately
    }
    return amountDue; // Return the amount due
}

// payment for bulk
public String loadBulkBillDetails(int clientId, int meterId) {
    // Query to fetch readings from the meter table
    String mainQuery = "SELECT previousReading, currentReading FROM meter WHERE meterID = ?";

    // Query to fetch billing details for the main meter
    String mainMeterQuery = "SELECT billingPeriod, totalBill, balance, charges, meterUsed, lastUpdated, meterUsed " +
                            "FROM bill WHERE clientID = ? AND meterID = ? ORDER BY lastUpdated DESC LIMIT 1";

    // Query to fetch submeter details
    String submeterQuery = "SELECT submeterID, submeterLocation, previousReading, currentReading " +
                           "FROM submeter WHERE primaryMeterID = ?";

    StringBuilder billDetails = new StringBuilder();

    try (
        // Prepare statements
        PreparedStatement mainMeterStmt = connect.prepareStatement(mainMeterQuery);
        PreparedStatement meterStmt = connect.prepareStatement(mainQuery);
    ) {
        // Fetch main meter readings from the meter table
        meterStmt.setInt(1, meterId);
        double mainPreviousReading = 0;
        double mainCurrentReading = 0;
        double mainConsumption = 0;

        try (ResultSet meterRs = meterStmt.executeQuery()) {
            if (meterRs.next()) {
                mainPreviousReading = meterRs.getDouble("previousReading");
                mainCurrentReading = meterRs.getDouble("currentReading");
            } else {
                return "No readings found for the main meter.";
            }
        }

        // Fetch main meter billing details
        mainMeterStmt.setInt(1, clientId);
        mainMeterStmt.setInt(2, meterId);

        try (ResultSet mainRs = mainMeterStmt.executeQuery()) {
            if (mainRs.next()) {
                // Retrieve main meter details
                String billingPeriod = mainRs.getString("billingPeriod");
                double totalBill = mainRs.getDouble("totalBill");
                double balance = mainRs.getDouble("balance");
                double charges = mainRs.getDouble("charges");
                Timestamp lastUpdated = mainRs.getTimestamp("lastUpdated");
                mainConsumption = mainRs.getDouble("meterUsed");

                // Append main meter details
                billDetails.append("<html><body>")
                    .append("<h3>Main Meter Details</h3>")
                    .append("<p>Billing Period: ").append(billingPeriod).append("</p>")
                    //.append("<p>Previous Reading: ").append(mainPreviousReading).append("</p>")
                    //.append("<p>Current Reading: ").append(mainCurrentReading).append("</p>")
                    .append("<p>Consumption: ").append(mainConsumption).append(" units</p>")
                    .append("<p>Total Bill: ").append(totalBill).append("</p>")
                    .append("<p>Balance: ").append(balance).append("</p>")
                    .append("<p>Additional Charges: ").append(charges).append("</p>")
                    .append("<p>Last Updated: ").append(lastUpdated).append("</p>");
            } else {
                return "No main meter bill details found for the client and meter.";
            }
        }

        // Fetch submeter details
        try (PreparedStatement subStmt = connect.prepareStatement(submeterQuery)) {
            subStmt.setInt(1, meterId);

            try (ResultSet subRs = subStmt.executeQuery()) {
                billDetails.append("<h3>Submeter Details</h3>");

                boolean hasSubmeters = false;

                while (subRs.next()) {
                    hasSubmeters = true;
                    String submeterName = subRs.getString("submeterLocation");
                    double subPreviousReading = subRs.getDouble("previousReading");
                    double subCurrentReading = subRs.getDouble("currentReading");

                    double subConsumption = subCurrentReading - subPreviousReading;

                    billDetails.append("<p>")
                        .append("Submeter Name: ").append(submeterName).append("<br>")
                        .append("Previous Reading: ").append(subPreviousReading).append("<br>")
                        .append("Current Reading: ").append(subCurrentReading).append("<br>")
                        .append("Consumption: ").append(subConsumption).append(" units</p>");
                }

                if (!hasSubmeters) {
                    billDetails.append("<p>No submeters available.</p>");
                }

                billDetails.append("</body></html>");
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error fetching bill details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return "Error fetching bill details.";
    }

    return billDetails.toString();
}

public void processPayment(int clientId, int meterId, double paymentAmount, String paymentMethod) {
    String billQuery = "SELECT totalBill, charges, meterUsed FROM bill WHERE clientID = ? AND meterID = ? ORDER BY lastUpdated DESC LIMIT 1";
    String removeBillQuery = "DELETE FROM bill WHERE clientID = ? AND meterID = ?";
    String insertPaymentQuery = "INSERT INTO paymentHistory (clientID, meterID, amountPaid, paymentMethod, paymentDate, charges, meterUsed) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement billStmt = connect.prepareStatement(billQuery)) {
        billStmt.setInt(1, clientId);
        billStmt.setInt(2, meterId);

        try (ResultSet rs = billStmt.executeQuery()) {
            if (rs.next()) {
                double totalBill = rs.getDouble("totalBill");
                double charges = rs.getDouble("charges");
                double meterUsed = rs.getDouble("meterUsed");
                double change = 0;

                if (paymentMethod.equals("Cash") && paymentAmount < totalBill) {
                    throw new IllegalArgumentException("Payment amount is less than the bill total.");
                }

                if (paymentMethod.equals("Cash")) {
                    change = paymentAmount - totalBill;
                }

                // Insert payment into paymentHistory
                try (PreparedStatement insertStmt = connect.prepareStatement(insertPaymentQuery)) {
                    insertStmt.setInt(1, clientId);
                    insertStmt.setInt(2, meterId);
                    insertStmt.setDouble(3, paymentAmount);
                    insertStmt.setString(4, paymentMethod);
                    insertStmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                    insertStmt.setDouble(6, charges);
                    insertStmt.setDouble(7, meterUsed);
                    insertStmt.executeUpdate();
                }

                // Remove the bill from the bill table
                try (PreparedStatement removeStmt = connect.prepareStatement(removeBillQuery)) {
                    removeStmt.setInt(1, clientId);
                    removeStmt.setInt(2, meterId);
                    removeStmt.executeUpdate();
                }

                // Generate receipt
                StringBuilder receipt = new StringBuilder();
                receipt.append("\n----- Receipt -----\n")
                       .append("Client ID: ").append(clientId).append("\n")
                       .append("Meter ID: ").append(meterId).append("\n")
                       .append("Amount Paid: ").append(paymentAmount).append("\n")
                       .append("Payment Method: ").append(paymentMethod).append("\n")
                       .append("Payment Date: ").append(new Timestamp(System.currentTimeMillis())).append("\n")
                       .append("Charges: ").append(charges).append("\n")
                       .append("Meter Used: ").append(meterUsed).append("\n");

                if (paymentMethod.equals("Cash")) {
                    receipt.append("Change: ").append(change).append("\n");
                }

                receipt.append("--------------------\n");

                System.out.println(receipt.toString()); // Replace with GUI receipt display logic if needed
            } else {
                throw new SQLException("No bill found for the client and meter.");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Error processing payment: " + e.getMessage());
    }
}


//for displaying payment history of client
public List<Object[]> getPaymentHistory(int clientID) {
    List<Object[]> paymentHistoryList = new ArrayList<>();
    String query = "SELECT paymentID, amountPaid, paymentMethod, meterUsed, charges, paymentDate " +
                   "FROM paymenthistory WHERE clientID = ?";

    try (PreparedStatement stmt = connect.prepareStatement(query)) {
        // Set the clientID parameter in the query
        stmt.setInt(1, clientID);

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // Retrieve each column's value and store them as an Object array
                Object[] paymentData = new Object[6];
                paymentData[0] = rs.getInt("paymentID");
                paymentData[1] = rs.getDouble("amountPaid");
                paymentData[2] = rs.getString("paymentMethod");
                paymentData[3] = rs.getString("meterUsed");
                paymentData[4] = rs.getDouble("charges");
                paymentData[5] = rs.getDate("paymentDate");

                paymentHistoryList.add(paymentData);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return paymentHistoryList;
}




}

