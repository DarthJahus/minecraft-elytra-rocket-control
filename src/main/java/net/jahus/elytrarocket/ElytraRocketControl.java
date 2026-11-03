package net.jahus.elytrarocket;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.jahus.elytrarocket.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ElytraRocketControl implements ModInitializer {

	public static final String MOD_ID = "elytrarocketcontrol";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModConfig.load(FabricLoader.getInstance().getConfigDir());
		LOGGER.info("Elytra Rocket Control loaded");
	}
}
