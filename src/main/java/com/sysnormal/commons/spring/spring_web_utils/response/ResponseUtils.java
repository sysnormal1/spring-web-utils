package com.sysnormal.commons.spring.spring_web_utils.response;

import com.sysnormal.commons.core.DefaultDataSwap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

/**
 * Response utils
 *
 * @author Alencar
 * @version 1.0.0
 */
public class ResponseUtils {

    private static final Logger logger = LoggerFactory.getLogger(ResponseUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /*@Autowired
    ObjectMapper objectMapper;*/

    public static DefaultDataSwap handleResponse(ClientRawResponseWrapper response){
        logger.debug("INIT {}.{}",ResponseUtils.class.getSimpleName(), "handleResponse");
        DefaultDataSwap result = new DefaultDataSwap();
        try {
            HttpStatusCode statusCode = response.clientResponse.statusCode();
            result.httpStatusCode = statusCode.value();
            logger.debug("response status {}",result.httpStatusCode);
            result.success = statusCode.is2xxSuccessful();
            try {
                JsonNode jsonResponse = objectMapper.readTree(response.rawResponse);
                result.success = jsonResponse.get("success").asBoolean();
                result.data = jsonResponse.get("data");
                result.message = jsonResponse.get("message").asText();
            } catch (Exception e) {
                result.data = response.rawResponse;
            }
        } catch (Exception e) {
            result.setException(e);
        }
        logger.debug("END {}.{}",ResponseUtils.class.getSimpleName(), "handleResponse");
        return result;
    }

    public static ResponseEntity<DefaultDataSwap> sendDefaultDataSwapResponse(DefaultDataSwap defaultDataSwap) {
        try {
            return defaultDataSwap.success ? ResponseEntity.status(HttpStatus.OK).body(defaultDataSwap) : ResponseEntity.status(Objects.requireNonNullElse(defaultDataSwap.httpStatusCode, HttpStatus.INTERNAL_SERVER_ERROR.value())).body(defaultDataSwap);
        } catch (Throwable e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DefaultDataSwap(false, e.getMessage()));
        }
    }
}
