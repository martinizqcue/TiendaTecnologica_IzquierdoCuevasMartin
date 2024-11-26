/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendatecnologica_izquierdocuevasmartin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBBDD {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/tienda_db"; // Cambia la URL de la base de datos
    private static final String USER = "root"; // Tu usuario de MySQL
    private static final String PASSWORD = ""; // Tu contraseña de MySQL

    // Método para obtener la conexión
    public static Connection getConnection() throws SQLException {
        try {
            // Cargar el driver de MySQL (si es necesario)
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establecer la conexión
            return DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error al conectar a la base de datos", e);
        }
    }
}
