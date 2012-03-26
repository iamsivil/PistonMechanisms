package com.github.igp.PistonMechanisms;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class BlockSetter implements Runnable {
	private final Block block;
	private final ItemStack stack;
	
	public BlockSetter(final Block block, final ItemStack stack){
		this.block = block;
		this.stack = stack;
	}
	
	@Override
	public void run() {
		block.setType(stack.getType());
		block.setData(stack.getData().getData());
	}
}
