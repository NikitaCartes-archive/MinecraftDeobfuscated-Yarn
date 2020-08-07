package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.Durations;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.AdmireItemTask;
import net.minecraft.entity.ai.brain.task.AdmireItemTimeLimitTask;
import net.minecraft.entity.ai.brain.task.AttackTask;
import net.minecraft.entity.ai.brain.task.ConditionalTask;
import net.minecraft.entity.ai.brain.task.CrossbowAttackTask;
import net.minecraft.entity.ai.brain.task.DefeatTargetTask;
import net.minecraft.entity.ai.brain.task.FindEntityTask;
import net.minecraft.entity.ai.brain.task.FindInteractionTargetTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.ForgetAngryAtTargetTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.ForgetTask;
import net.minecraft.entity.ai.brain.task.GoToCelebrateTask;
import net.minecraft.entity.ai.brain.task.GoToRememberedPositionTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTarget;
import net.minecraft.entity.ai.brain.task.HuntFinishTask;
import net.minecraft.entity.ai.brain.task.HuntHoglinTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
import net.minecraft.entity.ai.brain.task.MemoryTransferTask;
import net.minecraft.entity.ai.brain.task.OpenDoorsTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.ai.brain.task.RemoveOffHandItemTask;
import net.minecraft.entity.ai.brain.task.RidingTask;
import net.minecraft.entity.ai.brain.task.StartRidingTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.TimeLimitedTask;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WalkToNearestVisibleWantedItemTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.ai.brain.task.WantNewItemTask;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.IntRange;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;

public class PiglinBrain {
	public static final Item BARTERING_ITEM = Items.field_8695;
	private static final IntRange HUNT_MEMORY_DURATION = Durations.betweenSeconds(30, 120);
	private static final IntRange MEMORY_TRANSFER_TASK_DURATION = Durations.betweenSeconds(10, 40);
	private static final IntRange RIDE_TARGET_MEMORY_DURATION = Durations.betweenSeconds(10, 30);
	private static final IntRange AVOID_MEMORY_DURATION = Durations.betweenSeconds(5, 20);
	private static final IntRange field_25384 = Durations.betweenSeconds(5, 7);
	private static final IntRange field_25698 = Durations.betweenSeconds(5, 7);
	private static final Set<Item> FOOD = ImmutableSet.of(Items.field_8389, Items.field_8261);

	protected static Brain<?> create(PiglinEntity piglin, Brain<PiglinEntity> brain) {
		addCoreActivities(brain);
		addIdleActivities(brain);
		addAdmireItemActivities(brain);
		addFightActivities(piglin, brain);
		addCelebrateActivities(brain);
		addAvoidActivities(brain);
		addRideActivities(brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.field_18594));
		brain.setDefaultActivity(Activity.field_18595);
		brain.resetPossibleActivities();
		return brain;
	}

	protected static void setHuntedRecently(PiglinEntity piglin) {
		int i = HUNT_MEMORY_DURATION.choose(piglin.world.random);
		piglin.getBrain().remember(MemoryModuleType.field_22336, true, (long)i);
	}

	private static void addCoreActivities(Brain<PiglinEntity> piglin) {
		piglin.setTaskList(
			Activity.field_18594,
			0,
			ImmutableList.of(
				new LookAroundTask(45, 90),
				new WanderAroundTask(),
				new OpenDoorsTask(),
				method_30090(),
				makeGoToZombifiedPiglinTask(),
				new RemoveOffHandItemTask(),
				new AdmireItemTask(120),
				new DefeatTargetTask(300, PiglinBrain::method_29276),
				new ForgetAngryAtTargetTask()
			)
		);
	}

	private static void addIdleActivities(Brain<PiglinEntity> piglin) {
		piglin.setTaskList(
			Activity.field_18595,
			10,
			ImmutableList.of(
				new FollowMobTask(PiglinBrain::isGoldHoldingPlayer, 14.0F),
				new UpdateAttackTargetTask<>(AbstractPiglinEntity::isAdult, PiglinBrain::getPreferredTarget),
				new ConditionalTask(PiglinEntity::canHunt, new HuntHoglinTask<>()),
				makeGoToSoulFireTask(),
				makeRememberRideableHoglinTask(),
				makeRandomFollowTask(),
				makeRandomWanderTask(),
				new FindInteractionTargetTask(EntityType.field_6097, 4)
			)
		);
	}

	private static void addFightActivities(PiglinEntity piglin, Brain<PiglinEntity> brain) {
		brain.setTaskList(
			Activity.field_22396,
			10,
			ImmutableList.of(
				new ForgetAttackTargetTask<>(livingEntity -> !isPreferredAttackTarget(piglin, livingEntity)),
				new ConditionalTask(PiglinBrain::isHoldingCrossbow, new AttackTask<>(5, 0.75F)),
				new RangedApproachTask(1.0F),
				new MeleeAttackTask(20),
				new CrossbowAttackTask(),
				new HuntFinishTask(),
				new ForgetTask(PiglinBrain::getNearestZombifiedPiglin, MemoryModuleType.field_22355)
			),
			MemoryModuleType.field_22355
		);
	}

	private static void addCelebrateActivities(Brain<PiglinEntity> brain) {
		brain.setTaskList(
			Activity.field_22397,
			10,
			ImmutableList.of(
				makeGoToSoulFireTask(),
				new FollowMobTask(PiglinBrain::isGoldHoldingPlayer, 14.0F),
				new UpdateAttackTargetTask(AbstractPiglinEntity::isAdult, PiglinBrain::getPreferredTarget),
				new ConditionalTask(piglinEntity -> !piglinEntity.isDancing(), new GoToCelebrateTask(2, 1.0F)),
				new ConditionalTask(PiglinEntity::isDancing, new GoToCelebrateTask(4, 0.6F)),
				new RandomTask(
					ImmutableList.of(Pair.of(new FollowMobTask(EntityType.field_22281, 8.0F), 1), Pair.of(new StrollTask(0.6F, 2, 1), 1), Pair.of(new WaitTask(10, 20), 1))
				)
			),
			MemoryModuleType.field_22337
		);
	}

	private static void addAdmireItemActivities(Brain<PiglinEntity> brain) {
		brain.setTaskList(
			Activity.field_22398,
			10,
			ImmutableList.of(
				new WalkToNearestVisibleWantedItemTask<>(PiglinBrain::doesNotHaveGoldInOffHand, 1.0F, true, 9),
				new WantNewItemTask(9),
				new AdmireItemTimeLimitTask(200, 200)
			),
			MemoryModuleType.field_22334
		);
	}

	private static void addAvoidActivities(Brain<PiglinEntity> brain) {
		brain.setTaskList(
			Activity.field_22399,
			10,
			ImmutableList.of(
				GoToRememberedPositionTask.toEntity(MemoryModuleType.field_22357, 1.0F, 12, true),
				makeRandomFollowTask(),
				makeRandomWanderTask(),
				new ForgetTask(PiglinBrain::shouldRunAwayFromHoglins, MemoryModuleType.field_22357)
			),
			MemoryModuleType.field_22357
		);
	}

	private static void addRideActivities(Brain<PiglinEntity> brain) {
		brain.setTaskList(
			Activity.field_22400,
			10,
			ImmutableList.of(
				new StartRidingTask<>(0.8F),
				new FollowMobTask(PiglinBrain::isGoldHoldingPlayer, 8.0F),
				new ConditionalTask(Entity::hasVehicle, makeRandomFollowTask()),
				new RidingTask(8, PiglinBrain::canRide)
			),
			MemoryModuleType.field_22356
		);
	}

	private static RandomTask<PiglinEntity> makeRandomFollowTask() {
		return new RandomTask<>(
			ImmutableList.of(
				Pair.of(new FollowMobTask(EntityType.field_6097, 8.0F), 1),
				Pair.of(new FollowMobTask(EntityType.field_22281, 8.0F), 1),
				Pair.of(new FollowMobTask(8.0F), 1),
				Pair.of(new WaitTask(30, 60), 1)
			)
		);
	}

	private static RandomTask<PiglinEntity> makeRandomWanderTask() {
		return new RandomTask<>(
			ImmutableList.of(
				Pair.of(new StrollTask(0.6F), 2),
				Pair.of(FindEntityTask.create(EntityType.field_22281, 8, MemoryModuleType.field_18447, 0.6F, 2), 2),
				Pair.of(new ConditionalTask<>(PiglinBrain::canWander, new GoTowardsLookTarget(0.6F, 3)), 2),
				Pair.of(new WaitTask(30, 60), 1)
			)
		);
	}

	private static GoToRememberedPositionTask<BlockPos> makeGoToSoulFireTask() {
		return GoToRememberedPositionTask.toBlock(MemoryModuleType.field_22474, 1.0F, 8, false);
	}

	private static MemoryTransferTask<PiglinEntity, LivingEntity> method_30090() {
		return new MemoryTransferTask<>(PiglinEntity::isBaby, MemoryModuleType.field_25360, MemoryModuleType.field_22357, field_25698);
	}

	private static MemoryTransferTask<PiglinEntity, LivingEntity> makeGoToZombifiedPiglinTask() {
		return new MemoryTransferTask<>(PiglinBrain::getNearestZombifiedPiglin, MemoryModuleType.field_22346, MemoryModuleType.field_22357, field_25384);
	}

	protected static void tickActivities(PiglinEntity piglin) {
		Brain<PiglinEntity> brain = piglin.getBrain();
		Activity activity = (Activity)brain.getFirstPossibleNonCoreActivity().orElse(null);
		brain.resetPossibleActivities(
			ImmutableList.of(Activity.field_22398, Activity.field_22396, Activity.field_22399, Activity.field_22397, Activity.field_22400, Activity.field_18595)
		);
		Activity activity2 = (Activity)brain.getFirstPossibleNonCoreActivity().orElse(null);
		if (activity != activity2) {
			method_30091(piglin).ifPresent(piglin::playSound);
		}

		piglin.setAttacking(brain.hasMemoryModule(MemoryModuleType.field_22355));
		if (!brain.hasMemoryModule(MemoryModuleType.field_22356) && canRideHoglin(piglin)) {
			piglin.stopRiding();
		}

		if (!brain.hasMemoryModule(MemoryModuleType.field_22337)) {
			brain.forget(MemoryModuleType.field_25159);
		}

		piglin.setDancing(brain.hasMemoryModule(MemoryModuleType.field_25159));
	}

	private static boolean canRideHoglin(PiglinEntity piglin) {
		if (!piglin.isBaby()) {
			return false;
		} else {
			Entity entity = piglin.getVehicle();
			return entity instanceof PiglinEntity && ((PiglinEntity)entity).isBaby() || entity instanceof HoglinEntity && ((HoglinEntity)entity).isBaby();
		}
	}

	protected static void loot(PiglinEntity piglin, ItemEntity drop) {
		stopWalking(piglin);
		ItemStack itemStack;
		if (drop.getStack().getItem() == Items.field_8397) {
			piglin.sendPickup(drop, drop.getStack().getCount());
			itemStack = drop.getStack();
			drop.remove();
		} else {
			piglin.sendPickup(drop, 1);
			itemStack = getItemFromStack(drop);
		}

		Item item = itemStack.getItem();
		if (isGoldenItem(item)) {
			piglin.getBrain().forget(MemoryModuleType.field_25813);
			swapItemWithOffHand(piglin, itemStack);
			setAdmiringItem(piglin);
		} else if (isFood(item) && !hasAteRecently(piglin)) {
			setEatenRecently(piglin);
		} else {
			boolean bl = piglin.tryEquip(itemStack);
			if (!bl) {
				barterItem(piglin, itemStack);
			}
		}
	}

	private static void swapItemWithOffHand(PiglinEntity piglin, ItemStack stack) {
		if (hasItemInOffHand(piglin)) {
			piglin.dropStack(piglin.getStackInHand(Hand.field_5810));
		}

		piglin.equipToOffHand(stack);
	}

	private static ItemStack getItemFromStack(ItemEntity stack) {
		ItemStack itemStack = stack.getStack();
		ItemStack itemStack2 = itemStack.split(1);
		if (itemStack.isEmpty()) {
			stack.remove();
		} else {
			stack.setStack(itemStack);
		}

		return itemStack2;
	}

	public static void consumeOffHandItem(PiglinEntity piglin, boolean bl) {
		ItemStack itemStack = piglin.getStackInHand(Hand.field_5810);
		piglin.setStackInHand(Hand.field_5810, ItemStack.EMPTY);
		if (piglin.isAdult()) {
			boolean bl2 = acceptsForBarter(itemStack.getItem());
			if (bl && bl2) {
				doBarter(piglin, getBarteredItem(piglin));
			} else if (!bl2) {
				boolean bl3 = piglin.tryEquip(itemStack);
				if (!bl3) {
					barterItem(piglin, itemStack);
				}
			}
		} else {
			boolean bl2 = piglin.tryEquip(itemStack);
			if (!bl2) {
				ItemStack itemStack2 = piglin.getMainHandStack();
				if (isGoldenItem(itemStack2.getItem())) {
					barterItem(piglin, itemStack2);
				} else {
					doBarter(piglin, Collections.singletonList(itemStack2));
				}

				piglin.equipToMainHand(itemStack);
			}
		}
	}

	protected static void pickupItemWithOffHand(PiglinEntity piglin) {
		if (isAdmiringItem(piglin) && !piglin.getOffHandStack().isEmpty()) {
			piglin.dropStack(piglin.getOffHandStack());
			piglin.setStackInHand(Hand.field_5810, ItemStack.EMPTY);
		}
	}

	private static void barterItem(PiglinEntity piglin, ItemStack stack) {
		ItemStack itemStack = piglin.addItem(stack);
		dropBarteredItem(piglin, Collections.singletonList(itemStack));
	}

	private static void doBarter(PiglinEntity piglin, List<ItemStack> list) {
		Optional<PlayerEntity> optional = piglin.getBrain().getOptionalMemory(MemoryModuleType.field_18444);
		if (optional.isPresent()) {
			dropBarteredItem(piglin, (PlayerEntity)optional.get(), list);
		} else {
			dropBarteredItem(piglin, list);
		}
	}

	private static void dropBarteredItem(PiglinEntity piglin, List<ItemStack> list) {
		drop(piglin, list, findGround(piglin));
	}

	private static void dropBarteredItem(PiglinEntity piglin, PlayerEntity player, List<ItemStack> list) {
		drop(piglin, list, player.getPos());
	}

	private static void drop(PiglinEntity piglin, List<ItemStack> list, Vec3d vec3d) {
		if (!list.isEmpty()) {
			piglin.swingHand(Hand.field_5810);

			for (ItemStack itemStack : list) {
				LookTargetUtil.give(piglin, itemStack, vec3d.add(0.0, 1.0, 0.0));
			}
		}
	}

	private static List<ItemStack> getBarteredItem(PiglinEntity piglin) {
		LootTable lootTable = piglin.world.getServer().getLootManager().getTable(LootTables.field_22402);
		return lootTable.generateLoot(
			new LootContext.Builder((ServerWorld)piglin.world)
				.parameter(LootContextParameters.field_1226, piglin)
				.random(piglin.world.random)
				.build(LootContextTypes.field_22403)
		);
	}

	private static boolean method_29276(LivingEntity livingEntity, LivingEntity livingEntity2) {
		return livingEntity2.getType() != EntityType.field_21973 ? false : new Random(livingEntity.world.getTime()).nextFloat() < 0.1F;
	}

	protected static boolean canGather(PiglinEntity piglin, ItemStack stack) {
		Item item = stack.getItem();
		if (item.isIn(ItemTags.field_23064)) {
			return false;
		} else if (hasBeenHitByPlayer(piglin) && piglin.getBrain().hasMemoryModule(MemoryModuleType.field_22355)) {
			return false;
		} else if (acceptsForBarter(item)) {
			return doesNotHaveGoldInOffHand(piglin);
		} else {
			boolean bl = piglin.canInsertIntoInventory(stack);
			if (item == Items.field_8397) {
				return bl;
			} else if (isFood(item)) {
				return !hasAteRecently(piglin) && bl;
			} else {
				return !isGoldenItem(item) ? piglin.method_24846(stack) : doesNotHaveGoldInOffHand(piglin) && bl;
			}
		}
	}

	public static boolean isGoldenItem(Item item) {
		return item.isIn(ItemTags.field_24481);
	}

	private static boolean canRide(PiglinEntity piglin, Entity ridden) {
		if (!(ridden instanceof MobEntity)) {
			return false;
		} else {
			MobEntity mobEntity = (MobEntity)ridden;
			return !mobEntity.isBaby()
				|| !mobEntity.isAlive()
				|| hasBeenHurt(piglin)
				|| hasBeenHurt(mobEntity)
				|| mobEntity instanceof PiglinEntity && mobEntity.getVehicle() == null;
		}
	}

	private static boolean isPreferredAttackTarget(PiglinEntity piglin, LivingEntity target) {
		return getPreferredTarget(piglin).filter(livingEntity2 -> livingEntity2 == target).isPresent();
	}

	private static boolean getNearestZombifiedPiglin(PiglinEntity piglin) {
		Brain<PiglinEntity> brain = piglin.getBrain();
		if (brain.hasMemoryModule(MemoryModuleType.field_22346)) {
			LivingEntity livingEntity = (LivingEntity)brain.getOptionalMemory(MemoryModuleType.field_22346).get();
			return piglin.isInRange(livingEntity, 6.0);
		} else {
			return false;
		}
	}

	private static Optional<? extends LivingEntity> getPreferredTarget(PiglinEntity piglin) {
		Brain<PiglinEntity> brain = piglin.getBrain();
		if (getNearestZombifiedPiglin(piglin)) {
			return Optional.empty();
		} else {
			Optional<LivingEntity> optional = LookTargetUtil.getEntity(piglin, MemoryModuleType.field_22333);
			if (optional.isPresent() && shouldAttack((LivingEntity)optional.get())) {
				return optional;
			} else {
				if (brain.hasMemoryModule(MemoryModuleType.field_25361)) {
					Optional<PlayerEntity> optional2 = brain.getOptionalMemory(MemoryModuleType.field_22354);
					if (optional2.isPresent()) {
						return optional2;
					}
				}

				Optional<MobEntity> optional2 = brain.getOptionalMemory(MemoryModuleType.field_25360);
				if (optional2.isPresent()) {
					return optional2;
				} else {
					Optional<PlayerEntity> optional3 = brain.getOptionalMemory(MemoryModuleType.field_22342);
					return optional3.isPresent() && shouldAttack((LivingEntity)optional3.get()) ? optional3 : Optional.empty();
				}
			}
		}
	}

	public static void onGuardedBlockBroken(PlayerEntity player, boolean bl) {
		List<PiglinEntity> list = player.world.getNonSpectatingEntities(PiglinEntity.class, player.getBoundingBox().expand(16.0));
		list.stream().filter(PiglinBrain::hasIdleActivity).filter(piglinEntity -> !bl || LookTargetUtil.isVisibleInMemory(piglinEntity, player)).forEach(piglin -> {
			if (piglin.world.getGameRules().getBoolean(GameRules.field_25402)) {
				becomeAngryWithPlayer(piglin, player);
			} else {
				becomeAngryWith(piglin, player);
			}
		});
	}

	public static ActionResult playerInteract(PiglinEntity piglin, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (isWillingToTrade(piglin, itemStack)) {
			ItemStack itemStack2 = itemStack.split(1);
			swapItemWithOffHand(piglin, itemStack2);
			setAdmiringItem(piglin);
			stopWalking(piglin);
			return ActionResult.CONSUME;
		} else {
			return ActionResult.PASS;
		}
	}

	protected static boolean isWillingToTrade(PiglinEntity piglin, ItemStack nearbyItems) {
		return !hasBeenHitByPlayer(piglin) && !isAdmiringItem(piglin) && piglin.isAdult() && acceptsForBarter(nearbyItems.getItem());
	}

	protected static void onAttacked(PiglinEntity piglin, LivingEntity attacker) {
		if (!(attacker instanceof PiglinEntity)) {
			if (hasItemInOffHand(piglin)) {
				consumeOffHandItem(piglin, false);
			}

			Brain<PiglinEntity> brain = piglin.getBrain();
			brain.forget(MemoryModuleType.field_22337);
			brain.forget(MemoryModuleType.field_25159);
			brain.forget(MemoryModuleType.field_22334);
			if (attacker instanceof PlayerEntity) {
				brain.remember(MemoryModuleType.field_22473, true, 400L);
			}

			method_29536(piglin).ifPresent(livingEntity2 -> {
				if (livingEntity2.getType() != attacker.getType()) {
					brain.forget(MemoryModuleType.field_22357);
				}
			});
			if (piglin.isBaby()) {
				brain.remember(MemoryModuleType.field_22357, attacker, 100L);
				if (shouldAttack(attacker)) {
					angerAtCloserTargets(piglin, attacker);
				}
			} else if (attacker.getType() == EntityType.field_21973 && hasOutnumberedHoglins(piglin)) {
				runAwayFrom(piglin, attacker);
				groupRunAwayFrom(piglin, attacker);
			} else {
				tryRevenge(piglin, attacker);
			}
		}
	}

	protected static void tryRevenge(AbstractPiglinEntity abstractPiglinEntity, LivingEntity livingEntity) {
		if (!abstractPiglinEntity.getBrain().hasActivity(Activity.field_22399)) {
			if (shouldAttack(livingEntity)) {
				if (!LookTargetUtil.isNewTargetTooFar(abstractPiglinEntity, livingEntity, 4.0)) {
					if (livingEntity.getType() == EntityType.field_6097 && abstractPiglinEntity.world.getGameRules().getBoolean(GameRules.field_25402)) {
						becomeAngryWithPlayer(abstractPiglinEntity, livingEntity);
						angerNearbyPiglins(abstractPiglinEntity);
					} else {
						becomeAngryWith(abstractPiglinEntity, livingEntity);
						angerAtCloserTargets(abstractPiglinEntity, livingEntity);
					}
				}
			}
		}
	}

	public static Optional<SoundEvent> method_30091(PiglinEntity piglin) {
		return piglin.getBrain().getFirstPossibleNonCoreActivity().map(activity -> method_30087(piglin, activity));
	}

	private static SoundEvent method_30087(PiglinEntity piglin, Activity activity) {
		if (activity == Activity.field_22396) {
			return SoundEvents.field_22265;
		} else if (piglin.shouldZombify()) {
			return SoundEvents.field_22270;
		} else if (activity == Activity.field_22399 && hasTargetToAvoid(piglin)) {
			return SoundEvents.field_22270;
		} else if (activity == Activity.field_22398) {
			return SoundEvents.field_22263;
		} else if (activity == Activity.field_22397) {
			return SoundEvents.field_22266;
		} else if (hasPlayerHoldingWantedItemNearby(piglin)) {
			return SoundEvents.field_22268;
		} else {
			return hasSoulFireNearby(piglin) ? SoundEvents.field_22270 : SoundEvents.field_22264;
		}
	}

	private static boolean hasTargetToAvoid(PiglinEntity piglin) {
		Brain<PiglinEntity> brain = piglin.getBrain();
		return !brain.hasMemoryModule(MemoryModuleType.field_22357)
			? false
			: ((LivingEntity)brain.getOptionalMemory(MemoryModuleType.field_22357).get()).isInRange(piglin, 12.0);
	}

	public static boolean haveHuntedHoglinsRecently(PiglinEntity piglin) {
		return piglin.getBrain().hasMemoryModule(MemoryModuleType.field_22336)
			|| getNearbyVisiblePiglins(piglin).stream().anyMatch(abstractPiglinEntity -> abstractPiglinEntity.getBrain().hasMemoryModule(MemoryModuleType.field_22336));
	}

	private static List<AbstractPiglinEntity> getNearbyVisiblePiglins(PiglinEntity piglin) {
		return (List<AbstractPiglinEntity>)piglin.getBrain().getOptionalMemory(MemoryModuleType.field_22343).orElse(ImmutableList.of());
	}

	private static List<AbstractPiglinEntity> getNearbyPiglins(AbstractPiglinEntity piglin) {
		return (List<AbstractPiglinEntity>)piglin.getBrain().getOptionalMemory(MemoryModuleType.field_25755).orElse(ImmutableList.of());
	}

	public static boolean wearsGoldArmor(LivingEntity entity) {
		for (ItemStack itemStack : entity.getArmorItems()) {
			Item item = itemStack.getItem();
			if (item instanceof ArmorItem && ((ArmorItem)item).getMaterial() == ArmorMaterials.field_7895) {
				return true;
			}
		}

		return false;
	}

	private static void stopWalking(PiglinEntity piglin) {
		piglin.getBrain().forget(MemoryModuleType.field_18445);
		piglin.getNavigation().stop();
	}

	private static TimeLimitedTask<PiglinEntity> makeRememberRideableHoglinTask() {
		return new TimeLimitedTask<>(
			new MemoryTransferTask<>(PiglinEntity::isBaby, MemoryModuleType.field_22340, MemoryModuleType.field_22356, RIDE_TARGET_MEMORY_DURATION),
			MEMORY_TRANSFER_TASK_DURATION
		);
	}

	public static void angerAtCloserTargets(AbstractPiglinEntity piglin, LivingEntity target) {
		getNearbyPiglins(piglin).forEach(abstractPiglinEntity -> {
			if (target.getType() != EntityType.field_21973 || abstractPiglinEntity.canHunt() && ((HoglinEntity)target).canBeHunted()) {
				angerAtIfCloser(abstractPiglinEntity, target);
			}
		});
	}

	protected static void angerNearbyPiglins(AbstractPiglinEntity piglin) {
		getNearbyPiglins(piglin)
			.forEach(
				abstractPiglinEntity -> getNearestDetectedPlayer(abstractPiglinEntity).ifPresent(playerEntity -> becomeAngryWith(abstractPiglinEntity, playerEntity))
			);
	}

	public static void rememberGroupHunting(PiglinEntity piglin) {
		getNearbyVisiblePiglins(piglin).forEach(PiglinBrain::rememberHunting);
	}

	public static void becomeAngryWith(AbstractPiglinEntity piglin, LivingEntity target) {
		if (shouldAttack(target)) {
			piglin.getBrain().forget(MemoryModuleType.field_19293);
			piglin.getBrain().remember(MemoryModuleType.field_22333, target.getUuid(), 600L);
			if (target.getType() == EntityType.field_21973 && piglin.canHunt()) {
				rememberHunting(piglin);
			}

			if (target.getType() == EntityType.field_6097 && piglin.world.getGameRules().getBoolean(GameRules.field_25402)) {
				piglin.getBrain().remember(MemoryModuleType.field_25361, true, 600L);
			}
		}
	}

	private static void becomeAngryWithPlayer(AbstractPiglinEntity piglin, LivingEntity player) {
		Optional<PlayerEntity> optional = getNearestDetectedPlayer(piglin);
		if (optional.isPresent()) {
			becomeAngryWith(piglin, (LivingEntity)optional.get());
		} else {
			becomeAngryWith(piglin, player);
		}
	}

	private static void angerAtIfCloser(AbstractPiglinEntity piglin, LivingEntity target) {
		Optional<LivingEntity> optional = getAngryAt(piglin);
		LivingEntity livingEntity = LookTargetUtil.getCloserEntity(piglin, optional, target);
		if (!optional.isPresent() || optional.get() != livingEntity) {
			becomeAngryWith(piglin, livingEntity);
		}
	}

	private static Optional<LivingEntity> getAngryAt(AbstractPiglinEntity piglin) {
		return LookTargetUtil.getEntity(piglin, MemoryModuleType.field_22333);
	}

	public static Optional<LivingEntity> method_29536(PiglinEntity piglinEntity) {
		return piglinEntity.getBrain().hasMemoryModule(MemoryModuleType.field_22357)
			? piglinEntity.getBrain().getOptionalMemory(MemoryModuleType.field_22357)
			: Optional.empty();
	}

	public static Optional<PlayerEntity> getNearestDetectedPlayer(AbstractPiglinEntity piglin) {
		return piglin.getBrain().hasMemoryModule(MemoryModuleType.field_22354) ? piglin.getBrain().getOptionalMemory(MemoryModuleType.field_22354) : Optional.empty();
	}

	private static void groupRunAwayFrom(PiglinEntity piglin, LivingEntity target) {
		getNearbyVisiblePiglins(piglin)
			.stream()
			.filter(abstractPiglinEntity -> abstractPiglinEntity instanceof PiglinEntity)
			.forEach(piglinx -> runAwayFromClosestTarget((PiglinEntity)piglinx, target));
	}

	private static void runAwayFromClosestTarget(PiglinEntity piglin, LivingEntity target) {
		Brain<PiglinEntity> brain = piglin.getBrain();
		LivingEntity livingEntity = LookTargetUtil.getCloserEntity(piglin, brain.getOptionalMemory(MemoryModuleType.field_22357), target);
		livingEntity = LookTargetUtil.getCloserEntity(piglin, brain.getOptionalMemory(MemoryModuleType.field_22355), livingEntity);
		runAwayFrom(piglin, livingEntity);
	}

	private static boolean shouldRunAwayFromHoglins(PiglinEntity piglin) {
		Brain<PiglinEntity> brain = piglin.getBrain();
		if (!brain.hasMemoryModule(MemoryModuleType.field_22357)) {
			return true;
		} else {
			LivingEntity livingEntity = (LivingEntity)brain.getOptionalMemory(MemoryModuleType.field_22357).get();
			EntityType<?> entityType = livingEntity.getType();
			if (entityType == EntityType.field_21973) {
				return hasNoAdvantageAgainstHoglins(piglin);
			} else {
				return isZombified(entityType) ? !brain.method_29519(MemoryModuleType.field_22346, livingEntity) : false;
			}
		}
	}

	private static boolean hasNoAdvantageAgainstHoglins(PiglinEntity piglin) {
		return !hasOutnumberedHoglins(piglin);
	}

	private static boolean hasOutnumberedHoglins(PiglinEntity piglins) {
		int i = (Integer)piglins.getBrain().getOptionalMemory(MemoryModuleType.field_22347).orElse(0) + 1;
		int j = (Integer)piglins.getBrain().getOptionalMemory(MemoryModuleType.field_22348).orElse(0);
		return j > i;
	}

	private static void runAwayFrom(PiglinEntity piglin, LivingEntity target) {
		piglin.getBrain().forget(MemoryModuleType.field_22333);
		piglin.getBrain().forget(MemoryModuleType.field_22355);
		piglin.getBrain().forget(MemoryModuleType.field_18445);
		piglin.getBrain().remember(MemoryModuleType.field_22357, target, (long)AVOID_MEMORY_DURATION.choose(piglin.world.random));
		rememberHunting(piglin);
	}

	public static void rememberHunting(AbstractPiglinEntity piglin) {
		piglin.getBrain().remember(MemoryModuleType.field_22336, true, (long)HUNT_MEMORY_DURATION.choose(piglin.world.random));
	}

	private static void setEatenRecently(PiglinEntity piglin) {
		piglin.getBrain().remember(MemoryModuleType.field_22350, true, 200L);
	}

	private static Vec3d findGround(PiglinEntity piglin) {
		Vec3d vec3d = TargetFinder.findGroundTarget(piglin, 4, 2);
		return vec3d == null ? piglin.getPos() : vec3d;
	}

	private static boolean hasAteRecently(PiglinEntity piglin) {
		return piglin.getBrain().hasMemoryModule(MemoryModuleType.field_22350);
	}

	protected static boolean hasIdleActivity(AbstractPiglinEntity piglin) {
		return piglin.getBrain().hasActivity(Activity.field_18595);
	}

	private static boolean isHoldingCrossbow(LivingEntity piglin) {
		return piglin.isHolding(Items.field_8399);
	}

	private static void setAdmiringItem(LivingEntity entity) {
		entity.getBrain().remember(MemoryModuleType.field_22334, true, 120L);
	}

	private static boolean isAdmiringItem(PiglinEntity entity) {
		return entity.getBrain().hasMemoryModule(MemoryModuleType.field_22334);
	}

	private static boolean acceptsForBarter(Item item) {
		return item == BARTERING_ITEM;
	}

	private static boolean isFood(Item item) {
		return FOOD.contains(item);
	}

	private static boolean shouldAttack(LivingEntity target) {
		return EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(target);
	}

	private static boolean hasSoulFireNearby(PiglinEntity piglin) {
		return piglin.getBrain().hasMemoryModule(MemoryModuleType.field_22474);
	}

	private static boolean hasPlayerHoldingWantedItemNearby(LivingEntity entity) {
		return entity.getBrain().hasMemoryModule(MemoryModuleType.field_22349);
	}

	private static boolean canWander(LivingEntity piglin) {
		return !hasPlayerHoldingWantedItemNearby(piglin);
	}

	public static boolean isGoldHoldingPlayer(LivingEntity target) {
		return target.getType() == EntityType.field_6097 && target.isHolding(PiglinBrain::isGoldenItem);
	}

	private static boolean hasBeenHitByPlayer(PiglinEntity piglin) {
		return piglin.getBrain().hasMemoryModule(MemoryModuleType.field_22473);
	}

	private static boolean hasBeenHurt(LivingEntity piglin) {
		return piglin.getBrain().hasMemoryModule(MemoryModuleType.field_18451);
	}

	private static boolean hasItemInOffHand(PiglinEntity piglin) {
		return !piglin.getOffHandStack().isEmpty();
	}

	private static boolean doesNotHaveGoldInOffHand(PiglinEntity piglin) {
		return piglin.getOffHandStack().isEmpty() || !isGoldenItem(piglin.getOffHandStack().getItem());
	}

	public static boolean isZombified(EntityType entityType) {
		return entityType == EntityType.field_6050 || entityType == EntityType.field_23696;
	}
}
