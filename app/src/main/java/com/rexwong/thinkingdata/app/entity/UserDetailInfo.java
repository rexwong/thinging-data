package com.rexwong.thinkingdata.app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailInfo {
    String countryCode;
    int gender;
    int qualityAuth;
}
