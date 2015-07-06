<?php
require_once('loader.php');
 
// return json response 
$json = array();
 
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
     && isset($gcmRegID)) 
{     
    // Store user details in db
    $res = delUser($gcmRegID);
    
    echo $result;
} 
else 
{
    // user details not found
}
?>