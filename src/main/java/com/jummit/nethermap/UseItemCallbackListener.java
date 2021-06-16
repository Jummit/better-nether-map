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
        
        if (!world.isClient && used.getItem() instanceof EmptyMapItem) {
            ItemStack itemStack = player.getStackInHand(hand);
            if (world.isClient) {
                return TypedActionResult.success(itemStack);
            } else {
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }

                player.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
                player.world.playSoundFromEntity((PlayerEntity)null, player, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, player.getSoundCategory(), 1.0F, 1.0F);
                ItemStack filledMap = FilledMapItem.createMap(world, player.getBlockX(), player.getBlockZ(), (byte)0, true, false);
                NbtCompound tag = filledMap.getTag();
                tag.putInt("yLevel", (int) player.getY());
                filledMap.setTag(tag);
                if (itemStack.isEmpty()) {
                    return TypedActionResult.consume(filledMap);
                } else {
                    if (!player.getInventory().insertStack(filledMap.copy())) {
                    player.dropItem(filledMap, false);
                    }

                    return TypedActionResult.consume(itemStack);
                }
            }
        }
        return TypedActionResult.pass(used);
	}
}