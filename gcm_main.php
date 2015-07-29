<?php
require_once('loader.php'); //this is the same as as other answers on this topic

function sPushNotification($registration_ids, $message) 
{
    $url = 'https://android.googleapis.com/gcm/send';
    $fields = array(
        'registration_ids' => $registration_ids,
        'data' => $message,
    );

    //define('GOOGLE_API_KEY', 'your_google_api_key_here');

    $headers = array(
        'Authorization:key=' . GOOGLE_API_KEY,
        'Content-Type: application/json');
	
	// write out the json	
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

//main routine============================================================
$pushStatus = '';
$dateErr = $dateFromForm ='';
$okCode=1;



if(!empty($_GET['push'])) 
{
    $query = "SELECT gcm_regid FROM gcm_users";
    if($query_run = mysql_query($query)) 
	{
        $gcmRegIds = array();
        while($query_row = mysql_fetch_assoc($query_run)) 
		{
            array_push($gcmRegIds, $query_row['gcm_regid']);
        }
    }
	
	// get if it is an event with date attributes
	$isEvent = $_POST['chkIsEvent'];
	
	// get date from html date field after submit
	$dateFromForm = $_POST['calendar-input'];
	// get message field from html
    $pushMessage = $_POST['message'];
	
	if(isset($isEvent))
	{
		if(empty($dateFromForm))
		{
			$dateErr="A dátum kötelező!";
			$okCode=0;
			
		}
		else
		{
			$okCode=1;
		}
		//interpreted in android client
		$pushMessage="[e]{$dateFromForm}[/e]{$pushMessage}";
		echo $pushMessage;
	}
	else
	{
		$okCode=1;
	}
	
	//redirect url
	$url="http://somejourney.info/gcm_server_files/gcm_main.php";
	 
	
		if(isset($gcmRegIds) && isset($pushMessage)) 
		{
			$message = array('message' => $pushMessage);
			$regIdChunk=array_chunk($gcmRegIds,1000);
			foreach($regIdChunk as $RegId)
			{
				$pushStatus = sPushNotification($RegId, $message);
			}
			
			redirect($url);
		} 
	
	      
}

//not used
if(!empty($_GET['shareRegId'])) 
{
    $gcmRegId = $_POST['gcm_regid'];
    $query = "INSERT INTO gcm_users VALUES ('', '$gcmRegId')";
    if($query_run = mysql_query($query)) 
	{
       // echo 'OK';
        exit;
    }   
}
?>

<html>
<meta type="utf-8">
<meta charset="utf-8" /> 
<link rel="stylesheet" type="text/css" href="src/datepickr.min.css">
<style>
            body 
			{
                font-family: Verdana;
            }

            h1 
			{
                font-size: 25px;
            }

            .calendar-icon 
			{
                display: inline-block;
                vertical-align: middle;
                width: 32px;
                height: 32px;
                background: url(images/calendar.png);
            }   
			.error {color: #FF0000;}
			
        </style>
    <head>
        <title>GCM Üzenet küldés</title>
				
		<script type="text/javascript">
        function clrChkBox() 
		{
            document.getElementById("chkIsEvent").checked=false;
        }
		
		function validateForm()
		{
			var value1 = document.getElementById('txtMsg').value;
			var value2 = document.getElementById('calendar-input').value;
			var isEvent = document.getElementById('chkIsEvent').checked;
			
			if( value1 == "") 
			{
				alert("Az üzenet mező nem lehet üres!");
				return false;    
			}
			
			if(isEvent)
			{
				if(value2=="")
				{
					alert("Az dátum nem lehet üres!");
					return false;
				}
			}
			return true;
		}
		//var php_okCode =  "<?php echo $okCode; ?>";		
			window.onload = clrChkBox;		
        </script>
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
    <form method = 'POST' action = 'gcm_main.php/?push=1' onsubmit="return validateForm();">
		<div>
			Üzenet naptárba?:
			<input type = "checkbox" value="Event" name="chkIsEvent" id="chkIsEvent" onchange="chkIsEventOnChange(this)"/>
		</div>
        <div>
			
            <textarea rows = "3" name = "message" id="txtMsg" cols = "70" placeholder = "Ide írja az üzenetet" ></textarea>
        </div>
		<p>
		<span style="display:none" id="spnDate">
            <span class="calendar-icon"></span>
            <input readonly name="calendar-input" id="calendar-input" placeholder="Kattintson az ikonra">
			<span class="error">* <?php echo $dateErr;?></span>
		</span>
        </p>
		
		<br><br><br>
        <div>
            <input type = "submit" value = "Üzenet elküldése">
        </div>

    </form>
	<script src="src/datepickr.min.js"></script>
	<script>
	function chkIsEventOnChange(element)
	{
		//var php_okCode1 =  "<?php echo $okCode; ?>";
			element.checked ? document.getElementById("spnDate").style.display = 'block' : document.getElementById("spnDate").style.display = 'none';    
	}
	
	// datepickr on an icon, using altInput to store the value
    // altInput must be a direct reference to an input element (for now)
    datepickr('.calendar-icon', { dateFormat: 'Y.m.d', altInput: document.getElementById('calendar-input') });
	</script>
    </body>
</html>