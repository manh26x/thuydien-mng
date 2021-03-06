package com.codetreatise.thuydienapp.controller;


import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

import com.codetreatise.thuydienapp.config.DataConfig;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.config.request.LoginRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.http.*;

import com.codetreatise.thuydienapp.config.StageManager;
import com.codetreatise.thuydienapp.view.FxmlView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * @author Ram Alapure
 * @since 05-04-2017
 */

public class LoginController implements Initializable{

	private final String loginUrl;

    @FXML
    private PasswordField password;

    @FXML
    private TextField username;

    @FXML
    private Label lblLogin;

    private StageManager stageManager;

	public LoginController() {
		this.loginUrl = "http://quantri.i-lovecandy.com:9999/token";
		stageManager = StageManager.getInstance();
	}

	@FXML
    private void login(ActionEvent event) throws JsonProcessingException {
		AtomicReference<String> jsonBody = new AtomicReference<>("");
		AtomicReference<ResponseEntity<String>> response = new AtomicReference<>(ResponseEntity.status(HttpStatus.OK).body(""));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		RestTemplate restTemplate = new RestTemplate();
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		jsonBody.set(ow.writeValueAsString(LoginRequest.builder()
				.username(username.getText())
				.password(password.getText())
				.build()));
		HttpEntity<String> request = new HttpEntity<>(jsonBody.get(), headers);
		try {
			response.set(restTemplate.postForEntity(loginUrl, request, String.class));
			SystemArg.LOGIN = Boolean.TRUE;
			SystemArg.API_USERNAME = username.getText();
			SystemArg.API_PASSWORD = password.getText();
			SystemArg.TOKEN = response.get().getBody();
			DataConfig.saveFavorites(null);
			stageManager.switchScene(FxmlView.TIMING_MODBUS);
		} catch (HttpClientErrorException ex) {
			if(ex.getStatusCode().value() == HttpStatus.EXPECTATION_FAILED.value()) {
				lblLogin.setText("T??i kho???n h???t h???n");
			} else if(ex.getStatusCode().is4xxClientError()) {
				lblLogin.setText("Sai t??i kho???n ho???c m???t kh???u");
			}
			SystemArg.LOGIN = false;

			ex.printStackTrace();
		} catch ( Exception e) {
			lblLogin.setText("????ng nh???p th???t b???i");
			e.printStackTrace();
		}
	}
	
	public String getPassword() {
		return password.getText();
	}

	public String getUsername() {
		return username.getText();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

}
