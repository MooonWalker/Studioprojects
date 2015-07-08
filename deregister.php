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
if (isset($nameUser) 
     && isset($nameEmail) 
     && isset($gcmRegID)) 
{     
    // Delete the user from db
    $res = delUser($gcmRegID);
    
    echo $res;
} 
else 
{
    // user details not found
}
?>