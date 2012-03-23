package com.github.igp.PistonMechanisms;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class PMMechanisms {
	JavaPlugin plugin;

	public PMMechanisms(final JavaPlugin plugin) {
		this.plugin = plugin;
	}

	public void Bake(final Block b) {
		if (b.getType() == Material.WOOL)
			b.setType(Material.STONE);
	}

	public void Wash(final Block b) {

	}

	public void Crush(final Block b) {
		final ItemStack stack = new ItemStack(b.getType(), 1, (short) 0, b.getData());
		b.getWorld().dropItemNaturally(b.getLocation(), stack);
		b.setType(Material.AIR);
	}

	public void Store(final Block b, final Block container) {
		final ItemStack stack = new ItemStack(b.getType(), 1, (short) 0, b.getData());
		final Inventory inv = ((InventoryHolder) container.getState()).getInventory();
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

	
	public void Retreive(final Block b)	{
		
	}
}
