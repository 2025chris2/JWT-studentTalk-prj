package com.test.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.Service.AccountDetailsService;
import com.test.Service.AccountService;
import com.test.entity.dto.Account;
import com.test.entity.dto.AccountDetails;
import com.test.entity.vo.request.DetailsSaveVO;
import com.test.mapper.AccountDetailsMapper;
import com.test.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountDetailsServiceImpl extends ServiceImpl<AccountDetailsMapper, AccountDetails> implements AccountDetailsService {

    @Autowired
    AccountService service;

    @Override
    public AccountDetails findAccountDetailsById(int id) {
        return this.getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)  // 任一失败全回滚
    public synchronized boolean saveAccountDetails(int id, DetailsSaveVO vo) {
        Account account = service.findAccountByNameOrEmail(vo.getUsername());
        if( account == null || account.getId() == id){
            if (service.update()
                    .eq("id",id)
                    .set("username",vo.getUsername())
                    .update()) {
                this.saveOrUpdate(new AccountDetails(
                        id,vo.getGender(),vo.getPhone(),
                        vo.getQq(),vo.getWx(),vo.getDesc()
                ));
            }
            return true;
        }
        return false;
    }
}
