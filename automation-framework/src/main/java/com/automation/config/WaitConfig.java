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
public interface WaitConfig extends Config {

    @DefaultValue("30")
    @Key("explicit.wait")
    int explicitWait();

    @DefaultValue("10")
    @Key("implicit.wait")
    int implicitWait();

    @DefaultValue("60")
    @Key("page.load.timeout")
    int pageLoadTimeout();

    @DefaultValue("500")
    @Key("polling.interval")
    int pollingInterval();

    @DefaultValue("30")
    @Key("fluent.wait.timeout")
    int fluentWaitTimeout();
}
