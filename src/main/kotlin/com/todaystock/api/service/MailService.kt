package com.todaystock.api.service

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import jakarta.activation.DataSource
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.MimeMessageHelper

@Service
class MailService(
    private val mailSender: JavaMailSender
) {

    fun sendText(to: String, subject: String, body: String) {
        val message = SimpleMailMessage().apply {
            setTo(to)
            setSubject(subject)
            text = body
        }
        mailSender.send(message)
    }

    fun sendHtml(to: String, subject: String, htmlBody: String) {
        val mimeMessage: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, "UTF-8")
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(htmlBody, true) // true = HTML
        mailSender.send(mimeMessage)
    }

    fun sendWithAttachment(
        to: String,
        subject: String,
        body: String,
        html: Boolean = false,
        filename: String,
        dataSource: DataSource
    ) {
        val mimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(body, html)
        helper.addAttachment(filename, dataSource)
        mailSender.send(mimeMessage)
    }

    fun sendWithAttachment(
        to: String,
        subject: String,
        body: String,
        html: Boolean = false,
        file: MultipartFile
    ) {
        val mimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(body, html)
        helper.addAttachment(file.originalFilename ?: "attachment", file)
        mailSender.send(mimeMessage)
    }
}
