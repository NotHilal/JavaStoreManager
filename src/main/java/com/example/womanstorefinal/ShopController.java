package com.example.womanstorefinal;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ShopController {

    @FXML
    private VBox productContainer;

    @FXML
    private Label productNameLabel;
    @FXML
    private Label productPriceLabel;
    @FXML
    private Label productItemsLabel;
    @FXML
    private Label productSizeLabel;
    @FXML
    private Label ErrorMsgLbl; // Error message label

    @FXML
    private ImageView productImageView;

    @FXML
    private ComboBox<String> categoryFilter;

    @FXML
    private CheckBox discountCheckBox; // Checkbox for discount

    @FXML
    private Button BuyStockBtn; // Button for buying stock
    @FXML
    private Button SellBtn; // Button for selling stock

    private List<Product> products;

    private DatabaseConnection dbConnection = new DatabaseConnection();

    private Product selectedProduct; // Store the selected product
    private double capital; // Store the capital value
    private double totalIncome; // Store total income
    private double totalCost; // Store total cost
    private int accountId;  // Store logged-in user's account_id


    public void initialize(int accountId) {
        // Fetch the list of products for the user
        products = dbConnection.getProducts(accountId);  // Fetch products for the user
        displayProducts(products);  // Display products

        // Fetch capital, income, and cost details
        fetchGeneralInfo(accountId);

        // Set event listeners for BuyStockBtn and SellBtn
        BuyStockBtn.setOnAction(e -> buyStock());
        SellBtn.setOnAction(e -> sellProduct());
        // Initially hide the Buy and Sell buttons until a product is selected
        BuyStockBtn.setVisible(false);
        SellBtn.setVisible(false);


        discountCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (selectedProduct != null) { // Ensure a product is selected
                // Print debugging information
                System.out.println("Checkbox toggled, Discount applied: " + newValue);
                System.out.println("Discount Price: " + selectedProduct.getDiscountPrice());
                System.out.println("Sell Price: " + selectedProduct.getSellPrice());

                if (newValue) { // If checkbox is checked, display the discount price
                    productPriceLabel.setText("Price: €" + selectedProduct.getDiscountPrice());
                } else { // If checkbox is unchecked, display the regular sell price
                    productPriceLabel.setText("Price: €" + selectedProduct.getSellPrice());
                }

                // Print the updated price label
                System.out.println("Updated price label to: " + productPriceLabel.getText());
            }
        });




    }



    public void setAccountId(int accountId) {
        this.accountId = accountId;
        loadUserProducts();  // Load products specific to this accountId after setting it
    }

    private void loadUserProducts() {
        products = dbConnection.getProducts(accountId);  // Fetch products for this user
        displayProducts(products);  // Display the products
    }



    @FXML
    private void quitApp() {
        Platform.exit();
    }

    private int userId;  // To store the logged-in user's ID

    public void setUserId(int userId) {
        this.userId = userId;
    }


    private void fetchGeneralInfo(int accountId) {
        Connection connectDB = dbConnection.getConnection();
        String query = "SELECT capital, income, cost FROM user_account WHERE account_id = ?";

        try {
            PreparedStatement stmt = connectDB.prepareStatement(query);
            stmt.setInt(1, accountId);  // Use the logged-in user's account_id
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                capital = resultSet.getDouble("capital");
                totalIncome = resultSet.getDouble("income");
                totalCost = resultSet.getDouble("cost");
            }
            connectDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateGeneralInfo() {
        Connection connectDB = dbConnection.getConnection();
        String updateQuery = "UPDATE user_account SET capital = ?, income = ?, cost = ? WHERE account_id = ?";

        try {
            PreparedStatement stmt = connectDB.prepareStatement(updateQuery);
            System.out.println("Updating capital: " + capital);
            System.out.println("Updating income: " + totalIncome);
            System.out.println("Updating cost: " + totalCost);
            stmt.setDouble(1, capital);  // Update capital value
            stmt.setDouble(2, totalIncome);  // Update total income
            stmt.setDouble(3, totalCost);  // Update total cost
            stmt.setInt(4, accountId);  // Update for the logged-in user

            stmt.executeUpdate();
            connectDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void buyStock() {
        if (selectedProduct == null) {
            setErrorMessage("Please select a product to buy stock.", true);
            return;
        }

        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Buy Stock");
        dialog.setHeaderText("Enter the quantity you want to buy:");
        dialog.setContentText("Quantity (Price per unit: €" + selectedProduct.getPurchasePrice() + "):");

        dialog.showAndWait().ifPresent(quantityStr -> {
            try {
                int quantity = Integer.parseInt(quantityStr);

                if (quantity <= 0) {
                    setErrorMessage("Please enter a valid quantity to buy.", true);
                    return;
                }

                double totalCostForPurchase = selectedProduct.getPurchasePrice() * quantity;

                if (capital < totalCostForPurchase) {
                    setErrorMessage("You do not have enough capital to buy this product.", true);
                    return;
                }

                capital -= totalCostForPurchase;
                totalCost += totalCostForPurchase;
                selectedProduct.setNbItems(selectedProduct.getNbItems() + quantity);
                productItemsLabel.setText("Items: " + selectedProduct.getNbItems());

                updateGeneralInfo();
                dbConnection.updateProduct(selectedProduct, accountId);

                setErrorMessage("You successfully bought more stock!", false);

                // Clear the selected product after buying
                clearSelectedProduct();
            } catch (NumberFormatException e) {
                setErrorMessage("Please enter a valid number for the quantity.", true);
            }
        });
    }








    private void sellProduct() {
        if (selectedProduct == null) {
            setErrorMessage("Please select a product to sell.", true);
            return;
        }

        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Sell Product");
        dialog.setHeaderText("Enter the quantity you want to sell:");
        dialog.setContentText("Quantity:");

        dialog.showAndWait().ifPresent(quantityStr -> {
            try {
                int quantity = Integer.parseInt(quantityStr);

                if (quantity <= 0 || quantity > selectedProduct.getNbItems()) {
                    setErrorMessage("Please enter a valid quantity to sell.", true);
                    return;
                }

                double price = discountCheckBox.isSelected() ? selectedProduct.getDiscountPrice() : selectedProduct.getSellPrice();
                double totalRevenue = price * quantity;

                capital += totalRevenue;
                totalIncome += totalRevenue;
                selectedProduct.setNbItems(selectedProduct.getNbItems() - quantity);
                productItemsLabel.setText("Items: " + selectedProduct.getNbItems());

                updateGeneralInfo();
                dbConnection.updateProduct(selectedProduct, accountId);

                setErrorMessage("You successfully sold the items!", false);

                // Clear the selected product after selling
                clearSelectedProduct();
            } catch (NumberFormatException e) {
                setErrorMessage("Please enter a valid number for the quantity.", true);
            }
        });
    }









    // Set message in the label with color
    private void setErrorMessage(String message, boolean isError) {
        if (isError) {
            ErrorMsgLbl.setStyle("-fx-text-fill: red;");
        } else {
            ErrorMsgLbl.setStyle("-fx-text-fill: green;");
        }
        ErrorMsgLbl.setText(message);

        // Clear the message after 3 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(1.8));
        pause.setOnFinished(event -> ErrorMsgLbl.setText(""));
        pause.play();
    }
    // Other methods remain unchanged...
    public void displayProducts(List<Product> productList) {
        productContainer.getChildren().clear();
        productContainer.setPadding(new Insets(10, 0, 0, 0));
        productContainer.setAlignment(Pos.TOP_CENTER);
        productContainer.setSpacing(10);

        List<String> productNames = productList.stream()
                .map(Product::getName)
                .collect(Collectors.toList());

        for (int i = 0; i < productNames.size(); i += 2) {
            HBox row = new HBox(20);
            row.setAlignment(Pos.CENTER);

            String productName1 = productNames.get(i);
            Button product1Button = new Button(productName1);
            product1Button.setPrefWidth(130);
            product1Button.setPrefHeight(80);
            product1Button.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
            product1Button.setOnAction(e -> handleProductClick(productName1));
            row.getChildren().add(product1Button);

            if (i + 1 < productNames.size()) {
                String productName2 = productNames.get(i + 1);
                Button product2Button = new Button(productName2);
                product2Button.setPrefWidth(130);
                product2Button.setPrefHeight(80);
                product2Button.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
                product2Button.setOnAction(e -> handleProductClick(productName2));
                row.getChildren().add(product2Button);
            }

            productContainer.getChildren().add(row);
        }
    }

    private void handleProductClick(String productName) {
        selectedProduct = getProductByName(productName);  // Find the selected product by its name

        if (selectedProduct != null) {
            productNameLabel.setText(selectedProduct.getName());
            productItemsLabel.setText("Items: " + selectedProduct.getNbItems());

            // Print debugging information
            System.out.println("Selected Product: " + selectedProduct.getName());
            System.out.println("Discount Price: " + selectedProduct.getDiscountPrice());
            System.out.println("Sell Price: " + selectedProduct.getSellPrice());

            // Show Buy and Sell buttons when a product is selected
            BuyStockBtn.setVisible(true);
            SellBtn.setVisible(true);

            // Update price label based on the discount checkbox state
            if (discountCheckBox.isSelected()) {
                productPriceLabel.setText("Price: €" + selectedProduct.getDiscountPrice());
            } else {
                productPriceLabel.setText("Price: €" + selectedProduct.getSellPrice());
            }

            // Display size information based on the product type
            if (selectedProduct instanceof Shoes) {
                productSizeLabel.setText("Shoe Size: " + ((Shoes) selectedProduct).getSize());
            } else if (selectedProduct instanceof Clothes) {
                productSizeLabel.setText("Size: " + ((Clothes) selectedProduct).getSize());
            } else {
                productSizeLabel.setText("Size: Ø");  // For accessories or items without a size
            }

            // Load the product image (if available)
            loadProductImage(productName);
        }
    }








    private void loadProductImage(String productName) {
        String imagePath = "Images/" + productName + ".jpg";
        File imageFile = new File(imagePath);
        String defaultImagePath = "Images/NoImg.png";
        File defaultImageFile = new File(defaultImagePath);

        if (imageFile.exists()) {
            Image productImage = new Image(imageFile.toURI().toString());
            productImageView.setImage(productImage);
        } else {
            if (defaultImageFile.exists()) {
                Image defaultImage = new Image(defaultImageFile.toURI().toString());
                productImageView.setImage(defaultImage);
            } else {
                System.out.println("Default image not found: " + defaultImagePath);
                productImageView.setImage(null);
            }
        }
    }

    private Product getProductByName(String productName) {
        return products.stream()
                .filter(product -> product.getName().equals(productName))
                .findFirst()
                .orElse(null);
    }


    @FXML
    private void modifyProduct() {
        Product selectedProduct = getSelectedProduct();
        if (selectedProduct != null) {
            Stage modifyStage = new Stage();
            VBox modifyBox = new VBox(10);

            // Fields for modifying product properties
            TextField nameField = new TextField(selectedProduct.getName());
            TextField sizeField = new TextField(String.valueOf(selectedProduct.getSize()));
            ComboBox<String> typeField = new ComboBox<>();
            typeField.getItems().addAll("Shoes", "Clothes", "Accessories");
            typeField.setValue(selectedProduct.getType());

            TextField sellPriceField = new TextField(String.valueOf(selectedProduct.getSellPrice()));
            TextField buyPriceField = new TextField(String.valueOf(selectedProduct.getPurchasePrice()));
            TextField discountPriceField = new TextField(String.valueOf(selectedProduct.getDiscountPrice()));
            TextField quantityField = new TextField(String.valueOf(selectedProduct.getNbItems()));

            Button chooseImageButton = new Button("Select Image");
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));

            chooseImageButton.setOnAction(e -> {
                File selectedImage = fileChooser.showOpenDialog(modifyStage);
                if (selectedImage != null) {
                    // Handle image selection logic here
                }
            });

            // Save Changes Button
            Button modifyButton = new Button("Save Changes");
            modifyButton.setOnAction(e -> {
                // Remove the old product from the list and database
                products.remove(selectedProduct);
                dbConnection.deleteProduct(selectedProduct, accountId);

                // Create a new product based on the selected type
                Product newProduct;
                String selectedType = typeField.getValue();
                switch (selectedType.toLowerCase()) {
                    case "shoes":
                        newProduct = new Shoes(nameField.getText(), Double.parseDouble(buyPriceField.getText()), Double.parseDouble(sellPriceField.getText()), Integer.parseInt(sizeField.getText()));
                        break;
                    case "clothes":
                        newProduct = new Clothes(nameField.getText(), Double.parseDouble(buyPriceField.getText()), Double.parseDouble(sellPriceField.getText()));
                        ((Clothes) newProduct).setSize(Integer.parseInt(sizeField.getText()));
                        break;
                    case "accessories":
                        newProduct = new Accessories(nameField.getText(), Double.parseDouble(buyPriceField.getText()), Double.parseDouble(sellPriceField.getText()));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown product type: " + selectedType);
                }

                // Set additional properties
                newProduct.setDiscountPrice(Double.parseDouble(discountPriceField.getText()));
                newProduct.setNbItems(Integer.parseInt(quantityField.getText()));

                // Add the new product to the list and database
                products.add(newProduct);
                dbConnection.addProduct(newProduct, accountId);

                // Refresh the product list in the UI
                displayProducts(products);
                modifyStage.close();

                // Clear the selected product after modifying
                clearSelectedProduct();
            });

            modifyBox.getChildren().addAll(
                    new Label("Name"), nameField,
                    new Label("Size"), sizeField,
                    new Label("Type"), typeField,
                    new Label("Selling Price"), sellPriceField,
                    new Label("Buying Price"), buyPriceField,
                    new Label("Discount Price"), discountPriceField,
                    new Label("Quantity"), quantityField,
                    chooseImageButton,
                    modifyButton
            );

            Scene modifyScene = new Scene(modifyBox, 550, 550);
            modifyStage.setScene(modifyScene);
            modifyStage.show();
        }
    }



    @FXML
    private void addProduct() {
        Stage addStage = new Stage();
        VBox addBox = new VBox(10);

        TextField nameField = new TextField();
        TextField sizeField = new TextField();
        ComboBox<String> typeField = new ComboBox<>();
        typeField.getItems().addAll("Shoes", "Clothes", "Accessories");
        TextField sellPriceField = new TextField();
        TextField buyPriceField = new TextField();
        TextField discountPriceField = new TextField();
        TextField quantityField = new TextField();

        Button chooseImageButton = new Button("Select Image");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));

        chooseImageButton.setOnAction(e -> {
            File selectedImage = fileChooser.showOpenDialog(addStage);
            if (selectedImage != null) {
                // Handle image selection logic
            }
        });

        Button addButton = new Button("Add Product");
        addButton.setOnAction(e -> {
            // Check if any field is empty
            if (nameField.getText().isEmpty() ||
                    sizeField.getText().isEmpty() ||
                    typeField.getValue() == null ||
                    sellPriceField.getText().isEmpty() ||
                    buyPriceField.getText().isEmpty() ||
                    discountPriceField.getText().isEmpty() ||
                    quantityField.getText().isEmpty()) {

                // Display an error alert
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Missing Information");
                errorAlert.setContentText("Please fill out all fields before adding a product.");
                errorAlert.showAndWait();
                return;  // Stop the process if validation fails
            }

            try {
                Product newProduct;
                switch (typeField.getValue().toLowerCase()) {
                    case "shoes":
                        newProduct = new Shoes(nameField.getText(), Double.parseDouble(buyPriceField.getText()), Double.parseDouble(sellPriceField.getText()), Integer.parseInt(sizeField.getText()));
                        break;
                    case "clothes":
                        newProduct = new Clothes(nameField.getText(), Double.parseDouble(buyPriceField.getText()), Double.parseDouble(sellPriceField.getText()));
                        ((Clothes) newProduct).setSize(Integer.parseInt(sizeField.getText()));  // Set the size for Clothes
                        break;
                    default:
                        newProduct = new Accessories(nameField.getText(), Double.parseDouble(buyPriceField.getText()), Double.parseDouble(sellPriceField.getText()));
                        break;
                }

                // Set other properties
                newProduct.setDiscountPrice(Double.parseDouble(discountPriceField.getText()));
                newProduct.setNbItems(Integer.parseInt(quantityField.getText()));

                // Add product to the list and database
                products.add(newProduct);
                dbConnection.addProduct(newProduct, accountId);
                displayProducts(products);
                addStage.close();

                // Clear the selected product after adding
                clearSelectedProduct();
            } catch (NumberFormatException ex) {
                // Display an error alert for invalid number inputs
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Invalid Input");
                errorAlert.setContentText("Please ensure that price, size, and quantity fields contain valid numbers.");
                errorAlert.showAndWait();
            }
        });

        addBox.getChildren().addAll(
                new Label("Name"), nameField,
                new Label("Size"), sizeField,
                new Label("Type"), typeField,
                new Label("Selling Price"), sellPriceField,
                new Label("Buying Price"), buyPriceField,
                new Label("Discount Price"), discountPriceField,
                new Label("Quantity"), quantityField,
                chooseImageButton,
                addButton
        );

        Scene addScene = new Scene(addBox, 550, 550);
        addStage.setScene(addScene);
        addStage.show();
    }







    @FXML
    private void deleteProduct() {
        Product selectedProduct = getSelectedProduct();
        if (selectedProduct != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Delete Confirmation");
            confirmationAlert.setHeaderText("Are you sure you want to delete this product?");
            confirmationAlert.setContentText("Product: " + selectedProduct.getName());

            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    products.remove(selectedProduct);
                    dbConnection.deleteProduct(selectedProduct, accountId);
                    displayProducts(products);

                    // Clear the selected product after deletion
                    clearSelectedProduct();
                }
            });
        }
    }


    @FXML
    private void filterProducts() {
        String selectedCategory = categoryFilter.getSelectionModel().getSelectedItem();

        // Filter products based on the selected category
        List<Product> filteredProducts = products;

        if (!"All".equals(selectedCategory)) {
            filteredProducts = products.stream()
                    .filter(product -> {
                        if ("Clothes".equals(selectedCategory) && product instanceof Clothes) {
                            return true;
                        } else if ("Shoes".equals(selectedCategory) && product instanceof Shoes) {
                            return true;
                        } else if ("Accessories".equals(selectedCategory) && product instanceof Accessories) {
                            return true;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
        }

        displayProducts(filteredProducts);
    }

    @FXML
    public void showGeneralInfo() {
        // Create a new stage for the general info
        Stage infoStage = new Stage();
        VBox infoBox = new VBox(10);
        infoBox.setAlignment(Pos.CENTER);

        // Initialize capital, income, and cost
        double totalCapital = 0;
        double totalIncome = 0;
        double totalCost = 0;

        Connection connectDB = dbConnection.getConnection();
        String query = "SELECT capital, income, cost FROM user_account WHERE account_id = ?";

        try {
            PreparedStatement stmt = connectDB.prepareStatement(query);
            stmt.setInt(1, accountId); // Use the logged-in user's account_id

            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                totalCapital = resultSet.getDouble("capital");
                totalIncome = resultSet.getDouble("income");
                totalCost = resultSet.getDouble("cost");
            }

            connectDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create labels for capital, income, and cost with custom colors
        Label capitalLabel = new Label("Capital: €" + totalCapital);
        capitalLabel.setStyle("-fx-text-fill: blue;");  // Blue text for capital

        Label incomeLabel = new Label("Income: €" + totalIncome);
        incomeLabel.setStyle("-fx-text-fill: green;");  // Green text for income

        Label costLabel = new Label("Cost: €" + totalCost);
        costLabel.setStyle("-fx-text-fill: red;");  // Red text for cost

        // Add labels to the VBox
        infoBox.getChildren().addAll(capitalLabel, incomeLabel, costLabel);

        // Create and show the scene
        Scene infoScene = new Scene(infoBox, 200, 150);
        infoStage.setScene(infoScene);
        infoStage.setTitle("User Financial Info");
        infoStage.show();
    }

    // Helper method to get the selected product
    private Product getSelectedProduct() {
        String selectedProductName = productNameLabel.getText();
        return getProductByName(selectedProductName);
    }

    private void clearSelectedProduct() {
        selectedProduct = null;  // Clear the selected product

        // Clear the UI labels
        productNameLabel.setText("");
        productItemsLabel.setText("");
        productPriceLabel.setText("");
        productSizeLabel.setText("");
        productImageView.setImage(null);  // Clear the product image

        // Hide Buy and Sell buttons
        BuyStockBtn.setVisible(false);
        SellBtn.setVisible(false);

        // Uncheck the discount checkbox
        discountCheckBox.setSelected(false);
    }


}
