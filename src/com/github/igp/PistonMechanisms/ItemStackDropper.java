package com.github.igp.PistonMechanisms;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemStackDropper implements Runnable {
	private final World world;
	private final Location location;
	private final ItemStack stack;
	
	public ItemStackDropper(final World world, final Location location, final ItemStack stack) {
		this.world = world;
		this.location = location;
		this.stack = stack;
	}
	
	@Override
	public void run() {
		Item drop = world.dropItem(location, stack);
		drop.setVelocity(new Vector(0,0,0));
	}
}
