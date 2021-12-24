package org.laolittle.plugin.command

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import org.laolittle.plugin.MiraiBlackList
import org.laolittle.plugin.bandata.BlackList
import org.laolittle.plugin.utils.Tools.getNameCardOrId

@OptIn(ExperimentalCommandDescriptors::class, ConsoleExperimentalApi::class)
object Remove : SimpleCommand(
    MiraiBlackList, "rm", "remove", "unban", "解黑", "解黑名单",
    description = "解除黑名单"
){
    override val prefixOptional: Boolean = true

    @Handler
    suspend fun CommandSender.handle(unbanId: Long? = null){
        if (unbanId == null){
            sendMessage("请@或者输入要解除黑名单的用户的id")
            return
        }
        val nameOrId = bot?.getNameCardOrId(unbanId) ?: unbanId

        BlackList.blackList.remove(unbanId)
        sendMessage("已将$nameOrId 移出黑名单")
    }
}