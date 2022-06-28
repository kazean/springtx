package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
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
public class TxApplyBasicTest {

    @Autowired
    BasicService service;

    @Test
    void isProxy() {
        log.info("service class={}", service.getClass());
        assertThat(AopUtils.isAopProxy(service))
                .isTrue();
    }

    @Test
    void txTest() {
        service.tx();
        service.nonTx();
    }

    @TestConfiguration
    static class TxBasicConfig {
        @Bean
        BasicService basicService() {
            return new BasicService();
        }
    }
    static class BasicService{

        @Transactional
        public void tx() {
            log.info("call tx");
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }

        public void nonTx() {
            log.info("call nonTx");
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }
    }
}
