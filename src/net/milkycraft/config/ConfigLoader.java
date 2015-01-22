package net.milkycraft.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.milkycraft.EntityManager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class ConfigLoader {

	protected FileConfiguration c;
	protected File configFile;
	protected String fileName;
	protected EntityManager plugin;

	public ConfigLoader(EntityManager plugin, String fileName) {
		this.plugin = plugin;
		this.fileName = fileName;
		File dataFolder = plugin.getDataFolder();
		configFile = new File(dataFolder, File.separator + fileName);
		if (!configFile.exists()) {
			if (!dataFolder.exists()) {
				dataFolder.mkdir();
				dataFolder = plugin.getDataFolder();
			}
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			writeConfig(plugin.getResource("config.yml"));
		}
		c = YamlConfiguration.loadConfiguration(configFile);
	}

	protected void addDefaults() {
		c.options().copyDefaults(true);
		saveConfig();
	}

	public void load() {
		if (!configFile.exists()) {
			plugin.getDataFolder().mkdir();
			saveConfig();
		}
		addDefaults();
		loadKeys();
	}

	protected abstract void loadKeys();

	protected void rereadFromDisk() {
		c = YamlConfiguration.loadConfiguration(configFile);
	}

	protected void saveConfig() {
		try {
			c.save(configFile);
		} catch (IOException ex) {
			//
		}
	}

	protected void set(String key, Object value) {
		c.set(key, value);
	}

	protected void saveIfNotExist() {
		if (!configFile.exists())
			if (plugin.getResource(fileName) != null) {
				plugin.getLogger().info("Saving " + fileName + " to disk");
				plugin.saveResource(fileName, false);
			}
		rereadFromDisk();
	}

	protected void writeConfig(InputStream in) {
		OutputStream out = null;
		try {
			out = new FileOutputStream(configFile);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = in.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
		} catch (Exception ex) {
			plugin.getLogger().severe(
					"Writing default config generating an exception: " + ex.getMessage());
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (Exception ex) {
				plugin.getLogger().severe(
						"Closing streams for config writes generating an exception: "
								+ ex.getMessage());
			}
		}
	}
}