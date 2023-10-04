package net.minecraft.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class WeightedPressurePlateBlock extends AbstractPressurePlateBlock {
	public static final MapCodec<WeightedPressurePlateBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.intRange(1, 1024).fieldOf("max_weight").forGetter(block -> block.weight),
					BlockSetType.CODEC.fieldOf("block_set_type").forGetter(block -> block.blockSetType),
					createSettingsCodec()
				)
				.apply(instance, WeightedPressurePlateBlock::new)
	);
	public static final IntProperty POWER = Properties.POWER;
	private final int weight;

	@Override
	public MapCodec<WeightedPressurePlateBlock> getCodec() {
		return CODEC;
	}

	protected WeightedPressurePlateBlock(int weight, BlockSetType type, AbstractBlock.Settings settings) {
		super(settings, type);
		this.setDefaultState(this.stateManager.getDefaultState().with(POWER, Integer.valueOf(0)));
		this.weight = weight;
	}

	@Override
	protected int getRedstoneOutput(World world, BlockPos pos) {
		int i = Math.min(getEntityCount(world, BOX.offset(pos), Entity.class), this.weight);
		if (i > 0) {
			float f = (float)Math.min(this.weight, i) / (float)this.weight;
			return MathHelper.ceil(f * 15.0F);
		} else {
			return 0;
		}
	}

	@Override
	protected int getRedstoneOutput(BlockState state) {
		return (Integer)state.get(POWER);
	}

	@Override
	protected BlockState setRedstoneOutput(BlockState state, int rsOut) {
		return state.with(POWER, Integer.valueOf(rsOut));
	}

	@Override
	protected int getTickRate() {
		return 10;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(POWER);
	}
}
