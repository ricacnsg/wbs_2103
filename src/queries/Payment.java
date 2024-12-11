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

public class Payment {
    private Connection connect;
    private Component rootPane;

    public Payment() {
        DBConnect dbconnect = new DBConnect();
        this.connect = dbconnect.getConnection();
    }
    
    
    
    
    
    
    
    
    
    
    
}
