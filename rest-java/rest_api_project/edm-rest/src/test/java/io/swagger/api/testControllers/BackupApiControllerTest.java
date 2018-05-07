package io.swagger.api.testControllers;


import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import io.swagger.Swagger2SpringBoot;
import io.swagger.api.BackupApiController;
import io.swagger.model.MessageResponse;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration (classes = {
		Swagger2SpringBoot.class, 
		BackupApiController.class})
@WebAppConfiguration
public class BackupApiControllerTest {
	
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Mock
	private BackupApiController bac;
	
	@Rule 
	public MockitoRule rule = MockitoJUnit.rule();
	

	@Before
	public void setup(){
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@After
	public void destroy() {
		this.mockMvc = null;
	}
	
	@Value("${scriptfile.backup.path}")
	private String backupFilePath;
	
	
	//-------------------- Backup Create --------------------//
	
	@Test
	public void createBackup_GET_415() throws Exception { 
		mockMvc
			.perform(post("/backup"))
//			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is(415));
	}

	@Test
	public void createBackup_GET_405() throws Exception {
		mockMvc
			.perform(post("/backup/wrongURL"))
			.andExpect(status().is(405));
	}
	
	@Ignore
	public void createBackup_GET_200() throws Exception {
		mockMvc
			.perform(post("/backup")
					.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk());
	}
	
	@Test
	public void createBackup_GET_417_FileNotExists() throws Exception {
		ReflectionTestUtils.setField(bac, "backupFilePath", "../deployment/wrongFileName.sh");

		Mockito.doCallRealMethod().when(bac).createBackup();

		ResponseEntity<MessageResponse> r = new ResponseEntity<MessageResponse>(HttpStatus.EXPECTATION_FAILED);
		when(bac.createBackup()).thenReturn(r);
		bac.createBackup();
		
	}
	
	@Test
	public void createBackup_GET_200_FileExists_NotDirectory() throws Exception {
		ReflectionTestUtils.setField(bac, "backupFilePath", backupFilePath);
	
		Mockito.doCallRealMethod().when(bac).createBackup();
		
		File file = new File(backupFilePath);
		assertTrue(file.exists());
		assertTrue(!file.isDirectory());

		ResponseEntity<MessageResponse> r = new ResponseEntity<MessageResponse>(HttpStatus.OK);
		when(bac.createBackup()).thenReturn(r);
		bac.createBackup();
	}

	@Test
	public void createBackup_GET_417_FileExists_IsDirectory() throws Exception {
		ReflectionTestUtils.setField(bac, "backupFilePath", "../deployment");

		Mockito.doCallRealMethod().when(bac).createBackup();
		
		File file = new File("../deployment");
		assertTrue(file.exists());
		assertTrue(file.isDirectory());

		ResponseEntity<MessageResponse> r = new ResponseEntity<MessageResponse>(HttpStatus.EXPECTATION_FAILED);
		when(bac.createBackup()).thenReturn(r);
		bac.createBackup();
		
	}
	
	

	//-------------------- JSON ObjectAsString --------------------//
	
	// JSON Request Body - GOOD
	private static String jsonResponse_GOOD() {
		String requestBody = "{\"bigFileURL\": \"http://example.com/files/file001.dmp\","
				+ "\"content\":\"MyContent\","
				+ "\"filesystem\": \"s3\","
				+ "\"isBigFile\": true,"
				+ "\"location\": \"/MyModule/MyLocation\","
				+ "\"memberOfSuT\": \"string\"}";
		return requestBody;
	}
	
	// JSON Request Body - BAD
	private static String jsonResponse_BAD() {
		String requestBody = "{\"bigFileURL\": \"http://example.com/files/file001.dmp\","
				+ "\"content\":\"MyContent\","
				+ "\"isBigFile\": ERROR_HERE,"
				+ "\"location\": \"/MyModule/MyLocation\","
				+ "\"memberOfSuT\": \"string\"}";
		return requestBody;
	}
	
}
