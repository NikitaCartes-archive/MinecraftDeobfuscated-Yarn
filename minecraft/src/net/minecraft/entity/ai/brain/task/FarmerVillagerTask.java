package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.GameRules;

public class FarmerVillagerTask extends Task<VillagerEntity> {
	@Nullable
	private BlockPos field_18858;
	private boolean field_18859;
	private boolean field_18860;
	private long field_18861;
	private int field_19239;
	private final List<BlockPos> field_19351 = Lists.<BlockPos>newArrayList();

	public FarmerVillagerTask() {
		super(
			ImmutableMap.of(
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.SECONDARY_JOB_SITE,
				MemoryModuleState.VALUE_PRESENT
			)
		);
	}

	protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		if (!serverWorld.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
			return false;
		} else if (villagerEntity.getVillagerData().getProfession() != VillagerProfession.FARMER) {
			return false;
		} else {
			this.field_18859 = villagerEntity.hasSeedToPlant();
			this.field_18860 = false;
			BasicInventory basicInventory = villagerEntity.getInventory();
			int i = basicInventory.getInvSize();

			for (int j = 0; j < i; j++) {
				ItemStack itemStack = basicInventory.getInvStack(j);
				if (itemStack.isEmpty()) {
					this.field_18860 = true;
					break;
				}

				if (itemStack.getItem() == Items.WHEAT_SEEDS || itemStack.getItem() == Items.BEETROOT_SEEDS) {
					this.field_18860 = true;
					break;
				}
			}

			BlockPos.Mutable mutable = new BlockPos.Mutable(villagerEntity);
			this.field_19351.clear();

			for (int k = -1; k <= 1; k++) {
				for (int l = -1; l <= 1; l++) {
					for (int m = -1; m <= 1; m++) {
						mutable.set(villagerEntity.getX() + (double)k, villagerEntity.getY() + (double)l, villagerEntity.getZ() + (double)m);
						if (this.method_20640(mutable, serverWorld)) {
							this.field_19351.add(new BlockPos(mutable));
						}
					}
				}
			}

			this.field_18858 = this.method_20641(serverWorld);
			return (this.field_18859 || this.field_18860) && this.field_18858 != null;
		}
	}

	@Nullable
	private BlockPos method_20641(ServerWorld serverWorld) {
		return this.field_19351.isEmpty() ? null : (BlockPos)this.field_19351.get(serverWorld.getRandom().nextInt(this.field_19351.size()));
	}

	private boolean method_20640(BlockPos blockPos, ServerWorld serverWorld) {
		BlockState blockState = serverWorld.getBlockState(blockPos);
		Block block = blockState.getBlock();
		Block block2 = serverWorld.getBlockState(blockPos.down()).getBlock();
		return block instanceof CropBlock && ((CropBlock)block).isMature(blockState) && this.field_18860
			|| blockState.isAir() && block2 instanceof FarmlandBlock && this.field_18859;
	}

	protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		if (l > this.field_18861 && this.field_18858 != null) {
			villagerEntity.getBrain().putMemory(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.field_18858));
			villagerEntity.getBrain().putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.field_18858), 0.5F, 1));
		}
	}

	protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		villagerEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
		villagerEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
		this.field_19239 = 0;
		this.field_18861 = l + 40L;
	}

	protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		if (this.field_18858 != null && l > this.field_18861) {
			BlockState blockState = serverWorld.getBlockState(this.field_18858);
			Block block = blockState.getBlock();
			Block block2 = serverWorld.getBlockState(this.field_18858.down()).getBlock();
			if (block instanceof CropBlock && ((CropBlock)block).isMature(blockState) && this.field_18860) {
				serverWorld.breakBlock(this.field_18858, true, villagerEntity);
			}

			if (blockState.isAir() && block2 instanceof FarmlandBlock && this.field_18859) {
				BasicInventory basicInventory = villagerEntity.getInventory();

				for (int i = 0; i < basicInventory.getInvSize(); i++) {
					ItemStack itemStack = basicInventory.getInvStack(i);
					boolean bl = false;
					if (!itemStack.isEmpty()) {
						if (itemStack.getItem() == Items.WHEAT_SEEDS) {
							serverWorld.setBlockState(this.field_18858, Blocks.WHEAT.getDefaultState(), 3);
							bl = true;
						} else if (itemStack.getItem() == Items.POTATO) {
							serverWorld.setBlockState(this.field_18858, Blocks.POTATOES.getDefaultState(), 3);
							bl = true;
						} else if (itemStack.getItem() == Items.CARROT) {
							serverWorld.setBlockState(this.field_18858, Blocks.CARROTS.getDefaultState(), 3);
							bl = true;
						} else if (itemStack.getItem() == Items.BEETROOT_SEEDS) {
							serverWorld.setBlockState(this.field_18858, Blocks.BEETROOTS.getDefaultState(), 3);
							bl = true;
						}
					}

					if (bl) {
						serverWorld.playSound(
							null,
							(double)this.field_18858.getX(),
							(double)this.field_18858.getY(),
							(double)this.field_18858.getZ(),
							SoundEvents.ITEM_CROP_PLANT,
							SoundCategory.BLOCKS,
							1.0F,
							1.0F
						);
						itemStack.decrement(1);
						if (itemStack.isEmpty()) {
							basicInventory.setInvStack(i, ItemStack.EMPTY);
						}
						break;
					}
				}
			}

			if (block instanceof CropBlock && !((CropBlock)block).isMature(blockState)) {
				this.field_19351.remove(this.field_18858);
				this.field_18858 = this.method_20641(serverWorld);
				if (this.field_18858 != null) {
					this.field_18861 = l + 20L;
					villagerEntity.getBrain().putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.field_18858), 0.5F, 1));
					villagerEntity.getBrain().putMemory(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.field_18858));
				}
			}
		}

		this.field_19239++;
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return this.field_19239 < 200;
	}
}
