package glebi.javafx.app;

/*
    @author Ежов Глеб
    При использовании сборщиков проектов, например Maven, есть проблема с наследованием glebi.javafx.app.Main
    класса от класса Application JavaFX библиотеки. Приходится инициировать выполнение программы в другом классе
    (в данном случае glebi.javafx.app.AppManager, он же и наследуется от Application).
 */
public class Main {
    public static void main(String[] args) {
        AppManager appManager = new AppManager();
        appManager.init(args);
    }
}
