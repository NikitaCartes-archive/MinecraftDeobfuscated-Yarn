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
	private List<BlockPos> field_19351 = Lists.<BlockPos>newArrayList();

	public FarmerVillagerTask() {
		super(
			ImmutableMap.of(
				MemoryModuleType.field_18446,
				MemoryModuleState.field_18457,
				MemoryModuleType.field_18445,
				MemoryModuleState.field_18457,
				MemoryModuleType.field_18873,
				MemoryModuleState.field_18456
			)
		);
	}

	protected boolean method_19564(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		if (!serverWorld.getGameRules().getBoolean(GameRules.field_19388)) {
			return false;
		} else if (villagerEntity.getVillagerData().getProfession() != VillagerProfession.field_17056) {
			return false;
		} else {
			this.field_18859 = villagerEntity.hasSeedToPlant();
			this.field_18860 = false;
			BasicInventory basicInventory = villagerEntity.getInventory();
			int i = basicInventory.getInvSize();

			for (int j = 0; j < i; j++) {
				if (basicInventory.getInvStack(j).isEmpty()) {
					this.field_18860 = true;
					break;
				}
			}

			BlockPos.Mutable mutable = new BlockPos.Mutable(villagerEntity.x, villagerEntity.y, villagerEntity.z);
			this.field_19351.clear();

			for (int k = -1; k <= 1; k++) {
				for (int l = -1; l <= 1; l++) {
					for (int m = -1; m <= 1; m++) {
						mutable.set(villagerEntity.x + (double)k, villagerEntity.y + (double)l, villagerEntity.z + (double)m);
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

	protected void method_20392(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		if (l > this.field_18861 && this.field_18858 != null) {
			villagerEntity.getBrain().putMemory(MemoryModuleType.field_18446, new BlockPosLookTarget(this.field_18858));
			villagerEntity.getBrain().putMemory(MemoryModuleType.field_18445, new WalkTarget(new BlockPosLookTarget(this.field_18858), 0.5F, 1));
		}
	}

	protected void method_19566(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		villagerEntity.getBrain().forget(MemoryModuleType.field_18446);
		villagerEntity.getBrain().forget(MemoryModuleType.field_18445);
		this.field_19239 = 0;
		this.field_18861 = l + 40L;
	}

	protected void method_19565(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		if (this.field_18858 != null && l > this.field_18861) {
			BlockState blockState = serverWorld.getBlockState(this.field_18858);
			Block block = blockState.getBlock();
			Block block2 = serverWorld.getBlockState(this.field_18858.down()).getBlock();
			if (block instanceof CropBlock && ((CropBlock)block).isMature(blockState) && this.field_18860) {
				serverWorld.breakBlock(this.field_18858, true);
			}

			if (blockState.isAir() && block2 instanceof FarmlandBlock && this.field_18859) {
				BasicInventory basicInventory = villagerEntity.getInventory();

				for (int i = 0; i < basicInventory.getInvSize(); i++) {
					ItemStack itemStack = basicInventory.getInvStack(i);
					boolean bl = false;
					if (!itemStack.isEmpty()) {
						if (itemStack.getItem() == Items.field_8317) {
							serverWorld.setBlockState(this.field_18858, Blocks.field_10293.getDefaultState(), 3);
							bl = true;
						} else if (itemStack.getItem() == Items.field_8567) {
							serverWorld.setBlockState(this.field_18858, Blocks.field_10247.getDefaultState(), 3);
							bl = true;
						} else if (itemStack.getItem() == Items.field_8179) {
							serverWorld.setBlockState(this.field_18858, Blocks.field_10609.getDefaultState(), 3);
							bl = true;
						} else if (itemStack.getItem() == Items.field_8309) {
							serverWorld.setBlockState(this.field_18858, Blocks.field_10341.getDefaultState(), 3);
							bl = true;
						}
					}

					if (bl) {
						serverWorld.playSound(
							null,
							(double)this.field_18858.getX(),
							(double)this.field_18858.getY(),
							(double)this.field_18858.getZ(),
							SoundEvents.field_17611,
							SoundCategory.field_15245,
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
					villagerEntity.getBrain().putMemory(MemoryModuleType.field_18445, new WalkTarget(new BlockPosLookTarget(this.field_18858), 0.5F, 1));
					villagerEntity.getBrain().putMemory(MemoryModuleType.field_18446, new BlockPosLookTarget(this.field_18858));
				}
			}
		}

		this.field_19239++;
	}

	protected boolean method_20394(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return this.field_19239 < 200;
	}
}
