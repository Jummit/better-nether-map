package com.jummit.nethermap.mixin;

import com.jummit.nethermap.HeightGetter;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.jummit.nethermap.config.NetherMapConfig;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapItem.class)
public class FilledMapMixin {
	@Redirect(method = "update", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/dimension/DimensionType;hasCeiling()Z"))
	/*
	  Make every dimension have a sky, which makes maps show the surface.

	  @see FilledMapItem#updateColors(World world, Entity entity, MapState state)
	 */
    private boolean hasCeiling(DimensionType type) {
		return false;
	}


	@Inject(method = "update", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;getHoldingPlayer(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData$HoldingPlayer;"))
	private void provideHeightSelector(Level level, Entity player, MapItemSavedData data, CallbackInfo ci, @Share("height_getter") LocalRef<HeightGetter> heightGetter) {
		heightGetter.set(NetherMapConfig.getInstance().getHeightFor(level, player, data));
	}

	@ModifyExpressionValue(method = "update", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/chunk/LevelChunk;getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I"))
	/*
	  Change the height at which the map starts to scan for blocks.
	 */
	public int sampleHeightmap(int value, @Share("height_getter") LocalRef<HeightGetter> heightGetter) {
		return heightGetter.get().get(value);
	}

}
