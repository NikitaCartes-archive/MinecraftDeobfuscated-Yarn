package net.minecraft.world.gen.chunk;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

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
	protected final int shipwreckSpacing = 16;
	protected final int shipwreckSeparation = 8;
	protected int endCityDistance = 20;
	protected final int endCitySeparation = 11;
	protected final int oceanRuinSpacing = 16;
	protected final int oceanRuinSeparation = 8;
	protected int mansionDistance = 80;
	protected final int mansionSeparation = 20;
	protected BlockState defaultBlock = Blocks.field_10340.getDefaultState();
	protected BlockState defaultFluid = Blocks.field_10382.getDefaultState();

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
		return 16;
	}

	public int getShipwreckSeparation() {
		return 8;
	}

	public int getOceanRuinSpacing() {
		return 16;
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

	public void setDefaultBlock(BlockState blockState) {
		this.defaultBlock = blockState;
	}

	public void setDefaultFluid(BlockState blockState) {
		this.defaultFluid = blockState;
	}

	public int getMaxY() {
		return 0;
	}

	public int getMinY() {
		return 256;
	}
}
