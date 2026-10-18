package br.com.presentedacegonha.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SenhaHasherTest {

    @Test
    public void deveVerificarSenhaSemArmazenaLaEmTextoPuro() {
        String senha = "senha-segura-123";
        String hash = SenhaHasher.gerarHash(senha);

        assertNotEquals(senha, hash);
        assertTrue(SenhaHasher.verificar(senha, hash));
        assertFalse(SenhaHasher.verificar("senha-incorreta", hash));
    }

    @Test
    public void deveGerarSaltDiferenteParaCadaHash() {
        String primeiro = SenhaHasher.gerarHash("senha-segura-123");
        String segundo = SenhaHasher.gerarHash("senha-segura-123");

        assertNotEquals(primeiro, segundo);
    }
}