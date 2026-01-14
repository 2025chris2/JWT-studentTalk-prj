<script setup>
import {computed, onUnmounted, reactive, ref} from 'vue';
import {User, Lock, Message, EditPen} from "@element-plus/icons-vue";
import router from "@/router/index.js";
import {get, post} from "@/net/index.js";
import {ElMessage} from "element-plus";

const coldTime = ref(0);
const formRef = ref();

const form = reactive({
  username: "",
  password: "",
  password_repeat: "",
  email: "",
  code: "",
})

const validateUsername = (rule,value,callback) =>{
  if(value === ''){
    callback(new Error("请输入用户名"))
  }else if(!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)){
    callback(new Error("用户名不能包含特殊字符，只能是中/英文"))
  }else{
    callback();
  }
}

const validatePassword = (rule, value, callback) => {
  if(value === ''){
    callback(new Error('请再次输入密码!'))
  } else if(value !== form.password){
    callback(new Error('两次输入的密码不一样，请重新输入!'))
  }else {
    callback();
  }
}

const rule = {
  //为什么给表单的item传呢？
  username: [
    { validator : validateUsername , trigger : [ 'blur' , 'change' ] },
    { min : 1 , max : 10 , message : "用户名必须在2~8之间!" , trigger: [ 'blur' , 'change' ] }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur'},
    { min: 6,max: 20, message: '密码的长度必须在6-20个字符之间!', trigger: ['blur', 'change'] }
  ],
  password_repeat: [
    {validator: validatePassword , trigger: [ 'blur', 'change']},
  ],
  email: [
    {required: true, message: '请输入邮箱地址', trigger: 'blur'},
    {type: 'email', message: '请输入合法的邮箱地址', trigger: [ 'blur', 'change']}
  ],
  code: [
    {required: true, message: '请输入验证码!', trigger: 'blur'}
  ]
}

const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
let timer = null

// 组件卸载时清理定时器
onUnmounted(() => {
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
});

function askCode(){
  if(isEmailValid.value) {
    coldTime.value=60;
    if(timer) clearInterval(timer);
    get(`/api/auth/ask-code?email=${form.email}&type=register`, ()=>{
      ElMessage.success(`邮箱已经发送给${form.email},注意查收!`)
      timer = setInterval(() => {
        coldTime.value--;
        if (coldTime.value <= 0) {
          clearInterval(timer);  // ✅ 到0时停止
        }
      }, 1000);
    },(message)=>{
      ElMessage.warning(message)
      coldTime.value=0;
      if(timer) {
        clearInterval(timer);
        timer = null;
      }
    })
  }else{
    ElMessage.warning("请输入正确的电子邮件！")
  }

}

const isEmailValid = computed(()=> emailRegex.test(form.email))

function register(){
  formRef.value.validate((valid) =>{
    if(valid){
      post('/api/auth/register',{...form},()=>{
        ElMessage.success("注册成功，欢迎加入我们！")
        router.push('/');
      })
    }else{
      ElMessage.warning("请完整填写表单内容!")
    }
  })
}
</script>

<template>
  <div style="text-align:center;margin: 0 20px">
    <div style="margin-top:100px">
      <div style="font-size:25px;font-weight: bold">注册新用户!</div>
      <div style="font-size:14px;color:grey">欢迎注册我们的学习平台，请在下方添加信息</div>
    </div>
    <div style="margin-top:50px">
      <el-form :model="form" :rules="rule" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" maxlength="10" placeholder="用户名" type="text">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" maxlength="20" placeholder="密码" type="password">
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password_repeat">
          <el-input v-model="form.password_repeat" maxlength="20" type="password" placeholder="请再次输入你的密码">
            <template #prefix>
              <el-icon>
                <Lock />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" placeholder="邮件">
            <template #prefix><el-icon><Message /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code">
          <el-row :gutter="10" style="width:100%">
            <el-col :span="15">
              <el-input v-model="form.code" maxlength="6" minlength="6" type="text" placeholder="验证码">
                <template #prefix><el-icon><EditPen /></el-icon></template>
              </el-input>
            </el-col>
            <el-col :span="7">
              <el-button @click="askCode" :disabled="!isEmailValid||coldTime>0" type="success">
                {{ coldTime>0 ? `请稍后${coldTime}秒后，重试`: '获取验证码'}}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
    </div>
    <div style="text-align: center;margin-top:80px">
      <el-button type="warning" @click="register" plain style="width:270px">立即注册!</el-button>
    </div>
    <div style="margin-top:20px">
      <span style="font-size:14px;line-height: 14px;color:grey">已有账号？</span>
      <el-link style="translate:0 -1px" @click="router.push('/')">立即登录</el-link>
    </div>
  </div>

</template>

<style scoped>

</style>