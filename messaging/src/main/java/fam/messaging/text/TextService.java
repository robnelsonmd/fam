package fam.messaging.text;

public interface TextService {
    String getTextAddress(CellCarrier cellCarrier, String cellNumber);
    void sendText(String subject, String text, String to);
    void sendText(String subject, String text, String to, String[] bcc);
}
