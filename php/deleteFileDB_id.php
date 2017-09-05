<?php
    // connect to mysql database
    error_reporting(0);
    require "initDB.php";

    if(isset($_POST["id"])) {
        $id = $_POST["id"];
        
	$sql = "SELECT `extension`,`name` FROM `files` WHERE `id`='$id';";
        $result = mysqli_query($con, $sql);
        $row = mysqli_fetch_array($result);
        $ext = $row[0];
        $name = $row[1];


	if($ext){	//no error
		if($ext == 'jpg'){    //pictures
                        $filePath = "/storage/ssd2/270/1892270/public_html/pictures/" . $name . "." . $ext; 
                         // path of the file to store
                         // check if file exits
                         if (file_exists($filePath)) {
                                     unlink($filePath); // delete the file
                         }
                }else if($ext == 'msg'){     //message

                }else{             //documents
                        $filePath = "/storage/ssd2/270/1892270/public_html/documents/" . $name . "." . $ext; 
                         // path of the file to store
                         // check if file exits
                         if (file_exists($filePath)) {
                                     unlink($filePath); // delete the file
                         }
                }
		// delete from files table the give id.
 
		$sql = "DELETE FROM `files` WHERE `id`='$id';";
		if(!mysqli_query($con, $sql)){
			$response = array();
			$response['error']=true;
			$response['message']='Could not delete';
			echo json_encode($response);
		}else{
			$response = array();
			$response['error']=false;
			$response['message']='Deleted';
			echo json_encode($response);
		} 
        }else{
		$response = array();
		$response['error']=true;
		$response['message']='Could not delete';
		echo json_encode($response);
	}
    } else {
        $response = array();
	$response['error']=true;
	$response['message']='Parameter missing';
	echo json_encode($response);
    }
    // close the connection
    mysql_close($con);
?>