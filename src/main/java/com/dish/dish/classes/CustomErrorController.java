package com.dish.dish.classes;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@RestController
@RequestMapping("/error")
public class CustomErrorController implements ErrorController {

    @Autowired
    private ErrorAttributes errorAttributes;

    @GetMapping
    public ResponseEntity<Map<String, Object>> handleError(WebRequest webRequest) {
        // Get error attributes (details of the error)
        Map<String, Object> errorDetails = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE));

        // Set the HTTP status code to 404 for "Not Found"
        HttpStatus status = HttpStatus.NOT_FOUND;

        // Customize the response
        errorDetails.put("message", "Resource not found");
        errorDetails.put("status", status.value());

        return new ResponseEntity<>(errorDetails, status);
    }
}
