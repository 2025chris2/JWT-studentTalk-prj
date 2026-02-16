package com.test.Service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.Service.AccountService;
import com.test.entity.dto.Account;
import com.test.entity.vo.request.*;
import com.test.mapper.AccountMapper;
import com.test.utils.Const;
import com.test.utils.FlowUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Autowired
    FlowUtils utils;

    @Autowired
    AmqpTemplate amqptemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    PasswordEncoder encoder;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = this.findAccountByNameOrEmail(username);
        if(account == null)
            throw new UsernameNotFoundException("用户名或密码错误!");
        //无论是邮箱或用户名，只需要登录认证即可，不需要别的！
        return User
                .withUsername(username)//邮箱？
                .password(account.getPassword())
                .roles(account.getRole())
                .build();

    }

    @Override
    public String resetEmailAccountPassword(EmailResetVO vo) {
        String email = vo.getEmail();
        String verify = this.resetConfirm(new ConfirmResetVO(email,vo.getCode()));
        if(verify != null) return verify;
        String password = encoder.encode(vo.getPassword());
        boolean update = this.update().eq("email",email).set("password",password).update();
        if(update){
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA +email);
        }
        return null;
    }

    @Override
    public String modifyEmail(int id, ModifyEmailVO vo) {
        String email = vo.getEmail();
        String code = getEmailVerifyCode(email);
        if(code == null) return "请先获取验证码";
        if(code.equals(email)) return "验证码错误，请重新输入!";
        this.deleteEmailVerifyCode(email);
        Account account = this.findAccountByNameOrEmail(email);
        if(account != null && account.getId()!=id)
            return "该邮箱已经被绑定!";
        this.update()
                .eq("id",id)
                .set("email",email)
                .update();
        return null;
    }

    @Override
    public String changePassword(int id, ChangePasswordVO vo) {
        String password = this.query().eq("id",id).one().getPassword();
        if(!passwordEncoder.matches(vo.getPassword(),password))
            return "原密码错误，请重新输入密码!";
        boolean success = this.update()
                .eq("id",id)
                .set("password",passwordEncoder.encode(vo.getNew_password()))
                .update();
        return success ? null : "未知错误，请联系管理员!";
    }

    @Override
    public String resetConfirm(ConfirmResetVO vo) {
        String email = vo.getEmail();
        String code = stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA +email);
        if( code == null) return "请先获取验证码!";
        if(!code.equals(vo.getCode())) return "验证码错误，请重新输入!";
        return null;//为什么返回null?
    }

    public Account findAccountByNameOrEmail(String text){
        return this.query()
                .eq("username",text).or()
                .eq("email",text)
                .one();
    }

    @Override
    public Account findAccountById(int id) {
        return this.query().eq("id",id).one();
    }

    @Override
    public String registerEmailVerifyCode(String type, String email, String ip) {
        //上锁就是限流！！！
        synchronized(ip.intern()){
            if(!this.verifyLimit(ip))
                return "请求频繁，稍后再请求!";
            Random random = new Random();
            int code = random.nextInt(899999) + 100000;
            Map<String , Object> data = Map.of("type",type,"email",email,"code",code);
            //下面的"mail"，是队列的名称！
            amqptemplate.convertAndSend("mail",data);
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA + email,String.valueOf(code),3, TimeUnit.MINUTES);
            return null;
        }
    }

    @Override
    public String registerEmailAccount(EmailRegisterVO vo) {
        String email = vo.getEmail();
        String username = vo.getUsername();
        String key = Const.VERIFY_EMAIL_DATA + email;
        String code = stringRedisTemplate.opsForValue().get(key);
        if(code == null) return "请先申请验证码!";
        if(!code.equals(vo.getCode())) {
            return "验证码有错误，请重新输入！";
        }
        if(existAccountByEmail(email)) return "此电子邮件已经被使用过了，请换一个使用！";
        if(existAccountByUsername(username)) return "此用户名已经被使用了，请换一个使用！";
        String password = encoder.encode(vo.getPassword());
        Account account = new Account(null, username, password, email, "user", new Date());
        if(this.save(account)){
            stringRedisTemplate.delete(key);
            return null;
        }else{
            return "内部错误，请联系管理员!";
        }
    }

    //看看邮箱是否存在于库里面
    private boolean existAccountByEmail(String email){
        return baseMapper.exists(Wrappers.<Account>query().eq("email",email));
    }

    //看看用户名是否存在于库里面
    private boolean existAccountByUsername(String Username){
        return baseMapper.exists(Wrappers.<Account>query().eq("Username",Username));
    }

    //检查是否被限制了
    private boolean verifyLimit(String ip){
        String key = Const.VERIFY_EMAIL_DATA + ip;
        return utils.limitOnceCheck(key, 60);
    }

    /**
     * 获取Redis中存储的邮件验证码
     * @param email 电邮
     * @return 验证码
     */
    private String getEmailVerifyCode(String email){
        String key = Const.VERIFY_EMAIL_DATA + email;
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 移除Redis中存储的邮件验证码
     * @param email 电邮
     */
    private void deleteEmailVerifyCode(String email){
        String key = Const.VERIFY_EMAIL_DATA + email;
        stringRedisTemplate.delete(key);
    }


}
