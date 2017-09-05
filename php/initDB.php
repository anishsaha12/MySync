<?php

	error_reporting(0);

	$db_name = "XXXX";
	$mysql_user = "YYYY";
	$mysql_pass = "ZZZZ";
	$server_name = "localhost";

	$con = mysqli_connect($server_name, $mysql_user, $mysql_pass, $db_name);

	if(!$con){
		$response['error']=true;
	        $response['message']='Unable to connect to the database.';
	        echo json_encode($response);
	}

?>