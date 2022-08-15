# 单词相关api

- [获取搜索结果](#获取搜索结果)
- [获取单词详细信息](#获取单词详细信息)
- [获取基本学习数据](#获取基本学习数据)
- [获取学习数据](#获取学习数据)
- [获取复习数据](#获取复习数据)
- [添加单词到生词本](#添加单词到生词本)
- [添加学习记录](#添加学习记录)
- [更新学习记录](#更新学习记录)

---

## 获取搜索结果

> https://loaclhost:5301/word/getSearchResult
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名      | 类型   | 内容     | 必要性 | 备注          |
|----------|------|--------|-----|-------------|
| userId   | num  | 用户id   | 必要  |             |
| keyword  | str  | 搜索的关键词 | 必要  |             |
| getLemma | bool | 搜索原型   | 非必要 | 默认搜索单词的原型   |
| size     | num  | 请求单词数量 | 非必要 | 默认最多返回20个单词 |
| skip     | num  | 跳过数量   | 非必要 | 跳过多少个搜索结果   |

**json回复：**

根对象：

| 字段           | 类型          | 内容      | 备注  |
|--------------|-------------|---------|-----|
| errcode      | num         | 返回值     |     |
| errmsg       | str         | 错误信息    |     |
| lemmaSearch  | list `word` | 原型单词列表  |     |
| directSearch | list `word` | 直接搜索的结果 |     |

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
  "lemmaSearch": [
    {
      "wordId": 2689,
      "word": "abandon",
      "translation": "vt. 放弃, 抛弃, 遗弃, 使屈从, 沉溺, 放纵\nn. 放任, 无拘束, 狂热"
    }
  ],
  "directSearch": [
    {
      "wordId": 2694,
      "word": "abandoned",
      "translation": "a. 被抛弃的, 无约束的, 恣意放荡的"
    },
    {
      "wordId": 2703,
      "word": "abandonedly",
      "translation": "adv. abandoned的变形"
    },
    {
      "wordId": 2695,
      "word": "abandoned call",
      "translation": "[计] 未接通呼叫"
    }
  ]
}
```

</details>

## 获取单词详细信息

> https://loaclhost:5301/word/getWordDetail
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名    | 类型  | 内容   | 必要性 | 备注  |
|--------|-----|------|-----|-----|
| userId | num | 用户id | 必要  |     |
| wordId | num | 单词id | 必要  |     |

**json回复：**

根对象：

| 字段         | 类型              | 内容        | 备注  |
|------------|-----------------|-----------|-----|
| errcode    | num             | 返回值       |     |
| errmsg     | str             | 错误信息      |     |
| word       | obj             | 单词信息      |     |
| inNoteBook | bool            | 是否在用户的生词本 |     |
| bookList   | list `WordBook` | 直接搜索的结果   |     |

`word`对象：

| 字段          | 类型  | 内容                          | 备注               |
|-------------|-----|-----------------------------|------------------|
| wordId      | num | 单词id                        |                  |
| word        | str | 单词                          |                  |
| translation | str | 翻译                          | 可能为空             |
| phonetic    | str | 音标                          | 可能是美式也可能是英式，可能为空 |
| definition  | str | 单词释义(英文)                    |                  |
| collins     | int | 柯林斯星级                       | 0代表不存在           |
| bnc         | int | 英国国家语料库词频顺序                 |                  |
| frq         | int | 当代语料库词频顺序                   |                  |
| exchange    | str | 时态复数等变换，使用 "/" 分割不同项目，见后面表格 |                  |

`WordBook`对象：

| 字段          | 类型  | 内容   | 备注  |
|-------------|-----|------|-----|
| bookId      | num | 词书id |     |
| name        | str | 词书名字 |     |
| description | str | 描述   |     |
| total       | num | 单词总数 |     |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 0,
  "errmsg": null,
  "word": {
    "wordId": 36676,
    "word": "answer",
    "translation": "n. 答案, 回答, 回报, 答辩\nvt. 回答, 反驳, 适应, 响应, 符合\nvi. 回答, 答应, 负责, 符合, 成功\n[计] 用户问题及答案新闻组",
    "phonetic": "'ɑ:nsә",
    "definition": "n. a statement (either spoken or written) that is made to reply to a question or request or criticism or accusation\nn. the speech act of replying to a question\nn. the principal pleading by the defendant in response to plaintiff's complaint; in criminal law it consists of the defendant's plea of `guilty' or `not guilty' (or nolo contendere); in civil law it must contain denials of all allegations in the plaintiff's complaint that the defendant hopes to controvert and it can contain affirmative defenses or counterclaims\nn. a nonverbal reaction",
    "collins": 5,
    "bnc": 808,
    "frq": 767,
    "exchange": "s:answers/p:answered/d:answered/i:answering/3:answers"
  },
  "inNoteBook": false,
  "bookList": [
    {
      "bookId": 1,
      "name": "中考",
      "description": "初中课标词汇",
      "total": 1603
    },
    {
      "bookId": 2,
      "name": "高考",
      "description": "高考大纲词汇",
      "total": 3677
    },
    {
      "bookId": 9,
      "name": "牛津",
      "description": "牛津三千核心词汇",
      "total": 3461
    }
  ]
}
```

</details>

## 获取基本学习数据

> https://loaclhost:5301/word/getBasicLearningData
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名        | 类型  | 内容   | 必要性 | 备注  |
|------------|-----|------|-----|-----|
| userId     | num | 用户id | 必要  |     |
| wordBookId | num | 词书id | 必要  |     |

**json回复：**

根对象：

| 字段           | 类型  | 内容        | 备注  |
|--------------|-----|-----------|-----|
| errcode      | num | 返回值       |     |
| errmsg       | str | 错误信息      |     |
| needToLearn  | num | 需要学习的单词数量 |     |
| needToReview | num | 需要复习的单词数量 |     |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 0,
  "errmsg": null,
  "needToLearn": 1602,
  "needToReview": 0
}
```

</details>

## 获取学习数据

> https://loaclhost:5301/word/getLearningData
*请求方式：POST*

**url参数：**

Content-Type: application/json

| 参数名        | 类型   | 内容       | 必要性 | 备注      |
|------------|------|----------|-----|---------|
| userId     | num  | 用户id     | 必要  |         |
| wordBookId | num  | 词书id     | 必要  |         |
| size       | num  | 请求单词数量   | 必要  | 默认返回10个 |
| sample     | bool | 是否生成混淆列表 | 必要  | 默认生成    |

**json回复：**

根对象：

| 字段         | 类型          | 内容   | 备注  |
|------------|-------------|------|-----|
| errcode    | num         | 返回值  |     |
| errmsg     | str         | 错误信息 |     |
| wordList   | list `word` | 单词列表 |     |

`word`对象：

| 字段             | 类型            | 内容         | 备注               |
|----------------|---------------|------------|------------------|
| wordId         | num           | 单词id       |                  |
| word           | str           | 单词         |                  |
| translation    | str           | 翻译         | 可能为空             |
| phonetic       | str           | 音标         | 可能是美式也可能是英式，可能为空 |
| inNotebook     | bool          | 是否在用户的生词本  |                  |
| learningRecord | obj           | 之前未完成的学习记录 | 可能为空             |
| sampleList     | list `Sample` | 混淆列表       |                  |

`learningRecord`对象：

| 字段          | 类型   | 内容   | 备注  |
|-------------|------|------|-----|
| repeatTimes | num  | 重复次数 |     |
| learnTime   | time | 学习时间 |     |

`Sample`对象：

| 字段          | 类型  | 内容   | 备注   |
|-------------|-----|------|------|
| wordId      | num | 重复次数 |      |
| word        | str | 学习时间 |      |
| translation | str | 翻译   | 可能为空 |

<details>
<summary>查看响应示例：</summary>

```json
{
  "errcode": 200,
  "errmsg": null,
  "wordList": [
    {
      "wordId": 608,
      "word": "a",
      "translation": "第一个字母 A; 一个; 第一的\r\nart. [计] 累加器, 加法器, 地址, 振幅, 模拟, 区域, 面积, 汇编, 组件, 异步",
      "phonetic": "ei",
      "inNotebook": false,
      "learningRecord": null,
      "sampleList": [
        {
          "wordId": 359431,
          "word": "international",
          "translation": "a. 国际的\nn. 国别设定\n[计] 国别设定"
        },
        {
          "wordId": 612962,
          "word": "save",
          "translation": "n. 救球\nvt. 解救, 挽救, 储蓄, 保存, 节省, 保留\nvi. 挽救, 节省, 救球\nprep. 除...之外\n[计] 保存"
        },
        {
          "wordId": 707156,
          "word": "too",
          "translation": "adv. 也, 非常, 太"
        },
        {
          "wordId": 625936,
          "word": "September",
          "translation": "n. 九月"
        },
        {
          "wordId": 408854,
          "word": "lot",
          "translation": "n. 运气, 签, 抽签, 份额, 许多, 一堆\nvt. 划分\nvi. 抽签, 抓阄"
        }
      ]
    }
  ]
}
```

</details>
