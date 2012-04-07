package com.github.igp.PistonMechanisms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Monster;
import org.bukkit.entity.NPC;
import org.bukkit.entity.PoweredMinecart;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SpawnEgg;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.igp.IGHelpers.BlockHelper;
import com.github.igp.IGHelpers.BlockSetter;
import com.github.igp.IGHelpers.EntitySpawner;
import com.github.igp.IGHelpers.EntityTypeHelper;
import com.github.igp.IGHelpers.ItemStackDropper;
import com.github.igp.IGHelpers.MaterialHelper;
import com.github.igp.IGHelpers.VehicleSpawner;

public class PMMechanisms
{
	JavaPlugin plugin;
	private final MaterialHelper materialHelper;
	private final BlockHelper blockHelper;
	private final EntityTypeHelper entityTypeHelper;
	PMConfiguration config;

	public PMMechanisms(final JavaPlugin plugin)
	{
		this.plugin = plugin;
		materialHelper = new MaterialHelper();
		blockHelper = new BlockHelper();
		entityTypeHelper = new EntityTypeHelper();
		config = new PMConfiguration(plugin);
	}

	public void bake(final Block b)
	{
		if (!config.bake.isEnabled())
			return;

		final Material mat = config.bake.getProductMaterial(b.getType());

		if ((mat != null) && materialHelper.isValidBlockMaterial(mat))
			b.setType(mat);
	}

	public void bake(final Block b, final long delay)
	{
		bake(b, b.getType(), delay);
	}

	public void bake(final Block b, final Material material, final long delay)
	{
		if (!config.bake.isEnabled())
			return;

		final Material mat = config.bake.getProductMaterial(material);

		if ((mat != null) && materialHelper.isValidBlockMaterial(material))
		{
			final BlockSetter creator = new BlockSetter(b, mat);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, creator, delay);
		}
	}

	public void wash(final Block b)
	{
		if (!config.wash.isEnabled())
			return;

		final Material mat = config.wash.getProductMaterial(b.getType());

		if ((mat != null) && materialHelper.isValidBlockMaterial(mat))
			b.setType(mat);
	}

	public void wash(final Block b, final long delay)
	{
		wash(b, b.getType(), delay);
	}

	public void wash(final Block b, final Material material, final long delay)
	{
		if (!config.wash.isEnabled())
			return;

		final Material mat = config.wash.getProductMaterial(material);

		if ((mat != null) && materialHelper.isValidBlockMaterial(material))
		{
			final BlockSetter creator = new BlockSetter(b, mat);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, creator, delay);
		}
	}

	public void crush(final Block b)
	{
		if (!config.crush.isEnabled())
			return;

		if (config.crush.isOnBlackList(b.getType()))
			return;

		if (b.getType().equals(Material.AIR))
			return;

		if (config.crush.breakNaturally())
			b.breakNaturally();
		else
		{
			final ItemStack drop = new ItemStack(b.getType(), 1, (short) 0, b.getData());
			final ItemStackDropper dropper = new ItemStackDropper(blockHelper.getBlockCenter(b), drop);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, dropper, 1);
			b.setType(Material.AIR);
		}
	}
	
	public void compact(final List<Block> blocks)
	{
		if (!config.compact.isEnabled())
			return;
		
		if (blocks.size() != 3)
			return;
		
		Material mat = config.compact.getProductMaterial(blocks);
		
		if (mat == null)
			return;
		
		Block target = blocks.get((int) Math.floor(((float)blocks.size()) / 2.0));
		
		if (materialHelper.isValidBlockMaterial(mat))
		{
			for (Block b : blocks)
				b.setType(Material.AIR);
			
			target.setType(mat);
		}
	}

	public void store(final Block b, final Block container)
	{
		if (!config.store.isEnabled())
			return;

		if (!config.store.isContainerEnabled(container.getType()))
			return;

		final Inventory inv = ((InventoryHolder) container.getState()).getInventory();
		
		if (!b.getType().equals(Material.AIR) && config.store.isStoreBlocksEnabled() && !config.store.isOnBlackList(b.getType()))
		{
			if (!(materialHelper.isValidRailMaterial(b.getType()) && config.store.isStoreVehiclesEnabled()))
			{
				final ItemStack stack = new ItemStack(b.getType(), 1, (short) 0, b.getData());

				if (container.getType().equals(Material.FURNACE) || container.getType().equals(Material.BURNING_FURNACE))
				{
					final ItemStack burnstack = inv.getItem(0);
					if (burnstack == null)
					{
						inv.setItem(0, stack);
						b.setType(Material.AIR);
					}
					else if (burnstack.getType().equals(stack.getType()) && (burnstack.getData().getData() == stack.getData().getData()) && (burnstack.getAmount() < (65 - stack.getAmount())))
					{
						burnstack.setAmount(burnstack.getAmount() + stack.getAmount());
						b.setType(Material.AIR);
					}
				}
				else if (inv.addItem(stack).size() == 0)
					b.setType(Material.AIR);
				
				return;
			}
		}
		
		if (config.store.isStoreEntitiesEnabled())
		{			
			final List<Location> locations = new ArrayList<Location>();
			final List<Entity> entities = new ArrayList<Entity>();

			for (final Entity e : b.getChunk().getEntities())
			{
				final Location loc = e.getLocation();
				locations.add(new Location(e.getWorld(), loc.getX(), loc.getY(), loc.getZ()));
				entities.add(e);
			}

			if (locations.size() == entities.size())
			{
				final Location bCenter = blockHelper.getBlockCenter(b);

				for (int i = 0; i < locations.size(); i++)
				{
					final Location location = locations.get(i);
					final Entity entity = entities.get(i);
					ItemStack stack = null;

					if ((entity instanceof Item) && config.store.isStoreItemsEnabled())
					{
						if (!(location.subtract(bCenter).lengthSquared() <= config.store.getMaxItemStoreDistanceSquared()))
							continue;

						stack = ((Item) entity).getItemStack();
					}
					else if ((entity instanceof Vehicle) && config.store.isStoreVehiclesEnabled())
					{
						if (!(location.subtract(bCenter).lengthSquared() <= config.store.getMaxVehicleStoreDistanceSquared()))
							continue;

						if (entity instanceof Boat)
							stack = new ItemStack(Material.BOAT);
						else if (entity instanceof PoweredMinecart)
						{
							stack = new ItemStack(Material.POWERED_MINECART);
						}
						else if (entity instanceof StorageMinecart)
						{
							stack = new ItemStack(Material.STORAGE_MINECART);
							if (((StorageMinecart) entity).getInventory().getContents() != null)
							{
								final ItemStackDropper dropper = new ItemStackDropper(blockHelper.getBlockCenter(b), ((StorageMinecart) entity).getInventory().getContents());
								plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, dropper, 1);
							}
						}
						else if (entity instanceof Minecart)
							stack = new ItemStack(Material.MINECART);
						else
							continue;
					}
					else if ((entity instanceof Monster) && config.store.isStoreMonstersEnabled())
					{
						if (!(location.subtract(bCenter).lengthSquared() <= config.store.getMaxCreatureStoreDistanceSquared()))
							continue;
						
						final SpawnEgg spawnEgg = new SpawnEgg();
						spawnEgg.setSpawnedType(entity.getType());
						stack = spawnEgg.toItemStack(1);
						stack.setData(spawnEgg);
					}
					else if (((entity instanceof Animals) || (entity instanceof Creature) || (entity instanceof NPC)) && config.store.isStoreMonstersEnabled())
					{
						if (!(location.subtract(bCenter).lengthSquared() <= config.store.getMaxCreatureStoreDistanceSquared()))
							continue;
						
						final SpawnEgg spawnEgg = new SpawnEgg();
						spawnEgg.setSpawnedType(entity.getType());
						stack = spawnEgg.toItemStack(1);
						stack.setData(spawnEgg);
					}
					else
						continue;

					if (stack == null)
						continue;

					if (config.store.isOnBlackList(stack.getType()))
						continue;

					final HashMap<Integer, ItemStack> overflow = inv.addItem(stack);

					if (overflow.isEmpty())
						entity.remove();
					else
					{
						if (overflow.get(0).getAmount() < stack.getAmount())
							stack.setAmount(overflow.get(0).getAmount());
					}
				}
			}
		}
	}

	public Material retrieve(final Block container, final Block b)
	{
		if (!config.retrieve.isEnabled())
			return null;

		if (!config.retrieve.isContainerEnabled(container.getType()))
			return null;

		final Inventory inv = ((InventoryHolder) container.getState()).getInventory();
		ItemStack stack = null;
		int loc;

		if (container.getType().equals(Material.FURNACE) || container.getType().equals(Material.BURNING_FURNACE))
		{
			loc = 2;
			if ((inv.getItem(loc) != null) && (inv.getItem(loc).getTypeId() != 0) && (inv.getItem(loc).getAmount() > 0))
				stack = inv.getItem(loc);
		}
		else
		{
			for (loc = 0; loc < inv.getSize(); loc++)
			{
				if ((inv.getItem(loc) != null) && (inv.getItem(loc).getTypeId() != 0) && (inv.getItem(loc).getAmount() > 0))
				{
					if (!config.retrieve.isOnBlackList(inv.getItem(loc).getType()))
					{
						stack = inv.getItem(loc);
						break;
					}
				}
			}
		}

		if (stack != null)
		{
			if (materialHelper.isValidBlockMaterial(stack.getType()) && config.retrieve.isRetrieveBlocksEnabled())
			{
				final BlockSetter creator = new BlockSetter(b, stack);
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, creator, 1);
			}
			else if (materialHelper.isValidVehicleMaterial(stack.getType()) && config.retrieve.isRetrieveVehiclesEnabled())
			{
				final VehicleSpawner spawner = new VehicleSpawner(blockHelper.getBlockCenter(b), stack.getType());
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, spawner, 1);
			}
			else if (stack.getType().equals(Material.MONSTER_EGG) && ((config.retrieve.isRetrieveAnimalsEnabled() && entityTypeHelper.isAnimalEntityType(((SpawnEgg) stack.getData()).getSpawnedType())) || (config.retrieve.isRetrieveMonstersEnabled() && entityTypeHelper.isMonsterEntityType(((SpawnEgg) stack.getData()).getSpawnedType()))))
			{
				final EntitySpawner spawner = new EntitySpawner(blockHelper.getBlockCenter(b), ((SpawnEgg) stack.getData()).getSpawnedType());
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, spawner, 1);
			}
			else if (config.retrieve.isRetrieveItemsEnabled())
			{
				final ItemStack drop = new ItemStack(stack);
				drop.setAmount(1);
				final ItemStackDropper dropper = new ItemStackDropper(blockHelper.getBlockCenter(b), drop);
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, dropper, 1);
			}
			else
				return null;

			if (stack.getAmount() > 1)
				stack.setAmount(stack.getAmount() - 1);
			else
				inv.clear(loc);

			return stack.getType();
		}

		return null;
	}
}
