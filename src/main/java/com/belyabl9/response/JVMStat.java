package com.belyabl9.response;

public class JVMStat {

    private JVMMemStat mem;
    private GCStat gc;

    public JVMStat() {
    }

    public JVMStat(JVMMemStat mem, GCStat gc) {
        this.mem = mem;
        this.gc = gc;
    }

    public JVMMemStat getMem() {
        return mem;
    }

    public void setMem(JVMMemStat mem) {
        this.mem = mem;
    }

    public GCStat getGc() {
        return gc;
    }

    public void setGc(GCStat gc) {
        this.gc = gc;
    }
}
