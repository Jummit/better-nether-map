package com.jummit.nethermap.mixin;

import com.jummit.nethermap.HeightGetter;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.jummit.nethermap.config.NetherMapConfig;

import net.minecraft.entity.Entity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.map.MapState;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FilledMapItem.class)
public class FilledMapMixin {
	@Redirect(method = "updateColors", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/dimension/DimensionType;hasCeiling()Z"))
	/*
	  Make every dimension have a sky, which makes maps show the surface.

	  @see FilledMapItem#updateColors(World world, Entity entity, MapState state)
	 */
    private boolean hasCeiling(DimensionType type) {
		return false;
	}


	@Inject(method = "updateColors", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/item/map/MapState;getPlayerSyncData(Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/map/MapState$PlayerUpdateTracker;"))
	private void provideHeightSelector(World world, Entity entity, MapState state, CallbackInfo ci, @Share("height_getter") LocalRef<HeightGetter> heightGetter) {
		heightGetter.set(NetherMapConfig.getInstance().getHeightFor(world, entity, state));
	}

	@ModifyExpressionValue(method = "updateColors", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/chunk/WorldChunk;sampleHeightmap(Lnet/minecraft/world/Heightmap$Type;II)I"))
	/*
	  Change the height at which the map starts to scan for blocks.
	 */
	public int sampleHeightmap(int value, @Share("height_getter") LocalRef<HeightGetter> heightGetter) {
		return heightGetter.get().get(value);
	}

}
