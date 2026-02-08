package com.retro.global;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.http.HttpHeaders;

@Parameter(
    name = HttpHeaders.USER_AGENT,
    in = ParameterIn.HEADER,
    description = "Swagger UI에서는 브라우저 기본 User-Agent(App)로 덮어써집니다.",
    schema =
    @Schema(
        type = "string",
        allowableValues = {"Web", "App"},
        defaultValue = "Web"))
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UserAgentHeader {

}
