package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEvents;

public class BoneMealTask extends MultiTickTask<VillagerEntity> {
	private static final int MAX_DURATION = 80;
	private long startTime;
	private long lastEndEntityAge;
	private int duration;
	private Optional<BlockPos> pos = Optional.empty();

	public BoneMealTask() {
		super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
	}

	protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		if (villagerEntity.age % 10 == 0 && (this.lastEndEntityAge == 0L || this.lastEndEntityAge + 160L <= (long)villagerEntity.age)) {
			if (villagerEntity.getInventory().count(Items.BONE_MEAL) <= 0) {
				return false;
			} else {
				this.pos = this.findBoneMealPos(serverWorld, villagerEntity);
				return this.pos.isPresent();
			}
		} else {
			return false;
		}
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return this.duration < 80 && this.pos.isPresent();
	}

	private Optional<BlockPos> findBoneMealPos(ServerWorld world, VillagerEntity entity) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Optional<BlockPos> optional = Optional.empty();
		int i = 0;

		for (int j = -1; j <= 1; j++) {
			for (int k = -1; k <= 1; k++) {
				for (int l = -1; l <= 1; l++) {
					mutable.set(entity.getBlockPos(), j, k, l);
					if (this.canBoneMeal(mutable, world)) {
						if (world.random.nextInt(++i) == 0) {
							optional = Optional.of(mutable.toImmutable());
						}
					}
				}
			}
		}

		return optional;
	}

	private boolean canBoneMeal(BlockPos pos, ServerWorld world) {
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		return block instanceof CropBlock && !((CropBlock)block).isMature(blockState);
	}

	protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		this.addLookWalkTargets(villagerEntity);
		villagerEntity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BONE_MEAL));
		this.startTime = l;
		this.duration = 0;
	}

	private void addLookWalkTargets(VillagerEntity villager) {
		this.pos.ifPresent(pos -> {
			BlockPosLookTarget blockPosLookTarget = new BlockPosLookTarget(pos);
			villager.getBrain().remember(MemoryModuleType.LOOK_TARGET, blockPosLookTarget);
			villager.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(blockPosLookTarget, 0.5F, 1));
		});
	}

	protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		villagerEntity.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
		this.lastEndEntityAge = (long)villagerEntity.age;
	}

	protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		BlockPos blockPos = (BlockPos)this.pos.get();
		if (l >= this.startTime && blockPos.isWithinDistance(villagerEntity.getPos(), 1.0)) {
			ItemStack itemStack = ItemStack.EMPTY;
			SimpleInventory simpleInventory = villagerEntity.getInventory();
			int i = simpleInventory.size();

			for (int j = 0; j < i; j++) {
				ItemStack itemStack2 = simpleInventory.getStack(j);
				if (itemStack2.isOf(Items.BONE_MEAL)) {
					itemStack = itemStack2;
					break;
				}
			}

			if (!itemStack.isEmpty() && BoneMealItem.useOnFertilizable(itemStack, serverWorld, blockPos)) {
				serverWorld.syncWorldEvent(WorldEvents.BONE_MEAL_USED, blockPos, 0);
				this.pos = this.findBoneMealPos(serverWorld, villagerEntity);
				this.addLookWalkTargets(villagerEntity);
				this.startTime = l + 40L;
			}

			this.duration++;
		}
	}
}
