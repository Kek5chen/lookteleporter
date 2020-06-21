package me.itsjbey.lookteleport;

import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Main extends JavaPlugin {

	public static final File config = new File("plugins/LookTeleporter", "config.yml");

	public static int COOLDOWN = 60;
	public static int TELEPORT_DELAY = 60;
	public static int MAX_DISTANCE = 50;
	public static String PERMISSION = "";
	public static String PERMISSION_BYPASS = "";

	public static Main instance;

	public YamlConfiguration yml;

	@Override
	public void onEnable() {

		instance = this;

		config.getParentFile().mkdirs();

		if(!config.exists()) {

			try {

				config.createNewFile();

				PrintWriter pw = new PrintWriter(config);

				pw.print(IOUtils.toString(getResource("config.yml")));
				pw.flush();
				pw.close();

			} catch (IOException e) {
				Bukkit.getConsoleSender().sendMessage("§c§lCould'nt instantiate LookTeleporter.");
			}

		}

		yml = YamlConfiguration.loadConfiguration(config);

		COOLDOWN = yml.getInt("Cooldown");
		PERMISSION = yml.getString("Permission");
		PERMISSION_BYPASS = yml.getString("Bypass-Permission");
		MAX_DISTANCE = yml.getInt("Max-Distance");
		TELEPORT_DELAY = yml.getInt("Teleport-Delay");

		Bukkit.getPluginManager().registerEvents(new EVENTS(), this);

	}

	public static Main getInstance() {
		return instance;
	}

}
