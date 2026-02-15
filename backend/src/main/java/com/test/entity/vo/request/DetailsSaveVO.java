package com.test.entity.vo.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class DetailsSaveVO {
    @Pattern(regexp = "^[a-zA-z0-9\\u4e00-\\u9fa5]+$",message="用户只能包含数字，字母和中文")
            @Length(min = 1, max = 10,message = "用户名长度为1~10")
    String username;
    @Max(value = 1,message = "性别只能是0或1")
            @Min(value = 0,message = "性别只能是0或1")
     int gender;
    @Pattern(regexp = "^1[3-9]\\d{9}$",message = "手机号格式不正确!")
            @Length(max= 11)
    String phone;
    @Length(max=13)
    String qq;
    @Length(max=20)
    String wx;
    @Length(max=200)
    String desc;
}
