package net.frakbot.crowdpulse.common.util.spi;

import java.util.Map;

/**
 * @author Francesco Pontillo
 */
public class VoidConfig implements IPluginConfig {
    @Override public VoidConfig buildFromMap(Map<String, String> mapConfig) {
        return this;
    }
}
