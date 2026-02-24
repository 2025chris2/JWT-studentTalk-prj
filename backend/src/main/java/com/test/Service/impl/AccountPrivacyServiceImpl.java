package com.test.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.Service.AccountPrivacyService;
import com.test.entity.dto.AccountPrivacy;
import com.test.entity.vo.request.PrivacySaveVO;
import com.test.mapper.AccountPrivacyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AccountPrivacyServiceImpl extends ServiceImpl<AccountPrivacyMapper, AccountPrivacy> implements AccountPrivacyService {

    @Override
    @Transactional
    public void savePrivacy(int id, PrivacySaveVO vo) {
        AccountPrivacy privacy = Optional.ofNullable(this.getById(id)).orElse(new AccountPrivacy(id));
        boolean status = vo.isStatus();
        switch(vo.getType()){
            case "phone"->privacy.setPhone(status);
            case "email" -> privacy.setEmail(status);
            case "qq" -> privacy.setQq(status);
            case "wx" -> privacy.setWx(status);
            case "gender" -> privacy.setGender(status);
        }
        this.saveOrUpdate(privacy);
    }

    @Override
    public AccountPrivacy accountPrivacy(int id) {
        return Optional.ofNullable(this.getById(id)).orElse(new AccountPrivacy(id));
    }
}
