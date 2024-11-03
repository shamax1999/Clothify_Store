package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import dto.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.ServiceFactory;
import service.custom.UserService;
import util.ServiceType;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

public class ForgotPasswordController {
    UserService userService= ServiceFactory.getInstance().getServiceType(ServiceType.USER);

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtOTP;

    @FXML
    private JFXPasswordField txtPassword;

    @FXML
    private JFXButton btnLogin;

    private String generatedOTP;

    @FXML
    void btnLoginOnAction(ActionEvent event) {
        if (txtEmail.getText().isEmpty() || txtPassword.getText().isEmpty() ) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Empty Field or Fields");
            alert.show();
        }else if(!isValidEmail(txtEmail.getText())) {
            Alert alert=new Alert(Alert.AlertType.WARNING,"Invalid Email!");
            alert.show();
            txtEmail.selectAll();
        }else {

            User user = userService.getUserByEmail(txtEmail.getText());

            if (user != null) {
                if(!isValidPassword(txtPassword.getText())) {
                    Alert alert=new Alert(Alert.AlertType.WARNING,"Invalid Password!");
                    alert.show();
                    txtPassword.selectAll();
                }else {
                    user.setPassword(txtPassword.getText());
                    userService.updateUserPassword(user);

                    Stage stage = new Stage();
                    try {
                        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/order.fxml"))));
                        stage.setResizable(false);
                        stage.setOnCloseRequest(closeEvent -> {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You might have unsaved changes. Do you want to exit?");
                            if (alert.showAndWait().get() == ButtonType.CANCEL) {
                                closeEvent.consume();
                            }
                        });
                        stage.show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Invalid Email or Password");
                alert.show();
            }
        }
    }
@FXML
void btnSendOTPOnAction(ActionEvent event) {
    String email = txtEmail.getText().trim();

    // Check if email is empty or invalid format
    if (email.isEmpty() || !isValidEmail(email)) {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Invalid Email!");
        alert.show();
        txtEmail.selectAll();
        return; // Exit early if email is invalid
    }

    // Now check if the email corresponds to a user account
    User user = userService.getUserByEmail(email);
    System.out.println("Fetched user: " + user); // Debug output
    if (user == null) {
        Alert alert = new Alert(Alert.AlertType.WARNING, "No account associated with this email.");
        alert.show();
        return; // Exit early if no account is found
    }

    // Generate and send OTP if all validations pass
    generatedOTP = generateOTP();
    System.out.println("Generated OTP: " + generatedOTP); // Debug output
    try {
        sendOTPViaEmail(email, generatedOTP);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "OTP has been sent to your email.");
        alert.show();
    } catch (MessagingException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to send OTP. Please try again.");
        alert.setContentText("Error: " + e.getMessage());
        alert.show();
    }

    // Optional: Make email field non-editable after sending OTP
    txtEmail.setEditable(false);
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

    @FXML
    void btnVerifyOTPOnAction(ActionEvent event) {
        String enteredOTP = txtOTP.getText();

        if (!enteredOTP.equals(generatedOTP)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid OTP. Please try again.");
            alert.show();
        }else {
            txtOTP.setEditable(false);
            btnLogin.setDisable(false);
        }
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
