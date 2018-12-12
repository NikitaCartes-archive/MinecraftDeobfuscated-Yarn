package net.minecraft.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.util.Tickable;

public class DaylightDetectorBlockEntity extends BlockEntity implements Tickable {
	public DaylightDetectorBlockEntity() {
		super(BlockEntityType.DAYLIGHT_DETECTOR);
	}

	@Override
	public void tick() {
		if (this.world != null && !this.world.isClient && this.world.getTime() % 20L == 0L) {
			BlockState blockState = this.getCachedState();
			Block block = blockState.getBlock();
			if (block instanceof DaylightDetectorBlock) {
				DaylightDetectorBlock.updateState(blockState, this.world, this.pos);
			}
		}
	}
}
