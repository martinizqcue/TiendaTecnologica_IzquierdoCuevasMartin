/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.tiendatecnologica_izquierdocuevasmartin;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author alumno
 */
public class Comprar extends javax.swing.JFrame {

    /**
     * Creates new form Comprar
     */
    public Comprar() {
        initComponents();

        this.setResizable(false);
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        // Agregar evento de cierre para reabrir la ventana Tienda
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                abrirVentanaTienda();
            }
        });

        // Cargar usuarios, categorías y productos al inicio
        cargarUsuarios();
        cargarCategorias();
    }

    private void abrirVentanaTienda() {
        Tienda tiendaFrame = new Tienda();
        tiendaFrame.setVisible(true);
    }

    // Método para cargar los usuarios en el combo box
    private void cargarUsuarios() {
        try (Connection con = ConexionBBDD.getConnection(); Statement stmt = con.createStatement()) {
            String sql = "SELECT id, nombre FROM usuarios";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                boxUsuario.addItem(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para cargar las categorías en el combo box
    private void cargarCategorias() {
        try (Connection con = ConexionBBDD.getConnection(); Statement stmt = con.createStatement()) {
            String sql = "SELECT id, nombre FROM categorias";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                boxCategoria.addItem(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para cargar los productos según la categoría seleccionada
    private void cargarProductos(int categoriaId) {
        try (Connection con = ConexionBBDD.getConnection(); PreparedStatement pstmt = con.prepareStatement("SELECT id, nombre FROM productos WHERE categoria_id = ?")) {
            pstmt.setInt(1, categoriaId);
            ResultSet rs = pstmt.executeQuery();
            boxProducto.removeAllItems(); // Limpiar el combo box
            while (rs.next()) {
                boxProducto.addItem(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para registrar la compra
    private void registrarCompra(int usuarioId, int productoId, int cantidad) {
        try (Connection con = ConexionBBDD.getConnection()) {
            // Insertar en historial_compras
            String sqlInsert = "INSERT INTO historial_compras (usuario_id, producto_id, cantidad, fecha) VALUES (?, ?, ?, CURDATE())";
            try (PreparedStatement pstmt = con.prepareStatement(sqlInsert)) {
                pstmt.setInt(1, usuarioId);
                pstmt.setInt(2, productoId);
                pstmt.setInt(3, cantidad);
                pstmt.executeUpdate();
            }

            // Actualizar el inventario del producto
            String sqlUpdate = "UPDATE productos SET inventario = inventario - ? WHERE id = ?";
            try (PreparedStatement pstmt = con.prepareStatement(sqlUpdate)) {
                pstmt.setInt(1, cantidad);
                pstmt.setInt(2, productoId);
                pstmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Compra realizada con éxito.");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al realizar la compra.");
        }
    }

    private int obtenerUsuarioId(String usuarioSeleccionado) {
        int usuarioId = 0;
        try (Connection conn = ConexionBBDD.getConnection()) {
            String query = "SELECT id FROM usuarios WHERE nombre = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, usuarioSeleccionado);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    usuarioId = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarioId;
    }

    private int obtenerCategoriaId(String categoriaSeleccionada) {
        int categoriaId = 0;
        try (Connection conn = ConexionBBDD.getConnection()) {
            String query = "SELECT id FROM categorias WHERE nombre = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, categoriaSeleccionada);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    categoriaId = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoriaId;
    }

    private int obtenerProductoId(String productoSeleccionado) {
        // Consulta a la base de datos para obtener el ID del producto
        int productoId = 0;
        try (Connection conn = ConexionBBDD.getConnection()) {
            String query = "SELECT id FROM productos WHERE nombre = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, productoSeleccionado);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    productoId = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productoId;
    }

    private int obtenerCantidadInventario(int productoId) {
        // Consulta a la base de datos para obtener el inventario disponible del producto
        int cantidadDisponible = 0;
        try (Connection conn = ConexionBBDD.getConnection()) {
            String query = "SELECT inventario FROM productos WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, productoId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    cantidadDisponible = rs.getInt("inventario");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cantidadDisponible;
    }

    private void realizarCompra(int usuarioId, int productoId, int cantidad) {
        try (Connection conn = ConexionBBDD.getConnection()) {
            String query = "INSERT INTO historial_compras (usuario_id, producto_id, cantidad, fecha) VALUES (?, ?, ?, CURDATE())";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, usuarioId);
                stmt.setInt(2, productoId);
                stmt.setInt(3, cantidad);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void actualizarInventario(int productoId, int cantidadComprada) {
        try (Connection conn = ConexionBBDD.getConnection()) {
            // Actualizar el inventario en la base de datos
            String query = "UPDATE productos SET inventario = inventario - ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, cantidadComprada);
                stmt.setInt(2, productoId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        comprar = new javax.swing.JLabel();
        usuario = new javax.swing.JLabel();
        categoria = new javax.swing.JLabel();
        producto = new javax.swing.JLabel();
        cantidad = new javax.swing.JLabel();
        boxUsuario = new javax.swing.JComboBox<>();
        boxCategoria = new javax.swing.JComboBox<>();
        boxProducto = new javax.swing.JComboBox<>();
        TextFieldCantidad = new javax.swing.JTextField();
        RealizarCompra = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));

        comprar.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        comprar.setForeground(new java.awt.Color(255, 204, 0));
        comprar.setText("COMPRAR");

        usuario.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        usuario.setForeground(new java.awt.Color(204, 255, 204));
        usuario.setText("Selecciona el usuario:");

        categoria.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        categoria.setForeground(new java.awt.Color(204, 255, 204));
        categoria.setText("Selecciona la categoria del producto:");

        producto.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        producto.setForeground(new java.awt.Color(204, 255, 204));
        producto.setText("Selecciona el producto:");

        cantidad.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cantidad.setForeground(new java.awt.Color(204, 255, 204));
        cantidad.setText("Selecciona la cantidad del producto:");

        boxUsuario.setBackground(new java.awt.Color(0, 153, 153));
        boxUsuario.setForeground(new java.awt.Color(204, 255, 204));
        boxUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boxUsuarioActionPerformed(evt);
            }
        });

        boxCategoria.setBackground(new java.awt.Color(0, 153, 153));
        boxCategoria.setForeground(new java.awt.Color(204, 255, 204));
        boxCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boxCategoriaActionPerformed(evt);
            }
        });

        boxProducto.setBackground(new java.awt.Color(0, 153, 153));
        boxProducto.setForeground(new java.awt.Color(204, 255, 204));
        boxProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boxProductoActionPerformed(evt);
            }
        });

        TextFieldCantidad.setText("0");
        TextFieldCantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextFieldCantidadActionPerformed(evt);
            }
        });

        RealizarCompra.setBackground(new java.awt.Color(255, 204, 0));
        RealizarCompra.setFont(new java.awt.Font("Yu Gothic UI", 3, 14)); // NOI18N
        RealizarCompra.setText("REALIZAR COMPRA");
        RealizarCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RealizarCompraActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(comprar)
                .addGap(389, 389, 389))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(105, 105, 105)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(categoria)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(boxCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(usuario)
                        .addGap(202, 202, 202)
                        .addComponent(boxUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(producto)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(cantidad)
                                .addGap(77, 77, 77)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TextFieldCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(boxProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 67, Short.MAX_VALUE)
                .addComponent(RealizarCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addComponent(comprar)
                .addGap(75, 75, 75)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(usuario)
                    .addComponent(boxUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(53, 53, 53)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(categoria)
                    .addComponent(boxCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(53, 53, 53)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(producto)
                    .addComponent(boxProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(75, 75, 75)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cantidad)
                    .addComponent(TextFieldCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(119, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(RealizarCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(101, 101, 101))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void boxUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boxUsuarioActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_boxUsuarioActionPerformed

    private void boxCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boxCategoriaActionPerformed
        // TODO add your handling code here:
        String categoriaSeleccionada = (String) boxCategoria.getSelectedItem();
        try (Connection con = ConexionBBDD.getConnection(); PreparedStatement pstmt = con.prepareStatement("SELECT id FROM categorias WHERE nombre = ?")) {
            pstmt.setString(1, categoriaSeleccionada);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int categoriaId = rs.getInt("id");
                cargarProductos(categoriaId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_boxCategoriaActionPerformed

    private void boxProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boxProductoActionPerformed
        // TODO add your handling code here:
        // Obtener el producto seleccionado
        String productoSeleccionado = (String) boxProducto.getSelectedItem();

        // Obtener el ID del producto seleccionado desde la base de datos
        int productoId = obtenerProductoId(productoSeleccionado);

        // Obtener el inventario disponible de ese producto
        int cantidadDisponible = obtenerCantidadInventario(productoId);

        // Mostrar la cantidad disponible en el TextField
        TextFieldCantidad.setText(String.valueOf(cantidadDisponible));
    }//GEN-LAST:event_boxProductoActionPerformed

    private void TextFieldCantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TextFieldCantidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TextFieldCantidadActionPerformed

    private void RealizarCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RealizarCompraActionPerformed
        // TODO add your handling code here:
        // Obtener los valores seleccionados
        int usuarioId = obtenerUsuarioId((String) boxUsuario.getSelectedItem());
        int categoriaId = obtenerCategoriaId((String) boxCategoria.getSelectedItem());
        String productoSeleccionado = (String) boxProducto.getSelectedItem();
        int productoId = obtenerProductoId(productoSeleccionado);
        int cantidadSolicitada = Integer.parseInt(TextFieldCantidad.getText());

        // Obtener la cantidad disponible en inventario
        int cantidadDisponible = obtenerCantidadInventario(productoId);

        // Validar si la cantidad solicitada es mayor que la disponible
        if (cantidadSolicitada > cantidadDisponible || cantidadSolicitada <= 0) {
            // Mostrar mensaje de error
            JOptionPane.showMessageDialog(this, "No puedes comprar más unidades de las que hay disponibles o la cantidad es inválida.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            // Proceder con la compra
            realizarCompra(usuarioId, productoId, cantidadSolicitada);
            // Actualizar el inventario en la base de datos
            actualizarInventario(productoId, cantidadSolicitada);
            // Mostrar mensaje de éxito
            JOptionPane.showMessageDialog(this, "Compra realizada con éxito.");
        }

    }//GEN-LAST:event_RealizarCompraActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton RealizarCompra;
    private javax.swing.JTextField TextFieldCantidad;
    private javax.swing.JComboBox<String> boxCategoria;
    private javax.swing.JComboBox<String> boxProducto;
    private javax.swing.JComboBox<String> boxUsuario;
    private javax.swing.JLabel cantidad;
    private javax.swing.JLabel categoria;
    private javax.swing.JLabel comprar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel producto;
    private javax.swing.JLabel usuario;
    // End of variables declaration//GEN-END:variables
}
