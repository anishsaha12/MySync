<?php
    // connect to mysql database
    error_reporting(0);
    require "initDB.php";

    // check if "image" and "name" is set 
    if(isset($_POST["image"]) && isset($_POST["name"])) {
        $name = $_POST["name"];
	$image = $_POST["image"];
        $decodedImage = base64_decode("$image");
        $filePath = "/storage/ssd2/270/1892270/public_html/pictures/" . $name . ".jpg"; // path of the file to store

        echo "file ".$filePath;
        
        // check if file exits
        if (file_exists($filePath)) {
            unlink($filePath); // delete the old file
        }else{
	        // insert into the pictures table the new image name.
		$sql = "INSERT INTO `pictures` (`id`,`name`) VALUES (NULL, '".$name."');";
	        if(!mysqli_query($con, $sql)){
		        echo '{"message":"Unable to save the data to the database."}';
	        }
	} 
        // add data to that file
        file_put_contents($filePath, $decodedImage);
        
    } else {
        echo 'not set';
    }
    // close the connection
    mysql_close($con);
?>