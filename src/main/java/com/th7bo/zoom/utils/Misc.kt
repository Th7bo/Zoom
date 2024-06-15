package com.th7bo.zoom.utils

import net.minecraft.world.entity.player.Abilities
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*

object Misc {

    private val zoomedLevel: MutableMap<Player, Double> = mutableMapOf()

    fun resetFOV(p: Player) {
        val abilities = getAbilities(p)
        abilities.walkingSpeed = getScope(1.0)
        updateAbilities(p)
    }

    fun setFOV(p: Player, level: Double) {
        val abilities = getAbilities(p)
        abilities.walkingSpeed = getScope(level)
        updateAbilities(p)
    }

    private fun updateAbilities(p: Player) {
        (p as CraftPlayer).handle.onUpdateAbilities()
    }

    private fun getAbilities(p: Player) : Abilities {
        return (p as CraftPlayer).handle.abilities
    }

    private fun getScope(level: Double): Float {
        var level = level
        if (level < 0.5 || level > 10) {
            level = 0.5
        }
        return (1 / (20 / level - 10)).toFloat()
    }

    fun setZoom(p: Player, level: Double) {
        zoomedLevel[p] = level
    }

    fun stopZoom(p: Player) {
        zoomedLevel.remove(p)
    }

    fun isZoomed(p: Player): Boolean {
        return zoomedLevel.containsKey(p)
    }

    fun getZoomLevel(p: Player): Double? {
        return zoomedLevel[p]
    }

}