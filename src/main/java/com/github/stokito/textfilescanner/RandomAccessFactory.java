package com.github.stokito.textfilescanner;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class RandomAccessFactory {
    public static RandomAccessStream fromFileUri(String fileUri) {
        if (fileUri == null) {
            return null;
        }
        if (fileUri.startsWith("data:")) {
            // skip "data:application/octet-stream;base64,"
            int contentStartPos = fileUri.indexOf(',');
            if (contentStartPos == -1) {
                throw new RuntimeException("Unable to parse data URU");
            }
            String contentBase64 = fileUri.substring(contentStartPos + 1);
            String content = new String(Base64.getDecoder().decode(contentBase64), UTF_8);
            return new RandomAccessStringStream(content);
        }
        return new RandomAccessFileStream(fileUri);
    }

}
