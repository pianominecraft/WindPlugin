package com.pianominecraft.windplugin

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.lang.Exception
import java.util.regex.Pattern

class WindPlugin : JavaPlugin(), Listener {

    val teamFile = File(dataFolder, "youtubers.txt")

    override fun onEnable() {

        repeat {
            list.forEach {
                Bukkit.getPlayer(it)?.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 2, 1, false, false))
            }
        }

        Bukkit.getPluginManager().registerEvents(this, this)
        if (!dataFolder.exists()) dataFolder.mkdir()
        if (!teamFile.exists()) teamFile.createNewFile()
        BufferedReader(FileReader(teamFile)).use { list = it.readLines() }
        Bukkit.getLogger().info("[WindPlugin] has been enabled!")
    }

    override fun onCommand(s: CommandSender, c: Command, l: String, a: Array<out String>): Boolean {

        if (c.name.equals("youtuberReload", ignoreCase = true)) {
            if (s.isOp) {
                BufferedReader(FileReader(teamFile)).use { list = it.readLines() }
            }
        }

        return false
    }

    @EventHandler
    fun onChat(e: AsyncPlayerChatEvent) {
        e.isCancelled = true
        if (e.player.name in list) {
            list.forEach { p ->
                Bukkit.getPlayer(p)?.sendMessage(text("%yellow%[Team Chating] %white%<${e.player.name}> ") + e.message)
            }
        } else {
            Bukkit.getOnlinePlayers().forEach { p ->
                if (p.name !in list) {
                    p.sendMessage(text("%yellow%[Team Chating] %white%<%red%${e.player.name}%white%> ") + e.message)
                }
            }
        }
    }

    private fun repeat(delay: Long = 1, task: () -> Unit) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, task, 1, delay)
    }

}

var list = listOf("")

fun text(string: String) : String {
    var s = string
    val rgb = Pattern.compile("#[0-9a-f]{6}").matcher(string)
    while (rgb.find()) {
        try {
            s = s.replaceFirst(rgb.group(), net.md_5.bungee.api.ChatColor.of(rgb.group()).toString())
        } catch (e: Exception) {
        }
    }
    val color = Pattern.compile("%[a-zA-Z_]*%").matcher(string)
    while (color.find()) {
        try {
            s = s.replaceFirst(
                color.group(),
                net.md_5.bungee.api.ChatColor.of(color.group().replace("%", "")).toString()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return s
}