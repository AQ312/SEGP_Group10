/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PatInformation;

import DataBase.DataBase;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author abdul
 */
public class PATSInformationController implements Initializable {

    @FXML
    private Tab tab;

    @FXML
    private JFXListView<Label> list;

    @FXML
    private Label email;

    @FXML
    private Label contactNumber;

    @FXML
    private Label officeNumber;

    @FXML
    private Label load;

    @FXML
    private Label dept;

    @FXML
    private Label id;

    @FXML
    private BorderPane borderPane;

    @FXML
    private AnchorPane subAnchorPane;

    @FXML
    private Label allocatedStudents;

    private AnchorPane anchorPane;
    private PATsInformationEditingController PATsInformationEditingController;
    private AssignedStudentsController assignedStudentsController;
    private FXMLLoader loader;
    private JFXButton cancelButton;
    private DataBase dataBase = new DataBase();
    private String[][] patsData;
    private String[][] studentData;

    /**
     * Getting PATs data from database.
     */
    public void getPatsData() {

        ArrayList<String> patsInformation = dataBase.getTeachersData();
        patsData = new String[patsInformation.size()][8];

        for (int i = 0; i < patsInformation.size(); i++) {

            String[] tokens = patsInformation.get(i).split(",");
            for (int j = 0; j < tokens.length; j++) {
                patsData[i][j] = tokens[j];
            }
        }
    }

    /**
     * Getting students data from database.
     */
    public void getStudentsData() {

        ArrayList<String> studentsInformation = dataBase.getStudentsData();
        studentData = new String[studentsInformation.size()][7];

        for (int i = 0; i < studentsInformation.size(); i++) {

            String[] tokens = studentsInformation.get(i).split(",");
            for (int j = 0; j < tokens.length; j++) {
                studentData[i][j] = tokens[j];
            }
        }
    }

    @FXML
    public void viewButtonOnClicked() {

        int id = list.getSelectionModel().getSelectedIndex();

        if (id >= 0) {
            String name = list.getSelectionModel().getSelectedItem().getText();
            borderPane.setCenter(assignedStudentsController.anchorPane);

            assignedStudentsController.patName.setText(name);
            assignedStudentsController.groupNumber.setText(String.valueOf(id + 1));

            ObservableList<AssignedStudentsController.Student> students = FXCollections.observableArrayList();

            for (int i = 0; i < studentData.length; i++) {

                if (Integer.parseInt(studentData[i][6]) == id + 1) {

                    students.add(assignedStudentsController.new Student(studentData[i][0], studentData[i][1], studentData[i][2], studentData[i][3],
                            studentData[i][4], studentData[i][5], name));

                }
            }

            final TreeItem<AssignedStudentsController.Student> root = new RecursiveTreeItem<AssignedStudentsController.Student>(students, RecursiveTreeObject::getChildren);
            assignedStudentsController.tableView.setRoot(root);
            assignedStudentsController.tableView.setShowRoot(false);

            list.setDisable(true);

            assignedStudentsController.backButtonClicked.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent e) {
                    list.setDisable(false);
                    borderPane.setCenter(subAnchorPane);
                }
            });
        } else {
            
            ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert alert = new Alert(Alert.AlertType.WARNING, null, ok, cancel);

            alert.setTitle("Warning");
            alert.setHeaderText("Look, an Information Dialog");
            alert.setContentText("Please Select a PAT before Viewing Assigned Students");
            alert.showAndWait();

        }
    }

    public void setInformation(String name) {

        for (int i = 0; i < patsData.length; i++) {
            if (patsData[i][1].equals(name)) {

//                email.setTe
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loader = new FXMLLoader(getClass().getResource("../PatInformation/EditPatInformation.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PATsInformationEditingController = loader.getController();
        cancelButton = PATsInformationEditingController.getCancelButton();
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                list.setDisable(false);
                borderPane.setCenter(subAnchorPane);
            }

        });

        PATsInformationEditingController.saveChanges.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                String contactNumber = PATsInformationEditingController.editContactNumber.getText();
                String officeNumber = PATsInformationEditingController.editOfficeNumber.getText();

                if (validateEditPATInformation(contactNumber, officeNumber)) {
                    
                    changeSetting(contactNumber, officeNumber, String.valueOf(list.getSelectionModel().getSelectedIndex() + 1));
                }

            }
        });

        anchorPane = PATsInformationEditingController.getAnchorPane();

        loader = new FXMLLoader(getClass().getResource("../PatInformation/AssignedStudentsView.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assignedStudentsController = loader.getController();

        settingPATData();
    }

    public boolean validateEditPATInformation(String contactNumber, String officeNumber) {

        if (contactNumber.equals(null) && officeNumber.equals(null)) {
            showWarning("Nothing to be done!");
            return false;
        }
        if (((contactNumber.matches("03[0-9]{9}") == false
                && contactNumber.matches("03[0-9]{2}[-][0-9]{7}") == false))
                && contactNumber.matches("[+]{1}923[0-9]{9}") == false
                && contactNumber.matches("[+]{1}923[-]{1}[0-9]{9}") == false) {
            showWarning("Contact Number is not valid!");
            return false;
        }

        //need to make regex for phone number
        return true;
    }

    /**
     * Making a list of the names of pats and adding listener to them so that
     * when they are clicked information are meant to be displayed on the
     * screen.
     */
    public void settingPATData() {

        getPatsData();
        getStudentsData();
        for (int i = 0; i < patsData.length; i++) {

            final int row = i;
            final Label nameLabel = new Label(patsData[i][1]);
            nameLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    id.setText(patsData[row][0]);
                    email.setText(patsData[row][2]);
                    contactNumber.setText(patsData[row][4]);
                    officeNumber.setText(patsData[row][3]);
                    dept.setText(patsData[row][5]);
                    load.setText(patsData[row][6]);
                    allocatedStudents.setText(patsData[row][7]);
                }
            });
            list.getItems().add(nameLabel);
        }
    }

    public Tab getTab() {
        return tab;
    }

//    @FXML 
//    public void viewButtonOnClicked(){
//        
//    }
    @FXML
    public void viewAssignedPATsClicked() {
    }

    @FXML
    public void editButtonOnClicked() {

        if (list.getSelectionModel().getSelectedItem() != null) {
            borderPane.setCenter(anchorPane);
            list.setDisable(true);
        } else {

            showWarning("Please select a PAT before editing!");
        }
    }

    public void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Warning");
        alert.setHeaderText("Look, an Information Dialog");
        alert.setContentText(message);
        alert.show();
    }
    

    /**
     *  when the user saves the changed information then these information goes to database and then an execution of a query
     *  saves it. It is called if the contact number and the office number is in correct format.
     */
    public void changeSetting(String contactNumber, String officeNumber, String id) {

        int selectedRow = -1;

        System.out.println("office: "+officeNumber);
        for (int i = 0; i < patsData.length; i++) {
            if (patsData[i][0].equals(id)) {
                selectedRow = i;
                break;
            }
        }

        if (!officeNumber.equals(null)) {
            patsData[selectedRow][3] = officeNumber;
        }
        if (!contactNumber.equals(null)) {
            patsData[selectedRow][4] = contactNumber;
        }

        dataBase.editPatInformation(contactNumber, officeNumber, patsData[selectedRow][0]);
        PATsInformationEditingController.editContactNumber.setText("");
        PATsInformationEditingController.editOfficeNumber.setText("");
    }
}
