package com.github.igp.PistonMechanisms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.igp.IGHelpers.MaterialHelper;

public class PMConfiguration
{
	private final JavaPlugin plugin;
	private FileConfiguration config;
	public Bake bake;
	public Wash wash;
	public Crush crush;
	public Store store;
	public Retrieve retrieve;
	public Compact compact;

	public PMConfiguration(final JavaPlugin plugin)
	{
		this.plugin = plugin;

		load();
	}
	
	private void load()
	{
		final File configFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "config.yml");
		if ((configFile == null) || !configFile.exists())
		{
			plugin.getLogger().info("Configuration file not found: saving default");
			plugin.saveResource("pmconfig.yml", true);
			final File f = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "pmconfig.yml");
			f.renameTo(configFile);
		}
		
		try
		{
			plugin.getConfig().load(plugin.getDataFolder() + File.separator + "config.yml");
			config = plugin.getConfig();
			
			bake = new Bake();
			wash = new Wash();
			crush = new Crush();
			compact = new Compact();
			store = new Store();
			retrieve = new Retrieve();
		}
		catch (FileNotFoundException e)
		{
			plugin.getLogger().severe("Configuration file not found, please restart to regenerate it");
		}
		catch (IOException e)
		{
			plugin.getLogger().severe("Error reading the configuration file, do you have it open in another program?");
		}
		catch (InvalidConfigurationException e)
		{
			plugin.getLogger().severe("Invalid configuration file, please delete it and restart to regenerate it");
		}
	}
	
	public void reload()
	{
		load();
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
					final Material initial = MaterialHelper.getMaterialFromString(i);
					if (initial == null)
						continue;
					
					final String p = s.split("\\|")[1].trim().toUpperCase();
					final Material product = MaterialHelper.getMaterialFromString(p);
					if (product == null)
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
				final Material material = MaterialHelper.getMaterialFromString(s);

				if (material == null)
					continue;

				blackList.add(material);
			}
			
			blackList.add(Material.PISTON_BASE);
			blackList.add(Material.PISTON_STICKY_BASE);
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
	
	public class Compact extends Base
	{
		private List<ArrayList<Material>> recipes;
		private List<Material> products;
		
		public Compact()
		{
			load();
		}
		
		private void load()
		{
			super.enabled = config.getString("Compact.Enable", "true").equalsIgnoreCase("true") ? true : false;
			recipes = new ArrayList<ArrayList<Material>>();
			products = new ArrayList<Material>();
			
			for (final String s : config.getStringList("Compact.Recipes"))
			{
				if (s.contains("=") && (s.split("\\=").length > 1))
				{					
					final String p = s.split("\\=")[1].trim().toUpperCase();
					final Material product = MaterialHelper.getMaterialFromString(p);
					if (product == null)
						continue;
					
					ArrayList<Material> materials = null;
					String i = s.split("\\=")[0].trim().toUpperCase();
					if (i.contains("|") && (i.split("\\|").length > 1))
					{
						materials = new ArrayList<Material>();
						for (final String m : i.split("\\|"))
						{							
							final Material material = MaterialHelper.getMaterialFromString(m.trim().toUpperCase());
							
							if (material == null)
								continue;
							
							materials.add(material);
						}
					}
					
					if ((materials == null) || (materials.size() != 3))
						continue;
					
					recipes.add(materials);
					products.add(product);
				}
			}
		}
		
		public Material getProductMaterial(List<Block> blocks)
		{
			if (blocks.size() != 3)
				return null;
			
			List<Material> materials = new ArrayList<Material>();
			for (Block b : blocks)
				materials.add(b.getType());
			
			for (int i = 0; i < recipes.size(); i++)
			{
				List<Material> recipe = recipes.get(i);
				
				boolean toContinue = false;
				
				List<Material> checked = new ArrayList<Material>();
				for (Material material : recipe)
				{
					if (checked.contains(material))
						continue;
					
					if (Collections.frequency(recipe, material) == Collections.frequency(materials, material))
						checked.add(material);
					else
					{
						toContinue = true;
						break;
					}
				}
				
				if (toContinue)
					continue;
				
				return products.get(i);
			}
			
			return null;
		}
	}

	public class Store extends Base
	{
		private Boolean enableStoreBlocks;
		private Boolean enableStoreItems;
		private Boolean enableStoreVehicles;
		private Boolean enableStoreAnimals;
		private Boolean enableStoreMonsters;
		private Boolean enableStoreEntities;
		private Boolean enableContainerChest;
		private Boolean enableContainerFurnace;
		private Boolean enableContainerDispenser;
		private Double maxItemStoreDistance;
		private Double maxItemStoreDistanceSquared;
		private Double maxVehicleStoreDistance;
		private Double maxVehicleStoreDistanceSquared;
		private Double maxCreatureStoreDistance;
		private Double maxCreatureStoreDistanceSquared;

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
			enableStoreVehicles = config.getString("Store.EnableStoreVehicles", "true").equalsIgnoreCase("true") ? true : false;
			enableStoreAnimals = config.getString("Store.EnableStoreAnimals", "true").equalsIgnoreCase("true") ? true : false;
			enableStoreMonsters = config.getString("Store.EnableStoreMonsters", "true").equalsIgnoreCase("true") ? true : false;
			enableContainerChest = config.getString("Store.EnableContainerChest", "true").equalsIgnoreCase("true") ? true : false;
			enableContainerFurnace = config.getString("Store.EnableContainerFurnace", "true").equalsIgnoreCase("true") ? true : false;
			enableContainerDispenser = config.getString("Store.EnableContainerDispenser", "true").equalsIgnoreCase("true") ? true : false;

			if (enableStoreItems || enableStoreVehicles || enableStoreAnimals || enableStoreMonsters)
				enableStoreEntities = true;
			else
				enableStoreEntities = false;
			
			maxItemStoreDistance = config.getDouble("Store.MaxItemStoreDistance", 1.25);
			if (maxItemStoreDistance < 0.5)
				maxItemStoreDistance = 0.5;
			else if (maxItemStoreDistance > 4.5)
				maxItemStoreDistance = 4.5;
			maxItemStoreDistanceSquared = Math.pow(maxItemStoreDistance, 2.0);

			maxVehicleStoreDistance = config.getDouble("Store.MaxVehicleStoreDistance", 1.25);
			if (maxVehicleStoreDistance < 0.5)
				maxVehicleStoreDistance = 0.5;
			else if (maxVehicleStoreDistance > 4.5)
				maxVehicleStoreDistance = 4.5;
			maxVehicleStoreDistanceSquared = Math.pow(maxVehicleStoreDistance, 2.0);
			
			maxCreatureStoreDistance = config.getDouble("Store.MaxCreatureStoreDistance", 1.25);
			if (maxCreatureStoreDistance < 0.5)
				maxCreatureStoreDistance = 0.5;
			else if (maxCreatureStoreDistance > 4.5)
				maxCreatureStoreDistance = 4.5;
			maxCreatureStoreDistanceSquared = Math.pow(maxCreatureStoreDistance, 2.0);

			blackList = new ArrayList<Material>();

			for (final String s : config.getStringList("Store.Blacklist"))
			{
				final Material material = MaterialHelper.getMaterialFromString(s);

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

		public Double getMaxVehicleStoreDistance()
		{
			return maxVehicleStoreDistance;
		}

		public Double getMaxVehicleStoreDistanceSquared()
		{
			return maxVehicleStoreDistanceSquared;
		}
		
		public Double getMaxCreatureStoreDistance()
		{
			return maxCreatureStoreDistance;
		}

		public Double getMaxCreatureStoreDistanceSquared()
		{
			return maxCreatureStoreDistanceSquared;
		}

		public Boolean isStoreBlocksEnabled()
		{
			return enableStoreBlocks;
		}

		public Boolean isStoreItemsEnabled()
		{
			return enableStoreItems;
		}

		public Boolean isStoreVehiclesEnabled()
		{
			return enableStoreVehicles;
		}

		public Boolean isStoreAnimalsEnabled()
		{
			return enableStoreAnimals;
		}

		public Boolean isStoreMonstersEnabled()
		{
			return enableStoreMonsters;
		}
		
		public Boolean isStoreEntitiesEnabled()
		{
			return enableStoreEntities;
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
		private Boolean enableRetrieveVehicles;
		private Boolean enableRetrieveAnimals;
		private Boolean enableRetrieveMonsters;
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
			enableRetrieveVehicles = config.getString("Retrieve.EnableRetrieveVehicles", "true").equalsIgnoreCase("true") ? true : false;
			enableRetrieveAnimals = config.getString("Retrieve.EnableRetrieveAnimals", "true").equalsIgnoreCase("true") ? true : false;
			enableRetrieveMonsters = config.getString("Retrieve.EnableRetrieveMonsters", "true").equalsIgnoreCase("true") ? true : false;
			enableContainerChest = config.getString("Retrieve.EnableContainerChest", "true").equalsIgnoreCase("true") ? true : false;
			enableContainerFurnace = config.getString("Retrieve.EnableContainerFurnace", "true").equalsIgnoreCase("true") ? true : false;
			enableContainerDispenser = config.getString("Retrieve.EnableContainerDispenser", "true").equalsIgnoreCase("true") ? true : false;
			blackList = new ArrayList<Material>();

			for (final String s : config.getStringList("Retrieve.Blacklist"))
			{
				final Material material = MaterialHelper.getMaterialFromString(s);

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

		public Boolean isRetrieveVehiclesEnabled()
		{
			return enableRetrieveVehicles;
		}

		public Boolean isRetrieveAnimalsEnabled()
		{
			return enableRetrieveAnimals;
		}

		public Boolean isRetrieveMonstersEnabled()
		{
			return enableRetrieveMonsters;
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
