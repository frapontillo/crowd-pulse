/*
 * Copyright 2015 Francesco Pontillo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.frakbot.crowdpulse.email;

import com.google.gson.JsonElement;
import net.frakbot.crowdpulse.common.util.ConfigUtil;
import net.frakbot.crowdpulse.common.util.StringUtil;
import net.frakbot.crowdpulse.common.util.spi.IPluginConfig;
import net.frakbot.crowdpulse.common.util.spi.PluginConfigHelper;

import java.util.Properties;

/**
 * Configuration class for the {@link EmailNotifier} plugin.
 * The configuration can be loaded from either a {@link JsonElement} or a properties file.
 * <p>
 * The available properties are:
 * <ul>
 * <li>host: the SMTP host</li>
 * <li>port: the SMTP port to connect to</li>
 * <li>username: the username of the account that will send the email</li>
 * <li>password: the password of the account that will send the email</li>
 * <li>use_ssl: whether to use SSL for the connection to the SMTP server</li>
 * <li>from: the email address associated to the sender account sender</li>
 * <li>addresses: array of recipient email addresses (can only be set via JSON config)</li>
 * <li>subject: the subject of the email</li>
 * <li>body_success: the body of a successful report email (references to {{NAME}} will be replaced with the process
 * name)</li>
 * <li>body_error: the body of an error report email (references to {{NAME}} will be replaced with the process
 * name)</li>
 * <li>notify_success: {@code true} to notify successful pipelines, {@code false} otherwise</li>
 * <li>notify_error: {@code true} to notify errored pipelines, {@code false} otherwise</li>
 * </ul>
 * <p>
 * Configurations in a properties file must be introduced by the {@code email} accessor, e.g.:
 * <p>
 * {@code
 * email.host=smtp.myserver.com
 * email.port=123
 * email.username=user@myserver.com
 * email.password=lgtm1234
 * ...
 * }
 * <p>
 * The equivalent configuration as a JSON would be:
 * {@code
 * {
 * "host": "smtp.myserver.com",
 * "port": 123,
 * "username: "user@myserver.com",
 * "password: "lgtm1234"
 * }
 * }
 *
 * @author Francesco Pontillo
 */
public class EmailNotifierConfig implements IPluginConfig<EmailNotifierConfig> {
    private static final String PROP_HOST = "email.host";
    private static final String PROP_PORT = "email.port";
    private static final String PROP_USERNAME = "email.username";
    private static final String PROP_PASSWORD = "email.password";
    private static final String PROP_SSL = "email.use_ssl";
    private static final String PROP_FROM = "email.from";
    private static final String PROP_SUBJECT = "email.subject";
    private static final String PROP_BODY_SUCCESS = "email.body_success";
    private static final String PROP_BODY_ERROR = "email.body_error";
    private static final String PROP_SUCCESS = "email.notify_success";
    private static final String PROP_ERROR = "email.notify_error";

    private static final String DEFAULT_SUBJECT = "CrowdPulse Notification";
    private static final String DEFAULT_BODY_SUCCESS = "The pipeline \"{{NAME}}\" has completed successfully.";
    private static final String DEFAULT_BODY_ERROR = "The pipeline \"{{NAME}}\" has ERRORED!";

    private String host;
    private Integer port;
    private String username;
    private String password;
    private Boolean useSsl;
    private String from;
    private String subject;
    private String bodySuccess;
    private String bodyError;
    private String[] addresses;
    private Boolean notifySuccess = true;
    private Boolean notifyError = true;

    private static Properties props;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getUseSsl() {
        return useSsl;
    }

    public void setUseSsl(Boolean useSsl) {
        this.useSsl = useSsl;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBodySuccess() {
        return bodySuccess;
    }

    public void setBodySuccess(String bodySuccess) {
        this.bodySuccess = bodySuccess;
    }

    public String getBodyError() {
        return bodyError;
    }

    public void setBodyError(String bodyError) {
        this.bodyError = bodyError;
    }

    public String[] getAddresses() {
        return addresses;
    }

    public void setAddresses(String[] addresses) {
        this.addresses = addresses;
    }

    public Boolean getNotifySuccess() {
        return notifySuccess;
    }

    public void setNotifySuccess(Boolean notifySuccess) {
        this.notifySuccess = notifySuccess;
    }

    public Boolean getNotifyError() {
        return notifyError;
    }

    public void setNotifyError(Boolean notifyError) {
        this.notifyError = notifyError;
    }

    public static void setProps(Properties props) {
        EmailNotifierConfig.props = props;
    }

    /**
     * Read the properties from an {@code email.properties} file.
     *
     * @return {@link Properties} object read by an {@code email.properties} file.
     */
    private Properties getProps() {
        if (props == null) {
            props = ConfigUtil.getPropertyFile(EmailNotifier.class, "email.properties");
        }
        return props;
    }

    @Override public EmailNotifierConfig buildFromJsonElement(JsonElement json) {
        EmailNotifierConfig config = PluginConfigHelper.buildFromJson(json, EmailNotifierConfig.class);
        Properties props = getProps();
        if (props != null) {
            if (StringUtil.isNullOrEmpty(config.getHost())) {
                config.setHost(props.getProperty(PROP_HOST));
            }
            if (config.getPort() == null) {
                try {
                    config.setPort(Integer.parseInt(props.getProperty(PROP_PORT)));
                } catch (NumberFormatException ignored) {
                }
            }
            if (StringUtil.isNullOrEmpty(config.getUsername())) {
                config.setUsername(props.getProperty(PROP_USERNAME));
            }
            if (StringUtil.isNullOrEmpty(config.getPassword())) {
                config.setPassword(props.getProperty(PROP_PASSWORD));
            }
            if (config.getUseSsl() == null) {
                config.setUseSsl(Boolean.parseBoolean(props.getProperty(PROP_SSL)));
            }
            if (StringUtil.isNullOrEmpty(config.getFrom())) {
                config.setFrom(props.getProperty(PROP_FROM));
            }
            if (StringUtil.isNullOrEmpty(config.getSubject())) {
                config.setSubject(props.getProperty(PROP_SUBJECT));
            }
            if (StringUtil.isNullOrEmpty(config.getBodySuccess())) {
                config.setBodySuccess(props.getProperty(PROP_BODY_SUCCESS));
            }
            if (StringUtil.isNullOrEmpty(config.getBodyError())) {
                config.setBodyError(props.getProperty(PROP_BODY_ERROR));
            }
            if (config.getNotifySuccess() == null) {
                config.setNotifySuccess(Boolean.parseBoolean(props.getProperty(PROP_SUCCESS)));
            }
            if (config.getNotifyError() == null) {
                config.setNotifyError(Boolean.parseBoolean(props.getProperty(PROP_ERROR)));
            }
        }
        if (StringUtil.isNullOrEmpty(config.getSubject())) {
            config.setSubject(DEFAULT_SUBJECT);
        }
        if (StringUtil.isNullOrEmpty(config.getBodySuccess())) {
            config.setBodySuccess(DEFAULT_BODY_SUCCESS);
        }
        if (StringUtil.isNullOrEmpty(config.getBodyError())) {
            config.setBodyError(DEFAULT_BODY_ERROR);
        }
        return config;
    }

}
