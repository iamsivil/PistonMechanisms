package com.github.igp.PistonMechanisms;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class PMMain extends JavaPlugin{
	private Logger log;
	private PMBlockListener blockListener;

	@Override
	public void onEnable() {
		log = this.getLogger();
		
		blockListener = new PMBlockListener(this);
		
		getServer().getPluginManager().registerEvents(blockListener, this);

		log.info("Enabled.");
	}

	@Override
	public void onDisable() {
		log.info("Disabled.");
	}
}
