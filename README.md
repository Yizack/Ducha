[![Build Status](https://travis-ci.com/Yizack/duchita.svg?branch=master)](https://travis-ci.com/Yizack/duchita)
# Duchita
Proyecto de Android Studio - Cronómetro/Alarma para android
#
## Requerimientos
- Hosting con PHP y MySQL
- Java 8 o más
- Android Studio 3.2.1
  - SDK Versión 26
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
     ```java
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