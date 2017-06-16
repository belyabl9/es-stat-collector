package com.belyabl9.response;

import java.util.Map;

public class GCStat {

    private Map<String, GCCollectorStat> collectors;

    public GCStat() {}

    public GCStat(Map<String, GCCollectorStat> collectors) {
        this.collectors = collectors;
    }

    public Map<String, GCCollectorStat> getCollectors() {
        return collectors;
    }

    public void setCollectors(Map<String, GCCollectorStat> collectors) {
        this.collectors = collectors;
    }
}
