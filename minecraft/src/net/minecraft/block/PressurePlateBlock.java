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
import net.minecraft.util.math.BoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class PressurePlateBlock extends AbstractPressurePlateBlock {
	public static final BooleanProperty POWERED = Properties.POWERED;
	private final PressurePlateBlock.Type type;

	protected PressurePlateBlock(PressurePlateBlock.Type type, Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(POWERED, Boolean.valueOf(false)));
		this.type = type;
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
			iWorld.playSound(null, blockPos, SoundEvents.field_14961, SoundCategory.field_15245, 0.3F, 0.8F);
		} else {
			iWorld.playSound(null, blockPos, SoundEvents.field_15217, SoundCategory.field_15245, 0.3F, 0.6F);
		}
	}

	@Override
	protected void playDepressSound(IWorld iWorld, BlockPos blockPos) {
		if (this.material == Material.WOOD) {
			iWorld.playSound(null, blockPos, SoundEvents.field_15002, SoundCategory.field_15245, 0.3F, 0.7F);
		} else {
			iWorld.playSound(null, blockPos, SoundEvents.field_15116, SoundCategory.field_15245, 0.3F, 0.5F);
		}
	}

	@Override
	protected int getRedstoneOutput(World world, BlockPos blockPos) {
		BoundingBox boundingBox = BOX.offset(blockPos);
		List<? extends Entity> list;
		switch (this.type) {
			case WOOD:
				list = world.getEntities(null, boundingBox);
				break;
			case STONE:
				list = world.getEntities(LivingEntity.class, boundingBox);
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
		builder.with(POWERED);
	}

	public static enum Type {
		WOOD,
		STONE;
	}
}
