package icu.ketal.utils

import icu.ketal.data.OFMatrix
import icu.ketal.utils.StatUtils.inverseNormalCumulative
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sign
import icu.ketal.plugins.word.UpdateLearningRecordReq.LearningRecord as record

//region Description
// SM-5算法
// 计算下一个最优间隔的同时更新OF矩阵，从而单词在学习的时候不是一个个体，而是

// 用于生成最佳区间的随机散布 NOI--near-optimal intervals
// -------------------------------------------------------------
// 优点1: 通过一些差异值来加速OF矩阵优化过程
// 优点2: 消除复习的块状问题，将同一时期学习的内容适当分散进行复习
// 公式: NOI=PI+(OI-PI)*(1+m) m∈(-0.5, 0.5)
// m需满足（设概率密度函数为f(x)）：
//      (0, 0.5)内的概率为0.5，即 ∫[0, 0.5]f(x)dx=0.5
//      m=0的概率为m=0.5的概率的100倍 即 f(0)/f(0.5)=100
//      假设概率密度函数为 f(x)=a*exp(-b*x)
// -------------------------------------------------------------
// Piotr Wozniak求得 a=0.047; b=0.092;
// 从0到m的积分记为概率p，对于每一个p都有一个对应的m存在，p∈(0, 0.5)
// 生成一个(0, 1)之间的随机数，减去0.5得p，则|p|∈(0, 0.5)，而p的符号可以控制m的符号
// 则 ∫[0, m]f(x)dx=|p| => ∫[0, m]d( a*exp(-b*x) / (-b) )=|p| => m=-1/b*ln(1-b/a*|p|))
//
// const createNOI = (PI, OI) => {
//     let a = 0.047
//     let b = 0.092
//     let randNum = Math.random()
//     let p = randNum - 0.5
//     console.log('random p', p)
//     let m = -1 / b * (Math.log((1 - b / a * Math.abs(p))))
//     m = m * Math.sign(p)
//     console.log('random m', m)
//     let NOI = PI + (OI - PI) * (1 + m)
//     NOI = Math.round(NOI)
//     return NOI
// }

// -------------------------------------------------------------
// 由于作者给出的参数带入是有误的，采用类正态分布实现分布函数
// 原型(标准正态分布)：f(x) = 1/(√(2π)*Ω) * e(-x^2/(2Ω^2))
// 简化：f(x) = a*e^(-b*x^2)
// f(0) = 100*f(0.5) 可求得 b = -18.420680743952367
// ∫[0, 0.5]f(x)dx = 0.5 可求得 a = 2.4273047133848933
// 积分计算器网址: https://zh.numberempire.com/definiteintegralcalculator.php
// 画函数图像网址：https://www.desmos.com/calculator?lang=zh-CN
// 这里使用能解正态分布分位数的库进行运算
// f(0) = 100*f(0.5) 按正态分布算，可求得 std=0.1647525572455652
// X ~ N(0,0.1647525572455652) 从0~0.5的累计分布值为0.4987967402705885
// 故若要满足∫[0, 0.5]f(x)dx = 0.5，要在前面再乘上
// JStat库的jStat.normal.inv( p, mean, std )可以求出N(mean,std)分布从负无穷开始累计分布为p的分位点
// 因此思路转变为，首先随机获取[0, 1)的数r， r-0.5得到[-0.5, 0.5)的数m，(m*0.4987967402705885/0.5+0.5)得到累计值
// 即jStat.normal.inv(abs(m*0.4987967402705885/0.5)+0.5, 0, 0.1647525572455652) 可得到分位点
//endregion

object `Sm-5` {

    private fun createNOI(PI: Int, OI: Float): Int {
        logger.info("PI: $PI, OI: $OI")
        val std = 0.1647525572455652
        val randNum = Math.random()
        logger.info("randNum: $randNum")
        val p = abs((randNum - 0.5) * 0.4987967402705885 / 0.5) + 0.5
        logger.info("random p: $p")
        val invCdf = inverseNormalCumulative(p, 0.0, std)
        val m = invCdf * (randNum - 0.5).sign
        logger.info("random m: $m")
        return (PI + (OI - PI) * (1 + m)).roundToInt()
    }

    // 计算新的OF矩阵对应项
// 输入：
//      last_i - 用于相关项目的最后(上一个)间隔(原文描述为the last interval used for the item in question)
//      q - 重复响应的质量
//      used_OF - 用于计算相关项目的最后一个间隔时使用的最佳因子
//      old_OF - 与项目的相关重复次数和电子因子相对应的 OF 条目的前一个值
//      fraction - 属于确定修改速率的范围 (0，1) 的数字 (OF矩阵的变化越快)
// 输出：
//      new_OF - 考虑的 OF 矩阵条目的新计算值
// 局部变量：
//      modifier - 确定 OF 值将增加或减少多少次的数字
//      mod5 - 在 q=5 的情况下为修饰符建议的值
//      mod2 - 在 q=2 的情况下为修饰符建议的值
    private fun calculateNewOF(last_i: Int, q: Int, used_OF: Float, old_OF: Float, fraction: Float = 0.8f): Float {
        var mod5 = (last_i + 1f) / last_i
        if (mod5 < 1.05) mod5 = 1.05f
        var mod2 = (last_i - 1f) / last_i
        if (mod2 > 0.75) mod2 = 0.75f
        var modifier = if (q > 4) {
            1 + (mod5 - 1) * (q - 4)
        } else {
            1 - (1 - mod2) / 2 * (4 - q)
        }
        if (modifier < 0.05) modifier = 0.05f
        var new_OF = used_OF * modifier
        if (q > 4) if (new_OF < old_OF) new_OF = old_OF
        if (q < 4) if (new_OF > old_OF) new_OF = old_OF
        new_OF = new_OF * fraction + old_OF * (1 - fraction)
        if (new_OF < 1.2) new_OF = 1.2f
        return new_OF
    }

    fun sm_5(OF: OFMatrix, record: record): Pair<OFMatrix, record> {
        var (word_id, _EF, quality, last_NOI, next_n, last_l, _, master) = record
        if (master) return OF to record
        val now = Clock.System.now.toInstant(timeZone)
        var last_i = (now - last_l).inWholeDays.toInt()

        val EF = "%.1f".format(
            max(1.3, min(2.8, _EF.toFloat() + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02))))
        )

        // 更改矩阵对应项，这里认为若实际间隔时间超出所需间隔时间的1.5倍
        // 则视为极大异常值，规整为1.5倍，且不更改矩阵
        var used_OF = OF[EF]!![next_n - 1]
        if (0f == used_OF) used_OF = 1.2f
        next_n++
        if (0f == OF[EF]!![next_n - 1]) OF[EF]!![next_n - 1] = 1.2f
        if (last_i <= 1.5 * last_NOI) {
            val old_OF = OF[EF]!![next_n - 1]
            val new_OF = calculateNewOF(last_i, quality, used_OF, old_OF)
            // console.log('new_OF of', 'OF[', EF, '][', n - 1, ']:', new_OF)
            OF[EF]!![next_n - 1] = new_OF
        } else {
            // console.log('last_i', last_i, 'is longer than 1.5 expected interval :', last_NOI)
            last_i = (last_NOI * 1.5).roundToInt()
        }

        // 计算最优间隔时长并进行指定分布的随机分散
        // 同时计算下次需要复习的时间
        val NOI = if (quality < 2) {
            next_n = 0
            1
        } else if (quality < 3) {
            next_n = 0
            (OF[EF]!![0]).roundToInt()
        } else {
            val interval = if (next_n == 1) 5f else OF[EF]!![next_n - 1] * last_i
            // 若下个最优间隔时间大于100天，则将单词标记为已掌握
            if (interval > 100) master = true
            logger.info("next optimal interval is $interval")
            var NOI = createNOI(last_i, interval)
            if (NOI > 100 && !master) NOI = 100
            if (NOI < 0 && !master) NOI = 1
            NOI
        }

        last_l = Clock.System.now()
        val next_l = last_l.plus(NOI, DateTimeUnit.DAY, timeZone)
        return OF to record(
            word_id, EF, quality, NOI, next_n, last_l, next_l, master
        )
    }
}
