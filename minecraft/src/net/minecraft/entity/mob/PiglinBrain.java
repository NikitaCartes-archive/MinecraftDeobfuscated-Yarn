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
import net.minecraft.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.IntRange;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;

public class PiglinBrain {
	protected static final Item BARTERING_ITEM = Items.GOLD_INGOT;
	private static final IntRange HUNT_MEMORY_DURATION = Durations.betweenSeconds(30, 120);
	private static final IntRange MEMORY_TRANSFER_TASK_DURATION = Durations.betweenSeconds(10, 40);
	private static final IntRange RIDE_TARGET_MEMORY_DURATION = Durations.betweenSeconds(10, 30);
	private static final IntRange AVOID_MEMORY_DURATION = Durations.betweenSeconds(5, 20);
	private static final IntRange field_25384 = Durations.betweenSeconds(5, 7);
	private static final Set<Item> FOOD = ImmutableSet.of(Items.PORKCHOP, Items.COOKED_PORKCHOP);

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

	protected static void setHuntedRecently(PiglinEntity piglin) {
		int i = HUNT_MEMORY_DURATION.choose(piglin.world.random);
		piglin.getBrain().remember(MemoryModuleType.HUNTED_RECENTLY, true, (long)i);
	}

	private static void addCoreActivities(Brain<PiglinEntity> brain) {
		brain.setTaskList(
			Activity.CORE,
			0,
			ImmutableList.of(
				new LookAroundTask(45, 90),
				new WanderAroundTask(200),
				new OpenDoorsTask(),
				makeGoToZombifiedPiglinTask(),
				new RemoveOffHandItemTask(),
				new AdmireItemTask(120),
				new DefeatTargetTask(300, PiglinBrain::method_29276),
				new ForgetAngryAtTargetTask()
			)
		);
	}

	private static void addIdleActivities(Brain<PiglinEntity> brain) {
		brain.setTaskList(
			Activity.IDLE,
			10,
			ImmutableList.of(
				new FollowMobTask(PiglinBrain::isGoldHoldingPlayer, 14.0F),
				new UpdateAttackTargetTask<>(PiglinEntity::isAdult, PiglinBrain::getPreferredTarget),
				new ConditionalTask(PiglinEntity::canHunt, new HuntHoglinTask<>()),
				makeGoToSoulFireTask(),
				makeRememberRideableHoglinTask(),
				makeRandomFollowTask(),
				makeRandomWanderTask(),
				new FindInteractionTargetTask(EntityType.PLAYER, 4)
			)
		);
	}

	private static void addFightActivities(PiglinEntity piglin, Brain<PiglinEntity> brain) {
		brain.setTaskList(
			Activity.FIGHT,
			10,
			ImmutableList.of(
				new ForgetAttackTargetTask<>(livingEntity -> !isPreferredAttackTarget(piglin, livingEntity)),
				new ConditionalTask(PiglinBrain::isHoldingCrossbow, new AttackTask<>(5, 0.75F)),
				new RangedApproachTask(1.0F),
				new MeleeAttackTask(20),
				new CrossbowAttackTask(),
				new HuntFinishTask(),
				new ForgetTask(PiglinBrain::method_29537, MemoryModuleType.ATTACK_TARGET)
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
				new FollowMobTask(PiglinBrain::isGoldHoldingPlayer, 14.0F),
				new UpdateAttackTargetTask(PiglinEntity::isAdult, PiglinBrain::getPreferredTarget),
				new ConditionalTask(piglinEntity -> !piglinEntity.isDancing(), new GoToCelebrateTask(2, 1.0F)),
				new ConditionalTask(PiglinEntity::isDancing, new GoToCelebrateTask(4, 0.6F)),
				new RandomTask(
					ImmutableList.of(Pair.of(new FollowMobTask(EntityType.PIGLIN, 8.0F), 1), Pair.of(new StrollTask(0.6F, 2, 1), 1), Pair.of(new WaitTask(10, 20), 1))
				)
			),
			MemoryModuleType.CELEBRATE_LOCATION
		);
	}

	private static void addAdmireItemActivities(Brain<PiglinEntity> brain) {
		brain.setTaskList(
			Activity.ADMIRE_ITEM,
			10,
			ImmutableList.of(new WalkToNearestVisibleWantedItemTask<>(PiglinBrain::doesNotHaveGoldInOffHand, 1.0F, true, 9), new WantNewItemTask(9)),
			MemoryModuleType.ADMIRING_ITEM
		);
	}

	private static void addAvoidActivities(Brain<PiglinEntity> brain) {
		brain.setTaskList(
			Activity.AVOID,
			10,
			ImmutableList.of(
				GoToRememberedPositionTask.toEntity(MemoryModuleType.AVOID_TARGET, 1.1F, 6, true),
				makeRandomFollowTask(),
				makeRandomWanderTask(),
				new ForgetTask(PiglinBrain::shouldRunAwayFromHoglins, MemoryModuleType.AVOID_TARGET)
			),
			MemoryModuleType.AVOID_TARGET
		);
	}

	private static void addRideActivities(Brain<PiglinEntity> brain) {
		brain.setTaskList(
			Activity.RIDE,
			10,
			ImmutableList.of(
				new StartRidingTask<>(0.8F),
				new FollowMobTask(PiglinBrain::isGoldHoldingPlayer, 8.0F),
				new ConditionalTask(Entity::hasVehicle, makeRandomFollowTask()),
				new RidingTask(8, PiglinBrain::canRide)
			),
			MemoryModuleType.RIDE_TARGET
		);
	}

	private static RandomTask<PiglinEntity> makeRandomFollowTask() {
		return new RandomTask<>(
			ImmutableList.of(
				Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0F), 1),
				Pair.of(new FollowMobTask(EntityType.PIGLIN, 8.0F), 1),
				Pair.of(new FollowMobTask(8.0F), 1),
				Pair.of(new WaitTask(30, 60), 1)
			)
		);
	}

	private static RandomTask<PiglinEntity> makeRandomWanderTask() {
		return new RandomTask<>(
			ImmutableList.of(
				Pair.of(new StrollTask(0.6F), 2),
				Pair.of(FindEntityTask.create(EntityType.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2),
				Pair.of(new ConditionalTask<>(PiglinBrain::canWander, new GoTowardsLookTarget(0.6F, 3)), 2),
				Pair.of(new WaitTask(30, 60), 1)
			)
		);
	}

	private static GoToRememberedPositionTask<BlockPos> makeGoToSoulFireTask() {
		return GoToRememberedPositionTask.toBlock(MemoryModuleType.NEAREST_REPELLENT, 1.1F, 8, false);
	}

	private static MemoryTransferTask<PiglinEntity, LivingEntity> makeGoToZombifiedPiglinTask() {
		return new MemoryTransferTask<>(PiglinBrain::method_29537, MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, MemoryModuleType.AVOID_TARGET, field_25384);
	}

	protected static void tickActivities(PiglinEntity piglin) {
		Brain<PiglinEntity> brain = piglin.getBrain();
		Activity activity = (Activity)brain.getFirstPossibleNonCoreActivity().orElse(null);
		brain.resetPossibleActivities(ImmutableList.of(Activity.ADMIRE_ITEM, Activity.FIGHT, Activity.AVOID, Activity.CELEBRATE, Activity.RIDE, Activity.IDLE));
		Activity activity2 = (Activity)brain.getFirstPossibleNonCoreActivity().orElse(null);
		if (activity != activity2) {
			playSound(piglin);
		}

		piglin.setAttacking(brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
		if (!brain.hasMemoryModule(MemoryModuleType.RIDE_TARGET) && method_29277(piglin)) {
			piglin.stopRiding();
		}

		if (!brain.hasMemoryModule(MemoryModuleType.CELEBRATE_LOCATION)) {
			brain.forget(MemoryModuleType.DANCING);
		}

		piglin.setDancing(brain.hasMemoryModule(MemoryModuleType.DANCING));
	}

	private static boolean method_29277(PiglinEntity piglinEntity) {
		if (!piglinEntity.isBaby()) {
			return false;
		} else {
			Entity entity = piglinEntity.getVehicle();
			return entity instanceof PiglinEntity && ((PiglinEntity)entity).isBaby() || entity instanceof HoglinEntity && ((HoglinEntity)entity).isBaby();
		}
	}

	protected static void loot(PiglinEntity piglin, ItemEntity drop) {
		stopWalking(piglin);
		piglin.sendPickup(drop, 1);
		ItemStack itemStack = method_24848(drop);
		Item item = itemStack.getItem();
		if (isGoldenItem(item)) {
			if (hasItemInOffHand(piglin)) {
				piglin.dropStack(piglin.getStackInHand(Hand.OFF_HAND));
			}

			piglin.equipToOffHand(itemStack);
			setAdmiringItem(piglin);
		} else if (isFood(item) && !hasAteRecently(piglin)) {
			setEatenRecently(piglin);
		} else {
			boolean bl = piglin.tryEquip(itemStack);
			if (!bl) {
				method_24849(piglin, itemStack);
			}
		}
	}

	private static ItemStack method_24848(ItemEntity stack) {
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
		ItemStack itemStack = piglin.getStackInHand(Hand.OFF_HAND);
		piglin.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
		if (piglin.isAdult()) {
			boolean bl2 = acceptsForBarter(itemStack.getItem());
			if (bl && bl2) {
				doBarter(piglin, getBarteredItem(piglin));
			} else if (!bl2) {
				boolean bl3 = piglin.tryEquip(itemStack);
				if (!bl3) {
					method_24849(piglin, itemStack);
				}
			}
		} else {
			boolean bl2 = piglin.tryEquip(itemStack);
			if (!bl2) {
				ItemStack itemStack2 = piglin.getMainHandStack();
				if (isGoldenItem(itemStack2.getItem())) {
					method_24849(piglin, itemStack2);
				} else {
					doBarter(piglin, Collections.singletonList(itemStack2));
				}

				piglin.equipToMainHand(itemStack);
			}
		}
	}

	protected static void method_25948(PiglinEntity piglinEntity) {
		if (isAdmiringItem(piglinEntity) && !piglinEntity.getOffHandStack().isEmpty()) {
			piglinEntity.dropStack(piglinEntity.getOffHandStack());
			piglinEntity.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
		}
	}

	private static void method_24849(PiglinEntity piglin, ItemStack stack) {
		ItemStack itemStack = piglin.addItem(stack);
		dropBarteredItem(piglin, Collections.singletonList(itemStack));
	}

	private static void doBarter(PiglinEntity piglin, List<ItemStack> list) {
		Optional<PlayerEntity> optional = piglin.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
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

	private static void drop(PiglinEntity piglinEntity, List<ItemStack> list, Vec3d vec3d) {
		if (!list.isEmpty()) {
			piglinEntity.swingHand(Hand.OFF_HAND);

			for (ItemStack itemStack : list) {
				LookTargetUtil.give(piglinEntity, itemStack, vec3d.add(0.0, 1.0, 0.0));
			}
		}
	}

	private static List<ItemStack> getBarteredItem(PiglinEntity piglin) {
		LootTable lootTable = piglin.world.getServer().getLootManager().getTable(LootTables.PIGLIN_BARTERING_GAMEPLAY);
		return lootTable.generateLoot(
			new LootContext.Builder((ServerWorld)piglin.world)
				.parameter(LootContextParameters.THIS_ENTITY, piglin)
				.random(piglin.world.random)
				.build(LootContextTypes.BARTER)
		);
	}

	private static boolean method_29276(LivingEntity livingEntity, LivingEntity livingEntity2) {
		return livingEntity2.getType() != EntityType.HOGLIN ? false : new Random(livingEntity.world.getTime()).nextFloat() < 0.1F;
	}

	protected static boolean canGather(PiglinEntity piglin, ItemStack stack) {
		Item item = stack.getItem();
		if (item.isIn(ItemTags.PIGLIN_REPELLENTS)) {
			return false;
		} else if (hasBeenHitByPlayer(piglin) && piglin.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET)) {
			return false;
		} else if (acceptsForBarter(item)) {
			return doesNotHaveGoldInOffHand(piglin);
		} else {
			boolean bl = piglin.canInsertIntoInventory(stack);
			if (item == Items.GOLD_NUGGET) {
				return bl;
			} else if (isFood(item)) {
				return !hasAteRecently(piglin) && bl;
			} else {
				return !isGoldenItem(item) ? piglin.method_24846(stack) : doesNotHaveGoldInOffHand(piglin) && bl;
			}
		}
	}

	public static boolean isGoldenItem(Item item) {
		return item.isIn(ItemTags.PIGLIN_LOVED);
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

	private static boolean method_29537(PiglinEntity piglinEntity) {
		Brain<PiglinEntity> brain = piglinEntity.getBrain();
		if (brain.hasMemoryModule(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED)) {
			LivingEntity livingEntity = (LivingEntity)brain.getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED).get();
			return piglinEntity.isInRange(livingEntity, 6.0);
		} else {
			return false;
		}
	}

	private static Optional<? extends LivingEntity> getPreferredTarget(PiglinEntity piglin) {
		Brain<PiglinEntity> brain = piglin.getBrain();
		if (method_29537(piglin)) {
			return Optional.empty();
		} else {
			Optional<LivingEntity> optional = LookTargetUtil.getEntity(piglin, MemoryModuleType.ANGRY_AT);
			if (optional.isPresent() && shouldAttack((LivingEntity)optional.get())) {
				return optional;
			} else {
				if (brain.hasMemoryModule(MemoryModuleType.UNIVERSAL_ANGER)) {
					Optional<PlayerEntity> optional2 = brain.getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);
					if (optional2.isPresent()) {
						return optional2;
					}
				}

				Optional<MobEntity> optional2 = brain.getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
				if (optional2.isPresent()) {
					return optional2;
				} else {
					Optional<PlayerEntity> optional3 = brain.getOptionalMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);
					return optional3.isPresent() && shouldAttack((LivingEntity)optional3.get()) ? optional3 : Optional.empty();
				}
			}
		}
	}

	public static void onGoldBlockBroken(PlayerEntity player) {
		if (shouldAttack(player)) {
			List<PiglinEntity> list = player.world.getNonSpectatingEntities(PiglinEntity.class, player.getBoundingBox().expand(16.0));
			list.stream()
				.filter(PiglinBrain::hasIdleActivity)
				.filter(piglin -> LookTargetUtil.isVisibleInMemory(piglin, player))
				.forEach(piglin -> angerAt(piglin, player));
		}
	}

	public static ActionResult playerInteract(PiglinEntity piglin, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (method_27086(piglin, itemStack)) {
			ItemStack itemStack2 = itemStack.split(1);
			piglin.equipToOffHand(itemStack2);
			setAdmiringItem(piglin);
			return ActionResult.CONSUME;
		} else {
			return ActionResult.PASS;
		}
	}

	protected static boolean method_27086(PiglinEntity piglinEntity, ItemStack itemStack) {
		return !hasBeenHitByPlayer(piglinEntity) && !isAdmiringItem(piglinEntity) && piglinEntity.isAdult() && acceptsForBarter(itemStack.getItem());
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

			method_29536(piglin).ifPresent(livingEntity2 -> {
				if (livingEntity2.getType() != attacker.getType()) {
					brain.forget(MemoryModuleType.AVOID_TARGET);
				}
			});
			if (piglin.isBaby()) {
				brain.remember(MemoryModuleType.AVOID_TARGET, attacker, 100L);
				if (shouldAttack(attacker)) {
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

	private static void tryRevenge(PiglinEntity piglin, LivingEntity target) {
		if (!piglin.getBrain().hasActivity(Activity.AVOID)) {
			if (shouldAttack(target)) {
				if (!LookTargetUtil.isNewTargetTooFar(piglin, target, 4.0)) {
					angerAt(piglin, target);
					angerAtCloserTargets(piglin, target);
				}
			}
		}
	}

	private static void playSound(PiglinEntity piglin) {
		piglin.getBrain().getFirstPossibleNonCoreActivity().ifPresent(activity -> {
			if (activity == Activity.FIGHT) {
				piglin.playAngrySound();
			} else if (piglin.canConvert()) {
				piglin.playRetreatSound();
			} else if (activity == Activity.AVOID && method_29538(piglin)) {
				piglin.playRetreatSound();
			} else if (activity == Activity.ADMIRE_ITEM) {
				piglin.playAdmireItemSound();
			} else if (activity == Activity.CELEBRATE) {
				piglin.playCelebrateSound();
			} else if (hasPlayerHoldingWantedItemNearby(piglin)) {
				piglin.playJealousSound();
			} else if (hasSoulFireNearby(piglin)) {
				piglin.playRetreatSound();
			}
		});
	}

	private static boolean method_29538(PiglinEntity piglinEntity) {
		Brain<PiglinEntity> brain = piglinEntity.getBrain();
		return !brain.hasMemoryModule(MemoryModuleType.AVOID_TARGET)
			? false
			: ((LivingEntity)brain.getOptionalMemory(MemoryModuleType.AVOID_TARGET).get()).isInRange(piglinEntity, 6.0);
	}

	protected static void playSoundAtChance(PiglinEntity piglin) {
		if ((double)piglin.world.random.nextFloat() < 0.0125) {
			playSound(piglin);
		}
	}

	public static boolean haveHuntedHoglinsRecently(PiglinEntity piglin) {
		return piglin.getBrain().hasMemoryModule(MemoryModuleType.HUNTED_RECENTLY)
			|| getNearbyVisiblePiglins(piglin).stream().anyMatch(piglinEntity -> piglinEntity.getBrain().hasMemoryModule(MemoryModuleType.HUNTED_RECENTLY));
	}

	private static List<PiglinEntity> getNearbyVisiblePiglins(PiglinEntity piglin) {
		return (List<PiglinEntity>)piglin.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS).orElse(ImmutableList.of());
	}

	private static List<PiglinEntity> getNearbyPiglins(PiglinEntity piglin) {
		return (List<PiglinEntity>)piglin.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_ADULT_PIGLINS).orElse(ImmutableList.of());
	}

	public static boolean wearsGoldArmor(LivingEntity entity) {
		for (ItemStack itemStack : entity.getArmorItems()) {
			Item item = itemStack.getItem();
			if (item instanceof ArmorItem && ((ArmorItem)item).getMaterial() == ArmorMaterials.GOLD) {
				return true;
			}
		}

		return false;
	}

	private static void stopWalking(PiglinEntity piglin) {
		piglin.getBrain().forget(MemoryModuleType.WALK_TARGET);
		piglin.getNavigation().stop();
	}

	private static TimeLimitedTask<PiglinEntity> makeRememberRideableHoglinTask() {
		return new TimeLimitedTask<>(
			new MemoryTransferTask<>(PiglinEntity::isBaby, MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.RIDE_TARGET, RIDE_TARGET_MEMORY_DURATION),
			MEMORY_TRANSFER_TASK_DURATION
		);
	}

	public static void angerAtCloserTargets(PiglinEntity piglin, LivingEntity target) {
		getNearbyPiglins(piglin).forEach(piglinEntity -> {
			if (target.getType() != EntityType.HOGLIN || piglinEntity.canHunt() && ((HoglinEntity)target).canBeHunted()) {
				angerAtIfCloser(piglinEntity, target);
			}
		});
	}

	public static void rememberGroupHunting(PiglinEntity piglin) {
		getNearbyVisiblePiglins(piglin).forEach(PiglinBrain::rememberHunting);
	}

	public static void angerAt(PiglinEntity piglin, LivingEntity target) {
		piglin.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
		piglin.getBrain().remember(MemoryModuleType.ANGRY_AT, target.getUuid(), 600L);
		if (target.getType() == EntityType.HOGLIN) {
			rememberHunting(piglin);
		}

		if (target.getType() == EntityType.PLAYER && piglin.world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER)) {
			piglin.getBrain().remember(MemoryModuleType.UNIVERSAL_ANGER, true, 600L);
		}
	}

	private static void angerAtIfCloser(PiglinEntity piglin, LivingEntity target) {
		Optional<LivingEntity> optional = getAngryAt(piglin);
		LivingEntity livingEntity = LookTargetUtil.getCloserEntity(piglin, optional, target);
		if (!optional.isPresent() || optional.get() != livingEntity) {
			angerAt(piglin, livingEntity);
		}
	}

	private static Optional<LivingEntity> getAngryAt(PiglinEntity piglin) {
		return LookTargetUtil.getEntity(piglin, MemoryModuleType.ANGRY_AT);
	}

	public static Optional<LivingEntity> method_29536(PiglinEntity piglinEntity) {
		return piglinEntity.getBrain().hasMemoryModule(MemoryModuleType.AVOID_TARGET)
			? piglinEntity.getBrain().getOptionalMemory(MemoryModuleType.AVOID_TARGET)
			: Optional.empty();
	}

	private static void groupRunAwayFrom(PiglinEntity piglin, LivingEntity target) {
		getNearbyVisiblePiglins(piglin).forEach(piglinx -> runAwayFromClosestTarget(piglinx, target));
	}

	private static void runAwayFromClosestTarget(PiglinEntity piglin, LivingEntity target) {
		Brain<PiglinEntity> brain = piglin.getBrain();
		LivingEntity livingEntity = LookTargetUtil.getCloserEntity(piglin, brain.getOptionalMemory(MemoryModuleType.AVOID_TARGET), target);
		livingEntity = LookTargetUtil.getCloserEntity(piglin, brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET), livingEntity);
		runAwayFrom(piglin, livingEntity);
	}

	private static boolean shouldRunAwayFromHoglins(PiglinEntity piglin) {
		Brain<PiglinEntity> brain = piglin.getBrain();
		if (!brain.hasMemoryModule(MemoryModuleType.AVOID_TARGET)) {
			return true;
		} else {
			LivingEntity livingEntity = (LivingEntity)brain.getOptionalMemory(MemoryModuleType.AVOID_TARGET).get();
			EntityType<?> entityType = livingEntity.getType();
			if (entityType == EntityType.HOGLIN) {
				return hasNoAdvantageAgainstHoglins(piglin);
			} else {
				return isZombified(entityType) ? !brain.method_29519(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, livingEntity) : false;
			}
		}
	}

	private static boolean hasNoAdvantageAgainstHoglins(PiglinEntity piglin) {
		return !hasOutnumberedHoglins(piglin);
	}

	private static boolean hasOutnumberedHoglins(PiglinEntity piglins) {
		int i = (Integer)piglins.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT).orElse(0) + 1;
		int j = (Integer)piglins.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT).orElse(0);
		return j > i;
	}

	private static void runAwayFrom(PiglinEntity piglin, LivingEntity target) {
		piglin.getBrain().forget(MemoryModuleType.ANGRY_AT);
		piglin.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
		piglin.getBrain().forget(MemoryModuleType.WALK_TARGET);
		piglin.getBrain().remember(MemoryModuleType.AVOID_TARGET, target, (long)AVOID_MEMORY_DURATION.choose(piglin.world.random));
		rememberHunting(piglin);
	}

	public static void rememberHunting(PiglinEntity piglin) {
		piglin.getBrain().remember(MemoryModuleType.HUNTED_RECENTLY, true, (long)HUNT_MEMORY_DURATION.choose(piglin.world.random));
	}

	private static void setEatenRecently(PiglinEntity piglin) {
		piglin.getBrain().remember(MemoryModuleType.ATE_RECENTLY, true, 200L);
	}

	private static Vec3d findGround(PiglinEntity piglin) {
		Vec3d vec3d = TargetFinder.findGroundTarget(piglin, 4, 2);
		return vec3d == null ? piglin.getPos() : vec3d;
	}

	private static boolean hasAteRecently(PiglinEntity piglin) {
		return piglin.getBrain().hasMemoryModule(MemoryModuleType.ATE_RECENTLY);
	}

	protected static boolean hasIdleActivity(PiglinEntity piglin) {
		return piglin.getBrain().hasActivity(Activity.IDLE);
	}

	private static boolean isHoldingCrossbow(LivingEntity piglin) {
		return piglin.isHolding(Items.CROSSBOW);
	}

	private static void setAdmiringItem(LivingEntity entity) {
		entity.getBrain().remember(MemoryModuleType.ADMIRING_ITEM, true, 120L);
	}

	private static boolean isAdmiringItem(PiglinEntity entity) {
		return entity.getBrain().hasMemoryModule(MemoryModuleType.ADMIRING_ITEM);
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
		return piglin.getOffHandStack().isEmpty() || !isGoldenItem(piglin.getOffHandStack().getItem());
	}

	public static boolean isZombified(EntityType entityType) {
		return entityType == EntityType.ZOMBIFIED_PIGLIN || entityType == EntityType.ZOGLIN;
	}
}
