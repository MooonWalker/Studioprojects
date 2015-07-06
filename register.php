<?php
require_once('loader.php');
 
// return json response 
$response = array();
 
$nameUser  = $_POST["name"];
$nameEmail = $_POST["email"];
 
// GCM Registration ID got from device
$gcmRegID  = $_POST["regId"]; 
 
/**
 * Registering a user device in database
 * Store reg id in users table
 */
if (isset($nameUser) 
     && isset($nameEmail) 
     && isset($gcmRegID)) {
     
    // Store user details in db
    $res = storeUser($nameUser, $nameEmail, $gcmRegID);
 
    $registatoin_ids = array($gcmRegID);
    $message = array("message" => "registered");
 
    $result = send_push_notification($registatoin_ids, $message);
	
	$response["success"] = 1;
    $response["message"] = "session successfully added.";

        // echoing JSON response
    echo json_encode($response);

   // echo $result;
} else {
    // user details not found
}
?>