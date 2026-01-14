package com.test.config;

import com.test.Service.AccountService;
import com.test.entity.dto.Account;
import com.test.entity.RestBean;
import com.test.filter.JwtAuthorizeFilter;
import com.test.utils.JwtUtils;
import com.test.entity.vo.response.AuthorizeVO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfiguration {

    @Autowired
    JwtUtils utils;

    @Autowired
    JwtAuthorizeFilter jwtAuthorizeFilter;

    @Autowired
    AccountService service;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf ->conf
                        .requestMatchers("/api/auth/**","/error").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(conf ->conf
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(this::onAuthenticationSuccess)
                        .failureHandler(this::onAuthenticationFailure)

                )
                .logout(conf -> conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess)
                )
                //下面是处理失败的代码 和 没有权限访问的代码
                .exceptionHandling(conf -> conf
                        .authenticationEntryPoint(this::onUnauthorized)
                        .accessDeniedHandler(this::onAccessDeny)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(conf ->conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ) //使security不处理session，状态是：无状态
                .addFilterBefore(jwtAuthorizeFilter,UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    public void onAccessDeny(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(RestBean.forbidden(accessDeniedException.getMessage()).asJsonString());
    }

    public void onUnauthorized(HttpServletRequest request,
                               HttpServletResponse response,
                               AuthenticationException e) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(RestBean.unauthorized(e.getMessage()).asJsonString());
    }

    private void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(RestBean.unauthorized(e.getMessage()).asJsonString());
    }

    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        User user =(User) authentication.getPrincipal();
        Account account = service.findAccountByNameOrEmail(user.getUsername());
        String token = utils.createJwt(user,account.getId(),account.getUsername());
        AuthorizeVO vo = new AuthorizeVO();
        vo.setExpire(utils.expireTime());
        vo.setRole(account.getRole());
        vo.setToken(token);
        vo.setUsername(account.getUsername());
        response.getWriter().write(RestBean.success(vo).asJsonString());
    }

    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException,ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        String authorization = request.getHeader("Authorization");
        if(utils.invalidateJwt(authorization)){
            writer.write(RestBean.success().asJsonString());
        }else{
            writer.write(RestBean.failure(400,"退出登录失败").asJsonString());
        }
    }


}
