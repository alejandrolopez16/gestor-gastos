package com.gestorgastos.service;

import com.gestorgastos.model.Categoria;
import com.gestorgastos.model.Gasto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class GestorGastosPerformanceTest {

    private static final Logger LOG =
            LoggerFactory.getLogger(GestorGastosPerformanceTest.class);

    private static void registrar(String mensaje) {
        LOG.info("{}", mensaje);
    }

    private static void registrarError(String mensaje) {
        LOG.error("{}", mensaje);
    }

    private GestorGastos gestor1000;
    private GestorGastos gestor10000;
    private GestorGastos gestor100000;

    private record ResultadoThroughput(
            double throughput,
            int ejecucionesReales,
            int excepciones
    ) {
    }

    private record ResultadoTiempo(
            double tiempoPromedio,
            int ejecucionesExitosas,
            int excepciones
    ) {
    }

    @BeforeEach
    void configurarGestores() {

        GestorGastosTest.FakeGastoRepository repository1000 =
                crearRepositoryConGastos(1000);

        GestorGastosTest.FakeGastoRepository repository10000 =
                crearRepositoryConGastos(10000);

        GestorGastosTest.FakeGastoRepository repository100000 =
                crearRepositoryConGastos(100000);

        gestor1000 = new GestorGastos(repository1000);
        gestor10000 = new GestorGastos(repository10000);
        gestor100000 = new GestorGastos(repository100000);
    }

    private GestorGastosTest.FakeGastoRepository crearRepositoryConGastos(int cantidad) {

        GestorGastosTest.FakeGastoRepository repository = new GestorGastosTest.FakeGastoRepository();

        for (int i = 1; i <= cantidad; i++) {
            repository.guardar(
                    new Gasto(
                            i,
                            "Gasto " + i,
                            new BigDecimal("10000"),
                            Categoria.ALIMENTACION,
                            LocalDate.of(2026, 8, 16)
                    )
            );
        }

        return repository;
    }

    // ================
    // TIEMPO PROMEDIO
    // ================


    private ResultadoTiempo medirTiempoCalcularTotalPorCategoria(
            GestorGastos gestor) {

        int ejecuciones = 100;
        int ejecucionesExitosas = 0;
        int excepciones = 0;

        double suma = 0;

        for (int i = 0; i < ejecuciones; i++) {

            try {
                long inicio = System.nanoTime();

                gestor.calcularTotalPorCategoria(
                        Categoria.ALIMENTACION
                );

                long fin = System.nanoTime();

                suma += (double) (fin - inicio);
                ejecucionesExitosas++;

            } catch (Exception e) {
                excepciones++;

                registrarError(
                        "Excepción en calcularTotalPorCategoria(): "
                                + e.getMessage()
                );
            }
        }

        double tiempoPromedio = ejecucionesExitosas > 0
                ? suma / ejecucionesExitosas / 1_000_000
                : 0;

        return new ResultadoTiempo(
                tiempoPromedio,
                ejecucionesExitosas,
                excepciones
        );
    }

    private ResultadoTiempo medirTiempoCalcularTotal(
            GestorGastos gestor) {

        int ejecuciones = 100;
        int ejecucionesExitosas = 0;
        int excepciones = 0;

        double suma = 0;

        for (int i = 0; i < ejecuciones; i++) {

            try {
                long inicio = System.nanoTime();

                gestor.calcularTotal();

                long fin = System.nanoTime();

                suma += (double) (fin - inicio);
                ejecucionesExitosas++;

            } catch (Exception e) {
                excepciones++;

                registrarError(
                        "Excepción en calcularTotal(): "
                                + e.getMessage()
                );
            }
        }

        double tiempoPromedio = ejecucionesExitosas > 0
                ? suma / ejecucionesExitosas / 1_000_000
                : 0;

        return new ResultadoTiempo(
                tiempoPromedio,
                ejecucionesExitosas,
                excepciones
        );
    }

    private ResultadoTiempo medirTiempoRegistrarGasto(
            GestorGastos gestor) {

        int ejecuciones = 100;
        int ejecucionesExitosas = 0;
        int excepciones = 0;

        double suma = 0;

        for (int i = 0; i < ejecuciones; i++) {

            try {
                long inicio = System.nanoTime();

                gestor.registrarGasto(
                        "Gasto de prueba " + i,
                        new BigDecimal("18000"),
                        Categoria.ALIMENTACION,
                        LocalDate.of(2026, 8, 16)
                );

                long fin = System.nanoTime();

                suma += (double) (fin - inicio);
                ejecucionesExitosas++;

            } catch (Exception e) {
                excepciones++;

                registrarError(
                        "Excepción en registrarGasto(): "
                                + e.getMessage()
                );
            }
        }

        double tiempoPromedio = ejecucionesExitosas > 0
                ? suma / ejecucionesExitosas / 1_000_000
                : 0;

        return new ResultadoTiempo(
                tiempoPromedio,
                ejecucionesExitosas,
                excepciones
        );
    }

    // ===========
    // Throughput
    // ===========

    private ResultadoThroughput medirThroughputCalcularTotal(
            GestorGastos gestor) {

        int ejecuciones = 1000;
        int ejecucionesReales = 0;
        int excepciones = 0;

        double suma = 0;

        for (int i = 0; i < ejecuciones; i++) {

            long inicio = System.nanoTime();

            try {
                gestor.calcularTotal();
                ejecucionesReales++;

            } catch (Exception e) {
                excepciones++;
                registrarError(
                        "Error en calcularTotal(): "
                                + e.getMessage()
                );
            }

            long fin = System.nanoTime();

            suma += (double) (fin - inicio);
        }

        double tiempoTotalSegundos =
                suma / 1_000_000_000;

        double throughput =
                ejecucionesReales / tiempoTotalSegundos;

        return new ResultadoThroughput(
                throughput,
                ejecucionesReales,
                excepciones
        );
    }

    private ResultadoThroughput medirThroughputCalcularTotalPorCategoria(
            GestorGastos gestor) {

        int ejecuciones = 1000;
        int ejecucionesReales = 0;
        int excepciones = 0;

        double suma = 0;

        for (int i = 0; i < ejecuciones; i++) {

            long inicio = System.nanoTime();

            try {
                gestor.calcularTotalPorCategoria(
                        Categoria.ALIMENTACION
                );

                ejecucionesReales++;

            } catch (Exception e) {
                excepciones++;
                registrarError(
                        "Error en calcularTotalPorCategoria(): "
                                + e.getMessage()
                );
            }

            long fin = System.nanoTime();

            suma += (double) (fin - inicio);
        }

        double tiempoTotalSegundos =
                suma / 1_000_000_000;

        double throughput =
                ejecucionesReales / tiempoTotalSegundos;

        return new ResultadoThroughput(
                throughput,
                ejecucionesReales,
                excepciones
        );
    }

    private ResultadoThroughput medirThroughputRegistrarGasto(
            GestorGastos gestor) {

        int ejecuciones = 1000;
        int ejecucionesReales = 0;
        int excepciones = 0;

        double suma = 0;

        for (int i = 0; i < ejecuciones; i++) {

            long inicio = System.nanoTime();

            try {
                gestor.registrarGasto(
                        "Gasto throughput " + i,
                        new BigDecimal("18000"),
                        Categoria.ALIMENTACION,
                        LocalDate.of(2026, 8, 16)
                );

                ejecucionesReales++;

            } catch (Exception e) {
                excepciones++;
                registrarError(
                        "Error en registrarGasto(): "
                                + e.getMessage()
                );
            }

            long fin = System.nanoTime();

            suma += (double) (fin - inicio);
        }

        double tiempoTotalSegundos =
                suma / 1_000_000_000;

        double throughput =
                ejecucionesReales / tiempoTotalSegundos;

        return new ResultadoThroughput(
                throughput,
                ejecucionesReales,
                excepciones
        );
    }

    // ================
    // TIEMPO PROMEDIO
    // ================

    @Test
    void medirRendimientoRegistrarGasto() {

        // Arrange - en BeforeEach

        // Act
        ResultadoTiempo resultado1000 =
                medirTiempoRegistrarGasto(gestor1000);

        ResultadoTiempo resultado10000 =
                medirTiempoRegistrarGasto(gestor10000);

        ResultadoTiempo resultado100000 =
                medirTiempoRegistrarGasto(gestor100000);

        // Assert
        assertTrue(resultado1000.tiempoPromedio() <= 1.0);
        assertTrue(resultado10000.tiempoPromedio() <= 2.0);
        assertTrue(resultado100000.tiempoPromedio() <= 10.0);

        assertEquals(100, resultado1000.ejecucionesExitosas());
        assertEquals(100, resultado10000.ejecucionesExitosas());
        assertEquals(100, resultado100000.ejecucionesExitosas());

        assertEquals(0, resultado1000.excepciones());
        assertEquals(0, resultado10000.excepciones());
        assertEquals(0, resultado100000.excepciones());

        registrar(
                "Registrar gasto - 1.000 registros: "
                        + resultado1000.tiempoPromedio()
                        + " ms | Ejecuciones: "
                        + resultado1000.ejecucionesExitosas()
                        + "/100 | Excepciones: "
                        + resultado1000.excepciones()
        );

        registrar(
                "Registrar gasto - 10.000 registros: "
                        + resultado10000.tiempoPromedio()
                        + " ms | Ejecuciones: "
                        + resultado10000.ejecucionesExitosas()
                        + "/100 | Excepciones: "
                        + resultado10000.excepciones()
        );

        registrar(
                "Registrar gasto - 100.000 registros: "
                        + resultado100000.tiempoPromedio()
                        + " ms | Ejecuciones: "
                        + resultado100000.ejecucionesExitosas()
                        + "/100 | Excepciones: "
                        + resultado100000.excepciones() + "\n"
        );
    }

    @Test
    void medirRendimientoCalcularTotal() {

        // Arrange - en BeforeEach

        // Act
        ResultadoTiempo resultado1000 =
                medirTiempoCalcularTotal(gestor1000);

        ResultadoTiempo resultado10000 =
                medirTiempoCalcularTotal(gestor10000);

        ResultadoTiempo resultado100000 =
                medirTiempoCalcularTotal(gestor100000);

        // Assert
        assertTrue(resultado1000.tiempoPromedio() <= 1.0);
        assertTrue(resultado10000.tiempoPromedio() <= 2.0);
        assertTrue(resultado100000.tiempoPromedio() <= 10.0);

        assertEquals(100, resultado1000.ejecucionesExitosas());
        assertEquals(100, resultado10000.ejecucionesExitosas());
        assertEquals(100, resultado100000.ejecucionesExitosas());

        assertEquals(0, resultado1000.excepciones());
        assertEquals(0, resultado10000.excepciones());
        assertEquals(0, resultado100000.excepciones());

        registrar(
                "Calcular total - 1.000 registros: "
                        + resultado1000.tiempoPromedio()
                        + " ms | Ejecuciones: "
                        + resultado1000.ejecucionesExitosas()
                        + "/100 | Excepciones: "
                        + resultado1000.excepciones()
        );

        registrar(
                "Calcular total - 10.000 registros: "
                        + resultado10000.tiempoPromedio()
                        + " ms | Ejecuciones: "
                        + resultado10000.ejecucionesExitosas()
                        + "/100 | Excepciones: "
                        + resultado10000.excepciones()
        );

        registrar(
                "Calcular total - 100.000 registros: "
                        + resultado100000.tiempoPromedio()
                        + " ms | Ejecuciones: "
                        + resultado100000.ejecucionesExitosas()
                        + "/100 | Excepciones: "
                        + resultado100000.excepciones() + "\n"
        );
    }

    @Test
    void medirRendimientoCalcularTotalPorCategoria() {

        // Arrange - en BeforeEach

        // Act
        ResultadoTiempo resultado1000 =
                medirTiempoCalcularTotalPorCategoria(gestor1000);

        ResultadoTiempo resultado10000 =
                medirTiempoCalcularTotalPorCategoria(gestor10000);

        ResultadoTiempo resultado100000 =
                medirTiempoCalcularTotalPorCategoria(gestor100000);

        // Assert
        assertTrue(resultado1000.tiempoPromedio() <= 1.0);
        assertTrue(resultado10000.tiempoPromedio() <= 2.0);
        assertTrue(resultado100000.tiempoPromedio() <= 10.0);

        assertEquals(100, resultado1000.ejecucionesExitosas());
        assertEquals(100, resultado10000.ejecucionesExitosas());
        assertEquals(100, resultado100000.ejecucionesExitosas());

        assertEquals(0, resultado1000.excepciones());
        assertEquals(0, resultado10000.excepciones());
        assertEquals(0, resultado100000.excepciones());

        registrar(
                "Total por categoría - 1.000 registros: "
                        + resultado1000.tiempoPromedio()
                        + " ms | Ejecuciones: "
                        + resultado1000.ejecucionesExitosas()
                        + "/100 | Excepciones: "
                        + resultado1000.excepciones()
        );

        registrar(
                "Total por categoría - 10.000 registros: "
                        + resultado10000.tiempoPromedio()
                        + " ms | Ejecuciones: "
                        + resultado10000.ejecucionesExitosas()
                        + "/100 | Excepciones: "
                        + resultado10000.excepciones()
        );

        registrar(
                "Total por categoría - 100.000 registros: "
                        + resultado100000.tiempoPromedio()
                        + " ms | Ejecuciones: "
                        + resultado100000.ejecucionesExitosas()
                        + "/100 | Excepciones: "
                        + resultado100000.excepciones() + "\n"
        );
    }

    // ===========
    // Throughput
    // ===========

    @Test
    void medirThroughputCalcularTotal() {

        // Arrange
        int ejecucionesEsperadas = 1000;

        // Act
        ResultadoThroughput resultado1000 =
                medirThroughputCalcularTotal(gestor1000);

        ResultadoThroughput resultado10000 =
                medirThroughputCalcularTotal(gestor10000);

        ResultadoThroughput resultado100000 =
                medirThroughputCalcularTotal(gestor100000);

        // Assert
        assertTrue(resultado1000.throughput() >= 100,
                "El throughput debe ser de al menos 100 ops/s");

        assertTrue(resultado10000.throughput() >= 100,
                "El throughput debe ser de al menos 100 ops/s");

        assertTrue(resultado100000.throughput() >= 100,
                "El throughput debe ser de al menos 100 ops/s");
        assertEquals(0, resultado1000.excepciones());
        assertEquals(0, resultado10000.excepciones());
        assertEquals(0, resultado100000.excepciones());

        registrar(
                "Calcular total - 1.000 registros: "
                        + resultado1000.throughput()
                        + " ops/s | Ejecuciones: "
                        + resultado1000.ejecucionesReales()
                        + "/" + ejecucionesEsperadas
        );

        registrar(
                "Calcular total - 10.000 registros: "
                        + resultado10000.throughput()
                        + " ops/s | Ejecuciones: "
                        + resultado10000.ejecucionesReales()
                        + "/" + ejecucionesEsperadas
        );

        registrar(
                "Calcular total - 100.000 registros: "
                        + resultado100000.throughput()
                        + " ops/s | Ejecuciones: "
                        + resultado100000.ejecucionesReales()
                        + "/" + ejecucionesEsperadas + "\n"
        );
    }

    @Test
    void medirThroughputCalcularTotalPorCategoria() {

        // Arrange
        int ejecucionesEsperadas = 1000;

        // Act
        ResultadoThroughput resultado1000 =
                medirThroughputCalcularTotalPorCategoria(gestor1000);

        ResultadoThroughput resultado10000 =
                medirThroughputCalcularTotalPorCategoria(gestor10000);

        ResultadoThroughput resultado100000 =
                medirThroughputCalcularTotalPorCategoria(gestor100000);

        // Assert
        assertTrue(resultado1000.throughput() >= 100,
                "El throughput debe ser de al menos 100 ops/s");

        assertTrue(resultado10000.throughput() >= 100,
                "El throughput debe ser de al menos 100 ops/s");

        assertTrue(resultado100000.throughput() >= 100,
                "El throughput debe ser de al menos 100 ops/s");

        assertEquals(0, resultado1000.excepciones());
        assertEquals(0, resultado10000.excepciones());
        assertEquals(0, resultado100000.excepciones());

        registrar(
                "Total por categoría - 1.000 registros: "
                        + resultado1000.throughput()
                        + " ops/s | Ejecuciones: "
                        + resultado1000.ejecucionesReales()
                        + "/" + ejecucionesEsperadas
        );

        registrar(
                "Total por categoría - 10.000 registros: "
                        + resultado10000.throughput()
                        + " ops/s | Ejecuciones: "
                        + resultado10000.ejecucionesReales()
                        + "/" + ejecucionesEsperadas
        );

        registrar(
                "Total por categoría - 100.000 registros: "
                        + resultado100000.throughput()
                        + " ops/s | Ejecuciones: "
                        + resultado100000.ejecucionesReales()
                        + "/" + ejecucionesEsperadas + "\n"
        );
    }

    @Test
    void medirThroughputRegistrarGasto() {

        // Arrange
        int ejecucionesEsperadas = 1000;

        // Act
        ResultadoThroughput resultado1000 =
                medirThroughputRegistrarGasto(gestor1000);

        ResultadoThroughput resultado10000 =
                medirThroughputRegistrarGasto(gestor10000);

        ResultadoThroughput resultado100000 =
                medirThroughputRegistrarGasto(gestor100000);

        // Assert
        assertTrue(resultado1000.throughput() >= 100,
                "El throughput debe ser de al menos 100 ops/s");

        assertTrue(resultado10000.throughput() >= 100,
                "El throughput debe ser de al menos 100 ops/s");

        assertTrue(resultado100000.throughput() >= 100,
                "El throughput debe ser de al menos 100 ops/s");

        assertEquals(0, resultado1000.excepciones());
        assertEquals(0, resultado10000.excepciones());
        assertEquals(0, resultado100000.excepciones());

        registrar(
                "Registrar gasto - 1.000 registros: "
                        + resultado1000.throughput()
                        + " ops/s | Ejecuciones: "
                        + resultado1000.ejecucionesReales()
                        + "/" + ejecucionesEsperadas
        );

        registrar(
                "Registrar gasto - 10.000 registros: "
                        + resultado10000.throughput()
                        + " ops/s | Ejecuciones: "
                        + resultado10000.ejecucionesReales()
                        + "/" + ejecucionesEsperadas
        );

        registrar(
                "Registrar gasto - 100.000 registros: "
                        + resultado100000.throughput()
                        + " ops/s | Ejecuciones: "
                        + resultado100000.ejecucionesReales()
                        + "/" + ejecucionesEsperadas + "\n"
        );
    }

}