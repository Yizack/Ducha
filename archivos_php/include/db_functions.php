<?php require_once("db_connection.php");?>
<?php

	function update($username, $email, $antemail) {
		global $connection;
		$query = "UPDATE users SET username = '{$username}', email = '{$email}' WHERE email = '{$antemail}'";
		$result = mysqli_query($connection, $query);
		
		if($result){
			$user = "SELECT * FROM users WHERE email = '{$email}'";
			$res = mysqli_query($connection, $user);
			
			while ($user = mysqli_fetch_assoc($res)){
				return $user;
			}
		}
		else{
			return false;
		}
	}

	function storeUser($username, $email, $password){
		global $connection;
		
		$query = "INSERT INTO users(";
		$query .= "username, email, password) ";
		$query .= "VALUES('{$username}', '{$email}','{$password}')";

		$result = mysqli_query($connection, $query);

		if($result){
			$user = "SELECT * FROM users WHERE email = '{$email}'";
			$res = mysqli_query($connection, $user);

			while ($user = mysqli_fetch_assoc($res)){
				return $user;
			}
		}else{
				return false;
			}

	}


	function getUserByEmailAndPassword($email, $password){
		global $connection;
		$query = "SELECT * from users where email = '{$email}' and password = '{$password}'";
	
		$user = mysqli_query($connection, $query);
		
		if($user){
			while ($res = mysqli_fetch_assoc($user)){
				return $res;
			}
		}
		else{
			return false;
		}
	}


	function emailExists($email){
		global $connection;
		$query = "SELECT email from users where email = '{$email}'";

		$result = mysqli_query($connection, $query);

		if(mysqli_num_rows($result) > 0){
			return true;
		}else{
			return false;
		}
	}
	
	function usernameExists($username){
		global $connection;
		$query = "SELECT username from users where username = '{$username}'";
		$result = mysqli_query($connection, $query);

		if(mysqli_num_rows($result) > 0){
			return true;
		}else{
			return false;
		}
	}

?>