package com.th7bo.zoom

import com.th7bo.zoom.listener.ConnectionListener
import com.th7bo.zoom.listener.SneakListener
import org.bukkit.plugin.java.JavaPlugin

class Zoom : JavaPlugin() {
    override fun onEnable() {
        instance = this
        ConnectionListener()
        SneakListener()
        // Plugin startup logic
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    companion object {
        @JvmStatic
        lateinit var instance: Zoom
            private set
    }
}
