package net.liplum.mdt.animations.ganim

import arc.func.Cons
import arc.graphics.g2d.TextureRegion
import arc.util.Time
import mindustry.game.EventType
import net.liplum.annotations.Only
import net.liplum.mdt.ClientOnly
import net.liplum.annotations.Subscribe
import net.liplum.lib.utils.progress

typealias GlobalAnimationIndexer = GlobalAnimation.() -> TextureRegion

open class GlobalAnimation(
    val duration: Float,
    val setTR: Cons<TextureRegion>,
) : IGlobalAnimation {
    lateinit var frames: Array<TextureRegion>
    var frameIndexer: GlobalAnimationIndexer = loopIndexer
    override val canUpdate: Boolean
        get() = CanPlay
    var lastTR: TextureRegion? = null
    protected fun getCurTR(): TextureRegion {
        return frameIndexer()
    }
    @ClientOnly
    override fun update() {
        if (canUpdate) {
            val curTR = getCurTR()
            if (curTR != lastTR) {
                lastTR = curTR
                this.setTR.get(curTR)
            }
        }
    }

    companion object {
        val loopIndexer: GlobalAnimationIndexer = {
            val frames = frames
            val progress = Time.globalTime % duration / duration //percent
            frames.progress(progress)
        }
        // TODO: that's too random.
        val randomSelectIndexr: GlobalAnimationIndexer = {
            frames.random()
        }
        var CanPlay = false
        val updateTasks = HashSet<IGlobalAnimation>()
        @ClientOnly
        @JvmStatic
        @Subscribe(EventType.Trigger.update, Only.client)
        fun animate() {
            if (CanPlay) {
                for (task in updateTasks)
                    task.update()
            }
        }

        fun GlobalAnimation.useRandom(): GlobalAnimation {
            this.frameIndexer = randomSelectIndexr
            return this
        }

        fun GlobalAnimation.register(): GlobalAnimation {
            updateTasks.add(this)
            return this
        }
        @ClientOnly
        fun globalPlay() {
            if (CanPlay) {
                for (task in updateTasks)
                    task.update()
            }
        }
    }
}