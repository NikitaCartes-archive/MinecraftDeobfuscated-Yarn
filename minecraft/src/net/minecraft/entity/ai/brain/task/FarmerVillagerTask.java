package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
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
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;

public class FarmerVillagerTask extends Task<VillagerEntity> {
	@Nullable
	private BlockPos field_18858;
	private boolean field_18859;
	private boolean field_18860;
	private long field_18861;

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(
			Pair.of(MemoryModuleType.field_18446, MemoryModuleState.field_18457), Pair.of(MemoryModuleType.field_18873, MemoryModuleState.field_18456)
		);
	}

	protected boolean method_19564(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		if (!serverWorld.getGameRules().getBoolean("mobGriefing")) {
			return false;
		} else if (villagerEntity.getVillagerData().getProfession() != VillagerProfession.field_17056) {
			return false;
		} else {
			Set<BlockPos> set = (Set<BlockPos>)((List)villagerEntity.getBrain().getMemory(MemoryModuleType.field_18873).get())
				.stream()
				.map(GlobalPos::getPos)
				.collect(Collectors.toSet());
			BlockPos blockPos = new BlockPos(villagerEntity);
			List<BlockPos> list = (List<BlockPos>)ImmutableList.of(blockPos.down(), blockPos.south(), blockPos.north(), blockPos.east(), blockPos.west())
				.stream()
				.filter(set::contains)
				.collect(Collectors.toList());
			if (!list.isEmpty()) {
				this.field_18858 = (BlockPos)list.get(serverWorld.getRandom().nextInt(list.size()));
				this.field_18859 = villagerEntity.method_19623();
				this.field_18860 = villagerEntity.canBreed();
				return true;
			} else {
				return false;
			}
		}
	}

	protected void method_19565(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		if (l > this.field_18861 && this.field_18858 != null) {
			villagerEntity.getBrain().putMemory(MemoryModuleType.field_18446, new BlockPosLookTarget(this.field_18858));
			BlockPos blockPos = this.field_18858.up();
			BlockState blockState = serverWorld.getBlockState(blockPos);
			Block block = blockState.getBlock();
			if (block instanceof CropBlock && ((CropBlock)block).isValidState(blockState) && this.field_18860) {
				serverWorld.breakBlock(blockPos, true);
			} else if (blockState.isAir() && this.field_18859) {
				BasicInventory basicInventory = villagerEntity.getInventory();

				for (int i = 0; i < basicInventory.getInvSize(); i++) {
					ItemStack itemStack = basicInventory.getInvStack(i);
					boolean bl = false;
					if (!itemStack.isEmpty()) {
						if (itemStack.getItem() == Items.field_8317) {
							serverWorld.setBlockState(blockPos, Blocks.field_10293.getDefaultState(), 3);
							bl = true;
						} else if (itemStack.getItem() == Items.field_8567) {
							serverWorld.setBlockState(blockPos, Blocks.field_10247.getDefaultState(), 3);
							bl = true;
						} else if (itemStack.getItem() == Items.field_8179) {
							serverWorld.setBlockState(blockPos, Blocks.field_10609.getDefaultState(), 3);
							bl = true;
						} else if (itemStack.getItem() == Items.field_8309) {
							serverWorld.setBlockState(blockPos, Blocks.field_10341.getDefaultState(), 3);
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

			this.field_18861 = l + 20L;
		}
	}

	protected void method_19566(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		villagerEntity.getBrain().forget(MemoryModuleType.field_18446);
	}
}
