package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
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
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;

public class FarmerVillagerTask extends Task<VillagerEntity> {
	@Nullable
	private BlockPos field_18858;
	private boolean field_18859;
	private boolean field_18860;
	private long field_18861;
	private int field_19239;

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
		if (!serverWorld.getGameRules().getBoolean("mobGriefing")) {
			return false;
		} else if (villagerEntity.getVillagerData().getProfession() != VillagerProfession.field_17056) {
			return false;
		} else {
			Set<BlockPos> set = (Set<BlockPos>)((List)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.field_18873).get())
				.stream()
				.map(GlobalPos::getPos)
				.collect(Collectors.toSet());
			BlockPos blockPos = new BlockPos(villagerEntity);
			List<BlockPos> list = (List<BlockPos>)ImmutableList.of(blockPos.down(), blockPos.south(), blockPos.north(), blockPos.east(), blockPos.west())
				.stream()
				.filter(set::contains)
				.collect(Collectors.toList());
			this.field_18859 = villagerEntity.hasSeedToPlant();
			this.field_18860 = villagerEntity.canBreed();
			List<BlockPos> list2 = (List<BlockPos>)list.stream()
				.map(BlockPos::up)
				.filter(blockPosx -> this.method_20391(serverWorld.getBlockState(blockPosx)))
				.collect(Collectors.toList());
			if (!list2.isEmpty()) {
				this.field_18858 = (BlockPos)list2.get(serverWorld.getRandom().nextInt(list2.size()));
				return true;
			} else {
				return false;
			}
		}
	}

	private boolean method_20391(BlockState blockState) {
		Block block = blockState.getBlock();
		return block instanceof CropBlock && ((CropBlock)block).isMature(blockState) && this.field_18860 || blockState.isAir() && this.field_18859;
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
		if (this.field_19239 > 15 && this.field_18858 != null && l > this.field_18861) {
			BlockState blockState = serverWorld.getBlockState(this.field_18858);
			Block block = blockState.getBlock();
			if (block instanceof CropBlock && ((CropBlock)block).isMature(blockState) && this.field_18860) {
				serverWorld.breakBlock(this.field_18858, true);
			} else if (blockState.isAir() && this.field_18859) {
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
						itemStack.subtractAmount(1);
						if (itemStack.isEmpty()) {
							basicInventory.setInvStack(i, ItemStack.EMPTY);
						}
						break;
					}
				}
			}
		}

		this.field_19239++;
	}

	protected boolean method_20394(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return this.field_19239 < 30;
	}
}
