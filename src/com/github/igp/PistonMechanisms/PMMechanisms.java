package com.github.igp.PistonMechanisms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.igp.IGHelpers.BlockHelper;
import com.github.igp.IGHelpers.BlockSetter;
import com.github.igp.IGHelpers.ItemStackDropper;
import com.github.igp.IGHelpers.MaterialHelper;

public class PMMechanisms
{
	JavaPlugin plugin;
	private final MaterialHelper materialHelper;
	private final BlockHelper blockHelper;
	PMConfiguration config;

	public PMMechanisms(final JavaPlugin plugin)
	{
		this.plugin = plugin;
		materialHelper = new MaterialHelper();
		blockHelper = new BlockHelper();
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

	public void wash(final Block b)
	{
		if (!config.wash.isEnabled())
			return;

		final Material mat = config.wash.getProductMaterial(b.getType());

		if ((mat != null) && materialHelper.isValidBlockMaterial(mat))
			b.setType(mat);
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
			final ItemStackDropper dropper = new ItemStackDropper(b.getWorld(), b.getLocation().add(.5, .5, .5), drop);
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, dropper, 1);
			b.setType(Material.AIR);
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
		}
		else if (config.store.isStoreItemsEnabled())
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
					if (entities.get(i) instanceof Item)
					{
						if (locations.get(i).subtract(bCenter).lengthSquared() <= config.store.getMaxItemStoreDistanceSquared())
						{
							final ItemStack stack = ((Item) entities.get(i)).getItemStack();

							if (config.store.isOnBlackList(stack.getType()))
								continue;

							final HashMap<Integer, ItemStack> overflow = inv.addItem(stack);

							if (overflow.isEmpty())
								entities.get(i).remove();
							else
							{
								if (overflow.get(0).getAmount() < stack.getAmount())
								{
									stack.setAmount(overflow.get(0).getAmount());
								}
							}
						}
					}
				}
			}
		}
	}

	public void retrieve(final Block container, final Block b)
	{
		if (!config.retrieve.isEnabled())
			return;

		if (!config.retrieve.isContainerEnabled(container.getType()))
			return;

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
			Boolean update = false;

			if (materialHelper.isValidBlockMaterial(stack.getType()) && config.retrieve.isRetrieveBlocksEnabled())
			{
				final BlockSetter creator = new BlockSetter(b, stack);
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, creator, 1);
				update = true;
			}
			else if (config.retrieve.isRetrieveItemsEnabled())
			{
				final ItemStack drop = new ItemStack(stack);
				drop.setAmount(1);
				final ItemStackDropper dropper = new ItemStackDropper(b.getWorld(), b.getLocation().add(.5, .5, .5), drop);
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, dropper, 1);
				update = true;
			}

			if (update)
			{
				if (stack.getAmount() > 1)
					stack.setAmount(stack.getAmount() - 1);
				else
					inv.clear(loc);
			}
		}
	}
}
