<?php 
	error_reporting(0);
	require "initDB.php";

	//$name = $_POST["name"];

	//$name = "anish";

	$sql = "SELECT * FROM `pictures` ORDER BY `id`;";

	$result = mysqli_query($con, $sql);

	$response = array();

	while($row = mysqli_fetch_array($result)){
		$response = array("id"=>$row[0],"name"=>$row[1]);
                $output[]=$response;
	}

	echo json_encode($output);

?>