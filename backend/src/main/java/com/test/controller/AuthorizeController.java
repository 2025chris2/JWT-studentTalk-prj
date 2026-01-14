package com.test.controller;

import com.test.Service.AccountService;
import com.test.entity.RestBean;
import com.test.entity.vo.request.ConfirmResetVO;
import com.test.entity.vo.request.EmailRegisterVO;
import com.test.entity.vo.request.EmailResetVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;
import java.util.function.Supplier;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

    @Autowired
    AccountService service;

    @GetMapping("/ask-code")
    public RestBean<Void> askVerifyCode(@RequestParam("email") @Email String email,
                                        @RequestParam("type") @Pattern(regexp = ("register|reset")) String type,
                                        HttpServletRequest request) {
        return this.messageHandle(() ->
                service.registerEmailVerifyCode(type, email, request.getRemoteAddr()));
    }

    @PostMapping("/register")  //@RequestBody注解会将传过来的数据，转化为对应的实体类
    public RestBean<Void> register(@RequestBody @Valid EmailRegisterVO vo) {
        return this.messageHandle(vo,service::registerEmailAccount);
    }

    @PostMapping("/reset-confirm")
    public RestBean<Void> resetConfirm(@RequestBody @Valid ConfirmResetVO vo){
        return this.messageHandle(vo,service::resetConfirm);
    }

    @PostMapping("/reset-password")
    public RestBean<Void> resetConfirm(@RequestBody @Valid EmailResetVO vo){
        return this.messageHandle(vo,service::resetEmailAccountPassword);
    }

    private <T> RestBean<Void> messageHandle(T vo, Function<T,String> function){
        return messageHandle(() -> function.apply(vo));
    }

    private RestBean<Void> messageHandle(Supplier<String> action) {
        String message = action.get();
        return message == null ? RestBean.success() : RestBean.failure(400, message);

    }
}
