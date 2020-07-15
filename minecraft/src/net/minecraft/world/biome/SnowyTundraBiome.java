package net.minecraft.world.biome;

public final class SnowyTundraBiome extends Biome {
	public SnowyTundraBiome(Biome.Settings settings) {
		super(settings);
	}

	@Override
	public float getMaxSpawnChance() {
		return 0.07F;
	}
}
