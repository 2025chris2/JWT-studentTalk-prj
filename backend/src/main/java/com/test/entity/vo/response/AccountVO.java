package com.test.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class AccountVO {
    public String username;
    public String email;
    public String role;
    Date registerTime;
}
