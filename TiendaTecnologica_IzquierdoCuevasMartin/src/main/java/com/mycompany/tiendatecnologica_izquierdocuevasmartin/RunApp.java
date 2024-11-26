package com.mycompany.tiendatecnologica_izquierdocuevasmartin;

import java.io.IOException;
import org.json.simple.parser.ParseException;
import java.sql.SQLException;

public class RunApp {

    public static void main(String[] args) throws java.text.ParseException {
        try {
            // Llamamos al método para guardar los datos en la base de datos
            JsonBBDD.guardarDatos();
            System.out.println("Datos guardados correctamente en la base de datos.");

            // Ahora, creamos una instancia de Tienda y la mostramos
            Tienda tienda = new Tienda();
            tienda.setVisible(true);

        } catch (ParseException | IOException | SQLException e) {
            // Imprimir el error de excepción
            e.printStackTrace();
            System.out.println("Error al guardar los datos en la base de datos: " + e.getMessage());
        }
    }
}
