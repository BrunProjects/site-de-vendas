package br.com.presentedacegonha.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Test;

public class DatabaseConnectionTest {

    @Test
    public void deveNormalizarUrlDoBanco() {
        String jdbcUrl = DatabaseConnection.normalizarUrl(
                "postgres://postgres:senha@localhost:5432/site_de_vendas"
        );

        assertEquals(
                "jdbc:postgresql://postgres:senha@localhost:5432/site_de_vendas",
                jdbcUrl
        );
    }

    @Test
    public void deveConectarAoBanco() throws Exception {
        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            assertTrue(
                "A conexão com o banco não está válida.",
                connection.isValid(5)
            );

            try (Statement statement =
                         connection.createStatement();
                 ResultSet result = statement.executeQuery(
                         "SELECT current_database()")) {

                assertTrue(
                    "A consulta não retornou resultados.",
                    result.next()
                );

                System.out.println(
                    "Conexão com o Supabase estabelecida!"
                );
                System.out.println(
                    "Banco conectado: " + result.getString(1)
                );
            }
        }
    }
}
