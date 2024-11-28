DROP DATABASE IF EXISTS tienda_db;
CREATE DATABASE tienda_db;

USE tienda_db;

CREATE TABLE tienda (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL
);

CREATE TABLE categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tienda_id INT,
    nombre VARCHAR(255) NOT NULL,
    FOREIGN KEY (tienda_id) REFERENCES tienda(id)
);

CREATE TABLE productos (
    id INT PRIMARY KEY,  -- Eliminar AUTO_INCREMENT
    categoria_id INT,
    nombre VARCHAR(255) NOT NULL,
    precio DECIMAL(10,2),
    descripcion TEXT,
    inventario INT,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);


CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
);

CREATE TABLE direccion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT,
    calle VARCHAR(255),
    numero INT,
    ciudad VARCHAR(255),
    pais VARCHAR(255),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE historial_compras (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT,
    producto_id INT,
    cantidad INT,
    fecha DATE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);

-- Tabla para las características de cada producto
CREATE TABLE caracteristicas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    producto_id INT,
    clave VARCHAR(255),
    valor VARCHAR(255),
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);

-- Tabla para las imágenes de cada producto (almacenar las URLs)
CREATE TABLE imagenes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    producto_id INT,
    url VARCHAR(255),
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);




