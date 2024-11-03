package controller.admin;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import dto.Supplier;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import service.ServiceFactory;
import service.custom.SupplierService;
import util.ServiceType;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;


public class SupplierController implements Initializable{
    SupplierService supplierService= ServiceFactory.getInstance().getServiceType(ServiceType.SUPPLIER);

    @FXML
    private TableColumn<?, ?> colSupCompany;

    @FXML
    private TableColumn<?, ?> colSupID;

    @FXML
    private TableColumn<?, ?> colSupName;

    @FXML
    private TableView<Supplier> supplierTbl;

    @FXML
    private JFXTextField txtSupCompany;

    @FXML
    private JFXTextField txtSupEmail;

    @FXML
    private JFXTextField txtSupId;

    @FXML
    private JFXTextField txtSupName;

    @FXML
    private JFXTextField viewSupCompany;

    @FXML
    private JFXTextField viewSupEmail;

    @FXML
    private JFXTextField viewSupId;

    @FXML
    private JFXTextField viewSupName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        generateSupId();
        loadSupplierTable();


        supplierTbl.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                setSupViewFields(newValue);
            }
        });

    }



    private void setSupViewFields(Supplier supplier) {
        viewSupId.setText(supplier.getSupId());
        viewSupName.setText(supplier.getName());
        viewSupEmail.setText(supplier.getEmail());
        viewSupCompany.setText(supplier.getCompany());
    }

    private void generateSupId(){
        int lastSupCount=0;

        ObservableList<Supplier> supplierList = supplierService.getAll();
        if (!supplierList.isEmpty()) {
            Supplier lastSupplier = supplierList.getLast();

            String lastSupId = lastSupplier.getSupId();

            String lastStringSupCount = lastSupId.substring(lastSupId.length() - 3);

            lastSupCount = Integer.parseInt(lastStringSupCount);
        }

        String id=String.format("S%03d",lastSupCount+1);
        txtSupId.setText(id);
    }

    private void loadSupplierTable(){
        colSupID.setCellValueFactory(new PropertyValueFactory<>("supId"));
        colSupName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSupCompany.setCellValueFactory(new PropertyValueFactory<>("company"));

        supplierTbl.setItems(supplierService.getAll());
    }


    @FXML
    void AdminMainPageOnAction(ActionEvent event) {
        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/admin_main.fxml"))));
            stage.setResizable(false);
            stage.setOnCloseRequest(closeEvent -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You might have unsaved changes. Do you want to exit?");
                if (alert.showAndWait().get() == ButtonType.CANCEL) {
                    closeEvent.consume();
                }
            });
            stage.show();
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void EmployeePageOnAction(ActionEvent event) {
        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/employee.fxml"))));
            stage.setResizable(false);
            stage.setOnCloseRequest(closeEvent -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You might have unsaved changes. Do you want to exit?");
                if (alert.showAndWait().get() == ButtonType.OK) {
                    Stage adminStage = new Stage();
                    try {
                        adminStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/admin_main.fxml"))));
                        adminStage.setResizable(false);
                        adminStage.show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    closeEvent.consume();
                }
            });
            stage.show();
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void ItemPageOnAction(ActionEvent event) {
        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/admin_item.fxml"))));
            stage.setResizable(false);
            stage.setOnCloseRequest(closeEvent -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You might have unsaved changes. Do you want to exit?");
                if (alert.showAndWait().get() == ButtonType.OK) {
                    Stage adminStage = new Stage();
                    try {
                        adminStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/admin_main.fxml"))));
                        adminStage.setResizable(false);
                        adminStage.show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    closeEvent.consume();
                }
            });
            stage.show();
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    void SupplierPageOnAction(ActionEvent event) {
        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/supplier.fxml"))));
            stage.setResizable(false);
            stage.setOnCloseRequest(closeEvent -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You might have unsaved changes. Do you want to exit?");
                if (alert.showAndWait().get() == ButtonType.OK) {
                    Stage adminStage = new Stage();
                    try {
                        adminStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/admin_main.fxml"))));
                        adminStage.setResizable(false);
                        adminStage.show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    closeEvent.consume();
                }
            });
            stage.show();
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnAddSupOnAction(ActionEvent event) {
        if (txtSupName.getText().isEmpty() || txtSupCompany.getText().isEmpty() || txtSupEmail.getText().isEmpty() ) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Empty Field or Fields");
            alert.show();
        }else if(!isValidEmail(txtSupEmail.getText())) {
            Alert alert=new Alert(Alert.AlertType.WARNING,"Invalid Email!");
            alert.show();
            txtSupEmail.selectAll();
        }else{

            Supplier supplier = new Supplier(txtSupId.getText(),txtSupName.getText(),txtSupCompany.getText(),txtSupEmail.getText());
            System.out.println(supplier);

            if (supplierService.addSupplier(supplier)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Supplier Added Successfully");
                alert.show();
            }

            generateSupId();
            loadSupplierTable();


            txtSupName.setText("");
            txtSupCompany.setText("");
            txtSupEmail.setText("");

            txtSupName.requestFocus();
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(emailRegex, email); // No negation here
    }



    @FXML
    void btnDeleteSupOnAction(ActionEvent event) {
        Supplier selectedSupplier = supplierTbl.getSelectionModel().getSelectedItem();

        if (selectedSupplier == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an supplier to delete.");
            alert.show();
            return;
        }

        if (supplierService.deleteSupplier(selectedSupplier)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Supplier Deleted Successfully");
            alert.show();
        }

        loadSupplierTable();



        viewSupId.setText("");
        viewSupName.setText("");
        viewSupCompany.setText("");
        viewSupEmail.setText("");

    }


    @FXML
    void btnUpdateSupOnAction(ActionEvent event) {
        Supplier selectedSupplier = supplierTbl.getSelectionModel().getSelectedItem();

        if (selectedSupplier == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an Supplier to update.");
            alert.show();
            return;
        }

        if (viewSupName.getText().isEmpty() || viewSupEmail.getText().isEmpty() || viewSupCompany.getText().isEmpty() ) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Empty Field or Fields");
            alert.show();
        }else if(!isValidEmail(txtSupEmail.getText())) {
            Alert alert=new Alert(Alert.AlertType.WARNING,"Invalid Email!");
            alert.show();
            txtSupEmail.selectAll();
        }else{

            Supplier supplier = new Supplier(viewSupId.getText(),viewSupName.getText(),viewSupCompany.getText(),viewSupEmail.getText());
            System.out.println(supplier);

            if (supplierService.updateSupplier(supplier)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Supplier Updated Successfully");
                alert.show();
            }

            loadSupplierTable();


            viewSupId.setText("");
            viewSupName.setText("");
            viewSupCompany.setText("");
            viewSupEmail.setText("");
        }
    }

    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    void btnLogoutOnAction(ActionEvent event) {
        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/login.fxml"))));
            stage.setResizable(false);
            stage.show();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            throw new RuntimeException("Error loading login page", e);
        }
    }

    @FXML
    void OrderReportsPageOnAction(ActionEvent event) {
        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/order_reports.fxml"))));
            stage.setResizable(false);
            stage.setOnCloseRequest(closeEvent -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You might have unsaved changes. Do you want to exit?");
                if (alert.showAndWait().get() == ButtonType.OK) {
                    Stage adminStage = new Stage();
                    try {
                        adminStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/admin_main.fxml"))));
                        adminStage.setResizable(false);
                        adminStage.show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    closeEvent.consume();
                }
            });
            stage.show();
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
