package com.learning.belightweightpaymentsystem.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QRCodeUrlGenerator {

    public static String generateUrl(String baseUrl, Integer userId, Integer orderId) {
        String generatedUrl = baseUrl + "/" + userId + "/" + orderId;
        log.info("Generated url: {}", generatedUrl);
        return generatedUrl;
    }

}
