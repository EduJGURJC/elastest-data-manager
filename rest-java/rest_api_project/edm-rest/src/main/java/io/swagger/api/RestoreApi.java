package io.swagger.api;

import io.swagger.model.FileObject;
import io.swagger.model.MessageResponse;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

import javax.validation.Valid;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-07-12T12:44:56.423Z")

@Api(value = "restore", description = "the restore API")
public interface RestoreApi {


	@ApiOperation(value = "Restore all data for the platform based on backup id", notes = "Download and restore the gzipped tarball with all data for the platform. All existing will be overrided.", 
			response = MessageResponse.class, 
			tags = {"DataIO", })
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Restore Successful", response = MessageResponse.class),
			@ApiResponse(code = 404, message = "Invalid backupId", response = MessageResponse.class),
			@ApiResponse(code = 405, message = "Invalid input data", response = MessageResponse.class),
			@ApiResponse(code = 417, message = "Unexpected error", response = MessageResponse.class) })

	@RequestMapping(value = "/restore/{backupId}", 
			produces = { "application/json" }, 
			consumes = {"application/json" }, 
			method = RequestMethod.POST)
	ResponseEntity<MessageResponse> restoreBackup (
			@ApiParam(value = "Define the backup id to be restored", required = true) @PathVariable("backupId") String backupId,
			@ApiParam(value = "Information about compressed tarball, containing backup data", required = true) @Valid @RequestBody FileObject body)
					throws IOException, InterruptedException;
}
