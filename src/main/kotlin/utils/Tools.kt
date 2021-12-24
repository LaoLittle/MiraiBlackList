package org.laolittle.plugin.utils

import net.mamoe.mirai.console.internal.fuzzySearchMember
import net.mamoe.mirai.console.internal.toDecimalPlace
import net.mamoe.mirai.console.internal.truncate
import net.mamoe.mirai.contact.*

object Tools {

    suspend fun Contact.getUserOrNull(msg: String): User? {
        val noneAt = msg.replace("@", "")
        if (noneAt.isBlank()) {
            return null
        }
        return if (noneAt.contains(Regex("""\D"""))) {
            when (this) {
                is Group -> this.findMemberOrNull(noneAt)
                else -> null
            }
        } else {
            when (this) {
                is Group -> this[noneAt.toLong()]
                else -> bot.getFriend(noneAt.toLong()) ?: bot.getStranger(noneAt.toLong())
            }
        }
    }

    /**
     * 从一个群中模糊搜索昵称是[nameCard]的群员
     * @param nameCard 群员昵称
     * @return Member if only one exist or null otherwise
     * */
    private suspend fun Group.findMemberOrNull(nameCard: String): Member? {
        this.members.singleOrNull { it.nameCardOrNick.contains(nameCard) }?.let { return it }
        this.members.singleOrNull { it.nameCardOrNick.contains(nameCard, ignoreCase = true) }?.let { return it }

        val candidates = this.fuzzySearchMember(nameCard)
        candidates.singleOrNull()?.let {
            if (it.second == 1.0) return it.first // single match
        }
        if (candidates.isNotEmpty()) {
            var index = 1
            sendMessage(
                "无法找到成员 $nameCard。 多个成员满足搜索结果或匹配度不足: \n\n" +
                        candidates.joinToString("\n", limit = 6) {
                            val percentage = (it.second * 100).toDecimalPlace(0)
                            "#${index++}(${percentage}%)${it.first.nameCardOrNick.truncate(10)}(${it.first.id})" // #1 15.4%
                        }
            )
        }
        return null
    }
}