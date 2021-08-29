import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return user if username is existed"
    request {
        url "/users/user"
        method GET()
        headers {
            accept(applicationJson())
        }
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
                [
                        username: "user",
                        password: "password",
                        email   : "user@example.com"
                ]
        )
    }
}