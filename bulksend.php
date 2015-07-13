<?php
require_once('loader.php');
function sPushNotification($registration_ids, $message) {

    $url = 'https://android.googleapis.com/gcm/send';
    $fields = array(
        'registration_ids' => $registration_ids,
        'data' => $message,
    );

    $headers = array(
        'Authorization:key=' . GOOGLE_API_KEY,
        'Content-Type: application/json'
    );
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
//this block is to post message to GCM on-click
$pushStatus = "";   
if ( ! empty($_GET["push"])) 
{ 
    $gcmRegID  = file_get_contents("GCMRegId.txt");
    $pushMessage = $_POST["message"];   
    if (isset($gcmRegID) && isset($pushMessage)) {      
        $gcmRegIds = array($gcmRegID);
        $message = array("m" => $pushMessage);  
        $pushStatus = sPushNotificationToGCM($gcmRegIds, $message);
    }       
} 
?>
<html>
    <head>
        <title>Google Cloud Messaging (GCM) Server in PHP</title>
    </head>
    <body>
        <h1>Google Cloud Messaging (GCM) Server in PHP</h1> 
        <form method="post"   action="bulksend.php/?push=1">                                              
            <div>                                
                <textarea rows="2" name="message" cols="23" placeholder="Message to transmit via   GCM"></textarea>
            </div>
            <div><input type="submit"  value="Send Push Notification via GCM" /></div>
        </form>
        <p><h3><?php echo $pushStatus; ?></h3></p>        
    </body>
</html>