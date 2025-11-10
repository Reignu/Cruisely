package com.cruisely.utils;

import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.interceptor.Interceptors;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Interceptors(TrackingInterceptor.class)
public class PropertiesReader {

    private static final String SECURITY_PROPERTIES_FILE = "security/security.properties";

    public static Properties getFileProperties(String fileName) {
        Properties properties = new Properties();
        try {
            InputStream inputStream = PropertiesReader.class.getClassLoader().getResourceAsStream(fileName);
            if (inputStream == null) {
                throw new FileNotFoundException("File " + fileName + " not found");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static Properties getSecurityProperties() {
        return getFileProperties(SECURITY_PROPERTIES_FILE);
    }

}
