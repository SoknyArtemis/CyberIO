package net.liplum.type

import arc.graphics.Color
import mindustry.type.Item
import net.liplum.utils.spriteX

class SpecItem : Item {
    constructor(name: String, color: Color) : super(name, color)
    constructor(name: String) : super(name)

    override fun loadIcon() {
        uiIcon = this.spriteX
        fullIcon = this.spriteX
    }
}