package com.github.igp.PistonMechanisms;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class PMMechanisms {
	JavaPlugin plugin;

	public PMMechanisms(final JavaPlugin plugin) {
		this.plugin = plugin;
	}

	public void bake(final Block b) {
		
	}

	public void wash(final Block b) {

	}

	public void crush(final Block b) {
		b.breakNaturally();
	}

	public void store(final Block b, final Block container) {
		final Inventory inv = ((InventoryHolder)container.getState()).getInventory();
		
		if (b.getType() != Material.AIR)
		{
			final ItemStack stack = new ItemStack(b.getType(), 1, (short) 0, b.getData());
			
			if ((container.getType() == Material.FURNACE) || (container.getType() == Material.BURNING_FURNACE)) {
				final ItemStack burnstack = inv.getItem(0);
				if (burnstack == null) {
					inv.setItem(0, stack);
					b.setType(Material.AIR);
				}
				else if ((burnstack.getType() == stack.getType()) && (burnstack.getData().getData() == stack.getData().getData()) && (burnstack.getAmount() < (65 - stack.getAmount()))) {
					burnstack.setAmount(burnstack.getAmount() + stack.getAmount());
					b.setType(Material.AIR);
				}
			}
			else if (inv.addItem(stack).size() == 0)
				b.setType(Material.AIR);
		}
		else
		{
			for (Entity e : b.getChunk().getEntities())
			{
				if ((e.getLocation().subtract(b.getLocation()).lengthSquared() < 1) && (e instanceof Item))
				{
					ItemStack stack = ((Item) e).getItemStack();
					HashMap<Integer, ItemStack> overflow = inv.addItem(stack);
					
					if (overflow.isEmpty())
						e.remove();
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
	
	public void retrieve(final Block container, final Block b)	{
		final Inventory inv = ((InventoryHolder)container.getState()).getInventory();
		ItemStack stack = null;
		int loc;
		
		if ((container.getType() == Material.FURNACE) || (container.getType() == Material.BURNING_FURNACE))
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
					stack = inv.getItem(loc);
					break;
				}
			}
		}
		
		if (stack != null)
		{
			if (Materials.isValidBlock(stack.getType()))
			{
				BlockSetter creator = new BlockSetter(b, stack);
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, creator, 1);
			}
			else
			{
				ItemStack drop = new ItemStack(stack);
				drop.setAmount(1);
				ItemStackDropper dropper = new ItemStackDropper(b.getWorld(), b.getLocation().add(.5, .5, .5), drop);
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, dropper, 1);
			}
				
			if (stack.getAmount() > 1)
				stack.setAmount(stack.getAmount() - 1);
			else
				inv.clear(loc);	
		}
	}
}
