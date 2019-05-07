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
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTables;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextTypes;

public class GiveGiftsToHeroTask extends Task<VillagerEntity> {
	private static final Map<VillagerProfession, Identifier> GIFTS = SystemUtil.consume(Maps.<VillagerProfession, Identifier>newHashMap(), hashMap -> {
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

	public GiveGiftsToHeroTask(int i) {
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
		villagerEntity.getBrain().putMemory(MemoryModuleType.field_18447, playerEntity);
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
		villagerEntity.getBrain().forget(MemoryModuleType.field_18447);
		villagerEntity.getBrain().forget(MemoryModuleType.field_18445);
		villagerEntity.getBrain().forget(MemoryModuleType.field_18446);
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
				LootSupplier lootSupplier = villagerEntity.world.getServer().getLootManager().getSupplier((Identifier)GIFTS.get(villagerProfession));
				LootContext.Builder builder = new LootContext.Builder((ServerWorld)villagerEntity.world)
					.put(LootContextParameters.field_1232, new BlockPos(villagerEntity))
					.put(LootContextParameters.field_1226, villagerEntity)
					.setRandom(villagerEntity.getRand());
				return lootSupplier.getDrops(builder.build(LootContextTypes.field_16235));
			} else {
				return ImmutableList.of(new ItemStack(Items.field_8317));
			}
		}
	}

	private boolean isNearestPlayerHero(VillagerEntity villagerEntity) {
		return this.getNearestPlayerIfHero(villagerEntity).isPresent();
	}

	private Optional<PlayerEntity> getNearestPlayerIfHero(VillagerEntity villagerEntity) {
		return villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.field_18444).filter(this::isHero);
	}

	private boolean isHero(PlayerEntity playerEntity) {
		return playerEntity.hasStatusEffect(StatusEffects.field_18980);
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
