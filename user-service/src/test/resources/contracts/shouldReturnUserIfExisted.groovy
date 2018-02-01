import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

Contract.make {
    description "should return user if username is existed"
    request {
        url "/users/user"
        method GET()
    }
    response {
        status 200
        headers {
            header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
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