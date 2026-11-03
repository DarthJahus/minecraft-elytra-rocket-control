package net.jahus.elytrarocket;

import com.mojang.logging.LogUtils;
import net.jahus.elytrarocket.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(ElytraRocketControl.MOD_ID)
public final class ElytraRocketControl {

	public static final String MOD_ID = "elytrarocketcontrol";
	public static final Logger LOGGER = LogUtils.getLogger();

	public ElytraRocketControl() {
		ModConfig.load();
		LOGGER.info("Elytra Rocket Control loaded");
	}
}
