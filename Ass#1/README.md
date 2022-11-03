# COMP 6461
# LAB ASSIGNMENT 1
## Mrinal Rai and Akshay Dhabale

Content - Overview, How to Run, Test Cases

## Overview 
### Purpose
- to implement the basic functionalities of cURL command line, the functionalities that are related to HTTP protocol.
- implement HTTP client library for GET and POST using Sockets

### Syntax
```httpc (get|post) [-v] (-h "k:v")* [-d inline-data] [-f file] URL```
1. **Option -v** enables a verbose output from the command-line. Verbosity could be useful
for testing and debugging stages where you need more information to do so. You
define the format of the output. However, you are expected to print all the status, and
its headers, then the contents of the response.

2. **URL** determines the targeted HTTP server. It could contain parameters of the HTTP
operation. For example, the URL 'https://www.google.ca/?q=hello+world' includes the
parameter q with "hello world" value.

3. To pass the headers value to your HTTP operation, you could use **-h option**. The latter
means setting the header of the request in the format "key: value." Notice that; you can
have multiple headers by having the -h option before each header parameter.

4. **-d option** gives the user the possibility to associate the body of the HTTP Request with the
inline data, meaning a set of characters for standard input.

5. Similarly, to **-d, -f** associate the body of the HTTP Request with the data from a given
file.

6. **get/post options** are used to execute GET/POST requests respectively. post should
have either -d or -f but not both. However, get option should not be used with the
options -d or -f.

## Help
```httpc help (get|post)```
## Examples
#### 1. Get with query parameters
```httpc get 'http://httpbin.org/get?course=networking&assignment=1'```
##### Output
The output of above command is:
```javascript
{
  "args": {
    "assignment": "1", 
    "course": "networking"
  }, 
  "headers": {
    "Host": "httpbin.org", 
    "X-Amzn-Trace-Id": "Root=1-6343846f-5f9af85836f79e980a1897e7"
  }, 
  "origin": "184.145.152.16", 
  "url": "http://httpbin.org/get?course=networking&assignment=1"
}
```

#### 2. Get with verbose option
```httpc get -v 'http://httpbin.org/get?course=networking&assignment=1'```
##### Output
The output of above command is:
```javascript
HTTP/1.1 200 OK
Date: Mon, 10 Oct 2022 02:33:54 GMT
Content-Type: application/json
Content-Length: 284
Connection: close
Server: gunicorn/19.9.0
Access-Control-Allow-Origin: *
Access-Control-Allow-Credentials: true

{
  "args": {
    "assignment": "1", 
    "course": "networking"
  }, 
  "headers": {
    "Host": "httpbin.org", 
    "X-Amzn-Trace-Id": "Root=1-63438492-15d8f098026b5f5242fa8bb3"
  }, 
  "origin": "184.145.152.16", 
  "url": "http://httpbin.org/get?course=networking&assignment=1"
}
```
#### 3. Get with header option
```httpc get -h Content-Type:application/json http://httpbin.org/get?course=networking&assignment=1```
##### Output
The output of above command is:
```javascript
[Content-Type:application/json]
{
  "args": {
    "assignment": "1", 
    "course": "networking"
  }, 
  "headers": {
    "Content-Type": "application/json", 
    "Host": "httpbin.org", 
    "X-Amzn-Trace-Id": "Root=1-634396e3-50b059b4503ae99c44dc0193"
  }, 
  "origin": "184.145.152.16", 
  "url": "http://httpbin.org/get?course=networking&assignment=1"
}
```

#### 4. Get with header option and output to output.txt file
```httpc get -v -h Content-Type:application/json 'http://httpbin.org/get?course=networking&assignment=1' -o output.txt```
##### Output
The output of above command is:
```javascript
outputFileName: output.txt
```

And Output.txt file will contain the actual output as below:
```javascript
HTTP/1.1 200 OK
Date: Mon, 10 Oct 2022 03:56:16 GMT
Content-Type: application/json
Content-Length: 325
Connection: close
Server: gunicorn/19.9.0
Access-Control-Allow-Origin: *
Access-Control-Allow-Credentials: true

{
  "args": {
    "assignment": "1", 
    "course": "networking"
  }, 
  "headers": {
    "Content-Type": "application/json", 
    "Host": "httpbin.org", 
    "X-Amzn-Trace-Id": "Root=1-634397e0-187c91f807dccf0e3b255764"
  }, 
  "origin": "184.145.152.16", 
  "url": "http://httpbin.org/get?course=networking&assignment=1"
}

```
#### 5. Post with inline data
```httpc post -h Content-Type:application/json -d '{"Assignment": 1}' http://httpbin.org/post```
##### Output
The output of above command is:
```javascript
[Content-Type:application/json]
{"Assignment": 1}
{
  "args": {}, 
  "data": "{\"Assignment\": 1}", 
  "files": {}, 
  "form": {}, 
  "headers": {
    "Content-Length": "17", 
    "Content-Type": "application/json", 
    "Host": "httpbin.org", 
    "X-Amzn-Trace-Id": "Root=1-634384d7-10a045260990ab656bf2396f"
  }, 
  "json": {
    "Assignment": 1
  }, 
  "origin": "184.145.152.16", 
  "url": "http://httpbin.org/post"
}
```

#### 6. Post with file data
```httpc post -v -f data-test.txt http://httpbin.org/post```
##### Output
The output of above command is:
```javascript
{"Assignment": 1,
"test": {
	"inner": "value"
}
}


HTTP/1.1 200 OK
Date: Mon, 10 Oct 2022 03:45:09 GMT
Content-Type: application/json
Content-Length: 436
Connection: close
Server: gunicorn/19.9.0
Access-Control-Allow-Origin: *
Access-Control-Allow-Credentials: true
{
  "args": {}, 
  "data": "{\"Assignment\": 1,\r\n\"test\": {\r\n\t\"inner\": \"value\"\r\n}\r\n}\r\n\r\n", 
  "files": {}, 
  "form": {}, 
  "headers": {
    "Content-Length": "57", 
    "Host": "httpbin.org", 
    "X-Amzn-Trace-Id": "Root=1-63439545-08d6690c50693615187d894c"
  }, 
  "json": {
    "Assignment": 1, 
    "test": {
      "inner": "value"
    }
  }, 
  "origin": "184.145.152.16", 
  "url": "http://httpbin.org/post"
}
```
#### 7. Post with both -f and -d option
```httpc post -v -f data-test.txt -d '{"Assignment": 1}' http://httpbin.org/post```
##### Output
The output of above command is:
```javascript
Invalid command, can't use -d and -f together
```
Above command results in the error as -d and -f can't be passed together
## How to Run
Run the ```main``` method present in class ```MainApplication.java```. Type the command in the console, and press enter to see the results.

## Test Cases
Sample test case commands are present inside the ```commands.txt``` file
