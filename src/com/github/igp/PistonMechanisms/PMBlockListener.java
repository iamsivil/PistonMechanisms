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
import org.bukkit.material.PistonBaseMaterial;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.igp.IGLib.Helpers.BlockFaceHelper;
import com.github.igp.IGLib.Helpers.BlockHelper;
import com.github.igp.IGLib.Helpers.MaterialHelper;

public class PMBlockListener implements Listener
{
	@SuppressWarnings("unused")
	private final JavaPlugin plugin;
	private final PMMechanisms mechs;

	public PMBlockListener(final JavaPlugin plugin)
	{
		this.plugin = plugin;
		mechs = new PMMechanisms(plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPhysics(final BlockPhysicsEvent event)
	{
		final Block b = event.getBlock();
		if (b.getType().equals(Material.PISTON_BASE) || b.getType().equals(Material.PISTON_STICKY_BASE))
		{
			final BlockFace face = ((PistonBaseMaterial) b.getState().getData()).getFacing();

			if (b.isBlockPowered() || b.isBlockIndirectlyPowered())
			{
				if (BlockHelper.isBlockPowered(b, face))
				{
					final List<Block> blocks = new ArrayList<Block>(12);

					for (int i = 1; i < 13; i++)
					{
						final Block n = b.getRelative(face, i);
						if (isNotAcceptedType(n.getType()))
							break;
						else
							blocks.add(n);
					}
					
					if (blocks.size() == 4)
					{
						Block check = b.getRelative(face, 4);
						if (check.getType().equals(Material.PISTON_BASE) || check.getType().equals(Material.PISTON_STICKY_BASE))
						{
							final BlockFace f = ((PistonBaseMaterial) check.getState().getData()).getFacing();
							if (BlockFaceHelper.getOppositeFace(face) == f)
							{
								if (check.isBlockPowered() || check.isBlockIndirectlyPowered())
								{
									if (BlockHelper.isBlockPowered(check, f))
									{
										mechs.compact(blocks.subList(0, 3));
										return;
									}
								}
							}
						}
					}

					if (blocks.size() < 12)
					{
						if (blocks.isEmpty())
						{
							if (b.getRelative(face).getType().equals(Material.AIR) && MaterialHelper.isValidContainerMaterial((b.getRelative(face, 2).getType())))
								blocks.add(b.getRelative(face));
						}
						else
						{
							final Block last = blocks.get(blocks.size() - 1).getRelative(face);
							if (last.getType().equals(Material.AIR) && MaterialHelper.isValidContainerMaterial(last.getRelative(face).getType()))
								blocks.add(last);
						}
					}
					
					for (int i = (blocks.size() - 1); i > -1; i--)
					{
						final Block n = blocks.get(i);
						final Block next = n.getRelative(face);
						final Block nextdown = next.getRelative(BlockFace.DOWN);

						if (MaterialHelper.isValidContainerMaterial(next.getType()))
							mechs.store(n, next);
						else if (next.getType().equals(Material.OBSIDIAN))
							mechs.crush(n);

						if (nextdown.getType().equals(Material.LAVA) || nextdown.getType().equals(Material.STATIONARY_LAVA))
							mechs.bake(n);
						else if (nextdown.getType().equals(Material.WATER) || nextdown.getType().equals(Material.STATIONARY_WATER))
							mechs.wash(n);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPistonRetract(final BlockPistonRetractEvent event)
	{
		if (event.isSticky())
		{
			final Block b = event.getBlock();
			final BlockFace face = ((PistonBaseMaterial) b.getState().getData()).getFacing();
			final Block n = b.getRelative(face);
			final Block next = n.getRelative(face);
			final Block ndown = n.getRelative(BlockFace.DOWN);

			final Material nmat;
			if (MaterialHelper.isValidContainerMaterial(next.getType()))
				nmat = mechs.retrieve(next, n);
			else
				nmat = next.getType();

			if (ndown.getType().equals(Material.LAVA) || ndown.getType().equals(Material.STATIONARY_LAVA))
				mechs.bake(n, nmat, 1);
			else if (ndown.getType().equals(Material.WATER) || ndown.getType().equals(Material.STATIONARY_WATER))
				mechs.wash(n, nmat, 1);
		}
	}

	public boolean isNotAcceptedType(final Material type)
	{
		if (type.equals(Material.OBSIDIAN) || type.equals(Material.BEDROCK) || type.equals(Material.NOTE_BLOCK) || type.equals(Material.AIR) || type.equals(Material.REDSTONE_WIRE) || type.equals(Material.REDSTONE_TORCH_OFF) || type.equals(Material.REDSTONE_TORCH_ON) || type.equals(Material.DIODE_BLOCK_OFF) || type.equals(Material.DIODE_BLOCK_ON) || type.equals(Material.RED_ROSE) || type.equals(Material.YELLOW_FLOWER) || type.equals(Material.RED_MUSHROOM) || type.equals(Material.BROWN_MUSHROOM) || type.equals(Material.SAPLING) || type.equals(Material.SIGN) || type.equals(Material.STONE_BUTTON) || type.equals(Material.LEVER) || type.equals(Material.LADDER) || type.equals(Material.WOODEN_DOOR) || type.equals(Material.IRON_DOOR_BLOCK) || type.equals(Material.TORCH) || type.equals(Material.WATER) || type.equals(Material.LAVA) || type.equals(Material.STATIONARY_LAVA) || type.equals(Material.STATIONARY_WATER) || type.equals(Material.FIRE) || type.equals(Material.PISTON_MOVING_PIECE) || type.equals(Material.PISTON_EXTENSION) || type.equals(Material.DISPENSER) || type.equals(Material.CHEST) || type.equals(Material.FURNACE) || type.equals(Material.BURNING_FURNACE) || type.equals(Material.DIODE)) { return true; }
		return false;
	}
}
