package com.mycompany.tiendatecnologica_izquierdocuevasmartin;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.sql.*;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.parser.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonBBDD {

    private static Connection connection;

    static {
        try {
            // Inicializar la conexión usando ConexionBBDD
            connection = ConexionBBDD.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void guardarDatos() throws ParseException, IOException, SQLException {

        // Verificar si los datos ya están cargados antes de proceder
        if (datosYaCargados(connection)) {
            System.out.println("Los datos ya han sido cargados previamente. No se realizará la carga nuevamente.");
            return;  // Salir si los datos ya están cargados
        }
        JSONParser jsonParser = new JSONParser();

        // Leer el archivo JSON
        try (FileReader reader = new FileReader("JsonTienda.json")) {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            JSONObject tienda = (JSONObject) jsonObject.get("tienda");

            // Guardar la tienda
            String tiendaNombre = (String) tienda.get("nombre");
            int tiendaId = guardarTienda(tiendaNombre);

            // Guardar categorías
            JSONArray categorias = (JSONArray) tienda.get("categorias");
            for (Object objCategoria : categorias) {
                JSONObject categoria = (JSONObject) objCategoria;
                String categoriaNombre = (String) categoria.get("nombre");
                int categoriaId = guardarCategoria(tiendaId, categoriaNombre);

                // Guardar productos
                JSONArray productos = (JSONArray) categoria.get("productos");
                for (Object objProducto : productos) {
                    JSONObject producto = (JSONObject) objProducto;
                    int productoId = ((Long) producto.get("id")).intValue();  // Obtener el ID del producto del JSON
                    String productoNombre = (String) producto.get("nombre");
                    double precio = (double) producto.get("precio");
                    String descripcion = (String) producto.get("descripcion");
                    int inventario = ((Long) producto.get("inventario")).intValue();

                    // Ahora pasamos el productoId a guardarProducto
                    guardarProducto(categoriaId, productoId, productoNombre, precio, descripcion, inventario);

                    // Guardar características
                    JSONObject caracteristicas = (JSONObject) producto.get("caracteristicas");
                    for (Object clave : caracteristicas.keySet()) {
                        String key = (String) clave;
                        String value = (String) caracteristicas.get(key);
                        guardarCaracteristicas(productoId, key, value);
                    }

                    // Obtener las imágenes del JSON y procesarlas
                    JSONArray imagenes = (JSONArray) producto.get("imagenes");
                    String[] productoImagenes = new String[imagenes.size()];
                    for (int i = 0; i < imagenes.size(); i++) {
                        String imagenNombre = (String) imagenes.get(i); // Obtener el nombre de la imagen del JSON
                        productoImagenes[i] = cargarImagen(imagenNombre); // Generar la URL completa
                        guardarImagen(productoId, productoImagenes[i]);   // Guardar la URL en la base de datos
                    }

                }

            }

            // Guardar usuarios
            JSONArray usuarios = (JSONArray) tienda.get("usuarios");
            for (Object objUsuario : usuarios) {
                JSONObject usuario = (JSONObject) objUsuario;
                String nombreUsuario = (String) usuario.get("nombre");
                String email = (String) usuario.get("email");
                int usuarioId = guardarUsuario(nombreUsuario, email);

                // Guardar dirección
                JSONObject direccion = (JSONObject) usuario.get("direccion");
                guardarDireccion(usuarioId, direccion);

                // Guardar historial de compras
                JSONArray historialCompras = (JSONArray) usuario.get("historialCompras");
                for (Object objHistorial : historialCompras) {
                    JSONObject compra = (JSONObject) objHistorial;
                    int productoId = ((Long) compra.get("productoId")).intValue();
                    int cantidad = ((Long) compra.get("cantidad")).intValue();
                    String fecha = (String) compra.get("fecha");

                    // Verificar si el producto existe antes de intentar guardarlo en el historial
                    if (productoExisteEnBaseDeDatos(connection, productoId)) {
                        System.out.println("El producto con ID " + productoId + " existe. Procediendo a guardar el historial de compras.");
                        guardarHistorialCompra(connection, usuarioId, productoId, cantidad, fecha);  // Asegúrate de que connection esté correctamente definida
                    } else {
                        System.out.println("El producto con ID " + productoId + " no existe en la base de datos.");
                    }
                }
            }
        }
    }

    private static boolean datosYaCargados(Connection connection) throws SQLException {
        // Comprobar si hay datos en la tabla "tienda"
        String query = "SELECT COUNT(*) FROM tienda";
        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public static boolean productoExisteEnBaseDeDatos(Connection connection, int productoId) {
        String sql = "SELECT COUNT(*) FROM productos WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar si el producto existe: " + e.getMessage());
        }
        return false;
    }

    private static int guardarTienda(String nombre) throws SQLException {
        String sql = "INSERT INTO tienda (nombre) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nombre);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Error al obtener el ID de la tienda.");
                }
            }
        }
    }

    private static int guardarCategoria(int tiendaId, String nombre) throws SQLException {
        String sql = "INSERT INTO categorias (tienda_id, nombre) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, tiendaId);
            stmt.setString(2, nombre);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Error al obtener el ID de la categoría.");
                }
            }
        }
    }

    private static int guardarProducto(int categoriaId, int productoId, String nombre, double precio, String descripcion, int inventario) throws SQLException {
        String sql = "INSERT INTO productos (id, categoria_id, nombre, precio, descripcion, inventario) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productoId);  // Usamos el id manual proporcionado en el JSON
            stmt.setInt(2, categoriaId);  // ID de la categoría
            stmt.setString(3, nombre);    // Nombre del producto
            stmt.setDouble(4, precio);    // Precio del producto
            stmt.setString(5, descripcion);  // Descripción del producto
            stmt.setInt(6, inventario);   // Inventario del producto
            stmt.executeUpdate();
            return productoId;  // Devolvemos el id manual que se ha insertado
        }
    }

    private static void guardarCaracteristicas(int productoId, String clave, String valor) throws SQLException {
        String sql = "INSERT INTO caracteristicas (producto_id, clave, valor) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productoId);
            stmt.setString(2, clave);
            stmt.setString(3, valor);
            stmt.executeUpdate();
        }
    }

    private static String cargarImagen(String imagenNombre) {
        // Ruta base donde están las imágenes (puedes personalizarla según tus necesidades)
        String rutaBase = "Imagenes/";
        return rutaBase + imagenNombre; // Combinar la ruta base con el nombre de la imagen
    }

    private static void guardarImagen(int productoId, String url) throws SQLException {
        String sql = "INSERT INTO imagenes (producto_id, url) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productoId);
            stmt.setString(2, url);
            stmt.executeUpdate();
        }
    }

    private static int guardarUsuario(String nombre, String email) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, email) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nombre);
            stmt.setString(2, email);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Error al obtener el ID del usuario.");
                }
            }
        }
    }

    private static void guardarDireccion(int usuarioId, JSONObject direccion) throws SQLException {
        String sql = "INSERT INTO direccion (usuario_id, calle, numero, ciudad, pais) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setString(2, (String) direccion.get("calle"));
            stmt.setInt(3, ((Long) direccion.get("numero")).intValue());
            stmt.setString(4, (String) direccion.get("ciudad"));
            stmt.setString(5, (String) direccion.get("pais"));
            stmt.executeUpdate();
        }
    }

    public static boolean productoExiste(int productoId) throws SQLException {
        String query = "SELECT COUNT(*) FROM productos WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productoId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public static void guardarHistorialCompra(Connection connection, int usuarioId, int productoId, int cantidad, String fechaStr) {
        String sqlVerificarProducto = "SELECT COUNT(*) FROM productos WHERE id = ?";
        String sqlInsertarHistorial = "INSERT INTO historial_compras (usuario_id, producto_id, cantidad, fecha) VALUES (?, ?, ?, ?)";

        try {
            // Verificar si el producto existe en la base de datos
            try (PreparedStatement stmtVerificar = connection.prepareStatement(sqlVerificarProducto)) {
                stmtVerificar.setInt(1, productoId);
                ResultSet rs = stmtVerificar.executeQuery();
                rs.next();
                int count = rs.getInt(1);

                // Si el producto no existe, lanzar una excepción
                if (count == 0) {
                    System.out.println("El producto con ID " + productoId + " no existe en la base de datos.");
                    return;  // Salir del método si el producto no existe
                }
            }

            // Convertir la fecha de String a java.util.Date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date fecha = sdf.parse(fechaStr);
            java.sql.Date sqlDate = new java.sql.Date(fecha.getTime());

            // Ahora insertar en el historial de compras
            try (PreparedStatement stmtInsertar = connection.prepareStatement(sqlInsertarHistorial)) {
                stmtInsertar.setInt(1, usuarioId);
                stmtInsertar.setInt(2, productoId);
                stmtInsertar.setInt(3, cantidad);
                stmtInsertar.setDate(4, sqlDate);
                stmtInsertar.executeUpdate();
                System.out.println("Compra guardada correctamente en el historial.");
            }
        } catch (SQLException | java.text.ParseException e) {
            System.out.println("Error al guardar en el historial de compras: " + e.getMessage());
        }
    }

}
