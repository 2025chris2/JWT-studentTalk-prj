package com.test.controller;

import com.test.Service.AccountService;
import com.test.entity.RestBean;
import com.test.entity.dto.Account;
import com.test.entity.vo.response.AccountVO;
import com.test.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/user")
public class AccountController {

    @Autowired
    AccountService service;

    @GetMapping("/info")
    public RestBean<AccountVO> info(@RequestAttribute(Const.ATTR_USER_ID) int id){
        Account account = service.findAccountById(id);
        return RestBean.success(account.asViewObject(AccountVO.class));
    }
}
