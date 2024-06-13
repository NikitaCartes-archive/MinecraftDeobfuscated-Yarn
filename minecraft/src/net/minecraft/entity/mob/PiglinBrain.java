package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.task.AdmireItemTask;
import net.minecraft.entity.ai.brain.task.AdmireItemTimeLimitTask;
import net.minecraft.entity.ai.brain.task.AttackTask;
import net.minecraft.entity.ai.brain.task.CrossbowAttackTask;
import net.minecraft.entity.ai.brain.task.DefeatTargetTask;
import net.minecraft.entity.ai.brain.task.FindEntityTask;
import net.minecraft.entity.ai.brain.task.FindInteractionTargetTask;
import net.minecraft.entity.ai.brain.task.ForgetAngryAtTargetTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.ForgetTask;
import net.minecraft.entity.ai.brain.task.GoToRememberedPositionTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTargetTask;
import net.minecraft.entity.ai.brain.task.HuntFinishTask;
import net.minecraft.entity.ai.brain.task.HuntHoglinTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookAtMobTask;
import net.minecraft.entity.ai.brain.task.LookAtMobWithIntervalTask;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
import net.minecraft.entity.ai.brain.task.MemoryTransferTask;
import net.minecraft.entity.ai.brain.task.MoveToTargetTask;
import net.minecraft.entity.ai.brain.task.OpenDoorsTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.ai.brain.task.RemoveOffHandItemTask;
import net.minecraft.entity.ai.brain.task.RidingTask;
import net.minecraft.entity.ai.brain.task.SingleTickTask;
import net.minecraft.entity.ai.brain.task.StartRidingTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskRunnable;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.ai.brain.task.Tasks;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WalkToNearestVisibleWantedItemTask;
import net.minecraft.entity.ai.brain.task.WalkTowardsPosTask;
import net.minecraft.entity.ai.brain.task.WantNewItemTask;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;

public class PiglinBrain {
	public static final int field_30565 = 8;
	public static final int field_30566 = 4;
	public static final Item BARTERING_ITEM = Items.GOLD_INGOT;
	private static final int field_30567 = 16;
	private static final int field_30568 = 600;
	private static final int field_30569 = 119;
	private static final int field_30570 = 9;
	private static final int field_30571 = 200;
	private static final int field_30572 = 200;
	private static final int field_30573 = 300;
	public static final UniformIntProvider HUNT_MEMORY_DURATION = TimeHelper.betweenSeconds(30, 120);
	private static final int AVOID_TARGET_EXPIRY = 100;
	private static final int ADMIRING_DISABLED_EXPIRY = 400;
	private static final int field_30576 = 8;
	private static final UniformIntProvider MEMORY_TRANSFER_TASK_DURATION = TimeHelper.betweenSeconds(10, 40);
	private static final UniformIntProvider RIDE_TARGET_MEMORY_DURATION = TimeHelper.betweenSeconds(10, 30);
	private static final UniformIntProvider AVOID_MEMORY_DURATION = TimeHelper.betweenSeconds(5, 20);
	private static final int field_30577 = 20;
	private static final int field_30578 = 200;
	private static final int field_30579 = 12;
	private static final int field_30580 = 8;
	private static final int field_30581 = 14;
	private static final int field_30582 = 8;
	private static final int field_30583 = 5;
	private static final float CROSSBOW_ATTACK_FORWARD_MOVEMENT = 0.75F;
	private static final int field_30585 = 6;
	private static final UniformIntProvider GO_TO_ZOMBIFIED_MEMORY_DURATION = TimeHelper.betweenSeconds(5, 7);
	private static final UniformIntProvider GO_TO_NEMESIS_MEMORY_DURATION = TimeHelper.betweenSeconds(5, 7);
	private static final float field_30557 = 0.1F;
	private static final float field_30558 = 1.0F;
	private static final float field_30559 = 1.0F;
	private static final float START_RIDING_SPEED = 0.8F;
	private static final float field_30561 = 1.0F;
	private static final float field_30562 = 1.0F;
	private static final float field_30563 = 0.6F;
	private static final float field_30564 = 0.6F;

	protected static Brain<?> create(PiglinEntity piglin, Brain<PiglinEntity> brain) {
		addCoreActivities(brain);
		addIdleActivities(brain);
		addAdmireItemActivities(brain);
		addFightActivities(piglin, brain);
		addCelebrateActivities(brain);
		addAvoidActivities(brain);
		addRideActivities(brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
		return brain;
	}

	protected static void setHuntedRecently(PiglinEntity piglin, Random random) {
		int i = HUNT_MEMORY_DURATION.get(random);
		piglin.getBrain().remember(MemoryModuleType.HUNTED_RECENTLY, true, (long)i);
	}

	private static void addCoreActivities(Brain<PiglinEntity> piglin) {
		piglin.setTaskList(
			Activity.CORE,
			0,
			ImmutableList.of(
				new LookAroundTask(45, 90),
				new MoveToTargetTask(),
				OpenDoorsTask.create(),
				goToNemesisTask(),
				makeFleeFromZombifiedPiglinTask(),
				RemoveOffHandItemTask.create(),
				AdmireItemTask.create(119),
				DefeatTargetTask.create(300, PiglinBrain::isHuntingTarget),
				ForgetAngryAtTargetTask.create()
			)
		);
	}

	private static void addIdleActivities(Brain<PiglinEntity> piglin) {
		piglin.setTaskList(
			Activity.IDLE,
			10,
			ImmutableList.of(
				LookAtMobTask.create(PiglinBrain::isGoldHoldingPlayer, 14.0F),
				UpdateAttackTargetTask.create(AbstractPiglinEntity::isAdult, PiglinBrain::getPreferredTarget),
				TaskTriggerer.runIf(PiglinEntity::canHunt, HuntHoglinTask.create()),
				makeGoToSoulFireTask(),
				makeRememberRideableHoglinTask(),
				makeRandomFollowTask(),
				makeRandomWanderTask(),
				FindInteractionTargetTask.create(EntityType.PLAYER, 4)
			)
		);
	}

	private static void addFightActivities(PiglinEntity piglin, Brain<PiglinEntity> brain) {
		brain.setTaskList(
			Activity.FIGHT,
			10,
			ImmutableList.of(
				ForgetAttackTargetTask.create((Predicate<LivingEntity>)(target -> !isPreferredAttackTarget(piglin, target))),
				TaskTriggerer.runIf(PiglinBrain::isHoldingCrossbow, AttackTask.create(5, 0.75F)),
				RangedApproachTask.create(1.0F),
				MeleeAttackTask.create(20),
				new CrossbowAttackTask(),
				HuntFinishTask.create(),
				ForgetTask.create(PiglinBrain::getNearestZombifiedPiglin, MemoryModuleType.ATTACK_TARGET)
			),
			MemoryModuleType.ATTACK_TARGET
		);
	}

	private static void addCelebrateActivities(Brain<PiglinEntity> brain) {
		brain.setTaskList(
			Activity.CELEBRATE,
			10,
			ImmutableList.of(
				makeGoToSoulFireTask(),
				LookAtMobTask.create(PiglinBrain::isGoldHoldingPlayer, 14.0F),
				UpdateAttackTargetTask.create(AbstractPiglinEntity::isAdult, PiglinBrain::getPreferredTarget),
				TaskTriggerer.runIf(piglin -> !piglin.isDancing(), WalkTowardsPosTask.create(MemoryModuleType.CELEBRATE_LOCATION, 2, 1.0F)),
				TaskTriggerer.runIf(PiglinEntity::isDancing, WalkTowardsPosTask.create(MemoryModuleType.CELEBRATE_LOCATION, 4, 0.6F)),
				new RandomTask<LivingEntity>(
					ImmutableList.of(Pair.of(LookAtMobTask.create(EntityType.PIGLIN, 8.0F), 1), Pair.of(StrollTask.create(0.6F, 2, 1), 1), Pair.of(new WaitTask(10, 20), 1))
				)
			),
			MemoryModuleType.CELEBRATE_LOCATION
		);
	}

	private static void addAdmireItemActivities(Brain<PiglinEntity> brain) {
		brain.setTaskList(
			Activity.ADMIRE_ITEM,
			10,
			ImmutableList.of(
				WalkToNearestVisibleWantedItemTask.create(PiglinBrain::doesNotHaveGoldInOffHand, 1.0F, true, 9),
				WantNewItemTask.create(9),
				AdmireItemTimeLimitTask.create(200, 200)
			),
			MemoryModuleType.ADMIRING_ITEM
		);
	}

	private static void addAvoidActivities(Brain<PiglinEntity> brain) {
		brain.setTaskList(
			Activity.AVOID,
			10,
			ImmutableList.of(
				GoToRememberedPositionTask.createEntityBased(MemoryModuleType.AVOID_TARGET, 1.0F, 12, true),
				makeRandomFollowTask(),
				makeRandomWanderTask(),
				ForgetTask.<PathAwareEntity>create(PiglinBrain::shouldRunAwayFromHoglins, MemoryModuleType.AVOID_TARGET)
			),
			MemoryModuleType.AVOID_TARGET
		);
	}

	private static void addRideActivities(Brain<PiglinEntity> brain) {
		brain.setTaskList(
			Activity.RIDE,
			10,
			ImmutableList.of(
				StartRidingTask.create(0.8F),
				LookAtMobTask.create(PiglinBrain::isGoldHoldingPlayer, 8.0F),
				TaskTriggerer.runIf(
					TaskTriggerer.predicate(Entity::hasVehicle),
					Tasks.pickRandomly(
						ImmutableList.<Pair<? extends TaskRunnable<? super LivingEntity>, Integer>>builder()
							.addAll(makeFollowTasks())
							.add(Pair.of(TaskTriggerer.predicate((Predicate<? super LivingEntity>)(piglinEntity -> true)), 1))
							.build()
					)
				),
				RidingTask.<LivingEntity>create(8, PiglinBrain::canRide)
			),
			MemoryModuleType.RIDE_TARGET
		);
	}

	private static ImmutableList<Pair<SingleTickTask<LivingEntity>, Integer>> makeFollowTasks() {
		return ImmutableList.of(
			Pair.of(LookAtMobTask.create(EntityType.PLAYER, 8.0F), 1), Pair.of(LookAtMobTask.create(EntityType.PIGLIN, 8.0F), 1), Pair.of(LookAtMobTask.create(8.0F), 1)
		);
	}

	private static RandomTask<LivingEntity> makeRandomFollowTask() {
		return new RandomTask<>(
			ImmutableList.<Pair<? extends Task<? super LivingEntity>, Integer>>builder().addAll(makeFollowTasks()).add(Pair.of(new WaitTask(30, 60), 1)).build()
		);
	}

	private static RandomTask<PiglinEntity> makeRandomWanderTask() {
		return new RandomTask<>(
			ImmutableList.of(
				Pair.of(StrollTask.create(0.6F), 2),
				Pair.of(FindEntityTask.create(EntityType.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2),
				Pair.of(TaskTriggerer.runIf(PiglinBrain::canWander, GoTowardsLookTargetTask.create(0.6F, 3)), 2),
				Pair.of(new WaitTask(30, 60), 1)
			)
		);
	}

	private static Task<PathAwareEntity> makeGoToSoulFireTask() {
		return GoToRememberedPositionTask.createPosBased(MemoryModuleType.NEAREST_REPELLENT, 1.0F, 8, false);
	}

	private static Task<PiglinEntity> goToNemesisTask() {
		return MemoryTransferTask.create(PiglinEntity::isBaby, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.AVOID_TARGET, GO_TO_NEMESIS_MEMORY_DURATION);
	}

	private static Task<PiglinEntity> makeFleeFromZombifiedPiglinTask() {
		return MemoryTransferTask.create(
			PiglinBrain::getNearestZombifiedPiglin, MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, MemoryModuleType.AVOID_TARGET, GO_TO_ZOMBIFIED_MEMORY_DURATION
		);
	}

	protected static void tickActivities(PiglinEntity piglin) {
		Brain<PiglinEntity> brain = piglin.getBrain();
		Activity activity = (Activity)brain.getFirstPossibleNonCoreActivity().orElse(null);
		brain.resetPossibleActivities(ImmutableList.of(Activity.ADMIRE_ITEM, Activity.FIGHT, Activity.AVOID, Activity.CELEBRATE, Activity.RIDE, Activity.IDLE));
		Activity activity2 = (Activity)brain.getFirstPossibleNonCoreActivity().orElse(null);
		if (activity != activity2) {
			getCurrentActivitySound(piglin).ifPresent(piglin::playSound);
		}

		piglin.setAttacking(brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
		if (!brain.hasMemoryModule(MemoryModuleType.RIDE_TARGET) && canRideHoglin(piglin)) {
			piglin.stopRiding();
		}

		if (!brain.hasMemoryModule(MemoryModuleType.CELEBRATE_LOCATION)) {
			brain.forget(MemoryModuleType.DANCING);
		}

		piglin.setDancing(brain.hasMemoryModule(MemoryModuleType.DANCING));
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
		if (drop.getStack().isOf(Items.GOLD_NUGGET)) {
			piglin.sendPickup(drop, drop.getStack().getCount());
			itemStack = drop.getStack();
			drop.discard();
		} else {
			piglin.sendPickup(drop, 1);
			itemStack = getItemFromStack(drop);
		}

		if (isGoldenItem(itemStack)) {
			piglin.getBrain().forget(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
			swapItemWithOffHand(piglin, itemStack);
			setAdmiringItem(piglin);
		} else if (isFood(itemStack) && !hasAteRecently(piglin)) {
			setEatenRecently(piglin);
		} else {
			boolean bl = !piglin.tryEquip(itemStack).equals(ItemStack.EMPTY);
			if (!bl) {
				barterItem(piglin, itemStack);
			}
		}
	}

	private static void swapItemWithOffHand(PiglinEntity piglin, ItemStack stack) {
		if (hasItemInOffHand(piglin)) {
			piglin.dropStack(piglin.getStackInHand(Hand.OFF_HAND));
		}

		piglin.equipToOffHand(stack);
	}

	private static ItemStack getItemFromStack(ItemEntity stack) {
		ItemStack itemStack = stack.getStack();
		ItemStack itemStack2 = itemStack.split(1);
		if (itemStack.isEmpty()) {
			stack.discard();
		} else {
			stack.setStack(itemStack);
		}

		return itemStack2;
	}

	public static void consumeOffHandItem(PiglinEntity piglin, boolean barter) {
		ItemStack itemStack = piglin.getStackInHand(Hand.OFF_HAND);
		piglin.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
		if (piglin.isAdult()) {
			boolean bl = acceptsForBarter(itemStack);
			if (barter && bl) {
				doBarter(piglin, getBarteredItem(piglin));
			} else if (!bl) {
				boolean bl2 = !piglin.tryEquip(itemStack).isEmpty();
				if (!bl2) {
					barterItem(piglin, itemStack);
				}
			}
		} else {
			boolean bl = !piglin.tryEquip(itemStack).isEmpty();
			if (!bl) {
				ItemStack itemStack2 = piglin.getMainHandStack();
				if (isGoldenItem(itemStack2)) {
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
			piglin.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
		}
	}

	private static void barterItem(PiglinEntity piglin, ItemStack stack) {
		ItemStack itemStack = piglin.addItem(stack);
		dropBarteredItem(piglin, Collections.singletonList(itemStack));
	}

	private static void doBarter(PiglinEntity piglin, List<ItemStack> items) {
		Optional<PlayerEntity> optional = piglin.getBrain().getOptionalRegisteredMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
		if (optional.isPresent()) {
			dropBarteredItem(piglin, (PlayerEntity)optional.get(), items);
		} else {
			dropBarteredItem(piglin, items);
		}
	}

	private static void dropBarteredItem(PiglinEntity piglin, List<ItemStack> items) {
		drop(piglin, items, findGround(piglin));
	}

	private static void dropBarteredItem(PiglinEntity piglin, PlayerEntity player, List<ItemStack> items) {
		drop(piglin, items, player.getPos());
	}

	private static void drop(PiglinEntity piglin, List<ItemStack> items, Vec3d pos) {
		if (!items.isEmpty()) {
			piglin.swingHand(Hand.OFF_HAND);

			for (ItemStack itemStack : items) {
				LookTargetUtil.give(piglin, itemStack, pos.add(0.0, 1.0, 0.0));
			}
		}
	}

	private static List<ItemStack> getBarteredItem(PiglinEntity piglin) {
		LootTable lootTable = piglin.getWorld().getServer().getReloadableRegistries().getLootTable(LootTables.PIGLIN_BARTERING_GAMEPLAY);
		List<ItemStack> list = lootTable.generateLoot(
			new LootContextParameterSet.Builder((ServerWorld)piglin.getWorld()).add(LootContextParameters.THIS_ENTITY, piglin).build(LootContextTypes.BARTER)
		);
		return list;
	}

	private static boolean isHuntingTarget(LivingEntity piglin, LivingEntity target) {
		return target.getType() != EntityType.HOGLIN ? false : Random.create(piglin.getWorld().getTime()).nextFloat() < 0.1F;
	}

	protected static boolean canGather(PiglinEntity piglin, ItemStack stack) {
		if (piglin.isBaby() && stack.isIn(ItemTags.IGNORED_BY_PIGLIN_BABIES)) {
			return false;
		} else if (stack.isIn(ItemTags.PIGLIN_REPELLENTS)) {
			return false;
		} else if (hasBeenHitByPlayer(piglin) && piglin.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET)) {
			return false;
		} else if (acceptsForBarter(stack)) {
			return doesNotHaveGoldInOffHand(piglin);
		} else {
			boolean bl = piglin.canInsertIntoInventory(stack);
			if (stack.isOf(Items.GOLD_NUGGET)) {
				return bl;
			} else if (isFood(stack)) {
				return !hasAteRecently(piglin) && bl;
			} else {
				return !isGoldenItem(stack) ? piglin.canEquipStack(stack) : doesNotHaveGoldInOffHand(piglin) && bl;
			}
		}
	}

	public static boolean isGoldenItem(ItemStack stack) {
		return stack.isIn(ItemTags.PIGLIN_LOVED);
	}

	private static boolean canRide(PiglinEntity piglin, Entity ridden) {
		return !(ridden instanceof MobEntity mobEntity)
			? false
			: !mobEntity.isBaby()
				|| !mobEntity.isAlive()
				|| hasBeenHurt(piglin)
				|| hasBeenHurt(mobEntity)
				|| mobEntity instanceof PiglinEntity && mobEntity.getVehicle() == null;
	}

	private static boolean isPreferredAttackTarget(PiglinEntity piglin, LivingEntity target) {
		return getPreferredTarget(piglin).filter(preferredTarget -> preferredTarget == target).isPresent();
	}

	private static boolean getNearestZombifiedPiglin(PiglinEntity piglin) {
		Brain<PiglinEntity> brain = piglin.getBrain();
		if (brain.hasMemoryModule(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED)) {
			LivingEntity livingEntity = (LivingEntity)brain.getOptionalRegisteredMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED).get();
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
			Optional<LivingEntity> optional = LookTargetUtil.getEntity(piglin, MemoryModuleType.ANGRY_AT);
			if (optional.isPresent() && Sensor.testAttackableTargetPredicateIgnoreVisibility(piglin, (LivingEntity)optional.get())) {
				return optional;
			} else {
				if (brain.hasMemoryModule(MemoryModuleType.UNIVERSAL_ANGER)) {
					Optional<PlayerEntity> optional2 = brain.getOptionalRegisteredMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);
					if (optional2.isPresent()) {
						return optional2;
					}
				}

				Optional<MobEntity> optional2 = brain.getOptionalRegisteredMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
				if (optional2.isPresent()) {
					return optional2;
				} else {
					Optional<PlayerEntity> optional3 = brain.getOptionalRegisteredMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);
					return optional3.isPresent() && Sensor.testAttackableTargetPredicate(piglin, (LivingEntity)optional3.get()) ? optional3 : Optional.empty();
				}
			}
		}
	}

	public static void onGuardedBlockInteracted(PlayerEntity player, boolean blockOpen) {
		List<PiglinEntity> list = player.getWorld().getNonSpectatingEntities(PiglinEntity.class, player.getBoundingBox().expand(16.0));
		list.stream().filter(PiglinBrain::hasIdleActivity).filter(piglin -> !blockOpen || LookTargetUtil.isVisibleInMemory(piglin, player)).forEach(piglin -> {
			if (piglin.getWorld().getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER)) {
				becomeAngryWithPlayer(piglin, player);
			} else {
				becomeAngryWith(piglin, player);
			}
		});
	}

	public static ActionResult playerInteract(PiglinEntity piglin, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (isWillingToTrade(piglin, itemStack)) {
			ItemStack itemStack2 = itemStack.splitUnlessCreative(1, player);
			swapItemWithOffHand(piglin, itemStack2);
			setAdmiringItem(piglin);
			stopWalking(piglin);
			return ActionResult.CONSUME;
		} else {
			return ActionResult.PASS;
		}
	}

	protected static boolean isWillingToTrade(PiglinEntity piglin, ItemStack nearbyItems) {
		return !hasBeenHitByPlayer(piglin) && !isAdmiringItem(piglin) && piglin.isAdult() && acceptsForBarter(nearbyItems);
	}

	protected static void onAttacked(PiglinEntity piglin, LivingEntity attacker) {
		if (!(attacker instanceof PiglinEntity)) {
			if (hasItemInOffHand(piglin)) {
				consumeOffHandItem(piglin, false);
			}

			Brain<PiglinEntity> brain = piglin.getBrain();
			brain.forget(MemoryModuleType.CELEBRATE_LOCATION);
			brain.forget(MemoryModuleType.DANCING);
			brain.forget(MemoryModuleType.ADMIRING_ITEM);
			if (attacker instanceof PlayerEntity) {
				brain.remember(MemoryModuleType.ADMIRING_DISABLED, true, 400L);
			}

			getAvoiding(piglin).ifPresent(avoiding -> {
				if (avoiding.getType() != attacker.getType()) {
					brain.forget(MemoryModuleType.AVOID_TARGET);
				}
			});
			if (piglin.isBaby()) {
				brain.remember(MemoryModuleType.AVOID_TARGET, attacker, 100L);
				if (Sensor.testAttackableTargetPredicateIgnoreVisibility(piglin, attacker)) {
					angerAtCloserTargets(piglin, attacker);
				}
			} else if (attacker.getType() == EntityType.HOGLIN && hasOutnumberedHoglins(piglin)) {
				runAwayFrom(piglin, attacker);
				groupRunAwayFrom(piglin, attacker);
			} else {
				tryRevenge(piglin, attacker);
			}
		}
	}

	protected static void tryRevenge(AbstractPiglinEntity piglin, LivingEntity target) {
		if (!piglin.getBrain().hasActivity(Activity.AVOID)) {
			if (Sensor.testAttackableTargetPredicateIgnoreVisibility(piglin, target)) {
				if (!LookTargetUtil.isNewTargetTooFar(piglin, target, 4.0)) {
					if (target.getType() == EntityType.PLAYER && piglin.getWorld().getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER)) {
						becomeAngryWithPlayer(piglin, target);
						angerNearbyPiglins(piglin);
					} else {
						becomeAngryWith(piglin, target);
						angerAtCloserTargets(piglin, target);
					}
				}
			}
		}
	}

	public static Optional<SoundEvent> getCurrentActivitySound(PiglinEntity piglin) {
		return piglin.getBrain().getFirstPossibleNonCoreActivity().map(activity -> getSound(piglin, activity));
	}

	private static SoundEvent getSound(PiglinEntity piglin, Activity activity) {
		if (activity == Activity.FIGHT) {
			return SoundEvents.ENTITY_PIGLIN_ANGRY;
		} else if (piglin.shouldZombify()) {
			return SoundEvents.ENTITY_PIGLIN_RETREAT;
		} else if (activity == Activity.AVOID && hasTargetToAvoid(piglin)) {
			return SoundEvents.ENTITY_PIGLIN_RETREAT;
		} else if (activity == Activity.ADMIRE_ITEM) {
			return SoundEvents.ENTITY_PIGLIN_ADMIRING_ITEM;
		} else if (activity == Activity.CELEBRATE) {
			return SoundEvents.ENTITY_PIGLIN_CELEBRATE;
		} else if (hasPlayerHoldingWantedItemNearby(piglin)) {
			return SoundEvents.ENTITY_PIGLIN_JEALOUS;
		} else {
			return hasSoulFireNearby(piglin) ? SoundEvents.ENTITY_PIGLIN_RETREAT : SoundEvents.ENTITY_PIGLIN_AMBIENT;
		}
	}

	private static boolean hasTargetToAvoid(PiglinEntity piglin) {
		Brain<PiglinEntity> brain = piglin.getBrain();
		return !brain.hasMemoryModule(MemoryModuleType.AVOID_TARGET)
			? false
			: ((LivingEntity)brain.getOptionalRegisteredMemory(MemoryModuleType.AVOID_TARGET).get()).isInRange(piglin, 12.0);
	}

	protected static List<AbstractPiglinEntity> getNearbyVisiblePiglins(PiglinEntity piglin) {
		return (List<AbstractPiglinEntity>)piglin.getBrain().getOptionalRegisteredMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS).orElse(ImmutableList.of());
	}

	private static List<AbstractPiglinEntity> getNearbyPiglins(AbstractPiglinEntity piglin) {
		return (List<AbstractPiglinEntity>)piglin.getBrain().getOptionalRegisteredMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS).orElse(ImmutableList.of());
	}

	public static boolean wearsGoldArmor(LivingEntity entity) {
		for (ItemStack itemStack : entity.getAllArmorItems()) {
			Item item = itemStack.getItem();
			if (item instanceof ArmorItem && ((ArmorItem)item).getMaterial().matches(ArmorMaterials.GOLD)) {
				return true;
			}
		}

		return false;
	}

	private static void stopWalking(PiglinEntity piglin) {
		piglin.getBrain().forget(MemoryModuleType.WALK_TARGET);
		piglin.getNavigation().stop();
	}

	private static Task<LivingEntity> makeRememberRideableHoglinTask() {
		LookAtMobWithIntervalTask.Interval interval = new LookAtMobWithIntervalTask.Interval(MEMORY_TRANSFER_TASK_DURATION);
		return MemoryTransferTask.create(
			entity -> entity.isBaby() && interval.shouldRun(entity.getWorld().random),
			MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN,
			MemoryModuleType.RIDE_TARGET,
			RIDE_TARGET_MEMORY_DURATION
		);
	}

	public static void angerAtCloserTargets(AbstractPiglinEntity piglin, LivingEntity target) {
		getNearbyPiglins(piglin).forEach(nearbyPiglin -> {
			if (target.getType() != EntityType.HOGLIN || nearbyPiglin.canHunt() && ((HoglinEntity)target).canBeHunted()) {
				angerAtIfCloser(nearbyPiglin, target);
			}
		});
	}

	protected static void angerNearbyPiglins(AbstractPiglinEntity piglin) {
		getNearbyPiglins(piglin).forEach(nearbyPiglin -> getNearestDetectedPlayer(nearbyPiglin).ifPresent(player -> becomeAngryWith(nearbyPiglin, player)));
	}

	public static void becomeAngryWith(AbstractPiglinEntity piglin, LivingEntity target) {
		if (Sensor.testAttackableTargetPredicateIgnoreVisibility(piglin, target)) {
			piglin.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
			piglin.getBrain().remember(MemoryModuleType.ANGRY_AT, target.getUuid(), 600L);
			if (target.getType() == EntityType.HOGLIN && piglin.canHunt()) {
				rememberHunting(piglin);
			}

			if (target.getType() == EntityType.PLAYER && piglin.getWorld().getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER)) {
				piglin.getBrain().remember(MemoryModuleType.UNIVERSAL_ANGER, true, 600L);
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
		return LookTargetUtil.getEntity(piglin, MemoryModuleType.ANGRY_AT);
	}

	public static Optional<LivingEntity> getAvoiding(PiglinEntity piglin) {
		return piglin.getBrain().hasMemoryModule(MemoryModuleType.AVOID_TARGET)
			? piglin.getBrain().getOptionalRegisteredMemory(MemoryModuleType.AVOID_TARGET)
			: Optional.empty();
	}

	public static Optional<PlayerEntity> getNearestDetectedPlayer(AbstractPiglinEntity piglin) {
		return piglin.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER)
			? piglin.getBrain().getOptionalRegisteredMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER)
			: Optional.empty();
	}

	private static void groupRunAwayFrom(PiglinEntity piglin, LivingEntity target) {
		getNearbyVisiblePiglins(piglin)
			.stream()
			.filter(nearbyVisiblePiglin -> nearbyVisiblePiglin instanceof PiglinEntity)
			.forEach(piglinx -> runAwayFromClosestTarget((PiglinEntity)piglinx, target));
	}

	private static void runAwayFromClosestTarget(PiglinEntity piglin, LivingEntity target) {
		Brain<PiglinEntity> brain = piglin.getBrain();
		LivingEntity livingEntity = LookTargetUtil.getCloserEntity(piglin, brain.getOptionalRegisteredMemory(MemoryModuleType.AVOID_TARGET), target);
		livingEntity = LookTargetUtil.getCloserEntity(piglin, brain.getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET), livingEntity);
		runAwayFrom(piglin, livingEntity);
	}

	private static boolean shouldRunAwayFromHoglins(PiglinEntity piglin) {
		Brain<PiglinEntity> brain = piglin.getBrain();
		if (!brain.hasMemoryModule(MemoryModuleType.AVOID_TARGET)) {
			return true;
		} else {
			LivingEntity livingEntity = (LivingEntity)brain.getOptionalRegisteredMemory(MemoryModuleType.AVOID_TARGET).get();
			EntityType<?> entityType = livingEntity.getType();
			if (entityType == EntityType.HOGLIN) {
				return hasNoAdvantageAgainstHoglins(piglin);
			} else {
				return isZombified(entityType) ? !brain.hasMemoryModuleWithValue(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, livingEntity) : false;
			}
		}
	}

	private static boolean hasNoAdvantageAgainstHoglins(PiglinEntity piglin) {
		return !hasOutnumberedHoglins(piglin);
	}

	private static boolean hasOutnumberedHoglins(PiglinEntity piglins) {
		int i = (Integer)piglins.getBrain().getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT).orElse(0) + 1;
		int j = (Integer)piglins.getBrain().getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT).orElse(0);
		return j > i;
	}

	private static void runAwayFrom(PiglinEntity piglin, LivingEntity target) {
		piglin.getBrain().forget(MemoryModuleType.ANGRY_AT);
		piglin.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
		piglin.getBrain().forget(MemoryModuleType.WALK_TARGET);
		piglin.getBrain().remember(MemoryModuleType.AVOID_TARGET, target, (long)AVOID_MEMORY_DURATION.get(piglin.getWorld().random));
		rememberHunting(piglin);
	}

	public static void rememberHunting(AbstractPiglinEntity piglin) {
		piglin.getBrain().remember(MemoryModuleType.HUNTED_RECENTLY, true, (long)HUNT_MEMORY_DURATION.get(piglin.getWorld().random));
	}

	private static void setEatenRecently(PiglinEntity piglin) {
		piglin.getBrain().remember(MemoryModuleType.ATE_RECENTLY, true, 200L);
	}

	private static Vec3d findGround(PiglinEntity piglin) {
		Vec3d vec3d = FuzzyTargeting.find(piglin, 4, 2);
		return vec3d == null ? piglin.getPos() : vec3d;
	}

	private static boolean hasAteRecently(PiglinEntity piglin) {
		return piglin.getBrain().hasMemoryModule(MemoryModuleType.ATE_RECENTLY);
	}

	protected static boolean hasIdleActivity(AbstractPiglinEntity piglin) {
		return piglin.getBrain().hasActivity(Activity.IDLE);
	}

	private static boolean isHoldingCrossbow(LivingEntity piglin) {
		return piglin.isHolding(Items.CROSSBOW);
	}

	private static void setAdmiringItem(LivingEntity entity) {
		entity.getBrain().remember(MemoryModuleType.ADMIRING_ITEM, true, 119L);
	}

	private static boolean isAdmiringItem(PiglinEntity entity) {
		return entity.getBrain().hasMemoryModule(MemoryModuleType.ADMIRING_ITEM);
	}

	private static boolean acceptsForBarter(ItemStack stack) {
		return stack.isOf(BARTERING_ITEM);
	}

	private static boolean isFood(ItemStack stack) {
		return stack.isIn(ItemTags.PIGLIN_FOOD);
	}

	private static boolean hasSoulFireNearby(PiglinEntity piglin) {
		return piglin.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_REPELLENT);
	}

	private static boolean hasPlayerHoldingWantedItemNearby(LivingEntity entity) {
		return entity.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM);
	}

	private static boolean canWander(LivingEntity piglin) {
		return !hasPlayerHoldingWantedItemNearby(piglin);
	}

	public static boolean isGoldHoldingPlayer(LivingEntity target) {
		return target.getType() == EntityType.PLAYER && target.isHolding(PiglinBrain::isGoldenItem);
	}

	private static boolean hasBeenHitByPlayer(PiglinEntity piglin) {
		return piglin.getBrain().hasMemoryModule(MemoryModuleType.ADMIRING_DISABLED);
	}

	private static boolean hasBeenHurt(LivingEntity piglin) {
		return piglin.getBrain().hasMemoryModule(MemoryModuleType.HURT_BY);
	}

	private static boolean hasItemInOffHand(PiglinEntity piglin) {
		return !piglin.getOffHandStack().isEmpty();
	}

	private static boolean doesNotHaveGoldInOffHand(PiglinEntity piglin) {
		return piglin.getOffHandStack().isEmpty() || !isGoldenItem(piglin.getOffHandStack());
	}

	public static boolean isZombified(EntityType<?> entityType) {
		return entityType == EntityType.ZOMBIFIED_PIGLIN || entityType == EntityType.ZOGLIN;
	}
}
