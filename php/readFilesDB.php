<?php 
	error_reporting(0);
	require "initDB.php";

	$sql = "SELECT * FROM `files` ORDER BY `id`;";

	$result = mysqli_query($con, $sql);

	$response = array();

	while($row = mysqli_fetch_array($result)){
		$response = array("id"=>$row[0], "url"=>$row[1] ,"name"=>$row[2], "extension"=>$row[3], "content"=>$row[4]);
                $output[]=$response;
	}

	echo json_encode($output);

?>