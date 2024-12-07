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

    // Get addresses from the Address table
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

    // Get the meter type of a client from the Client table
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

    // Insert a new client
    public int insertClient(String clientName, String contactNumber, String password, int addressID, String meterType, Integer primaryMeterID) {
        String query = "INSERT INTO Client (clientName, contactNumber, password, addressID, meterType, primaryMeterID) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connect.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, clientName);
            stmt.setString(2, contactNumber);
            stmt.setString(3, password);
            stmt.setInt(4, addressID);
            stmt.setString(5, meterType);
            if (primaryMeterID != null) {
                stmt.setInt(6, primaryMeterID);
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
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

    // Insert a new meter
    public void insertMeter(int clientID, String meterName, String meterType, String location) {
        String query = "INSERT INTO Meter (clientID, meterName, meterType, location) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, clientID);
            stmt.setString(2, meterName);
            stmt.setString(3, meterType);
            stmt.setString(4, location);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insert a submeter for bulk clients
    public void insertSubmeter(int primaryMeterID, String submeterLocation, String readingType) {
        String query = "INSERT INTO Submeter (primaryMeterID, submeterLocation, readingType) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connect.prepareStatement(query)) {
            stmt.setInt(1, primaryMeterID);
            stmt.setString(2, submeterLocation);
            stmt.setString(3, readingType);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get submeters for a bulk client
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

    // Verify client login credentials
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
}

