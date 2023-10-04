package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PressurePlateBlock extends AbstractPressurePlateBlock {
	public static final MapCodec<PressurePlateBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(BlockSetType.CODEC.fieldOf("block_set_type").forGetter(block -> block.blockSetType), createSettingsCodec())
				.apply(instance, PressurePlateBlock::new)
	);
	public static final BooleanProperty POWERED = Properties.POWERED;

	@Override
	public MapCodec<PressurePlateBlock> getCodec() {
		return CODEC;
	}

	protected PressurePlateBlock(BlockSetType type, AbstractBlock.Settings settings) {
		super(settings, type);
		this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, Boolean.valueOf(false)));
	}

	@Override
	protected int getRedstoneOutput(BlockState state) {
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	protected BlockState setRedstoneOutput(BlockState state, int rsOut) {
		return state.with(POWERED, Boolean.valueOf(rsOut > 0));
	}

	@Override
	protected int getRedstoneOutput(World world, BlockPos pos) {
		Class<? extends Entity> class_ = switch (this.blockSetType.pressurePlateSensitivity()) {
			case EVERYTHING -> Entity.class;
			case MOBS -> LivingEntity.class;
		};
		return getEntityCount(world, BOX.offset(pos), class_) > 0 ? 15 : 0;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(POWERED);
	}
}
