package com.jummit.nethermap.mixin;

import com.jummit.nethermap.NetherMap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EmptyMapItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EmptyMapItem.class)
public class EmptyMapItemMixin {
    @ModifyExpressionValue(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/FilledMapItem;createMap(Lnet/minecraft/server/world/ServerWorld;IIBZZ)Lnet/minecraft/item/ItemStack;"))
    private ItemStack addHeightComponent(ItemStack original, @Local(argsOnly = true) PlayerEntity entity) {
        original.set(NetherMap.MAP_HEIGHT, (int) entity.getY());
        return original;
    }
}
