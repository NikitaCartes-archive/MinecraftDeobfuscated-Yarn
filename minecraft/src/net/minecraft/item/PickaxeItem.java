package net.minecraft.item;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;

public class PickaxeItem extends MiningToolItem {
	private static final Set<Block> EFFECTIVE_BLOCKS = ImmutableSet.of(
		Blocks.field_10546,
		Blocks.field_10418,
		Blocks.field_10445,
		Blocks.field_10025,
		Blocks.field_10201,
		Blocks.field_10442,
		Blocks.field_10425,
		Blocks.field_10205,
		Blocks.field_10571,
		Blocks.field_10295,
		Blocks.field_10085,
		Blocks.field_10212,
		Blocks.field_10441,
		Blocks.field_10090,
		Blocks.field_9989,
		Blocks.field_10515,
		Blocks.field_10225,
		Blocks.field_10384,
		Blocks.field_10167,
		Blocks.field_10080,
		Blocks.field_9979,
		Blocks.field_10292,
		Blocks.field_10361,
		Blocks.field_10117,
		Blocks.field_10518,
		Blocks.field_10344,
		Blocks.field_10340,
		Blocks.field_10474,
		Blocks.field_10289,
		Blocks.field_10508,
		Blocks.field_10346,
		Blocks.field_10115,
		Blocks.field_10093,
		Blocks.field_10454,
		Blocks.field_10136,
		Blocks.field_10007,
		Blocks.field_10298,
		Blocks.field_10351,
		Blocks.field_10191,
		Blocks.field_10131,
		Blocks.field_10390,
		Blocks.field_10237,
		Blocks.field_10624,
		Blocks.field_10175,
		Blocks.field_9978,
		Blocks.field_10483,
		Blocks.field_10467,
		Blocks.field_10360,
		Blocks.field_10494,
		Blocks.field_10158,
		Blocks.field_10329,
		Blocks.field_10283,
		Blocks.field_10024,
		Blocks.field_10412,
		Blocks.field_10405,
		Blocks.field_10064,
		Blocks.field_10262,
		Blocks.field_10601,
		Blocks.field_10189,
		Blocks.field_10016,
		Blocks.field_10478,
		Blocks.field_10322,
		Blocks.field_10507,
		Blocks.field_10603,
		Blocks.field_10371,
		Blocks.field_10605,
		Blocks.field_10373,
		Blocks.field_10532,
		Blocks.field_10140,
		Blocks.field_10055,
		Blocks.field_10203,
		Blocks.field_10320,
		Blocks.field_10275,
		Blocks.field_10063,
		Blocks.field_10407,
		Blocks.field_10051,
		Blocks.field_10268,
		Blocks.field_10068,
		Blocks.field_10199,
		Blocks.field_10600
	);

	protected PickaxeItem(ToolMaterial toolMaterial, Item.Settings settings) {
		super(toolMaterial, EFFECTIVE_BLOCKS, settings);
	}

	@Override
	public boolean isEffectiveOn(BlockState blockState) {
		Block block = blockState.getBlock();
		int i = this.getMaterial().getMiningLevel();
		if (block == Blocks.field_10540) {
			return i == 3;
		} else if (block == Blocks.field_10201
			|| block == Blocks.field_10442
			|| block == Blocks.field_10013
			|| block == Blocks.field_10234
			|| block == Blocks.field_10205
			|| block == Blocks.field_10571
			|| block == Blocks.field_10080) {
			return i >= 2;
		} else if (block != Blocks.field_10085 && block != Blocks.field_10212 && block != Blocks.field_10441 && block != Blocks.field_10090) {
			Material material = blockState.getMaterial();
			return material == Material.STONE || material == Material.METAL || material == Material.ANVIL;
		} else {
			return i >= 1;
		}
	}

	@Override
	public float getMiningSpeed(ItemStack itemStack, BlockState blockState) {
		Material material = blockState.getMaterial();
		return material != Material.METAL && material != Material.ANVIL && material != Material.STONE
			? super.getMiningSpeed(itemStack, blockState)
			: this.miningSpeed;
	}

	@Override
	protected ToolType getToolType() {
		return ToolType.field_20342;
	}
}
