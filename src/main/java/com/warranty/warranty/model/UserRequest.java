package com.warranty.warranty.model;

import java.sql.Date;

import lombok.Data;

@Data
public class UserRequest {
	
	    private Integer id;

	    private String mobile;

	    private String email;

	    private String name;

	    private String photoUrl;

	    private String city;

	    private Date dob;

	    private String gender;

	    private Double lat;

	    private Double longitude;

	    private String country;
	   
	    private String authToken;


}
