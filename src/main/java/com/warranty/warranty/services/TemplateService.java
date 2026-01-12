package com.warranty.warranty.services;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
//import com.warranty.warranty.MySequenceApplication;
import com.warranty.warranty.entities.TemplateEntity;
import com.warranty.warranty.model.TemplateResponse;
import com.warranty.warranty.repositories.TemplateRepository;

@Service
public class TemplateService {

	private static final Logger logger = LogManager.getLogger(VerifyOTPService.class);
	@Autowired
	TemplateRepository templateRepository;

	public TemplateResponse getTemplate(int id) {
		TemplateResponse templateResponse = new TemplateResponse();

		try {
			logger.info("inside TemplateService... finding by template id................");
			Optional<TemplateEntity> templateOptional = templateRepository.findByTemplateId(id);
			logger.info("inside TemplateService... find by template id");
			if (id == 1002 || id == 1003) {
				if (templateOptional.isPresent()) {
					TemplateEntity templateEntity = templateOptional.get();

					logger.info("inside TemplateService... find by template id present");
					// TemplateResponse templateResponse=new TemplateResponse();
					templateResponse.setStatus("success");
					templateResponse.setMessage("Updated details");
					templateResponse.setStatuscode(HttpStatus.OK + "");
					templateResponse.setId(id);
					templateResponse.setTemplate(templateEntity.getTemplate());
					return templateResponse;
				}
				logger.info("inside TemplateService... find by template id not present");
				templateResponse.setStatus("success");
				templateResponse.setMessage("Record Not Found");
				templateResponse.setStatuscode(HttpStatus.NO_CONTENT + "");
				// return templateResponse;

			}

		} catch (Exception e) {
			logger.error("Exception   " + e.getMessage() + "... occurred....in " + getClass().getName());
			// throw new Exception();
			templateResponse.setStatus("fialed");
			templateResponse.setMessage("Exception occurred...." + e.getMessage());
			templateResponse.setStatuscode(HttpStatus.NO_CONTENT + "");
			// return templateResponse;
		}
		return templateResponse;
	}

}
