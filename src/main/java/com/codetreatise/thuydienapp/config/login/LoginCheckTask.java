package com.codetreatise.thuydienapp.config.login;

import com.codetreatise.thuydienapp.config.DataConfig;
import com.codetreatise.thuydienapp.config.StageManager;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.config.request.LoginRequest;
import com.codetreatise.thuydienapp.view.FxmlView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

public class LoginCheckTask extends TimerTask {

    private static LoginCheckTask instance;

    public static LoginCheckTask getInstance() {
        if(instance == null) {
            instance = new LoginCheckTask();
        }
        return instance;
    }

    private final StageManager stageManager;
    private final String validTokenUrl;
    private final String loginUrl;
    public LoginCheckTask() {
        this.stageManager = StageManager.getInstance();
        this.validTokenUrl = "http://quantri.i-lovecandy.com:9999/token/valid";
        loginUrl = "http://quantri.i-lovecandy.com:9999/token";
    }

    @Override
    public void run() {
        try {
            loginCheck();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loginCheck() throws IOException {
        if(SystemArg.LOGIN) {
            AtomicReference<String> jsonBody = new AtomicReference<>("");
            AtomicReference<ResponseEntity<String>> response = new AtomicReference<>(ResponseEntity.status(HttpStatus.OK).body(""));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            RestTemplate restTemplate = new RestTemplate();
            if(SystemArg.TOKEN.equals("")) {
                try {
                    DataConfig.getHostsList();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                }
            }
            jsonBody.set(SystemArg.TOKEN);
            HttpEntity<String> request = new HttpEntity<>(jsonBody.get(), headers);
            try {
                response.set(restTemplate.postForEntity(validTokenUrl, request, String.class));
                SystemArg.LOGIN = response.get().getBody().equals(Boolean.TRUE.toString());
                if(!SystemArg.LOGIN) {
                    stageManager.showWhenHidden(FxmlView.LOGIN);
                }
                DataConfig.saveFavorites(null);
            } catch (HttpClientErrorException ex) {
                if(ex.getStatusCode().compareTo(HttpStatus.EXPECTATION_FAILED) == 0) {
                    SystemArg.LOGIN = false;
                    DataConfig.saveFavorites(null);
                    jsonBody = new AtomicReference<>("");
                    response = new AtomicReference<>(ResponseEntity.status(HttpStatus.OK).body(""));
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                    jsonBody.set(ow.writeValueAsString(LoginRequest.builder()
                            .username(SystemArg.API_USERNAME)
                            .password(SystemArg.API_PASSWORD)
                            .build()));
                    request = new HttpEntity<>(jsonBody.get(), headers);
                    try {
                        response.set(restTemplate.postForEntity(loginUrl, request, String.class));
                        SystemArg.LOGIN = Boolean.TRUE;
                        SystemArg.TOKEN = response.get().getBody();
                        DataConfig.saveFavorites(null);
                    } catch (Exception e) {
                        SystemArg.LOGIN = false;
                        DataConfig.saveFavorites(null);
                        stageManager.showWhenHidden(FxmlView.LOGIN);
                    }
                }

                ex.printStackTrace();
            } catch (Exception e) {
                SystemArg.LOGIN = false;
                DataConfig.saveFavorites(null);
                stageManager.showWhenHidden(FxmlView.LOGIN);
            }
        } else {
            stageManager.showWhenHidden(FxmlView.LOGIN);
        }

    }
}
