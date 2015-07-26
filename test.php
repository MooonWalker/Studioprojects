<?php
require_once('loader.php');
$gcm_regid="czEWVvIbxOE:APA91bFlg_7yecOOfl1qV6SwpjyGcwuZeV0j7sQFe0-gQWEK2hbcTioX5hYGzTVJqb5K3PuNM-mIbgyIY42y2WuldaPAESxlWfy8RPQpfLrVefK-lRo-d4Wjxfz5BfSBEkpquWpkD6uR";
echo $gcm_regid;

//$query = "DELETE FROM gcm_users WHERE gcm_regid=$gcm_regid";
$result = mysql_query("DELETE FROM gcm_users WHERE gcm_regid = '$gcm_regid'") or die(mysql_error());	
	//$result = mysql_query($query)
echo $result; //result is 1 if success

?>