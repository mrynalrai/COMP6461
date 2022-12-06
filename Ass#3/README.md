# COMP 6461
# LAB ASSIGNMENT 3
## Mrinal Rai and Akshay Dhabale

Content - Overview, How to Run, Test Cases

## Overview 
### Purpose
- Re-implement the HTTP client and the HTTP remote file 
-manager of Assignments #1 and #2 respectively using UDP protocol.

### Run Router
Run router with both drop rate and delay

```router --port=3000 --drop-rate=0.2 --max-delay=10ms --seed=1```
##### Output
The output of above command is:

```javascript
11:46:36.433789 config: drop-rate=0.20, max-delay=10ms, seed=1
11:46:36.438095 router is listening at :3000
```

Run router with delay only
```router --port=3000 --max-delay=10ms --seed=1```

The output of above command is:

```javascript
12:08:07.142174 config: drop-rate=0.00, max-delay=10ms, seed=1
12:08:07.147123 router is listening at :3000
```

Run router with both drop rate only

```router --port=3000 --drop-rate=0.2 --seed=1```

##### Output
The output of above command is:
```javascript
12:16:39.114643 config: drop-rate=0.20, max-delay=0s, seed=1
12:16:39.119763 router is listening at :3000
```




### Syntax

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
Server is up and it assign to port Number: 8080
```
Now run the ClientFTP.java file for testing below commands. This file can be run multiple time to connect multiple clients.
#### 2. a) Get to access the list of files in the directory (With delayed and drop)
```httpc http://localhost:8080/get/```
##### Output
The output of above command is:
```javascript
Sending Hi from Client
sending request to Router...>
No response after timeout
Sending again
httpc http://localhost:8080/get/
No response after timeout
Sending again
httpc http://localhost:8080/get/
No response after timeout
Sending again
httpc http://localhost:8080/get/
No response after timeout
Sending again
httpc http://localhost:8080/get/
HTTP/1.1 200 OK
Connection: keep-alive
Server: httpfs/1.0.0
Date: 2022/12/04 11:49:44
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
	"url": "http://localhost:8080/get/"
}
Connection closed..!
OK sent
```
#### 2. b) Get to access the list of files in the directory (With delayed only)
```httpc http://localhost:8080/get/```
##### Output
The output of above command is:
```javascript
Sending Hi from Client
sending request to Router...>
HTTP/1.1 200 OK
Connection: keep-alive
Server: httpfs/1.0.0
Date: 2022/12/04 12:09:59
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
	"url": "http://localhost:8080/get"
}
Connection closed..!
OK sent
```
#### 3.a) Provide filename to get file contents (With delayed and drop)
```httpc http://localhost:8080/get/file1.txt```
##### Output
The output of above command is:
```javascript
Sending Hi from Client
sending request to Router...>
HTTP/1.1 200 OK
Connection: keep-alive
Server: httpfs/1.0.0
Date: 2022/12/04 11:51:38
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
	"url": "http://localhost:8080/get/file1.txt"
}
Received
No response after timeout
Sending again
Received
No response after timeout
Sending again
Received
No response after timeout
Sending again
Received
Connection closed..!
OK sent
```

#### 3.b) Provide filename to get file contents (with drop rate only)
```httpc http://localhost:8080/get/file1.txt```
##### Output
The output of above command is:
```javascript
Sending Hi from Client
No response after timeout
Sending again
Hi from Client
No response after timeout
Sending again
Hi from Client
sending request to Router...>
HTTP/1.1 200 OK
Connection: keep-alive
Server: httpfs/1.0.0
Date: 2022/12/04 12:20:02
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
	"url": "http://localhost:8080/get/file1.txt"
}
Connection closed..!
OK sent
```

#### 4. Post operation to create new file at FTP server directory
```httpc http://localhost:8080/post/test1.txt -d '{"Assignment":2}'```
##### Output
The output of above command is:
```javascript
Sending Hi from Client
sending request to Router...>
HTTP/1.1 202 NEW FILE CREATED
Connection: keep-alive
Server: httpfs/1.0.0
Date: 2022/12/04 11:56:01
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
	"url": "http://localhost:8080/post/test1.txt"
}
Connection closed..!
OK sent
```

And test1.txt file will contain the actual output as below:
```javascript
{"Assignment":2}
```

#### 5. Content-type with get
```httpc -h Content-Type:txt http://localhost:8080/get/```
##### Output
The output of above command is:
```javascript
Sending Hi from Client
sending request to Router...>
HTTP/1.1 200 OK
Connection: keep-alive
Server: httpfs/1.0.0
Date: 2022/12/04 11:54:22
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
	"url": "http://localhost:8080/get/"
}
Connection closed..!
OK sent
```
## Test Cases
Sample test case commands are present inside the ```commands.txt``` file
