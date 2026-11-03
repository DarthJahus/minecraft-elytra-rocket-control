package net.jahus.elytrarocket.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.jahus.elytrarocket.ElytraRocketControl;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ModConfig {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static final Path PATH = FMLPaths.CONFIGDIR.get().resolve("elytra-rocket-control.json");
	private static Data data = new Data();

	public static final class Data {
		public boolean enable = true;
		public boolean cancel_rockets_in_flight = true;
		/** {@code "flight"} = no extra water rule; {@code "always"} = cancel any rocket use in water. */
		public String cancel_rockets_under_water = "flight";
		public String message = "Firework rockets cannot be used right now.";
	}

	private ModConfig() {}

	public static void load() {
		if (Files.isRegularFile(PATH)) {
			try (Reader reader = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
				Data loaded = GSON.fromJson(reader, Data.class);
				if (loaded != null) {
					data = loaded;
				}
			} catch (Exception e) {
				ElytraRocketControl.LOGGER.error("Failed to read config, using defaults", e);
				data = new Data();
			}
		}
		if (data.cancel_rockets_under_water == null || data.cancel_rockets_under_water.isBlank()) {
			data.cancel_rockets_under_water = "flight";
		} else {
			data.cancel_rockets_under_water = data.cancel_rockets_under_water.trim().toLowerCase();
			if (!data.cancel_rockets_under_water.equals("always")
					&& !data.cancel_rockets_under_water.equals("flight")) {
				ElytraRocketControl.LOGGER.warn(
						"Unknown cancel_rockets_under_water='{}', falling back to 'flight'",
						data.cancel_rockets_under_water
				);
				data.cancel_rockets_under_water = "flight";
			}
		}
		save();
		ElytraRocketControl.LOGGER.info(
				"Config enable={} cancel_in_flight={} cancel_under_water={}",
				data.enable, data.cancel_rockets_in_flight, data.cancel_rockets_under_water
		);
	}

	public static void save() {
		try {
			Files.createDirectories(PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(PATH, StandardCharsets.UTF_8)) {
				GSON.toJson(data, writer);
			}
		} catch (IOException e) {
			ElytraRocketControl.LOGGER.error("Failed to write config", e);
		}
	}

	public static boolean cancelUnderWaterAlways() {
		return "always".equalsIgnoreCase(data.cancel_rockets_under_water);
	}

	public static String message() {
		return data.message == null ? "" : data.message;
	}

	public static boolean shouldCancel(boolean fallFlying, boolean inWater) {
		if (!data.enable) {
			return false;
		}
		if (fallFlying && data.cancel_rockets_in_flight) {
			return true;
		}
		return inWater && cancelUnderWaterAlways();
	}
}
