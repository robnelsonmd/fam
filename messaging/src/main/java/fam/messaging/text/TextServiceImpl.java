package fam.messaging.text;

import fam.core.util.StringUtil;
import fam.messaging.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextServiceImpl implements TextService {
    private static final Logger LOG = LoggerFactory.getLogger(TextServiceImpl.class);

    private final EmailService emailService;

    public TextServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public String getTextAddress(CellCarrier cellCarrier, String cellNumber) {
        if (StringUtil.isEmptyString(cellNumber)) {
            return null;
        }

        cellNumber = normalizeCellNumber(cellNumber);

        switch (cellCarrier) {
            case ATT:
                return String.format("%s@txt.att.net",cellNumber);
            case VERIZON:
                return String.format("%s@vtext.com",cellNumber);
            case SPRINT:
                return String.format("%s@messaging.sprintpcs.com",cellNumber);
            case TMOBILE:
                return String.format("%s@tmomail.net",cellNumber);
            default:
                throw new IllegalArgumentException("Invalid cell carrier: " + cellCarrier);
        }
    }

    @Override
    public void sendText(String subject, String text, String to) {
        sendText(subject, text, to, null);
    }

    @Override
    public void sendText(String subject, String text, String to, String[] bcc) {
        emailService.sendEmail(subject, text, to, bcc);
    }

    private static String normalizeCellNumber(String cellNumber) {
        return cellNumber.replaceAll("[\\(\\)\\-\\. ]*", "");
    }
}
