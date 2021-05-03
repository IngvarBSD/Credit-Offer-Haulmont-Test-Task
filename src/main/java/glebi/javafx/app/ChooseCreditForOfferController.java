package glebi.javafx.app;

import glebi.helpers.CreditDao;
import glebi.helpers.CreditOfferDao;
import glebi.helpers.fxml.utils.ControllerDataTransfer;
import glebi.helpers.fxml.utils.FxmlFileManipulations;
import glebi.objects.Credit;
import glebi.objects.CreditOffer;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Этот класс копирует часть класса CreditsController связанную с таблицей, но этот контроллер нужен
 * для отдельного fxml для выбора кредита для кредитного предложения.
 */
public class ChooseCreditForOfferController implements Initializable {
    @FXML
    public TableView<Credit> creditsTable;

    CreditDao dao = new CreditDao();

    @Override
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // столбец с id кредита
        TableColumn<Credit, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        idColumn.setPrefWidth(250);

        // столбец с лимитом по кредиту
        TableColumn<Credit, BigDecimal> creditLimitColumn = new TableColumn<>("Лимит по кредиту");
        creditLimitColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCreditLimit()));
        creditLimitColumn.setPrefWidth(248);

        // столбец с процентной ставкой
        TableColumn<Credit, String> interestRateColumn = new TableColumn<>("Процентная ставка");
        interestRateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInterestRate().toString() + "%"));
        interestRateColumn.setPrefWidth(150);

        creditsTable.getColumns().addAll(idColumn, creditLimitColumn, interestRateColumn);

        updateTableItems();
    }

    private void updateTableItems() {
        ObservableList<Credit> observableList = FXCollections.observableArrayList(dao.getAll());
        creditsTable.getItems().clear();
        creditsTable.getItems().addAll(observableList);
    }

    @FXML
    public void btnChooseCreditAction(ActionEvent actionEvent) {
        CreditOfferDao creditOfferDao = new CreditOfferDao();
        FxmlFileManipulations fxmlManipul = new FxmlFileManipulations();

        Credit creditForCreditOffer;
        if ((creditForCreditOffer = creditsTable.getSelectionModel().getSelectedItem()) != null) {
            ControllerDataTransfer.creditId = creditForCreditOffer.getId();
            ControllerDataTransfer.creditLimit = creditForCreditOffer.getCreditLimit();
            ControllerDataTransfer.interestRate = creditForCreditOffer.getInterestRate();

            // Ввод суммы кредита и срока выплаты в месяцах с валидацией ввода.
            BigDecimal creditSum = inputCreditSumDialog(String.valueOf(creditForCreditOffer.getCreditLimit().doubleValue()));
            if (creditSum == null) return;
            int months = inputMonthsDialog();
            if (months == 0) return;

            // Занесение кредтного предложения и графика платежей в базу данных.
            CreditOffer creditOffer = new CreditOffer();
            creditOffer.setCreditSum(creditSum);
            creditOffer.setClient(ControllerDataTransfer.loadClientInfo());
            creditOffer.setCredit(ControllerDataTransfer.loadCreditInfo());
            creditOffer.setMonths(months);
            creditOfferDao.insert(creditOffer);
            creditOffer.insertPaymentScheduleToDB();

            // Переход к таблице кредитных предложений.
            Alert alert = new Alert(Alert.AlertType.NONE, "Кредитное предложение оформлено! График платежей доступен для просмотра.", ButtonType.CLOSE);
            alert.showAndWait();
            fxmlManipul.changeContentHBox("./fxml/credit_offer_table.fxml", creditsTable);
        } else {
            // pop up window
            Alert alert = new Alert(Alert.AlertType.NONE, "Чтобы присвоить кредитную опцию клиенту, нужно выбрать кредит из списка доступных.", ButtonType.CLOSE);
            alert.showAndWait();
        }
    }

    /**
     * Вызов диалогового окна для ввода суммы кредита с валидацией ввода.
     * @param creditLimit Лимит по кредиту для его дальнейшего отображения в окне.
     * @return Сумма кредита, готовая для установки в экземпляр CreditOffer.
     */
    private BigDecimal inputCreditSumDialog(String creditLimit) {
        Optional<String> result;
        boolean isCorrectSum = false;
        do {
            TextInputDialog inputDialog = new TextInputDialog(creditLimit);
            inputDialog.setHeaderText("Введите сумму кредита, не превышающую кредитный лимит (" + creditLimit + ").");
            inputDialog.setContentText("Введите сумму кредита для взятия:");
            result = inputDialog.showAndWait();

            // Если не нажата кнопка Cancel и ввод отличен от пустого, то спускаемся ниже
            if (result.isPresent() && !result.get().equals("")) {
                // Если было введено не число, то отловится исключение
                try {
                    double result_d = Double.parseDouble(result.get());
                    // Если введённая сумма в допустимых пределах кредитного лимита, то возвращаем её
                    if (result_d > 0 && result_d <= Double.parseDouble(creditLimit)) {
                        isCorrectSum = true;
                        return new BigDecimal(result_d);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Введите корректную сумму кредита.", ButtonType.CLOSE);
                        alert.showAndWait();
                    }
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Введите корректную сумму кредита.", ButtonType.CLOSE);
                    alert.showAndWait();
                }
            } else if (result.isPresent()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Введите сумму кредита.", ButtonType.CLOSE);
                alert.showAndWait();
            }
        } while (result.isPresent() && !isCorrectSum);

        // Если нажата кнопка Cancel, то вернётся пустой Optional<String>, значит цикл while
        // будет прерван. В таком случае возвращаем нулевое значение, чтобы выйти из процесса формирования
        // кредитного предложения в вызывающем методе.
        return null;
    }

    /**
     * Вызов диалогового окна для ввода срока выплаты кредита с валидацией ввода.
     * @return Целое число месяцев для выплаты кредита.
     */
    private int inputMonthsDialog() {
        Optional<String> result;
        boolean isCorrectNumber = false;
        do {
            TextInputDialog inputDialog = new TextInputDialog("12");
            inputDialog.setHeaderText("Введите срок для выплаты кредита (от 12 до 24 месяцев).");
            inputDialog.setContentText("Введите количество месяцев:");
            result = inputDialog.showAndWait();

            // Если не нажата кнопка Cancel и ввод отличен от пустого, то спускаемся ниже
            if (result.isPresent() && !result.get().equals("")) {
                // Если было введено не число, то отловится исключение
                try {
                    int result_i = Integer.parseInt(result.get());
                    // Если введённое число в допустимых пределах срока для выплаты, то возвращаем его
                    if (result_i >= 12 && result_i <= 24) {
                        isCorrectNumber = true;
                        return result_i;
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Введите корректное количество месяцев.", ButtonType.CLOSE);
                        alert.showAndWait();
                    }
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Введите корректное количество месяцев.", ButtonType.CLOSE);
                    alert.showAndWait();
                }
            } else if (result.isPresent()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Введите количество месяцев.", ButtonType.CLOSE);
                alert.showAndWait();
            }
        } while (result.isPresent() && !isCorrectNumber);

        // Если нажата кнопка Cancel, то вернётся пустой Optional<String>, значит цикл while
        // будет прерван. В таком случае возвращаем нулевое значение, чтобы выйти из процесса формирования
        // кредитного предложения в вызывающем методе.
        return 0;
    }
}
