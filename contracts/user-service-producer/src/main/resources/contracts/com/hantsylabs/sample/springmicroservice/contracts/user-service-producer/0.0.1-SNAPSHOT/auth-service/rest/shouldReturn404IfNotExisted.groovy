import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

Contract.make {
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
            entity: "USER",
            id: "noneExisting",
            code:"not_found",
            message:"User:noneExisting was not found"
        )
        headers {
            contentType(applicationJson())
        }
    }
}