package com.ht.bnu_tiku_backend.utils.LabelingTool;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacSigner {
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    public static String generateSignature(String secret,
                                           String method,
                                           String path,
                                           String timestamp,
                                           String nonce,
                                           String body) {
        try {
            String data = String.join("|", method, path, timestamp, nonce, body);
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(), HMAC_ALGORITHM));
            byte[] rawHmac = mac.doFinal(data.getBytes());
            return bytesToHex(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
