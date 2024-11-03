package controller;
import controller.employee.ItemController;

import com.jfoenix.controls.JFXPasswordField;
import controller.employee.EmployeeDashboardController;
import dto.Admin;
import dto.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.ServiceFactory;
import service.custom.AdminService;
import service.custom.UserService;
import util.ServiceType;

import java.io.IOException;
import java.util.regex.Pattern;

public class LoginController {
    UserService userService = ServiceFactory.getInstance().getServiceType(ServiceType.USER);
    AdminService adminService = ServiceFactory.getInstance().getServiceType(ServiceType.ADMIN);

    @FXML
    private TextField txtEmail;

    @FXML
    private JFXPasswordField txtPassword;

    public void btnRegisterOnAction(javafx.scene.input.MouseEvent mouseEvent) {
        openNewStage("/view/register.fxml", mouseEvent);
    }

    @FXML
    void btnLoginOnAction(ActionEvent event) {
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Field or Fields");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.WARNING, "Invalid Email!");
            txtEmail.selectAll();
            return;
        }

        User user = userService.loginUser(email, password);
        Admin admin = adminService.loginAdmin(email, password);

        try {
            if (user != null) {
                ItemController.setLoginEmployee(user);

                EmployeeDashboardController.setEmpId(user.getEmpId());
                openDashboard("/view/employee_main.fxml", event);
            } else if (admin != null) {
                openDashboard("/view/admin_main.fxml", event);
            } else {
                showAlert(Alert.AlertType.WARNING, "Invalid Email or Password");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(emailRegex, email);
    }

    public void btnPWForgotPageOnAction(javafx.scene.input.MouseEvent mouseEvent) {
        openNewStage("/view/forgot_password.fxml", mouseEvent);
    }

    private void openDashboard(String fxmlPath, ActionEvent event) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource(fxmlPath))));
        stage.setResizable(false);
        stage.setOnCloseRequest(closeEvent -> reopenLogin());
        stage.show();
        closeCurrentStage(event);
    }

    private void reopenLogin() {
        try {
            Stage adminStage = new Stage();
            adminStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/login.fxml"))));
            adminStage.setResizable(false);
            adminStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void openNewStage(String fxmlPath, javafx.scene.input.MouseEvent mouseEvent) {
        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource(fxmlPath))));
            stage.setResizable(false);
            stage.show();
            closeCurrentStage(mouseEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeCurrentStage(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    private void closeCurrentStage(javafx.scene.input.MouseEvent mouseEvent) {
        ((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType, message);
        alert.show();
    }
}