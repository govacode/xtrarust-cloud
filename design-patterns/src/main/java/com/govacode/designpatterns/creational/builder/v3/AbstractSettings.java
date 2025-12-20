package com.govacode.designpatterns.creational.builder.v3;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractSettings {

    private final Map<String, Object> settings;

    protected AbstractSettings(Map<String, Object> settings) {
        this.settings = Map.copyOf(settings);
    }

    protected <T> T getSetting(String name) {
        return (T) getSettings().get(name);
    }

    protected Map<String, Object> getSettings() {
        return this.settings;
    }

    protected static abstract class AbstractBuilder<T extends AbstractSettings, B extends AbstractBuilder<T, B>> {

        private final Map<String, Object> settings = new HashMap<>();

        protected AbstractBuilder() {
        }

        protected B setting(String name, Object value) {
            getSettings().put(name, value);
            return getThis();
        }

        protected B settings(Consumer<Map<String, Object>> settingConsumer) {
            settingConsumer.accept(getSettings());
            return getThis();
        }

        public abstract T build();

        protected Map<String, Object> getSettings() {
            return this.settings;
        }

        protected final B getThis() {
            return (B) this;
        }
    }
}
