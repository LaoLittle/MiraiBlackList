package org.laolittle.plugin.utils

import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.nameCardOrNick

object Tools {
    fun Bot.getNameCardOrId(userId: Long): String {
        val user = getFriend(userId) ?: getStranger(userId)
        return user?.nameCardOrNick ?: userId.toString()
    }
}