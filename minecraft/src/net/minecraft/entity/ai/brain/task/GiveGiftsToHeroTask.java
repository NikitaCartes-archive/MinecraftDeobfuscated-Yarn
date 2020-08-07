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
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;

public class GiveGiftsToHeroTask extends Task<VillagerEntity> {
	private static final Map<VillagerProfession, Identifier> GIFTS = Util.make(Maps.<VillagerProfession, Identifier>newHashMap(), hashMap -> {
		hashMap.put(VillagerProfession.field_17052, LootTables.field_19062);
		hashMap.put(VillagerProfession.field_17053, LootTables.field_19063);
		hashMap.put(VillagerProfession.field_17054, LootTables.field_19064);
		hashMap.put(VillagerProfession.field_17055, LootTables.field_19065);
		hashMap.put(VillagerProfession.field_17056, LootTables.field_19066);
		hashMap.put(VillagerProfession.field_17057, LootTables.field_19067);
		hashMap.put(VillagerProfession.field_17058, LootTables.field_19068);
		hashMap.put(VillagerProfession.field_17059, LootTables.field_19069);
		hashMap.put(VillagerProfession.field_17060, LootTables.field_19070);
		hashMap.put(VillagerProfession.field_17061, LootTables.field_19071);
		hashMap.put(VillagerProfession.field_17063, LootTables.field_19072);
		hashMap.put(VillagerProfession.field_17064, LootTables.field_19073);
		hashMap.put(VillagerProfession.field_17065, LootTables.field_19074);
	});
	private int ticksLeft = 600;
	private boolean done;
	private long startTime;

	public GiveGiftsToHeroTask(int delay) {
		super(
			ImmutableMap.of(
				MemoryModuleType.field_18445,
				MemoryModuleState.field_18458,
				MemoryModuleType.field_18446,
				MemoryModuleState.field_18458,
				MemoryModuleType.field_18447,
				MemoryModuleState.field_18458,
				MemoryModuleType.field_18444,
				MemoryModuleState.field_18456
			),
			delay
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
		villagerEntity.getBrain().remember(MemoryModuleType.field_18447, playerEntity);
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
			LookTargetUtil.walkTowards(villagerEntity, playerEntity, 0.5F, 5);
		}
	}

	protected void method_19968(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		this.ticksLeft = getNextGiftDelay(serverWorld);
		villagerEntity.getBrain().forget(MemoryModuleType.field_18447);
		villagerEntity.getBrain().forget(MemoryModuleType.field_18445);
		villagerEntity.getBrain().forget(MemoryModuleType.field_18446);
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
				LootTable lootTable = villager.world.getServer().getLootManager().getTable((Identifier)GIFTS.get(villagerProfession));
				LootContext.Builder builder = new LootContext.Builder((ServerWorld)villager.world)
					.parameter(LootContextParameters.field_24424, villager.getPos())
					.parameter(LootContextParameters.field_1226, villager)
					.random(villager.getRandom());
				return lootTable.generateLoot(builder.build(LootContextTypes.field_16235));
			} else {
				return ImmutableList.of(new ItemStack(Items.field_8317));
			}
		}
	}

	private boolean isNearestPlayerHero(VillagerEntity villager) {
		return this.getNearestPlayerIfHero(villager).isPresent();
	}

	private Optional<PlayerEntity> getNearestPlayerIfHero(VillagerEntity villager) {
		return villager.getBrain().getOptionalMemory(MemoryModuleType.field_18444).filter(this::isHero);
	}

	private boolean isHero(PlayerEntity player) {
		return player.hasStatusEffect(StatusEffects.field_18980);
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
