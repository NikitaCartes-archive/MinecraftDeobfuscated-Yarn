package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
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
import net.minecraft.entity.ai.brain.task.WanderWithOffHandItemTask;
import net.minecraft.entity.ai.brain.task.WantNewItemTask;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DynamicSerializableUuid;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.IntRange;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PiglinBrain {
	private static final IntRange HUNT_MEMORY_DURATION = Durations.betweenSeconds(30, 120);
	private static final IntRange MEMORY_TRANSFER_TASK_DURATION = Durations.betweenSeconds(10, 40);
	private static final IntRange RIDE_TARGET_MEMORY_DURATION = Durations.betweenSeconds(10, 30);
	private static final IntRange AVOID_MEMORY_DURATION = Durations.betweenSeconds(5, 20);
	private static final Set FOOD = ImmutableSet.of(Items.PORKCHOP, Items.COOKED_PORKCHOP);
	private static final Set<Item> GOLDEN_ITEMS = ImmutableSet.of(
		Items.GOLD_INGOT,
		Items.GOLDEN_APPLE,
		Items.GOLDEN_HORSE_ARMOR,
		Items.GOLDEN_CARROT,
		Items.GOLD_BLOCK,
		Items.GOLD_ORE,
		Items.ENCHANTED_GOLDEN_APPLE,
		Items.GOLDEN_HORSE_ARMOR,
		Items.LIGHT_WEIGHTED_PRESSURE_PLATE,
		Items.BELL,
		Items.GLISTERING_MELON_SLICE,
		Items.CLOCK
	);

	protected static Brain<?> create(PiglinEntity piglin, Dynamic<?> data) {
		Brain<PiglinEntity> brain = new Brain<>(PiglinEntity.MEMORY_MODULE_TYPES, PiglinEntity.SENSOR_TYPES, data);
		addCoreActivities(piglin, brain);
		addIdleActivities(piglin, brain);
		addAdmireItemActivities(piglin, brain);
		addFightActivities(piglin, brain);
		addCelebrateActivities(piglin, brain);
		addAvoidActivities(piglin, brain);
		addRideActivities(piglin, brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
		setHuntedRecently(piglin.world, brain);
		return brain;
	}

	private static void setHuntedRecently(World world, Brain<PiglinEntity> brain) {
		int i = HUNT_MEMORY_DURATION.choose(world.random);
		brain.remember(MemoryModuleType.HUNTED_RECENTLY, true, world.getTime(), (long)i);
	}

	private static void addCoreActivities(PiglinEntity piglin, Brain<PiglinEntity> brain) {
		brain.setTaskList(
			Activity.CORE,
			0,
			ImmutableList.of(
				new LookAroundTask(45, 90),
				new WanderAroundTask(200),
				new OpenDoorsTask(),
				new RemoveOffHandItemTask<>(),
				new AdmireItemTask(120),
				new DefeatTargetTask(300),
				new ForgetAngryAtTargetTask()
			)
		);
	}

	private static void addIdleActivities(PiglinEntity piglin, Brain<PiglinEntity> brain) {
		float f = piglin.getWalkSpeed();
		brain.setTaskList(
			Activity.IDLE,
			10,
			ImmutableList.of(
				new FollowMobTask(PiglinBrain::isGoldHoldingPlayer, 14.0F),
				new UpdateAttackTargetTask<>(PiglinEntity::isAdult, PiglinBrain::getPreferredTarget),
				new HuntHoglinTask(),
				(UpdateAttackTargetTask<? super PiglinEntity>)makeGoToZombifiedPiglinTask(f),
				makeGoToSoulFireTask(f),
				makeRememberRideableHoglinTask(),
				makeRandomFollowTask(),
				makeRandomWanderTask(f),
				new FindInteractionTargetTask(EntityType.PLAYER, 4)
			)
		);
	}

	private static void addFightActivities(PiglinEntity piglin, Brain<PiglinEntity> brain) {
		float f = piglin.getWalkSpeed();
		brain.setTaskList(
			Activity.FIGHT,
			10,
			ImmutableList.of(
				new ForgetAttackTargetTask<>(livingEntity -> !isPreferredAttackTarget(piglin, livingEntity)),
				new ConditionalTask(PiglinBrain::isHoldingCrossbow, new AttackTask<>(5, 0.75F)),
				new RangedApproachTask(f * 1.2F),
				new MeleeAttackTask(1.5, 20),
				new CrossbowAttackTask(),
				new HuntFinishTask()
			),
			MemoryModuleType.ATTACK_TARGET
		);
	}

	private static void addCelebrateActivities(PiglinEntity piglin, Brain<PiglinEntity> brain) {
		float f = piglin.getWalkSpeed();
		brain.setTaskList(
			Activity.CELEBRATE,
			10,
			ImmutableList.of(
				makeGoToZombifiedPiglinTask(f),
				makeGoToSoulFireTask(f),
				new FollowMobTask(PiglinBrain::isGoldHoldingPlayer, 14.0F),
				new UpdateAttackTargetTask(PiglinEntity::isAdult, PiglinBrain::getPreferredTarget),
				new GoToCelebrateTask(2),
				new RandomTask(
					ImmutableList.of(Pair.of(new FollowMobTask(EntityType.PIGLIN, 8.0F), 1), Pair.of(new StrollTask(f, 2, 1), 1), Pair.of(new WaitTask(10, 20), 1))
				)
			),
			MemoryModuleType.CELEBRATE_LOCATION
		);
	}

	private static void addAdmireItemActivities(PiglinEntity piglin, Brain<PiglinEntity> brain) {
		float f = piglin.getWalkSpeed();
		brain.setTaskList(
			Activity.ADMIRE_ITEM,
			10,
			ImmutableList.of(
				new WalkToNearestVisibleWantedItemTask<>(PiglinBrain::method_24850, 9, true), new WanderWithOffHandItemTask(f * 0.5F), new WantNewItemTask(9)
			),
			MemoryModuleType.ADMIRING_ITEM
		);
	}

	private static void addAvoidActivities(PiglinEntity piglin, Brain<PiglinEntity> brain) {
		float f = piglin.getWalkSpeed() * 1.3F;
		brain.setTaskList(
			Activity.AVOID,
			10,
			ImmutableList.of(
				GoToRememberedPositionTask.toEntity(MemoryModuleType.AVOID_TARGET, f, 6, false),
				makeRandomFollowTask(),
				makeRandomWanderTask(piglin.getWalkSpeed()),
				new ForgetTask(PiglinBrain::shouldRunAwayFromHoglins, MemoryModuleType.AVOID_TARGET)
			),
			MemoryModuleType.AVOID_TARGET
		);
	}

	private static void addRideActivities(PiglinEntity piglin, Brain<PiglinEntity> brain) {
		brain.setTaskList(
			Activity.RIDE,
			10,
			ImmutableList.of(
				new StartRidingTask<>(),
				new FollowMobTask(PiglinBrain::isGoldHoldingPlayer, 8.0F),
				new ConditionalTask(PiglinEntity::isRiding, makeRandomFollowTask()),
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

	private static RandomTask<PiglinEntity> makeRandomWanderTask(float speed) {
		return new RandomTask<>(
			ImmutableList.of(
				Pair.of(new StrollTask(speed), 2),
				Pair.of(FindEntityTask.create(EntityType.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, speed, 2), 2),
				Pair.of(new ConditionalTask<>(PiglinBrain::canWander, new GoTowardsLookTarget(speed, 3)), 2),
				Pair.of(new WaitTask(30, 60), 1)
			)
		);
	}

	private static GoToRememberedPositionTask<BlockPos> makeGoToSoulFireTask(float speed) {
		return GoToRememberedPositionTask.toBlock(MemoryModuleType.NEAREST_VISIBLE_SOUL_FIRE_ITEM, speed * 1.5F, 8, false);
	}

	private static GoToRememberedPositionTask<?> makeGoToZombifiedPiglinTask(float speed) {
		return GoToRememberedPositionTask.toEntity(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED_PIGLIN, speed * 1.5F, 10, false);
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
		if (!brain.hasMemoryModule(MemoryModuleType.RIDE_TARGET)) {
			piglin.stopRiding();
		}

		if (piglin.isRiding() && hasPlayerHoldingWantedItemNearby(piglin)) {
			piglin.stopRiding();
			piglin.getBrain().forget(MemoryModuleType.RIDE_TARGET);
		}
	}

	protected static void loot(PiglinEntity piglin, ItemEntity drop) {
		stopWalking(piglin);
		piglin.sendPickup(drop, 1);
		ItemStack itemStack = method_24848(drop);
		Item item = itemStack.getItem();
		if (isGoldenItem(item)) {
			if (!piglin.getOffHandStack().isEmpty()) {
				piglin.dropStack(piglin.getStackInHand(Hand.OFF_HAND));
			}

			piglin.method_24845(itemStack);
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

	private static ItemStack method_24848(ItemEntity itemEntity) {
		ItemStack itemStack = itemEntity.getStack();
		ItemStack itemStack2 = itemStack.split(1);
		if (itemStack.isEmpty()) {
			itemEntity.remove();
		} else {
			itemEntity.setStack(itemStack);
		}

		return itemStack2;
	}

	public static void consumeOffHandItem(PiglinEntity piglin, boolean bl) {
		ItemStack itemStack = piglin.getStackInHand(Hand.OFF_HAND);
		piglin.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
		if (piglin.isAdult()) {
			if (bl && acceptsForBarter(itemStack.getItem())) {
				doBarter(piglin, getBarteredItem(piglin));
			} else {
				boolean bl2 = piglin.tryEquip(itemStack);
				if (!bl2) {
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
					doBarter(piglin, itemStack2);
				}

				piglin.method_24844(itemStack);
			}
		}
	}

	private static void method_24849(PiglinEntity piglinEntity, ItemStack itemStack) {
		ItemStack itemStack2 = piglinEntity.addItem(itemStack);
		dropBarteredItem(piglinEntity, itemStack2);
	}

	private static void doBarter(PiglinEntity piglin, ItemStack itemStack) {
		Optional<PlayerEntity> optional = piglin.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
		if (optional.isPresent()) {
			dropBarteredItem(piglin, (PlayerEntity)optional.get(), itemStack);
		} else {
			dropBarteredItem(piglin, itemStack);
		}
	}

	private static void dropBarteredItem(PiglinEntity piglinEntity, ItemStack itemStack) {
		drop(piglinEntity, itemStack, findGround(piglinEntity));
	}

	private static void dropBarteredItem(PiglinEntity piglinEntity, PlayerEntity player, ItemStack itemStack) {
		drop(piglinEntity, itemStack, player.getPos());
	}

	private static void drop(PiglinEntity piglinEntity, ItemStack itemStack, Vec3d pos) {
		if (!itemStack.isEmpty()) {
			piglinEntity.swingHand(Hand.OFF_HAND);
			LookTargetUtil.give(piglinEntity, itemStack, pos.add(0.0, 1.0, 0.0));
		}
	}

	private static ItemStack getBarteredItem(PiglinEntity piglin) {
		LootTable lootTable = piglin.world.getServer().getLootManager().getTable(LootTables.PIGLIN_BARTERING_GAMEPLAY);
		List<ItemStack> list = lootTable.getDrops(
			new LootContext.Builder((ServerWorld)piglin.world)
				.put(LootContextParameters.THIS_ENTITY, piglin)
				.setRandom(piglin.world.random)
				.build(LootContextTypes.BARTER)
		);
		return list.isEmpty() ? ItemStack.EMPTY : (ItemStack)list.get(0);
	}

	protected static boolean canGather(PiglinEntity piglin, ItemStack stack) {
		Item item = stack.getItem();
		if (item == Items.GOLD_NUGGET) {
			return true;
		} else if (hasBeenHitByPlayer(piglin) && piglin.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET)) {
			return false;
		} else if (isFood(item)) {
			return !hasAteRecently(piglin);
		} else {
			return isGoldenItem(item) ? method_24850(piglin) : piglin.method_24846(stack);
		}
	}

	public static boolean isGoldenItem(Item item) {
		return GOLDEN_ITEMS.contains(item)
			|| item instanceof ToolItem && ((ToolItem)item).getMaterial() == ToolMaterials.GOLD
			|| item instanceof ArmorItem && ((ArmorItem)item).getMaterial() == ArmorMaterials.GOLD;
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

	private static Optional<? extends LivingEntity> getPreferredTarget(PiglinEntity piglin) {
		Brain<PiglinEntity> brain = piglin.getBrain();
		Optional<LivingEntity> optional = LookTargetUtil.getEntity(piglin, MemoryModuleType.ANGRY_AT);
		if (optional.isPresent() && shouldAttack((LivingEntity)optional.get())) {
			return optional;
		} else {
			Optional<WitherSkeletonEntity> optional2 = brain.getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_WITHER_SKELETON);
			if (optional2.isPresent()) {
				return optional2;
			} else {
				Optional<PlayerEntity> optional3 = brain.getOptionalMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);
				return optional3.isPresent() && shouldAttack((LivingEntity)optional3.get()) ? optional3 : Optional.empty();
			}
		}
	}

	public static void onGoldBlockBroken(PlayerEntity player) {
		if (shouldAttack(player)) {
			List<PiglinEntity> list = player.world.getNonSpectatingEntities(PiglinEntity.class, player.getBoundingBox().expand(16.0));
			list.stream()
				.filter(PiglinBrain::hasIdleActivity)
				.filter(piglinEntity -> LookTargetUtil.isVisibleInMemory(piglinEntity, player))
				.forEach(piglinEntity -> angerAt(piglinEntity, player));
		}
	}

	public static boolean playerInteract(PiglinEntity piglin, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		Item item = itemStack.getItem();
		if (!isAdmiringItem(piglin) && piglin.isAdult() && acceptsForBarter(item) && !hasBeenHitByPlayer(piglin)) {
			itemStack.decrement(1);
			piglin.method_24845(new ItemStack(item, 1));
			setAdmiringItem(piglin);
			return true;
		} else {
			return false;
		}
	}

	protected static void onAttacked(PiglinEntity piglin, LivingEntity attacker) {
		if (!(attacker instanceof PiglinEntity)) {
			if (!piglin.getOffHandStack().isEmpty()) {
				consumeOffHandItem(piglin, false);
			}

			Brain<PiglinEntity> brain = piglin.getBrain();
			brain.forget(MemoryModuleType.CELEBRATE_LOCATION);
			brain.forget(MemoryModuleType.ADMIRING_ITEM);
			if (attacker instanceof PlayerEntity) {
				brain.remember(MemoryModuleType.WAS_HIT_BY_PLAYER, (PlayerEntity)attacker, piglin.world.getTime(), 400L);
			}

			if (piglin.isBaby()) {
				brain.remember(MemoryModuleType.AVOID_TARGET, attacker, piglin.world.getTime(), 100L);
			} else if (attacker.getType() == EntityType.HOGLIN && hasOutnumberedHoglins(piglin)) {
				runAwayFrom(piglin, attacker);
				groupRunAwayFrom(piglin, attacker);
			} else {
				tryRevenge(piglin, attacker);
			}
		}
	}

	private static void tryRevenge(PiglinEntity piglin, LivingEntity target) {
		if (!piglin.getBrain().hasActivity(Activity.AVOID) || target.getType() != EntityType.HOGLIN) {
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
			} else if (activity == Activity.AVOID || piglin.canConvert()) {
				piglin.playRetreatSound();
			} else if (activity == Activity.ADMIRE_ITEM) {
				piglin.playAdmireItemSound();
			} else if (activity == Activity.CELEBRATE) {
				piglin.playCelebrateSound();
			} else if (hasPlayerHoldingWantedItemNearby((LivingEntity)piglin)) {
				piglin.playJealousSound();
			} else if (hasZombifiedPiglinNearby(piglin) || hasSoulFireNearby(piglin)) {
				piglin.playRetreatSound();
			}
		});
	}

	protected static void playSoundAtChance(PiglinEntity piglin) {
		if ((double)piglin.world.random.nextFloat() < 0.0125) {
			playSound(piglin);
		}
	}

	public static boolean haveHuntedHoglinsRecently(PiglinEntity piglin) {
		return piglin.getBrain().hasMemoryModule(MemoryModuleType.HUNTED_RECENTLY)
			|| getNearbyPiglins(piglin).stream().anyMatch(piglinEntity -> piglinEntity.getBrain().hasMemoryModule(MemoryModuleType.HUNTED_RECENTLY));
	}

	private static List<PiglinEntity> getNearbyPiglins(PiglinEntity piglin) {
		return (List<PiglinEntity>)(piglin.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS)
			? (List)piglin.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS).get()
			: Lists.<PiglinEntity>newArrayList());
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
		getNearbyPiglins(piglin).forEach(piglinEntity -> angerAtIfCloser(piglinEntity, target));
	}

	public static void rememberGroupHunting(PiglinEntity piglin) {
		getNearbyPiglins(piglin).forEach(piglinEntity -> rememberHunting(piglinEntity));
	}

	public static void angerAt(PiglinEntity piglin, LivingEntity target) {
		piglin.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
		piglin.getBrain().remember(MemoryModuleType.ANGRY_AT, new DynamicSerializableUuid(target.getUuid()), piglin.world.getTime(), 600L);
		if (target.getType() == EntityType.HOGLIN) {
			rememberHunting(piglin);
		}
	}

	private static void angerAtIfCloser(PiglinEntity piglin, LivingEntity target) {
		Optional<LivingEntity> optional = getAngryAt(piglin);
		LivingEntity livingEntity = LookTargetUtil.getCloserEntity(piglin, optional, target);
		angerAt(piglin, livingEntity);
	}

	private static Optional<LivingEntity> getAngryAt(PiglinEntity piglin) {
		return LookTargetUtil.getEntity(piglin, MemoryModuleType.ANGRY_AT);
	}

	private static void groupRunAwayFrom(PiglinEntity piglin, LivingEntity target) {
		getNearbyPiglins(piglin).forEach(piglinEntity -> runAwayFromClosestTarget(piglinEntity, target));
	}

	private static void runAwayFromClosestTarget(PiglinEntity piglin, LivingEntity target) {
		Brain<PiglinEntity> brain = piglin.getBrain();
		LivingEntity livingEntity = LookTargetUtil.getCloserEntity(piglin, brain.getOptionalMemory(MemoryModuleType.AVOID_TARGET), target);
		livingEntity = LookTargetUtil.getCloserEntity(piglin, brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET), livingEntity);
		runAwayFrom(piglin, livingEntity);
	}

	private static boolean shouldRunAwayFromHoglins(PiglinEntity piglin) {
		return piglin.isAdult() && hasNoAdvantageAgainstHoglins(piglin);
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
		piglin.getBrain().remember(MemoryModuleType.AVOID_TARGET, target, piglin.world.getTime(), (long)AVOID_MEMORY_DURATION.choose(piglin.world.random));
		rememberHunting(piglin);
	}

	public static void rememberHunting(PiglinEntity piglin) {
		piglin.getBrain().remember(MemoryModuleType.HUNTED_RECENTLY, true, piglin.world.getTime(), (long)HUNT_MEMORY_DURATION.choose(piglin.world.random));
	}

	private static boolean hasPlayerHoldingWantedItemNearby(PiglinEntity piglin) {
		return piglin.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM);
	}

	private static void setEatenRecently(PiglinEntity piglin) {
		piglin.getBrain().remember(MemoryModuleType.ATE_RECENTLY, true, piglin.world.getTime(), 200L);
	}

	private static Vec3d findGround(PiglinEntity piglin) {
		Vec3d vec3d = TargetFinder.findGroundTarget(piglin, 4, 2);
		return vec3d == null ? piglin.getPos() : vec3d;
	}

	private static boolean hasAteRecently(PiglinEntity piglin) {
		return piglin.getBrain().hasMemoryModule(MemoryModuleType.ATE_RECENTLY);
	}

	static boolean hasIdleActivity(PiglinEntity piglin) {
		return piglin.getBrain().hasActivity(Activity.IDLE);
	}

	private static boolean isHoldingCrossbow(LivingEntity piglin) {
		return piglin.isHolding(Items.CROSSBOW);
	}

	private static void setAdmiringItem(LivingEntity entity) {
		entity.getBrain().remember(MemoryModuleType.ADMIRING_ITEM, true, entity.world.getTime(), 120L);
	}

	private static boolean isAdmiringItem(PiglinEntity entity) {
		return entity.getBrain().hasMemoryModule(MemoryModuleType.ADMIRING_ITEM);
	}

	private static boolean acceptsForBarter(Item item) {
		return item == Items.GOLD_INGOT;
	}

	private static boolean isFood(Item item) {
		return FOOD.contains(item);
	}

	private static boolean shouldAttack(LivingEntity target) {
		return EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(target);
	}

	private static boolean hasSoulFireNearby(PiglinEntity piglin) {
		return piglin.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_VISIBLE_SOUL_FIRE_ITEM);
	}

	private static boolean hasZombifiedPiglinNearby(PiglinEntity piglin) {
		return piglin.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED_PIGLIN);
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
		return piglin.getBrain().hasMemoryModule(MemoryModuleType.WAS_HIT_BY_PLAYER);
	}

	private static boolean hasBeenHurt(LivingEntity piglin) {
		return piglin.getBrain().hasMemoryModule(MemoryModuleType.HURT_BY);
	}

	private static boolean method_24850(PiglinEntity piglinEntity) {
		return piglinEntity.getOffHandStack().isEmpty() || !isGoldenItem(piglinEntity.getOffHandStack().getItem());
	}
}
