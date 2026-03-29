package com.jummit.nethermap.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.jummit.nethermap.config.NetherMapConfig;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.item.map.MapState;

@Mixin(MapState.class)
public class MapDataMixin {

    @Definition(id = "dimension",
            field = "Lnet/minecraft/item/map/MapState;dimension:Lnet/minecraft/registry/RegistryKey;")
    @Definition(id = "NETHER",
            field = "Lnet/minecraft/world/World;NETHER:Lnet/minecraft/registry/RegistryKey;")
    @Expression("this.dimension == NETHER")
    @ModifyExpressionValue(method = "getPlayerMarkerRotation", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean disablePlayerMarkerRotation(boolean original) {
        return NetherMapConfig.getInstance().disableSpinningIndicator ? false : original;
    }
}
