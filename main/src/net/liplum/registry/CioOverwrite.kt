package net.liplum.registry

import mindustry.Vars
import mindustry.content.Blocks
import mindustry.content.UnitTypes
import mindustry.world.blocks.heat.HeatProducer
import mindustry.world.blocks.payloads.*
import mindustry.world.blocks.power.ConsumeGenerator.ConsumeGeneratorBuild
import mindustry.world.blocks.power.PowerGenerator
import mindustry.world.blocks.sandbox.*
import mindustry.world.meta.BuildVisibility
import net.liplum.DebugOnly
import net.liplum.ExperimentalOnly
import net.liplum.annotations.DependOn
import net.liplum.mdt.render.G
import net.liplum.render.DrawBuild
import net.liplum.render.regionSection

object CioOverwrite {
    @DependOn
    fun debugOnly() {
        DebugOnly {
            (Blocks.powerSource as PowerSource).apply {
                buildVisibility = BuildVisibility.shown
                health = Int.MAX_VALUE
            }
            (Blocks.itemSource as ItemSource).apply {
                buildVisibility = BuildVisibility.shown
                health = Int.MAX_VALUE
            }
            (Blocks.liquidSource as LiquidSource).apply {
                buildVisibility = BuildVisibility.shown
                health = Int.MAX_VALUE
            }
            (Blocks.payloadSource as PayloadSource).apply {
                buildVisibility = BuildVisibility.shown
                health = Int.MAX_VALUE
            }
            (Blocks.heatSource as HeatProducer).apply {
                buildVisibility = BuildVisibility.shown
                health = Int.MAX_VALUE
            }
            (Blocks.powerVoid as PowerVoid).apply {
                buildVisibility = BuildVisibility.shown
            }
            (Blocks.itemVoid as ItemVoid).apply {
                buildVisibility = BuildVisibility.shown
            }
            (Blocks.liquidVoid as LiquidVoid).apply {
                buildVisibility = BuildVisibility.shown
            }
            (Blocks.payloadVoid as PayloadVoid).apply {
                buildVisibility = BuildVisibility.shown
            }
            val blockSize = 10f
            (Blocks.payloadConveyor as PayloadConveyor).payloadLimit = blockSize
            (Blocks.payloadLoader as PayloadLoader).maxBlockSize = blockSize.toInt()
            (Blocks.payloadRouter as PayloadRouter).payloadLimit = blockSize
            (Blocks.payloadUnloader as PayloadUnloader).maxBlockSize = blockSize.toInt()
            (Blocks.payloadPropulsionTower as PayloadMassDriver).maxPayloadSize = blockSize
            (Blocks.payloadMassDriver as PayloadMassDriver).maxPayloadSize = blockSize
            (Blocks.reinforcedPayloadConveyor as PayloadConveyor).payloadLimit = blockSize
            (Blocks.reinforcedPayloadRouter as PayloadConveyor).payloadLimit = blockSize
            UnitTypes.evoke.payloadCapacity = blockSize * blockSize * Vars.tilePayload
            UnitTypes.incite.payloadCapacity = blockSize * blockSize * Vars.tilePayload
            UnitTypes.emanate.payloadCapacity = blockSize * blockSize * Vars.tilePayload
            /*val coreBlock = Blocks.coreShard as CoreBlock
            coreBlock.unitType = CioUnitType.holoFighter
            coreBlock.solid = false*/
            (Blocks.chemicalCombustionChamber as PowerGenerator).apply {
                drawer = DrawBuild<ConsumeGeneratorBuild> {
                    regionSection("-mid") {
                        progress = { G.sin }
                        moveRotation = 360f
                    }
                }
            }
        }
        ExperimentalOnly {
            Blocks.conveyor.sync = true
            Blocks.titaniumConveyor.sync = true
            Blocks.armoredConveyor.sync = true
            Blocks.plastaniumConveyor.sync = true
            Blocks.duct.sync = true
            Blocks.armoredDuct.sync = true
        }
    }
}