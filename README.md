# COMP 6461
# LAB ASSIGNMENT 1
## By Pranoti Mulay

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
 "User-Agent": "Concordia-HTTP/1.0"
 },
 "url": "http://httpbin.org/get?course=networking&assignment=1"
}
```
#### 2. Get with verbose option
```httpc get -v 'http://httpbin.org/get?course=networking&assignment=1'```
##### Output
The output of above command is:
```javascript
HTTP/1.1 200 OK
Server: nginx
Date: Fri, 1 Sep 2017 14:52:12 GMT
Content-Type: application/json
Content-Length: 255
Connection: close
Access-Control-Allow-Origin: *
Access-Control-Allow-Credentials: true{
 "args": {
 "assignment": "1",
 "course": "networking"
 },
 "headers": {
 "Host": "httpbin.org",
 "User-Agent": "Concordia-HTTP/1.0"
 },
 "url": "http://httpbin.org/get?course=networking&assignment=1"
}
```
#### 3. Post with inline data
```httpc post -h Content-Type:application/json --d '{"Assignment": 1}' http://httpbin.org/post```
##### Output
The output of above command is:
```javascript
{
 "args": {},
 "data": "{\"Assignment\": 1}",
 "files": {},
 "form": {},
 "headers": {
 "Content-Length": "17",
 "Content-Type": "application/json",
 "Host": "httpbin.org",
 "User-Agent": "Concordia-HTTP/1.0"
 },
 "json": {
 "Assignment": 1
 },
 "url": "http://httpbin.org/post"
}
```
## How to Run
Run the ```main``` method present in class ```MainTester.java```. Type the command in the console, and press enter to see the results.

## Test Cases
Sample test case commands are present inside the ```sample-commands.txt``` file
