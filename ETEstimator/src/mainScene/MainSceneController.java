/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainScene;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import backend.*;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.concurrent.Task;

/**
 * FXML Controller class
 *
 * @author David
 */
public class MainSceneController implements Initializable {

    /**
     * Initializes the controller class.
     */
    double lat;
    double lon;
    boolean ready = false;
    private JSObject jdoc;

    @FXML
    private WebView sourceWebView;

    @FXML
    private WebView destWebView;

    @FXML
    private WebView autoSourceWebView;

    private WebEngine we1;
    private WebEngine we2;
    private WebEngine we3;

    @FXML
    private TextField airTempText;
    @FXML
    private TextField windSpeedText;
    @FXML
    private TextField relHumText;
    @FXML
    private TextField pressureText;
    @FXML
    private TextField surTempText;
    @FXML
    private TextField netRadText;
    @FXML
    private TextField soilHeatText;
    @FXML
    private TextField ndviText;
    @FXML
    private TextField eviTExt;
    @FXML
    private TextField etText;
    @FXML
    private DatePicker dateSelector;
    @FXML
    private Button goButton;
    @FXML
    private TextField arffFileLocationText;

    @FXML
    private GridPane modelGridPane;
    @FXML
    private GridPane attributesGridPane;
    @FXML
    private GridPane arffFileGirdPane;
    @FXML
    private GridPane resultsGridPane;

    @FXML
    private Label etLabel;
    @FXML
    private Label attributeCheckLabel;
    @FXML
    private Label etWarningLabel;
    @FXML
    private Label indicatorWarningLabel;
    @FXML
    private Label indicatorLabel;

    @FXML
    private ComboBox modeCombo;

    @FXML
    private CheckBox dateCheck;
    @FXML
    private CheckBox airTempCheck;
    @FXML
    private CheckBox windSpeedCheck;
    @FXML
    private CheckBox relHumCheck;
    @FXML
    private CheckBox pressureCheck;
    @FXML
    private CheckBox surTempCheck;
    @FXML
    private CheckBox netRadCheck;
    @FXML
    private CheckBox soilHeatCheck;
    @FXML
    private CheckBox ndviCheck;
    @FXML
    private CheckBox eviCheck;

    @FXML
    private CheckBox annCheck;
    @FXML
    private CheckBox smoPolyCheck;
    @FXML
    private CheckBox smoPukCheck;
    @FXML
    private CheckBox smoRbfCheck;
    @FXML
    private CheckBox gpPolyCheck;
    @FXML
    private CheckBox gpPukCheck;
    @FXML
    private CheckBox gpRbfCheck;
    @FXML
    private CheckBox m5pCheck;
    @FXML
    private CheckBox lrCheck;

    @FXML
    private ComboBox indicatorCombo;

    @FXML
    private TextField bestModelText;

    @FXML
    private Label selectClassifierLabel;

    @FXML
    private ProgressBar etProgressBar;
    @FXML
    private ProgressIndicator etProgressIndicator;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO        
        //login.GoogleMap gm = new login.GoogleMap(mapsWebView,we1);
        we1 = sourceWebView.getEngine();
        we1.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            public void changed(final ObservableValue<? extends Worker.State> observableValue,
                    final Worker.State oldState,
                    final Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {
                    ready = true;
                    System.out.println("ready");
                }
            }
        });
        we1.load("file:///C:/Users/David/Documents/NetBeansProjects/ETEstimator/src/Resources/Test2.html");
        initCommunication();

        we2 = destWebView.getEngine();
        we2.load("file:///C:/Users/David/Documents/NetBeansProjects/ETEstimator/src/Resources/Test1.html");

        we3 = autoSourceWebView.getEngine();
        we3.load("file:///C:/Users/David/Documents/NetBeansProjects/ETEstimator/src/Resources/Test1.html");

        for (Node node : attributesGridPane.getChildren()) {
            node.setDisable(true);
        }

        for (Node node : modelGridPane.getChildren()) {
            node.setDisable(true);
        }
        attributeCheckLabel.setDisable(true);
        arffFileGirdPane.setVisible(false);
        resultsGridPane.setVisible(false);
        arffFileLocationText.setDisable(true);
        etWarningLabel.setVisible(false);
        selectClassifierLabel.setVisible(false);
        bestModelText.setDisable(true);
        etText.setDisable(true);
        etProgressBar.setVisible(false);
        etProgressIndicator.setVisible(false);
        indicatorWarningLabel.setVisible(false);

    }
    @FXML
    TextField latText;

    @FXML
    TextField longText;

    //Initiate communcation with javasript
    private void initCommunication() {
        we1.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(final ObservableValue<? extends Worker.State> observableValue,
                    final Worker.State oldState,
                    final Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {
                    jdoc = (JSObject) we1.executeScript("window");
                    jdoc.setMember("app", new GoogleMap());
                }
            }
        });
    }

    //Class to handle event when clicked on map
    public class GoogleMap {

        public void handle(double lat, double lng) {

            latText.setText("" + lat);
            longText.setText("" + lng);

        }
    }

    //When button clicked marker on map moves to corresponding location
    @FXML
    protected void loadMap(ActionEvent event) {

        lat = Double.parseDouble(latText.getText());
        lon = Double.parseDouble(longText.getText());
        we1.executeScript(""
                + "window.lat = " + lat + ";"
                + "window.lon = " + lon + ";"
                + "document.goToLocation(window.lat, window.lon);"
        );
        System.out.printf("%.2f %.2f%n", lat, lon);
    }

    //Handler selecting insert instance or estimate ET
    @FXML
    protected void selectMode(ActionEvent event) {

        resultsGridPane.setVisible(false);
        selectClassifierLabel.setVisible(false);
        etProgressBar.setVisible(false);
        etProgressIndicator.setVisible(false);
        etWarningLabel.setVisible(false);
        indicatorWarningLabel.setVisible(false);
        attributeCheckLabel.setDisable(true);
        for (Node node : modelGridPane.getChildren()) {
            node.setDisable(true);
        }
        //If selected insert instance
        if (modeCombo.getSelectionModel().getSelectedIndex() == 0) {
            //enable all controls in attributes grid pane
            for (Node node : attributesGridPane.getChildren()) {
                if (node instanceof CheckBox) {
                    node.setDisable(true);
                }else if (node.equals(indicatorLabel)){
                    node.setDisable(true);                    
                } else if (node.equals(indicatorCombo)){
                    node.setDisable(true);
                }
                else {
                    node.setDisable(false);
                }
            }
            arffFileGirdPane.setVisible(false);
            //disable all controls in model grid pane
        } else if (modeCombo.getSelectionModel().getSelectedIndex() == 1) {
            for (Node node : attributesGridPane.getChildren()) {
                node.setDisable(true);
            }
            arffFileLocationText.setDisable(true);
            selectClassifierLabel.setVisible(false);
            arffFileGirdPane.setVisible(true);
            bestModelText.setText("");
        }
    }

    @FXML
    protected void goButtonClicked(ActionEvent e) {
        if (modeCombo.getSelectionModel().getSelectedIndex() == 0) {
            if (etText.getText().equals("")) {
                etWarningLabel.setVisible(true);
            } else {
                etWarningLabel.setVisible(false);
                ArrayList<String> attributesValues = new ArrayList<String>();
                String tableName = "seven_weather_ndvi_evi";
                backend.DBConnect DBC = new backend.DBConnect();
                String id = DBC.getMaxResult(tableName, "`ID`") + 1 + "";
                if (dateSelector.getValue() != null) {
                    attributesValues.add(dateSelector.getValue().toString());
                    System.out.println(attributesValues.get(0));
                } else {
                    attributesValues.add("NULL");
                }

                for (Node node : attributesGridPane.getChildren()) {
                    if (node instanceof TextField) {
                        TextField temp = (TextField) node;
                        if (!temp.getText().equals("")) {
                            attributesValues.add(temp.getText());
                        } else {
                            attributesValues.add("NULL");
                        }
                    }
                }
                //insert instance to DB
                DBC.insertARFFDB(tableName, id, attributesValues.get(0), attributesValues.get(1), attributesValues.get(2), attributesValues.get(3), attributesValues.get(4), attributesValues.get(5), attributesValues.get(6), attributesValues.get(7), attributesValues.get(8), attributesValues.get(9), attributesValues.get(10));
            }
        } //Chose Estimate ET
        else if (modeCombo.getSelectionModel().getSelectedIndex() == 1) {
            indicatorWarningLabel.setVisible(false);

            resultsGridPane.setVisible(false);
            bestModelText.setText("");
            //Check that at least one classifier has been chosen
            boolean classifierChosen = false;
            for (Node node : modelGridPane.getChildren()) {

                if (node instanceof CheckBox) {
                    CheckBox temp = (CheckBox) node;
                    if (temp.isSelected()) {
                        selectClassifierLabel.setVisible(false);
                        classifierChosen = true;
                    }
                }
            }
            if (classifierChosen == false) {
                selectClassifierLabel.setVisible(true);
                return;
            }

            if (indicatorCombo.getSelectionModel().isEmpty()) {
                indicatorWarningLabel.setVisible(true);
                return;
            }
            etProgressBar.setVisible(true);
            etProgressIndicator.setVisible(true);
            etProgressBar.setProgress(0.0f);
            etProgressIndicator.setProgress(0.0f);

            Task task = new Task<Void>() {
                String ModelName = "";

                @Override
                public Void call() {

                    ArffBuilder AB = new ArffBuilder();
                    EvaluationModel EM = new EvaluationModel();

                    try {
                        ArrayList<Integer> AttributeIndex = new ArrayList<Integer>();
                        AttributeIndex.add(0); //not using id for generating model
                        int i = 1;
                        for (Node node : attributesGridPane.getChildren()) {
                            if (node instanceof CheckBox) {
                                CheckBox tempCheck = (CheckBox) node;
                                if (tempCheck.isSelected()) {
                                    AttributeIndex.add(1);
                                    i++;
                                } else {
                                    AttributeIndex.add(0);
                                }
                            }
                        }
                        AttributeIndex.add(1); //use ET
                        System.out.print("Length: " + AttributeIndex.size());
                        String FilePath = arffFileLocationText.getText() + "/";
                        AB.ArffCreate("seven_weather_ndvi_evi", "ARFF", FilePath, AttributeIndex);

                        String ResultDB = "testResultcreate";
                        backend.DBConnect DB = new backend.DBConnect();
                        if (DB.checkTable("testResultcreate") == 1) {
                            DB.dropTable("testResultcreate");
                        }
                        DB.createResultDB(ResultDB, "ID", "Model", "ModelSetting", "RMSE", "R", "NSE", "NameofArff");
                        System.out.println("Table " + ResultDB + " establised");
                        //EM.evaluation("MultiPerceptron", "seven_weather_ndvi_evi", ResultDB, FilePath + "ARFF.arff");

                        if (annCheck.isSelected()) {
                            EM.evaluation("MultiPerceptron", "seven_weather_ndvi_evi", ResultDB, FilePath + "ARFF.arff");
                        }
                        updateProgress(1, 10);

                        //   etProgressBar.setProgress(0.1f);
                        // etProgressIndicator.setProgress(0.1f);
                        if (smoPolyCheck.isSelected()) {
                            EM.evaluation("SMOregPoly", "seven_weather_ndvi_evi", ResultDB, FilePath + "/ARFF.arff");
                        }
                        updateProgress(2, 10);
                        //   etProgressBar.setProgress(0.2f);
                        //  etProgressIndicator.setProgress(0.2f);
                        if (smoPukCheck.isSelected()) {
                            EM.evaluation("SMOregPuk", "seven_weather_ndvi_evi", ResultDB, FilePath + "/ARFF.arff");
                        }
                        updateProgress(3, 10);
                        //  etProgressBar.setProgress(0.3f);
                        //  etProgressIndicator.setProgress(0.3f);
                        if (smoRbfCheck.isSelected()) {
                            EM.evaluation("SMOregRBF", "seven_weather_ndvi_evi", ResultDB, FilePath + "/ARFF.arff");
                        }
                        updateProgress(4, 10);
                        //  etProgressBar.setProgress(0.4f);
                        //  etProgressIndicator.setProgress(0.4f);
                        if (gpPolyCheck.isSelected()) {
                            EM.evaluation("GPPoly", "seven_weather_ndvi_evi", ResultDB, FilePath + "/ARFF.arff");
                        }
                        updateProgress(5, 10);
                        //  etProgressBar.setProgress(0.5f);
                        // etProgressIndicator.setProgress(0.5f);
                        if (gpPukCheck.isSelected()) {
                            EM.evaluation("GPPuk", "seven_weather_ndvi_evi", ResultDB, FilePath + "/ARFF.arff");
                        }
                        updateProgress(6, 10);
                        // etProgressBar.setProgress(0.6f);
                        //etProgressIndicator.setProgress(0.6f);
                        if (gpRbfCheck.isSelected()) {
                            EM.evaluation("GPRBF", "seven_weather_ndvi_evi", ResultDB, FilePath + "/ARFF.arff");
                        }
                        updateProgress(7, 10);
                        //etProgressBar.setProgress(0.7f);
                        //etProgressIndicator.setProgress(0.7f);
                        if (m5pCheck.isSelected()) {
                            EM.evaluation("M5P", "seven_weather_ndvi_evi", ResultDB, FilePath + "/ARFF.arff");
                        }
                        updateProgress(8, 10);
                        //etProgressBar.setProgress(0.8f);
                        //etProgressIndicator.setProgress(0.8f);
                        if (lrCheck.isSelected()) {
                            EM.evaluation("LR", "seven_weather_ndvi_evi", ResultDB, FilePath + "/ARFF.arff");
                        }
                        updateProgress(9, 10);
                        // etProgressBar.setProgress(0.9f);
                        //  etProgressIndicator.setProgress(0.9f);

                        if (indicatorCombo.getSelectionModel().getSelectedIndex() == 0) {
                            System.out.println("Reached Selected RMSE");
                            double RMSEValue = DB.getMinResult(ResultDB, "RMSE");
                            ArrayList<Double> RMSEArray = new ArrayList<Double>();
                            RMSEArray.add(RMSEValue);

                            //get id of instance
                            int IndexValue = DB.getIndexValue(ResultDB, "RMSE", "" + RMSEValue + "");
                            ArrayList<Integer> ResultIndexArray = new ArrayList<Integer>();
                            ResultIndexArray.add(IndexValue);
                            //model Name
                            ModelName = DB.getData(ResultDB, "ID", "" + IndexValue + "", "Model");
                        } else if (indicatorCombo.getSelectionModel().getSelectedIndex() == 1) {
                            //do sth
                        } else if (indicatorCombo.getSelectionModel().getSelectedIndex() == 1) {
                            //do sth
                        }

                    } catch (Exception ee) {
                        System.out.println(ee);
                    }
                    updateProgress(10, 10);
                    // etProgressBar.setProgress(1.0f);
                    //etProgressIndicator.setProgress(1.0f);
                    bestModelText.setText(ModelName);
                    resultsGridPane.setVisible(true);
                    return null;
                }
            };

            etProgressBar.progressProperty().bind(task.progressProperty());
            etProgressIndicator.progressProperty().bind(task.progressProperty());
            new Thread(task).start();

        }

    }

    @FXML
    protected void selectArffLocation(ActionEvent e) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("arff path");
        File selectedDirectory = chooser.showDialog(((Node) (e.getSource())).getScene().getWindow());
        if (selectedDirectory != null) {
            arffFileLocationText.setText(selectedDirectory.getAbsolutePath());
            for (Node node : attributesGridPane.getChildren()) {
                if (node.equals(etText)) {
                    node.setDisable(true);
                } else if (node.equals(etLabel)) {
                    node.setDisable(true);
                } else {
                    node.setDisable(false);
                }
            }
            for (Node node : modelGridPane.getChildren()) {
                node.setDisable(false);
            }

            attributeCheckLabel.setDisable(false);
            for (Node node : modelGridPane.getChildren()) {
                node.setDisable(false);
            }

        }

    }

}
