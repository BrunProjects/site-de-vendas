
package br.com.presentedacegonha.config;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConnection {

        private static final Dotenv dotenv = carregarDotenv();

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "Driver PostgreSQL não está disponível na aplicação.", e);
        }

        String databaseUrl = obterUrlConfigurada();
        String jdbcUrl = normalizarUrl(databaseUrl);

        URI uri = URI.create(jdbcUrl.substring("jdbc:".length()));

        String user = null;
        String password = null;
        String userInfo = uri.getRawUserInfo();

        if (userInfo != null && userInfo.contains(":")) {
            String[] credentials = userInfo.split(":", 2);
            user = URLDecoder.decode(credentials[0], StandardCharsets.UTF_8);
            password = URLDecoder.decode(credentials[1], StandardCharsets.UTF_8);
        } else {
            user = obterValorConfigurado("DB_USER", "PGUSER");
            password = obterValorConfigurado("DB_PASSWORD", "PGPASSWORD");
        }

        if (user == null || user.isBlank() || password == null) {
            throw new IllegalStateException(
                    "Banco não configurado. Defina DATABASE_URL ou DB_URL + DB_USER/DB_PASSWORD."
            );
        }

        String authority = uri.getRawAuthority();
        String jdbcTarget = "jdbc:postgresql://"
            + (authority != null && authority.contains("@")
                ? authority.substring(authority.indexOf('@') + 1)
                : authority)
                + (uri.getRawPath() == null ? "/postgres" : uri.getRawPath())
                + (uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery());

        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        properties.setProperty("sslmode", "require");

        return DriverManager.getConnection(jdbcTarget, properties);
    }

    static String normalizarUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            throw new IllegalStateException(
                    "Banco não configurado. Defina DATABASE_URL ou DB_URL."
            );
        }

        String url = rawUrl.trim();

        if (url.startsWith("postgres://")) {
            return "jdbc:postgresql://" + url.substring("postgres://".length());
        }

        if (url.startsWith("postgresql://")) {
            return "jdbc:" + url;
        }

        if (url.startsWith("jdbc:postgresql://")) {
            return url;
        }

        throw new IllegalArgumentException(
                "DATABASE_URL precisa ser uma URL PostgreSQL válida."
        );
    }

    private static String obterUrlConfigurada() {
        String databaseUrl = obterValorConfigurado("DATABASE_URL", "DB_URL");

        if (databaseUrl == null || databaseUrl.isBlank()) {
            throw new IllegalStateException(
                    "A variável DATABASE_URL não foi configurada."
            );
        }

        return databaseUrl;
    }

    private static String obterValorConfigurado(String... nomes) {
        for (String nome : nomes) {
            if (nome == null || nome.isBlank()) {
                continue;
            }

            String valor = System.getenv(nome);
            if ((valor == null || valor.isBlank()) && dotenv != null) {
                valor = dotenv.get(nome);
            }

            if (valor != null && !valor.isBlank()) {
                return valor;
            }
        }

        return null;
    }

    private static Dotenv carregarDotenv() {
        String envFile = System.getenv("APP_ENV_FILE");
        if (envFile == null || envFile.isBlank()) {
            return Dotenv.configure().ignoreIfMissing().load();
        }

        Path path = Path.of(envFile).toAbsolutePath().normalize();
        return Dotenv.configure()
                .directory(path.getParent().toString())
                .filename(path.getFileName().toString())
                .ignoreIfMissing()
                .load();
    }
}
