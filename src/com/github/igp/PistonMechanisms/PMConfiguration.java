package com.github.igp.PistonMechanisms;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.igp.IGHelpers.Materials;

public class PMConfiguration
{
	@SuppressWarnings("unused")
	private final JavaPlugin plugin;
	private final FileConfiguration config;
	public Bake bake;
	public Wash wash;
	public Crush crush;
	public Store store;
	public Retrieve retrieve;

	public PMConfiguration(final JavaPlugin plugin)
	{
		this.plugin = plugin;
		
		final File configFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "config.yml");
		if ((configFile == null) || !configFile.exists())
		{
			plugin.getLogger().info("Configuration file not found: saving default");
			plugin.saveDefaultConfig();
		}
			
		config = plugin.getConfig();

		bake = new Bake();
		wash = new Wash();
		crush = new Crush();
		store = new Store();
		retrieve = new Retrieve();
	}

	public class Bake extends Base
	{
		private List<ArrayList<Material>> recipes;

		public Bake()
		{
			load("Bake");
		}

		private void load(final String base)
		{
			super.enabled = config.getString(base + ".Enable").equalsIgnoreCase("true") ? true : false;
			recipes = new ArrayList<ArrayList<Material>>();

			for (final String s : config.getStringList(base + ".Recipes"))
			{
				if (s.contains("|") && (s.split("\\|").length > 1))
				{
					final String i = s.split("\\|")[0].trim().toUpperCase();
					final String p = s.split("\\|")[1].trim().toUpperCase();

					final Material initial = Materials.getMaterialFromString(i);
					final Material product = Materials.getMaterialFromString(p);

					if ((initial == null) || (product == null))
						continue;

					final ArrayList<Material> recipe = new ArrayList<Material>(2);
					recipe.add(initial);
					recipe.add(product);
					recipes.add(recipe);
				}
			}
		}

		public Material getProductMaterial(final Material material)
		{
			for (final ArrayList<Material> recipe : recipes)
			{
				if (recipe.get(0).equals(material))
					return recipe.get(1);
			}

			return null;
		}
	}

	public class Wash extends Bake
	{
		public Wash()
		{
			super.load("Wash");
		}
	}

	public class Crush extends Base
	{
		private Boolean breakNaturally;

		private List<Material> blackList;

		public Crush()
		{
			load();
		}

		private void load()
		{
			super.enabled = config.getString("Crush.Enable").equalsIgnoreCase("true") ? true : false;
			breakNaturally = config.getString("Crush.BreakNaturally").equalsIgnoreCase("true") ? true : false;
			blackList = new ArrayList<Material>();

			for (final String s : config.getStringList("Crush.Blacklist"))
			{
				final Material material = Materials.getMaterialFromString(s);

				if (material == null)
					continue;

				blackList.add(material);
			}
		}

		public Boolean breakNaturally()
		{
			return breakNaturally;
		}

		public Boolean isOnBlackList(final Material material)
		{
			for (final Material m : blackList)
			{
				if (m.equals(material))
					return true;
			}

			return false;
		}
	}

	public class Store extends Base
	{
		private Boolean enableStoreBlocks;
		private Boolean enableStoreItems;
		private Boolean enableContainerChest;
		private Boolean enableContainerFurnace;
		private Boolean enableContainerDispenser;

		private List<Material> blackList;

		public Store()
		{
			load();
		}

		private void load()
		{
			super.enabled = config.getString("Store.Enable").equalsIgnoreCase("true") ? true : false;
			enableStoreBlocks = config.getString("Store.EnableStoreBlocks").equalsIgnoreCase("true") ? true : false;
			enableStoreItems = config.getString("Store.EnableStoreItems").equalsIgnoreCase("true") ? true : false;
			enableContainerChest = config.getString("Store.EnableContainerChest").equalsIgnoreCase("true") ? true : false;
			enableContainerFurnace = config.getString("Store.EnableContainerFurnace").equalsIgnoreCase("true") ? true : false;
			enableContainerDispenser = config.getString("Store.EnableContainerDispenser").equalsIgnoreCase("true") ? true : false;
			blackList = new ArrayList<Material>();

			for (final String s : config.getStringList("Store.Blacklist"))
			{
				final Material material = Materials.getMaterialFromString(s);

				if (material == null)
					continue;

				blackList.add(material);
			}
		}

		public Boolean isStoreBlocksEnabled()
		{
			return enableStoreBlocks;
		}

		public Boolean isStoreItemsEnabled()
		{
			return enableStoreItems;
		}

		public Boolean isContainerEnabled(final Material material)
		{
			if (material.equals(Material.CHEST))
				return enableContainerChest;

			if (material.equals(Material.DISPENSER))
				return enableContainerDispenser;

			if (material.equals(Material.FURNACE) || material.equals(Material.BURNING_FURNACE))
				return enableContainerFurnace;

			return null;
		}

		public Boolean isOnBlackList(final Material material)
		{
			for (final Material m : blackList)
			{
				if (m.equals(material))
					return true;
			}

			return false;
		}
	}

	public class Retrieve extends Base
	{
		private Boolean enableRetrieveBlocks;
		private Boolean enableRetrieveItems;
		private Boolean enableContainerChest;
		private Boolean enableContainerFurnace;
		private Boolean enableContainerDispenser;

		private List<Material> blackList;

		public Retrieve()
		{
			load();
		}

		private void load()
		{
			super.enabled = config.getString("Retrieve.Enable").equalsIgnoreCase("true") ? true : false;
			enableRetrieveBlocks = config.getString("Retrieve.EnableRetrieveBlocks").equalsIgnoreCase("true") ? true : false;
			enableRetrieveItems = config.getString("Retrieve.EnableRetrieveItems").equalsIgnoreCase("true") ? true : false;
			enableContainerChest = config.getString("Retrieve.EnableContainerChest").equalsIgnoreCase("true") ? true : false;
			enableContainerFurnace = config.getString("Retrieve.EnableContainerFurnace").equalsIgnoreCase("true") ? true : false;
			enableContainerDispenser = config.getString("Retrieve.EnableContainerDispenser").equalsIgnoreCase("true") ? true : false;
			blackList = new ArrayList<Material>();

			for (final String s : config.getStringList("Retrieve.Blacklist"))
			{
				final Material material = Materials.getMaterialFromString(s);

				if (material == null)
					continue;

				blackList.add(material);
			}
		}

		public Boolean isRetrieveBlocksEnabled()
		{
			return enableRetrieveBlocks;
		}

		public Boolean isRetrieveItemsEnabled()
		{
			return enableRetrieveItems;
		}

		public Boolean isContainerEnabled(final Material material)
		{
			if (material.equals(Material.CHEST))
				return enableContainerChest;

			if (material.equals(Material.DISPENSER))
				return enableContainerDispenser;

			if (material.equals(Material.FURNACE) || material.equals(Material.BURNING_FURNACE))
				return enableContainerFurnace;

			return null;
		}

		public Boolean isOnBlackList(final Material material)
		{
			for (final Material m : blackList)
			{
				if (m.equals(material))
					return true;
			}

			return false;
		}
	}

	private class Base
	{
		private Boolean enabled;

		public Boolean isEnabled()
		{
			return enabled;
		}
	}
}
