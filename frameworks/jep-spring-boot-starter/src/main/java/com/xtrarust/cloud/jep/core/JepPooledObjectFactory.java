package com.xtrarust.cloud.jep.core;

import com.xtrarust.cloud.jep.config.JepProperties;
import jep.Interpreter;
import jep.JepConfig;
import jep.SharedInterpreter;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.beans.factory.InitializingBean;

public class JepPooledObjectFactory extends BasePooledObjectFactory<Interpreter> implements InitializingBean {

    private final JepProperties properties;

    public JepPooledObjectFactory(JepProperties properties) {
        this.properties = properties;
    }

    @Override
    public Interpreter create() throws Exception {
        return new SharedInterpreter();
    }

    @Override
    public PooledObject<Interpreter> wrap(Interpreter interpreter) {
        return new DefaultPooledObject<>(interpreter);
    }

    @Override
    public void destroyObject(PooledObject<Interpreter> p) throws Exception {
        p.getObject().close();
    }

    @Override
    public boolean validateObject(PooledObject<Interpreter> p) {
        try {
            p.getObject().exec("1+1");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        JepConfig config = new JepConfig();
        if (properties.getIncludePaths() != null) {
            properties.getIncludePaths().forEach(config::addIncludePaths);
        }
        SharedInterpreter.setConfig(config);
    }
}
