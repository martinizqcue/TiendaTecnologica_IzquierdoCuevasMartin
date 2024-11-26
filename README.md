# TiendaTecnologica_IzquierdoCuevasMartin
Tienda Tecnologica en Java Swing
Se va a modelar el siguiente JSON, el cual, se nutre de una tienda tecnológica online.

Este json está a medio llenar, por lo necesitamos completar los datos de éste:



{

   "tienda": {

       "nombre": "Mi Tienda Online",

       "categorias": [

           {

               "id": 1,

               "nombre": "Electrónica",

               "productos": [

                   {

                       "id": 101,

                       "nombre": "Smartphone",

                       "precio": 999.99,

                       "descripcion": "El último smartphone del mercado.",

                       "caracteristicas": {

                           "pantalla": "6.5 pulgadas",

                           "camara": "48 MP",

                           "bateria": "4500 mAh"

                       },

                       "imagenes": ["imagen1.jpg", "imagen2.jpg"],

                       "inventario": 10

                   },

                   // ... otros productos

               ]

           },

           // ... otras categorías

       ],

       "usuarios": [

           {

               "id": 1,

               "nombre": "Juan Pérez",

               "email": "juanperez@example.com",

               "direccion": {

                   "calle": "Calle Principal",

                   "numero": 123,

                   "ciudad": "Madrid",

                   "pais": "España"

               },

               "historialCompras": [

                   {

                       "productoId": 101,

                       "cantidad": 2,

                       "fecha": "2023-11-14"

                   },

                   // ... otras compras

               ]

           },

           // ... otros usuarios

       ]

   }

}



Se debe desarrollar un aplicativo donde recopile esos datos y muestre:

Los datos personales del usuario
Un producto dependiendo de su categoría
Los históricos de compras


El aplicativo debe ser capaz de hacer una compra de un producto y guardar la información en el historial de compras de ese usuario.


Se pide:

Un mockUp que nos diseñe el aplicativo al completo
Un diagrama de clases antes de modelar el aplicativo y un diagrama de clases generado por ingeniería inversa
Un json completo
Un aplicativo java en swing
