package net.liplum.registries

import arc.func.Func
import arc.func.Prov
import arc.graphics.Color
import mindustry.Vars
import mindustry.ai.types.BuilderAI
import mindustry.ai.types.DefenderAI
import mindustry.ai.types.MinerAI
import mindustry.ai.types.RepairAI
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.entities.abilities.RepairFieldAbility
import mindustry.entities.bullet.BasicBulletType
import mindustry.entities.bullet.LaserBoltBulletType
import mindustry.entities.bullet.MissileBulletType
import mindustry.gen.Sounds
import mindustry.type.ItemStack
import mindustry.type.Weapon
import mindustry.type.ammo.ItemAmmoType
import mindustry.type.ammo.PowerAmmoType
import mindustry.world.meta.BlockFlag
import net.liplum.*
import net.liplum.annotations.DependOn
import net.liplum.bullets.RuvikBullet
import net.liplum.bullets.STEM_VERSION
import net.liplum.flesh.BrainUnitType
import net.liplum.holo.*
import net.liplum.mdt.utils.NewUnitType
import net.liplum.mdt.utils.registerPayloadSource
import net.liplum.mdt.utils.registerUnitType
import net.liplum.scripts.NpcUnitType

object CioUnitTypes {
    @JvmStatic lateinit var holoMiner: HoloUnitType
    @JvmStatic lateinit var holoFighter: HoloUnitType
    @JvmStatic lateinit var holoGuardian: HoloUnitType
    @JvmStatic lateinit var holoArchitect: HoloUnitType
    @JvmStatic lateinit var holoSupporter: HoloUnitType
    @JvmStatic lateinit var brain: BrainUnitType
    @DependOn
    fun _preRegister() {
        HoloUnitType::class.java.registerPayloadSource()
        BrainUnitType::class.java.registerPayloadSource()
        NpcUnitType::class.java.registerPayloadSource()
    }
    @DependOn("CioItems.ic")
    fun holoMiner() {
        holoMiner = NewUnitType(R.Unit.HoloMiner, ::HoloUnitType, ::HoloUnit).apply {
            AutoLife(maxHealth = 1600f, lose = 0.08f)
            health = 2000f
            speed = 2f
            targetPriority = -1f
            aiController = Prov { MinerAI() }
            controller = Func { MinerAI() }
            lowAltitude = true
            flying = true
            mineHardnessScaling = true
            hovering = true
            mineWalls = true
            mineFloor = true
            mineSpeed = 10f
            mineTier = 5
            armor = 2f
            buildSpeed = 1f
            drag = 0.06f
            accel = 0.12f
            engineSize = 1.8f
            engineOffset = 5.7f
            range = 50f
            researchReq = arrayOf(
                ItemStack(CioItems.ic, 1),
                ItemStack(Items.titanium, 60),
                ItemStack(Items.plastanium, 30),
            )
            ammoType = PowerAmmoType(500f)
        }
    }
    @DependOn("CioItems.ic")
    fun holoFighter() {
        holoFighter = NewUnitType(R.Unit.HoloFighter, ::HoloUnitType, ::HoloUnit).apply {
            AutoLife(maxHealth = 2400f, lose = 0.19f)
            speed = 4f
            accel = 0.08f
            targetPriority = 1f
            drag = 0.016f
            buildSpeed = 1f
            flying = true
            hovering = true
            hitSize = 9f
            targetAir = true
            rotateSpeed = 25f
            engineOffset = 7f
            range = 150f
            armor = 3f
            targetFlags = arrayOf(BlockFlag.factory, null)
            circleTarget = true
            ammoType = ItemAmmoType(Items.plastanium)
            enableRuvikTip = true
            ruvikTipRange = 220f
            researchReq = arrayOf(
                ItemStack(CioItems.ic, 2),
                ItemStack(Items.titanium, 100),
                ItemStack(Items.plastanium, 80),
                ItemStack(Items.thorium, 60),
            )
            weapons.add(HoloWeapon("holo-fighter-gun".Cio).apply {
                top = false
                shootSound = Sounds.flame
                shootY = 2f
                reload = 11f
                recoil = 1f
                ejectEffect = Fx.none
                shootCost = 15f
                bullet = RuvikBullet(1.6f, 35f).apply {
                    stemVersion = STEM_VERSION.STEM1
                    maxRange = ruvikTipRange
                    width = 10f
                    height = 10f
                    hitSize = 10f
                    lifetime = 240f
                    frontColor = S.Hologram
                    backColor = S.HologramDark
                }
            })
        }
    }
    @DependOn("CioItems.ic")
    fun holoGuardian() {
        holoGuardian = NewUnitType(R.Unit.HoloGuardian, ::HoloUnitType, ::HoloUnit).apply {
            AutoLife(maxHealth = 5000f, lose = 0.28f)
            abilities.add(
                HoloForceField(
                    60f, 4f, 2200f, 60f * 8
                )
            )
            targetPriority = 5f
            aiController = Prov { DefenderAI() }
            HoloOpacity = 0.4f
            speed = 1.6f
            flying = true
            hovering = true
            buildSpeed = 2.6f
            drag = 0.05f
            accel = 0.1f
            rotateSpeed = 15f
            engineSize = 1.8f
            engineOffset = 5.7f
            hitSize = 15f
            armor = 5f
            researchReq = arrayOf(
                ItemStack(CioItems.ic, 1),
                ItemStack(Items.titanium, 40),
            )
        }
    }
    @DependOn("CioItems.ic")
    fun holoArchitect() {
        holoArchitect = NewUnitType(R.Unit.HoloArchitect, ::HoloUnitType, ::HoloUnit).apply {
            VanillaSpec {
                AutoLife(maxHealth = 2200f, lose = 0.15f)
                buildSpeed = 4.6f
                speed = 3.5f
            }
            ErekirSpec {
                AutoLife(maxHealth = 2800f, lose = 0.15f)
                buildSpeed = 3.6f
                speed = 3.0f
            }
            aiController = Prov { BuilderAI() }
            HoloOpacity = 0.4f
            ColorOpacity = 0.05f
            flying = true
            hovering = true
            drag = 0.06f
            accel = 0.12f
            lowAltitude = true
            engineSize = 1.8f
            engineOffset = 3.7f
            hitSize = 15f
            armor = 5f
            ammoType = PowerAmmoType(900f)
            researchReq = arrayOf(
                ItemStack(CioItems.ic, 3),
                ItemStack(Items.titanium, 120),
                ItemStack(Items.plastanium, 160),
                ItemStack(Items.thorium, 100),
            )
            weapons.add(HoloWeapon().apply {
                x = 0f
                y = 5f
                top = false
                reload = 30f
                ejectEffect = Fx.none
                recoil = 2f
                shoot.shots = 2
                shootSound = Sounds.missile
                velocityRnd = 0.5f
                inaccuracy = 15f
                alternate = true
                shootCost = 3f
                bullet = MissileBulletType(4f, 8f).apply {
                    homingPower = 0.08f
                    weaveMag = 4f
                    weaveScale = 4f
                    lifetime = 50f
                    keepVelocity = false
                    shootEffect = HoloFx.shootHeal
                    smokeEffect = HoloFx.hitLaser
                    despawnEffect = HoloFx.hitLaser
                    hitEffect = despawnEffect
                    frontColor = Color.white
                    hitSound = Sounds.none
                    healPercent = 5.5f
                    collidesTeam = true
                    backColor = S.Hologram
                    trailColor = S.HologramDark
                }
            })
        }
    }
    @DependOn
    fun holoSupporter() {
        holoSupporter = NewUnitType(R.Unit.HoloSupporter, ::HoloUnitType, ::HoloUnit).apply {
            AutoLife(maxHealth = 4000f, lose = 0.15f)
            abilities.add(
                RepairFieldAbility(20f, 60f * 8, 60f).apply {
                    healEffect = HoloFx.heal
                    activeEffect = HoloFx.healWaveDynamic
                }
            )
            aiController = Prov { RepairAI() }
            flying = true
            hovering = true
            HoloOpacity = 0.4f
            ColorOpacity = 0.3f
            armor = 2f
            VanillaSpec {
                buildSpeed = 2.2f
                speed = 2.5f
                accel = 0.06f
                drag = 0.017f
            }
            ErekirSpec {
                buildSpeed = 1.2f
                speed = 2.3f
                payloadCapacity = (5f * 5f) * Vars.tilePayload
                accel = 0.06f
                drag = 0.05f
                pickupUnits = true
            }
            hitSize = 14f
            engineSize = 2f
            engineOffset = 3f
            ammoType = PowerAmmoType(1100f)
            researchReq = arrayOf(
                ItemStack(CioItems.ic, 2),
                ItemStack(Items.titanium, 80),
                ItemStack(Items.plastanium, 120),
            )
            weapons.add(HoloWeapon((R.Unit.HoloSupporter + "-gun").Cio).apply {
                shootSound = Sounds.lasershoot
                reload = 15f
                x = 4f
                y = 5f
                rotate = true
                shootCost = 20f
                bullet = LaserBoltBulletType(5.2f, 15f).apply {
                    lifetime = 35f
                    VanillaSpec {
                        healPercent = 8f
                    }
                    ErekirSpec {
                        healPercent = 2f
                    }
                    collidesTeam = true
                    backColor = S.HologramDark
                    frontColor = S.Hologram
                    smokeEffect = HoloFx.hitLaser
                    hitEffect = HoloFx.hitLaser
                    despawnEffect = HoloFx.hitLaser
                    lightColor = S.Hologram
                }
            })
        }
    }
    @DependOn
    fun brain() {
        registerUnitType(R.Unit.Brain)
        brain = BrainUnitType(R.Unit.Brain).apply {
            flying = true
            drag = 0.06f
            accel = 0.12f
            speed = 1.5f
            health = 100f
            engineSize = 1.8f
            engineOffset = 5.7f
            range = 50f

            ammoType = PowerAmmoType(500f)
            weapons.add(Weapon("${R.Unit.Brain}-hand".Cio).apply {
                x = 8f
                y = 8f
                recoil = -10f
                reload = 7f
                bullet = BasicBulletType(10f, 1f).apply {
                    recoil = -0.7f
                }
            })
        }
    }
}