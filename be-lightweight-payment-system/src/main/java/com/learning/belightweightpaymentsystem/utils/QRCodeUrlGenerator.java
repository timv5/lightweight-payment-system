package com.learning.belightweightpaymentsystem.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class QRCodeUrlGenerator {

    public String generateUrl(String baseUrl, Integer orderId) {
        String generatedUrl = baseUrl + "/" + orderId;
        log.info("Generated url: {}", generatedUrl);
        return generatedUrl;
    }

}
