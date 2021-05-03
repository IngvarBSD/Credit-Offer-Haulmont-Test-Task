package glebi.javafx.app;

import glebi.helpers.CreditDao;
import glebi.helpers.fxml.utils.FxmlFileManipulations;
import glebi.objects.Credit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.math.BigDecimal;

public class InsertCreditFormController {
    @FXML
    public TextField creditLimitTextField;
    @FXML
    public TextField interestRateTextField;

    @FXML
    public void btnAddAction(ActionEvent actionEvent) {
        CreditDao creditDao = new CreditDao();
        // Экземпляр кредита заполняется значениями, которые были введены в текстовые поля
        Credit creditToInsert = new Credit();
        try {
            creditToInsert.setCreditLimit(new BigDecimal(creditLimitTextField.getText()));
            creditToInsert.setInterestRate(new BigDecimal(interestRateTextField.getText()));

            // Валидация ввода
            double creditLimit = creditToInsert.getCreditLimit().doubleValue();
            double interestRate = creditToInsert.getInterestRate().doubleValue();
            if (interestRate >= 100 || interestRate <= 0 || creditLimit <= 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Введите корректное число!", ButtonType.CLOSE);
                alert.showAndWait();
                return;
            }

            creditDao.insert(creditToInsert);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Введите число в подходящем формате!", ButtonType.CLOSE);
            alert.showAndWait();
            return;
        }

        // загрузка таблицы кредитов обратно
        FxmlFileManipulations fxmlManipul = new FxmlFileManipulations();
        fxmlManipul.changeContentHBox("./fxml/credits_table.fxml", creditLimitTextField);
    }

    @FXML
    public void btnCancelAction(ActionEvent actionEvent) {
        FxmlFileManipulations fxmlManipul = new FxmlFileManipulations();
        fxmlManipul.changeContentHBox("./fxml/credits_table.fxml", creditLimitTextField);
    }
}
