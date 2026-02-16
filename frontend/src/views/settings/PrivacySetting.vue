<script setup>

import {Setting, Switch} from "@element-plus/icons-vue";
import Card from "@/components/Card.vue";
</script>

<template>
  <div>
    <div style="margin-top: 20px">
      <card :icon="Setting" title="隐私设置" desc="在这里可以设置哪些内容可以被其他人看见，请小伙伴们注重自己的隐私!">
        <div class="checkbox-list">
          <el-checkbox>公开展示我的手机号</el-checkbox>
          <el-checkbox>公开展示我的电子邮件地址</el-checkbox>
          <el-checkbox>公开展示我的qq</el-checkbox>
          <el-checkbox>公开展示我的微信号</el-checkbox>
          <el-checkbox>公开展示我的性别</el-checkbox>
        </div>
      </card>
      <card :icon="Setting" style="margin:20px 0"
              title="修改密码" desc="修改密码在这里操作，请务必牢记你的密码!">
        <div>
          <el-form label-width="100" style="margin:20px">
            <el-form-item label="当前密码">
              <el-input :prefix-icon="Lock" placeholder="请输入当前密码" maxlength="16"/>
            </el-form-item>
            <el-form-item label="新密码">
              <el-input :prefix-icon="Lock" placeholder="请输入新密码" maxlength="16"/>
            </el-form-item>
            <el-form-item label="重复新密码">
              <el-input :prefix-icon="Lock" placeholder="请再次输入新密码" maxlength="16"/>
            </el-form-item>
            <div style="text-align:center">
              <el-button :icon="Switch" type="success">立即重置密码!</el-button>
            </div>
          </el-form>
        </div>
      </card>
    </div>
  </div>
</template>

<style scoped>
.checkbox-list{
  margin: 10px 0 0 10px;
  display: flex;
  flex-direction: column;
}
</style>