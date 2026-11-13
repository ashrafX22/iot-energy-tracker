package org.example.usageservice.config;

import org.example.usageservice.client.device.DeviceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

@Configuration
public class HttpInterfaceConfig {

    /**
     * Explicitly declare RestClient.Builder since spring-boot-starter-webmvc
     * in this Boot version does not auto-configure it.
     */
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public DeviceClient deviceClient(RestClient.Builder builder,
                                     @Value("${services.device-service.url:http://localhost:8082}") String baseUrl) {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(5));

        RestClient restClient = builder
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();

        return proxyFactory.createClient(DeviceClient.class);
    }
}