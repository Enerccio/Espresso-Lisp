package com.en_circle.el.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class IOUtils {

    public static String fromURL(URL url) throws Exception {
        InputStream is = url.openStream();
        return fromInputStream(is);
    }

    public static String fromInputStream(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
        StringBuilder builder = new StringBuilder();
        char[] chars = new char[4096];
        for (;;) {
            int readSize = reader.read(chars);
            if (readSize <= 1)
                break;
            builder.append(chars, 0, readSize);
        }
        return builder.toString();
    }

}
