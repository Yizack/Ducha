<?php
require_once 'include/db_functions.php';

// json response array
$response = array("error" => FALSE);
 
if (isset($_POST['username']) && isset($_POST['email']) && isset($_POST['antemail']) && isset($_POST['antusername'])){
 
    $username = $_POST['username'];
	$email = $_POST['email'];
	$antemail = $_POST['antemail'];
	$antusername = $_POST['antusername'];
	if(usernameExists($username) && $username !== $antusername){
		$response["error"] = TRUE;
		$response["error_msg"] = "El usuario no está disponible " . $username;
		echo json_encode($response);
    }
	elseif(emailExists($email) && $email !== $antemail){
		$response["error"] = TRUE;
		$response["error_msg"] = "El correo no está disponible " . $email;
		echo json_encode($response);
	}
	else {
	    $user = update($username, $email, $antemail); // update user
		$response["error"] = FALSE;
		$response["user"]["id"] = $user["id"];
		$response["user"]["email"] = $user["email"];
		$response["user"]["username"] = $user["username"];
		echo json_encode($response);
	}
}
else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters missing!";
    echo json_encode($response);
}
?>