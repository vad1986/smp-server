package com.vertex.dataObjects;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.vertex.config.MessageConfig;
import com.vertex.config.MessageLog;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mail.MailAttachment;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.web.RoutingContext;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CryptoEmail {
    private static Logger logger = LoggerFactory.getLogger(CryptoEmail.class);

    public static void sendEmail(Vertx vertx, MailMessage message, RoutingContext routingContext) throws Exception {
        try {
            System.out.println("BEFORE SENDING MAIL" + message
                    .toJson().toString());
            getClient(vertx).sendMail(message, result -> {
                if (result.succeeded()) {
                    System.out.println("Sent mail");
                    if (routingContext != null) {
                        MessageLog.sendMessageCode(routingContext, MessageConfig.MessageKey.REPORT, "Successfully sent email to " + (String)message.getTo().get(0), logger);
                    } else {
                        MessageLog.logMessage("Successfully sent email to " + (String)message.getTo().get(0), logger);
                    }
                } else if (routingContext != null) {
                    MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.REPORT_ERROR, "Failed sending email to " + (String)message.getTo().get(0) + " client result did not succeed due to " + result.cause(), logger);
                } else {
                    MessageLog.logMessage("Failed sending email to " + (String)message.getTo().get(0), logger);
                }
            });
        } catch (Exception e) {
            throw new Exception("Failed sending email due to :" + e.getMessage());
        }
    }

    public static MailAttachment createAttachment(Buffer buffer, String type, String name) {
        MailAttachment attachment = (new MailAttachment()).setData(buffer).setContentType(type).setName(name).setDisposition("inline");
        return attachment;
    }

    public static MailMessage getMaileMessage(MailFormat mailFormat, MailAttachment mailAttachment) {
        MailMessage message = new MailMessage();
        message.setFrom(CryptoEmailConfig.USER);
        message.setTo(mailFormat.getTo());
        if (mailFormat.getCc() != null)
            message.setCc(mailFormat.getCc());
        message.setSubject(mailFormat.getSubject());
        if (mailFormat.getContent() != null)
            message.setHtml(mailFormat.getContent());
        if (mailAttachment != null)
            message.setAttachment(mailAttachment);
        return message;
    }

    public static Document generatePDFFromHTML(String filename) throws IOException, DocumentException {
        Document document = new Document();
        Paragraph paragraph1 = new Paragraph("This is SettingPdfAttributesExample.pdf");
        document.open();
        document.add(paragraph1);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("test.pdf"));
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new FileInputStream(filename));
        return document;
    }

    private static MailClient getClient(Vertx vertx) {
        MailConfig config = new CryptoEmailConfig();
        MailClient mailClient = MailClient.createShared(vertx, config, "exampleclient");
        return mailClient;
    }
}
