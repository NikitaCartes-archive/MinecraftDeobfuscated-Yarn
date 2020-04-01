package net.minecraft.world.gen.chunk;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;

public class ChunkGeneratorConfig {
	protected int villageDistance = 32;
	protected final int villageSeparation = 8;
	protected int oceanMonumentSpacing = 32;
	protected int oceanMonumentSeparation = 5;
	protected int strongholdDistance = 32;
	protected int strongholdCount = 128;
	protected int strongholdSpread = 3;
	protected int templeDistance = 32;
	protected final int templeSeparation = 8;
	protected final int shipwreckSpacing = 20;
	protected final int shipwreckSeparation = 8;
	protected int endCityDistance = 20;
	protected final int endCitySeparation = 11;
	protected final int oceanRuinSpacing = 24;
	protected final int oceanRuinSeparation = 4;
	protected int mansionDistance = 80;
	protected final int mansionSeparation = 20;
	protected BlockState defaultBlock = Blocks.STONE.getDefaultState();
	protected BlockState defaultFluid = Blocks.WATER.getDefaultState();

	public int getVillageDistance() {
		return this.villageDistance;
	}

	public int getVillageSeparation() {
		return 8;
	}

	public int getOceanMonumentSpacing() {
		return this.oceanMonumentSpacing;
	}

	public int getOceanMonumentSeparation() {
		return this.oceanMonumentSeparation;
	}

	public int getStrongholdDistance() {
		return this.strongholdDistance;
	}

	public int getStrongholdCount() {
		return this.strongholdCount;
	}

	public int getStrongholdSpread() {
		return this.strongholdSpread;
	}

	public int getTempleDistance() {
		return this.templeDistance;
	}

	public int getTempleSeparation() {
		return 8;
	}

	public int getShipwreckSpacing() {
		return 24;
	}

	public int getShipwreckSeparation() {
		return 4;
	}

	public int getOceanRuinSpacing() {
		return 20;
	}

	public int getOceanRuinSeparation() {
		return 8;
	}

	public int getEndCityDistance() {
		return this.endCityDistance;
	}

	public int getEndCitySeparation() {
		return 11;
	}

	public int getMansionDistance() {
		return this.mansionDistance;
	}

	public int getMansionSeparation() {
		return 20;
	}

	public BlockState getDefaultBlock() {
		return this.defaultBlock;
	}

	public BlockState getDefaultFluid() {
		return this.defaultFluid;
	}

	public void setDefaultBlock(BlockState state) {
		this.defaultBlock = state;
	}

	public void setDefaultFluid(BlockState state) {
		this.defaultFluid = state;
	}

	/**
	 * Returns the Y level of the bedrock ceiling, or {@code 0} if the bedrock
	 * ceiling should not be generated.
	 */
	public int getBedrockCeilingY() {
		return 0;
	}

	/**
	 * Returns the Y level of the bedrock floor, or {@code 256} if the bedrock
	 * floor should not be generated.
	 */
	public int getBedrockFloorY() {
		return 256;
	}

	public <T> Dynamic<T> method_26574(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.<T, T>builder()
					.put(dynamicOps.createString("defaultBlock"), BlockState.serialize(dynamicOps, this.defaultBlock).getValue())
					.put(dynamicOps.createString("defaultFluid"), BlockState.serialize(dynamicOps, this.defaultFluid).getValue())
					.build()
			)
		);
	}

	public BlockState method_26575(Random random) {
		return random.nextInt(5) != 2
			? (random.nextBoolean() ? Blocks.WATER : Blocks.LAVA).getDefaultState()
			: Util.method_26719(random, OverworldChunkGeneratorConfig.field_23568);
	}

	public BlockState method_26576(Random random) {
		return Util.method_26719(random, OverworldChunkGeneratorConfig.field_23568);
	}
}
