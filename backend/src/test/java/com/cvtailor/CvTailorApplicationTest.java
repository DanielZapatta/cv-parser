package com.cvtailor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "ollama.base-url=http://localhost:11434"
})
class CvTailorApplicationTest {

    @Test
    void contextLoads() {
    }
}
