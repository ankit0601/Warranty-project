package com.warranty.warranty.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDeviceRequest {

    private String deviceId;
    private String deviceType;
    private String deviceModel;
    private String os;
    private String osVersion;
    private String appVersion;
    private String manufacturer;
    private String userId;
    private String pushToken;
    private double latitude;
    private double longitude;

    
    
}
