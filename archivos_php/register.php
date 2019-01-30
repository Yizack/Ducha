<?php
 
require_once 'include/db_functions.php';
 
// json response array
$response = array("error" => FALSE);
 
if (isset($_POST['username']) && isset($_POST['email']) && isset($_POST['password'])) {
 
    // receiving the post params
    $username = $_POST['username'];
	$email = $_POST['email'];
    $password = $_POST["password"];
 
    // check if user already exists with the same email
    if(emailExists($email)){
		// email already exists
        $response["error"] = TRUE;
        $response["error_msg"] = "Ya está registrado el correo " . $email;
        echo json_encode($response);
	}
	elseif(usernameExists($username)){
		$response["error"] = TRUE;
        $response["error_msg"] = "El nombre de usuario no está disponible " . $username;
        echo json_encode($response);
	}
	else {
        // create a new user
        $user = storeUser($username, $email, $password);
        if ($user) {
            // user stored successfully
            $response["error"] = FALSE;
            $response["user"]["id"] = $user["id"];
            $response["user"]["email"] = $user["email"];
            $response["user"]["username"] = $user["username"];
            echo json_encode($response);
        } else {
            // user failed to store
            $response["error"] = TRUE;
            $response["error_msg"] = "Error de conexión";
            echo json_encode($response);
        }
    }
} else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters missing!";
    echo json_encode($response);
}
?>