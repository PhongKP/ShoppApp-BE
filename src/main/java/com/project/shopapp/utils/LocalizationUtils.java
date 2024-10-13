package com.project.shopapp.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@RequiredArgsConstructor
@Component
public class LocalizationUtils {

    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    private final WebUtils webUtils;

    // Object... - Spread Parameter (Có thể 0 - nhiều phần tử)
    public String getLocalizedMessage(String messageKey, Object... params){
        HttpServletRequest request = webUtils.getCurrentRequest();
        Locale locale = localeResolver.resolveLocale(request);
        return messageSource.getMessage(messageKey,params,locale);
    }

}
