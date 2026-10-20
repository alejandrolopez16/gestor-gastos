package com.gestorgastos.service;

import com.gestorgastos.model.Categoria;
import com.gestorgastos.model.Gasto;
import com.gestorgastos.repository.GastoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GestorGastosTest {

    private static final BigDecimal MONTO_VALIDO = new BigDecimal("18000");
    private static final LocalDate FECHA_VALIDA = LocalDate.of(2026, 8, 16);

    private FakeGastoRepository repository;
    private GestorGastos gestor;

    //Arrange común para cada prueba
    @BeforeEach
    void configurar() {
        repository = new FakeGastoRepository();
        gestor = new GestorGastos(repository);
    }

    @Test
    void debeRegistrarUnGasto() {

        //Act
        Gasto gasto = gestor.registrarGasto(
                "Almuerzo",
                MONTO_VALIDO,
                Categoria.ALIMENTACION,
                FECHA_VALIDA
        );

        // Assert
        assertEquals(1, gasto.getId(), "El ID del gasto debería ser 1");
        assertEquals(1, repository.obtenerTodos().size(), "Debería haber exactamente 1 gasto en el repositorio");
        assertTrue(repository.obtenerTodos().contains(gasto), "El repositorio debería contener el gasto registrado");
    }

    public static class FakeGastoRepository implements GastoRepository {

        private final List<Gasto> gastos = new ArrayList<>();

        @Override
        public void guardar(Gasto gasto) {
            gastos.add(gasto);
        }

        @Override
        public List<Gasto> obtenerTodos() {
            return gastos;
        }

        @Override
        public Optional<Gasto> buscarPorId(int id) {
            return gastos.stream()
                    .filter(gasto -> gasto.getId() == id)
                    .findFirst();
        }

        @Override
        public void eliminar(int id) {
            gastos.removeIf(gasto -> gasto.getId() == id);
        }
    }

    @Test
    void noDebePermitirUnMontoMenorOIgualACero() {

        // Arrange
        BigDecimal montoNegativo = new BigDecimal("-5000");

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        "Almuerzo",
                        montoNegativo,
                        Categoria.ALIMENTACION,
                        FECHA_VALIDA
                )
        );

        // Assert
        assertEquals("El monto debe ser mayor que cero", exception.getMessage());
    }

    @Test
    void noDebePermitirMontoNulo() {

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        "Almuerzo",
                        null,
                        Categoria.ALIMENTACION,
                        FECHA_VALIDA
                )
        );

        // Assert
        assertEquals("El monto debe ser mayor que cero", exception.getMessage());
    }

    @Test
    void noDebePermitirDescripcionVacia() {

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        "",
                        MONTO_VALIDO,
                        Categoria.ALIMENTACION,
                        FECHA_VALIDA
                )
        );

        // Assert
        assertEquals("La descripción no puede estar vacía", exception.getMessage());
    }

    @Test
    void noDebePermitirDescripcionConSoloEspacios() {

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        "    ",
                        MONTO_VALIDO,
                        Categoria.ALIMENTACION,
                        FECHA_VALIDA
                )
        );

        // Assert
        assertEquals("La descripción no puede estar vacía", exception.getMessage());
    }

    @Test
    void noDebePermitirDescripcionNula() {

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        null,
                        MONTO_VALIDO,
                        Categoria.ALIMENTACION,
                        FECHA_VALIDA
                )
        );

        // Assert
        assertEquals("La descripción no puede estar vacía", exception.getMessage());
    }


    @Test
    void noDebePermitirCategoriaNula() {

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        "Almuerzo",
                        MONTO_VALIDO,
                        null,
                        FECHA_VALIDA
                )
        );

        // Assert
        assertEquals("La categoría no puede estar vacía", exception.getMessage());
    }

    @Test
    void debeGenerarIdsConsecutivos() {

        // Act
        Gasto primerGasto = gestor.registrarGasto(
                "Almuerzo",
                MONTO_VALIDO,
                Categoria.ALIMENTACION,
                FECHA_VALIDA
        );

        Gasto segundoGasto = gestor.registrarGasto(
                "Transporte",
                new BigDecimal("5000"),
                Categoria.TRANSPORTE,
                FECHA_VALIDA
        );

        // Assert
        assertEquals(1, primerGasto.getId(), "El primer gasto debería tener ID 1");
        assertEquals(2, segundoGasto.getId(), "El segundo gasto debería tener ID 2");
    }

    @Test
    void debeGenerarElSiguienteIdDisponible() {

        // Arrange
        repository.guardar(new Gasto(
                5,
                "Gasto existente",
                new BigDecimal("10000"),
                Categoria.OTROS,
                LocalDate.of(2026, 8, 15)
        ));

        // Act
        Gasto nuevoGasto = gestor.registrarGasto(
                "Almuerzo",
                MONTO_VALIDO,
                Categoria.ALIMENTACION,
                FECHA_VALIDA
        );

        // Assert
        assertEquals(6, nuevoGasto.getId(), "El siguiente ID disponible debería ser 6");
    }

    @Test
    void noDebePermitirFechaNula() {

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gestor.registrarGasto(
                        "Almuerzo",
                        MONTO_VALIDO,
                        Categoria.ALIMENTACION,
                        null
                )
        );

        // Assert
        assertEquals("La fecha no puede ser nula", exception.getMessage());
    }

    @Test
    void debePermitirUnaFechaValida() {
        //Arrange
        LocalDate fecha = FECHA_VALIDA;

        // Act
        Gasto gasto = gestor.registrarGasto(
                "Almuerzo",
                MONTO_VALIDO,
                Categoria.ALIMENTACION,
                fecha
        );

        // Assert
        assertEquals(fecha, gasto.getFecha(), "La fecha del gasto debería coincidir con la fecha proporcionada");
    }

    @Test
    void debeEncontrarUnGastoPorId() {

        // Arrange
        Gasto gasto = gestor.registrarGasto(
                "Almuerzo",
                MONTO_VALIDO,
                Categoria.ALIMENTACION,
                FECHA_VALIDA
        );

        // Act
        Optional<Gasto> resultado = gestor.buscarGastoPorId(gasto.getId());

        // Assert
        assertTrue(resultado.isPresent(), "El gasto debería existir en el repositorio");
        assertEquals(gasto, resultado.get(), "El gasto encontrado debería coincidir con el registrado");
    }

    @Test
    void debeRetornarVacioSiElGastoNoExiste() {

        // Act
        Optional<Gasto> resultado = gestor.buscarGastoPorId(999);

        // Assert
        assertTrue(resultado.isEmpty(), "No debería encontrar un gasto con ID inexistente");
    }

    @Test
    void debeEliminarUnGastoExistente() {

        // Arrange

        Gasto gasto = gestor.registrarGasto(
                "Almuerzo",
                MONTO_VALIDO,
                Categoria.ALIMENTACION,
                FECHA_VALIDA
        );

        // Act
        boolean eliminado = gestor.eliminarGasto(gasto.getId());

        // Assert
        assertTrue(eliminado, "El método debería retornar true al eliminar un gasto existente");
        assertTrue(repository.obtenerTodos().isEmpty(), "El repositorio debería quedar vacío después de eliminar");
    }
    @Test
    void debeRetornarFalseSiElGastoNoExisteAlEliminarlo() {

        // Act
        boolean eliminado = gestor.eliminarGasto(999);

        // Assert
        assertFalse(eliminado, "El método debería retornar false al intentar eliminar un gasto inexistente");
    }

    @Test
    void debeCalcularElTotalDeLosGastos() {

        // Arrange
        gestor.registrarGasto(
                "Almuerzo",
                MONTO_VALIDO,
                Categoria.ALIMENTACION,
                FECHA_VALIDA
        );

        gestor.registrarGasto(
                "Transporte",
                new BigDecimal("5000"),
                Categoria.TRANSPORTE,
                FECHA_VALIDA
        );

        gestor.registrarGasto(
                "Café",
                new BigDecimal("3500"),
                Categoria.ALIMENTACION,
                FECHA_VALIDA
        );

        // Act
        BigDecimal total = gestor.calcularTotal();

        // Assert
        assertEquals(
                new BigDecimal("26500"),
                total,
                "El total de todos los gastos debería ser 26500"
        );
    }

    @Test
    void debeRetornarCeroCuandoNoHayGastos() {

        // Act
        BigDecimal total = gestor.calcularTotal();

        // Assert
        assertEquals(
                BigDecimal.ZERO,
                total,
                "El total debería ser cero cuando no hay gastos"
        );
    }

    @Test
    void debeListarTodosLosGastos() {

        // Arrange
        Gasto primerGasto = gestor.registrarGasto(
                "Almuerzo",
                MONTO_VALIDO,
                Categoria.ALIMENTACION,
                FECHA_VALIDA
        );

        Gasto segundoGasto = gestor.registrarGasto(
                "Transporte",
                new BigDecimal("5000"),
                Categoria.TRANSPORTE,
                FECHA_VALIDA
        );

        // Act
        List<Gasto> gastos = gestor.listarGastos();

        // Assert
        assertEquals(2, gastos.size(), "Debería haber 2 gastos en la lista");
        assertTrue(gastos.contains(primerGasto), "La lista debería contener el primer gasto");
        assertTrue(gastos.contains(segundoGasto), "La lista debería contener el segundo gasto");
    }

    @Test
    void debeRetornarListaVaciaCuandoNoHayGastos() {

        // Act
        List<Gasto> gastos = gestor.listarGastos();

        // Assert
        assertTrue(gastos.isEmpty(), "La lista debería estar vacía cuando no hay gastos");
    }

    @Test
    void debeCalcularTotalPorCategoria() {

        // Arrange
        gestor.registrarGasto(
                "Almuerzo",
                MONTO_VALIDO,
                Categoria.ALIMENTACION,
                FECHA_VALIDA
        );

        gestor.registrarGasto(
                "Café",
                new BigDecimal("3500"),
                Categoria.ALIMENTACION,
                FECHA_VALIDA
        );

        gestor.registrarGasto(
                "Transporte",
                new BigDecimal("5000"),
                Categoria.TRANSPORTE,
                FECHA_VALIDA
        );

        // Act
        BigDecimal total = gestor.calcularTotalPorCategoria(
                Categoria.ALIMENTACION
        );

        // Assert
        assertEquals(
                new BigDecimal("21500"),
                total,
                "El total de gastos en ALIMENTACION debería ser 21500"
        );
    }

    @Test
    void debeRetornarCeroSiNoHayGastosEnLaCategoria() {

        // Arrange
        gestor.registrarGasto(
                "Almuerzo",
                MONTO_VALIDO,
                Categoria.ALIMENTACION,
                FECHA_VALIDA
        );

        // Act
        BigDecimal total = gestor.calcularTotalPorCategoria(
                Categoria.EDUCACION
        );

        // Assert
        assertEquals(
                BigDecimal.ZERO,
                total,
                "El total debería ser cero si no hay gastos en la categoría"
        );
    }

    @Test
    void noDebePermitirCategoriaNulaAlCalcularTotal() {

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gestor.calcularTotalPorCategoria(null)
        );

        // Assert
        assertEquals("La categoría no puede estar vacía", exception.getMessage());
    }


}