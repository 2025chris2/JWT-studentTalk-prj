import axios from 'axios';
import {ElMessage} from "element-plus";

const authItemName = "access_token"

const defaultFailure = (message, code, url) =>{
    console.warn(`请求地址:${url},状态码:${code},错误信息:${message}`)
    ElMessage.warning(message);
}

const defaultError = (err) =>{
    console.error(err);
    ElMessage.warning('发生了一下错误');
}

function takeAccessToken(){
    const str = localStorage.getItem(authItemName)||sessionStorage.getItem(authItemName);
    if(!str) return null;
    const authObj = JSON.parse(str);
    if(authObj.expire <= new Date()){
        deleteAccessToken();
        ElMessage("登录状态已经过期，请重新登录!")
        return null;
    }
    return authObj.token;
}

function deleteAccessToken(){
    localStorage.removeItem(authItemName);
    sessionStorage.removeItem(authItemName);
}

function storeAccessToken(token, remember, expire){
    const authObj = {token: token, expire: expire};
    const str = JSON.stringify(authObj);
    if(remember)
        localStorage.setItem(authItemName, str);
    else
        sessionStorage.setItem(authItemName, str);
}

//为什么error=defaultError,不是直接传函数呢
function internalPost(url, data, header, success, failure, error=defaultError) {
    axios.post(url, data, {headers:header}).then(({data})=>{//为什么data加{}
        if(data.code === 200){
            success(data.data);
        }
        else{
            failure(data.message)
        }
    }).catch(err => {
        // HTTP 错误也转给 failure 处理
        if (err.response?.data) {
            // 后端返回了错误体
            failure(err.response.data.message, err.response.status, url);
        } else {
            error(err);
        }
    });
}

function internalGet(url, header, success, failure, error=defaultError) {
    axios.get(url, {headers:header}).then(({data})=>{//为什么data加{}
        if(data.code === 200){
            success(data.data);
        }
        else{
            failure(data.message)
        }
    }).catch(err => error(err))
}

function login(username,password,remember,success,failure=defaultError) {
    internalPost('/api/auth/login',{
        username:username,password:password
    },{'Content-Type':'application/x-www-form-urlencoded'},(data)=>{
        storeAccessToken(data.token,remember,data.expire);
        ElMessage(`欢迎${data.username}登录！`)
        success(data)
    },failure)
}

function get(url, success, failure = defaultFailure){
    internalGet(url,accessHeader(),success,failure);
}

function post(url, data, success, failure= defaultFailure){
    internalPost(url,data,accessHeader(),success,failure);
}

function accessHeader(){
    const token = takeAccessToken();
    return token?{
        'Authorization': `Bearer ${takeAccessToken()}`
    }:{};
}

function logout(success, failure= defaultFailure){
    get('/api/auth/logout',()=>{
        deleteAccessToken()
        ElMessage("退出成功，欢迎下次使用！！！")
        success()
    },failure)
}

function unauthorized(){
    return !takeAccessToken()
}

export { login, logout, get, post, unauthorized}
