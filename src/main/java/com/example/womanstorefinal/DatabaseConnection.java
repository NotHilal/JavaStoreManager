package com.example.womanstorefinal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {

    private Connection databaseLink;

    public Connection getConnection() {
        String dbName = "womanstoreapp";
        String dbUser = "root";
        String dbPassword = "root";
        String url = "jdbc:mysql://localhost:3306/" + dbName;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, dbUser, dbPassword);
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }

        return databaseLink;
    }

    public List<Product> getProducts(int accountId) {
        List<Product> products = new ArrayList<>();
        Connection connectDB = getConnection();

        String query = "SELECT name, purchasePrice, sellPrice, discountPrice, nbItem, size, type FROM products WHERE user_id = ?";

        try {
            PreparedStatement statement = connectDB.prepareStatement(query);
            statement.setInt(1, accountId);  // Use account_id to filter products
            ResultSet queryResult = statement.executeQuery();

            while (queryResult.next()) {
                String name = queryResult.getString("name");
                double purchasePrice = queryResult.getDouble("purchasePrice");
                double sellPrice = queryResult.getDouble("sellPrice");
                double discountPrice = queryResult.getDouble("discountPrice");
                int nbItems = queryResult.getInt("nbItem");
                double size = queryResult.getDouble("size");
                String type = queryResult.getString("type");

                Product product;

                switch (type.toLowerCase()) {
                    case "shoes":
                        product = new Shoes(name, purchasePrice, sellPrice, (int) size);  // Shoes with size
                        break;
                    case "clothes":
                        product = new Clothes(name, purchasePrice, sellPrice);
                        ((Clothes) product).setSize((int) size);  // Clothes with size
                        break;
                    default:
                        product = new Accessories(name, purchasePrice, sellPrice);
                        break;
                }

                product.setDiscountPrice(discountPrice);  // Set discount price
                product.setNbItems(nbItems);

                products.add(product);
            }

            connectDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }





    public void addProduct(Product product, int accountId) {
        Connection connectDB = getConnection();
        String insertQuery = "INSERT INTO products (name, purchasePrice, sellPrice, discountPrice, nbItem, size, type, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(insertQuery);
            preparedStatement.setString(1, product.getName());
            preparedStatement.setDouble(2, product.getPurchasePrice());
            preparedStatement.setDouble(3, product.getSellPrice());
            preparedStatement.setDouble(4, product.getDiscountPrice());
            preparedStatement.setInt(5, product.getNbItems());
            preparedStatement.setDouble(6, product.getSize());  // assuming all products have size
            preparedStatement.setString(7, product.getType());
            preparedStatement.setInt(8, accountId);  // Associate product with the user

            preparedStatement.executeUpdate();
            connectDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void updateProduct(Product product, int accountId) {
        Connection connectDB = getConnection();
        String updateQuery = "UPDATE products SET purchasePrice = ?, sellPrice = ?, discountPrice = ?, nbItem = ?, size = ?, type = ? WHERE name = ? AND user_id = ?";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(updateQuery);
            preparedStatement.setDouble(1, product.getPurchasePrice());
            preparedStatement.setDouble(2, product.getSellPrice());
            preparedStatement.setDouble(3, product.getDiscountPrice());
            preparedStatement.setInt(4, product.getNbItems());
            preparedStatement.setDouble(5, product.getSize());
            preparedStatement.setString(6, product.getType());  // Ensure this captures the updated value
            preparedStatement.setString(7, product.getName());
            preparedStatement.setInt(8, accountId);

            preparedStatement.executeUpdate();
            connectDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





    public void deleteProduct(Product product, int accountId) {
        Connection connectDB = getConnection();
        String deleteQuery = "DELETE FROM products WHERE name = ? AND user_id = ?";  // Use account_id

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(deleteQuery);
            preparedStatement.setString(1, product.getName());
            preparedStatement.setInt(2, accountId);  // Ensure the correct account is used

            preparedStatement.executeUpdate();
            connectDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
