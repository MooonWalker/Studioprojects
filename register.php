<?php
require_once('loader.php');
 
// return json response 
$response = array();
 
$nameUser  = $_POST["name"];
$nameEmail = $_POST["email"]; 
$gcmRegID  = $_POST["regId"]; // GCM Registration ID got from device
 
/**
 * Registering a user device in database
 * Store reg id in users table
 */
if (isset($nameUser) && isset($nameEmail) && isset($gcmRegID)) 
{     
    // Store user details in db
    $res = storeUser($nameUser, $nameEmail, $gcmRegID);
 
    $registatoin_ids = array($gcmRegID);
    $message = array("message" => "User registered"); 
    $result = send_push_notification($registatoin_ids, $message);
	
    // echoing JSON response
	if($res > 0)
	{
		$response["successfromreg"] = 1;
		$response["message"] = "User successfully added.";
		echo json_encode($response);
	}
	else
	{
		$response["successfromreg"] = 2;
		$response["message"] = "User could not be inserted into db.";
		echo json_encode($response);
	}
} 
else 
{
    // user details not found
	$response["successfromreg"] = 3;
    $response["message"] = "User details not arrived at server.";
	echo json_encode($response);
}
?>