# Spring Microservice Sample

This demo application show you how to build an microservice application via Spring Boot.


## Prerequisites

Following the installation guide from [Docker official website](https://www.docker.com), install the latest Docker, Docker Compose and Docker Machine into your local system.

Start up dependent servers via `docker-compose` command.

```
docker-compose up
```

If you are using Docker Toolbox, create a new machine for this project.

```
$ docker-machine create -d virtualbox --engine-registry-mirror https://docker.mirrors.ustc.edu.cn springms
```

Switch to the new created machine, `springms`.

```
eval "$(docker-machine env springms)"
```

Forward the virtualbox ports to local system, thus you can access the servers via `localhost` instead of the machine ip addresss.

```
 VBoxManage modifyvm "springms" --natpf1 "tcp-port3306,tcp,,3306,,3306"
 VBoxManage modifyvm "springms" --natpf1 "tcp-port3307,tcp,,3307,,3307"
 VBoxManage modifyvm "springms" --natpf1 "tcp-port5672,tcp,,5672,,5672"
 VBoxManage modifyvm "springms" --natpf1 "tcp-port15672,tcp,,15672,,15672"
 VBoxManage modifyvm "springms" --natpf1 "tcp-port6379,tcp,,6379,,6379"
 VBoxManage modifyvm "springms" --natpf1 "tcp-port27017,tcp,,27017,,27017"
```

Then run the dependent servers via `docker-compose` command line.


## Run

For development stage, you can run the services one by one.

```
mvn spring-boot:run // run in user-service, auth-service, post-service
```

## Try

When all service are running successfully, try to get authentication.

```
curl -v  http://localhost:8000/user -u user:test123
* timeout on name lookup is not supported
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8000 (#0)
* Server auth using Basic with user 'user'
> GET /user HTTP/1.1
> Host: localhost:8000
> Authorization: Basic dXNlcjp0ZXN0MTIz
> User-Agent: curl/7.54.0
> Accept: */*
>
< HTTP/1.1 200
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< X-Auth-Token: 49090ba7-e641-45e3-935b-894a43b85f62
< Content-Type: application/json;charset=UTF-8
< Transfer-Encoding: chunked
< Date: Mon, 15 May 2017 09:29:14 GMT
<
{"authorities":[{"authority":"USER"}],"details":{"remoteAddress":"0:0:0:0:0:0:0:1","sessionId":null},"authenticated":true,"principal":{"password":null,"username":"user","authorities":[{"authority":"USER"}],"accountNonExpired":true,"accountNonLocked":true,"credentialsNonExpired":true,"enabled":true,"name":"user"},"credentials":null,"name":"user"}* Connection #0 to host localhost left intact
```

You will see a `X-Auth-Token` header in the response.

Put this header into request header when you access the protected resources in another resource server.

```
curl -v  http://localhost:8001/user -H "x-auth-token: 49090ba7-e641-45e3-935b-894a43b85f62"
``` 

Try to add some posts data:

```
>curl -v  http://localhost:8002/posts 
-H "x-auth-token:  44586f33-4c51-4d8f-ab12-7ad25a1c3a30" 
-H "Accept: application/json" 
-H "Content-Type: application/json;charset=UTF-8" 
-X POST 
-d "{\"title\": \"test post\", \"content\":\"test content of post\"}"
```

You will see the result. It returns 201 status, and set `Location` header to the new created `Post`.

```
Note: Unnecessary use of -X or --request, POST is already inferred.
* timeout on name lookup is not supported
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8002 (#0)
> POST /posts HTTP/1.1
> Host: localhost:8002
> User-Agent: curl/7.54.0
> x-auth-token:  44586f33-4c51-4d8f-ab12-7ad25a1c3a30
> Accept: application/json
> Content-Type: application/json;charset=UTF-8
> Content-Length: 56
>
* upload completely sent off: 56 out of 56 bytes
< HTTP/1.1 201
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< Location: http://localhost:8002/posts/4
< Content-Length: 0
< Date: Thu, 18 May 2017 06:54:40 GMT
```

Fetch the new created post.

```
curl -v  http://localhost:8002/posts/4 -H "Accept: application/json"
* timeout on name lookup is not supported
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8002 (#0)
> GET /posts/4 HTTP/1.1
> Host: localhost:8002
> User-Agent: curl/7.54.0
> Accept: application/json
>
< HTTP/1.1 200
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< Content-Type: application/json;charset=UTF-8
< Transfer-Encoding: chunked
< Date: Thu, 18 May 2017 06:59:42 GMT
<
{"id":4,"title":"test post","content":"test content of post","status":"DRAFT","author":null,"createdDate":null}*
```

## Bonus 

Run all services in your local system or a staging server.

Build the project via `mvn` command.

```
mvn clean package -DskipTests
```

Then run the following command to run all services.

```
docker-compose -f docker-compose.yml -f docker-compose.local.yml up
```

Now test the APIs in a single entry.

Get authentication.

```
curl -v  http://localhost/user -u user:test123

>
< HTTP/1.1 200
< Server: nginx/1.13.0
< Date: Thu, 25 May 2017 06:49:52 GMT
< Content-Type: application/json;charset=UTF-8
< Transfer-Encoding: chunked
< Connection: keep-alive
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< X-Auth-Token: 8b185a90-37db-444a-832b-6cbcd6db6df8
<
{"name":"user","roles":["USER"]}* Connection #0 to host localhost left intact
```

Create a new post.

```
curl -v  http://localhost/posts -X POST -H "X-Auth-Token: 8b185a90-37db-444a-832b-6cbcd6db6df8" -H "Content-Type:application/json" -d "{\"title\": \"test post\", \"content\":\"test content of post\"}"
Note: Unnecessary use of -X or --request, POST is already inferred.
* timeout on name lookup is not supported
*   Trying ::1...
* TCP_NODELAY set
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to localhost (127.0.0.1) port 80 (#0)
> POST /posts HTTP/1.1
> Host: localhost
> User-Agent: curl/7.54.0
> Accept: */*
> X-Auth-Token: 8b185a90-37db-444a-832b-6cbcd6db6df8
> Content-Type:application/json
> Content-Length: 56
>
* upload completely sent off: 56 out of 56 bytes
< HTTP/1.1 201
< Server: nginx/1.13.0
< Date: Thu, 25 May 2017 06:52:46 GMT
< Content-Length: 0
< Connection: keep-alive
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< Location: http://localhost/posts/1
<
* Connection #0 to host localhost left intact
```

Create another new post.

```
curl -v  http://localhost/posts -X POST -H "X-Auth-Token: 8b185a90-37db-444a-832b-6cbcd6db6df8" -H "Content-Type:application/json" -d "{\"title\": \"test post 2\", \"content\":\"test content of post 2\"}"
Note: Unnecessary use of -X or --request, POST is already inferred.
* timeout on name lookup is not supported
*   Trying ::1...
* TCP_NODELAY set
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to localhost (127.0.0.1) port 80 (#0)
> POST /posts HTTP/1.1
> Host: localhost
> User-Agent: curl/7.54.0
> Accept: */*
> X-Auth-Token: 8b185a90-37db-444a-832b-6cbcd6db6df8
> Content-Type:application/json
> Content-Length: 60
>
* upload completely sent off: 60 out of 60 bytes
< HTTP/1.1 201
< Server: nginx/1.13.0
< Date: Thu, 25 May 2017 06:53:29 GMT
< Content-Length: 0
< Connection: keep-alive
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< Location: http://localhost/posts/2
<
* Connection #0 to host localhost left intact
```

Verify the created posts.

```
E:\hantsylabs\spring-microservice-sample>curl -v  http://localhost/posts  -H "Accpet:application/json"
* timeout on name lookup is not supported
*   Trying ::1...
* TCP_NODELAY set
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to localhost (127.0.0.1) port 80 (#0)
> GET /posts HTTP/1.1
> Host: localhost
> User-Agent: curl/7.54.0
> Accept: */*
> Accpet:application/json
>
< HTTP/1.1 200
< Server: nginx/1.13.0
< Date: Thu, 25 May 2017 06:53:58 GMT
< Content-Type: application/json;charset=UTF-8
< Transfer-Encoding: chunked
< Connection: keep-alive
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
<
{
  "content" : [ {
    "title" : "test post 2",
    "slug" : "test-post-2",
    "status" : "DRAFT",
    "id" : 2,
    "createdDate" : "2017-05-25T06:53:30",
    "author" : {
      "username" : "user"
    }
  }, {
    "title" : "test post",
    "slug" : "test-post",
    "status" : "DRAFT",
    "id" : 1,
    "createdDate" : "2017-05-25T06:52:45",
    "author" : {
      "username" : "user"
    }
  } ],
  "pageable" : {
    "sort" : {
      "sorted" : true,
      "unsorted" : false
    },
    "pageSize" : 10,
    "pageNumber" : 0,
    "offset" : 0,
    "paged" : true,
    "unpaged" : false
  },
  "last" : true,
  "totalElements" : 2,
  "totalPages" : 1,
  "sort" : {
    "sorted" : true,
    "unsorted" : false
  },
  "numberOfElements" : 2,
  "first" : true,
  "size" : 10,
  "number" : 0
}* Connection #0 to host localhost left intact
```

Create a comment for "test post 2".

```
curl -v  http://localhost/posts/test-post-2/comments -X POST -H "X-Auth-Token: 8b185a90-37db-444a-832b-6cbcd6db6df8" -H "Content-Type:application/json" -d "{ \"content\":\"conmment content of post 2\"}"
Note: Unnecessary use of -X or --request, POST is already inferred.
* timeout on name lookup is not supported
*   Trying ::1...
* TCP_NODELAY set
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to localhost (127.0.0.1) port 80 (#0)
> POST /posts/test-post-2/comments HTTP/1.1
> Host: localhost
> User-Agent: curl/7.54.0
> Accept: */*
> X-Auth-Token: 8b185a90-37db-444a-832b-6cbcd6db6df8
> Content-Type:application/json
> Content-Length: 41
>
* upload completely sent off: 41 out of 41 bytes
< HTTP/1.1 201
< Server: nginx/1.13.0
< Date: Thu, 25 May 2017 06:54:59 GMT
< Content-Length: 0
< Connection: keep-alive
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< Location: http://localhost/posts/test-post-2/comments/3
<
* Connection #0 to host localhost left intact
```

Create another comment.

```
curl -v  http://localhost/posts/test-post-2/comments -X POST -H "X-Auth-Token: 8b185a90-37db-444a-832b-6cbcd6db6df8" -H "Content-Type:application/json" -d "{ \"content\":\"conmment content of post, another comment\"}"
Note: Unnecessary use of -X or --request, POST is already inferred.
* timeout on name lookup is not supported
*   Trying ::1...
* TCP_NODELAY set
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to localhost (127.0.0.1) port 80 (#0)
> POST /posts/test-post-2/comments HTTP/1.1
> Host: localhost
> User-Agent: curl/7.54.0
> Accept: */*
> X-Auth-Token: 8b185a90-37db-444a-832b-6cbcd6db6df8
> Content-Type:application/json
> Content-Length: 56
>
* upload completely sent off: 56 out of 56 bytes
< HTTP/1.1 201
< Server: nginx/1.13.0
< Date: Thu, 25 May 2017 06:55:21 GMT
< Content-Length: 0
< Connection: keep-alive
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< Location: http://localhost/posts/test-post-2/comments/4
<
* Connection #0 to host localhost left intact
```

Verify the comments.

```
curl -v  http://localhost/posts/test-post-2/comments  -H "Accpet:application/json"
* timeout on name lookup is not supported
*   Trying ::1...
* TCP_NODELAY set
* connect to ::1 port 80 failed: Connection refused
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to localhost (127.0.0.1) port 80 (#0)
> GET /posts/test-post-2/comments HTTP/1.1
> Host: localhost
> User-Agent: curl/7.54.0
> Accept: */*
> Accpet:application/json
>
< HTTP/1.1 200
< Server: nginx/1.13.0
< Date: Thu, 25 May 2017 06:55:35 GMT
< Content-Type: application/json;charset=UTF-8
< Transfer-Encoding: chunked
< Connection: keep-alive
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
<
{
  "content" : [ {
    "content" : "conmment content of post, another comment",
    "id" : 4,
    "createdDate" : "2017-05-25T06:55:22",
    "author" : {
      "username" : "user"
    }
  }, {
    "content" : "conmment content of post 2",
    "id" : 3,
    "createdDate" : "2017-05-25T06:54:59",
    "author" : {
      "username" : "user"
    }
  } ],
  "pageable" : {
    "sort" : {
      "sorted" : true,
      "unsorted" : false
    },
    "pageSize" : 10,
    "pageNumber" : 0,
    "offset" : 0,
    "paged" : true,
    "unpaged" : false
  },
  "last" : true,
  "totalElements" : 2,
  "totalPages" : 1,
  "sort" : {
    "sorted" : true,
    "unsorted" : false
  },
  "numberOfElements" : 2,
  "first" : true,
  "size" : 10,
  "number" : 0
}* Connection #0 to host localhost left intact
```