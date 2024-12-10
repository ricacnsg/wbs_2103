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
    return null; // Return null if insertion fails
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
        // Update the current reading in the database
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
            stmt.setInt(1, meterID); // Set the meterID parameter

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
        return null; // Return null if submeter name is not found
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
        return -1; // Return -1 if no submeter found with the name
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
        // Update the current reading in the database
        String query = "UPDATE Submeter SET currentReading = ? WHERE submeterID = ?";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setDouble(1, currentReading);
            stmt.setInt(2, submeterID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Method to sum up submeter readings and update the main meter's current reading
    public boolean updateMainMeterReading(int mainMeterID) {
        String sumQuery = "SELECT SUM(currentReading) AS totalSubmeterReading FROM Submeter WHERE primaryMeterID = ?";
        String updateQuery = "UPDATE Meter SET currentReading = ? WHERE meterID = ?";

        try (PreparedStatement sumStmt = connect.prepareStatement(sumQuery)) {
            // Step 1: Calculate total submeter readings
            sumStmt.setInt(1, mainMeterID);
            double totalSubmeterReading = 0;
            try (ResultSet rs = sumStmt.executeQuery()) {
                if (rs.next()) {
                    totalSubmeterReading = rs.getDouble("totalSubmeterReading");
                }
            }

            // Step 2: Update main meter's current reading with the total
            try (PreparedStatement updateStmt = connect.prepareStatement(updateQuery)) {
                updateStmt.setDouble(1, totalSubmeterReading);
                updateStmt.setInt(2, mainMeterID);
                int rowsUpdated = updateStmt.executeUpdate();
                return rowsUpdated > 0; // Return true if the update was successful
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if the process failed
    }




}

