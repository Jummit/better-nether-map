package com.jummit.nethermap.mixin;

import com.jummit.nethermap.NetherMap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EmptyMapItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EmptyMapItem.class)
public class EmptyMapItemMixin {
    @ModifyExpressionValue(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/MapItem;create(Lnet/minecraft/server/level/ServerLevel;IIBZZ)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack addHeightComponent(ItemStack original, @Local(argsOnly = true) Player entity) {
        original.set(NetherMap.MAP_HEIGHT, (int) entity.getY());
        return original;
    }
}
