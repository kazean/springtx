package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
public class InternalCallV1Test {

    @Autowired
    CallService service;

    @Test
    void printProxy() {
        log.info("service={}", service.getClass());
        assertThat(AopUtils.isAopProxy(service))
                .isTrue();
    }

    @Test
    void internal() {
        service.internal();
    }

    @Test
    void external() {
        service.external();
    }

    @TestConfiguration
    static class InternalCallV1Config {
        @Bean
        CallService callService() {
            return new CallService();
        }
    }

    static class CallService {
        public void external() {
            log.info("external call");
            printTxInfo();
            internal();
        }
        @Transactional
        public void internal() {
            log.info("internal call");
            printTxInfo();

        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("txActive={}", txActive);
            boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            log.info("readOnly={}", readOnly);
        }
    }
}
