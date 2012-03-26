package com.github.igp.PistonMechanisms;

import org.bukkit.block.BlockFace;

public class BlockFaces
{
	private final static BlockFace[] adjacentFaces =
	{
			BlockFace.UP,
			BlockFace.DOWN,
			BlockFace.NORTH,
			BlockFace.EAST,
			BlockFace.SOUTH,
			BlockFace.WEST
	};

	public final static BlockFace[] getAdjacentFaces()
	{
		return adjacentFaces;
	}
}
