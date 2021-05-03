package glebi.helpers.fxml.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class FxmlFileManipulations {
    /**
     * Метод заменяет содержимое контент-бокса (HBox, содержащий другой fxml лэйаут).
     * @param resourceName Путь до ресурса. В скомпилированном Maven проекте поиск ведётся от папки classes.
     * @param subElement FXML-элемент от которого будет произведено "поднятие" до contentHBox'а.
     */
    public void changeContentHBox(String resourceName, Node subElement)
    {
        // получение contentHBox для загрузки в него лэйаута с формой
        HBox contentHBox = (HBox) subElement.getParent().getParent();

        contentHBox.getChildren().clear();
        try {
            // загрузка fxml файла с таблицей клиентов в контент-бокс
            contentHBox.getChildren().add(FXMLLoader.load(getClass().getClassLoader().getResource(resourceName)));
        }
        catch (IOException e) {
            e.getMessage();
            e.printStackTrace();
        }
    }
}
