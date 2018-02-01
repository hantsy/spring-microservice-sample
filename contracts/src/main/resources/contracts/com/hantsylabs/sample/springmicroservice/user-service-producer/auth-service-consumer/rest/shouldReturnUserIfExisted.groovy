org.springframework.cloud.contract.spec.Contract.make {
    description("""
        return user by username, if it is exited
    """)

    request{
        method GET()
        url "/users/{username}"
        headers {
            accept(applicationJson())
        }
    }

    response{
        status 200
        body(
//            file("find-user-by-username.json")
            username: "user",
            password:"password",
            email:"user@example.com"
        )
        headers {
            contentType(applicationJson())
        }
    }
}

