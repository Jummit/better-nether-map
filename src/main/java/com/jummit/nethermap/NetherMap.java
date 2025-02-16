package com.jummit.nethermap;

import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jummit.nethermap.config.NetherMapConfig;
import com.mojang.serialization.Codec;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class NetherMap implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Better Nether Map");
	public static final ComponentType<Integer> MAP_HEIGHT = new ComponentType.Builder<Integer>().codec(Codec.INT).packetCodec(PacketCodecs.VAR_INT).build();

	@Override
	public void onInitialize() {
		Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of("nethermap", "map_height"), MAP_HEIGHT);
		AutoConfig.register(NetherMapConfig.class, Toml4jConfigSerializer::new);

		if (NetherMapConfig.getInstance().enablePolymerSupport && FabricLoader.getInstance().isModLoaded("polymer-core")) {
			PolymerComponent.registerDataComponent(MAP_HEIGHT);
		}
	}

}
