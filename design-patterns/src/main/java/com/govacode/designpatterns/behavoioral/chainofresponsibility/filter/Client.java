package com.govacode.designpatterns.behavoioral.chainofresponsibility.filter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Client {

    public static void main(String[] args) {
        FilterChain filterChain = new FilterChain();
        filterChain.addFilter((req, resp, chain) -> {
            log.info("filter one invoked...");
            chain.doFilter(req, resp);
        });
        filterChain.addFilter((req, resp, chain) -> {
            log.info("filter two invoked...");
            chain.doFilter(req, resp);
        });

        filterChain.doFilter(null, null);
    }
}
