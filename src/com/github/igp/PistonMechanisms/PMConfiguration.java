package com.github.igp.PistonMechanisms;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.igp.IGHelpers.MaterialHelper;

public class PMConfiguration
{
	@SuppressWarnings("unused")
	private final JavaPlugin plugin;
	private final MaterialHelper materialHelper;
	private final FileConfiguration config;
	public Bake bake;
	public Wash wash;
	public Crush crush;
	public Store store;
	public Retrieve retrieve;

	public PMConfiguration(final JavaPlugin plugin)
	{
		this.plugin = plugin;
		materialHelper = new MaterialHelper();

		final File configFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "config.yml");
		if ((configFile == null) || !configFile.exists())
		{
			plugin.getLogger().info("Configuration file not found: saving default");
			plugin.saveResource("pmconfig.yml", false);
			final File f = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "pmconfig.yml");
			f.renameTo(configFile);
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
			super.enabled = config.getString(base + ".Enable", "true").equalsIgnoreCase("true") ? true : false;
			recipes = new ArrayList<ArrayList<Material>>();

			for (final String s : config.getStringList(base + ".Recipes"))
			{
				if (s.contains("|") && (s.split("\\|").length > 1))
				{
					final String i = s.split("\\|")[0].trim().toUpperCase();
					final String p = s.split("\\|")[1].trim().toUpperCase();

					final Material initial = materialHelper.getMaterialFromString(i);
					final Material product = materialHelper.getMaterialFromString(p);

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
			super.enabled = config.getString("Crush.Enable", "true").equalsIgnoreCase("true") ? true : false;
			breakNaturally = config.getString("Crush.BreakNaturally", "true").equalsIgnoreCase("true") ? true : false;
			blackList = new ArrayList<Material>();

			for (final String s : config.getStringList("Crush.Blacklist"))
			{
				final Material material = materialHelper.getMaterialFromString(s);

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
		private Double maxItemStoreDistance;
		private Double maxItemStoreDistanceSquared;

		private List<Material> blackList;

		public Store()
		{
			load();
		}

		private void load()
		{
			super.enabled = config.getString("Store.Enable", "true").equalsIgnoreCase("true") ? true : false;
			enableStoreBlocks = config.getString("Store.EnableStoreBlocks", "true").equalsIgnoreCase("true") ? true : false;
			enableStoreItems = config.getString("Store.EnableStoreItems", "true").equalsIgnoreCase("true") ? true : false;
			enableContainerChest = config.getString("Store.EnableContainerChest", "true").equalsIgnoreCase("true") ? true : false;
			enableContainerFurnace = config.getString("Store.EnableContainerFurnace", "true").equalsIgnoreCase("true") ? true : false;
			enableContainerDispenser = config.getString("Store.EnableContainerDispenser", "true").equalsIgnoreCase("true") ? true : false;
			
			maxItemStoreDistance = config.getDouble("Store.MaxItemStoreDistance", 1.25);
			if (maxItemStoreDistance < 0.5)
				maxItemStoreDistance = 0.5;
			else if (maxItemStoreDistance > 4.5)
				maxItemStoreDistance = 4.5;
			maxItemStoreDistanceSquared = Math.pow(maxItemStoreDistance, 2.0);			
				
			blackList = new ArrayList<Material>();

			for (final String s : config.getStringList("Store.Blacklist"))
			{
				final Material material = materialHelper.getMaterialFromString(s);

				if (material == null)
					continue;

				blackList.add(material);
			}
		}
		
		public Double getMaxItemStoreDistance()
		{
			return maxItemStoreDistance;
		}
		
		public Double getMaxItemStoreDistanceSquared()
		{
			return maxItemStoreDistanceSquared;
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
			super.enabled = config.getString("Retrieve.Enable", "true").equalsIgnoreCase("true") ? true : false;
			enableRetrieveBlocks = config.getString("Retrieve.EnableRetrieveBlocks", "true").equalsIgnoreCase("true") ? true : false;
			enableRetrieveItems = config.getString("Retrieve.EnableRetrieveItems", "true").equalsIgnoreCase("true") ? true : false;
			enableContainerChest = config.getString("Retrieve.EnableContainerChest", "true").equalsIgnoreCase("true") ? true : false;
			enableContainerFurnace = config.getString("Retrieve.EnableContainerFurnace", "true").equalsIgnoreCase("true") ? true : false;
			enableContainerDispenser = config.getString("Retrieve.EnableContainerDispenser", "true").equalsIgnoreCase("true") ? true : false;
			blackList = new ArrayList<Material>();

			for (final String s : config.getStringList("Retrieve.Blacklist"))
			{
				final Material material = materialHelper.getMaterialFromString(s);

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
