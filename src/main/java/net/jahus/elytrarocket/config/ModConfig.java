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
		/** Master switch. */
		public boolean enabled = true;
		/**
		 * Multiplier applied to the elytra firework velocity boost.
		 * 0.0 = no boost (default), 1.0 = vanilla, 0.5 = half, etc.
		 */
		public double boost_multiplier = 0.0;
		/**
		 * When boost_multiplier is &lt;= 0, also cancel using a rocket while gliding
		 * (no item consume, optional message).
		 */
		public boolean cancel_use_if_no_boost = true;
		/** Shown when use is cancelled. Empty string = no message. */
		public String message = "Firework boost is disabled while gliding.";
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
		save();
		ElytraRocketControl.LOGGER.info(
				"Config enabled={} boost_multiplier={} cancel_use_if_no_boost={}",
				data.enabled, data.boost_multiplier, data.cancel_use_if_no_boost
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

	public static boolean enabled() {
		return data.enabled;
	}

	public static double boostMultiplier() {
		return data.boost_multiplier;
	}

	public static boolean cancelUseIfNoBoost() {
		return data.cancel_use_if_no_boost;
	}

	public static String message() {
		return data.message == null ? "" : data.message;
	}
}
