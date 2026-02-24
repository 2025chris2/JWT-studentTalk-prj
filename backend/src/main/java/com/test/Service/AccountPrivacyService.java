package com.test.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.test.entity.dto.AccountPrivacy;
import com.test.entity.vo.request.PrivacySaveVO;

public interface AccountPrivacyService extends IService<AccountPrivacy> {
    void savePrivacy(int id, PrivacySaveVO vo);
    AccountPrivacy accountPrivacy(int id);
}
