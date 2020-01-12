package com.vertex.services;

import com.vertex.config.MessageConfig;
import com.vertex.config.MessageLog;
import com.vertex.dataObjects.CryptoEmail;
import com.vertex.dataObjects.MailFormat;
import com.vertex.utils.FileUtil;
import com.vertex.utils.HtmlGenerator;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.ext.mail.MailAttachment;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.web.RoutingContext;
import java.util.function.Function;

public class NotificationsServices {
    public static void sendDynamicMail(Logger logger, String emailAddress, String fileName, Vertx vertx, RoutingContext routingContext, Object... args) {
        Function func = result -> {
            MailFormat mailFormat = new MailFormat();
            if (((JsonObject)result).getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM) {
                try {
                    String htmlString = ((JsonObject)result).getString("content");
                    String content = null;
                    content = String.format(htmlString, args);
                    mailFormat.setContent(content);
                    mailFormat.setSubject("SMP Support");
                    mailFormat.setTo(emailAddress);
                    MailMessage mailMessage = CryptoEmail.getMaileMessage(mailFormat, null);
                    CryptoEmail.sendEmail(vertx, mailMessage, routingContext);
                } catch (Exception e) {
                    if (routingContext != null) {
                        MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.SENTENCES_ERROR, "Failed to send email due to Exception: " + e.getMessage(), logger);
                    } else {
                        MessageLog.logMessage("Failed to send email due to Exception: " + e.getMessage(), logger);
                    }
                }
            } else {
                MessageLog.logMessage("Failed to send mail", logger);
            }
            return null;
        };
        FileUtil.getFileContent(func, fileName, vertx);
    }

    public static void sendReportsMail(Logger logger, String emailAddress, JsonArray array, Vertx vertx, String fileName, RoutingContext routingContext) {
        Function func = result -> {
            MailFormat mailFormat = new MailFormat();
            if (((JsonObject)result).getInteger("response_code").intValue() < MessageConfig.ERROR_CODE_FROM) {
                try {
                    String htmlString = ((JsonObject)result).getString("content");
                    String html = "";
                    if (fileName.equals("report_punch_clock.html")) {
                        html = HtmlGenerator.userClockReportTable(array);
                    } else {
                        html = HtmlGenerator.userTaskReportTable(array);
                    }
                    html = String.format(htmlString, new Object[] { html });
                    mailFormat.setSubject("Report");
                    mailFormat.setTo(emailAddress);
                    FileUtil.overWriteFileContents("/home/ec2-user/fileName", html);
                    CryptoEmail.generatePDFFromHTML("fileName");
                    Buffer buffer = vertx.fileSystem().readFileBlocking("/home/ec2-user/test.pdf");
                    MailAttachment attachment = CryptoEmail.createAttachment(buffer, "application/pdf", "Report");
                    MailMessage mailMessage = CryptoEmail.getMaileMessage(mailFormat, attachment);
                    CryptoEmail.sendEmail(vertx, mailMessage, routingContext);
                } catch (Exception e) {
                    MessageLog.sendErrorCode(routingContext, MessageConfig.MessageKey.REPORT_ERROR, "Failed sending report due to this Exception: " + e.getMessage(), logger);
                }
            } else {
                MessageLog.logMessage("Failed to send mail", logger);
            }
            return null;
        };
        FileUtil.getFileContent(func, fileName, vertx);
    }
}
