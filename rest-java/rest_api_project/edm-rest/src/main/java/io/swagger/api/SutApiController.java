package io.swagger.api;

import io.swagger.model.MessageResponse;
import io.swagger.model.SutsResponse;
import io.swagger.annotations.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-07-12T12:44:56.423Z")

@Controller
public class SutApiController implements SutApi {



    public ResponseEntity<MessageResponse> backupSutWithId(@ApiParam(value = "The id code of the SuT", required = true ) @PathVariable("sut") String sut) {
    	System.out.println("Inside: SUT -> backupSutWithId");
        return new ResponseEntity<MessageResponse>(HttpStatus.OK);
    }

    public ResponseEntity<Void> deleteSutWithId(@ApiParam(value = "The id code of the SuT", required = true ) @PathVariable("sut") String sut) {
    	System.out.println("Inside: SUT -> deleteSutWithId");
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

	public ResponseEntity<Void> restoreSutWithId(@ApiParam(value = "Information about compressed tarball, containing the sut data", required = true ) @RequestBody MessageResponse body) {
    	System.out.println("Inside: SUT -> restoreSutWithId");
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public ResponseEntity<SutsResponse> retrieveAllSut() {
        System.out.println("Inside: SUT -> retrieveAllSut");
        return new ResponseEntity<SutsResponse>(HttpStatus.OK);
    }

}
