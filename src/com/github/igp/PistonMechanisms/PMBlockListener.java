package com.github.igp.PistonMechanisms;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PMBlockListener implements Listener {
	@SuppressWarnings("unused")
	private final JavaPlugin plugin;
	private final PMMechanisms mechs;

	public PMBlockListener(final JavaPlugin plugin) {
		this.plugin = plugin;
		mechs = new PMMechanisms(plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPhysics(final BlockPhysicsEvent event) {
		final Block b = event.getBlock();
		if ((b.getType() == Material.PISTON_BASE) || (b.getType() == Material.PISTON_STICKY_BASE)) {
			final BlockFace face = getPistonDirection(b.getData());
			
			if (b.isBlockPowered() || b.isBlockIndirectlyPowered()) {	
				if (isBlockPowered(b, face))				{
					final List<Block> blocks = new ArrayList<Block>(12);
					
					for (int i = 1; i < 13; i++) {
						final Block n = b.getRelative(face, i);
						if (!isNotAcceptedType(n.getType()))
							blocks.add(n);
						else
							break;
					}
	
					for (int i = (blocks.size() - 1); i > -1; i--) {
						final Block n = blocks.get(i);
						final Block next = n.getRelative(face);
						final Block nextdown = next.getRelative(BlockFace.DOWN);
	
						if ((nextdown.getType() == Material.LAVA) || (nextdown.getType() == Material.STATIONARY_LAVA))
							mechs.bake(n);
						else if ((nextdown.getType() == Material.WATER) || (nextdown.getType() == Material.STATIONARY_WATER))
							mechs.wash(n);
	
						if (isValidContainer(next))
							mechs.store(n, next);
						else if (next.getType() == Material.OBSIDIAN)
							mechs.crush(n);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		if (event.isSticky())
		{
			final Block b = event.getBlock();
			final BlockFace face = getPistonDirection(b.getData());
			final Block n = b.getRelative(face, 2);
			
			if (isValidContainer(n))
				mechs.retrieve(n, b.getRelative(face));
		}
	}
	
	public Boolean isBlockPowered(Block b, BlockFace ignore)
	{
		for (BlockFace face : Faces.getAdjacentFaces())
		{						
			if (face == ignore)
				continue;
			
			if (b.getRelative(face).getBlockPower(face) == 0)
				continue;
			
			return true;
		}
		
		return false;
	}

	public BlockFace getPistonDirection(byte data) {
		data = (byte) (data & 0x7);

		switch (data) {
			case 0:
				return BlockFace.DOWN;
			case 1:
				return BlockFace.UP;
			case 2:
				return BlockFace.EAST;
			case 3:
				return BlockFace.WEST;
			case 4:
				return BlockFace.NORTH;
			case 5:
				return BlockFace.SOUTH;
		}

		return null;
	}

	public boolean isNotAcceptedType(final Material type) {
		if (type.equals(Material.OBSIDIAN) || type.equals(Material.BEDROCK) || type.equals(Material.NOTE_BLOCK)  || type.equals(Material.REDSTONE_WIRE) || type.equals(Material.REDSTONE_TORCH_OFF) || type.equals(Material.REDSTONE_TORCH_ON) || type.equals(Material.DIODE_BLOCK_OFF) || type.equals(Material.DIODE_BLOCK_ON) || type.equals(Material.RED_ROSE) || type.equals(Material.YELLOW_FLOWER) || type.equals(Material.RED_MUSHROOM) || type.equals(Material.BROWN_MUSHROOM) || type.equals(Material.SAPLING) || type.equals(Material.SIGN) || type.equals(Material.STONE_BUTTON) || type.equals(Material.LEVER) || type.equals(Material.LADDER) || type.equals(Material.WOODEN_DOOR) || type.equals(Material.IRON_DOOR_BLOCK) || type.equals(Material.TORCH) || type.equals(Material.WATER) || type.equals(Material.LAVA) || type.equals(Material.STATIONARY_LAVA) || type.equals(Material.STATIONARY_WATER) || type.equals(Material.FIRE) || type.equals(Material.PISTON_MOVING_PIECE)
				|| type.equals(Material.PISTON_EXTENSION) || type.equals(Material.DISPENSER) || type.equals(Material.CHEST) || type.equals(Material.FURNACE) || type.equals(Material.BURNING_FURNACE) || type.equals(Material.DIODE)) {
			return true;
		}
		return false;
	}

	private boolean isValidContainer(final Block b) {
		if ((b.getType() == Material.CHEST) || (b.getType() == Material.DISPENSER) || (b.getType() == Material.FURNACE) || (b.getType() == Material.BURNING_FURNACE))
			return true;
		return false;
	}
}
