package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
public class BasicTxTest {

    @Autowired
    PlatformTransactionManager txManager;

    @TestConfiguration
    static class Config {
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }
    
    @Test
    void commit() {
        log.info("-tx start");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("-tx commit");
        txManager.commit(status);
    }

    @Test
    void rollback() {
        log.info("-tx start");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("-tx rollback");
        txManager.rollback(status);
    }

    @Test
    void double_commit() {
        log.info("-tx1 start");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("-tx1 commit");
        txManager.commit(tx1);

        log.info("-tx2 start");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("-tx2 commit");
        txManager.commit(tx2);
    }

    @Test
    void double_commit_rollback() {
        log.info("-tx1 start");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("-tx1 commit");
        txManager.commit(tx1);

        log.info("-tx2 start");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("-tx1 rollback");
        txManager.rollback(tx2);
    }

    @Test
    void inner_commit() {
        log.info("-outer start");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("-outer.isNewTransaction()={}", outer.isNewTransaction());

        log.info("-inner start");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("-inner.isNewTransaction()={}", inner.isNewTransaction());
        log.info("-inner commit");
        txManager.commit(inner);

        log.info("-outer commit");
        txManager.commit(outer);
    }

    @Test
    void outer_rollback() {
        log.info("-outer start");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("-outer.isNewTransaction()={}", outer.isNewTransaction());

        log.info("-inner start");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("-inner.isNewTransaction()={}", inner.isNewTransaction());
        log.info("-inner commit");
        txManager.commit(inner);

        log.info("-outer rollback");
        txManager.rollback(outer);
    }

    @Test
    void inner_rollback() {
        log.info("-outer start");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("-outer.isNewTransaction()={}", outer.isNewTransaction());

        log.info("-inner start");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("-inner.isNewTransaction()={}", inner.isNewTransaction());
        log.info("-inner rollback");
        txManager.rollback(inner);

        log.info("-outer commit");
        assertThatThrownBy(() -> txManager.commit(outer))
                .isInstanceOf(UnexpectedRollbackException.class);
    }

    @Test
    void inner_rollback_requires_new() {
        log.info("outer start");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction());

        log.info("inner start");
        DefaultTransactionAttribute txAttribute = new DefaultTransactionAttribute();
        txAttribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus inner = txManager.getTransaction(txAttribute);
        log.info("inner.isNewTransaction()={}", inner.isNewTransaction());
        log.info("inner rollback");
        txManager.rollback(inner);

        log.info("outer commit");
        txManager.commit(outer);
    }


}
