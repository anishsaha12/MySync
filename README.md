# MySync
A synchronization app

In order to make the app functionable, follow the steps listed:
1. Create an account at https://www.000webhost.com/free-website-sign-up
2. In 000webhost, open "File Manager".
3. In the File Manager, under the public_html folder, create new folders: php, documents, pictures.
4. Leave the documents and pictures folders empty.
5. In side the php folder upload the files upload all files from the link: https://github.com/anishsaha12/MySync/tree/master/php
   without modifying their content or filenames.
6. In 000webhost, open "Manage Databases".
7. Create a new database with name "syncdatabase".
8. Next to the created database shown in the list (DB Name and DB User are listed here. Keep note of these), click on Manage->PhpMyAdmin
9. Under the created database, create 2 new tables: "files", "pictures".
   The tables are to be created following the descriptions specified in the link-
   https://github.com/anishsaha12/MySync/tree/master/descriptionData   
10. Open the file initDB.php from the php folder and edit the following lines:
$db_name = "XXXX";        //**replace XXXX with DB Name from step 8**
$mysql_user = "YYYY";     //**replace YYYY with DB User from step 8**
$mysql_pass = "ZZZZ";     //**replace ZZZZ with the password you had chosen**
11. In AndroidStudio, go to the string resources. Edit the contents of the file:
"SERVER_ADDRESS" https://XXXX.000webhostapp.com/
"UPLOAD_URL" https://XXXX.000webhostapp.com/php/uploadAndUpdateDatabase.php
//*****************replace XXXX with your website name****************//
12. Execute and use the app.

P.S. You can upload files, pictures, messages to your app from a computer using Postman (a REST client).
You can view your uploaded files and pictures and messages using the following links from anywhere:
https://XXXX.000webhostapp.com/documents
https://XXXX.000webhostapp.com/pictures
https://XXXX.000webhostapp.com/php/readFilesDB.php
