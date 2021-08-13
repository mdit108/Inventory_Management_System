package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class loginControler {

    public TextField uName;
    public TextField pwd;
    public Label invalidMessage;
    public void exitProgram(ActionEvent actionEvent) throws IOException {
        System.exit(0);
    }
    public void checkLogin(ActionEvent actionEvent) throws IOException, InterruptedException {
        if (uName.getText().equals("admin") && pwd.getText().equals("rvce")){
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Main.fxml")));
            Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();

            Scene scene = new Scene(root, 1200, 600);
            stage.setTitle("Inventory System");

            stage.setScene(scene);

            stage.show();
        }
        else{
            invalidMessage.setText("Invalid Login. Please try again!");
        }
    }

}
