package org.laolittle.plugin.command

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.message.data.buildMessageChain
import org.laolittle.plugin.MiraiBlackList
import org.laolittle.plugin.bandata.BlackList

object List : SimpleCommand(
    MiraiBlackList, "blacklist", "bls",
    description = "黑名单列表"
) {

    @Handler
    suspend fun CommandSender.handle(){
        val banned = buildMessageChain {
            add("黑名单列表: \n")
            BlackList.blackList.keys.forEach {
                add("$it ,")
            }
        }
        sendMessage(banned)
    }
}