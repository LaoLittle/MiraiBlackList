package org.laolittle.plugin.bandata

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object BlackList : AutoSavePluginData("BlackList") {
    @ValueDescription("黑名单列表")
    val blackList by value(mutableMapOf<Long, Long>())
}