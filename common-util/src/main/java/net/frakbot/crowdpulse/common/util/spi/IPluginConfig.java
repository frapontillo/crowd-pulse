package net.frakbot.crowdpulse.common.util.spi;

import java.util.Map;

/**
 * Interface for {@link IPlugin} configuration classes.
 *
 * @author Francesco Pontillo
 */
public interface IPluginConfig {
    /**
     * Build the current IPluginConfig from a {@link Map}<{@link String}, {@link String}>.
     *
     * @param mapConfig The input configuration as a {@link Map}.
     * @return The current object, with parameters set from the input {@link Map}.
     */
    IPluginConfig buildFromMap(Map<String, String> mapConfig);
}
