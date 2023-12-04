package com.jummit.nethermap;

import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.Heightmap;

public class HeightmapHeight implements Height {

    WorldChunk chunk;
    int x;
    int z;

    @Override
    public int get() {
        return chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x, z) + 1;
    }

    public HeightmapHeight(WorldChunk chunk, int x, int z) {
        this.chunk = chunk;
        this.x = x;
        this.z = z;
    }
}
