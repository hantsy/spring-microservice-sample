# Spring Microservice Sample

This demo application show you how to build an microservice application via Spring Boot.

There are three services in this demo.

* **auth-service** is the service for signin, signup and siginout.
* **user-service** is responsible for user management.
* **post-service** is the APIs for a simple CMS, including posts and comments.
* **nginx** includes the Dockerfile and nginx configuration file which use nginx as a reverse proxy for all services in a staging or production environment.


## Prerequisites

This demo sample is built on Spring stack, including Spring Boot, Spring Data, Spring Security, etc. Following the 12 factors application guide, I suggest to use Docker in both development and production enviroment to make sure the same codebase working in different environments.

Following the installation guide from [Docker official website](https://www.docker.com), install the latest Docker, Docker Compose and Docker Machine into your local system.

## Setup local development environment

Start up dependent servers via `docker-compose` command.

```
docker-compose up
```

### Docker Toolbox Notes

If you are using Docker Toolbox, create a new machine for this project.

```
$ docker-machine create -d virtualbox --engine-registry-mirror https://docker.mirrors.ustc.edu.cn springms
```

The `--engine-registry-mirror https://docker.mirrors.ustc.edu.cn` will add a docker registry mirror settings in docker-machine config. For Chinese users, it will speed up the Docker images downloading.

Then switch to the new created machine **springms**.

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

For development stage, you can run the services one by one.

```
mvn spring-boot:run // run in user-service, auth-service, post-service
```

The following services will be provided.

|Service|Url|Description|
|---|---|---|
|auth-service|http://localhost:8000/user,http://localhost:8000/auth|Authentication APIs(signin, signup, signout), user info|
|user-service|http://localhost:8001/users|User management APIs|
|post-service|http://localhost:8002/posts|Post and comment APIs|


The authentication flow is:

1. Get authentication from **auth-service**, and get `X-Auth-Token` from response headers.
2. Add `X-Auth-Token` to http request header to access the protected resources.

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
{"name":"user","roles":["USER"]}* Connection #0 to host localhost left intact
```

You will see a `X-Auth-Token` header in the response.

Put this header into request header when you access the protected resources in another resource server.

```
curl -v  http://localhost:8001/user -H "x-auth-token: 49090ba7-e641-45e3-935b-894a43b85f62"
``` 

Try to add some posts data:

```
>curl -v  http://localhost:8002/posts 
-H "x-auth-token:  49090ba7-e641-45e3-935b-894a43b85f62" 
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
> x-auth-token:  49090ba7-e641-45e3-935b-894a43b85f62
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

## Run all services via Docker Compose

Run all services in your local system or a staging server.

Build the project via `mvn` command.

```
mvn clean package -DskipTests
```

Then run the following command to run all services.

```
docker-compose -f docker-compose.yml -f docker-compose.local.yml up
```

Due we have run a Nginx a reverse proxy, all APIs can be accessed through a single entry. 

The following services will be provided.

|Service|Url|Description|
|---|---|---|
|auth-service|http://localhost/user,http://localhost/auth|Authentication APIs(signin, signup, signout), user info|
|user-service|http://localhost/users|User management APIs|
|post-service|http://localhost/posts|Post and comment APIs|

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
< Location: http://localhost/posts/test-post-2
<
* Connection #0 to host localhost left intact
```

Verify the created posts.

```
curl -v  http://localhost/posts  -H "Accpet:application/json"
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

## Docker Swarm

Use Docker Machine to create multi nodes. In order to demonstrate running this project in Swarm mode, we created two managers and three workers.

```
$ docker-machine create -d virtualbox --engine-registry-mirror https://docker.mirrors.ustc.edu.cn manager1
$ docker-machine create -d virtualbox --engine-registry-mirror https://docker.mirrors.ustc.edu.cn manager2
$ docker-machine create -d virtualbox --engine-registry-mirror https://docker.mirrors.ustc.edu.cn worker1
$ docker-machine create -d virtualbox --engine-registry-mirror https://docker.mirrors.ustc.edu.cn worker2
$ docker-machine create -d virtualbox --engine-registry-mirror https://docker.mirrors.ustc.edu.cn worker3
```

Show the created machines.

```
$ docker-machine ls
NAME       ACTIVE   DRIVER       STATE     URL                         SWARM   DOCKER        ERRORS
default    *        virtualbox   Running   tcp://192.168.99.100:2376           v17.05.0-ce
manager1   -        virtualbox   Running   tcp://192.168.99.101:2376           v17.05.0-ce
manager2   -        virtualbox   Running   tcp://192.168.99.102:2376           v17.05.0-ce
springms   -        virtualbox   Stopped                                       Unknown
worker1    -        virtualbox   Running   tcp://192.168.99.103:2376           v17.05.0-ce
worker2    -        virtualbox   Running   tcp://192.168.99.104:2376           v17.05.0-ce
worker3    -        virtualbox   Running   tcp://192.168.99.105:2376           v17.05.0-ce
```

Switch to `manager1`.

```
eval "$(docker-manager env manager1)"
```

Initializes Docker Swarm.

```
$ docker swarm init --listen-addr 192.168.99.101 --advertise-addr 192.168.99.101
Swarm initialized: current node (t36lxk020fasw5tdes4gm9ucf) is now a manager.

To add a worker to this swarm, run the following command:

    docker swarm join \
    --token SWMTKN-1-10bwwj2u6erepp9oc0qlkwao4o79vogifon51qkhdqfsl7zkkd-810eddvkzt2g8vvxb4gul4pnb \
    192.168.99.101:2377

To add a manager to this swarm, run 'docker swarm join-token manager' and follow the instructions.
```

We want to add *manager2* as manager in this swarm. Follow the above info. Execute `docker swarm join-token manager`, it will show the guide to add more managers.

```
$ docker swarm join-token manager
To add a manager to this swarm, run the following command:

    docker swarm join \
    --token SWMTKN-1-10bwwj2u6erepp9oc0qlkwao4o79vogifon51qkhdqfsl7zkkd-4xus5y6wa7a4ass0f5bt20pym \
    192.168.99.101:2377
```
	
Let us switch to *manager2*.

```
eval "$(docker-machine env manager2)"
```

Copy and paste the `docker swarm join` command lines and execute it.

```
$ docker swarm join \
     --token SWMTKN-1-10bwwj2u6erepp9oc0qlkwao4o79vogifon51qkhdqfsl7zkkd-4xus5y6wa7a4ass0f5bt20pym \
     192.168.99.101:2377
This node joined a swarm as a manager.
```

Switch to worker1, worker2, and worker3, join this swarm as a worker.

```
    docker swarm join \
    --token SWMTKN-1-10bwwj2u6erepp9oc0qlkwao4o79vogifon51qkhdqfsl7zkkd-810eddvkzt2g8vvxb4gul4pnb \
    192.168.99.101:2377
```

Switch to any **manager** machine, and you can show all running nodes.

```
$ docker node ls
ID                            HOSTNAME            STATUS              AVAILABILITY        MANAGER STATUS
9d07by6czpem6hx55ke3ks1v1     manager2            Ready               Active              Reachable
er9klqvww0kdwyfaxr5f7n15l     worker1             Ready               Active
hsmaugexj4l7p5ighl9nega8q     worker2             Ready               Active
lknqw5dg5jyxw3j2camcpnb0v *   manager1            Ready               Active              Leader
ovqfs7ymrgbeyfqu8db8n6apc     worker3             Ready               Active
```

Switch to any **manager** machine, deploy all service via `docker stack` command.

```
docker stack deploy -c docker-stack.yml blogapp
```

The services will be scheduled to deploy in this swarm.


The *docker-stack.yml* file includes a `visualizer` service to visualize all services. It can be accessed via http://&lt;any manager ip&gt;:8080, you will see the deployment progress.

![visualizer](./docker-viz.png)

Remove this stack by the following command.

```
docker stack rm blogapp
```