<?php
	/**
	*Reemplaza estos valores para utilizar tu base de datos
	*/
	define("DB_HOST","localhost");
	define("DB_USER","tu_usuario");
	define("DB_PASSWORD","tu_contraseña");
	define("DB_DATABASE","tu_basededatos");

	$connection = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);

	if(mysqli_connect_errno()){
		die("Database connnection failed " . "(" .
			mysqli_connect_error() . " - " . mysqli_connect_errno() . ")"
				);
	}
?>
