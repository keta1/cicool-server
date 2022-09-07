# 统计相关api

- [获取指定词书学习数据](#获取指定词书学习数据)
- [获取所有词书数据](#获取所有词书数据)
- [获取指定词书数据](#获取指定词书数据)
- [获取所有学习数据](#获取所有学习数据)
- [获取今日学习数据](#获取今日学习数据)
- [获取指定时间段统计数据](#获取指定时间段统计数据)
- [获取每日统计数据](#获取每日统计数据)
- [获取生词本单词](#获取生词本单词)
- [获取指定词书学习过的单词](#获取指定词书学习过的单词)
- [获取指定词书掌握的单词](#获取指定词书掌握的单词)
- [获取指定词书未学的单词](#获取指定词书未学的单词)

---

## 获取指定词书学习数据

> https://loaclhost:5301/statistic/getWBLearnData
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名    | 类型  | 内容   | 必要性 | 备注  |
|--------|-----|------|-----|-----|
| userId | num | 用户id | 必要  |     |
| bookId | num | 词书id | 必要  |     |

**json回复：**

根对象：

| 字段      | 类型  | 内容   | 备注  |
|---------|-----|------|-----|
| errcode | num | 返回值  |     |
| errmsg  | str | 错误信息 |     |
| data    | obj |      |     |

`data`对象：

| 字段       | 类型  | 内容        | 备注  |
|----------|-----|-----------|-----|
| notLearn | num | 没有学习的单词数量 |     |
| learn    | num | 学习过的单词数量  |     |
| master   | num | 掌握的单词数量   |     |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 200,
  "errmsg": null,
  "data": {
    "notLearn": 3676,
    "learn": 1,
    "master": 1
  }
}
```

</details>

## 获取所有词书数据

> https://loaclhost:5301/statistic/getAllWBData
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名    | 类型  | 内容   | 必要性 | 备注  |
|--------|-----|------|-----|-----|
| userId | num | 用户id | 必要  |     |

**json回复：**

根对象：

| 字段      | 类型          | 内容   | 备注  |
|---------|-------------|------|-----|
| errcode | num         | 返回值  |     |
| errmsg  | str         | 错误信息 |     |
| books   | list `book` | 词书数据 |     |

`book`对象：

| 字段          | 类型  | 内容   | 备注        |
|-------------|-----|------|-----------|
| bookId      | num | 词书id |           |
| name        | str | 名字   |           |
| description | str | 描述   |           |
| total       | num | 单词总数 |           |
| coverType   | str | 封面类型 | color/pic |
| color       | str | 颜色值  | 可能为空      |
| coverUrl    | str | 图片连接 | 可能为空      |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 200,
  "errmsg": null,
  "books": [
    {
      "name": "中考",
      "description": "初中课标词汇",
      "total": 1603,
      "coverType": "color",
      "color": "#66ccff",
      "coverUrl": null
    },
    {
      "name": "高考",
      "description": "高考大纲词汇",
      "total": 3677,
      "coverType": "color",
      "color": "#66ccff",
      "coverUrl": null
    }
  ]
}
```

</details>

## 获取指定词书数据

> https://loaclhost:5301/statistic/getSingleWBData
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名    | 类型  | 内容   | 必要性 | 备注  |
|--------|-----|------|-----|-----|
| userId | num | 用户id | 必要  |     |
| bookId | num | 词书id | 必要  |     |

**json回复：**

根对象：

| 字段           | 类型  | 内容   | 备注  |
|--------------|-----|------|-----|
| errcode      | num | 返回值  |     |
| errmsg       | str | 错误信息 |     |
| book         | obj | 词书   |     |

`book`对象：

| 字段          | 类型  | 内容   | 备注             |
|-------------|-----|------|----------------|
| bookId      | num | 词书id |                |
| name        | str | 名字   |                |
| description | str | 描述   |                |
| total       | num | 单词总数 |                |
| coverType   | str | 封面类型 | color/imageUrl |
| color       | str | 颜色值  | 可能为空           |
| coverUrl    | str | 图片连接 | 可能为空           |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 0,
  "errmsg": null,
  "book": {
    "name": "高考",
    "description": "高考大纲词汇",
    "total": 3677,
    "coverType": "color",
    "color": "#66ccff",
    "coverUrl": null
  }
}
```

</details>

## 获取所有学习数据

> https://loaclhost:5301/statistic/getAllLearnData
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名    | 类型   | 内容   | 必要性 | 备注  |
|--------|------|------|-----|-----|
| userId | num  | 用户id | 必要  |     |

**json回复：**

根对象：

| 字段      | 类型  | 内容      | 备注  |
|---------|-----|---------|-----|
| errcode | num | 返回值     |     |
| errmsg  | str | 错误信息    |     |
| learn   | num | 学习的单词数量 |     |
| master  | num | 掌握的单词数量 |     |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 0,
  "errmsg": null,
  "learn": 2,
  "master": 2
}
```

</details>

## 获取今日学习数据

> https://loaclhost:5301/statistic/getTodayLearnData
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名        | 类型   | 内容       | 必要性 | 备注      |
|------------|------|----------|-----|---------|
| userId     | num  | 用户id     | 必要  |         |

**json回复：**

根对象：

| 字段      | 类型  | 内容   | 备注  |
|---------|-----|------|-----|
| errcode | num | 返回值  |     |
| errmsg  | str | 错误信息 |     |
| data    | obj | 学习数据 |     |

`data`对象：

| 字段        | 类型   | 内容      | 备注  |
|-----------|------|---------|-----|
| learnTime | time | 学习的时间   |     |
| learn     | str  | 学习的单词数量 |     |
| review    | str  | 复习的单词数量 |     |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 0,
  "errmsg": null,
  "data": {
    "learnTime": 0,
    "learn": 0,
    "review": 0
  }
}
```

</details>

## 获取指定时间段统计数据

> https://loaclhost:5301/statistic/getDailySumByDate
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名       | 类型   | 内容   | 必要性 | 备注    |
|-----------|------|------|-----|-------|
| userId    | num  | 用户id | 必要  |       |
| startDate | date | 开始日期 | 必要  |       |
| endDate   | date | 结束日期 | 非必要 | 默认为今天 |

**json回复：**

根对象：

| 字段       | 类型  | 内容   | 备注  |
|----------|-----|------|-----|
| errcode  | num | 返回值  |     |
| errmsg   | str | 错误信息 |     |
| dailySum | obj | 学习数据 |     |

`dailySum`对象：

| 字段     | 类型   | 内容      | 备注  |
|--------|------|---------|-----|
| date   | date | 日期      |     |
| learn  | num  | 学习的单词数量 |     |
| review | num  | 复习的单词数量 |     |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 0,
  "errmsg": null,
  "dailySum": [
    {
      "date": "2022-08-11",
      "learn": 0,
      "review": 5
    }
  ]
}
```

</details>

## 获取每日统计数据

> https://loaclhost:5301/statistic/getDailySum
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名    | 类型  | 内容   | 必要性 | 备注        |
|--------|-----|------|-----|-----------|
| userId | num | 用户id | 必要  |           |
| size   | num | 获取数量 | 非必要 | 默认返回10个结果 |
| skip   | num | 跳过数量 | 非必要 |           |

**json回复：**

根对象：

| 字段       | 类型              | 内容   | 备注                   |
|----------|-----------------|------|----------------------|
| errcode  | num             | 返回值  | 0 请求成功<br/>500 服务器错误 |
| errmsg   | str             | 错误信息 |                      |
| dailySum | list `dailySum` |      |                      |

`dailySum`对象：

| 字段     | 类型   | 内容      | 备注  |
|--------|------|---------|-----|
| date   | date | 日期      |     |
| learn  | num  | 学习的单词数量 |     |
| review | num  | 复习的单词数量 |     |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 0,
  "errmsg": null,
  "dailySum": [
    {
      "date": "2022-08-11",
      "learn": 0,
      "review": 5
    }
  ]
}
```

</details>

## 获取生词本单词

> https://loaclhost:5301/statistic/getNoteBookWord
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名    | 类型  | 内容   | 必要性 | 备注        |
|--------|-----|------|-----|-----------|
| userId | num | 用户id | 必要  |           |
| size   | num | 获取数量 | 非必要 | 默认返回20个结果 |
| skip   | num | 跳过数量 | 非必要 |           |

**json回复：**

根对象：

| 字段      | 类型          | 内容   | 备注                   |
|---------|-------------|------|----------------------|
| errcode | num         | 返回值  | 0 请求成功<br/>500 服务器错误 |
| errmsg  | str         | 错误信息 |                      |
| words   | list `word` |      |                      |

`word`对象：

| 字段          | 类型  | 内容   | 备注   |
|-------------|-----|------|------|
| wordId      | num | 单词id |      |
| word        | str | 单词   |      |
| translation | str | 翻译   | 可能为空 |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 0,
  "errmsg": null,
  "words": [
    {
      "wordId": 2689,
      "word": "abandon",
      "translation": "vt. 放弃, 抛弃, 遗弃, 使屈从, 沉溺, 放纵\nn. 放任, 无拘束, 狂热"
    }
  ]
}
```

</details>

## 获取指定词书学习过的单词

> https://loaclhost:5301/statistic/getBkLearnedWord
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名    | 类型  | 内容   | 必要性 | 备注        |
|--------|-----|------|-----|-----------|
| userId | num | 用户id | 必要  |           |
| bookId | num | 词书id | 必要  |           |
| size   | num | 获取数量 | 非必要 | 默认返回20个结果 |
| skip   | num | 跳过数量 | 非必要 |           |

**json回复：**

根对象：

| 字段      | 类型          | 内容   | 备注                   |
|---------|-------------|------|----------------------|
| errcode | num         | 返回值  | 0 请求成功<br/>500 服务器错误 |
| errmsg  | str         | 错误信息 |                      |
| words   | list `word` |      |                      |

`word`对象：

| 字段          | 类型  | 内容   | 备注   |
|-------------|-----|------|------|
| wordId      | num | 单词id |      |
| word        | str | 单词   |      |
| translation | str | 翻译   | 可能为空 |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 200,
  "errmsg": null,
  "words": [
    {
      "wordId": 23806,
      "word": "all",
      "translation": "a. 所有的, 全部的, 一切的\nadv. 全部, 全然\npron. 全部\nn. 全部"
    }
  ]
}
```

</details>

## 获取指定词书掌握的单词

> https://loaclhost:5301/statistic/getBkMasteredWord
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名    | 类型  | 内容   | 必要性 | 备注        |
|--------|-----|------|-----|-----------|
| userId | num | 用户id | 必要  |           |
| bookId | num | 词书id | 必要  |           |
| size   | num | 获取数量 | 非必要 | 默认返回20个结果 |
| skip   | num | 跳过数量 | 非必要 |           |

**json回复：**

根对象：

| 字段      | 类型          | 内容   | 备注                   |
|---------|-------------|------|----------------------|
| errcode | num         | 返回值  | 0 请求成功<br/>500 服务器错误 |
| errmsg  | str         | 错误信息 |                      |
| words   | list `word` |      |                      |

`word`对象：

| 字段          | 类型  | 内容   | 备注   |
|-------------|-----|------|------|
| wordId      | num | 单词id |      |
| word        | str | 单词   |      |
| translation | str | 翻译   | 可能为空 |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 200,
  "errmsg": null,
  "words": [
    {
      "wordId": 23806,
      "word": "all",
      "translation": "a. 所有的, 全部的, 一切的\nadv. 全部, 全然\npron. 全部\nn. 全部"
    }
  ]
}
```

</details>

## 获取指定词书未学的单词

> https://loaclhost:5301/statistic/getBkUnlearnedWord
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名    | 类型  | 内容   | 必要性 | 备注        |
|--------|-----|------|-----|-----------|
| userId | num | 用户id | 必要  |           |
| bookId | num | 词书id | 必要  |           |
| size   | num | 获取数量 | 非必要 | 默认返回20个结果 |
| skip   | num | 跳过数量 | 非必要 |           |

**json回复：**

根对象：

| 字段      | 类型          | 内容   | 备注                   |
|---------|-------------|------|----------------------|
| errcode | num         | 返回值  | 0 请求成功<br/>500 服务器错误 |
| errmsg  | str         | 错误信息 |                      |
| words   | list `word` |      |                      |

`word`对象：

| 字段          | 类型  | 内容   | 备注   |
|-------------|-----|------|------|
| wordId      | num | 单词id |      |
| word        | str | 单词   |      |
| translation | str | 翻译   | 可能为空 |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 200,
  "errmsg": null,
  "words": [
    {
      "wordId": 23806,
      "word": "all",
      "translation": "a. 所有的, 全部的, 一切的\nadv. 全部, 全然\npron. 全部\nn. 全部"
    }
  ]
}
```

</details>

## 获取指定词书的单词

> https://loaclhost:5301/statistic/getBkWord
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名    | 类型  | 内容   | 必要性 | 备注        |
|--------|-----|------|-----|-----------|
| userId | num | 用户id | 必要  |           |
| bookId | num | 词书id | 必要  |           |
| size   | num | 获取数量 | 非必要 | 默认返回20个结果 |
| skip   | num | 跳过数量 | 非必要 |           |

**json回复：**

根对象：

| 字段      | 类型          | 内容   | 备注                   |
|---------|-------------|------|----------------------|
| errcode | num         | 返回值  | 0 请求成功<br/>500 服务器错误 |
| errmsg  | str         | 错误信息 |                      |
| words   | list `word` |      |                      |

`word`对象：

| 字段          | 类型  | 内容   | 备注   |
|-------------|-----|------|------|
| wordId      | num | 单词id |      |
| word        | str | 单词   |      |
| translation | str | 翻译   | 可能为空 |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 200,
  "errmsg": null,
  "words": [
    {
      "wordId": 23806,
      "word": "all",
      "translation": "a. 所有的, 全部的, 一切的\nadv. 全部, 全然\npron. 全部\nn. 全部"
    }
  ]
}
```

</details>

## 获取学习过的单词

> https://loaclhost:5301/statistic/getLearnedWord
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名    | 类型  | 内容   | 必要性 | 备注        |
|--------|-----|------|-----|-----------|
| userId | num | 用户id | 必要  |           |
| size   | num | 获取数量 | 非必要 | 默认返回20个结果 |
| skip   | num | 跳过数量 | 非必要 |           |

**json回复：**

根对象：

| 字段      | 类型          | 内容   | 备注                   |
|---------|-------------|------|----------------------|
| errcode | num         | 返回值  | 0 请求成功<br/>500 服务器错误 |
| errmsg  | str         | 错误信息 |                      |
| words   | list `word` |      |                      |

`word`对象：

| 字段          | 类型  | 内容   | 备注   |
|-------------|-----|------|------|
| wordId      | num | 单词id |      |
| word        | str | 单词   |      |
| translation | str | 翻译   | 可能为空 |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 200,
  "errmsg": null,
  "words": [
    {
      "wordId": 23806,
      "word": "all",
      "translation": "a. 所有的, 全部的, 一切的\nadv. 全部, 全然\npron. 全部\nn. 全部"
    }
  ]
}
```

</details>

## 获取掌握的单词

> https://loaclhost:5301/statistic/getMasteredWord
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名    | 类型  | 内容   | 必要性 | 备注        |
|--------|-----|------|-----|-----------|
| userId | num | 用户id | 必要  |           |
| size   | num | 获取数量 | 非必要 | 默认返回20个结果 |
| skip   | num | 跳过数量 | 非必要 |           |

**json回复：**

根对象：

| 字段      | 类型          | 内容   | 备注                   |
|---------|-------------|------|----------------------|
| errcode | num         | 返回值  | 0 请求成功<br/>500 服务器错误 |
| errmsg  | str         | 错误信息 |                      |
| words   | list `word` |      |                      |

`word`对象：

| 字段          | 类型  | 内容   | 备注   |
|-------------|-----|------|------|
| wordId      | num | 单词id |      |
| word        | str | 单词   |      |
| translation | str | 翻译   | 可能为空 |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 200,
  "errmsg": null,
  "words": [
    {
      "wordId": 23806,
      "word": "all",
      "translation": "a. 所有的, 全部的, 一切的\nadv. 全部, 全然\npron. 全部\nn. 全部"
    }
  ]
}
```

</details>

## 获取复习过的单词

> https://loaclhost:5301/statistic/getReviewWord
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名    | 类型  | 内容   | 必要性 | 备注        |
|--------|-----|------|-----|-----------|
| userId | num | 用户id | 必要  |           |
| size   | num | 获取数量 | 非必要 | 默认返回20个结果 |
| skip   | num | 跳过数量 | 非必要 |           |

**json回复：**

根对象：

| 字段      | 类型          | 内容   | 备注                   |
|---------|-------------|------|----------------------|
| errcode | num         | 返回值  | 0 请求成功<br/>500 服务器错误 |
| errmsg  | str         | 错误信息 |                      |
| words   | list `word` |      |                      |

`word`对象：

| 字段          | 类型  | 内容   | 备注   |
|-------------|-----|------|------|
| wordId      | num | 单词id |      |
| word        | str | 单词   |      |
| translation | str | 翻译   | 可能为空 |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 200,
  "errmsg": null,
  "words": [
    {
      "wordId": 23806,
      "word": "all",
      "translation": "a. 所有的, 全部的, 一切的\nadv. 全部, 全然\npron. 全部\nn. 全部"
    }
  ]
}
```

</details>

## 获取今日学习过的单词

> https://loaclhost:5301/statistic/getTodayLearnWord
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名    | 类型  | 内容   | 必要性 | 备注        |
|--------|-----|------|-----|-----------|
| userId | num | 用户id | 必要  |           |
| size   | num | 获取数量 | 非必要 | 默认返回20个结果 |
| skip   | num | 跳过数量 | 非必要 |           |

**json回复：**

根对象：

| 字段      | 类型          | 内容   | 备注                   |
|---------|-------------|------|----------------------|
| errcode | num         | 返回值  | 0 请求成功<br/>500 服务器错误 |
| errmsg  | str         | 错误信息 |                      |
| words   | list `word` |      |                      |

`word`对象：

| 字段          | 类型  | 内容   | 备注   |
|-------------|-----|------|------|
| wordId      | num | 单词id |      |
| word        | str | 单词   |      |
| translation | str | 翻译   | 可能为空 |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 200,
  "errmsg": null,
  "words": [
    {
      "wordId": 23806,
      "word": "all",
      "translation": "a. 所有的, 全部的, 一切的\nadv. 全部, 全然\npron. 全部\nn. 全部"
    }
  ]
}
```

</details>

## 获取今日复习过的单词

> https://loaclhost:5301/statistic/getTodayReviewWord
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名    | 类型  | 内容   | 必要性 | 备注        |
|--------|-----|------|-----|-----------|
| userId | num | 用户id | 必要  |           |
| size   | num | 获取数量 | 非必要 | 默认返回20个结果 |
| skip   | num | 跳过数量 | 非必要 |           |

**json回复：**

根对象：

| 字段      | 类型          | 内容   | 备注                   |
|---------|-------------|------|----------------------|
| errcode | num         | 返回值  | 0 请求成功<br/>500 服务器错误 |
| errmsg  | str         | 错误信息 |                      |
| words   | list `word` |      |                      |

`word`对象：

| 字段          | 类型  | 内容   | 备注   |
|-------------|-----|------|------|
| wordId      | num | 单词id |      |
| word        | str | 单词   |      |
| translation | str | 翻译   | 可能为空 |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 200,
  "errmsg": null,
  "words": [
    {
      "wordId": 23806,
      "word": "all",
      "translation": "a. 所有的, 全部的, 一切的\nadv. 全部, 全然\npron. 全部\nn. 全部"
    }
  ]
}
```

</details>
