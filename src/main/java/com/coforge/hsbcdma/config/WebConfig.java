package com.coforge.hsbcdma.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Configuration
public class WebConfig  implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(false);
        registrar.setDateFormatter(DateTimeFormatter.ofPattern("dd-MMM-yyyy").withLocale(Locale.ENGLISH));
        registrar.registerFormatters(registry);
    }
}
