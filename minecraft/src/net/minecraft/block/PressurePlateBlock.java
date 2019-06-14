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
	public static final BooleanProperty field_11358 = Properties.field_12484;
	private final PressurePlateBlock.ActivationRule type;

	protected PressurePlateBlock(PressurePlateBlock.ActivationRule activationRule, Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11358, Boolean.valueOf(false)));
		this.type = activationRule;
	}

	@Override
	protected int method_9435(BlockState blockState) {
		return blockState.method_11654(field_11358) ? 15 : 0;
	}

	@Override
	protected BlockState method_9432(BlockState blockState, int i) {
		return blockState.method_11657(field_11358, Boolean.valueOf(i > 0));
	}

	@Override
	protected void playPressSound(IWorld iWorld, BlockPos blockPos) {
		if (this.field_10635 == Material.WOOD) {
			iWorld.playSound(null, blockPos, SoundEvents.field_14961, SoundCategory.field_15245, 0.3F, 0.8F);
		} else {
			iWorld.playSound(null, blockPos, SoundEvents.field_15217, SoundCategory.field_15245, 0.3F, 0.6F);
		}
	}

	@Override
	protected void playDepressSound(IWorld iWorld, BlockPos blockPos) {
		if (this.field_10635 == Material.WOOD) {
			iWorld.playSound(null, blockPos, SoundEvents.field_15002, SoundCategory.field_15245, 0.3F, 0.7F);
		} else {
			iWorld.playSound(null, blockPos, SoundEvents.field_15116, SoundCategory.field_15245, 0.3F, 0.5F);
		}
	}

	@Override
	protected int getRedstoneOutput(World world, BlockPos blockPos) {
		Box box = field_9941.offset(blockPos);
		List<? extends Entity> list;
		switch (this.type) {
			case field_11361:
				list = world.method_8335(null, box);
				break;
			case field_11362:
				list = world.method_18467(LivingEntity.class, box);
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
		builder.method_11667(field_11358);
	}

	public static enum ActivationRule {
		field_11361,
		field_11362;
	}
}
