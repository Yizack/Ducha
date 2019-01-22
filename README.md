[![Build Status](https://travis-ci.com/Yizack/duchita.svg?branch=master)](https://travis-ci.com/Yizack/duchita)
# Duchita
Proyecto de Android Studio - Cronómetro/Alarma para ahorrar agua en la ducha.\
Probado en las versiones de Android 5.1 Lollipop *(API 22)* hasta Android 8.1 Oreo *(API 27)*.
## Introducción
DuchitaApp es una aplicación para celulares Android desarrollada por estudiantes de la Universidad Tecnológica de Panamá con el fin de aportar una forma de ahorro de agua mediante el uso de un cronómetro que indica el tiempo que una persona ha permanecido en el baño y con un sonido de notificación que se activa en un intervalo de 5 minutos (en **Modo Normal**) o 10 minutos (en **Modo Shampoo**), éste es considerado el tiempo normal de ducha de una persona.
##
## Demo
Si quieres probar la app, descarga el APK en tu celular [`duchita.apk`](https://github.com/Yizack/duchita/raw/master/apk/duchita.apk)
##
## Requerimientos
- Hosting con PHP y MySQL
- Java 8
- Android Studio 3.3
  - SDK Versión 28
  - Gradle Versión 4.10.1
  - BuildTools Versión 28.0.3
##
## Configuración
1. Subir el contenido de `/archivos php` en tu Hosting.
2. Ubicación de la base de datos
   - En [`/app/src/main/java/com/esmifrase/duchita/`](https://github.com/Yizack/duchita/blob/master/app/src/main/java/com/esmifrase/duchita/) editar [`Utils.java`](https://github.com/Yizack/duchita/blob/master/app/src/main/java/com/esmifrase/duchita/Utils.java) con la información de tu sitio web.
      ```java
      ...
      public static final String BASE_IP          = "http://tusitioweb.com/";
      public static final String LOGIN_URL        = BASE_IP + "login.php";
      public static final String REGISTER_URL     = BASE_IP + "register.php";
      public static final String UPDATE_URL       = BASE_IP + "update.php";
      ...
      ```
3. Crear base de datos.
   - Crear base de datos, usuario y contraseña para phpMyAdmin
   - Abrir la base de datos creada y ejecuta el código de [`users.sql`](https://github.com/Yizack/duchita/blob/master/sql/users.sql) en la pestaña **SQL**.
     ```sql
     create table users(
       id int(11) primary key auto_increment,
       username varchar(50) unique,
       email varchar(200) unique,
       password varchar(64) not null
     );
     ```
4. Conexión a la base de datos.
   - En [`/archivos php/include/`](https://github.com/Yizack/duchita/blob/master/archivos%20php/include/) editar conexión a tu base de datos de [`db_connection.php`](https://github.com/Yizack/duchita/blob/master/archivos%20php/include/db_connection.php).
     ```php
     ...
     define("DB_HOST","localhost");
     define("DB_USER","tu_usuario");
     define("DB_PASSWORD","tu_contraseña");
     define("DB_DATABASE","tu_basededatos");
     ...
     ```
5. Asignar nombre al APK
   - En [`/app/`](https://github.com/Yizack/duchita/blob/master/app/) edita el nombre de tu app en [`build.gradle`](https://github.com/Yizack/duchita/blob/master/app/build.gradle).
     ```gradle
     ...
     def appName = "duchita"
     ...
     ```
6. Compilar el proyecto.
   - Abre el proyecto en **Android Studio** y ejecuta una Build en el símbolo del martillo.
7. Probar app.
   - Crea un APK en la sección de pestañas `Build > Build Bundle(s) / APK(s) >Build APK(s)` y se generará una ventana pequeña en la parte inferior derecha de la pantalla, luego presiona `locate` para localizar el APK y se abrirá la ubicación del APK en el explorador de Windows.
   - Copia y pega el APK en tu celular e instalalo.
   - También puedes utilizar un Dispositivo Virtual para probar tu app dentro de Android Studio.
