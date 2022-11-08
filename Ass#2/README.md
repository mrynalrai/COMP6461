# COMP 6461
# LAB ASSIGNMENT 2
## Mrinal Rai and Akshay Dhabale

Content - Overview, How to Run, Test Cases

## Overview 
### Purpose
- to implement the basic functionalities of FTP Server, the functionalities that are related to HTTP FTP protocol.
- implement FTP client library for GET and POST using Sockets

### Syntax
```httpfs [-v] [-p PORT] [-d PATH-TO-DIR]```
1. **Option -v** enables a verbose output from the command-line. Verbosity could be useful
for testing and debugging stages where you need more information to do so. You
define the format of the output. However, you are expected to print all the status, and
its headers, then the contents of the response.

2. **-p port** determines the port for HTTP server.


3. **-d option** provide the FTP server directory

Run the Server.java file and provide the port and directory using below command.
## Examples
#### 1. Run Server.java file
```httpfs -v -p 8080 -d D://FTP```
##### Output
The output of above command is:
```javascript
Server Directory------>>>> D:\Concordia Academics\LA02\Master
>httpfs -v -p 8080 -d D://FTP
Dir ==>>>>> D://FTP
Server is up and it assign to port Number: 8080
```
Now run the ClientFTP.java file for testing below commands. This file can be run multiple time to connect multiple clients.
#### 2. Get to access the list of files in the directory
```httpc http://localhost:8080/get/```
##### Output
The output of above command is:
```javascript
Sending request to Socket Server
HTTP/1.1 200 OK
Connection: keep-alive
Server: httpfs/1.0.0
Date: 2022/11/07 17:24:58
Access-Control-Allow-Origin: *
Access-Control-Allow-Credentials: true
Via : 1.1 vegur

{
	"args":{},
	"headers": {
		"Connection": "close",
		"Host": "localhost"
	},
	"files": { file1.txt , file2.txt },
	"origin": "192.168.2.19",
	"url": "httpc http://localhost:8080/get/"
}
```
#### 3. Provide filename to get file contents
```httpc http://localhost:8080/get/file1.txt```
##### Output
The output of above command is:
```javascript
Sending request to Socket Server
HTTP/1.1 200 OK
Connection: keep-alive
Server: httpfs/1.0.0
Date: 2022/11/07 17:25:56
Access-Control-Allow-Origin: *
Access-Control-Allow-Credentials: true
Via : 1.1 vegur

{
	"args":{},
	"headers": {
		"Connection": "close",
		"Host": "localhost"
	},
	"data": "Value1",
	"origin": "192.168.2.19",
	"url": "httpc http://localhost:8080/get/file1.txt"
}
```

#### 4. Post operation to create new file at FTP server directory
```httpc http://localhost:8080/post/test1.txt -d '{"Assignment":2}'```
##### Output
The output of above command is:
```javascript
Sending request to Socket Server
HTTP/1.1 202 NEW FILE CREATED
Connection: keep-alive
Server: httpfs/1.0.0
Date: 2022/11/07 17:27:26
Access-Control-Allow-Origin: *
Access-Control-Allow-Credentials: true
Via : 1.1 vegur

{
	"args":{},
	"headers": {
		"Connection": "close",
		"Host": "localhost"
	},
	"origin": "192.168.2.19",
	"url": "httpc http://localhost:8080/post/test1.txt -d '{"Assignment":2}'"
}
```

And test1.txt file will contain the actual output as below:
```javascript
{"Assignment":2}
```
#### 5. Post with overwritting existing file
```httpc -h overwrite:true http://localhost:8080/post/test1.txt -d '{"test":1235}```
##### Output
The output of above command is:
```javascript
Sending request to Socket Server
HTTP/1.1 201 FILE OVER-WRITTEN
Connection: keep-alive
Server: httpfs/1.0.0
Date: 2022/11/07 17:30:31
Access-Control-Allow-Origin: *
Access-Control-Allow-Credentials: true
Via : 1.1 vegur

{
	"args":{},
	"headers": {
		"Connection": "close",
		"Host": "localhost"
	},
	"origin": "192.168.2.19",
	"url": "httpc -h overwrite:true http://localhost:8080/post/test1.txt -d '{"test":1235}"
}
```

And test1.txt file will contain the actual output as below:
```javascript
{"test":1235}
```

#### 6. Content-type with get
```httpc -h Content-Type:txt http://localhost:8080/get/```
##### Output
The output of above command is:
```javascript
Sending request to Socket Server
HTTP/1.1 200 OK
Connection: keep-alive
Server: httpfs/1.0.0
Date: 2022/11/07 17:33:22
Access-Control-Allow-Origin: *
Access-Control-Allow-Credentials: true
Via : 1.1 vegur

{
	"args":{},
	"headers": {
		"Connection": "close",
		"Host": "localhost"
	},
	"files": { file1.txt , file2.txt , test1.txt },
	"origin": "192.168.2.19",
	"url": "httpc -h Content-Type:txt http://localhost:8080/get/"
}
```
## Test Cases
Sample test case commands are present inside the ```commands.txt``` file
