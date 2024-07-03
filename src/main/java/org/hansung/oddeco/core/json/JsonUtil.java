package org.hansung.oddeco.core.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public interface JsonUtil {
    public static JsonElement of(String path) {
        Class<?> contextClass = JsonUtil.class;
        try (InputStream inputStream = contextClass.getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Cannot find json file " + path);
            }
            try (Reader reader = new InputStreamReader(inputStream)) {
                return JsonParser.parseReader(reader);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
