package com.warranty.warranty.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateResponse {

		private String status;
		private String message;
		private String statuscode;
	    private Integer id;
	    private String type;
	    private String template;
	    private String templateDesc;
	    private String modifiedOn;
	    
}
