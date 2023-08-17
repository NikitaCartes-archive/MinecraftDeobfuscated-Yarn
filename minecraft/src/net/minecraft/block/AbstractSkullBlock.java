package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.Equipment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class AbstractSkullBlock extends BlockWithEntity implements Equipment {
	private final SkullBlock.SkullType type;

	public AbstractSkullBlock(SkullBlock.SkullType type, AbstractBlock.Settings settings) {
		super(settings);
		this.type = type;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new SkullBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		if (world.isClient) {
			boolean bl = state.isOf(Blocks.DRAGON_HEAD) || state.isOf(Blocks.DRAGON_WALL_HEAD) || state.isOf(Blocks.PIGLIN_HEAD) || state.isOf(Blocks.PIGLIN_WALL_HEAD);
			if (bl) {
				return validateTicker(type, BlockEntityType.SKULL, SkullBlockEntity::tick);
			}
		}

		return null;
	}

	public SkullBlock.SkullType getSkullType() {
		return this.type;
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}

	@Override
	public EquipmentSlot getSlotType() {
		return EquipmentSlot.HEAD;
	}
}
