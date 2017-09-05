<?php
	error_reporting(0);
	require "initDB.php";

	//echo 'YOLO,';
//this is the upload folder
$upload_path_documents = "/storage/ssd2/270/1892270/public_html/documents/";
$upload_path_pictures = "/storage/ssd2/270/1892270/public_html/pictures/";

//the upload url
$upload_url_documents = 'https://anishsaha12.000webhostapp.com/documents/';
$upload_url_pictures = 'https://anishsaha12.000webhostapp.com/pictures/';

//response array
$response = array();


if($_SERVER['REQUEST_METHOD']=='POST'){

    //echo ' level1,';

    //checking the required parameters from the request
    if(isset($_POST['name']) and isset($_FILES['pdf']['name'])){
        //echo ' level2,';
        //getting name from the request
        $name = $_POST['name'];

        //getting file info from the request
        $fileinfo = pathinfo($_FILES['pdf']['name']);

        //getting the file extension
        $extension = $fileinfo['extension'];

        //file url to store in the database
	if($extension == 'JPG' || $extension == 'jpg' ){
        	$file_url = $upload_url_pictures . $name . '.' . $extension;
		//file path to upload in the server
        	$file_path = $upload_path_pictures . $name . '.'. $extension;

	}else if($extension == 'msg'){
        	$file_url = 'nil';
		$file_path = 'nil';
	}else{
                //echo ' level-pdf,';
        	$file_url = $upload_url_documents . $name . '.' . $extension;
		//file path to upload in the server
        	$file_path = $upload_path_documents . $name . '.'. $extension;
	}


        //trying to save the file in the directory
        try{
	    if($file_path == 'nil'){
	    }else{
            	//saving the file
            	move_uploaded_file($_FILES['pdf']['tmp_name'],$file_path);
	    }
            $sql = "INSERT INTO `files` (`id`, `url`, `name`, `extension`, `content`) VALUES (NULL, '$file_url', '$name', '$extension', NULL);";

            //adding the path and name to database
            if(mysqli_query($con,$sql)){

                //filling response array with values
                $response['error'] = false;
                $response['url'] = $file_url;
                $response['name'] = $name;
            }
            //if some error occurred
        }catch(Exception $e){
            $response['error']=true;
            $response['message']=$e->getMessage();
        } 
        //closing the connection
        mysqli_close($con);
    }else{
        $response['error']=true;
        $response['message']='Please choose a file';
    }
    
    //displaying the response
    echo json_encode($response);
}else{
    $response['error']=true;
    $response['message']='Wrong Request Method';
    echo json_encode($response);
}