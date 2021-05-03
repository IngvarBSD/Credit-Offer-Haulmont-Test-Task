package glebi.javafx.app;

import glebi.helpers.ClientDao;
import glebi.helpers.fxml.utils.FxmlFileManipulations;
import glebi.objects.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class InsertClientFormController {
    @FXML
    TextField fioTextField;
    @FXML
    TextField telephoneNumberTextField;
    @FXML
    TextField emailTextField;
    @FXML
    TextField passportNumberTextField;

    @FXML
    public void btnAddAction(ActionEvent actionEvent) {
        ClientDao clientDao = new ClientDao();
        // Экземпляр клиента заполняется значениями, которые были введены в текстовые поля
        Client clientToInsert = new Client();
        clientToInsert.setFio(fioTextField.getText());
        clientToInsert.setTelephoneNumber(telephoneNumberTextField.getText());
        clientToInsert.setEmail(emailTextField.getText());
        clientToInsert.setPassportNumber(passportNumberTextField.getText());

        clientDao.insert(clientToInsert);

        // загрузка таблицы клиентов обратно
        FxmlFileManipulations fxmlManipul = new FxmlFileManipulations();
        fxmlManipul.changeContentHBox("./fxml/clients_table.fxml", fioTextField);
    }

    @FXML
    public void btnCancelAction(ActionEvent actionEvent) {
        FxmlFileManipulations fxmlManipul = new FxmlFileManipulations();
        fxmlManipul.changeContentHBox("./fxml/clients_table.fxml", fioTextField);
    }
}
