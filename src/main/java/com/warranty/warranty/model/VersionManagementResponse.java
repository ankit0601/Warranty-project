package com.warranty.warranty.model;

//import com.meditation.my_sequence.dto.VersionManagementDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VersionManagementResponse {
	private String status;
	private String message;
	//private VersionManagementDTO version;

}
