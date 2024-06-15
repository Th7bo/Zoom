package com.th7bo.zoom.listener

import com.th7bo.zoom.Zoom.Companion.instance
import com.th7bo.zoom.utils.Misc
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerToggleSneakEvent

class SneakListener : Listener {

    init {
        Bukkit.getServer().pluginManager.registerEvents(this, instance)
    }

    @EventHandler
    fun sneakToggle(e: PlayerToggleSneakEvent) {
        val p = e.player
        Misc.resetFOV(p)
        Misc.stopZoom(p)
    }

}