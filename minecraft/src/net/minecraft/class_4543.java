package net.minecraft;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;

public class class_4543 {
	private final class_4543.class_4544 field_20640;
	private final long field_20641;
	private final class_4545 field_20642;

	public class_4543(class_4543.class_4544 arg, long l, class_4545 arg2) {
		this.field_20640 = arg;
		this.field_20641 = l;
		this.field_20642 = arg2;
	}

	public class_4543 method_22392(BiomeSource biomeSource) {
		return new class_4543(biomeSource, this.field_20641, this.field_20642);
	}

	public Biome method_22393(BlockPos blockPos) {
		return this.field_20642.method_22396(this.field_20641, blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.field_20640);
	}

	public interface class_4544 {
		Biome getBiome(int i, int j, int k);
	}
}
