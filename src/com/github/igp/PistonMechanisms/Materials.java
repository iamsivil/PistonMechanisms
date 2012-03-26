package com.github.igp.PistonMechanisms;

import org.bukkit.Material;

public class Materials {
	private final static Material[] materials = {

	};
	
	public final static Material[] getInvalidMaterials() {
		return materials;
	}
	
	public final static Boolean isValidMaterial(Material material)
	{
		for (Material m : materials)
		{
			if (m == material)
				return false;
		}
		return true;
	}
	
	public final static Boolean isValidBlock(Material material)
	{
		if (material.isBlock())
		{
			if ((material == Material.REDSTONE_TORCH_ON) || (material == Material.REDSTONE_TORCH_OFF))
				return false;
			if ((material == Material.PISTON_BASE) || (material == Material.PISTON_EXTENSION) || (material == Material.PISTON_MOVING_PIECE) || (material == Material.PISTON_STICKY_BASE))
				return false;
			if (material == Material.DISPENSER)
				return false;
			if (material == Material.SAPLING)
				return false;
			if (material == Material.LONG_GRASS)
				return false;
			if (material == Material.DEAD_BUSH)
				return false;
			if (material == Material.BED)
				return false;
			if (material == Material.BED_BLOCK)
				return false;
			if ((material == Material.WATER) || (material == Material.STATIONARY_WATER))
				return false;
			if ((material == Material.LAVA) || (material == Material.STATIONARY_LAVA))
				return false;
			if ((material == Material.RAILS) || (material == Material.POWERED_RAIL) || (material == Material.DETECTOR_RAIL))
				return false;
			if ((material == Material.RED_ROSE) || (material == Material.YELLOW_FLOWER))
				return false;
			if ((material == Material.BROWN_MUSHROOM) || (material == Material.RED_MUSHROOM))
				return false;
			if (material == Material.TORCH)
				return false;
			if (material == Material.FIRE)
				return false;
			if (material == Material.REDSTONE_WIRE)
				return false;
			if (material == Material.CROPS)
				return false;
			if ((material == Material.FURNACE) || (material == Material.BURNING_FURNACE))
				return false;
			if ((material == Material.SIGN_POST) || (material == Material.WALL_SIGN))
				return false;
			if ((material == Material.WOODEN_DOOR) || (material == Material.WOOD_DOOR))
				return false;
			if ((material == Material.IRON_DOOR) || (material == Material.IRON_DOOR_BLOCK))
				return false;
			if (material == Material.LADDER)
				return false;
			if (material == Material.LEVER)
				return false;
			if (material == Material.STONE_BUTTON)
				return false;
			if (material == Material.PORTAL)
				return false;
			if (material == Material.LOCKED_CHEST)
				return false;
			if ((material == Material.DIODE_BLOCK_ON) || (material == Material.DIODE_BLOCK_OFF))
				return false;
			if (material == Material.MONSTER_EGGS)
				return false;
			if ((material == Material.PUMPKIN_STEM) || (material == Material.MELON_STEM))
				return false;
			if (material == Material.VINE)
				return false;
			if (material == Material.WATER_LILY)
				return false;
			if (material == Material.NETHER_WARTS)
				return false;
			if (material == Material.ENDER_PORTAL)
				return false;			
			
			return true;
		}
		return false;
	}
}
