/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mainScene.MainSceneController;

/**
 *
 * @author David
 */
public class FXMLDocumentController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

    }
    @FXML
    private Button loginButton;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    protected void onEnter(ActionEvent event) {
    loginAction( event);
    }
    
    @FXML
    protected void loginButtonReleased(ActionEvent event) {       
        // Parent root;

        /*
            root = FXMLLoader.load(getClass().getClassLoader().getResource("mainMenu/MainMenuFXML.fxml"));
           
            //Stage stage = new Stage();
            //stage.setTitle("My New Stage Title");         
           // stage.show();
       

            //hide this current window (if this is whant you want
          
         */               
            // System.out.println("Inccorrect Creditials");
            loginAction( event);
        }
    
     protected void loginAction(ActionEvent event){         
        DBConnect dbc = new DBConnect();       
        String un = usernameTextField.getText();
        String pw = passwordField.getText();
        if (dbc.checkCred(un, pw) == true) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/mainScene/MainScene.fxml"));
                 Parent content = (Parent) loader.load();
                Stage stage = new Stage();
                stage.setTitle("ET Estimator");
                stage.setScene(new Scene(content));                
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }            
            ((Node) (event.getSource())).getScene().getWindow().hide(); //close the original window
        } else {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/errorMessage/ErrWindow.fxml"));
                Parent content = (Parent) loader.load();
                Stage stage = new Stage();
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(((Node) (event.getSource())).getScene().getWindow()); //Disable User ability to change original window
                stage.setScene(new Scene(content));
                stage.setResizable(false);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    }
    
   

}
