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
if (isset($gcmRegID)) 
{     
    // Delete the user from db
    $res = delUser($gcmRegID);
    // echoing JSON response
	if($res > 0)
	{
		$response["successfromdel"] = 10;
		$response["message"] = "User successfully deleted.";
		echo json_encode($response);
	}
	else
	{
		$response["successfromdel"] = $res;
		$response["message"] = "User could not be deleted from db.";
		echo json_encode($response);
	}

} 
else 
{
    // user details not found
	$response["successfromdel"] = 30;
	$response["message"] = "RegID not arrived to webserver.";
	echo json_encode($response);
}
?>