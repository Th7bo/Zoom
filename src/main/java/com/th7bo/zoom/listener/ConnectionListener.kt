package com.th7bo.zoom.listener

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import com.th7bo.zoom.Zoom.Companion.instance
import com.th7bo.zoom.utils.Misc
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class ConnectionListener : Listener {

    @EventHandler
    fun removePlayer(e: PlayerQuitEvent) {
        Misc.resetFOV(e.player)
        Misc.stopZoom(e.player)
        val channel = (e.player as CraftPlayer).handle.connection.connection.channel
        channel.eventLoop().submit<Any?> {
            channel.pipeline().remove(e.player.name)
            null
        }
    }
    @EventHandler
    fun injectPlayer(e: PlayerJoinEvent) {
        Misc.resetFOV(e.player)
        Misc.stopZoom(e.player)
        val channelDuplexHandler: ChannelDuplexHandler = object : ChannelDuplexHandler() {
            @Throws(Exception::class)
            override fun channelRead(channelHandlerContext: ChannelHandlerContext, packet: Any) {
                if (handleItemSwitch(e.player, packet)) return
                if (packet is ServerboundSeenAdvancementsPacket && e.player.inventory.heldItemSlot == 0) {
                    startZoom(e.player)
                    return
                }
                super.channelRead(channelHandlerContext, packet)
            }

            @Throws(Exception::class)
            override fun write(
                channelHandlerContext: ChannelHandlerContext,
                packet: Any,
                channelPromise: ChannelPromise
            ) {
                super.write(channelHandlerContext, packet, channelPromise)
            }
        }
        val pipeline = (e.player as CraftPlayer).handle.connection.connection.channel.pipeline()
        pipeline.addBefore("packet_handler", e.player.name, channelDuplexHandler)
    }

    fun handleItemSwitch(p: Player, packet: Any) : Boolean {
        if (packet is ServerboundSetCarriedItemPacket) {
            if (Misc.getZoomLevel(p) == null) return false
            if (0 == packet.slot) return false
            if (p.inventory.heldItemSlot != 0) return false
            val slot = packet.slot
            val add = updateZoom(p, slot)
            var zoom = Misc.getZoomLevel(p) ?: 1.0
            if (add) {
                println("Adding")
                zoom += 0.5
            } else {
                println("Subtracting")
                zoom -= 0.5
            }
            if (zoom < 0.5) {
                zoom = 0.5
            }
            if (zoom > 10.0) {
                zoom = 10.0
            }
            p.sendActionBar("Zoom level: $zoom")
            Misc.setZoom(p, zoom)
            Misc.setFOV(p, zoom)
            p.inventory.heldItemSlot = 0
            return true
        }
        return false
    }

    private fun updateZoom(p: Player, new: Int): Boolean {
        return (new >= 5)

    }

    fun startZoom(p: Player) {
        object : BukkitRunnable() {
            override fun run() {
                Misc.setZoom(p, 1.0)
                p.closeInventory()
            }
        }.runTask(instance)
    }

    init {
        Bukkit.getServer().pluginManager.registerEvents(this, instance)
    }

}