package org.laolittle.plugin.command

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.contact.nameCardOrNick
import org.laolittle.plugin.BlackMan.BlackList
import org.laolittle.plugin.MiraiBlackList

@OptIn(ConsoleExperimentalApi::class, ExperimentalCommandDescriptors::class)
object Add : SimpleCommand(
    MiraiBlackList, "add", "ban" , "加黑", "加黑名单",
    description = "添加某人到黑名单",
) {

    override val prefixOptional: Boolean = true

    @Handler
    suspend fun CommandSender.handle(blackUser: User? = null, time: String? = null){
        if (blackUser == null) {
            sendMessage("请@或输入要加入黑名单的用户的id")
            return
        }
        when{
            time.isNullOrEmpty() -> {
                BlackList.blackList.add(blackUser.id)
                sendMessage("已将${blackUser.nameCardOrNick} 加入黑名单")
            }
            time.contains(Regex("(m|分钟|min)")) -> {
                BlackList.blackTime[blackUser.id] = time.replace(Regex("(m|分钟|min)"), "").toLong() * 60 * 1000 * 1000
                sendMessage("已将${blackUser.nameCardOrNick} 加入黑名单，将在${time.replace(Regex("(m|分钟|min)"), "分钟")}后解除")
            }
        }
    }
}