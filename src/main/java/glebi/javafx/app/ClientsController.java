package glebi.javafx.app;

import glebi.helpers.ClientDao;
import glebi.helpers.fxml.utils.ControllerDataTransfer;
import glebi.helpers.fxml.utils.FxmlFileManipulations;
import glebi.objects.Client;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ResourceBundle;

public class ClientsController implements Initializable {
    @FXML
    TableView<Client> clientsTable;

    ClientDao dao = new ClientDao();

    // инициализация таблицы при первой загрузке fxml с этим контроллером
    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // столбец с id клиента
        TableColumn<Client, String> idColumn = new TableColumn<>("ID");
        // для каждого столбца устанавливается фабрика ячеек
        // установка фабрики без лямбда выражения не работает должным образом: idColumn.setCellValueFactory(new PropertyValueFactory<>("fio"));
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        idColumn.setPrefWidth(250);

        // столбец с fio клиента
        TableColumn<Client, String> fioColumn = new TableColumn<>("ФИО");
        fioColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFio()));
        fioColumn.setPrefWidth(248);

        // столбец с telephoneNumber клиента
        TableColumn<Client, String> telephoneNumberColumn = new TableColumn<>("Номер телефона");
        telephoneNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTelephoneNumber()));
        telephoneNumberColumn.setPrefWidth(150);

        // столбец с email клиента
        TableColumn<Client, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        emailColumn.setPrefWidth(100);

        // столбец с passportNumber клиента
        TableColumn<Client, String> passportNumberColumn = new TableColumn<>("Номер пасспорта");
        passportNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPassportNumber()));
        passportNumberColumn.setPrefWidth(146);

        clientsTable.getColumns().addAll(idColumn, fioColumn, telephoneNumberColumn, emailColumn, passportNumberColumn);

        updateTableItems();
    }

    /**
     * Метод обновляет список объектов используемых таблицей, но минус в том, что
     * приходится выполнять запрос на получение этих объектов из базы данных (при вызове dao.getAll()).
     */
    private void updateTableItems() {
        // ObservableList составляется из списка клиентов и используется таблицей
        ObservableList<Client> observableList = FXCollections.observableArrayList(dao.getAll());
        clientsTable.getItems().clear();
        clientsTable.getItems().addAll(observableList); // bug(?) визуально таблица не очищается, если после clear() сразу добавить новый список через addAll()
    }

    @FXML
    public void btnInsertAction(ActionEvent actionEvent) {
        FxmlFileManipulations fxmlManipul = new FxmlFileManipulations();
        fxmlManipul.changeContentHBox("./fxml/insert_client_form.fxml", clientsTable);
    }

    @FXML
    public void btnDeleteAction(ActionEvent actionEvent) {
        Client clientToDelete;
        if ((clientToDelete = clientsTable.getSelectionModel().getSelectedItem()) != null) {
            try {
                dao.delete(clientToDelete);
            } catch (SQLIntegrityConstraintViolationException e) {
                return;
            }
            clientsTable.getItems().remove(clientToDelete);

            // pop up window
            Alert alert = new Alert(Alert.AlertType.NONE, "Запись клиента удалена!", ButtonType.CLOSE);
            alert.showAndWait();
        } else {
            // pop up window
            Alert alert = new Alert(Alert.AlertType.NONE, "Выберите клиента из таблицы для удаления.", ButtonType.CLOSE);
            alert.showAndWait();
        }
    }

    @FXML
    public void btnUpdateAction(ActionEvent actionEvent) {
        FxmlFileManipulations fxmlManipul = new FxmlFileManipulations();
        Client clientToUpdate;
        if ((clientToUpdate = clientsTable.getSelectionModel().getSelectedItem()) != null) {
            ControllerDataTransfer.clientId = clientToUpdate.getId();
            ControllerDataTransfer.fio = clientToUpdate.getFio();
            ControllerDataTransfer.telephoneNumber = clientToUpdate.getTelephoneNumber();
            ControllerDataTransfer.email = clientToUpdate.getEmail();
            ControllerDataTransfer.passportNumber = clientToUpdate.getPassportNumber();
            fxmlManipul.changeContentHBox("./fxml/update_client_form.fxml", clientsTable);
        } else {
            // pop up window
            Alert alert = new Alert(Alert.AlertType.NONE, "Выберите клиента из таблицы для обновления.", ButtonType.CLOSE);
            alert.showAndWait();
        }
    }

    public void btnFormCreditOfferAction(ActionEvent actionEvent) {
        FxmlFileManipulations fxmlManipul = new FxmlFileManipulations();
        Client clientForCreditOffer;
        if ((clientForCreditOffer = clientsTable.getSelectionModel().getSelectedItem()) != null) {
            ControllerDataTransfer.clientId = clientForCreditOffer.getId();
            ControllerDataTransfer.fio = clientForCreditOffer.getFio();
            ControllerDataTransfer.telephoneNumber = clientForCreditOffer.getTelephoneNumber();
            ControllerDataTransfer.email = clientForCreditOffer.getEmail();
            ControllerDataTransfer.passportNumber = clientForCreditOffer.getPassportNumber();
            fxmlManipul.changeContentHBox("./fxml/choose_credit_for_offer.fxml", clientsTable);
        } else {
            // pop up window
            Alert alert = new Alert(Alert.AlertType.NONE, "Для формирования кредитного предложения требуется выбрать запись клиента, на которого будет оформлен кредит.", ButtonType.CLOSE);
            alert.showAndWait();
        }
    }
}
