package com.test.listener;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "mail")
public class MailQueueListener {

    @Autowired
    JavaMailSender sender;

    @Value("${spring.mail.username}")
    String username;



    @RabbitHandler
    public void sendMailMessage(Map<String,Object> data){
        String email = (String) data.get("email");
        Integer code = (Integer) data.get("code");
        String type = (String) data.get("type");
        SimpleMailMessage message = switch (type){
            case "register" ->
                    createMessage("欢迎注册我们的网站",
                            "你的邮件注册验证码为: "+code+"有效时间3分钟",
                            email);
            case "reset" ->
                    createMessage("你的密码重置邮件",
                    "你正在进行重置密码操作"+code+"有效时间3分钟",
                            email);
            case "modify" ->
                    createMessage("您的邮件-修改验证邮件",
                            "您好，您正在修改您的邮件地址，验证码" + code +"有效时间三分钟",
                            email);
            default -> null;
        };
        if(message == null)return;
        sender.send(message);

    }

    private SimpleMailMessage createMessage(String title, String content, String email){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(email);
        message.setFrom(username);
        return message;
    }

}
