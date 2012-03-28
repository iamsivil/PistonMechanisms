package com.github.igp.PistonMechanisms;

import org.bukkit.Material;

public class Materials
{
	private final static Material[] materials = {};

	public final static Material[] getInvalidMaterials()
	{
		return materials;
	}

	public final static Boolean isValidMaterial(final Material material)
	{
		for (final Material m : materials)
		{
			if (m.equals(material))
				return false;
		}
		return true;
	}

	public final static Boolean isValidBlock(final Material material)
	{
		if (material.isBlock())
		{
			if (material.equals(Material.REDSTONE_TORCH_ON) || material.equals(Material.REDSTONE_TORCH_OFF))
				return false;
			if (material.equals(Material.PISTON_BASE) || material.equals(Material.PISTON_EXTENSION) || material.equals(Material.PISTON_MOVING_PIECE) || material.equals(Material.PISTON_STICKY_BASE))
				return false;
			if (material.equals(Material.DISPENSER))
				return false;
			if (material.equals(Material.SAPLING))
				return false;
			if (material.equals(Material.LONG_GRASS))
				return false;
			if (material.equals(Material.DEAD_BUSH))
				return false;
			if (material.equals(Material.BED))
				return false;
			if (material.equals(Material.BED_BLOCK))
				return false;
			if (material.equals(Material.WATER) || material.equals(Material.STATIONARY_WATER))
				return false;
			if (material.equals(Material.LAVA) || material.equals(Material.STATIONARY_LAVA))
				return false;
			if (material.equals(Material.RAILS) || material.equals(Material.POWERED_RAIL) || material.equals(Material.DETECTOR_RAIL))
				return false;
			if (material.equals(Material.RED_ROSE) || material.equals(Material.YELLOW_FLOWER))
				return false;
			if (material.equals(Material.BROWN_MUSHROOM) || material.equals(Material.RED_MUSHROOM))
				return false;
			if (material.equals(Material.TORCH))
				return false;
			if (material.equals(Material.FIRE))
				return false;
			if (material.equals(Material.REDSTONE_WIRE))
				return false;
			if (material.equals(Material.CROPS))
				return false;
			if (material.equals(Material.FURNACE) || material.equals(Material.BURNING_FURNACE))
				return false;
			if (material.equals(Material.SIGN_POST) || material.equals(Material.WALL_SIGN))
				return false;
			if (material.equals(Material.WOODEN_DOOR) || material.equals(Material.WOOD_DOOR))
				return false;
			if (material.equals(Material.IRON_DOOR) || material.equals(Material.IRON_DOOR_BLOCK))
				return false;
			if (material.equals(Material.LADDER))
				return false;
			if (material.equals(Material.LEVER))
				return false;
			if (material.equals(Material.STONE_BUTTON))
				return false;
			if (material.equals(Material.PORTAL))
				return false;
			if (material.equals(Material.LOCKED_CHEST))
				return false;
			if (material.equals(Material.DIODE_BLOCK_ON) || material.equals(Material.DIODE_BLOCK_OFF))
				return false;
			if (material.equals(Material.MONSTER_EGGS))
				return false;
			if (material.equals(Material.PUMPKIN_STEM) || material.equals(Material.MELON_STEM))
				return false;
			if (material.equals(Material.VINE))
				return false;
			if (material.equals(Material.WATER_LILY))
				return false;
			if (material.equals(Material.NETHER_WARTS))
				return false;
			if (material.equals(Material.ENDER_PORTAL))
				return false;

			return true;
		}
		return false;
	}

	public final static Boolean isValidContainer(final Material material)
	{
		if (material.equals(Material.CHEST) || material.equals(Material.DISPENSER) || material.equals(Material.FURNACE) || material.equals(Material.BURNING_FURNACE))
			return true;

		return false;
	}
}
