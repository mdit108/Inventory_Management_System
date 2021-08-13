package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.*;

import java.io.IOException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;


public class AddProduct implements Initializable {
    //vars
    public TableView<Part> partTable;
    public TableColumn partID;
    public TableColumn partName;
    public TableColumn partInvLevel;
    public TableColumn partPriceUnit;
    public TextField searchPartsTxt;
    public TextField id;
    public int idunique;
    public TextField max;
    public TextField min;
    public TextField price;
    public TextField inv;
    public TextField name;
    public TableView<Part> associatedPartTable;
    public TableColumn associatedPartID;
    public TableColumn associatedPartName;
    public TableColumn associatedPartInvLevel;
    public TableColumn associatedPartPriceUnit;
    ObservableList<Part> associatedParts = FXCollections.observableArrayList();
    Outsourced addPart1;
    InHouse addPart2;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        partTable.setItems(Inventory.getParts());
        partID.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
        partName.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
        partInvLevel.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
        partPriceUnit.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));

        associatedPartID.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
        associatedPartName.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
        associatedPartInvLevel.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
        associatedPartPriceUnit.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));
    }


    public void addProductCancelButton(ActionEvent actionEvent) throws IOException {
        Alert alertUser = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?");
        Optional<ButtonType> optButton = alertUser.showAndWait();
        if (optButton.isPresent() && optButton.get() == ButtonType.OK){
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Main.fxml")));
            Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1200, 600);
            stage.setTitle("Inventory System");
            stage.setScene(scene);
            stage.show();
        }
    }


    public void searchPartButton(ActionEvent actionEvent) {
        String searchPartText = searchPartsTxt.getText();
        ObservableList<Part> parts = Inventory.searchParts(searchPartText);
        if (parts.size() == 0){
            Alert alertUser = new Alert(Alert.AlertType.ERROR, "Part not found");
            alertUser.showAndWait();
        }else{
            partTable.setItems(parts);
            searchPartsTxt.setText("");
        }
    }


    public void saveProduct(ActionEvent actionEvent) {
        try {
            int idCounter = 0;
            for (Product prod: Inventory.getProducts()){
                if (prod.getId() > idCounter){
                    idCounter = prod.getId();
                }
            }
            partID.setText(String.valueOf(++idCounter));
            String pName = name.getText();
            int invLevel = Integer.parseInt(inv.getText());
            double priceCost = Double.parseDouble(price.getText());
            int partMin = Integer.parseInt(min.getText());
            int partMax = Integer.parseInt(max.getText());
            if (partMin > partMax){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Min value is not allowed to be greater than Max. " +
                        "Please try again.");
                alert.showAndWait();
            }
            else if (invLevel > partMax || invLevel < partMin){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Inventory level cannot be less than min or greater " +
                        "than max. Please try again.");
                alert.showAndWait();
            }else{
                Product product = new Product(idCounter, pName, priceCost, invLevel, partMin, partMax);
                for (int i = 0; i < associatedParts.size(); i++){
                    product.setAssociatedParts(associatedParts.get(i));
                }
                Inventory.addProduct(product);
                if (!(Objects.isNull(addPart1))){
                    Inventory.getParts().set(idunique-1,addPart1);
                }
                if (!(Objects.isNull(addPart2))){
                    Inventory.getParts().set(idunique-1,addPart2);
                }


                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Main.fxml")));
                Stage stage = (Stage) ((Parent)actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 1200, 600);
                stage.setTitle("Inventory System");
                stage.setScene(scene);
                stage.show();
            }
        }
        catch(NumberFormatException | IOException e){
            System.out.println(e);
            Alert alertUser = new Alert(Alert.AlertType.CONFIRMATION, "Invalid input. Try again.");
            Optional<ButtonType> optButton = alertUser.showAndWait();
        }
    }


    public void deleteAssPart(ActionEvent actionEvent) {
        Alert alertUser = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?");
        Optional<ButtonType> optButton = alertUser.showAndWait();
        try {
            if (optButton.isPresent() && optButton.get() == ButtonType.OK) {
                Part part = associatedPartTable.getSelectionModel().getSelectedItem();
                if (part != null){
                    associatedParts.remove(part);
                    associatedPartTable.setItems(associatedParts);
                }
            }
        } catch (IndexOutOfBoundsException | NoSuchElementException err) {
            System.out.println(err);
        }
    }


    public void addProductButton(ActionEvent actionEvent) {
        String pname;
        int invLevel,partMin,partMax;
        double priceCost;
        Part part = partTable.getSelectionModel().getSelectedItem();
        if (part != null && !associatedParts.contains(part)){
            if (part.getStock()-Integer.parseInt(inv.getText())<part.getMin()){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Not enough Parts Available.");
                alert.showAndWait();
            }
            else {
                idunique = part.getId();
                pname = part.getName();
                priceCost = part.getPrice();
                partMax = part.getMax();
                partMin = part.getMin();
                invLevel = part.getStock() - Integer.parseInt(inv.getText());
                if (part instanceof Outsourced) {
                    addPart1 = new Outsourced(idunique, pname, priceCost, invLevel, partMin,
                            partMax);

                    associatedParts.add(addPart1);
                }
                if (part instanceof InHouse) {
                    addPart2 = new InHouse(idunique, pname, priceCost, invLevel, partMin,
                            partMax);
                    associatedParts.add(addPart2);
                }
                partTable.setItems(Inventory.getParts());
                partID.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
                partName.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
                partInvLevel.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
                partPriceUnit.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));


                associatedPartTable.setItems(associatedParts);
            }
        }
    }
}