package net.minecraft.world.gen.chunk;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class ChunkGeneratorConfig {
	protected int villageDistance = 32;
	protected final int field_13145 = 8;
	protected int oceanMonumentSpacing = 32;
	protected int oceanMonumentSeparation = 5;
	protected int strongholdDistance = 32;
	protected int strongholdCount = 128;
	protected int strongholdSpread = 3;
	protected int templeDistance = 32;
	protected final int field_13137 = 8;
	protected final int field_13155 = 16;
	protected final int field_13153 = 8;
	protected int endCityDistance = 20;
	protected final int field_13151 = 11;
	protected final int field_13150 = 16;
	protected final int field_13149 = 8;
	protected int mansionDistance = 80;
	protected final int field_13147 = 20;
	protected BlockState defaultBlock = Blocks.field_10340.getDefaultState();
	protected BlockState defaultFluid = Blocks.field_10382.getDefaultState();

	public int getVillageDistance() {
		return this.villageDistance;
	}

	public int method_12559() {
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

	public int method_12568() {
		return 8;
	}

	public int method_12566() {
		return 16;
	}

	public int method_12562() {
		return 8;
	}

	public int method_12564() {
		return 16;
	}

	public int method_12555() {
		return 8;
	}

	public int getEndCityDistance() {
		return this.endCityDistance;
	}

	public int method_12557() {
		return 11;
	}

	public int getMansionDistance() {
		return this.mansionDistance;
	}

	public int method_12552() {
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
