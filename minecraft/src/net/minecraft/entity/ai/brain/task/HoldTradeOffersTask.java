package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_7317;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TradeOffer;

public class HoldTradeOffersTask extends Task<VillagerEntity> {
	private static final int RUN_INTERVAL = 900;
	private static final int OFFER_SHOWING_INTERVAL = 40;
	public static final int field_38512 = 100;
	@Nullable
	private class_7317 field_38513;
	private final List<TradeOffer> field_38514 = Lists.<TradeOffer>newArrayList();
	private int offerShownTicks;
	private int offerIndex;
	private int ticksLeft;

	public HoldTradeOffersTask(int minRunTime, int maxRunTime) {
		super(ImmutableMap.of(MemoryModuleType.NEAREST_PLAYERS, MemoryModuleState.VALUE_PRESENT), minRunTime, maxRunTime);
	}

	public boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		Brain<?> brain = villagerEntity.getBrain();
		if (!brain.getOptionalMemory(MemoryModuleType.NEAREST_PLAYERS).isPresent()) {
			return false;
		} else if (villagerEntity.isAlive() && !villagerEntity.isBaby()) {
			for (PlayerEntity playerEntity : (List)brain.getOptionalMemory(MemoryModuleType.NEAREST_PLAYERS).get()) {
				if (playerEntity.isAlive() && playerEntity.getPos().isInRange(villagerEntity.getPos(), 6.0)) {
					class_7317 lv = playerEntity.method_42802();
					if (lv != null && this.method_42810(villagerEntity, lv)) {
						villagerEntity.getBrain().remember(MemoryModuleType.INTERACTION_TARGET, playerEntity);
						return true;
					}
				}
			}

			return false;
		} else {
			return false;
		}
	}

	public boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return this.shouldRun(serverWorld, villagerEntity)
			&& this.ticksLeft > 0
			&& villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
	}

	public void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		super.run(serverWorld, villagerEntity, l);
		this.findPotentialCustomer(villagerEntity);
		this.offerShownTicks = 0;
		this.offerIndex = 0;
		this.ticksLeft = 40;
	}

	public void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		LivingEntity livingEntity = this.findPotentialCustomer(villagerEntity);
		this.setupOffers(livingEntity, villagerEntity);
		if (!this.field_38514.isEmpty()) {
			this.refreshShownOffer(villagerEntity);
		} else {
			villagerEntity.method_42830(null);
			this.ticksLeft = Math.min(this.ticksLeft, 40);
		}

		this.ticksLeft--;
	}

	public void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		super.finishRunning(serverWorld, villagerEntity, l);
		villagerEntity.getBrain().forget(MemoryModuleType.INTERACTION_TARGET);
		villagerEntity.method_42830(null);
		this.field_38513 = null;
	}

	private void setupOffers(LivingEntity customer, VillagerEntity villager) {
		boolean bl = false;
		class_7317 lv = customer.method_42802();
		if (this.field_38513 == null || lv == null || !lv.method_42843(this.field_38513)) {
			this.field_38513 = lv;
			bl = true;
			this.field_38514.clear();
		}

		if (bl && this.field_38513 != null) {
			this.loadPossibleOffers(villager, this.field_38513);
			if (!this.field_38514.isEmpty()) {
				this.ticksLeft = 900;
				this.holdOffer(villager);
			}
		}
	}

	private void holdOffer(VillagerEntity villagerEntity) {
		TradeOffer tradeOffer = (TradeOffer)this.field_38514.get(0);
		villagerEntity.method_42830(tradeOffer);
	}

	private void loadPossibleOffers(VillagerEntity villager, class_7317 arg) {
		method_42811(villager, arg).forEach(this.field_38514::add);
		Collections.shuffle(this.field_38514);
	}

	private boolean method_42810(VillagerEntity villagerEntity, class_7317 arg) {
		return !Iterables.isEmpty(method_42811(villagerEntity, arg));
	}

	private static Iterable<TradeOffer> method_42811(VillagerEntity villagerEntity, class_7317 arg) {
		return Iterables.filter(villagerEntity.getOffers(), tradeOffer -> !tradeOffer.isDisabled() && tradeOffer.method_42853(arg));
	}

	private LivingEntity findPotentialCustomer(VillagerEntity villager) {
		Brain<?> brain = villager.getBrain();
		LivingEntity livingEntity = (LivingEntity)brain.getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).get();
		brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(livingEntity, true));
		return livingEntity;
	}

	private void refreshShownOffer(VillagerEntity villager) {
		if (this.field_38514.size() >= 2 && ++this.offerShownTicks >= 100) {
			this.offerIndex++;
			this.offerShownTicks = 0;
			if (this.offerIndex > this.field_38514.size() - 1) {
				this.offerIndex = 0;
			}

			villager.method_42830((TradeOffer)this.field_38514.get(this.offerIndex));
		}
	}
}
