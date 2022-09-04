# 用户相关api

- [登录](#登录)
- [获取用户信息](#获取用户信息)
- [修改用户信息](#修改用户信息)
- [上传用户头像](#上传用户头像)
- [获取用户头像](#获取用户头像)
- [修改单词书](#修改单词书)
- [修改用户设置](#修改用户设置)

---

## 登录

> https://loaclhost:5301/cicool/user/login
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名       | 类型  | 内容          | 必要性         | 备注                                                                                                                                      |
|-----------|-----|-------------|-------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| code      | str | 登录时获取的 code | 必要          | 通过 [wx.login](https://developers.weixin.qq.com/miniprogram/dev/api/open-api/login/wx.login.html) 接口获得临时登录凭证 code 后调用此接口完成登录流程。          |

**json回复：**

根对象：

| 字段      | 类型  | 内容   | 备注                                                                                                                   |
|---------|-----|------|----------------------------------------------------------------------------------------------------------------------|
| errcode | num | 返回值  | -1	系统繁忙，此时请开发者稍候再试 <br/> 0	请求成功<br/>500 服务器错误<br/>40029	code 无效<br/>45011	频率限制，每个用户每分钟100次<br/>40226	高风险等级用户，小程序登录拦截 |
| errmsg  | str | 错误信息 |                                                                                                                      |
| data    | obj | 用户信息 |                                                                                                                      |

`data`对象：

| 字段         | 类型  | 内容        | 备注         |
|------------|-----|-----------|------------|
| id         | num | 用户id      |            |
| token      | str | 用户凭证      |            |
| nickName   | str | 用户昵称      |            |                                                                                                                                         |
| createTime | num | 账户创建时间    |            |
| lastLogin  | num | 最后一次登录的时间 |            |
| bookId     | num | 用户选择的词书id | 若未选择词书则为-1 |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 0,
  "errmsg": null,
  "data": {
    "id": 1,
    "token": "xxxxxxxxxxxxxxxxxxxx",
    "avatarPic": "https://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eqNR8np76ZtqtkVj0UVFMZTMzckseuaU84ibIXic7twkmIHUdOficuBicTcVoKfvlNxuyxkXK7uuiawNqQ/132",
    "nickName": "ketal",
    "createTime": 1658825071,
    "lastLogin": 1658825071,
    "bookId": -1
  }
}
```

</details>

## 获取用户信息

> https://loaclhost:5301/cicool/user/getUserInfo
*请求方式：POST*

认证方式：Cookie(TOKEN)

**url参数：**

Content-Type: application/json

| 参数名    | 类型  | 内容   | 必要性 | 备注  |
|--------|-----|------|-----|-----|
| userId | num | 用户id | 必要  |     |

**json回复：**

根对象：

| 字段      | 类型  | 内容   | 备注                   |
|---------|-----|------|----------------------|
| errcode | num | 返回值  | 0 请求成功<br/>500 服务器错误 |
| errmsg  | str | 错误信息 |                      |
| data    | obj | 用户信息 |                      |

`data`对象：

| 字段         | 类型  | 内容        | 备注         |
|------------|-----|-----------|------------|
| nickName   | str | 用户昵称      |            |
| createTime | num | 账户创建时间    |            |
| lastLogin  | num | 最后一次登录的时间 |            |
| bookId     | num | 用户选择的词书id | 若未选中词书则为-1 |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 0,
  "errmsg": null,
  "data": {
    "avatarPic": "https://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eqNR8np76ZtqtkVj0UVFMZTMzckseuaU84ibIXic7twkmIHUdOficuBicTcVoKfvlNxuyxkXK7uuiawNqQ/132",
    "nickName": "ketal",
    "createTime": 1658825071,
    "lastLogin": 1658825071,
    "bookId": -1
  }
}
```

</details>

## 修改用户信息

> https://loaclhost:5301/cicool/user/changeUserInfo
*请求方式：POST*

认证方式：Cookie(TOKEN)

**url参数：**

Content-Type: application/json

| 参数名      | 类型  | 内容   | 必要性 | 备注  |
|----------|-----|------|-----|-----|
| userId   | num | 用户id | 必要  |     |
| nickName | str | 用户昵称 | 非必要 |     |

**json回复：**

根对象：

| 字段      | 类型  | 内容   | 备注                   |
|---------|-----|------|----------------------|
| errcode | num | 返回值  | 0 请求成功<br/>500 服务器错误 |
| errmsg  | str | 错误信息 |                      |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 0,
  "errmsg": "OK"
}
```

</details>

## 上传用户头像

> https://loaclhost:5301/cicool/user/uploadAvatar
*请求方式：POST*

认证方式：Cookie(TOKEN)

**Header：**

| 参数名       | 类型  | 内容   | 必要性 | 备注  |
|-----------|-----|------|-----|-----|
| X-User-Id | num | 用户id | 必要  |     |

**json回复：**

根对象：

| 字段      | 类型  | 内容   | 备注                   |
|---------|-----|------|----------------------|
| errcode | num | 返回值  | 0 请求成功<br/>500 服务器错误 |
| errmsg  | str | 错误信息 |                      |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 0,
  "errmsg": "OK"
}
```

</details>

## 获取用户头像

> https://loaclhost:5301/cicool/user/getAvatar
*请求方式：POST*

认证方式：Cookie(TOKEN)

**url参数：**

Content-Type: application/json

| 参数名    | 类型  | 内容   | 必要性 | 备注  |
|--------|-----|------|-----|-----|
| userId | num | 用户id | 必要  |     |

## 修改单词书

> https://loaclhost:5301/cicool/user/changeWordBook
*请求方式：POST*

认证方式：Cookie(TOKEN)

**url参数：**

Content-Type: application/json

| 参数名    | 类型  | 内容    | 必要性 | 备注  |
|--------|-----|-------|-----|-----|
| userId | num | 用户id  | 必要  |     |
| bookId | num | 单词书id | 必要  |     |

**json回复：**

根对象：

| 字段      | 类型  | 内容   | 备注                   |
|---------|-----|------|----------------------|
| errcode | num | 返回值  | 0 请求成功<br/>500 服务器错误 |
| errmsg  | str | 错误信息 |                      |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 0,
  "errmsg": "OK"
}
```

</details>

## 修改用户设置

> https://loaclhost:5301/cicool/user/changeUserSetting
*请求方式：POST*

认证方式：Cookie(TOKEN)

**url参数：**

Content-Type: application/json

| 参数名      | 类型  | 内容   | 必要性 | 备注  |
|----------|-----|------|-----|-----|
| userId   | num | 用户id | 必要  |     |
| settings | str | 用户设置 | 必要  |     |

**json回复：**

根对象：

| 字段      | 类型  | 内容   | 备注                   |
|---------|-----|------|----------------------|
| errcode | num | 返回值  | 0 请求成功<br/>500 服务器错误 |
| errmsg  | str | 错误信息 |                      |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 0,
  "errmsg": "OK"
}
```

</details>
