package glebi.javafx.app;

import glebi.helpers.CreditDao;
import glebi.helpers.fxml.utils.ControllerDataTransfer;
import glebi.helpers.fxml.utils.FxmlFileManipulations;
import glebi.objects.Credit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class UpdateCreditFormController implements Initializable {
    @FXML
    public TextField uuidTextField;
    @FXML
    public TextField creditLimitTextField;
    @FXML
    public TextField interestRateTextField;

    // текстбоксы получают значения выбранного кредита для понятного обновления информации
    @Override
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Credit creditInfo = ControllerDataTransfer.loadCreditInfo();
        uuidTextField.setText(creditInfo.getId());
        creditLimitTextField.setText(String.valueOf(creditInfo.getCreditLimit().doubleValue()));
        interestRateTextField.setText(String.valueOf(creditInfo.getInterestRate().doubleValue()));
    }

    @FXML
    public void btnUpdateAction(ActionEvent actionEvent) {
        CreditDao creditDao = new CreditDao();
        // Заполнение обновлённого экземпляра Credit
        Credit updatedCredit = new Credit();
        try {
            updatedCredit.setId(uuidTextField.getText());
            updatedCredit.setCreditLimit(new BigDecimal(creditLimitTextField.getText()));
            updatedCredit.setInterestRate(new BigDecimal(interestRateTextField.getText()));

            // Валидация ввода
            double creditLimit = updatedCredit.getCreditLimit().doubleValue();
            double interestRate = updatedCredit.getInterestRate().doubleValue();
            if (interestRate >= 100 || interestRate <= 0 || creditLimit <= 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Введите корректное число!", ButtonType.CLOSE);
                alert.showAndWait();
                return;
            }

            creditDao.update(updatedCredit);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Введите число в подходящем формате!", ButtonType.CLOSE);
            alert.showAndWait();
            return;
        }

        FxmlFileManipulations fxmlManipul = new FxmlFileManipulations();
        fxmlManipul.changeContentHBox("./fxml/credits_table.fxml", uuidTextField);
    }

    @FXML
    public void btnCancelAction(ActionEvent actionEvent) {
        FxmlFileManipulations fxmlManipul = new FxmlFileManipulations();
        fxmlManipul.changeContentHBox("./fxml/credits_table.fxml", uuidTextField);
    }
}
