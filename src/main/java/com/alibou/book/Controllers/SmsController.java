package com.alibou.book.Controllers;

import com.alibou.book.DTO.SendSmsRequest;
import com.alibou.book.email.MNotifyV2SmsService;
import com.alibou.book.email.SmsRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth/sms")
public class SmsController {

    @Autowired
    private MNotifyV2SmsService mNotifyV2SmsService;
          @PostMapping("/send")
            public String sendSms(@RequestBody SendSmsRequest request) throws JsonProcessingException {
        return mNotifyV2SmsService.sendSms(request.getRecipient(), request.getMessage());
    }
}
