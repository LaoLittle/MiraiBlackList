package org.laolittle.plugin

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.utils.info
import org.laolittle.plugin.bandata.BlackList
import org.laolittle.plugin.command.Add
import org.laolittle.plugin.command.Remove

object MiraiBlackList : KotlinPlugin(
    JvmPluginDescription(
        id = "org.laolittle.plugin.MiraiBlackList",
        name = "MiraiBlackList",
        version = "1.0",
    ) {
        author("LaoLittle")
        info("""全局黑名单""")
    }
) {
    override fun onEnable() {
        BlackList.reload()
        logger.info { "黑名单数据已加载" }
        Add.register()
        Remove.register()
        GlobalEventChannel.subscribeAlways<MessageEvent>(
            priority = EventPriority.HIGHEST
        ) {
            if (BlackList.blackList.contains(sender.id)) intercept()
        }
    }
}