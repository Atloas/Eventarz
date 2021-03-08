package com.agh.EventarzApplication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * Adds extra configuration on top of Spring MVC's default.
 */
@ComponentScan
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * Returns a LocaleResolver to use, with a default pl locale.
     *
     * @return A configured LocaleResolver.
     */
    //TODO: Doesn't work on login page
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(new Locale("pl"));
        return slr;
    }

    /**
     * Defines a LocaleChangeInterceptor that changes the locale based on a lang request parameter.
     *
     * @return The configured LocaleChangeInterceptor.
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    /**
     * Adds the configured LocaleChangeInterceptor from {@link #localeChangeInterceptor} to the interceptor registry.
     *
     * @param registry Spring's InterceptorRegistry object.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

}
