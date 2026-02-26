package com.automation.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;

@LoadPolicy(LoadType.MERGE)
@Sources({
        "system:properties",
        "system:env",
        "classpath:config/${env}.properties",
        "classpath:config/config.properties"
})
public interface ApiConfig extends Config {

    @DefaultValue("https://admin-dev.burgershop.io/api")
    @Key("api.base.url")
    String apiBaseUrl();

    @DefaultValue("")
    @Key("api.username")
    String apiUsername();

    @DefaultValue("")
    @Key("api.password")
    String apiPassword();

    @DefaultValue("30")
    @Key("api.connection.timeout")
    int connectionTimeout();

    @DefaultValue("30")
    @Key("api.socket.timeout")
    int socketTimeout();

    @DefaultValue("application/json")
    @Key("api.content.type")
    String contentType();

    @DefaultValue("true")
    @Key("api.logging.enabled")
    boolean loggingEnabled();

    @DefaultValue("")
    @Key("api.key")
    String apiKey();
}
