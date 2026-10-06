package com.texflow.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean enviarSenhaNova(String paraEmail, String nomeDestinatario, String senhaNova) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, "UTF-8");
            helper.setFrom(remetente, "TexFlow");
            helper.setTo(paraEmail);
            helper.setSubject("Sua nova senha - TexFlow");
            String html = "<p>Oi, " + nomeDestinatario + "!</p>"
                    + "<p>Sua senha do TexFlow foi redefinida. Sua nova senha e:</p>"
                    + "<p style=\"font-size:24px;font-weight:bold;letter-spacing:2px;\">" + senhaNova + "</p>"
                    + "<p>Use ela pra entrar e, se quiser, troque por outra depois.</p>";
            helper.setText(html, true);
            mailSender.send(mensagem);
            return true;
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de nova senha: {} - {}",
                    e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }
}
