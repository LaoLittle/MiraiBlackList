package org.laolittle.plugin.command

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.contact.nameCardOrNick
import org.laolittle.plugin.BlackMan.BlackList
import org.laolittle.plugin.MiraiBlackList

@OptIn(ExperimentalCommandDescriptors::class, ConsoleExperimentalApi::class)
object Remove : SimpleCommand(
    MiraiBlackList, "rm", "remove", "unban", "解黑", "解黑名单",
    description = "解除黑名单"
){
    override val prefixOptional: Boolean = true

    @Handler
    suspend fun CommandSender.handle(whiteUser: User? = null){
        if (whiteUser == null){
            sendMessage("请@或者输入要解除黑名单的用户的id")
            return
        }
        BlackList.blackList.remove(whiteUser.id)
        sendMessage("已将${whiteUser.nameCardOrNick} 移出黑名单")
    }
}