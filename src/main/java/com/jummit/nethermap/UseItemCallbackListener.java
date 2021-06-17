package com.jummit.nethermap;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EmptyMapItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class UseItemCallbackListener implements UseItemCallback {
	@Override
	public TypedActionResult<ItemStack> interact(PlayerEntity player, World world, Hand hand) {
        ItemStack used = player.getStackInHand(hand);
        
        if (used.getItem() instanceof EmptyMapItem) {
            if (world.isClient) {
                return TypedActionResult.success(used);
            } else {
                if (!player.getAbilities().creativeMode) {
                    used.decrement(1);
                }

                player.incrementStat(Stats.USED.getOrCreateStat(used.getItem()));
                player.world.playSoundFromEntity((PlayerEntity)null, player, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, player.getSoundCategory(), 1.0F, 1.0F);
                ItemStack filledMap = FilledMapItem.createMap(world, player.getBlockX(), player.getBlockZ(), (byte)0, true, false);
                NbtCompound tag = filledMap.getTag();
                tag.putInt("yLevel", (int) player.getY());
                filledMap.setTag(tag);
                if (used.isEmpty()) {
                    // For some reason fabric's mixin breaks consuming items, so doing it manually here.
                    player.setStackInHand(hand, filledMap);
                    return TypedActionResult.consume(filledMap);
                } else {
                    if (!player.getInventory().insertStack(filledMap.copy())) {
                        player.dropItem(filledMap, false);
                    }

                    return TypedActionResult.consume(used);
                }
            }
        }
        return TypedActionResult.pass(used);
	}
}