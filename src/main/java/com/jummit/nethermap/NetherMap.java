package com.jummit.nethermap;

import com.jummit.nethermap.config.NetherMapConfig;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;

public class NetherMap implements ModInitializer {
	
	@Override
	public void onInitialize() {
		UseItemCallback.EVENT.register(new UseItemCallbackListener());
		AutoConfig.register(NetherMapConfig.class, Toml4jConfigSerializer::new);
	}

}
