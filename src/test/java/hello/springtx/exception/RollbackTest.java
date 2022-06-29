package hello.springtx.exception;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class RollbackTest {

    @Autowired
    RollbackService service;

    @Test
    void runtimeException() {
        assertThatThrownBy(() -> service.runtimeException())
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void checkedException() throws Exception {
        assertThatThrownBy(() -> service.checkedException())
                .isInstanceOf(Exception.class);
    }

    @Test
    void rollbackfor() throws MyException {
        assertThatThrownBy(() -> service.rollbackfor())
                .isInstanceOf(MyException.class);
    }

    @TestConfiguration
    static class RollbackConfig {
        @Bean
        RollbackService rollbackService() {
            return new RollbackService();
        }
    }

    @Slf4j
    static class RollbackService {
        //Runtime
        @Transactional
        public void runtimeException() {
            log.info("call runtimeException");
            throw new RuntimeException();
        }
        //Exception
        @Transactional
        public void checkedException() throws Exception {
            log.info("call checkedException");
            throw new Exception();
        }

        //rollbackfor
        @Transactional(rollbackFor = MyException.class)
        public void rollbackfor() throws MyException {
            log.info("call rollbackfor");
            throw new MyException();
        }
    }


    static class MyException extends Exception {

    }

}
