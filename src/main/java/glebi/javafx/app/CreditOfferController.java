package glebi.javafx.app;

import glebi.helpers.CreditOfferDao;
import glebi.helpers.fxml.utils.ControllerDataTransfer;
import glebi.helpers.fxml.utils.FxmlFileManipulations;
import glebi.objects.CreditOffer;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class CreditOfferController implements Initializable {
    @FXML
    public TableView<CreditOffer> creditOfferTable;

    CreditOfferDao dao = new CreditOfferDao();

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // столбец с id кредитного предложения
        TableColumn<CreditOffer, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        idColumn.setPrefWidth(250);

        // столбец с суммой кредита
        TableColumn<CreditOffer, BigDecimal> creditSumColumn = new TableColumn<>("Сумма кредита");
        creditSumColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCreditSum()));
        creditSumColumn.setPrefWidth(248);

        // столбец с процентной ставкой
        TableColumn<CreditOffer, String> creditInterestRateColumn = new TableColumn<>("Процентная ставка");
        creditInterestRateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCredit().getInterestRate().toString() + "%"));
        creditInterestRateColumn.setPrefWidth(150);

        // столбец с фио клиента
        TableColumn<CreditOffer, String> clientFioColumn = new TableColumn<>("ФИО Клиента");
        clientFioColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClient().getFio()));
        clientFioColumn.setPrefWidth(250);

        creditOfferTable.getColumns().addAll(idColumn, creditSumColumn, creditInterestRateColumn, clientFioColumn);

        updateTableItems();
    }

    /**
     * Метод обновляет список объектов используемых таблицей, но минус в том, что
     * приходится выполнять запрос на получение этих объектов из базы данных (при вызове dao.getAll()).
     */
    private void updateTableItems() {
        ObservableList<CreditOffer> observableList = FXCollections.observableArrayList(dao.getAll());
        creditOfferTable.getItems().clear();
        creditOfferTable.getItems().addAll(observableList);
    }

    @FXML
    public void btnPaymentScheduleAction(ActionEvent actionEvent) {
        CreditOffer offer;
        if ((offer = creditOfferTable.getSelectionModel().getSelectedItem()) != null) {
            ControllerDataTransfer.creditOfferId = offer.getId();
            FxmlFileManipulations fxmlManipul = new FxmlFileManipulations();
            fxmlManipul.changeContentHBox("./fxml/payment_schedule_table.fxml", creditOfferTable);
        } else {
            Alert alert = new Alert(Alert.AlertType.NONE, "Выберите кредитное предложение для отображения графика платежей.", ButtonType.CLOSE);
            alert.showAndWait();
        }
    }

    @FXML
    public void btnDeleteAction(ActionEvent actionEvent) {
        CreditOffer offerToDelete;
        if ((offerToDelete = creditOfferTable.getSelectionModel().getSelectedItem()) != null) {
            creditOfferTable.getItems().remove(offerToDelete);
            dao.delete(offerToDelete);

            Alert alert = new Alert(Alert.AlertType.NONE, "Запись кредитного предложения удалена!", ButtonType.CLOSE);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.NONE, "Выберите кредитное предложение из таблицы для удаления.", ButtonType.CLOSE);
            alert.showAndWait();
        }
    }
}
