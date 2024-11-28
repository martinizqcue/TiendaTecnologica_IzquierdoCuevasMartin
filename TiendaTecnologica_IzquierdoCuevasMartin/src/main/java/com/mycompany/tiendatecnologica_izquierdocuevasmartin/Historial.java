/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.tiendatecnologica_izquierdocuevasmartin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author alumno
 */
public class Historial extends javax.swing.JFrame {

    /**
     * Creates new form Historial
     */
    public Historial() {
        initComponents();

        // Hacer la ventana no redimensionable
        this.setResizable(false);
        // Establecer el tamaño de la ventana 
        this.setSize(1000, 600);
        // Centrar la ventana en la pantalla
        this.setLocationRelativeTo(null);

        // Configurar el comportamiento al cerrar la ventana
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        // Agregar un evento de cierre para reabrir la ventana Tienda
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                abrirVentanaTienda();
            }
        });

        // Cargar los usuarios en el combo box
        cargarUsuarios();

        // Agregar el evento para seleccionar un usuario y cargar su historial
        boxHistorial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarHistorialDeCompras();
            }
        });
    }

    private void abrirVentanaTienda() {
        Tienda tiendaFrame = new Tienda();
        tiendaFrame.setVisible(true);
    }

    private void cargarUsuarios() {
        // Limpiar el combo box antes de cargar los nuevos datos
        boxHistorial.removeAllItems();

        // Obtener la lista de usuarios desde la base de datos
        try (Connection conn = ConexionBBDD.getConnection()) {
            String sql = "SELECT nombre FROM usuarios";
            try (PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String nombreUsuario = rs.getString("nombre");
                    boxHistorial.addItem(nombreUsuario);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarHistorialDeCompras() {
        // Obtener el nombre del usuario seleccionado
        String nombreUsuarioSeleccionado = (String) boxHistorial.getSelectedItem();
        if (nombreUsuarioSeleccionado != null) {
            // Variable para almacenar el total de la compra
            double totalCompra = 0.0;
            
            // Obtener el historial de compras del usuario
            try (Connection conn = ConexionBBDD.getConnection()) {
                String sql = "SELECT p.nombre, hc.cantidad, hc.fecha, p.precio " +
                             "FROM historial_compras hc " +
                             "JOIN productos p ON hc.producto_id = p.id " +
                             "JOIN usuarios u ON hc.usuario_id = u.id " +
                             "WHERE u.nombre = ?";
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setString(1, nombreUsuarioSeleccionado);
                    try (ResultSet rs = pst.executeQuery()) {
                        // Limpiar el JTextArea antes de agregar los nuevos datos
                        TextAreaDatosHistorial.setText("");
                        while (rs.next()) {
                            String nombreProducto = rs.getString("nombre");
                            int cantidad = rs.getInt("cantidad");
                            Date fecha = rs.getDate("fecha");
                            double precio = rs.getDouble("precio");

                            // Mostrar los datos en el JTextArea
                            TextAreaDatosHistorial.append("Producto: " + nombreProducto + "\n");
                            TextAreaDatosHistorial.append("Cantidad: " + cantidad + "\n");
                            TextAreaDatosHistorial.append("Fecha: " + fecha.toString() + "\n");
                            TextAreaDatosHistorial.append("Precio: " + precio + "\n");
                            TextAreaDatosHistorial.append("------------------------------\n");
                            
                            // Calcular el total de la compra
                            totalCompra += cantidad * precio;
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
            // Mostrar el total en el JTextArea
            TextAreaDatosHistorial.append("\nTotal de la compra: " + totalCompra + "€");
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
        historial = new javax.swing.JLabel();
        SeleccioneHistorial = new javax.swing.JLabel();
        boxHistorial = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        TextAreaDatosHistorial = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));

        historial.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        historial.setForeground(new java.awt.Color(255, 204, 0));
        historial.setText("HISTORIAL");

        SeleccioneHistorial.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        SeleccioneHistorial.setForeground(new java.awt.Color(204, 255, 204));
        SeleccioneHistorial.setText("Seleccione el usuario para ver su historial de compra:");

        boxHistorial.setBackground(new java.awt.Color(0, 153, 153));
        boxHistorial.setForeground(new java.awt.Color(204, 255, 204));
        boxHistorial.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        boxHistorial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boxHistorialActionPerformed(evt);
            }
        });

        TextAreaDatosHistorial.setColumns(20);
        TextAreaDatosHistorial.setRows(5);
        jScrollPane1.setViewportView(TextAreaDatosHistorial);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 491, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(SeleccioneHistorial)
                        .addGap(227, 227, 227)))
                .addContainerGap(252, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(boxHistorial, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(130, 130, 130))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(historial)
                        .addGap(382, 382, 382))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(historial)
                .addGap(62, 62, 62)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SeleccioneHistorial)
                    .addComponent(boxHistorial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 79, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(115, 115, 115))
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

    private void boxHistorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boxHistorialActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_boxHistorialActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel SeleccioneHistorial;
    private javax.swing.JTextArea TextAreaDatosHistorial;
    private javax.swing.JComboBox<String> boxHistorial;
    private javax.swing.JLabel historial;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
