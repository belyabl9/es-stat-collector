package com.belyabl9.response;

import java.util.Map;

public class StatResponse {
    private Map<String, Node> nodes;

    public StatResponse(Map<String, Node> nodes) {
        this.nodes = nodes;
    }

    public Map<String, Node> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, Node> nodes) {
        this.nodes = nodes;
    }
}
