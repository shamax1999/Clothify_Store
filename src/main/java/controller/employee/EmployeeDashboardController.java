package controller.employee;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import dto.Employee;
import dto.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.ServiceFactory;
import service.custom.EmployeeService;
import service.custom.UserService;
import util.ServiceType;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class EmployeeDashboardController implements Initializable {
    static EmployeeService employeeService = ServiceFactory.getInstance().getServiceType(ServiceType.EMPLOYEE);
    UserService userService = ServiceFactory.getInstance().getServiceType(ServiceType.USER);

    @FXML
    private ImageView imageView;

    @FXML
    private Label lblEmpName;

    @FXML
    private JFXTextField txtEmail;

    @FXML
    private JFXTextField txtId;

    @FXML
    private JFXTextField txtName;

    @FXML
    private JFXPasswordField txtPassword;

    private byte[] imageByte = null;

    public static Employee loginEmployee;

    public static void setEmpId(String empId) {
        loginEmployee = employeeService.getAll().stream()
                .filter(employee -> Objects.equals(employee.getEmpId(), empId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setEmployeeDetails();
    }

    private void setEmployeeDetails() {
        if (loginEmployee != null) {
            txtId.setText(loginEmployee.getEmpId());
            txtName.setText(loginEmployee.getName());
            lblEmpName.setText(loginEmployee.getName());
            txtEmail.setText(loginEmployee.getEmail());
            if (loginEmployee.getImage() != null) {
                imageView.setImage(new Image(new ByteArrayInputStream(loginEmployee.getImage())));
            }
        }
    }

    @FXML
    void btnUpdateEmpOnAction(ActionEvent event) {
        if (txtName.getText().isEmpty() || txtEmail.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Field or Fields");
        } else if (!isValidEmail(txtEmail.getText())) {
            showAlert(Alert.AlertType.WARNING, "Invalid Email!");
            txtEmail.selectAll();
        } else if (!isValidPassword(txtPassword.getText())) {
            showAlert(Alert.AlertType.WARNING, "Invalid Password!");
            txtPassword.selectAll();
        } else {
            try {
                updateEmployeeDetails();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "An error occurred while updating the employee.");
            }
        }
    }

    private void updateEmployeeDetails() {
        Employee employee = new Employee(
                txtId.getText(),
                txtName.getText(),
                txtEmail.getText(),
                loginEmployee.getPassword(),
                loginEmployee.getOrderCount(),
                imageByte != null ? imageByte : loginEmployee.getImage()
        );

        System.out.println("Prepared Employee Object: " + employee);

        User user = userService.getUserByEmail(loginEmployee.getEmail());
        if (user == null) {
            System.out.println("User not found for email: " + loginEmployee.getEmail());
            showAlert(Alert.AlertType.ERROR, "User not found!");
            return;
        }

        // Check if email has been updated
        if (!Objects.equals(loginEmployee.getEmail(), txtEmail.getText())) {
            user.setEmail(txtEmail.getText());
            employee.setEmail(txtEmail.getText());
            userService.updateUserEmail(user);
            System.out.println("Updated user email: " + user.getEmail());
        }

        // Check if password has been updated
        if (!txtPassword.getText().isEmpty()) {
            user.setPassword(txtPassword.getText());
            userService.updateUserPassword(user);
            employee.setPassword(txtPassword.getText()); // Update Employee object with new password
            System.out.println("Updated user password.");
        }

        boolean updated = employeeService.updateEmployee(employee);
        System.out.println("Employee update status: " + updated);

        if (updated) {
            showAlert(Alert.AlertType.INFORMATION, "Employee Updated Successfully");
            loginEmployee = employee;
            setEmployeeDetails();
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed to Update Employee");
        }

        txtPassword.clear();
    }


    private boolean isValidPassword(String password) {
        return txtPassword.getText().isEmpty() || Pattern.matches("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+]).{8,}$", password);
    }

    private boolean isValidEmail(String email) {
        return Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", email);
    }

    @FXML
    void btnUpdateEmpImageOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Edit Image File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (selectedFile != null) {
            try {
                imageView.setImage(new Image(selectedFile.toURI().toString()));
                imageByte = Files.readAllBytes(selectedFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void EmployeeMainPageOnAction(ActionEvent event) {
        navigateToPage(event, "/view/employee_main.fxml", "You might have unsaved changes. Do you want to exit?");
    }

    @FXML
    void ItemPageOnAction(ActionEvent event) {
        navigateToPage(event, "/view/item.fxml", "You might have unsaved changes. Do you want to exit?");
    }

    @FXML
    void OrderPageOnAction(ActionEvent event) {
        navigateToPage(event, "/view/order.fxml", "You might have unsaved changes. Do you want to exit?");
    }

    private void navigateToPage(ActionEvent event, String fxmlPath, String closeMessage) {
        Stage stage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(false);
            stage.setOnCloseRequest(closeEvent -> {
                Optional<ButtonType> result = showAlertWithConfirmation(closeMessage);
                if (result.isEmpty() || result.get() == ButtonType.CANCEL) {
                    closeEvent.consume();
                }
            });
            stage.show();

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnLogoutOnAction(ActionEvent actionEvent) {
        Optional<ButtonType> result = showAlertWithConfirmation("Are you sure you want to log out?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close();
                Stage loginStage = new Stage();
                loginStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/login.fxml"))));
                loginStage.setResizable(false);
                loginStage.show();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Error loading login page.", e);
            }
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }

    private Optional<ButtonType> showAlertWithConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message);
        return alert.showAndWait();
    }
}
