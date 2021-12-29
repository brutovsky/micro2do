package com.brtvsk.todoservice.i18n;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class Translator {

    private final ResourceBundleMessageSource messageSource;

    public Translator(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String toLocale(String msgCode, List<String> args) {
        Locale locale = LocaleContextHolder.getLocale();
        System.out.println(locale);
        var msg = messageSource.getMessage(msgCode, args.toArray(), locale);
        System.out.println(msg);
        return msg;
    }
}
