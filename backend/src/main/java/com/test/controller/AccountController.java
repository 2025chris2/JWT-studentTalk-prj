package com.test.controller;

import com.test.Service.AccountDetailsService;
import com.test.Service.AccountService;
import com.test.entity.RestBean;
import com.test.entity.dto.Account;
import com.test.entity.dto.AccountDetails;
import com.test.entity.vo.request.ChangePasswordVO;
import com.test.entity.vo.request.DetailsSaveVO;
import com.test.entity.vo.request.ModifyEmailVO;
import com.test.entity.vo.response.AccountDetailsVO;
import com.test.entity.vo.response.AccountVO;
import com.test.utils.Const;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.function.Supplier;


@RestController
@RequestMapping("/api/user")
public class AccountController {

    @Autowired
    AccountService service;

    @Autowired
    AccountDetailsService detailsService;

    @GetMapping("/info")
    public RestBean<AccountVO> info(@RequestAttribute(Const.ATTR_USER_ID) int id){
        Account account = service.findAccountById(id);
        return RestBean.success(account.asViewObject(AccountVO.class));
    }

    @GetMapping("/details")
    public RestBean<AccountDetailsVO> details(@RequestAttribute(Const.ATTR_USER_ID) int id){
        AccountDetails details = Optional
                .ofNullable(detailsService.findAccountDetailsById(id))
                .orElseGet(AccountDetails::new);
        return RestBean.success(details.asViewObject(AccountDetailsVO.class));
    }

    @PostMapping("/save-details")
    public RestBean<Void> saveDetails(@RequestAttribute (Const.ATTR_USER_ID) int id,
                                      @RequestBody @Valid DetailsSaveVO vo){
        boolean success = detailsService.saveAccountDetails(id ,vo);
        return success ? RestBean.success() : RestBean.failure(400, "此用户名已被使用，请更换！");
    }

    @PostMapping("/modify-email")
    public RestBean<Void> modifyEmail(@RequestAttribute (Const.ATTR_USER_ID) int id,
                                      @RequestBody @Valid ModifyEmailVO vo){
        return this.messageHandle(()->service.modifyEmail(id,vo));
    }

    @PostMapping("/change-password")
    public RestBean<Void> changePassword(@RequestAttribute (Const.ATTR_USER_ID) int id,
                                         @RequestBody @Valid ChangePasswordVO vo){
        return this.messageHandle(()->service.changePassword(id,vo));
    }

    public  <T> RestBean<T> messageHandle(Supplier<String> action){
        String message = action.get();
        if(message == null)
            return RestBean.success();
        else
            return RestBean.failure(400, message);
    }

}
