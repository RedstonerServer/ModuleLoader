package com.redstoner.modules.lagchunks;

import org.bukkit.Location;
import org.bukkit.World;

public class LaggyChunk {
	public final int	x, y, z, amount;
	public final World	world;
	
	public LaggyChunk(int x, int y, int z, World world, int amount) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.amount = amount;
	}
	
	public Location getLocation() {
		return new Location(world, x, y, z);
	}
}
