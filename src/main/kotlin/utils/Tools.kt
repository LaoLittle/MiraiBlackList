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

    private suspend fun Group.findMemberOrNull(idOrCard: String): Member? {
        idOrCard.toLongOrNull()?.let { get(it) }?.let { return it }
        this.members.singleOrNull { it.nameCardOrNick.contains(idOrCard) }?.let { return it }
        this.members.singleOrNull { it.nameCardOrNick.contains(idOrCard, ignoreCase = true) }?.let { return it }

        val candidates = this.fuzzySearchMember(idOrCard)
        candidates.singleOrNull()?.let {
            if (it.second == 1.0) return it.first // single match
        }
        if (candidates.isNotEmpty()) {
            var index = 1
            sendMessage(
                "无法找到成员 $idOrCard。 多个成员满足搜索结果或匹配度不足: \n\n" +
                        candidates.joinToString("\n", limit = 6) {
                            val percentage = (it.second * 100).toDecimalPlace(0)
                            "#${index++}(${percentage}%)${it.first.nameCardOrNick.truncate(10)}(${it.first.id})" // #1 15.4%
                        }
            )
        }
        return null
    }
}