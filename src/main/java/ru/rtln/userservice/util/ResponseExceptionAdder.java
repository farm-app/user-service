package ru.rtln.userservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.rtln.common.model.ErrorModel;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@UtilityClass
public class ResponseExceptionAdder {

    public static void addExceptionToResponse(HttpServletResponse response, Exception e) throws IOException {
        log.error("Exception occurred", e);
        response.setStatus(SC_UNAUTHORIZED);
        response.setContentType(APPLICATION_JSON_VALUE);
        ErrorModel errorModel = new ErrorModel(SC_UNAUTHORIZED, e.getClass().getSimpleName(), e.getMessage());
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorModel));
    }
}
