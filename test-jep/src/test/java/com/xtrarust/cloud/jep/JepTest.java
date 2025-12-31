package com.xtrarust.cloud.jep;

import com.xtrarust.cloud.jep.core.JepTemplate;
import com.xtrarust.cloud.jep.core.PythonTask;
import com.xtrarust.cloud.jep.core.ScriptValidationResult;
import com.xtrarust.cloud.jep.core.ScriptValidationTask;
import jep.Interpreter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@SpringBootTest
public class JepTest {

    @Autowired
    private JepTemplate jepTemplate;

    // 2760
    @Test
    public void test() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10000);
        long start = System.currentTimeMillis();

        ScriptValidationResult validationResult = jepTemplate.submit(new ScriptValidationTask("// abc")).get();
        if (validationResult.isPass()) {
            log.info("script validation passed");
        } else {
            log.info("script validation failed, error: {}", validationResult.getError());
        }

        for (int i = 0; i < 10000; i++) {
            executorService.execute(() -> {
               try {
                   Long ret = jepTemplate.submit(new PythonTask<Long>() {

                       @Override
                       public Long run(Interpreter interpreter) throws Exception {
                           interpreter.set("a", 1);
                           interpreter.set("b", 1);
                           interpreter.exec("c = a + b");
                           return (Long) interpreter.getValue("c");
                       }
                   }).get();
                   Assertions.assertEquals(2, ret);
               } catch (Exception e) {
                   log.error("script execution failed", e);
               } finally {
                   latch.countDown();
               }
            });
        }
        latch.await();
        log.info("elapsed: {}ms", System.currentTimeMillis() - start);
        executorService.shutdown();
    }
}
