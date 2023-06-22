package com.jummit.nethermap.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.minecraft.entity.Entity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Config(name = "nethermap")
public class NetherMapConfig implements ConfigData {

    public boolean useMapCreationHeight = true;
    public List<DimensionEntry> dimensions = Arrays.asList(new DimensionEntry("minecraft:nether", 40));

    public static NetherMapConfig getInstance() {
        return AutoConfig.getConfigHolder(NetherMapConfig.class).getConfig();
    }

    public int getDimensionScanHeight(World world, Entity entity, MapState state) {
        if (useMapCreationHeight) {
            ServerPlayerEntity player = (ServerPlayerEntity)(entity);
            for (int slot = 0; slot < player.getInventory().size(); slot++) {
                ItemStack item = player.getInventory().getStack(slot);
                if (item.getItem() instanceof FilledMapItem && FilledMapItem.getMapState(item, entity.getWorld()) == state) {
                    return Objects.requireNonNull(item.getNbt()).getInt("yLevel");
                }
            }
        } else {
            for (DimensionEntry dimensionEntry : dimensions) {
                if (dimensionEntry.dimension.equals(world.getRegistryKey().getValue().toString())) {
                    return dimensionEntry.scanHeight;
                }
            }
        }
        return 100;
    }
}

class DimensionEntry {
    
    String dimension;
    int scanHeight;

    public DimensionEntry() {
        dimension = "";
        scanHeight = 100;
    }

    public DimensionEntry(String dimension, int scanHeight) {
        this.dimension = dimension;
        this.scanHeight = scanHeight;
    }
}