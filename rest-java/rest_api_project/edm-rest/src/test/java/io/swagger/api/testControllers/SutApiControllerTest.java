package io.swagger.api.testControllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import io.swagger.Swagger2SpringBoot;
import io.swagger.api.SutApiController;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration (classes = {
		Swagger2SpringBoot.class, 
		SutApiController.class})
@WebAppConfiguration
public class SutApiControllerTest {

	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Before
	public void setup(){
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@After
	public void destroy(){
		this.mockMvc = null;
	}
	
	
	//-------------------- Sut backupSutWithId --------------------//
	
	@Test
	public void backupSutWithId_GET_415_No_MediaType() throws Exception {
		mockMvc
		.perform(post("/sut/backup/1"))
		.andExpect(status().is(415));
	}

	@Test
	public void backupSutWithId_GET_405_No_MediaType_Wrong_URL() throws Exception {
		mockMvc
		.perform(post("/sut/backup"))
		.andExpect(status().is(405));
	}

	
	@Test
	public void backupSutWithId_GET_405_Wrong_URL() throws Exception {
		mockMvc
			.perform(post("/sut/backup")
					.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().is(405));
	}
	
	@Test
	public void backupSutWithId_GET_200() throws Exception {
		mockMvc
			.perform(post("/sut/backup/1")
					.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk());
	}
	
	
	//-------------------- Sut deleteSutWithId --------------------//
	// 200, 520 DELETE
	
	@Test
	public void deleteSutWithId_GET_415() throws Exception{
		mockMvc
			.perform(delete("/sut/1"))
			.andExpect(status().is(415));
	}
	
	@Test
	public void deleteSutWithId_GET_405_Wrong_URL_No_MediaType() throws Exception{
		mockMvc
			.perform(delete("/sut"))
			.andExpect(status().is(405));
	}
	
	@Test
	public void deleteSutWithId_GET_405_Wrong_URL() throws Exception{
		mockMvc
			.perform(delete("/sut/")
					.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().is(405));
	}
	
	@Test
	public void deleteSutWithId_GET_200() throws Exception{
		mockMvc
			.perform(delete("/sut/1")
					.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk());
	}
	
	//-------------------- Sut restoreSutWithId --------------------//
	// 200, 405, 520
	
	@Test
	public void restoreSutWithId_POST_415_No_MediaType_No_JSON() throws Exception{
		mockMvc
			.perform(post("/sut/restore/1"))
			.andExpect(status().is(415));
	}
	
	@Test
	public void restoreSutWithId_POST_400_No_JSON() throws Exception{
		mockMvc
			.perform(post("/sut/restore/1")
					.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().is(400));
	}
	
	@Test
	public void restoreSutWithId_POST_405_No_MediaType_WrongUrl_No_JSON() throws Exception{
		mockMvc
			.perform(post("/sut/restore"))
			.andExpect(status().is(405));
	}
	
	@Test
	public void restoreSutWithId_POST_400_JSON_BAD() throws Exception{
		mockMvc
			.perform(post("/sut/restore/1")
					.contentType(MediaType.APPLICATION_JSON_UTF8) 
					.content( SutApiControllerTest.jsonResponse_BAD() ))
			.andExpect(status().is(400));
	}
	
	@Test
	public void restoreSutWithId_POST_200_JSON_GOOD() throws Exception{
		mockMvc
			.perform(post("/sut/restore/1")
					.contentType(MediaType.APPLICATION_JSON_UTF8) 
					.content( SutApiControllerTest.jsonResponse_GOOD() ))
			.andExpect(status().is(200));
	}
	
	
	//-------------------- Sut retrieveAllSut --------------------//
	// 200, 520
	
	@Test
	public void retrieveAllSut_GET_415() throws Exception{
		mockMvc
			.perform(get("/sut"))
			.andExpect(status().is(415));
	}
	
	@Test
	public void retrieveAllSut_GET_200() throws Exception{
		mockMvc
			.perform(get("/sut")
					.contentType(MediaType.APPLICATION_JSON_UTF8))
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
