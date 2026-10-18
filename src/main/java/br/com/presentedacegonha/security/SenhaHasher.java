package br.com.presentedacegonha.security;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class SenhaHasher {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String FORMAT = "pbkdf2-sha256";
    private static final int ITERATIONS = 210_000;
    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private SenhaHasher() {
    }

    public static String gerarHash(String senha) {
        if (senha == null || senha.isEmpty()) {
            throw new IllegalArgumentException("Senha obrigatória.");
        }

        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        byte[] hash = derivar(senha.toCharArray(), salt, ITERATIONS);

        return FORMAT + "$" + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verificar(String senha, String hashArmazenado) {
        if (senha == null || hashArmazenado == null) {
            return false;
        }

        try {
            String[] partes = hashArmazenado.split("\\$", -1);
            if (partes.length != 4 || !FORMAT.equals(partes[0])) {
                return false;
            }

            int iteracoes = Integer.parseInt(partes[1]);
            if (iteracoes < 1 || iteracoes > 1_000_000) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[3]);
            byte[] hashRecebido = derivar(
                    senha.toCharArray(), salt, iteracoes);

            return MessageDigest.isEqual(hashEsperado, hashRecebido);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] derivar(char[] senha, byte[] salt, int iteracoes) {
        PBEKeySpec especificacao = new PBEKeySpec(
                senha, salt, iteracoes, HASH_LENGTH);
        try {
            return SecretKeyFactory.getInstance(ALGORITHM)
                    .generateSecret(especificacao)
                    .getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "Não foi possível proteger a senha.", e);
        } finally {
            especificacao.clearPassword();
            java.util.Arrays.fill(senha, '\0');
        }
    }
}