<script setup>
import {reactive, ref} from "vue";
import {User} from "@element-plus/icons-vue";
import {Lock} from "@element-plus/icons-vue";
import {login} from "@/net/index.js";
import router from "@/router/index.js";



const form = reactive({
  username:'',
  password:'',
  remember:false
})

const formRef = ref();

const rule = {
  username:[
    {required: true,message:"请输入用户名!"}
  ],
  password:[
    {required: true,message:"请输入密码!"}
  ]
}

function userLogin(){
  formRef.value.validate((valid)=> {
    if (valid) {
      login(form.username, form.password, form.remember, () => {
        router.push('/index')
      })
    }
  })
}

</script>

<template>
  <div style="text-align:center;margin:0 20px;">
    <div style="margin-top:130px">
      <div style="font-size: 25px;font-weight: bold">登录</div>
      <div style="font-size:14px;color:grey;">请输入账号和密码，登录即可看关羽5杀压箱底的照片</div>
    </div>
    <div style="margin-top: 50px">
      <el-form :model="form" :rules="rule" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" maxlength="10" placeholder="输入用户名或邮箱！">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password"><!--#prefix 是 Vue 3 的插槽语法，全称 v-slot:prefix 的简写，意思是"在输入框前面插入自定义内容"。-->
          <el-input v-model="form.password" maxlength="20" type="password" placeholder="输入密码">
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-row>
          <el-col :span="12" style="text-align: left">
            <el-form-item><!--label="记住我"，页面上复选框旁边就会显示 “记住我” 这三个字，不用再额外写 <span>记住我</span>，是 Element Plus 封装好的便捷写法。-->
              <el-checkbox v-model="form.remember" label="记住我" size="large" />
            </el-form-item>
          </el-col>
          <el-col :span="12" style="text-align: right";>
            <el-link @click="router.push('/reset')">忘记密码？</el-link>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div><!-- 这里的type里面的属性，是给按钮改变颜色的，不是改变提交的属性！！！-->
      <el-button style="width:270px;" @click="userLogin()" type="success" plain>立即登录</el-button>
    </div>
    <div>
      <el-divider >
        <span style="font-size:13px;color:grey;">没有账号？</span>
      </el-divider>
    </div>
    <div>
      <el-button @click="router.push('/register')" style="font-size:13px;" type="danger" plain>立即注册</el-button>
    </div>
  </div>
</template>

<style scoped>

</style>