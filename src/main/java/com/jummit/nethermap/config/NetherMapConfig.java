package com.jummit.nethermap.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jummit.nethermap.Height;
import com.jummit.nethermap.CreationHeight;
import com.jummit.nethermap.FixedHeight;
import com.jummit.nethermap.HeightmapHeight;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.entity.Entity;
import net.minecraft.item.map.MapState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

@Config(name = "nethermap")
public class NetherMapConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip()
    public boolean useMapCreationHeight = true;
    @ConfigEntry.Gui.Tooltip()
    public int fixedHeight = 100;
    @ConfigEntry.Gui.Tooltip()
    public List<FixedEntry> fixedEntries = Arrays.asList(new FixedEntry("minecraft:the_nether", 40));
    @ConfigEntry.Gui.Tooltip()
    public List<String> creationHeightEntries = new ArrayList<String>();

    public static NetherMapConfig getInstance() {
        return AutoConfig.getConfigHolder(NetherMapConfig.class).getConfig();
    }

    public Height getHeightFor(WorldChunk chunk, int x, int z, World world, Entity entity, MapState state) {
        String dimension = world.getRegistryKey().getValue().toString();
        Height heightmapHeight = new HeightmapHeight(chunk, x, z);
        Height creationHeight = new CreationHeight(world, entity, state, heightmapHeight);
        for (FixedEntry entry : fixedEntries) {
            if (entry.dimension.equals(dimension)) {
                return new FixedHeight(entry.height);
            }
        };
        for (String entry : creationHeightEntries) {
            if (entry.equals(dimension)) {
                return creationHeight;
            }
        };
        if (!world.getDimension().hasCeiling()) {
            return heightmapHeight;
        }
        if (useMapCreationHeight) {
            return creationHeight;
        } else {
            return new FixedHeight(fixedHeight);
        }
    };
}

class FixedEntry {
    
    String dimension;
    int height;

    public FixedEntry() {
        dimension = "";
        height = 100;
    }

    public FixedEntry(String dimension, int height) {
        this.dimension = dimension;
        this.height = height;
    }
}
