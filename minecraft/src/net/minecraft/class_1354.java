package net.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

public class class_1354 extends class_1367 {
	private final VillagerEntity field_6458;
	private boolean field_6459;
	private boolean field_6457;
	private int field_6456;

	public class_1354(VillagerEntity villagerEntity, double d) {
		super(villagerEntity, d, 16);
		this.field_6458 = villagerEntity;
	}

	@Override
	public boolean canStart() {
		if (this.field_6518 <= 0) {
			if (!this.field_6458.world.getGameRules().getBoolean("mobGriefing")) {
				return false;
			}

			this.field_6456 = -1;
			this.field_6459 = this.field_6458.method_7244();
			this.field_6457 = this.field_6458.method_7239();
		}

		return super.canStart();
	}

	@Override
	public boolean shouldContinue() {
		return this.field_6456 >= 0 && super.shouldContinue();
	}

	@Override
	public void tick() {
		super.tick();
		this.field_6458
			.getLookControl()
			.lookAt(
				(double)this.field_6512.getX() + 0.5,
				(double)(this.field_6512.getY() + 1),
				(double)this.field_6512.getZ() + 0.5,
				10.0F,
				(float)this.field_6458.method_5978()
			);
		if (this.method_6295()) {
			IWorld iWorld = this.field_6458.world;
			BlockPos blockPos = this.field_6512.up();
			BlockState blockState = iWorld.getBlockState(blockPos);
			Block block = blockState.getBlock();
			if (this.field_6456 == 0 && block instanceof CropBlock && ((CropBlock)block).isValidState(blockState)) {
				iWorld.breakBlock(blockPos, true);
			} else if (this.field_6456 == 1 && blockState.isAir()) {
				BasicInventory basicInventory = this.field_6458.getInventory();

				for (int i = 0; i < basicInventory.getInvSize(); i++) {
					ItemStack itemStack = basicInventory.getInvStack(i);
					boolean bl = false;
					if (!itemStack.isEmpty()) {
						if (itemStack.getItem() == Items.field_8317) {
							iWorld.setBlockState(blockPos, Blocks.field_10293.getDefaultState(), 3);
							bl = true;
						} else if (itemStack.getItem() == Items.field_8567) {
							iWorld.setBlockState(blockPos, Blocks.field_10247.getDefaultState(), 3);
							bl = true;
						} else if (itemStack.getItem() == Items.field_8179) {
							iWorld.setBlockState(blockPos, Blocks.field_10609.getDefaultState(), 3);
							bl = true;
						} else if (itemStack.getItem() == Items.field_8309) {
							iWorld.setBlockState(blockPos, Blocks.field_10341.getDefaultState(), 3);
							bl = true;
						}
					}

					if (bl) {
						itemStack.subtractAmount(1);
						if (itemStack.isEmpty()) {
							basicInventory.setInvStack(i, ItemStack.EMPTY);
						}
						break;
					}
				}
			}

			this.field_6456 = -1;
			this.field_6518 = 10;
		}
	}

	@Override
	protected boolean method_6296(ViewableWorld viewableWorld, BlockPos blockPos) {
		Block block = viewableWorld.getBlockState(blockPos).getBlock();
		if (block == Blocks.field_10362) {
			blockPos = blockPos.up();
			BlockState blockState = viewableWorld.getBlockState(blockPos);
			block = blockState.getBlock();
			if (block instanceof CropBlock && ((CropBlock)block).isValidState(blockState) && this.field_6457 && (this.field_6456 == 0 || this.field_6456 < 0)) {
				this.field_6456 = 0;
				return true;
			}

			if (blockState.isAir() && this.field_6459 && (this.field_6456 == 1 || this.field_6456 < 0)) {
				this.field_6456 = 1;
				return true;
			}
		}

		return false;
	}
}
