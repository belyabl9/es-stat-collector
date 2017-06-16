package com.belyabl9.response;

import com.google.gson.annotations.SerializedName;

public class Swap {
    @SerializedName("used_in_bytes")
    private long usedInBytes;

    public Swap(long usedInBytes) {
        this.usedInBytes = usedInBytes;
    }

    public long getUsedInBytes() {
        return usedInBytes;
    }

    public void setUsedInBytes(long usedInBytes) {
        this.usedInBytes = usedInBytes;
    }
}
