package com.test.filter;

import com.test.utils.Const;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Const.ORDER_CORS)
public class CorsFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.addCorsHeader(request,response);
        chain.doFilter(request,response);
    }

    private void addCorsHeader(HttpServletRequest request,
                               HttpServletResponse response){
        //下面这行代码是让所有的访问都可以进行！
        response.addHeader("Access-Control-Allow-Origin",request.getHeader("Origin"));
        //下面这行代码是让5173接口访问可以进行！
//        response.addHeader("Access-Control-Allow-Origin","http://localhost:5173");
        response.addHeader("Access-Control-Allow-Methods","GET,POST,OPTIONS,PUT,DELETE");
        response.addHeader("Access-Control-Allow-Headers","Authorization,Content-Type");


    }
}
