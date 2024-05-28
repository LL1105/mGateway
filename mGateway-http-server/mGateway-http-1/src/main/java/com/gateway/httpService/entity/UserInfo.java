package com.gateway.httpService.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {
    int id;
    String phoneNumber;
    String name;
    String passwd;
}
