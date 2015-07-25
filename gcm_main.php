<?php
require_once('loader.php'); //this is the same as as other answers on this topic

function sPushNotification($registration_ids, $message) {

    $url = 'https://android.googleapis.com/gcm/send';
    $fields = array(
        'registration_ids' => $registration_ids,
        'data' => $message,
    );

    //define('GOOGLE_API_KEY', 'your_google_api_key_here');

    $headers = array(
        'Authorization:key=' . GOOGLE_API_KEY,
        'Content-Type: application/json');
		
    echo json_encode($fields);
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));

    $result = curl_exec($ch);
    if($result === false)
        die('Curl failed ' . curl_error());

    curl_close($ch);
    return $result;

}

function redirect($url)
{
    if (!headers_sent())
    {    
        header('Location: '.$url);
        exit;
        }
    else
        {  
        echo '<script type="text/javascript">';
        echo 'window.location.href="'.$url.'";';
        echo '</script>';
        echo '<noscript>';
        echo '<meta http-equiv="refresh" content="0;url='.$url.'" />';
        echo '</noscript>'; exit;
    }
}


$pushStatus = '';

if(!empty($_GET['push'])) 
{
    $query = "SELECT gcm_regid FROM gcm_users";
    if($query_run = mysql_query($query)) {

        $gcmRegIds = array();
        while($query_row = mysql_fetch_assoc($query_run)) {

            array_push($gcmRegIds, $query_row['gcm_regid']);

        }

    }
    $pushMessage = $_POST['message'];
    if(isset($gcmRegIds) && isset($pushMessage)) 
	{
        $message = array('message' => $pushMessage);
		$regIdChunk=array_chunk($gcmRegIds,1000);
		foreach($regIdChunk as $RegId)
		{
			$pushStatus = sPushNotification($RegId, $message);
		}
    }   
   $url="http://somejourney.info/gcm_server_files/gcm_main.php";
   redirect($url);

}


if(!empty($_GET['shareRegId'])) {

    $gcmRegId = $_POST['gcm_regid'];
    $query = "INSERT INTO gcm_users VALUES ('', '$gcmRegId')";
    if($query_run = mysql_query($query)) {
       // echo 'OK';
        exit;
    }   
}
?>

<html>
<meta type="utf-8">
<meta charset="utf-8" /> 
    <head>
        <title>GCM Üzenet küldés</title>
    </head>
    <body>

        <?php
        include_once 'db_functions.php';
        $db = new DB_Functions();
        $users = $db->getAllUsers();
        if ($users != false)
            $no_of_users = mysql_num_rows($users);
        else
            $no_of_users = 0;
        ?>
    <h1>GCM Üzenetküldés</h1>
    <h4>Jelenlegi címzettek száma: <?php echo $no_of_users; ?></h4>
    <h4></h4>
    <form method = 'POST' action = 'gcm_main.php/?push=1'>
        <div>
            <textarea rows = "3" name = "message" cols = "75" placeholder = "Type message here"></textarea>
        </div>
		<div>
			<input type = "button" value = "Insert Event tags">
		</div>
		<br><br>
        <div>
            <input type = "submit" value = "Send Notification">
        </div>

    </form>
    </body>
</html>