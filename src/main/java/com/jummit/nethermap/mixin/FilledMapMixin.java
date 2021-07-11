package com.jummit.nethermap.mixin;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.jummit.nethermap.config.NetherMapConfig;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.map.MapState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;


@Mixin(FilledMapItem.class)
public class FilledMapMixin {
	@Overwrite
	public void updateColors(World world, Entity entity, MapState state) {
		int scanHeight = AutoConfig.getConfigHolder(NetherMapConfig.class).getConfig().getDimensionScanHeight(world, entity, state);
		if (world.getRegistryKey() == state.dimension && entity instanceof PlayerEntity) {
			int i = 1 << state.scale;
			int j = state.centerX;
			int k = state.centerZ;
			int l = MathHelper.floor(entity.getX() - (double)j) / i + 64;
			int m = MathHelper.floor(entity.getZ() - (double)k) / i + 64;
			int n = 128 / i;
			if (world.getDimension().hasCeiling()) {
				n /= 2;
			}
			
			MapState.PlayerUpdateTracker playerUpdateTracker = state.getPlayerSyncData((PlayerEntity)entity);
			++playerUpdateTracker.field_131;
			boolean bl = false;
			
			for(int o = l - n + 1; o < l + n; ++o) {
				if ((o & 15) == (playerUpdateTracker.field_131 & 15) || bl) {
					bl = false;
					double d = 0.0D;
					
					for(int p = m - n - 1; p < m + n; ++p) {
						if (o >= 0 && p >= -1 && o < 128 && p < 128) {
							int q = o - l;
							int r = p - m;
							boolean bl2 = q * q + r * r > (n - 2) * (n - 2);
							int s = (j / i + o - 64) * i;
							int t = (k / i + p - 64) * i;
							Multiset<MapColor> multiset = LinkedHashMultiset.create();
							WorldChunk worldChunk = world.getWorldChunk(new BlockPos(s, 0, t));
							if (!worldChunk.isEmpty()) {
								ChunkPos chunkPos = worldChunk.getPos();
								int u = s & 15;
								int v = t & 15;
								int w = 0;
								double e = 0.0D;
								
								BlockPos.Mutable mutable = new BlockPos.Mutable();
								BlockPos.Mutable mutable2 = new BlockPos.Mutable();
								
								for(int y = 0; y < i; ++y) {
									for(int z = 0; z < i; ++z) {
										int aa = world.getDimension().hasCeiling() ? scanHeight : worldChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, y + u, z + v) + 1;
										BlockState blockState;
										if (aa <= 1) {
											blockState = Blocks.BEDROCK.getDefaultState();
										} else {
											do {
												--aa;
												mutable.set(chunkPos.getStartX() + y + u, aa, chunkPos.getStartZ() + z + v);
												blockState = worldChunk.getBlockState(mutable);
											} while(blockState.getTopMaterialColor(world, mutable) == MapColor.CLEAR && aa > 0);
											
											if (aa > 0 && !blockState.getFluidState().isEmpty()) {
												int ab = aa - 1;
												mutable2.set(mutable);
												
												BlockState blockState2;
												do {
													mutable2.setY(ab--);
													blockState2 = worldChunk.getBlockState(mutable2);
													++w;
												} while(ab > 0 && !blockState2.getFluidState().isEmpty());
												
												FluidState fluidState = blockState.getFluidState();
												blockState = !fluidState.isEmpty() && !blockState.isSideSolidFullSquare(world, mutable, Direction.UP) ? fluidState.getBlockState() : blockState;
											}
										}
										
										state.removeBanner(world, chunkPos.getStartX() + y + u, chunkPos.getStartZ() + z + v);
										e += (double)aa / (double)(i * i);
										multiset.add(blockState.getTopMaterialColor(world, mutable));
									}
								}
								
								w /= i * i;
								double f = (e - d) * 4.0D / (double)(i + 4) + ((double)(o + p & 1) - 0.5D) * 0.4D;
								int ac = 1;
								if (f > 0.6D) {
									ac = 2;
								}
								
								if (f < -0.6D) {
									ac = 0;
								}
								
								MapColor mapColor = (MapColor)Iterables.getFirst(Multisets.copyHighestCountFirst(multiset), MapColor.CLEAR);
								if (mapColor == MapColor.WATER_BLUE) {
									f = (double)w * 0.1D + (double)(o + p & 1) * 0.2D;
									ac = 1;
									if (f < 0.5D) {
										ac = 2;
									}
									
									if (f > 0.9D) {
										ac = 0;
									}
								}
								
								d = e;
								if (p >= 0 && q * q + r * r < n * n && (!bl2 || (o + p & 1) != 0)) {
									byte b = state.colors[o + p * 128];
									byte c = (byte)(mapColor.id * 4 + ac);
									if (b != c) {
										state.colors[o + p * 128] = c;
										state.markDirty(o, p);
										bl = true;
									}
								}
							}
						}
					}
				}
			}
		}
	}
}