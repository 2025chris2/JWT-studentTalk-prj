<script setup>

import Card from "@/components/Card.vue";
import {Message, Refresh, Select, User} from "@element-plus/icons-vue";
import {useStore} from "@/store/index.js";
import {computed, reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import {get, post} from "@/net/index.js";

const desc = ref('')
const baseFormRef = ref()
const emailFormRef = ref()

const baseForm = reactive({
  username: '',
  gender: 1,
  phone: '',
  qq: '',
  wx: '',
  desc: ''
})

const emailForm = reactive({
  email: '',
  code: ''
})

const validateUsername = (rule, value, callback)=>{
  if( value === ''){
    callback(new Error("请输入用户名!"))
  }else if(!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)){
    callback(new Error("用户名不能包含特殊字符，只能是中文/英文"))
  }else{
    callback()
  }
}

const rules = {
  username: [
    { validator: validateUsername, trigger: [ 'blur', 'change']},
    { min: 2, max: 10, message: '用户名必须在2~10个字符之间!', trigger: [ 'blur', 'change']}
  ],email: [
    { require: true, message: '请输入邮件地址!', trigger: [ 'blur']},
    { type: 'email', message: '请输入合法的邮件地址!', trigger: [ 'blur', 'change']},
  ],code: [
    { require: true, message: '请输入验证码!', trigger: [ 'blur']}
  ]
}

const store = useStore()
const registerTime=computed(()=>new Date(store.user.registerTime).toDateString())

const loading = reactive({
  form: true,
  base: false
})

function saveDetails(){
  baseFormRef.value.validate(isValid =>{
    if(isValid){
      loading.base = true
      post('/api/user/save-details', baseForm, ()=>{
        ElMessage.success( '用户保存成功！' )
        store.user.username = baseForm.username;
        desc.value = baseForm.desc
        loading.base = false
      },(err) => {  // ← 加上错误回调
        loading.base = false
        console.error('完整错误:', err);
        console.error('后端返回:', err.response?.data);
        ElMessage.error(err.response?.data?.message || '保存失败');
      })
    }
  })
}

get('/api/user/details', data=>{
  baseForm.username = store.user.username;
  baseForm.gender = data.gender;
  baseForm.phone = data.phone;
  baseForm.wx = data.wx;
  baseForm.qq = data.qq
  baseForm.desc = desc.value = data.desc
  loading.form = false
})
</script>

<template>
 <div style="display: flex">
  <div class="setting-left">
    <card v-loading="loading.form" :icon="User" title="账号信息设置" desc="在这里编辑您的个人信息，您可以在隐私设置中选择是否展示这些信息">
      <el-form ref="baseFormRef" :rules="rules" :model="baseForm" label-position="top" style="margin: 0 10px 10px 10px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="baseForm.username" maxlength="10"/>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="baseForm.gender">
            <el-radio :value="0">男</el-radio>
            <el-radio :value="1">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="baseForm.phone" maxlength="11"/>
        </el-form-item>
        <el-form-item label="QQ号" prop="qq">
          <el-input v-model="baseForm.qq" maxlength="13"/>
        </el-form-item>
        <el-form-item label="微信号" prop="wx">
          <el-input v-model="baseForm.wx" maxlength="20"/>
        </el-form-item>
        <el-form-item label="个人简介" prop="desc">
          <el-input type="textarea" :rows="6" maxlength="200" v-model="baseForm.desc"/>
        </el-form-item>
        <div>
          <el-button @click="saveDetails" :loading="loading.base" :icon="Select">保存用户信息</el-button>
        </div>
      </el-form>
    </card>
    <card style="margin-top: 10px" :icon="Message" title="电子邮件设置" desc="您可以修改默认绑定的电子邮件地址">
      <el-form ref="emailFormRef" :rules="rules" :model="emailForm" label-position="top" style="margin: 0 10px 10px 10px">
        <el-form-item label="电子邮件" prop="email">
          <el-input  v-model="emailForm.email"/>
        </el-form-item>
        <el-form-item>
          <el-row style="width: 100%;" :gutter="10">
            <el-col :span="18" prop="code">
              <el-input placeholder="请获取验证码!"  v-model="emailForm.code"/>
            </el-col>
            <el-col :span="6">
              <el-button type="success" style="width: 100%" plain>获取验证码</el-button>
            </el-col>
          </el-row>
        </el-form-item>
        <div>
          <el-button :icon="Refresh" type="success">更新电子邮件</el-button>
        </div>
      </el-form>
    </card>
  </div>
  <div class="setting-right">
    <div style="position: sticky;top:20px">
      <card>
        <div style="text-align: center; padding: 5px 15px 0 15px">
            <el-avatar :size="70" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"></el-avatar>
            <div style="font-weight: bold;">您好,{{store.user.username}}</div>
        </div>
        <el-divider style="margin: 10px 0"/>
        <div style="font-size:14px;color: grey;padding: 10px">
          {{desc || "这个用户很懒,没有填写用户简介~"}}
        </div>
      </card>
      <card style="margin-top: 10px;font-size: 14px">
        <div>账号注册时间:{{registerTime}}</div>
        <div style="color:grey">欢迎加入我们的学习论坛！</div>
      </card>
    </div>

  </div>
 </div>
</template>

<style scoped>
.setting-left{
  flex: 1;
  margin: 20px;
}

.setting-right{
  width: 300px;
  margin: 20px 30px 20px 0;
}
</style>