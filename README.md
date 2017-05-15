# Spring Microservice Sample

Create a new machine for this project.

```
$ docker-machine create -d virtualbox --engine-registry-mirror https://docker.m
irrors.ustc.edu.cn springms
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

Run the dependent servers via docker-compose command line.

```
docker-compose up
```

Run the services one by one.

```
mvn spring-boot:run // run in user-service, auth-service, post-service
```

Get authentication.

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

