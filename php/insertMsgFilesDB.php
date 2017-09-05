<?php
    // connect to mysql database
    error_reporting(0);
    require "initDB.php";

    if(isset($_POST["extension"]) && isset($_POST["content"])) {
        $extension = $_POST["extension"];
	$content = $_POST["content"];
        
	// insert into the files table the new message name.
	if($extension == 'msg'){
		$sql = "INSERT INTO `files` (`id`, `url`, `name`, `extension`, `content`) VALUES (NULL, NULL, NULL, '$extension', '$content');";
	        if(!mysqli_query($con, $sql)){
		        $response = array();
		        $response['error']=true;
		        $response['message']='Could not post';
		        echo json_encode($response);
	        }else{
		        $response = array();
		        $response['error']=false;
		        $response['message']='Success';
		        echo json_encode($response);
		}
	} 
        
    } else {
        $response = array();
	$response['error']=true;
	$response['message']='Parameters missing';
	echo json_encode($response);
    }
    // close the connection
    mysql_close($con);
?>