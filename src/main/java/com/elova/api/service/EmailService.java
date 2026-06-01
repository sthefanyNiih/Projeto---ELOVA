package com.elova.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável pelo envio de e-mails transacionais.
 * Usado principalmente para recuperação de senha.
 * Módulo 1 - Auth.
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Envia o e-mail de recuperação de senha com o link contendo o token.
     *
     * @param destinatario E-mail do usuário
     * @param token        Token de reset gerado pelo JwtService
     */
    public void enviarEmailRecuperacao(String destinatario, String token) {
        String link = "http://localhost:3000/redefinir-senha?token=" + token;
        // Em produção, troque pelo domínio real do frontend

        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(destinatario);
        mensagem.setSubject("ELOVA — Recuperação de Senha");
        mensagem.setText(
                "Olá!\n\n" +
                        "Recebemos uma solicitação para redefinir a senha da sua conta ELOVA.\n\n" +
                        "Clique no link abaixo para criar uma nova senha:\n" +
                        link + "\n\n" +
                        "Este link é válido por 30 minutos.\n\n" +
                        "Se você não solicitou a recuperação de senha, ignore este e-mail. " +
                        "Sua senha permanecerá a mesma.\n\n" +
                        "— Equipe ELOVA"
        );

        mailSender.send(mensagem);
    }

    /**
     * Envia e-mail de confirmação após o cadastro bem-sucedido.
     */
    public void enviarEmailBoasVindas(String destinatario, String nome) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(destinatario);
        mensagem.setSubject("ELOVA — Bem-vindo(a)!");
        mensagem.setText(
                "Olá, " + nome + "!\n\n" +
                        "Sua conta no ELOVA foi criada com sucesso. " +
                        "Agora você pode começar a organizar seus estudos.\n\n" +
                        "Bons estudos!\n— Equipe ELOVA"
        );

        mailSender.send(mensagem);
    }
}
