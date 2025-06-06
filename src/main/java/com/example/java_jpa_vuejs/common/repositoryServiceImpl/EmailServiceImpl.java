package com.example.java_jpa_vuejs.common.repositoryServiceImpl;

import jakarta.mail.Message.RecipientType;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.java_jpa_vuejs.auth.AuthProvider;
import com.example.java_jpa_vuejs.auth.JoinDto;
import com.example.java_jpa_vuejs.auth.LoginDto;
import com.example.java_jpa_vuejs.common.repositoryService.EmailService;

import lombok.RequiredArgsConstructor;


@Service("emailService")
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{
	
	/* 
    @Autowired
    JavaMailSender emailSender;
	*/

	private final JavaMailSender emailSender;

    public static final String ePw = createKey();
 
    private MimeMessage createMessage(JoinDto joinDto, String validToken)throws Exception{

        String to = joinDto.getEmail();
        long id = joinDto.getId();
        
        System.out.println("보내는 대상 : "+ to);
        System.out.println("인증 번호 : "+ePw);
        MimeMessage  message = emailSender.createMimeMessage();
 
        message.addRecipients(RecipientType.TO, to);//보내는 대상
        message.setSubject("이메일 인증 테스트");//제목
 
        String msgg="";
        msgg+= "<div style='margin:20px;'>";
        msgg+= "<h1> 안녕하세요 조영현입니다. </h1>";
        msgg+= "<br>";
        msgg+= "<p>아래 URL을 클릭하여 회원정보를 변경할 수 있습니다.<p>";
        msgg+= "<br>";
        msgg+= "<p>감사합니다.<p>";
        msgg+= "<br>";
		msgg+= "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg+= "<h3 style='color:blue;'>회원정보 변경 URL입니다.</h3>";
        msgg+= "<div style='font-size:130%'>";
        msgg+= "CODE : <a href='http://localhost:8080/auth?accessType=MODIFY&accessPath=emailAuth&email="+to+"&token="+ validToken +"&id="+id+"'><strong>회원정보 변경하기";
        msgg+= "</strong></a><div><br/> ";
        msgg+= "</div>";
        message.setText(msgg, "utf-8", "html");//내용
        //message.setFrom(new InternetAddress("properties에 입력한 이메일","limjunho"));//보내는 사람
		
		message.setFrom(new InternetAddress("a5869a@gmail.com","조영현"));//보내는 사람

        return message;
    }
 
    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();
 
        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤
            System.out.println(index);
            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    //  a~z  (ex. 1+97=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    //  A~Z
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    // 0~9
                    break;
            }
        }

        return key.toString();
    }


    @Override
    public String sendSimpleMessage(JoinDto joinDto, String validToken)throws Exception {
        // TODO Auto-generated method stub
        MimeMessage message = createMessage(joinDto, validToken);
        try{//예외처리
            emailSender.send(message);
        }catch(MailException es){
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        return ePw;
    }












    @Override
    public String sendSimpleMessage2(LoginDto loginDto, String validToken)throws Exception {
        // TODO Auto-generated method stub
        MimeMessage message = createMessage2(loginDto, validToken);
        try{//예외처리
            emailSender.send(message);
        }catch(MailException es){
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        return ePw;
    }

    private MimeMessage createMessage2(LoginDto loginDto, String validToken)throws Exception{

        String to = loginDto.getEmail();
        long id = loginDto.getId();
        
        System.out.println("보내는 대상 : "+ to);
        System.out.println("인증 번호 : "+ePw);
        MimeMessage  message = emailSender.createMimeMessage();
 
        message.addRecipients(RecipientType.TO, to);//보내는 대상
        message.setSubject("이메일 인증 테스트");//제목
 
        String msgg="";
        msgg+= "<div style='margin:20px;'>";
        msgg+= "<h1> 안녕하세요 조영현입니다. </h1>";
        msgg+= "<br>";
        msgg+= "<p>아래 코드를 복사해 입력해주세요<p>";
        msgg+= "<br>";
        msgg+= "<p>감사합니다.<p>";
        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg+= "<h3 style='color:blue;'>회원가입 이메일 인증 코드입니다. 해당코드를 인증란에 입력해 주세요.</h3>";
        msgg+= "<div style='font-size:130%'>";
        msgg+= "CODE : <strong>";
        msgg+= ePw+"</strong><div><br/> ";
        msgg+= "</div>";

        message.setText(msgg, "utf-8", "html");//내용
        //message.setFrom(new InternetAddress("properties에 입력한 이메일","limjunho"));//보내는 사람
		
		message.setFrom(new InternetAddress("a5869a@gmail.com","조영현"));//보내는 사람

        return message;
    }
}