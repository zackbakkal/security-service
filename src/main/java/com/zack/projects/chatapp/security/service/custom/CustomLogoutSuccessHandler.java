package com.zack.projects.chatapp.security.service.custom;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private RestTemplate restTemplate;

    @SneakyThrows
    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {

        log.info("Create path variable to send with the put notification methods");
        Map< String, String > params = new HashMap< String, String >();
        params.put("username", authentication.getName());

        log.info(String.format("Calling user service to set user [%s] offline", authentication.getName()));
        restTemplate.put(
                "http://USER-SERVICE/users/status/{username}/offline",
                null,
                params);

        log.info(String.format("Logout successful with user [%s]", authentication.getName()));
        response.setStatus(HttpServletResponse.SC_OK);

        log.info("Calling the notification service to unsubscribe user [%s] from the emitter's list");
        restTemplate
                .put(
                        "http://NOTIFICATION-SERVICE/notifications/unsubscribe/" + authentication.getName(),
                        null,
                        params);


        log.info("Calling the notification service to notify all users");
        restTemplate
                .put(
                        "http://NOTIFICATION-SERVICE/notifications/notifyUsers/" + authentication.getName(),
                        null,
                        params);

        log.info(String.format("Redirecting to login page"));
        response.sendRedirect("/login");

    }
}
