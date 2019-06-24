package net.minecraft.block;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class PressurePlateBlock extends AbstractPressurePlateBlock {
	public static final BooleanProperty POWERED = Properties.POWERED;
	private final PressurePlateBlock.ActivationRule type;

	protected PressurePlateBlock(PressurePlateBlock.ActivationRule activationRule, Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(POWERED, Boolean.valueOf(false)));
		this.type = activationRule;
	}

	@Override
	protected int getRedstoneOutput(BlockState blockState) {
		return blockState.get(POWERED) ? 15 : 0;
	}

	@Override
	protected BlockState setRedstoneOutput(BlockState blockState, int i) {
		return blockState.with(POWERED, Boolean.valueOf(i > 0));
	}

	@Override
	protected void playPressSound(IWorld iWorld, BlockPos blockPos) {
		if (this.material == Material.WOOD) {
			iWorld.playSound(null, blockPos, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.8F);
		} else {
			iWorld.playSound(null, blockPos, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
		}
	}

	@Override
	protected void playDepressSound(IWorld iWorld, BlockPos blockPos) {
		if (this.material == Material.WOOD) {
			iWorld.playSound(null, blockPos, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.7F);
		} else {
			iWorld.playSound(null, blockPos, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
		}
	}

	@Override
	protected int getRedstoneOutput(World world, BlockPos blockPos) {
		Box box = BOX.offset(blockPos);
		List<? extends Entity> list;
		switch (this.type) {
			case EVERYTHING:
				list = world.getEntities(null, box);
				break;
			case MOBS:
				list = world.getEntities(LivingEntity.class, box);
				break;
			default:
				return 0;
		}

		if (!list.isEmpty()) {
			for (Entity entity : list) {
				if (!entity.canAvoidTraps()) {
					return 15;
				}
			}
		}

		return 0;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(POWERED);
	}

	public static enum ActivationRule {
		EVERYTHING,
		MOBS;
	}
}
