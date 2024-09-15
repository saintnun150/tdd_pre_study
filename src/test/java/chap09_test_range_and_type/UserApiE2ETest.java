package chap09_test_range_and_type;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserApiE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    void weakPwResponse() {
        String resBody = "{\"id\":\"id\", \"pw\":\"123\", \"email\":\" lowell@lowell.com\"}";

        RequestEntity<String> req = RequestEntity.post(URI.create("/users"))
                                                 .contentType(MediaType.APPLICATION_JSON)
                                                 .body(resBody);

        ResponseEntity<String> res = restTemplate.exchange(req, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertTrue(Objects.requireNonNull(res.getBody()).contains("WeakPasswordException"));
    }
}
