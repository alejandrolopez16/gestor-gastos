package com.gestorgastos.repository;

import com.gestorgastos.model.Categoria;
import com.gestorgastos.model.Gasto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JsonGastoRepositoryTest {

    @TempDir
    Path directorioTemporal;

    private Path archivo;
    private JsonGastoRepository repository;

    @BeforeEach
    void configurar() {
        archivo = directorioTemporal.resolve("gastos.json");
        repository = new JsonGastoRepository(archivo);
    }

    private Gasto crearGasto(int id, String descripcion, Categoria categoria) {
        return new Gasto(
                id,
                descripcion,
                new BigDecimal("18000"),
                categoria,
                LocalDate.of(2026, 8, 16)
        );
    }

    // =============
    // obtenerTodos
    // =============

    @Test
    void debeRetornarListaVaciaSiElArchivoNoExiste() {

        // Act
        List<Gasto> gastos = repository.obtenerTodos();

        // Assert
        assertTrue(gastos.isEmpty(), "Sin archivo debería retornar una lista vacía");
    }

    @Test
    void debeLanzarExcepcionSiElArchivoTieneUnJsonInvalido() throws IOException {

        // Arrange
        Files.writeString(archivo, "esto no es un json");

        // Act
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> repository.obtenerTodos()
        );

        // Assert
        assertEquals("No se pudieron leer los gastos", exception.getMessage());
        assertInstanceOf(IOException.class, exception.getCause());
    }

    // =======
    // guardar
    // =======

    @Test
    void debeGuardarUnGastoYPoderLeerloDespues() {

        // Arrange
        Gasto gasto = crearGasto(1, "Almuerzo", Categoria.ALIMENTACION);

        // Act
        repository.guardar(gasto);
        List<Gasto> gastos = repository.obtenerTodos();

        // Assert
        assertEquals(1, gastos.size());
        assertEquals(gasto, gastos.get(0));
    }

    @Test
    void debeConservarElOrdenAlGuardarVariosGastos() {

        // Arrange
        Gasto primero = crearGasto(1, "Almuerzo", Categoria.ALIMENTACION);
        Gasto segundo = crearGasto(2, "Bus", Categoria.TRANSPORTE);

        // Act
        repository.guardar(primero);
        repository.guardar(segundo);
        List<Gasto> gastos = repository.obtenerTodos();

        // Assert
        assertEquals(List.of(primero, segundo), gastos);
    }

    @Test
    void debeCrearLosDirectoriosQueNoExistenAlGuardar() {

        // Arrange
        Path archivoEnSubcarpeta = directorioTemporal.resolve("datos").resolve("anidado").resolve("gastos.json");
        JsonGastoRepository repositoryAnidado = new JsonGastoRepository(archivoEnSubcarpeta);

        // Act
        repositoryAnidado.guardar(crearGasto(1, "Almuerzo", Categoria.ALIMENTACION));

        // Assert
        assertTrue(Files.exists(archivoEnSubcarpeta), "Debería haber creado el archivo y sus carpetas");
    }

    @Test
    void debeGuardarCuandoLaRutaNoTieneDirectorioPadre() throws IOException {

        // Arrange: una ruta relativa de un solo nombre no tiene padre (getParent() == null)
        Path archivoSinPadre = Path.of("gastos-prueba-sin-padre.json");
        JsonGastoRepository repositorySinPadre = new JsonGastoRepository(archivoSinPadre);

        try {
            // Act
            repositorySinPadre.guardar(crearGasto(1, "Almuerzo", Categoria.ALIMENTACION));

            // Assert
            assertEquals(1, repositorySinPadre.obtenerTodos().size());
        } finally {
            Files.deleteIfExists(archivoSinPadre);
        }
    }

    @Test
    void debeLanzarExcepcionSiNoSePuedeGuardar() throws IOException {

        // Arrange: el "directorio" padre es en realidad un archivo, entonces no se puede crear
        Path padreQueEsArchivo = directorioTemporal.resolve("archivo.txt");
        Files.writeString(padreQueEsArchivo, "contenido");
        JsonGastoRepository repositoryInvalido =
                new JsonGastoRepository(padreQueEsArchivo.resolve("gastos.json"));
        Gasto gasto = crearGasto(1, "Almuerzo", Categoria.ALIMENTACION);

        // Act
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> repositoryInvalido.guardar(gasto)
        );

        // Assert
        assertEquals("No se pudieron guardar los gastos", exception.getMessage());
        assertInstanceOf(IOException.class, exception.getCause());
    }

    // ============
    // buscarPorId
    // ============

    @Test
    void debeEncontrarUnGastoPorId() {

        // Arrange
        Gasto gasto = crearGasto(7, "Cine", Categoria.ENTRETENIMIENTO);
        repository.guardar(crearGasto(1, "Almuerzo", Categoria.ALIMENTACION));
        repository.guardar(gasto);

        // Act
        Optional<Gasto> resultado = repository.buscarPorId(7);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(gasto, resultado.get());
    }

    @Test
    void debeRetornarVacioSiElIdNoExiste() {

        // Arrange
        repository.guardar(crearGasto(1, "Almuerzo", Categoria.ALIMENTACION));

        // Act
        Optional<Gasto> resultado = repository.buscarPorId(999);

        // Assert
        assertTrue(resultado.isEmpty());
    }

    // ========
    // eliminar
    // ========

    @Test
    void debeEliminarUnGastoExistente() {

        // Arrange
        Gasto aConservar = crearGasto(1, "Almuerzo", Categoria.ALIMENTACION);
        repository.guardar(aConservar);
        repository.guardar(crearGasto(2, "Bus", Categoria.TRANSPORTE));

        // Act
        repository.eliminar(2);

        // Assert
        assertEquals(List.of(aConservar), repository.obtenerTodos());
    }

    @Test
    void noDebeCambiarNadaAlEliminarUnIdInexistente() {

        // Arrange
        Gasto gasto = crearGasto(1, "Almuerzo", Categoria.ALIMENTACION);
        repository.guardar(gasto);

        // Act
        repository.eliminar(999);

        // Assert
        assertEquals(List.of(gasto), repository.obtenerTodos());
    }
}