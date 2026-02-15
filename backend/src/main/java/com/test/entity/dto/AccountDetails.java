package com.test.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("db_account_details")
public class AccountDetails implements com.example.entity.BaseData {
    @TableId
    Integer id;
    int gender;
    String phone;
    String qq;
    String wx;
    @TableField("`desc`")
    String desc;
}
