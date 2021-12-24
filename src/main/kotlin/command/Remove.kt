package org.laolittle.plugin.command

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.nameCardOrNick
import org.laolittle.plugin.MiraiBlackList
import org.laolittle.plugin.bandata.BlackList
import org.laolittle.plugin.utils.Tools.getUserOrNull

@OptIn(ExperimentalCommandDescriptors::class, ConsoleExperimentalApi::class)
object Remove : SimpleCommand(
    MiraiBlackList, "rm", "remove", "unban", "解黑", "解黑名单",
    description = "解除黑名单"
) {
    override val prefixOptional: Boolean = true

    @Handler
    suspend fun CommandSender.handle(unbanId: String? = null) {
        if (unbanId == null) {
            sendMessage("请@或者输入要解除黑名单的用户的id")
            return
        }
        val findUser = if (unbanId.contains(Regex("""\D"""))) subject?.getUserOrNull(unbanId)
        else bot?.getFriend(unbanId.toLong()) ?: bot?.getStranger(unbanId.toLong())
        val nameOrId = findUser?.nameCardOrNick ?: unbanId
        val id = runCatching { findUser?.id ?: unbanId.toLong() }.onFailure {
            sendMessage("无法推断目标用户，请尝试使用QQ号！")
        }.getOrNull() ?: return


        if (!BlackList.blackList.contains(id)) {
            sendMessage("$nameOrId 不在黑名单中")
            return
        }

        BlackList.blackList.remove(id)
        sendMessage("已将$nameOrId 移出黑名单")
    }
}