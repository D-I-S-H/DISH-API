package com.dish.dish;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class MainController {

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    /**
     * @type GET
     * @return base URL
     */
    @GetMapping
    public String index() {
        return "All your base are belong to us";
        // TODO - remove entirely or add a more meaningful message
    }

    /**
     * @type GET
     * @return List of all endpoints
     */
    @GetMapping("/endpoints")
    public List<String> listAllEndpoints() {
        return handlerMapping.getHandlerMethods()
                .keySet()
                .stream()
                .map(requestMappingInfo -> requestMappingInfo.toString())
                .filter(endpoint -> !endpoint.contains("/error")) // Exclude /error endpoint
                .collect(Collectors.toList());
    }
}
