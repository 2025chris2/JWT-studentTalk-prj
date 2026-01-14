<script setup>
import {computed, reactive, ref} from 'vue';
import {EditPen, Lock, Message} from "@element-plus/icons-vue";
import {get, post} from "@/net/index.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const active = ref(0)
let timer = null
const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
const coldTime = ref(0);
const formRef = ref();

const form = reactive({
  email: '',
  code: '',
  password: '',
  password_repeat: ''
})

const validatePassword = (rule, value, callback) => {
  if(value === ''){
    callback(new Error('请再次输入密码!'))
  } else if(value !== form.password){
    callback(new Error('两次输入的密码不一样，请重新输入!'))
  }else {
    callback();
  }
}

const validateUsername = (rule,value,callback) =>{
  if(value === ''){
    callback(new Error("请输入用户名"))
  }else if(!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)){
    callback(new Error("用户名不能包含特殊字符，只能是中/英文"))
  }else{
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

function confirmReset(){
  formRef.value.validate((valid)=>{
    if(valid){
      post('/api/auth/reset-confirm',{
        email: form.email,
        code: form.code
      },()=>active.value++,)
    }
  })
}

function doRet(){
  formRef.value.validate((valid)=> {
    if (valid) {
      post('/api/auth/reset-password',{...form},()=>{
        ElMessage("重置成功!")
        router.push('/')
      })
    }
  })
}
</script>

<template>
  <div style="text-align:center">
    <div style="margin-top: 50px">
      <el-steps
          style="max-width: 600px"
          :space="200"
          :active="active"
          finish-status="success"
          align-center
      >
        <el-step title="验证电子邮箱" />
        <el-step title="重新设定密码" />
      </el-steps>
    </div>
    <div style="margin: 0 20px" v-if="active === 0">
      <div style="margin-top:80px">
        <div style="font-size:20px;font-weight: bold;">重置密码</div>
        <div style="margin-top:6px">输入你要重置密码的电子邮箱!</div>
      </div>
      <div style="margin: 10px 30px">
        <el-form :model="form" :rules="rule" ref="formRef">
          <el-form-item prop="email">
            <el-input v-model="form.email" type="email" placeholder="邮箱" minlength="5" maxlength="20">
              <template #prefix><Message /></template>
            </el-input>
          </el-form-item>
          <el-form-item prop="code">
            <el-row :gutter="10" style="width:100%">
              <el-col :span="15">
                <el-input placeholder="输入验证码" type="text" v-model="form.code" minlength="6" maxlength="6">
                  <template #prefix><EditPen /></template>
                </el-input>
              </el-col>
              <el-col :span="7" >
                <el-button @click="askCode" :disabled="!isEmailValid||coldTime>0" type="success">
                  {{ coldTime>0 ? `请稍后${coldTime}秒后，重试`: '获取验证码'}}
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
      </div>
      <div style="text-align: center;margin-top:25px">
        <el-button type="warning" @click="confirmReset" plain style="width:270px">开始重置密码!</el-button>
      </div>
    </div>
    <div style="margin: 0 20px" v-if="active === 1">
      <div style="margin-top:80px">
        <div style="font-size:20px;font-weight: bold;">重置密码</div>
        <div style="margin-top:6px">输入新的密码，请务必牢记你的密码！</div>
      </div>
      <div style="margin-top: 50px">
        <el-form :model="form" :rules="rule" ref="formRef">
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
        </el-form>
      </div>
      <div style="text-align: center;margin-top:25px">
        <el-button type="danger" @click="doRet" plain style="width:270px">立即重置密码!</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>