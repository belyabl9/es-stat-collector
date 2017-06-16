package com.belyabl9;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;

public class StartupParameters {
    private final URI uri;
    private final long time;
    private final long interval;

    public StartupParameters(@NotNull CommandLine cmd) {
        String uri = cmd.getOptionValue("uri");
        if (StringUtils.isEmpty(uri)) {
            throw new IllegalArgumentException("Method URI must be specified.");
        }
        String time = cmd.getOptionValue("time");
        if (StringUtils.isEmpty(time)) {
            throw new IllegalArgumentException("Time must be specified.");
        }
        String interval = cmd.getOptionValue("interval");
        if (StringUtils.isEmpty(interval)) {
            throw new IllegalArgumentException("Interval must be specified.");
        }

        try {
            this.uri = new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        this.time = Long.parseLong(time);
        this.interval = Long.parseLong(interval);
    }

    public URI getUri() {
        return uri;
    }

    public long getTime() {
        return time;
    }

    public long getInterval() {
        return interval;
    }
}
