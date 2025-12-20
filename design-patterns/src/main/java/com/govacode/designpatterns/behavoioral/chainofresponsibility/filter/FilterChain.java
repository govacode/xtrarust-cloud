package com.govacode.designpatterns.behavoioral.chainofresponsibility.filter;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FilterChain {

    private int pos;

    private final List<Filter> filters;

    public FilterChain() {
        this.filters = new ArrayList<>();
    }

    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    public void doFilter(Request req, Response resp) {
        if (pos == filters.size()) {
            return;
        }
        Filter filter = filters.get(pos++);
        filter.doFilter(req, resp, this);
    }
}
