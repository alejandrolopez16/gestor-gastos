package com.gestorgastos.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GastoTest {

    private Gasto gastoBase;

    @BeforeEach
    void configurar() {
        gastoBase = new Gasto(
                1,
                "Almuerzo",
                new BigDecimal("18000"),
                Categoria.ALIMENTACION,
                LocalDate.of(2026, 8, 16)
        );
    }

    @Test
    void debeCrearUnGastoCorrectamente() {

        // Arrange
        int id = 1;
        String descripcion = "Almuerzo";
        BigDecimal monto = new BigDecimal("18000");
        Categoria categoria = Categoria.ALIMENTACION;
        LocalDate fecha = LocalDate.of(2026, 8, 16);

        // Act
        Gasto gasto = new Gasto(
                id,
                descripcion,
                monto,
                categoria,
                fecha
        );

        // Assert
        assertEquals(id, gasto.getId());
        assertEquals(descripcion, gasto.getDescripcion());
        assertEquals(monto, gasto.getMonto());
        assertEquals(categoria, gasto.getCategoria());
        assertEquals(fecha, gasto.getFecha());
    }

    // ======
    // equals
    // ======

    @Test
    void debeSerIgualAsiMismo() {
        assertEquals(gastoBase, gastoBase);
    }

    @Test
    void debeSerIgualAOtroGastoConLosMismosValores() {

        // Arrange
        Gasto copia = new Gasto(
                1,
                "Almuerzo",
                new BigDecimal("18000"),
                Categoria.ALIMENTACION,
                LocalDate.of(2026, 8, 16)
        );

        // Assert
        assertEquals(gastoBase, copia);
    }

    @Test
    void noDebeSerIgualANull() {
        assertNotEquals(null, gastoBase);
    }

    @Test
    void noDebeSerIgualAUnObjetoDeOtroTipo() {
        assertNotEquals("Almuerzo", gastoBase);
    }

    @Test
    void noDebeSerIgualSiCambiaElId() {

        Gasto otro = new Gasto(
                2,
                "Almuerzo",
                new BigDecimal("18000"),
                Categoria.ALIMENTACION,
                LocalDate.of(2026, 8, 16)
        );

        assertNotEquals(gastoBase, otro);
    }

    @Test
    void noDebeSerIgualSiCambiaLaDescripcion() {

        Gasto otro = new Gasto(
                1,
                "Cena",
                new BigDecimal("18000"),
                Categoria.ALIMENTACION,
                LocalDate.of(2026, 8, 16)
        );

        assertNotEquals(gastoBase, otro);
    }

    @Test
    void noDebeSerIgualSiCambiaElMonto() {

        Gasto otro = new Gasto(
                1,
                "Almuerzo",
                new BigDecimal("20000"),
                Categoria.ALIMENTACION,
                LocalDate.of(2026, 8, 16)
        );

        assertNotEquals(gastoBase, otro);
    }

    @Test
    void noDebeSerIgualSiCambiaLaCategoria() {

        Gasto otro = new Gasto(
                1,
                "Almuerzo",
                new BigDecimal("18000"),
                Categoria.OTROS,
                LocalDate.of(2026, 8, 16)
        );

        assertNotEquals(gastoBase, otro);
    }

    @Test
    void noDebeSerIgualSiCambiaLaFecha() {

        Gasto otro = new Gasto(
                1,
                "Almuerzo",
                new BigDecimal("18000"),
                Categoria.ALIMENTACION,
                LocalDate.of(2026, 8, 17)
        );

        assertNotEquals(gastoBase, otro);
    }

    // ========
    // hashCode
    // ========

    @Test
    void gastosIgualesDebenTenerElMismoHashCode() {

        Gasto copia = new Gasto(
                1,
                "Almuerzo",
                new BigDecimal("18000"),
                Categoria.ALIMENTACION,
                LocalDate.of(2026, 8, 16)
        );

        assertEquals(gastoBase.hashCode(), copia.hashCode());
    }

    // ========
    // toString
    // ========

    @Test
    void toStringDebeIncluirTodosLosCampos() {

        // Act
        String texto = gastoBase.toString();

        // Assert
        assertTrue(texto.contains("id=1"));
        assertTrue(texto.contains("descripcion='Almuerzo'"));
        assertTrue(texto.contains("monto=18000"));
        assertTrue(texto.contains("categoria=ALIMENTACION"));
        assertTrue(texto.contains("fecha=2026-08-16"));
    }
}