package org.laolittle.plugin

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.events.NudgeEvent
import net.mamoe.mirai.event.events.UserEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.utils.info
import net.mamoe.mirai.utils.verbose
import org.laolittle.plugin.bandata.BlackList
import org.laolittle.plugin.command.BlackListCommand
import org.laolittle.plugin.command.GroupBlackListCommand

object MiraiBlackList : KotlinPlugin(
    JvmPluginDescription(
        id = "org.laolittle.plugin.MiraiBlackList",
        name = "MiraiBlackList",
        version = "1.1.1",
    ) {
        author("LaoLittle")
        info("""全局黑名单""")
    }
) {
    override fun onEnable() {
        BlackList.reload()
        logger.info { "黑名单数据已加载" }
        GroupBlackListCommand.register()
        BlackListCommand.register()
        val priority = EventPriority.HIGHEST

        globalEventChannel().subscribeAlways<MessageEvent>(
            priority = priority
        ) {
            if (BlackList.blackList.contains(sender.id)) {
                intercept()
                logger.verbose { "已拦截${sender.nameCardOrNick}(${sender.id})的消息，黑名单类型为：全局黑名单" }
            }
        }

        globalEventChannel().subscribeAlways<GroupMessageEvent>(
            priority = priority
        ) {
            if (BlackList.groupBlackList[subject.id]?.contains(sender.id) == true) {
                intercept()
                logger.verbose { "已拦截${sender.nameCardOrNick}(${sender.id})的消息，黑名单类型为：${subject.name}(${subject.id})的局部黑名单" }
            }
        }

        globalEventChannel().subscribeAlways<NudgeEvent>(
            priority = priority
        ) {
            val group = subject
            if (group is Group && BlackList.groupBlackList[group.id]?.contains(from.id) == true) intercept()
        }

        globalEventChannel().subscribeAlways<NudgeEvent>(
            priority = priority
        ) {
            if (BlackList.blackList.contains(from.id)) intercept()
        }

        globalEventChannel().subscribeAlways<UserEvent>(
            priority = priority
        ) {
            if (BlackList.blackList.contains(user.id)) intercept()
        }
    }
}