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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.loot.LootTable;
import net.minecraft.world.loot.LootTables;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextTypes;

public class GiveGiftsToHeroTask extends Task<VillagerEntity> {
	private static final Map<VillagerProfession, Identifier> GIFTS = SystemUtil.consume(Maps.<VillagerProfession, Identifier>newHashMap(), hashMap -> {
		hashMap.put(VillagerProfession.ARMORER, LootTables.HERO_OF_THE_VILLAGE_ARMORER_GIFT_GAMEPLAY);
		hashMap.put(VillagerProfession.BUTCHER, LootTables.HERO_OF_THE_VILLAGE_BUTCHER_GIFT_GAMEPLAY);
		hashMap.put(VillagerProfession.CARTOGRAPHER, LootTables.HERO_OF_THE_VILLAGE_CARTOGRAPHER_GIFT_GAMEPLAY);
		hashMap.put(VillagerProfession.CLERIC, LootTables.HERO_OF_THE_VILLAGE_CLERIC_GIFT_GAMEPLAY);
		hashMap.put(VillagerProfession.FARMER, LootTables.HERO_OF_THE_VILLAGE_FARMER_GIFT_GAMEPLAY);
		hashMap.put(VillagerProfession.FISHERMAN, LootTables.HERO_OF_THE_VILLAGE_FISHERMAN_GIFT_GAMEPLAY);
		hashMap.put(VillagerProfession.FLETCHER, LootTables.HERO_OF_THE_VILLAGE_FLETCHER_GIFT_GAMEPLAY);
		hashMap.put(VillagerProfession.LEATHERWORKER, LootTables.HERO_OF_THE_VILLAGE_LEATHERWORKER_GIFT_GAMEPLAY);
		hashMap.put(VillagerProfession.LIBRARIAN, LootTables.HERO_OF_THE_VILLAGE_LIBRARIAN_GIFT_GAMEPLAY);
		hashMap.put(VillagerProfession.MASON, LootTables.HERO_OF_THE_VILLAGE_MASON_GIFT_GAMEPLAY);
		hashMap.put(VillagerProfession.SHEPHERD, LootTables.HERO_OF_THE_VILLAGE_SHEPHERD_GIFT_GAMEPLAY);
		hashMap.put(VillagerProfession.TOOLSMITH, LootTables.HERO_OF_THE_VILLAGE_TOOLSMITH_GIFT_GAMEPLAY);
		hashMap.put(VillagerProfession.WEAPONSMITH, LootTables.HERO_OF_THE_VILLAGE_WEAPONSMITH_GIFT_GAMEPLAY);
	});
	private int ticksLeft = 600;
	private boolean done;
	private long startTime;

	public GiveGiftsToHeroTask(int i) {
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
			i
		);
	}

	protected boolean method_19962(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		if (!this.isNearestPlayerHero(villagerEntity)) {
			return false;
		} else if (this.ticksLeft > 0) {
			this.ticksLeft--;
			return false;
		} else {
			return true;
		}
	}

	protected void method_19963(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		this.done = false;
		this.startTime = l;
		PlayerEntity playerEntity = (PlayerEntity)this.getNearestPlayerIfHero(villagerEntity).get();
		villagerEntity.getBrain().putMemory(MemoryModuleType.INTERACTION_TARGET, playerEntity);
		LookTargetUtil.lookAt(villagerEntity, playerEntity);
	}

	protected boolean method_19965(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return this.isNearestPlayerHero(villagerEntity) && !this.done;
	}

	protected void method_19967(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		PlayerEntity playerEntity = (PlayerEntity)this.getNearestPlayerIfHero(villagerEntity).get();
		LookTargetUtil.lookAt(villagerEntity, playerEntity);
		if (this.isCloseEnough(villagerEntity, playerEntity)) {
			if (l - this.startTime > 20L) {
				this.giveGifts(villagerEntity, playerEntity);
				this.done = true;
			}
		} else {
			LookTargetUtil.walkTowards(villagerEntity, playerEntity, 5);
		}
	}

	protected void method_19968(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		this.ticksLeft = getNextGiftDelay(serverWorld);
		villagerEntity.getBrain().forget(MemoryModuleType.INTERACTION_TARGET);
		villagerEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
		villagerEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
	}

	private void giveGifts(VillagerEntity villagerEntity, LivingEntity livingEntity) {
		for (ItemStack itemStack : this.getGifts(villagerEntity)) {
			LookTargetUtil.give(villagerEntity, itemStack, livingEntity);
		}
	}

	private List<ItemStack> getGifts(VillagerEntity villagerEntity) {
		if (villagerEntity.isBaby()) {
			return ImmutableList.of(new ItemStack(Items.POPPY));
		} else {
			VillagerProfession villagerProfession = villagerEntity.getVillagerData().getProfession();
			if (GIFTS.containsKey(villagerProfession)) {
				LootTable lootTable = villagerEntity.world.getServer().getLootManager().getSupplier((Identifier)GIFTS.get(villagerProfession));
				LootContext.Builder builder = new LootContext.Builder((ServerWorld)villagerEntity.world)
					.put(LootContextParameters.POSITION, new BlockPos(villagerEntity))
					.put(LootContextParameters.THIS_ENTITY, villagerEntity)
					.setRandom(villagerEntity.getRandom());
				return lootTable.getDrops(builder.build(LootContextTypes.GIFT));
			} else {
				return ImmutableList.of(new ItemStack(Items.WHEAT_SEEDS));
			}
		}
	}

	private boolean isNearestPlayerHero(VillagerEntity villagerEntity) {
		return this.getNearestPlayerIfHero(villagerEntity).isPresent();
	}

	private Optional<PlayerEntity> getNearestPlayerIfHero(VillagerEntity villagerEntity) {
		return villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).filter(this::isHero);
	}

	private boolean isHero(PlayerEntity playerEntity) {
		return playerEntity.hasStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE);
	}

	private boolean isCloseEnough(VillagerEntity villagerEntity, PlayerEntity playerEntity) {
		BlockPos blockPos = new BlockPos(playerEntity);
		BlockPos blockPos2 = new BlockPos(villagerEntity);
		return blockPos2.isWithinDistance(blockPos, 5.0);
	}

	private static int getNextGiftDelay(ServerWorld serverWorld) {
		return 600 + serverWorld.random.nextInt(6001);
	}
}
