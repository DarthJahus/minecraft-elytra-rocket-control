package net.jahus.elytrarocket.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.jahus.elytrarocket.ElytraRocketControl;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ModConfig {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static Path path;
	private static Data data = new Data();

	public static final class Data {
		/** Master switch for the whole mod. */
		public boolean enable = true;
		/** Cancel firework rocket use while gliding (fall flying). */
		public boolean cancel_rockets_in_flight = true;
		/**
		 * Underwater rocket policy:
		 * <ul>
		 *   <li>{@code "flight"} — no extra water rule (only {@code cancel_rockets_in_flight} applies)</li>
		 *   <li>{@code "always"} — cancel any rocket use while in water (even if
		 *       {@code cancel_rockets_in_flight} is false)</li>
		 * </ul>
		 */
		public String cancel_rockets_under_water = "flight";
		/** Action-bar message when use is cancelled. Empty = silent. */
		public String message = "Firework rockets cannot be used right now.";
	}

	private ModConfig() {}

	public static void load(Path configDir) {
		path = configDir.resolve("elytra-rocket-control.json");
		if (Files.isRegularFile(path)) {
			try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
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
		if (path == null) return;
		try {
			Files.createDirectories(path.getParent());
			try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
				GSON.toJson(data, writer);
			}
		} catch (IOException e) {
			ElytraRocketControl.LOGGER.error("Failed to write config", e);
		}
	}

	/** {@code true} when value is {@code "always"}. */
	public static boolean cancelUnderWaterAlways() {
		return "always".equalsIgnoreCase(data.cancel_rockets_under_water);
	}

	public static String message() {
		return data.message == null ? "" : data.message;
	}

	/**
	 * Whether rocket use should be canceled for this player state.
	 */
	public static boolean shouldCancel(boolean fallFlying, boolean inWater) {
		if (!data.enable) {
			return false;
		}
		if (fallFlying && data.cancel_rockets_in_flight) {
			return true;
		}
		// "always" = block all rocket use in water; "flight" = no extra water rule
		return inWater && cancelUnderWaterAlways();
	}
}
