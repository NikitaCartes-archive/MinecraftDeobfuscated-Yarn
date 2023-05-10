package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;

public class GiveGiftsToHeroTask extends MultiTickTask<VillagerEntity> {
	private static final int MAX_DISTANCE = 5;
	private static final int DEFAULT_DURATION = 600;
	private static final int MAX_NEXT_GIFT_DELAY = 6600;
	private static final int RUN_TIME = 20;
	private static final Map<VillagerProfession, Identifier> GIFTS = Util.make(Maps.<VillagerProfession, Identifier>newHashMap(), gifts -> {
		gifts.put(VillagerProfession.ARMORER, LootTables.HERO_OF_THE_VILLAGE_ARMORER_GIFT_GAMEPLAY);
		gifts.put(VillagerProfession.BUTCHER, LootTables.HERO_OF_THE_VILLAGE_BUTCHER_GIFT_GAMEPLAY);
		gifts.put(VillagerProfession.CARTOGRAPHER, LootTables.HERO_OF_THE_VILLAGE_CARTOGRAPHER_GIFT_GAMEPLAY);
		gifts.put(VillagerProfession.CLERIC, LootTables.HERO_OF_THE_VILLAGE_CLERIC_GIFT_GAMEPLAY);
		gifts.put(VillagerProfession.FARMER, LootTables.HERO_OF_THE_VILLAGE_FARMER_GIFT_GAMEPLAY);
		gifts.put(VillagerProfession.FISHERMAN, LootTables.HERO_OF_THE_VILLAGE_FISHERMAN_GIFT_GAMEPLAY);
		gifts.put(VillagerProfession.FLETCHER, LootTables.HERO_OF_THE_VILLAGE_FLETCHER_GIFT_GAMEPLAY);
		gifts.put(VillagerProfession.LEATHERWORKER, LootTables.HERO_OF_THE_VILLAGE_LEATHERWORKER_GIFT_GAMEPLAY);
		gifts.put(VillagerProfession.LIBRARIAN, LootTables.HERO_OF_THE_VILLAGE_LIBRARIAN_GIFT_GAMEPLAY);
		gifts.put(VillagerProfession.MASON, LootTables.HERO_OF_THE_VILLAGE_MASON_GIFT_GAMEPLAY);
		gifts.put(VillagerProfession.SHEPHERD, LootTables.HERO_OF_THE_VILLAGE_SHEPHERD_GIFT_GAMEPLAY);
		gifts.put(VillagerProfession.TOOLSMITH, LootTables.HERO_OF_THE_VILLAGE_TOOLSMITH_GIFT_GAMEPLAY);
		gifts.put(VillagerProfession.WEAPONSMITH, LootTables.HERO_OF_THE_VILLAGE_WEAPONSMITH_GIFT_GAMEPLAY);
	});
	private static final float WALK_SPEED = 0.5F;
	private int ticksLeft = 600;
	private boolean done;
	private long startTime;

	public GiveGiftsToHeroTask(int delay) {
		super(
			ImmutableMap.of(
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.INTERACTION_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.NEAREST_VISIBLE_PLAYER,
				MemoryModuleState.VALUE_PRESENT
			),
			delay
		);
	}

	protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		if (!this.isNearestPlayerHero(villagerEntity)) {
			return false;
		} else if (this.ticksLeft > 0) {
			this.ticksLeft--;
			return false;
		} else {
			return true;
		}
	}

	protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		this.done = false;
		this.startTime = l;
		PlayerEntity playerEntity = (PlayerEntity)this.getNearestPlayerIfHero(villagerEntity).get();
		villagerEntity.getBrain().remember(MemoryModuleType.INTERACTION_TARGET, playerEntity);
		LookTargetUtil.lookAt(villagerEntity, playerEntity);
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return this.isNearestPlayerHero(villagerEntity) && !this.done;
	}

	protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		PlayerEntity playerEntity = (PlayerEntity)this.getNearestPlayerIfHero(villagerEntity).get();
		LookTargetUtil.lookAt(villagerEntity, playerEntity);
		if (this.isCloseEnough(villagerEntity, playerEntity)) {
			if (l - this.startTime > 20L) {
				this.giveGifts(villagerEntity, playerEntity);
				this.done = true;
			}
		} else {
			LookTargetUtil.walkTowards(villagerEntity, playerEntity, 0.5F, 5);
		}
	}

	protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		this.ticksLeft = getNextGiftDelay(serverWorld);
		villagerEntity.getBrain().forget(MemoryModuleType.INTERACTION_TARGET);
		villagerEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
		villagerEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
	}

	private void giveGifts(VillagerEntity villager, LivingEntity recipient) {
		for (ItemStack itemStack : this.getGifts(villager)) {
			LookTargetUtil.give(villager, itemStack, recipient.getPos());
		}
	}

	private List<ItemStack> getGifts(VillagerEntity villager) {
		if (villager.isBaby()) {
			return ImmutableList.of(new ItemStack(Items.POPPY));
		} else {
			VillagerProfession villagerProfession = villager.getVillagerData().getProfession();
			if (GIFTS.containsKey(villagerProfession)) {
				LootTable lootTable = villager.getWorld().getServer().getLootManager().getLootTable((Identifier)GIFTS.get(villagerProfession));
				LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder((ServerWorld)villager.getWorld())
					.add(LootContextParameters.ORIGIN, villager.getPos())
					.add(LootContextParameters.THIS_ENTITY, villager)
					.build(LootContextTypes.GIFT);
				return lootTable.generateLoot(lootContextParameterSet);
			} else {
				return ImmutableList.of(new ItemStack(Items.WHEAT_SEEDS));
			}
		}
	}

	private boolean isNearestPlayerHero(VillagerEntity villager) {
		return this.getNearestPlayerIfHero(villager).isPresent();
	}

	private Optional<PlayerEntity> getNearestPlayerIfHero(VillagerEntity villager) {
		return villager.getBrain().getOptionalRegisteredMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).filter(this::isHero);
	}

	private boolean isHero(PlayerEntity player) {
		return player.hasStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE);
	}

	private boolean isCloseEnough(VillagerEntity villager, PlayerEntity player) {
		BlockPos blockPos = player.getBlockPos();
		BlockPos blockPos2 = villager.getBlockPos();
		return blockPos2.isWithinDistance(blockPos, 5.0);
	}

	private static int getNextGiftDelay(ServerWorld world) {
		return 600 + world.random.nextInt(6001);
	}
}
