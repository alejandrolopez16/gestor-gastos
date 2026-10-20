package com.gestorgastos;

import com.gestorgastos.model.Categoria;
import com.gestorgastos.model.Gasto;
import com.gestorgastos.repository.GastoRepository;
import com.gestorgastos.repository.JsonGastoRepository;
import com.gestorgastos.service.GestorGastos;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final String SEPARADOR = "------------------------------";
    private static final String ETIQUETA_DESCRIPCION = "Descripción: ";

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        GastoRepository repository =
                new JsonGastoRepository(
                        Path.of("data/gastos.json")
                );

        GestorGastos gestor = new GestorGastos(repository);

        int opcion;

        do {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opción: ");

            try {

                switch (opcion) {

                    case 1:
                        registrarGasto(gestor);
                        break;

                    case 2:
                        listarGastos(gestor);
                        break;

                    case 3:
                        buscarGastoPorId(gestor);
                        break;

                    case 4:
                        eliminarGasto(gestor);
                        break;

                    case 5:
                        calcularTotal(gestor);
                        break;

                    case 6:
                        calcularTotalPorCategoria(gestor);
                        break;

                    case 0:
                        imprimir("\n¡Hasta luego!");
                        break;

                    default:
                        imprimir("\nOpción no válida.");
                }

            } catch (IllegalArgumentException e) {

                imprimir("\nError: " + e.getMessage());

            } catch (Exception e) {

                imprimir(
                        "\nOcurrió un error: " + e.getMessage()
                );
            }

        } while (opcion != 0);

        scanner.close();
    }

    private static void mostrarMenu() {

        imprimir("\n==============================");
        imprimir("       GESTOR DE GASTOS");
        imprimir("==============================");
        imprimir("1. Registrar gasto");
        imprimir("2. Listar gastos");
        imprimir("3. Buscar gasto por ID");
        imprimir("4. Eliminar gasto");
        imprimir("5. Calcular total");
        imprimir("6. Calcular total por categoría");
        imprimir("0. Salir");
        imprimir("==============================");
    }

    private static void registrarGasto(GestorGastos gestor) {

        imprimir("\n--- Registrar gasto ---");

        imprimirSinSalto(ETIQUETA_DESCRIPCION);
        String descripcion = scanner.nextLine();

        imprimirSinSalto("Monto: ");
        String valor = scanner.nextLine();

        BigDecimal monto = new BigDecimal(valor);

        imprimirSinSalto("Categoría: ");
        Categoria categoria = seleccionarCategoria(scanner);

        LocalDate fecha = leerFecha();

        Gasto gasto = gestor.registrarGasto(
                descripcion,
                monto,
                categoria,
                fecha
        );

        imprimir("\nGasto registrado correctamente:");
        imprimir(gasto.toString());
    }

    private static Categoria seleccionarCategoria(Scanner scanner) {

        imprimir("\n--- Categorías ---");

        Categoria[] categorias = Categoria.values();

        for (int i = 0; i < categorias.length; i++) {
            imprimir(
                    (i + 1) + ". " + categorias[i]
            );
        }

        imprimirSinSalto("Seleccione una categoría: ");

        int opcion = Integer.parseInt(scanner.nextLine());

        if (opcion < 1 || opcion > categorias.length) {
            throw new IllegalArgumentException(
                    "Categoría inválida"
            );
        }

        return categorias[opcion - 1];
    }

    private static void listarGastos(GestorGastos gestor) {

        imprimir("\n--- Lista de gastos ---");

        List<Gasto> gastos = gestor.listarGastos();

        if (gastos.isEmpty()) {
            imprimir("No hay gastos registrados.");
            return;
        }

        for (Gasto gasto : gastos) {

            imprimirGasto(gasto);
        }

        imprimir(SEPARADOR);
    }

    private static void buscarGastoPorId(GestorGastos gestor) {

        imprimir("\n--- Buscar gasto ---");

        int id = leerEntero("Ingrese el ID: ");

        Optional<Gasto> resultado =
                gestor.buscarGastoPorId(id);

        if (resultado.isPresent()) {

            Gasto gasto = resultado.get();

            imprimir("\nGasto encontrado:");
            imprimirGasto(gasto);
            imprimir(SEPARADOR);

        } else {

            imprimir(
                    "\nNo se encontró un gasto con el ID " + id + "."
            );
        }
    }

    private static void eliminarGasto(GestorGastos gestor) {

        imprimir("\n--- Eliminar gasto ---");

        int id = leerEntero(
                "Ingrese el ID del gasto: "
        );

        boolean eliminado =
                gestor.eliminarGasto(id);

        if (eliminado) {

            imprimir(
                    "Gasto eliminado correctamente."
            );

        } else {

            imprimir(
                    "No existe un gasto con el ID " + id + "."
            );
        }
    }

    private static void calcularTotal(GestorGastos gestor) {

        imprimir("\n--- Total de gastos ---");

        BigDecimal total = gestor.calcularTotal();

        imprimir(
                "Total gastado: $" + total
        );
    }

    private static void calcularTotalPorCategoria(
            GestorGastos gestor) {

        imprimir(
                "\n--- Total por categoría ---"
        );

        imprimirSinSalto(
                "Ingrese la categoría: "
        );

        Categoria categoria = seleccionarCategoria(scanner);

        BigDecimal total =
                gestor.calcularTotalPorCategoria(
                        categoria
                );

        imprimir(
                "Total en " + categoria + ": $" + total
        );
    }

    private static LocalDate leerFecha() {

        while (true) {

            imprimirSinSalto(
                    "Fecha (YYYY-MM-DD): "
            );

            String texto = scanner.nextLine();

            try {

                return LocalDate.parse(texto);

            } catch (DateTimeParseException e) {

                imprimir(
                        "Formato inválido. " +
                                "Ejemplo: 2026-08-17"
                );
            }
        }
    }

    private static int leerEntero(String mensaje) {

        while (true) {

            imprimirSinSalto(mensaje);

            String valor = scanner.nextLine();

            try {

                return Integer.parseInt(valor);

            } catch (NumberFormatException e) {

                imprimir(
                        "Ingrese un número válido."
                );
            }
        }
    }

    private static void imprimirGasto(Gasto gasto) {

        imprimir(SEPARADOR);
        imprimir("ID: " + gasto.getId());
        imprimir(ETIQUETA_DESCRIPCION + gasto.getDescripcion());
        imprimir("Monto: $" + gasto.getMonto());
        imprimir("Categoría: " + gasto.getCategoria());
        imprimir("Fecha: " + gasto.getFecha());
    }

    // Único punto de salida por consola de la aplicación
    private static void imprimir(String mensaje) {
        System.out.println(mensaje);
    }

    private static void imprimirSinSalto(String mensaje) {
        System.out.print(mensaje);
    }
}