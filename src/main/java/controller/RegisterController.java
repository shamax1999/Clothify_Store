package controller;

import com.jfoenix.controls.JFXPasswordField;
import dto.Admin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.ServiceFactory;
import service.custom.AdminService;
import util.ServiceType;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class RegisterController implements Initializable {
    AdminService adminService= ServiceFactory.getInstance().getServiceType(ServiceType.ADMIN);

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtOTP;

    @FXML
    private JFXPasswordField txtPassword;

    @FXML
    private ComboBox<String> roleList;

    private String generatedOTP;

    @FXML
    void btnRegisterOnAction(ActionEvent event) {
        String email = txtEmail.getText();
        String enteredOTP = txtOTP.getText();
        String password = txtPassword.getText();

        if (email.isEmpty() || enteredOTP.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Empty Field or Fields");
            alert.show();
            return;
        } else if (!isValidEmail(email)) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Invalid Email!");
            alert.show();
            txtEmail.selectAll();
            return;
        } else if (!isValidPassword(password)) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Invalid Password!");
            alert.show();
            txtPassword.selectAll();
            return;
        } else if (!enteredOTP.equals(generatedOTP)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid OTP. Please try again.");
            alert.show();
            return;
        }

        // Register the user if all validations pass
        adminService.registerUser(new Admin(email, password, roleList.getSelectionModel().getSelectedItem()));

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Registration successful!");
        alert.show();

        // Load the login page
        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/login.fxml"))));
            stage.setResizable(false);
            stage.show();
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Clear fields
        txtEmail.clear();
        txtOTP.clear();
        txtPassword.clear();
        roleList.setValue("");
    }


    @FXML
    void btnSendOTPOnAction(ActionEvent event) {
        String email = txtEmail.getText();

        // Check if email is empty or invalid format
        if (email.isEmpty() || !isValidEmail(email)) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Invalid Email!");
            alert.show();
            txtEmail.selectAll();
            return;
        }

        // Now check if the email corresponds to an admin account
        Admin admin = adminService.getAdminByEmail(email);
        if (admin == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No account associated with this email.");
            alert.show();
            return;
        }

        // Generate and send OTP if all validations pass
        generatedOTP = generateOTP();
        try {
            sendOTPViaEmail(email, generatedOTP);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "OTP has been sent to your email.");
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to send OTP. Please try again.");
            alert.show();
        }
    }



    private String generateOTP() {
        int otp = (int) (Math.random() * 9000) + 1000;
        return String.valueOf(otp);
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+]).{8,}$";
        return Pattern.matches(passwordRegex, password);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(emailRegex, email); // No negation here
    }




    private void sendOTPViaEmail(String recipientEmail, String otp) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.debug", "true");


        String senderEmail = "shamax199@gmail.com";
        String senderPassword = "abms mgsa roxu xdwl";

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp);

        Transport.send(message);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> roles= FXCollections.observableArrayList();
        roles.add("Owner");
        roles.add("Manager");

        roleList.setItems(roles);
    }

    public void btnBackOnAction(ActionEvent actionEvent) {
        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/login.fxml"))));
            stage.setResizable(false);
            stage.show();

            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
