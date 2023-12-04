package com.jummit.nethermap.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.jummit.nethermap.config.NetherMapConfig;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.Entity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.map.MapState;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;

@Mixin(FilledMapItem.class)
public class FilledMapMixin {
	@Redirect(method = "updateColors", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/dimension/DimensionType;hasCeiling()Z"))
	/*
	  Make every dimension have a sky, which makes maps show the surface.

	  @see FilledMapItem#updateColors(World world, Entity entity, MapState state)
	 */
    public boolean hasCeiling(DimensionType type) {
		return false;
	}

	@Redirect(method = "updateColors", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/chunk/WorldChunk;sampleHeightmap(Lnet/minecraft/world/Heightmap$Type;II)I"))
	/*
	  Change the height at which the map starts to scan for blocks.
	 */
	public int sampleHeightmap(WorldChunk chunk, Heightmap.Type type, int x, int z, World world, Entity entity, MapState state) {
		return AutoConfig.getConfigHolder(NetherMapConfig.class).get()
				.getHeightFor(chunk, x, z, world, entity, state)
				.get();
	}

}
