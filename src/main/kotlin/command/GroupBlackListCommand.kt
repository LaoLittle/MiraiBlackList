package org.laolittle.plugin.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.buildMessageChain
import org.laolittle.plugin.MiraiBlackList
import org.laolittle.plugin.bandata.BlackList.groupBlackList
import org.laolittle.plugin.utils.Tools.getUserOrNull

object GroupBlackListCommand : CompositeCommand(
    MiraiBlackList, "groupblacklist", "gbl","群黑名单",
    description = "群聊黑名单操作指令"
) {
    @OptIn(ExperimentalCommandDescriptors::class, ConsoleExperimentalApi::class)
    override val prefixOptional: Boolean = true

    @SubCommand("add", "ban", "block", "加黑", "加黑名单")
    suspend fun CommandSenderOnMessage<GroupMessageEvent>.add(blackId: String? = null) {
        if (!fromEvent.sender.isOperator()){
            sendMessage("你不是管理！")
            return
        }
        if (blackId == null) {
            sendMessage("请@或输入要加入黑名单的用户的id")
            return
        }
        val findUser = subject?.getUserOrNull(blackId)
        if (findUser == fromEvent.sender) {
            sendMessage("不能把自己加入黑名单！")
            return
        }else if ((findUser as? Member)?.isOperator() == true){
            sendMessage("对方也是管理哦")
            return
        }
        val id = runCatching { findUser?.id ?: blackId.toLong() }.onFailure {
            sendMessage("无法推断目标用户，请尝试使用QQ号！")
        }.getOrNull() ?: return
        val nameOrId = findUser?.nameCardOrNick ?: blackId
        val groupBl = groupBlackList[fromEvent.subject.id] ?: mutableListOf()
        if (groupBl.add(id)) {
            sendMessage("已将$nameOrId 加入黑名单")
        } else {
            sendMessage("$nameOrId 已在黑名单中")
            return
        }
        groupBlackList[fromEvent.subject.id] = groupBl
    }

    @SubCommand("rm", "remove", "unban", "解黑", "解黑名单")
    suspend fun CommandSenderOnMessage<GroupMessageEvent>.remove(unbanId: String? = null) {
        if (!fromEvent.sender.isOperator()){
            sendMessage("你不是管理！")
            return
        }
        if (unbanId == null) {
            sendMessage("请@或者输入要解除黑名单的用户的id")
            return
        }
        val findUser = subject?.getUserOrNull(unbanId)
        val nameOrId = findUser?.nameCardOrNick ?: unbanId
        val id = runCatching { findUser?.id ?: unbanId.toLong() }.onFailure {
            sendMessage("无法推断目标用户，请尝试使用QQ号！")
        }.getOrNull() ?: return
        val groupBl = groupBlackList[fromEvent.subject.id] ?: mutableListOf()

        if (groupBl.remove(id)) {
            sendMessage("已将$nameOrId 移出黑名单")
        } else {
            sendMessage("$nameOrId 不在黑名单中")
            return
        }
        groupBlackList[fromEvent.subject.id] = groupBl
    }

    @SubCommand("list", "ls", "列表")
    suspend fun CommandSenderOnMessage<GroupMessageEvent>.list() {
        if (!fromEvent.sender.isOperator()){
            sendMessage("你不是管理！")
            return
        }
        val groupBl = groupBlackList[fromEvent.subject.id] ?: mutableListOf()
        val banned = buildMessageChain {
            add("本群黑名单列表: \n")
            groupBl.forEach {
                add("$it ,")
            }
        }
        sendMessage(banned)
    }
}