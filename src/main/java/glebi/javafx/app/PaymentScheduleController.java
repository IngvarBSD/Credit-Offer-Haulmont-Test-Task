package glebi.javafx.app;

import glebi.helpers.fxml.utils.ControllerDataTransfer;
import glebi.objects.CreditOffer;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class PaymentScheduleController implements Initializable {
    @FXML
    public TableView<CreditOffer.PaymentSchedule> paymentScheduleTable;

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // столбец с датой платежа
        TableColumn<CreditOffer.PaymentSchedule, String> dateColumn = new TableColumn<>("Дата платежа");
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPaymentDate().toString()));
        dateColumn.setPrefWidth(150);

        // столбец с суммой платежа
        TableColumn<CreditOffer.PaymentSchedule, BigDecimal> paymentSumColumn = new TableColumn<>("Сумма платежа");
        paymentSumColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPaymentSum()));
        paymentSumColumn.setPrefWidth(200);

        // столбец с суммой гашения тела кредита
        TableColumn<CreditOffer.PaymentSchedule, BigDecimal> creditRepaymentColumn = new TableColumn<>("На гашение кредита");
        creditRepaymentColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCreditRepayment()));
        creditRepaymentColumn.setPrefWidth(200);

        // столбец с суммой гашения процентов
        TableColumn<CreditOffer.PaymentSchedule, BigDecimal> interestRepaymentColumn = new TableColumn<>("На гашение процентов");
        interestRepaymentColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getInterestRepayment()));
        interestRepaymentColumn.setPrefWidth(200);

        paymentScheduleTable.getColumns().addAll(dateColumn, paymentSumColumn, creditRepaymentColumn, interestRepaymentColumn);

        updateTableItems();
    }

    /**
     * Метод обновляет список объектов используемых таблицей, но минус в том, что
     * приходится выполнять запрос на получение этих объектов из базы данных (при вызове dao.getAll()).
     */
    private void updateTableItems() {
        ObservableList<CreditOffer.PaymentSchedule> observableList = FXCollections.observableArrayList(ControllerDataTransfer.loadCreditOfferInfo().getPaymentScheduleList());
        paymentScheduleTable.getItems().clear();
        paymentScheduleTable.getItems().addAll(observableList);
    }
}
