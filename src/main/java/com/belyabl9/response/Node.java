package com.belyabl9.response;

public class Node {
    private String name;
    private OSStat os;
    private JVMStat jvm;

    public Node(String name, OSStat os, JVMStat jvm) {
        this.name = name;
        this.os = os;
        this.jvm = jvm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OSStat getOs() {
        return os;
    }

    public void setOs(OSStat os) {
        this.os = os;
    }

    public JVMStat getJvm() {
        return jvm;
    }

    public void setJvm(JVMStat jvm) {
        this.jvm = jvm;
    }
}
