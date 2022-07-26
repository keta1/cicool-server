# 用户相关api

- [登录](#登录)

---

## 登录

> https://loaclhost:5301/cicool/user/login
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名       | 类型     | 内容          | 必要性         | 备注                                                                                                                                      |
|-----------|--------|-------------|-------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| code      | string | 登录时获取的 code | 必要          | 通过 [wx.login](https://developers.weixin.qq.com/miniprogram/dev/api/open-api/login/wx.login.html) 接口获得临时登录凭证 code 后调用此接口完成登录流程。          |
| nickName  | string | 用户昵称        | 必要          |                                                                                                                                         |
| avatarUrl | string |             | 用户头像图片的 URL | URL 最后一个数值代表正方形头像大小（有 0、46、64、96、132 数值可选，0 代表 640x640 的正方形头像，46 表示 46x46 的正方形头像，剩余数值以此类推。默认132），用户没有头像时该项为空。若用户更换头像，原有头像 URL 将 **失效**。 |

**json回复：**

根对象：

| 字段      | 类型  | 内容   | 备注                                                                                                                   |
|---------|-----|------|----------------------------------------------------------------------------------------------------------------------|
| errcode | num | 返回值  | -1	系统繁忙，此时请开发者稍候再试 <br/> 0	请求成功<br/>403 服务器错误<br/>40029	code 无效<br/>45011	频率限制，每个用户每分钟100次<br/>40226	高风险等级用户，小程序登录拦截 |
| errmsg  | str | 错误信息 |                                                                                                                      |
| data    | obj | 用户信息 |                                                                                                                      |

`data`对象：

| 字段         | 类型  | 内容          | 备注         |
|------------|-----|-------------|------------|
| id         | num | 用户id        |            |
| nickName   | str | 用户昵称        |            |
| avatarUrl  | str | 用户头像图片的 URL |            |
| createTime | num | 账户创建时间      |            |
| lastLogin  | num | 最后一次登录的时间   |            |
| bookId     | num | 用户选择的词书id   | 若未选中词书则未-1 |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 0,
  "errmsg": null,
  "data": {
    "id": 1,
    "avatarPic": "https://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eqNR8np76ZtqtkVj0UVFMZTMzckseuaU84ibIXic7twkmIHUdOficuBicTcVoKfvlNxuyxkXK7uuiawNqQ/132",
    "nickName": "ketal",
    "createTime": 1658825071,
    "lastLogin": 1658825071,
    "bookId": -1
  }
}
```

</details>
