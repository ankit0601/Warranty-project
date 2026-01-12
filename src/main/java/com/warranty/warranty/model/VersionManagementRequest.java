package com.warranty.warranty.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class VersionManagementRequest {
	
	private int versionNo;
	private String operatingSystem;

	
}
