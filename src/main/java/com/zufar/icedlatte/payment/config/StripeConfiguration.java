package com.zufar.icedlatte.payment.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * This record responsible for displaying the Stripe Api configuration in Spring as a bean.
 * Configuration stores special keys with which Stripe controls data security.
 *
 * @param secretKey used for security work with the Stripe Api from the backend side.
 * @param webHookSecret used for security work with the Stripe Api from the webhooks side.
 * */
@ConfigurationProperties(prefix = "stripe")
public record StripeConfiguration(String secretKey,
                                  String webHookSecret) {

    @PostConstruct
    private void init() {
        setStripeKey(secretKey);
    }

    public static synchronized void setStripeKey(String stripeKey) {
        Stripe.apiKey = stripeKey;
    }
}
