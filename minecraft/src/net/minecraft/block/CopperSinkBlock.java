package net.minecraft.block;

import net.minecraft.class_8293;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CopperSinkBlock extends AbstractCauldronBlock {
	public CopperSinkBlock(AbstractBlock.Settings settings) {
		super(settings, CauldronBehavior.EMPTY_COPPER_SINK_BEHAVIOR);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (class_8293.field_43664.method_50116()) {
			if (entity.getY() < (double)pos.getY() + 0.375
				&& entity.getBoundingBox().maxY > (double)pos.getY() + 0.25
				&& entity instanceof ItemEntity itemEntity
				&& itemEntity.getStack().isIn(ItemTags.COPPER)) {
				itemEntity.kill();
			}
		}
	}

	@Override
	public boolean isFull(BlockState state) {
		return false;
	}
}
