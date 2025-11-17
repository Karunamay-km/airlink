package com.karunamay.airlink;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AirlinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirlinkApplication.class, args);

        Stripe.apiKey = System.getenv("STRIPE_SECRET_KEY");
    }

}
