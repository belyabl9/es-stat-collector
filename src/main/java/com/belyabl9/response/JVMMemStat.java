package com.belyabl9.response;

import com.google.gson.annotations.SerializedName;

public class JVMMemStat {
    @SerializedName("heap_used_in_bytes")
    private long heapUsedInBytes;

    public JVMMemStat(long heapUsedInBytes) {
        this.heapUsedInBytes = heapUsedInBytes;
    }

    public long getHeapUsedInBytes() {
        return heapUsedInBytes;
    }

    public void setHeapUsedInBytes(long heapUsedInBytes) {
        this.heapUsedInBytes = heapUsedInBytes;
    }
}
