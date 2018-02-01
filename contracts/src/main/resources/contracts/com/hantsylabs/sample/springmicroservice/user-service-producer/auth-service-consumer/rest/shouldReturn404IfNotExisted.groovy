org.springframework.cloud.contract.spec.Contract.make {
    description("""
        return error 404 if user is not existed
    """)

    request{
        method GET()
        url "/users/noneExisting"
        headers {
            accept(applicationJson())
        }
    }

    response{
        status 404
        body(
            entity: "User",
            id: "noneExisting",
            code:"not found",
            message:"User:noneExisting is not found"
        )
        headers {
            contentType(applicationJson())
        }
    }
}

