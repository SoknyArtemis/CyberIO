package net.liplum.registries

import mindustry.graphics.CacheLayer
import net.liplum.annotations.SubscribeEvent
import net.liplum.events.CioLoadContentEvent
import net.liplum.lib.shaders.SD

object CioCLs {
    private var registered = false
    private var allCacheLayers = ArrayList<CacheLayer>()
    var cyberion = CacheLayer.ShaderLayer(SD.Cyberion).register()
    @JvmStatic
    @SubscribeEvent(CioLoadContentEvent::class)
    fun load() {
        allCacheLayers = ArrayList()
        registerAll()
    }
    @JvmStatic
    fun registerAll() {
        if (!registered) {
            CacheLayer.add(*allCacheLayers.toTypedArray())
            registered = true
        }
    }

    fun <T> T.register(): T where T : CacheLayer {
        allCacheLayers.add(this)
        return this
    }
}
