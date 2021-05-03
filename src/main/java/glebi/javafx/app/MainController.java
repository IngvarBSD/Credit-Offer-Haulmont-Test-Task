package glebi.javafx.app;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class MainController {
    @FXML
    HBox contentHBox;

    @FXML
    public void btnClientsAction(ActionEvent actionEvent) {
        contentHBox.getChildren().clear();
        try {
            // загрузка fxml файла с таблицей клиентов в контент-бокс
            contentHBox.getChildren().add(FXMLLoader.load(getClass().getClassLoader().getResource("./fxml/clients_table.fxml")));
        }
        catch (IOException e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

    @FXML
    public void btnCreditsAction(ActionEvent actionEvent) {
        contentHBox.getChildren().clear();
        try {
            // загрузка fxml файла с таблицей кредитов в контент-бокс
            contentHBox.getChildren().add(FXMLLoader.load(getClass().getClassLoader().getResource("./fxml/credits_table.fxml")));
        }
        catch (IOException e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

    @FXML
    public void btnOffersAction(ActionEvent actionEvent) {
        contentHBox.getChildren().clear();
        try {
            // загрузка fxml файла с таблицей кредитных предложений в контент-бокс
            contentHBox.getChildren().add(FXMLLoader.load(getClass().getClassLoader().getResource("./fxml/credit_offer_table.fxml")));
        }
        catch (IOException e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

    @FXML
    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    @FXML
    public void btnAboutProgram(ActionEvent actionEvent) {
        String aboutMessage = "Тестовое задание Junior Java Developer от Haulmont.\nСтэк: Java 11 + JavaFX 11 + HSQLDB 2.5.1.\n" +
                "ФИО разработчика: Ежов Глеб Владимирович.\n\n" +
                "Три кнопки сверху управляют отображением таблиц клиентов, кредитов и кредитных предложений.\n" +
                "При открытии одной из таблиц внизу появляются кнопки для управления сущностями данной таблицы.\n\n" +
                "Таблица \"Клиенты\": добавление, удаление, редактирование записи клиента, а также возможность " +
                "сформировать кредитное предложение для выбранного клиента.\n\n" +
                "Таблица \"Кредиты\": добавление, удаление, редактирование записи кредита. Таблица кредитов хранит в " +
                "себе кредитные опции, которые выбираются в ходе формирования кредитного предложения. На основе содержимого " +
                "записи кредита формируется график платежей.\n\n" +
                "Таблица \"Кредитные предложения\": просмотр графика платежей по выбранному офферу, а также возможность удалить " +
                "кредитное предложение с каскадным удалением графика платежей.";
        Alert alert = new Alert(Alert.AlertType.INFORMATION, aboutMessage, ButtonType.CLOSE);
        alert.setTitle("О программе");
        alert.setHeaderText("О программе");
        alert.showAndWait();
    }
}
