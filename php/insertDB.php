<?php
	error_reporting(0);
	require "initDB.php";

	$name = $_POST["name"];


	//$name = "exhibitionMe";

	$sql = "INSERT INTO `pictures` (`id`,`name`) VALUES (NULL, '".$name."');";
	if(!mysqli_query($con, $sql)){
		echo '{"message":"Unable to save the data to the database."}';
	}

?>