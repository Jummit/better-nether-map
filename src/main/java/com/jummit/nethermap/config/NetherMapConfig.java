package com.jummit.nethermap.config;

import java.util.ArrayList;
import java.util.List;

import com.jummit.nethermap.HeightGetter;

import com.jummit.nethermap.NetherMap;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;


@Config(name = "nethermap")
public class NetherMapConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip()
    public boolean useMapCreationHeight = true;
    @ConfigEntry.Gui.Tooltip()
    public int fixedHeight = 100;
    @ConfigEntry.Gui.Tooltip()
    public List<FixedEntry> fixedEntries = List.of(new FixedEntry("minecraft:the_nether", 40));
    @ConfigEntry.Gui.Tooltip()
    public List<String> creationHeightEntries = new ArrayList<>();
    @ConfigEntry.Gui.Tooltip()
    public boolean disableSpinningIndicator = true;
    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.Gui.RequiresRestart()
    public boolean enablePolymerSupport = false;

    public static NetherMapConfig getInstance() {
        return AutoConfig.getConfigHolder(NetherMapConfig.class).getConfig();
    }

    public HeightGetter getHeightFor(Level world, Entity entity, MapItemSavedData state) {
        var dimension = world.dimension().identifier().toString();
        for (var entry : fixedEntries) {
            if (entry.dimension.equals(dimension)) {
                return new HeightGetter.Fixed(entry.height);
            }
        }

        for (String entry : creationHeightEntries) {
            if (entry.equals(dimension)) {
                return getCreationHeight(entity, state);
            }
        }

        if (!world.dimensionType().hasCeiling()) {
            return HeightGetter.PASSTHROUGH;
        }

        if (useMapCreationHeight) {
            return getCreationHeight(entity, state);
        } else {
            return new HeightGetter.Fixed(fixedHeight);
        }
    }

    private HeightGetter getCreationHeight(Entity entity, MapItemSavedData state) {
        if (entity instanceof Player player) {
            var stack = player.getMainHandItem();
            var mapId = stack.get(DataComponents.MAP_ID);
            if (player.level().getMapData(mapId) == state) {
                var val = stack.get(NetherMap.MAP_HEIGHT);
                return val != null ? new HeightGetter.Fixed(val) : HeightGetter.PASSTHROUGH;
            }
            stack = player.getOffhandItem();
            mapId = stack.get(DataComponents.MAP_ID);
            if (player.level().getMapData(mapId) == state) {
                var val = stack.get(NetherMap.MAP_HEIGHT);
                return val != null ? new HeightGetter.Fixed(val) : HeightGetter.PASSTHROUGH;
            }
        }
        return HeightGetter.PASSTHROUGH;
    }

    public static class FixedEntry {
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
}
