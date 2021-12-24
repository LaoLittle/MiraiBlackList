package org.laolittle.plugin.BlackMan

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object BlackList : AutoSavePluginData("BlackList") {
    @ValueDescription("黑名单列表")
    val blackList by value(mutableSetOf<Long>())

    @ValueDescription("黑名单时间")
    val blackTime by value(mutableMapOf<Long, Long>())
}