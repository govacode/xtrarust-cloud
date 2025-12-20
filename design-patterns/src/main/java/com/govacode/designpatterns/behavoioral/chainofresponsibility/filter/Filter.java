package com.govacode.designpatterns.behavoioral.chainofresponsibility.filter;

public interface Filter {

    void doFilter(Request req, Response resp, FilterChain chain);
}
