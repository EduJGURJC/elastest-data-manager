package io.swagger.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import io.swagger.model.MessageResponse;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-07-12T12:44:56.423Z")

@Controller
public class BackupApiController implements BackupApi {

	private static final Logger LOGGER = Logger.getLogger(BackupApiController.class.getName());

	@Value("${scriptfile.backup.file}")
	private String backupFilePath;

	public ResponseEntity<MessageResponse> createBackup() throws IOException, InterruptedException {

		MessageResponse msg = new MessageResponse();

		File file = new File(backupFilePath);

		if (file.exists() && !file.isDirectory()) {
			runScript(backupFilePath);
			msg.setMsg("Backup Completed");
			return new ResponseEntity<MessageResponse>(msg, HttpStatus.OK);
		}

		msg.setMsg("Cannot create backup");
		return new ResponseEntity<MessageResponse>(msg, HttpStatus.EXPECTATION_FAILED);

	}

	private void runScript(String filepath) throws IOException, InterruptedException {
		
		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec("python " + filepath);
		proc.waitFor();
	
		StringBuffer output = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		String line = "";
		while ((line = reader.readLine()) != null) {
			output.append(line + "\n");
		}

		LOGGER.info("### " + output);
	}

}
