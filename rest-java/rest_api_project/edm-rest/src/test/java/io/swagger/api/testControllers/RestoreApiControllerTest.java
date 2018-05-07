package io.swagger.api.testControllers;


import static org.junit.Assert.assertFalse;
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
import io.swagger.api.RestoreApiController;
import io.swagger.model.FileObject;
import io.swagger.model.MessageResponse;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration (classes = {
		Swagger2SpringBoot.class, 
		RestoreApiController.class})
@WebAppConfiguration
public class RestoreApiControllerTest {
	
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Mock
	private RestoreApiController bac;
	
	@Rule 
	public MockitoRule rule = MockitoJUnit.rule();
	

	@Before
	public void setup(){
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@After
	public void destroy(){
		this.mockMvc = null;
	}
	
	@Value("${scriptfile.restore.path}")
	private String restoreFilePath;
	
	
	//-------------------- Backup Restore --------------------//
	
	@Test
	public void restoreBackup_GET_200_File_Not_Exists_NotDirectory() throws Exception {
		ReflectionTestUtils.setField(bac, "restoreFilePath", "aaa.sh");
	
		FileObject fobjBody = new FileObject();
		fobjBody.setBigFileURL("http://elastest.com/myfile/123");
		fobjBody.setContent("this is mycontent");
		fobjBody.setIsBigFile(true);
		fobjBody.setMemberOfSuT("");
		
		Mockito.doCallRealMethod().when(bac).restoreBackup("123", fobjBody);
		
		File file = new File("../deploymentaaa.sh");
		assertFalse(file.exists());
//		assertTrue(!file.isDirectory());

		ResponseEntity<MessageResponse> r = new ResponseEntity<MessageResponse>(HttpStatus.EXPECTATION_FAILED);
		when(bac.restoreBackup("123", fobjBody)).thenReturn(r);
		bac.restoreBackup("123", fobjBody);
	}
	
	@Test
	public void restoreBackup_GET_200_File_Not_Exists_Directory_Exists() throws Exception {
		ReflectionTestUtils.setField(bac, "restoreFilePath", "../deployment");
	
		FileObject fobjBody = new FileObject();
		fobjBody.setBigFileURL("http://elastest.com/myfile/123");
		fobjBody.setContent("this is mycontent");
		fobjBody.setIsBigFile(true);
		fobjBody.setMemberOfSuT("");
		
		Mockito.doCallRealMethod().when(bac).restoreBackup("123", fobjBody);
		
		File file = new File("../deployment");
		
		assertTrue(file.exists());
		assertTrue(file.isDirectory());

		ResponseEntity<MessageResponse> r = new ResponseEntity<MessageResponse>(HttpStatus.EXPECTATION_FAILED);
		when(bac.restoreBackup("123", fobjBody)).thenReturn(r);
		bac.restoreBackup("123", fobjBody);
	}
	
	
	@Test
	public void restoreBackup_GET_200_FileExists_NotDirectory1() throws Exception {
		ReflectionTestUtils.setField(bac, "restoreFilePath", restoreFilePath);
	
		FileObject fobjBody = new FileObject();
		fobjBody.setBigFileURL("http://elastest.com/myfile/123");
		fobjBody.setContent("this is mycontent");
		fobjBody.setIsBigFile(true);
		fobjBody.setMemberOfSuT("");
		
		Mockito.doCallRealMethod().when(bac).restoreBackup("123", fobjBody);
		
		File file = new File(restoreFilePath);
		assertTrue(file.exists());
		assertTrue(!file.isDirectory());

		ResponseEntity<MessageResponse> r = new ResponseEntity<MessageResponse>(HttpStatus.OK);
		when(bac.restoreBackup("123", fobjBody)).thenReturn(r);
		bac.restoreBackup("123", fobjBody);
	}
	
	
	
	@Test
	public void restoreBackup_POST_415() throws Exception {
		mockMvc
			.perform(post("/restore/123"))
			.andExpect(status().is(415));
	}
	
	@Test
	public void restoreBackup_POST_400() throws Exception {
		mockMvc
			.perform(post("/restore/123")
					.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().is(400));
	}
	
	@Test
	public void restoreBackup_POST_415_NO_CONTENT_TYPE() throws Exception {
		mockMvc
			.perform(post("/restore/123")
					.content( RestoreApiControllerTest.jsonResponse_BAD()))
			.andExpect(status().is(415));
	}
	
	@Test
	public void restoreBackup_POST_405_WRONG_URL_WRONG_JSON() throws Exception {
		mockMvc
			.perform(post("/restore")
					.contentType(MediaType.APPLICATION_JSON_UTF8) 
					.content( RestoreApiControllerTest.jsonResponse_BAD() ))
			.andExpect(status().is(405));
	}
	
	@Test
	public void restoreBackup_POST_405_WRONG_URL_CORRECT_JSON() throws Exception {
		mockMvc
			.perform(post("/restore")
					.contentType(MediaType.APPLICATION_JSON_UTF8) 
					.content( RestoreApiControllerTest.jsonResponse_GOOD() ))
			.andExpect(status().is(405));
	}
	
	@Test
	public void restoreBackup_POST_405_WRONG_URL_NO_CONTENT_TYPE() throws Exception {
		mockMvc
			.perform(post("/restore")
					.content( RestoreApiControllerTest.jsonResponse_BAD() ))
			.andExpect(status().is(405));
	}
	
	@Test
	public void restoreBackup_POST_405_WRONG_URL_NO_JSON() throws Exception {
		mockMvc
			.perform(post("/restore")
					.contentType(MediaType.APPLICATION_JSON_UTF8) )
			.andExpect(status().is(405));
	}
	
	
	@Ignore
	public void restoreBackup_POST_200() throws Exception {
		mockMvc
			.perform(post("/restore/123")
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.content( RestoreApiControllerTest.jsonResponse_GOOD() ))
			.andExpect(status().isOk());
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
