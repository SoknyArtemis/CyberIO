package net.liplum.blocks.stream

import arc.graphics.Color
import arc.struct.OrderedSet
import arc.struct.Seq
import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.type.Liquid
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.meta.BlockGroup
import net.liplum.CalledBySync
import net.liplum.ClientOnly
import net.liplum.DebugOnly
import net.liplum.SendDataPack
import net.liplum.animations.Floating
import net.liplum.animations.anis.*
import net.liplum.api.drawStreamGraphic
import net.liplum.api.stream.*
import net.liplum.blocks.AniedBlock
import net.liplum.persistance.intSet
import net.liplum.utils.*

private typealias AniStateH = AniState<StreamHost, StreamHost.HostBuild>

open class StreamHost(name: String) : AniedBlock<StreamHost, StreamHost.HostBuild>(name) {
    @JvmField var maxConnection = 5
    @JvmField var liquidColorLerp = 0.5f
    @ClientOnly lateinit var BaseTR: TR
    @ClientOnly lateinit var LiquidTR: TR
    @ClientOnly lateinit var TopTR: TR
    @ClientOnly lateinit var NoPowerAni: AniStateH
    @ClientOnly lateinit var NormalAni: AniStateH
    @ClientOnly lateinit var NoPowerTR: TR
    @ClientOnly @JvmField var IconFloatingRange = 1f
    /**
     * 1 networkSpeed = 60 per seconds
     */
    @JvmField var networkSpeed = 1f
    @JvmField var SharedClientSeq: Seq<IStreamClient> = Seq(
        if (maxConnection == -1) 10 else maxConnection
    )

    init {
        update = true
        solid = true
        configurable = true
        outputsLiquid = false
        group = BlockGroup.liquids
        noUpdateDisabled = true
        hasLiquids = true
        canOverdrive = false
        sync = true
        config(Integer::class.java) { obj: HostBuild, clientPackedPos ->
            obj.setClient(clientPackedPos.toInt())
        }
        configClear<HostBuild> {
            it.clearClients()
        }
    }

    override fun load() {
        super.load()
        BaseTR = this.sub("base")
        LiquidTR = this.sub("liquid")
        TopTR = this.sub("top")
        NoPowerTR = this.inMod("rs-no-power-large")
    }

    override fun init() {
        super.init()
        IconFloatingRange = IconFloatingRange / 8f * size
    }

    override fun setBars() {
        super.setBars()
        DebugOnly {
            bars.addClientInfo<HostBuild>()
        }
    }

    open inner class HostBuild : AniedBuild(), IStreamHost {
        var clients = OrderedSet<Int>()
        @ClientOnly @JvmField var liquidFlow = 0f
        open fun checkClientsPos() {
            clients.removeAll { !it.sc().exists }
        }
        @ClientOnly @JvmField
        var floating: Floating = Floating(IconFloatingRange).randomXY()
        override fun getHostColor(): Color = liquids.current().color
        override fun updateTile() {
            // Check connection every second
            if (Time.time % 60f < 1) {
                checkClientsPos()
            }
            if (!consValid()) return
            SharedClientSeq.clear()
            for (pos in clients) {
                val client = pos.sc()
                if (client != null) {
                    SharedClientSeq.add(client)
                }
            }
            val liquid = liquids.current()
            var needPumped = networkSpeed.coerceAtMost(liquids.currentAmount())
            var per = needPumped / clients.size
            var resetClient = clients.size
            for (client in SharedClientSeq) {
                if (liquid.match(client.requirements)) {
                    val rest = streaming(client, liquid, per)
                    needPumped -= (per - rest)
                }
                resetClient--
                if (resetClient > 0) {
                    per = needPumped / resetClient
                }
            }
            liquids.remove(liquid, networkSpeed - needPumped)
        }

        override fun onProximityAdded() {
            super.onProximityAdded()
            resubscribeRequirementUpdated()
        }

        open fun onClientRequirementsUpdated(client: IStreamClient) {
        }

        open fun onClientsChanged() {
        }

        open fun resubscribeRequirementUpdated() {
            clients.forEach { pos ->
                pos.sc()?.let {
                    it.onRequirementUpdated += ::onClientRequirementsUpdated
                }
            }
        }

        override fun acceptLiquid(source: Building, liquid: Liquid): Boolean {
            return consValid() && liquids.current() === liquid || liquids.currentAmount() < 0.2f
        }
        @CalledBySync
        open fun setClient(pos: Int) {
            if (pos in clients) {
                pos.sc()?.let {
                    disconnectClient(it)
                    it.disconnect(this)
                }
            } else {
                pos.sc()?.let {
                    connectClient(it)
                    it.connect(this)
                }
            }
        }
        @CalledBySync
        open fun connectClient(client: IStreamClient) {
            if (clients.add(client.building.pos())) {
                onClientsChanged()
                client.onRequirementUpdated += ::onClientRequirementsUpdated
            }
        }
        @CalledBySync
        open fun disconnectClient(client: IStreamClient) {
            if (clients.remove(client.building.pos())) {
                onClientsChanged()
                client.onRequirementUpdated -= ::onClientRequirementsUpdated
            }
        }
        @CalledBySync
        open fun clearClients() {
            clients.forEach { pos ->
                pos.sc()?.let {
                    it.disconnect(this)
                    it.onRequirementUpdated -= ::onClientRequirementsUpdated
                }
            }
            clients.clear()
            onClientsChanged()
        }

        override fun onConfigureTileTapped(other: Building): Boolean {
            if (this === other) {
                deselect()
                configure(null)
                return false
            }
            val pos = other.pos()
            if (pos in clients) {
                if (maxConnection == 1) {
                    deselect()
                }
                pos.sc()?.let { disconnectSync(it) }
                return false
            }
            if (other is IStreamClient) {
                if (maxConnection == 1) {
                    deselect()
                }
                if (canHaveMoreClientConnection() &&
                    other.acceptConnection(this)
                ) {
                    connectSync(other)
                }
                return false
            }
            return true
        }

        override fun drawConfigure() {
            super.drawConfigure()
            this.drawStreamGraphic()
        }

        override fun drawSelect() {
            this.drawStreamGraphic()
        }

        override fun getBuilding(): Building = this
        override fun getTile(): Tile = tile
        override fun getBlock(): Block = this@StreamHost
        @SendDataPack
        override fun connectSync(client: IStreamClient) {
            if (client.building.pos() !in clients) {
                configure(client.building.pos())
            }
        }
        @SendDataPack
        override fun disconnectSync(client: IStreamClient) {
            if (client.building.pos() in clients) {
                configure(client.building.pos())
            }
        }

        override fun maxClientConnection() = maxConnection
        override fun connectedClients(): OrderedSet<Int> = clients
        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            clients = read.intSet()
        }

        override fun beforeDraw() {
            val d = G.D(0.1f * IconFloatingRange * delta())
            floating.move(d)
        }

        override fun write(write: Writes) {
            super.write(write)
            write.intSet(clients)
        }

        override fun fixedDraw() {
            BaseTR.DrawOn(this)
            Drawf.liquid(
                LiquidTR, x, y,
                liquids.total() / liquidCapacity,
                liquids.current().color,
                (rotation - 90).toFloat()
            )
            TopTR.DrawOn(this)
        }
    }

    override fun genAniState() {
        NoPowerAni = addAniState("NoPower") {
            SetAlpha(0.8f)
            NoPowerTR.DrawSize(
                it.x + it.floating.xOffset,
                it.y + it.floating.yOffset,
                1f / 7f * this@StreamHost.size
            )
        }
        NormalAni = addAniState("Normal")
    }

    override fun genAniConfig() {
        config {
            From(NoPowerAni) To NormalAni When {
                it.consValid()
            }

            From(NormalAni) To NoPowerAni When {
                !it.consValid()
            }
        }
    }
}