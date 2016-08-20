/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package errorMessage;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author David
 */
public class ErrWindowController implements Initializable {

    @FXML
    private Button errOkButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
     

    }

    @FXML
    protected void closeErr(ActionEvent event) {
        Stage stage = (Stage) errOkButton.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

}
