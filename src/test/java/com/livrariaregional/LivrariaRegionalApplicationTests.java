package com.livrariaregional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LivrariaRegionalApplicationTests {

    @Test
    void contextLoads() {
        // Sobe o contexto Spring completo com H2 in-memory.
        // Se alguma bean estiver faltando (ex.: PasswordEncoder), o teste falha.
    }
}