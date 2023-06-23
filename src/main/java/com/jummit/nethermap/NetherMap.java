package com.jummit.nethermap;

import com.jummit.nethermap.config.NetherMapConfig;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetherMap implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Better Nether Map");

	@Override
	public void onInitialize() {
		UseItemCallback.EVENT.register(new UseItemCallbackListener());
		AutoConfig.register(NetherMapConfig.class, Toml4jConfigSerializer::new);
	}

}
