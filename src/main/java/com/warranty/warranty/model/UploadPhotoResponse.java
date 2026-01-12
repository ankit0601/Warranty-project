package com.warranty.warranty.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadPhotoResponse {

	private String status;
	private String message;
	private String photo_url;

}
