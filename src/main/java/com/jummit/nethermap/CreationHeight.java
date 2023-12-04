package com.jummit.nethermap;

import java.util.Objects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.world.World;

public class CreationHeight implements Height {

    Height fallback;
    World world;
    Entity entity;
    MapState state;

    @Override
    public int get() {
        PlayerEntity player = (PlayerEntity)(entity);
        for (int slot = 0; slot < player.getInventory().size(); slot++) {
        ItemStack item = player.getInventory().getStack(slot);
            if (item.getItem() instanceof FilledMapItem && FilledMapItem.getMapState(item, entity.getWorld()) == state) {
                return Objects.requireNonNull(item.getNbt()).getInt("yLevel");
            }
        }
        return fallback.get();
    }

    public CreationHeight(World world, Entity entity, MapState state, Height fallback) {
        this.fallback = fallback;
        this.world = world;
        this.entity = entity;
        this.state = state;
    }
}
