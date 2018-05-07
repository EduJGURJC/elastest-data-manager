package io.swagger.api;

import io.swagger.annotations.*;
import io.swagger.model.MessageResponse;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-07-12T12:44:56.423Z")

@Api(value = "backup", description = "the backup API")
public interface BackupApi {


	@ApiOperation(value = "Backup all data from EDM module", notes = "Backup and create a gzipped tarball with all data for EDM module. Please, notice that EDM module contains all platforms data.", 
			response = MessageResponse.class, 
			tags = {"DataIO", })
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Backup Successful", response = MessageResponse.class),
			@ApiResponse(code = 405, message = "Invalid input", response = MessageResponse.class),
			@ApiResponse(code = 417, message = "Unexpected error", response = MessageResponse.class) })

	@RequestMapping(value = "/backup", 
		produces = { "application/json" }, 
		consumes = { "application/json" }, 
		method = RequestMethod.POST)
	ResponseEntity<MessageResponse> createBackup() throws IOException, InterruptedException ;
}
