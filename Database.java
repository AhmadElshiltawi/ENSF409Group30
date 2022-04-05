package edu.ucalgary.ensf409;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Database {

    private final String DBURL;
    private final String USERNAME;
    private final String PASSWORD;
    private int itemLength;

    private Connection dbConnect;
    private ResultSet results;

    public Database(String url, String user, String pw) {
        this.DBURL = url;
        this.USERNAME = user;
        this.PASSWORD = pw;
    }

    public void initializeConnection() {
        try {
            dbConnect = DriverManager.getConnection(this.DBURL, this.USERNAME, this.PASSWORD);
            updateItemLength();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    String getDburl() {
        return this.DBURL;
    }

    String getUsername() {
        return this.USERNAME;
    }

    String getPassword() {
        return this.PASSWORD;
    }

    public Map<String, String> selectClientNeeds(int clientID) {
        Map<String, String> map = new HashMap<String, String>();
        String ID = Integer.toString(clientID);

        try {
            Statement myStmt = dbConnect.createStatement();
            results = myStmt.executeQuery("SELECT * FROM DAILY_CLIENT_NEEDS WHERE (ClientID =" + ID + ")");
            if (!results.next()) throw new IllegalArgumentException("Invalid client ID");
            else {
                String calories = results.getString("Calories");
                String wholeGrains = results.getString("WholeGrains");
                String protein = results.getString("Protein");
                String fruitsVeggies = results.getString("FruitVeggies");
                String other = results.getString("Other");

                map.put("Calories", calories);
                map.put("WholeGrains", wholeGrains);
                map.put("Protein", protein);
                map.put("FruitVeggies", fruitsVeggies);
                map.put("Other", other);
                map.put("ClientID", ID);
            }

            myStmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> selectFoodItem(int foodID) {
        Map<String, String> map = new HashMap<String, String>();
        String ID = Integer.toString(foodID);

        try {
            Statement myStmt = dbConnect.createStatement();
            results = myStmt.executeQuery("SELECT * FROM AVAILABLE_FOOD WHERE (ItemID =" + ID + ")");
            if (!results.next()) throw new IllegalArgumentException(ID + " is an invalid food ID");
            else {
                String grainContent = results.getString("GrainContent");
                String fvContent = results.getString("FVContent");
                String proContent = results.getString("ProContent");
                String other = results.getString("Other");
                String calories = results.getString("Calories");
                String name = results.getString("Name");

                map.put("GrainContent", grainContent);
                map.put("FVContent", fvContent);
                map.put("ProContent", proContent);
                map.put("Other", other);
                map.put("Calories", calories);
                map.put("ItemID", ID);
                map.put("Name", name);
            }
            myStmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public void updateItemLength() {
        int length = 0;
        try {
            String query = "SELECT * FROM AVAILABLE_FOOD";

            Statement myStmt = dbConnect.createStatement();
            results = myStmt.executeQuery(query);

            while (results.next()) {
                length++;
            }

            myStmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        this.itemLength = length;
    }

    public int getItemLength() {
        return this.itemLength;
    }

    public void deleteFoodItem(int foodID) {
        try {
            String query = "DELETE FROM AVAILABLE_FOOD WHERE ItemID = ?";

            PreparedStatement preparedStmt = dbConnect.prepareStatement(query);
            preparedStmt.setInt(1, foodID);
            preparedStmt.execute();

            preparedStmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        try {
            results.close();
            dbConnect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
