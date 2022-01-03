package org.laolittle.plugin.command

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.message.data.buildMessageChain
import org.laolittle.plugin.MiraiBlackList
import org.laolittle.plugin.bandata.BlackList.blackList
import org.laolittle.plugin.utils.Tools.getUserOrNull

object BlackListCommand : CompositeCommand(
    MiraiBlackList, "blacklist", "bl",
    description = "黑名单操作指令"
) {
    @OptIn(ExperimentalCommandDescriptors::class, ConsoleExperimentalApi::class)
    override val prefixOptional: Boolean = true

    @SubCommand("add", "ban", "block", "加黑", "加黑名单")
    suspend fun CommandSender.add(blackId: String? = null, time: String? = null) {
        if (blackId == null) {
            sendMessage("请@或输入要加入黑名单的用户的id")
            return
        }
        val findUser = subject?.getUserOrNull(blackId)
        val id = runCatching { findUser?.id ?: blackId.toLong() }.onFailure {
            sendMessage("无法推断目标用户，请尝试使用QQ号！")
        }.getOrNull() ?: return
        val nameOrId = findUser?.nameCardOrNick ?: blackId

        if (blackList.contains(id)) {
            sendMessage("$nameOrId 已在黑名单中")
            return
        }

        when {
            time.isNullOrEmpty() -> {
                blackList[id] = 0
                sendMessage("已将$nameOrId 加入黑名单")
            }
            (time.replace(Regex("""\D"""), "").toLong() <= 0) || time.contains(Regex("""\D""")) -> {
                sendMessage("时间不正确！")
            }
            time.contains(Regex("(m|分钟|min)")) -> {
                sendMessage("已将$nameOrId 加入黑名单，将在${time.replace(Regex("(m|分钟|min)"), "分钟")}后解除")
            }
        }
    }

    @SubCommand("rm", "remove", "unban", "解黑", "解黑名单")
    suspend fun CommandSender.remove(unbanId: String? = null) {
        if (unbanId == null) {
            sendMessage("请@或者输入要解除黑名单的用户的id")
            return
        }
        val findUser = subject?.getUserOrNull(unbanId)
        val nameOrId = findUser?.nameCardOrNick ?: unbanId
        val id = runCatching { findUser?.id ?: unbanId.toLong() }.onFailure {
            sendMessage("无法推断目标用户，请尝试使用QQ号！")
        }.getOrNull() ?: return


        if (!blackList.contains(id)) {
            sendMessage("$nameOrId 不在黑名单中")
            return
        }

        blackList.remove(id)
        sendMessage("已将$nameOrId 移出黑名单")
    }

    @SubCommand("list", "ls", "列表")
    suspend fun CommandSender.list() {
        val banned = buildMessageChain {
            add("黑名单列表: \n")
            blackList.keys.forEach {
                add("$it ,")
            }
        }
        sendMessage(banned)
    }
}