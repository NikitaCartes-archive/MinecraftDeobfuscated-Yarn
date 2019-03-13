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
	public static final BooleanProperty field_11358 = Properties.field_12484;
	private final PressurePlateBlock.Type type;

	protected PressurePlateBlock(PressurePlateBlock.Type type, Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11358, Boolean.valueOf(false)));
		this.type = type;
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
	protected void method_9436(IWorld iWorld, BlockPos blockPos) {
		if (this.field_10635 == Material.WOOD) {
			iWorld.method_8396(null, blockPos, SoundEvents.field_14961, SoundCategory.field_15245, 0.3F, 0.8F);
		} else {
			iWorld.method_8396(null, blockPos, SoundEvents.field_15217, SoundCategory.field_15245, 0.3F, 0.6F);
		}
	}

	@Override
	protected void method_9438(IWorld iWorld, BlockPos blockPos) {
		if (this.field_10635 == Material.WOOD) {
			iWorld.method_8396(null, blockPos, SoundEvents.field_15002, SoundCategory.field_15245, 0.3F, 0.7F);
		} else {
			iWorld.method_8396(null, blockPos, SoundEvents.field_15116, SoundCategory.field_15245, 0.3F, 0.5F);
		}
	}

	@Override
	protected int method_9434(World world, BlockPos blockPos) {
		BoundingBox boundingBox = field_9941.method_996(blockPos);
		List<? extends Entity> list;
		switch (this.type) {
			case WOOD:
				list = world.method_8335(null, boundingBox);
				break;
			case STONE:
				list = world.method_18467(LivingEntity.class, boundingBox);
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
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11358);
	}

	public static enum Type {
		WOOD,
		STONE;
	}
}
