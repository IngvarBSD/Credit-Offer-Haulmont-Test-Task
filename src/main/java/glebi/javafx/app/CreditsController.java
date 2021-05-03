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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.ResourceBundle;

public class CreditsController implements Initializable {
    @FXML
    public TableView<Credit> creditsTable;

    CreditDao dao = new CreditDao();

    // инициализаця таблицы со списком кредитов
    @FXML
    @Override
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

    /**
     * Метод обновляет список объектов используемых таблицей, но минус в том, что
     * приходится выполнять запрос на получение этих объектов из базы данных (при вызове dao.getAll()).
     */
    private void updateTableItems() {
        ObservableList<Credit> observableList = FXCollections.observableArrayList(dao.getAll());
        creditsTable.getItems().clear();
        creditsTable.getItems().addAll(observableList);
    }

    @FXML
    public void btnInsertAction(ActionEvent actionEvent) {
        FxmlFileManipulations fxmlManipul = new FxmlFileManipulations();
        fxmlManipul.changeContentHBox("./fxml/insert_credit_form.fxml", creditsTable);
    }

    @FXML
    public void btnDeleteAction(ActionEvent actionEvent) {
        Credit creditToDelete;
        if ((creditToDelete = creditsTable.getSelectionModel().getSelectedItem()) != null) {
            try {
                dao.delete(creditToDelete);
            } catch (SQLIntegrityConstraintViolationException e) {
                return;
            }
            creditsTable.getItems().remove(creditToDelete);

            Alert alert = new Alert(Alert.AlertType.NONE, "Запись кредита удалена!", ButtonType.CLOSE);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.NONE, "Выберите кредит из таблицы для удаления.", ButtonType.CLOSE);
            alert.showAndWait();
        }
    }

    @FXML
    public void btnUpdateAction(ActionEvent actionEvent) {
        CreditOfferDao creditOfferDao = new CreditOfferDao();
        FxmlFileManipulations fxmlManipul = new FxmlFileManipulations();
        Credit creditToUpdate;
        if ((creditToUpdate = creditsTable.getSelectionModel().getSelectedItem()) != null) {
            ControllerDataTransfer.creditId = creditToUpdate.getId();
            ControllerDataTransfer.creditLimit = creditToUpdate.getCreditLimit();
            ControllerDataTransfer.interestRate = creditToUpdate.getInterestRate();

            // Если изменяемый кредит используется в кредитном предложении, то отменить изменение.
            // Дефолтный рестрикт базы данных почему-то не работает с обновлением, хотя с удалением всё хорошо,
            // поэтому пришлось тут проверку сделать.
            List<CreditOffer> offers = creditOfferDao.getAll();
            for (CreditOffer offer : offers) {
                if (offer.getCredit().equals(creditToUpdate)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Нельзя изменить данную запись, так как она связана с кредитным предложением!", ButtonType.CLOSE);
                    alert.showAndWait();
                    return;
                }
            }

            fxmlManipul.changeContentHBox("./fxml/update_credit_form.fxml", creditsTable);
        } else {
            // pop up window
            Alert alert = new Alert(Alert.AlertType.NONE, "Выберите кредит из таблицы для обновления.", ButtonType.CLOSE);
            alert.showAndWait();
        }
    }
}
