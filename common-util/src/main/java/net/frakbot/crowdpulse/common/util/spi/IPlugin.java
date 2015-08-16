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

package net.frakbot.crowdpulse.common.util.spi;

import com.google.gson.JsonElement;
import net.frakbot.crowdpulse.common.util.rx.BackpressureAsyncTransformer;
import rx.Observable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Advanced base class for CrowdPulse plugins.
 * <p>
 * A {@link IPlugin} implementation is based on:
 * <ul>
 * <li>an {@link Input} generic type, that is the class of the objects that will be handled by the plugin</li>
 * <li>an {@link Output} type, the class of the objects returned by the plugin ({@link Output} may equal {@link
 * Input})</li>
 * <li>a {@link Parameter} generic type that defines the option object class that the plugin accepts for transforming
 * {@link Input} objects into {@link Output} objects.</li>
 * </ul>
 * <p>
 * Implementing the {@link IPlugin} class allows for different use cases.
 * <p>
 * <ol>
 * <li>
 * If you want to handle every {@link Input} object (maybe by enriching them or by transforming them into different
 * objects), you can simply implement the {@link #getOperator(Parameter)} and transform every object there.
 * </li>
 * <li>
 * If you want to apply more than one {@link rx.Observable.Operator} on your data stream, you can override
 * {@link #transform(Parameter)} and return your custom {@link rx.Observable.Transformer}.
 * </li>
 * <li>
 * If you want to use more than one {@link rx.Observable.Transformer} on your data stream, you can override
 * {@link #processSingle(Parameter, Observable)}.
 * If you override this method, keep in mind that the default implementation first applies the result of the
 * {@link #transform(Parameter)} method, then composes the stream with a {@link BackpressureAsyncTransformer}.
 * </li>
 * <li>
 * If you want to handle more than one input stream, you can override the {@link #processMulti(IPluginConfig,
 * Observable[])} method and handle the input {@link Observable}s yourself.
 * Please note that the default implementation throws an {@link UnsupportedOperationException} if it isn't implemented.
 * </li>
 * </ol>
 * <p>
 * Plugins should be lazy: when instantiated, they should do the least possible work, delaying the nested object
 * instantiations when the first relevant method is called (e.g. when the first element has to be processed in the
 * {@link rx.Observable.Operator} returned by {@link #getOperator(Parameter)}.
 * Plugins, in fact, are instantiated by {@link java.util.ServiceLoader} when they have to be analyzed to check if
 * they must be used for the specific task instance (e.g., a task may prefer a Foo plugin instead of a Bar plugin,
 * but both plugins are actually instantiated to check which one will have to be used).
 *
 * @author Francesco Pontillo
 */
public abstract class IPlugin<Input, Output, Parameter extends IPluginConfig<Parameter>> {
    private final static char CSV_NEW_COLUMN = ',';

    private String jobName;
    private ProcessInfo processInfo;
    private HashMap<Object, ProcessingStat> statMap;

    /**
     * Get the name that was given to this plugin instance. Plugin instances are called jobs.
     *
     * @return The name of this job.
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * Set a name that will be given to this plugin job instance.
     * A plugin is identified by {@link #getName()}, but the specific instance can be named via this method.
     *
     * @param jobName The name to give the plugin instance.
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * Get the information of the process that has created and instantiated the plugin.
     *
     * @return The process info as a {@link ProcessInfo}.
     */
    public ProcessInfo getProcessInfo() {
        return processInfo;
    }

    /**
     * Set the information of the process that has created and instantiated the plugin.
     *
     * @param processInfo The process info as a {@link ProcessInfo}.
     */
    public void setProcessInfo(ProcessInfo processInfo) {
        this.processInfo = processInfo;
    }

    /**
     * Retrieve the name of the specific plugin implementation.
     *
     * @return The name of the plugin implementation.
     */
    public abstract String getName();

    /**
     * Build a new empty {@link Parameter} configuration object.
     *
     * @return An empty {@link Parameter} configuration object.
     */
    public abstract Parameter getNewParameter();

    /**
     * Create a new {@link Parameter} configuration object starting from a {@link JsonElement}.
     *
     * @param configuration The {@link JsonElement} to read configuration from.
     * @return The {@link Parameter} built from the JSON.
     */
    public final Parameter buildPluginConfig(JsonElement configuration) {
        return IPluginConfig.buildFromJson(getNewParameter(), configuration);
    }

    /**
     * Returns the appropriate {@link rx.Observable.Operator<Input, Output>} exposed by the plugin, which will work
     * on a given stream of data of type {@code Input} and return a stream of type {@code Output}.
     *
     * @param parameters Plugin-specific parameters of class {@link Parameter} to invoke the operator.
     * @return An {@link rx.Observable.Operator} that works on {@link Observable<Input>} and emits values in a
     * {@link Observable<Output>}.
     */
    protected abstract Observable.Operator<Output, Input> getOperator(Parameter parameters);

    /**
     * Return the plugin {@link rx.Observable.Operator<Input, Output>} with a <code>null</code> parameter object.
     *
     * @see IPlugin#getOperator(IPluginConfig)
     */
    protected final Observable.Operator<Output, Input> getOperator() {
        return getOperator(null);
    }

    /**
     * Default implementation to transform a stream of generic type {@link Input} by applying the single operation
     * provided by {@link IPlugin#getOperator()} via {@link Observable#lift(Observable.Operator)}.
     * <p>
     * If the {@link IPlugin<Input>} doesn't use a single {@link rx.Observable.Operator}, you can override this method
     * and provide your own transformation flow. In this case, {@link IPlugin#getOperator()} <i>should</i> return
     * {@code null} to keep your code clearer.
     *
     * @param params Parameters to perform the specific task with.
     * @return A {@link rx.Observable.Transformer} that defines the proper transformations applied by this plugin
     * to the stream.
     */
    public Observable.Transformer<Input, Output> transform(Parameter params) {
        Observable.Operator<Output, Input> operator = getOperator(params);
        if (operator != null) {
            return inputObservable -> inputObservable.lift(getOperator(params));
        }
        // if there is no operator, return null
        return null;
    }

    /**
     * Transform a stream of {@link Input} elements by applying the series of transformations defined by {@link
     * #transform(Parameter)} but with a {@code null} parameters object.
     *
     * @return A {@link rx.Observable.Transformer} that defines the proper transformations applied by this plugin
     * to the stream.
     * @see #transform(Parameter)
     */
    public final Observable.Transformer<Input, Output> transform() {
        return transform(null);
    }

    /**
     * Default implementation that takes an {@link Observable<Input>} stream and transforms it applying the
     * {@link rx.Observable.Transformer} returned by {@link IPlugin#transform(IPluginConfig)}.
     * <p>
     * You should override this method only when the plugin generates a stream and when the
     * {@link IPlugin#processSingle(IPluginConfig, Observable)} method accepts {@code null} as valid input.
     *
     * @param params An optional parameter object of type {@link Parameter}.
     * @param stream The {@link Observable<Input>} to process.
     * @return A new {@link Observable<Output>} built by applying the {@link rx.Observable.Transformer} returned by
     * {@link IPlugin#transform(IPluginConfig)}.
     */
    public Observable<Output> processSingle(Parameter params, Observable<Input> stream) {
        if (stream != null) {
            return stream
                    .compose(this.transform(params))
                    .compose(new BackpressureAsyncTransformer<>());
        }
        return null;
    }

    /**
     * Process an {@link Observable<Input>} stream just as in {@link IPlugin#processSingle(IPluginConfig, Observable)}
     * but with {@code null} parameters.
     *
     * @param stream The {@link Observable<Input>} to process.
     * @return A new {@link Observable<Output>} built by applying the {@link rx.Observable.Transformer} returned by
     * {@link IPlugin#transform(IPluginConfig)}.
     * @see {@link IPlugin#processSingle(IPluginConfig, Observable)}
     */
    public final Observable<Output> processSingle(Observable<Input> stream) {
        return processSingle(null, stream);
    }

    /**
     * Default implementation for processing multiple generic {@link Observable<Object>}s:
     * <ul>
     * <li>if there is only one input stream, it will be processed by {@link IPlugin#processSingle(IPluginConfig,
     * Observable)}, then it will be returned</li>
     * <li>if there is more than one input stream, they will be handled by {@link IPlugin#processMulti(IPluginConfig,
     * Observable[])}</li>
     * </ul>
     * <p>
     * If there's no input stream, a new empty, immediately completing stream will be created.
     *
     * @param params  An optional parameter object of type {@link Parameter}.
     * @param streams The array of {@link Observable}s to use.
     * @return A new {@link Observable<Output>} built by {@link IPlugin#processSingle(IPluginConfig, Observable)} or by
     * {@link IPlugin#processMulti(IPluginConfig, Observable[])}.
     */
    public Observable<Output> process(Parameter params, Observable<? extends Object>... streams) {
        if (streams.length <= 1) {
            Observable<Input> singleStream;
            if (streams.length == 0) {
                singleStream = Observable.empty();
            } else {
                singleStream = (Observable<Input>) streams[0];
            }
            return processSingle(params, singleStream);
        }

        return processMulti(params, streams);
    }

    /**
     * Process multiple streams and return just one.
     * This method will be automatically called by {@link IPlugin#process(IPluginConfig, Observable[])} if the input
     * streams are more than one.
     * <p>
     * This method always throws {@link UnsupportedOperationException} if it isn't overridden in the plugin that
     * has to process multiple input streams.
     *
     * @param params  An optional parameter object of type {@link Parameter}.
     * @param streams The array of {@link Observable}s to use (only the first one will be processed).
     * @return A new {@link Observable<Output>} built by {@link IPlugin#processSingle(IPluginConfig, Observable)}.
     */
    public Observable<Output> processMulti(Parameter params, Observable<? extends Object>... streams) {
        throw new UnsupportedOperationException("IPlugin doesn't support multiple streams. " +
                "You have to override \"processMulti\" and handle it yourself.");
    }

    /**
     * Process an {@link Observable<Object>} stream array just as in {@link IPlugin#process(Parameter, Observable[])}
     * but with parameters in the form of a {@link JsonElement}.
     *
     * @param params  An optional parameter object of type {@link JsonElement}.
     * @param streams The array of {@link Observable}s to use (only the first one will be processed).
     * @return A new {@link Observable<Output>} built by applying the {@link rx.Observable.Transformer} returned by
     * {@link IPlugin#transform(IPluginConfig)}.
     * @see {@link IPlugin#process(Parameter, Observable[])}
     */
    public Observable<Output> process(JsonElement params, Observable<? extends Object>... streams) {
        return process(buildPluginConfig(params), streams);
    }

    /**
     * Process a {@link List} of {@link Observable} streams just as in {@link IPlugin#process(Parameter,
     * Observable[])}.
     *
     * @param params  An optional parameter object of type {@link Parameter}.
     * @param streams The {@link List} of {@link Observable}s to use (only the first one will be processed).
     * @return A new {@link Observable<Output>} built by {@link IPlugin#process(IPluginConfig, Observable[])}.
     */
    public Observable<Output> process(Parameter params, List<Observable<? extends Object>> streams) {
        if (streams != null) {
            return process(params, streams.toArray(new Observable[]{}));
        }
        return process(params);
    }

    /**
     * Process a {@link List} of {@link Observable} streams just as in {@link IPlugin#process(Parameter,
     * Observable[])} but with parameters in the form of a {@link JsonElement}.
     *
     * @param params  An optional parameter object of type {@link JsonElement}.
     * @param streams The {@link List} of {@link Observable}s to use (only the first one will be processed).
     * @return A new {@link Observable<Output>} built by {@link IPlugin#process(JsonElement, Observable[])}.
     */
    public Observable<Output> process(JsonElement params, List<Observable<? extends Object>> streams) {
        if (streams != null) {
            return process(params, streams.toArray(new Observable[]{}));
        }
        return process(params);
    }

    /**
     * Process an {@link Observable<Object>} stream array just as in {@link IPlugin#process(Parameter, Observable[])}
     * but with {@code null} parameters.
     *
     * @see {@link IPlugin#process(Parameter, Observable[])}
     */
    public final Observable<Output> process(Observable<? extends Object>... streams) {
        return process((Parameter) null, streams);
    }

    /**
     * Returns the current plugin's statistics map, where every element has associated a {@link
     * net.frakbot.crowdpulse.common.util.spi.IPlugin.ProcessingStat} object.
     *
     * @return The execution statistics map.
     */
    private HashMap<Object, ProcessingStat> getStatMap() {
        if (statMap == null) {
            statMap = new HashMap<>();
        }
        return statMap;
    }

    /**
     * Report that the plugin has begun processing an element. This method is idempotent, if the processing had already
     * begun, this notification has no effect.
     *
     * @param elementId The ID of the element the plugin started to process.
     */
    public final void reportElementAsStarted(Object elementId) {
        if (getStatMap().get(elementId) == null) {
            getStatMap().put(elementId, new ProcessingStat(new Date()));
        }
    }

    /**
     * Report that the plugin has finished processing an element. This method is idempotent, if the processing had
     * already completed, the notification has no effect.
     *
     * @param elementId The ID of the element the plugin finished to process.
     */
    public final void reportElementAsEnded(Object elementId) {
        ProcessingStat stat = getStatMap().get(elementId);
        if (stat == null) {
            stat = new ProcessingStat();
        }
        stat.setEndTime(new Date());
    }

    private void printStatisticsToCSV() {
        Map<Object, ProcessingStat> statMap = getStatMap();
        if (statMap.keySet().isEmpty()) {
            return;
        }

        List<String> rows = new ArrayList<>(statMap.keySet().size() + 1);
        // build the header
        String header = buildRow(new String[]{"element", "start_time", "end_time", "duration"});
        rows.add(header);

        // build all rows
        statMap.keySet().forEach(k -> {
            ProcessingStat stat = statMap.get(k);
            String[] values = new String[]{k.toString(),
                    Long.toString(stat.getStartTime().getTime()), Long.toString(stat.getEndTime().getTime()),
                    Long.toString(stat.getDuration())};
            rows.add(buildRow(values));
        });

        try {
            Path dirPath = Paths.get(getProcessInfo().getLogs(), getProcessInfo().getName());
            Files.createDirectories(dirPath);
            Path filePath = dirPath.resolve(getJobName() + ".csv");
            Files.write(filePath, rows, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Report that the plugin has finished processing all elements, thus it can print out the statistics to a CSV file.
     */
    public final void reportPluginAsCompleted() {
        printStatisticsToCSV();
    }

    /**
     * Report that the plugin has errored while processing elements. The statistics CSV file will still be written.
     */
    public final void reportPluginAsErrored() {
        printStatisticsToCSV();
    }

    /**
     * Build a row as a {@link String} starting from the column values.
     *
     * @param columns The column values, as array of {@link String}s.
     * @return The {@link String} representation of the row.
     */
    private String buildRow(String[] columns) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            builder = builder.append(columns[i]);
            if (i < columns.length - 1) {
                builder = builder.append(CSV_NEW_COLUMN);
            }
        }
        return builder.toString();
    }

    /**
     * Statistics holder class for an element processing time.
     */
    private final class ProcessingStat {
        private Date startTime;
        private Date endTime;

        public ProcessingStat() {
        }

        public ProcessingStat(Date startTime) {
            this.startTime = startTime;
        }

        /**
         * Get the time a specific element processing began.
         *
         * @return The start processing time for an element.
         */
        public Date getStartTime() {
            return startTime;
        }

        /**
         * Set the time a specific element processing began.
         *
         * @param startTime The start processing time for an element.
         */
        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        /**
         * Get the time a specific element processing completed.
         *
         * @return The end processing time for an element.
         */
        public Date getEndTime() {
            return endTime;
        }

        /**
         * Set the time a specific element processing completed.
         *
         * @param endTime The end processing time for an element.
         */
        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }

        /**
         * Calculate the duration, in milliseconds, that an element has taken to go through the plugin.
         * If the element has no start time but has an end time, 0 is returned.
         * Otherwise, -1 is returned.
         *
         * @return The time (in milliseconds) that an element has taken to get processed.
         */
        public long getDuration() {
            if (startTime != null && endTime != null) {
                return endTime.getTime() - startTime.getTime();
            }
            if (endTime != null) {
                return 0;
            }
            return -1;
        }
    }

}
