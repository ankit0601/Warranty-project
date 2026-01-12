package com.warranty.warranty.controller;

import com.warranty.warranty.model.TNCPrivacyPolicy;
import com.warranty.warranty.model.TemplateRequest;
import com.warranty.warranty.services.TemplateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")

public class TemplateController {
    private static final Logger logger = LogManager.getLogger(TemplateController.class);
    @Autowired
    private TemplateService templateService;

    @PostMapping("/getpolicyandtnc")
    public ResponseEntity<TNCPrivacyPolicy> getPolicyorTnc(@RequestBody TemplateRequest templateRequest) {
        logger.info("inside controller.........." + getClass().getName());
        TNCPrivacyPolicy tncPrivacyPolicy = new TNCPrivacyPolicy();
        tncPrivacyPolicy.setStatus("success");
        tncPrivacyPolicy.setMessage("");
        tncPrivacyPolicy.setPrivacyPolcy("https://quietloop.app/docs/Privacy_Policy.html");
        tncPrivacyPolicy.setTermsCondition("https://quietloop.app/docs/image/Terms_Conditions.html");
        return new ResponseEntity<>(tncPrivacyPolicy, HttpStatus.OK);
    }
}
