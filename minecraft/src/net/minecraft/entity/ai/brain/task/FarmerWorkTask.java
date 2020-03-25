package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;

public class FarmerWorkTask extends VillagerWorkTask {
	private static final List<Item> COMPOSTABLES = ImmutableList.of(Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS);

	@Override
	protected void performAdditionalWork(ServerWorld world, VillagerEntity entity) {
		Optional<GlobalPos> optional = entity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE);
		if (optional.isPresent()) {
			GlobalPos globalPos = (GlobalPos)optional.get();
			BlockState blockState = world.getBlockState(globalPos.getPos());
			Block block = blockState.getBlock();
			if (block == Blocks.COMPOSTER) {
				this.craftAndDropBread(entity);
				this.compostSeeds(world, entity, globalPos, blockState);
			}
		}
	}

	private void compostSeeds(ServerWorld world, VillagerEntity entity, GlobalPos pos, BlockState composterState) {
		if ((Integer)composterState.get(ComposterBlock.LEVEL) == 8) {
			composterState = ComposterBlock.emptyFullComposter(composterState, world, pos.getPos());
		}

		int i = 20;
		int j = 10;
		int[] is = new int[COMPOSTABLES.size()];
		BasicInventory basicInventory = entity.getInventory();
		int k = basicInventory.getInvSize();

		for (int l = k - 1; l >= 0 && i > 0; l--) {
			ItemStack itemStack = basicInventory.getInvStack(l);
			int m = COMPOSTABLES.indexOf(itemStack.getItem());
			if (m != -1) {
				int n = itemStack.getCount();
				int o = is[m] + n;
				is[m] = o;
				int p = Math.min(Math.min(o - 10, i), n);
				if (p > 0) {
					i -= p;

					for (int q = 0; q < p; q++) {
						composterState = ComposterBlock.compost(composterState, world, itemStack, pos.getPos());
						if ((Integer)composterState.get(ComposterBlock.LEVEL) == 7) {
							return;
						}
					}
				}
			}
		}
	}

	private void craftAndDropBread(VillagerEntity entity) {
		BasicInventory basicInventory = entity.getInventory();
		if (basicInventory.countInInv(Items.BREAD) <= 36) {
			int i = basicInventory.countInInv(Items.WHEAT);
			int j = 3;
			int k = 3;
			int l = Math.min(3, i / 3);
			if (l != 0) {
				int m = l * 3;
				basicInventory.poll(Items.WHEAT, m);
				ItemStack itemStack = basicInventory.add(new ItemStack(Items.BREAD, l));
				if (!itemStack.isEmpty()) {
					entity.dropStack(itemStack, 0.5F);
				}
			}
		}
	}
}
