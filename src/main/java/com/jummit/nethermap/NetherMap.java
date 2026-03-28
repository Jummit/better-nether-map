package com.jummit.nethermap;

import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jummit.nethermap.config.NetherMapConfig;
import com.mojang.serialization.Codec;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;


public class NetherMap implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Better Nether Map");
	public static final DataComponentType<Integer> MAP_HEIGHT = new DataComponentType.Builder<Integer>().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build();

	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath("nethermap", "map_height"), MAP_HEIGHT);
		AutoConfig.register(NetherMapConfig.class, Toml4jConfigSerializer::new);

		if (NetherMapConfig.getInstance().enablePolymerSupport && FabricLoader.getInstance().isModLoaded("polymer-core")) {
			PolymerComponent.registerDataComponent(MAP_HEIGHT);
		}
	}

}
