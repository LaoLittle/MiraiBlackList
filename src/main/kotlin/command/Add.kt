package org.laolittle.plugin.command

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import org.laolittle.plugin.MiraiBlackList
import org.laolittle.plugin.bandata.BlackList
import org.laolittle.plugin.utils.Tools.getNameCardOrId

@OptIn(ConsoleExperimentalApi::class, ExperimentalCommandDescriptors::class)
object Add : SimpleCommand(
    MiraiBlackList, "add", "ban" , "加黑", "加黑名单",
    description = "添加某人到黑名单",
) {

    override val prefixOptional: Boolean = true

    @Handler
    suspend fun CommandSender.handle(blackId: Long? = null, time: String? = null){
        if (blackId == null) {
            sendMessage("请@或输入要加入黑名单的用户的id")
            return
        }
        val nameOrId = bot?.getNameCardOrId(blackId) ?: blackId

        if (BlackList.blackList.contains(blackId)){
            sendMessage("$nameOrId 已在黑名单中")
            return
        }
        when{
            time.isNullOrEmpty() -> {
                BlackList.blackList[blackId] = 0
                sendMessage("已将$nameOrId 加入黑名单")
            }
            time.contains(Regex("(m|分钟|min)")) -> {
                sendMessage("已将$nameOrId 加入黑名单，将在${time.replace(Regex("(m|分钟|min)"), "分钟")}后解除")
            }
        }
    }
}