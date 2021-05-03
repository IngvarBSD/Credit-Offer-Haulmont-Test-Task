package glebi.javafx.app;

import glebi.helpers.ClientDao;
import glebi.helpers.fxml.utils.ControllerDataTransfer;
import glebi.helpers.fxml.utils.FxmlFileManipulations;
import glebi.objects.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateClientFormController implements Initializable {
    @FXML
    public TextField uuidTextField;
    @FXML
    public TextField fioTextField;
    @FXML
    public TextField telephoneNumberTextField;
    @FXML
    public TextField emailTextField;
    @FXML
    public TextField passportNumberTextField;

    // текстбоксы получают значения выбранного клиента для понятного обновления информации
    @Override
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Client clientInfo = ControllerDataTransfer.loadClientInfo();
        uuidTextField.setText(clientInfo.getId());
        fioTextField.setText(clientInfo.getFio());
        telephoneNumberTextField.setText(clientInfo.getTelephoneNumber());
        emailTextField.setText(clientInfo.getEmail());
        passportNumberTextField.setText(clientInfo.getPassportNumber());
    }

    @FXML
    public void btnUpdateAction(ActionEvent actionEvent) {
        ClientDao clientDao = new ClientDao();
        // Заполнение обновлённого экземпляра клиента
        Client updatedClient = new Client();
        updatedClient.setId(uuidTextField.getText());
        updatedClient.setFio(fioTextField.getText());
        updatedClient.setTelephoneNumber(telephoneNumberTextField.getText());
        updatedClient.setEmail(emailTextField.getText());
        updatedClient.setPassportNumber(passportNumberTextField.getText());

        clientDao.update(updatedClient);

        // загрузка таблицы клиентов обратно
        FxmlFileManipulations fxmlManipul = new FxmlFileManipulations();
        fxmlManipul.changeContentHBox("./fxml/clients_table.fxml", uuidTextField);
    }

    @FXML
    public void btnCancelAction(ActionEvent actionEvent) {
        FxmlFileManipulations fxmlManipul = new FxmlFileManipulations();
        fxmlManipul.changeContentHBox("./fxml/clients_table.fxml", uuidTextField);
    }
}
