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
}
