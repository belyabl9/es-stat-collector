package com.belyabl9.response;

import com.google.gson.annotations.SerializedName;

public class GCCollectorStat {
    @SerializedName("collection_count")
    private String collectionCount;
    @SerializedName("collection_time_in_millis")
    private long collectionTimeInMs;

    public GCCollectorStat() {}

    public GCCollectorStat(String collectionCount, long collectionTimeInMs) {
        this.collectionCount = collectionCount;
        this.collectionTimeInMs = collectionTimeInMs;
    }

    public String getCollectionCount() {
        return collectionCount;
    }

    public void setCollectionCount(String collectionCount) {
        this.collectionCount = collectionCount;
    }

    public long getCollectionTimeInMs() {
        return collectionTimeInMs;
    }

    public void setCollectionTimeInMs(long collectionTimeInMs) {
        this.collectionTimeInMs = collectionTimeInMs;
    }
}
