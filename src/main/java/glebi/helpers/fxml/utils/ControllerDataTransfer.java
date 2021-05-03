package glebi.helpers.fxml.utils;

import glebi.objects.Client;
import glebi.objects.Credit;
import glebi.objects.CreditOffer;

import java.math.BigDecimal;

public class ControllerDataTransfer {
    // поля для экземпляра Client
    public static String clientId;
    public static String fio;
    public static String telephoneNumber;
    public static String passportNumber;
    public static String email;

    // поля для экземпляра Credit
    public static String creditId;
    public static BigDecimal creditLimit;
    public static BigDecimal interestRate;

    // uuid CreditOffer для отображения графика платежей
    // остальные поля не рассматриваются, так как не нужны
    public static String creditOfferId;

    /**
     * Загрузить сохранённую информацию о клиенте.
     * @return Экземпляр Client.
     */
    public static Client loadClientInfo() {
        Client client = new Client();
        client.setId(clientId);
        client.setFio(fio);
        client.setEmail(email);
        client.setPassportNumber(passportNumber);
        client.setTelephoneNumber(telephoneNumber);
        return client;
    }

    /**
     * Загрузить сохранённую информацию о кредите.
     * @return Экземпляр Credit.
     */
    public static Credit loadCreditInfo() {
        Credit credit = new Credit();
        credit.setId(creditId);
        credit.setCreditLimit(creditLimit);
        credit.setInterestRate(interestRate);
        return credit;
    }

    /**
     * Загружается экзмепляр кредитного предложения.
     * Он хранит только ID, чтобы просто отобразить график платежей.
     * @return Экземпляр CreditOffer.
     */
    public static CreditOffer loadCreditOfferInfo() {
        CreditOffer creditOffer = new CreditOffer();
        creditOffer.setId(creditOfferId);
        return creditOffer;
    }
}
