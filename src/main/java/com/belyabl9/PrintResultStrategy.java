package com.belyabl9;

import org.jetbrains.annotations.NotNull;
import com.belyabl9.response.Node;

public interface PrintResultStrategy {
    String getPrefix();
    String getLine(@NotNull Node response);
}
