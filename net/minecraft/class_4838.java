/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.class_4801;
import net.minecraft.class_4802;
import net.minecraft.class_4805;
import net.minecraft.class_4807;
import net.minecraft.class_4809;
import net.minecraft.class_4810;
import net.minecraft.class_4812;
import net.minecraft.class_4813;
import net.minecraft.class_4814;
import net.minecraft.class_4815;
import net.minecraft.class_4816;
import net.minecraft.class_4817;
import net.minecraft.class_4818;
import net.minecraft.class_4819;
import net.minecraft.class_4820;
import net.minecraft.class_4821;
import net.minecraft.class_4822;
import net.minecraft.class_4823;
import net.minecraft.class_4824;
import net.minecraft.class_4825;
import net.minecraft.class_4826;
import net.minecraft.class_4827;
import net.minecraft.class_4828;
import net.minecraft.class_4829;
import net.minecraft.class_4830;
import net.minecraft.class_4836;
import net.minecraft.class_4844;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.FindEntityTask;
import net.minecraft.entity.ai.brain.task.FindInteractionTargetTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.GoToNearbyEntityTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTarget;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.OpenDoorsTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
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
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class class_4838 {
    private static final class_4801 field_22388 = class_4802.method_24505(30, 120);
    private static final class_4801 field_22389 = class_4802.method_24505(5, 20);
    private static final class_4801 field_22390 = class_4802.method_24505(10, 30);
    private static final class_4801 field_22391 = class_4802.method_24505(5, 20);
    private static final Set field_22392 = ImmutableSet.of(Items.PORKCHOP, Items.COOKED_PORKCHOP);
    private static final Set<Pair<Integer, Item>> field_22393 = ImmutableSet.of(Pair.of(1, Items.SHROOMLIGHT), Pair.of(1, Items.OBSIDIAN), Pair.of(2, Items.QUARTZ), Pair.of(2, Items.GLOWSTONE_DUST), Pair.of(2, Items.MAGMA_CREAM), Pair.of(2, Items.ENDER_PEARL), new Pair[]{Pair.of(2, Items.WARPED_FUNGI), Pair.of(5, Items.GRAVEL), Pair.of(5, Items.SOUL_SAND), Pair.of(5, Items.FIRE_CHARGE), Pair.of(10, Items.COOKED_PORKCHOP), Pair.of(10, Items.LEATHER), Pair.of(10, Items.NETHER_BRICK), Pair.of(10, Items.RED_MUSHROOM), Pair.of(10, Items.BROWN_MUSHROOM), Pair.of(10, Items.FLINT), Pair.of(10, Items.ROTTEN_FLESH), Pair.of(10, Items.CRIMSON_FUNGI)});
    private static final Set<Item> field_22394 = ImmutableSet.of(Items.PORKCHOP, Items.COOKED_PORKCHOP, Items.GOLD_NUGGET);
    private static final Set<Item> field_22395 = ImmutableSet.of(Items.GOLD_INGOT, Items.GOLDEN_APPLE, Items.GOLDEN_HORSE_ARMOR, Items.GOLDEN_CARROT, Items.GOLD_BLOCK, Items.GOLD_ORE, new Item[]{Items.ENCHANTED_GOLDEN_APPLE, Items.GOLDEN_HORSE_ARMOR});

    protected static Brain<?> method_24732(class_4836 arg, Dynamic<?> dynamic) {
        Brain<class_4836> brain = new Brain<class_4836>(class_4836.field_22381, class_4836.field_22376, dynamic);
        class_4838.method_24725(arg, brain);
        class_4838.method_24743(arg, brain);
        class_4838.method_24760(arg, brain);
        class_4838.method_24751(arg, brain);
        class_4838.method_24756(arg, brain);
        class_4838.method_24764(arg, brain);
        class_4838.method_24768(arg, brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.method_24536();
        class_4838.method_24736(arg.world, brain);
        return brain;
    }

    private static void method_24736(World world, Brain<class_4836> brain) {
        int i = field_22388.method_24503(world.random);
        brain.method_24525(MemoryModuleType.HUNTED_RECENTLY, true, world.getTime(), i);
    }

    private static void method_24725(class_4836 arg, Brain<class_4836> brain) {
        brain.setTaskList(Activity.CORE, 0, ImmutableList.of(new LookAroundTask(45, 90), new WanderAroundTask(200), new OpenDoorsTask(), new class_4830(), new class_4823(120), new class_4825(300), new class_4829()));
    }

    private static void method_24743(class_4836 arg, Brain<class_4836> brain) {
        float f = arg.method_24708();
        brain.setTaskList(Activity.IDLE, 10, ImmutableList.of(new FollowMobTask(class_4838::method_24739, 14.0f), new class_4824<class_4836>(class_4836::method_24712, class_4838::method_24777), new class_4826(), class_4838.method_24747(f), class_4838.method_24738(f), class_4838.method_24737(), class_4838.method_24717(), class_4838.method_24718(f), new FindInteractionTargetTask(EntityType.PLAYER, 4)));
    }

    private static void method_24751(class_4836 arg, Brain<class_4836> brain) {
        float f = arg.method_24708();
        brain.method_24527(Activity.FIGHT, 10, ImmutableList.of(new class_4828(livingEntity -> !class_4838.method_24755(arg, livingEntity)), new class_4820<class_4836>(class_4838::method_24748, new class_4807(5, 0.75f)), new class_4822(f * 1.2f), new class_4816(1.5, 20), new class_4810(), new class_4819()), MemoryModuleType.ATTACK_TARGET);
    }

    private static void method_24756(class_4836 arg, Brain<class_4836> brain) {
        float f = arg.method_24708();
        brain.method_24527(Activity.CELEBRATE, 10, ImmutableList.of(class_4838.method_24747(f), class_4838.method_24738(f), new FollowMobTask(class_4838::method_24739, 14.0f), new class_4824<class_4836>(class_4836::method_24712, class_4838::method_24777), new class_4814(2), new RandomTask(ImmutableList.of(Pair.of(new FollowMobTask(EntityType.PIGLIN, 8.0f), 1), Pair.of(new class_4818(f, 2, 1), 1), Pair.of(new WaitTask(10, 20), 1)))), MemoryModuleType.CELEBRATE_LOCATION);
    }

    private static void method_24760(class_4836 arg, Brain<class_4836> brain) {
        float f = arg.method_24708();
        brain.method_24527(Activity.ADMIRE_ITEM, 10, ImmutableList.of(new class_4815<class_4836>(class_4836::method_24706, 9, true), new class_4805(f * 0.5f), new class_4827(9)), MemoryModuleType.ADMIRING_ITEM);
    }

    private static void method_24764(class_4836 arg, Brain<class_4836> brain) {
        float f = arg.method_24708() * 1.3f;
        brain.method_24527(Activity.AVOID, 10, ImmutableList.of(GoToNearbyEntityTask.method_24603(MemoryModuleType.AVOID_TARGET, f, 6, false), class_4838.method_24717(), class_4838.method_24718(arg.method_24708()), new class_4813<class_4836>(class_4838::method_24783, MemoryModuleType.AVOID_TARGET)), MemoryModuleType.AVOID_TARGET);
    }

    private static void method_24768(class_4836 arg, Brain<class_4836> brain) {
        brain.method_24527(Activity.RIDE, 10, ImmutableList.of(new class_4817(), new FollowMobTask(class_4838::method_24739, 8.0f), new class_4820<class_4836>(class_4836::method_24707, class_4838.method_24717()), new class_4812(8, class_4838::method_24723)), MemoryModuleType.RIDE_TARGET);
    }

    private static RandomTask<class_4836> method_24717() {
        return new RandomTask(ImmutableList.of(Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0f), 1), Pair.of(new FollowMobTask(EntityType.PIGLIN, 8.0f), 1), Pair.of(new FollowMobTask(8.0f), 1), Pair.of(new WaitTask(30, 60), 1)));
    }

    private static RandomTask<class_4836> method_24718(float f) {
        return new RandomTask(ImmutableList.of(Pair.of(new class_4818(f), 2), Pair.of(FindEntityTask.create(EntityType.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), 2), Pair.of(new class_4820<LivingEntity>(class_4838::method_24765, new GoTowardsLookTarget(f, 3)), 2), Pair.of(new WaitTask(30, 60), 1)));
    }

    private static GoToNearbyEntityTask<BlockPos> method_24738(float f) {
        return GoToNearbyEntityTask.method_24601(MemoryModuleType.NEAREST_VISIBLE_SOUL_FIRE_ITEM, f * 1.5f, 8, false);
    }

    private static GoToNearbyEntityTask<?> method_24747(float f) {
        return GoToNearbyEntityTask.method_24603(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED_PIGLIN, f * 1.5f, 10, false);
    }

    protected static void method_24722(class_4836 arg) {
        Brain<class_4836> brain = arg.getBrain();
        Activity activity = brain.method_24538().orElse(null);
        brain.method_24531(ImmutableList.of(Activity.ADMIRE_ITEM, Activity.FIGHT, Activity.AVOID, Activity.CELEBRATE, Activity.RIDE, Activity.IDLE));
        Activity activity2 = brain.method_24538().orElse(null);
        if (activity != activity2) {
            class_4838.method_24778(arg);
        }
        arg.setAttacking(brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
        if (!brain.hasMemoryModule(MemoryModuleType.RIDE_TARGET)) {
            arg.stopRiding();
        }
        if (arg.method_24707() && class_4838.method_24786(arg)) {
            arg.stopRiding();
            arg.getBrain().forget(MemoryModuleType.RIDE_TARGET);
        }
    }

    protected static void method_24726(class_4836 arg, ItemEntity itemEntity) {
        arg.sendPickup(itemEntity, 1);
        ItemStack itemStack = itemEntity.getStack();
        ItemStack itemStack2 = itemStack.split(1);
        if (class_4838.method_24735(itemStack2.getItem())) {
            arg.setStackInHand(Hand.OFF_HAND, itemStack2);
            class_4838.method_24753(arg);
            if (arg.method_24712() && class_4838.method_24746(itemStack2.getItem())) {
                class_4838.method_24772(arg);
            }
        } else {
            arg.method_24523(itemStack2);
        }
        if (class_4838.method_24752(itemStack2.getItem()) && !class_4838.method_24789(arg)) {
            class_4838.method_24787(arg);
        }
        if (itemStack.isEmpty()) {
            itemEntity.remove();
        } else {
            itemEntity.setStack(itemStack);
        }
        class_4838.method_24781(arg);
    }

    public static void method_24741(class_4836 arg) {
        ItemStack itemStack = arg.getStackInHand(Hand.OFF_HAND);
        if (itemStack.isEmpty()) {
            return;
        }
        arg.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
        if (class_4838.method_24746(itemStack.getItem())) {
            return;
        }
        if (arg.method_24523(itemStack)) {
            return;
        }
        ItemStack itemStack2 = arg.method_24711(itemStack);
        class_4838.method_24731(arg, itemStack2, class_4838.method_24788(arg));
    }

    private static void method_24772(class_4836 arg) {
        Optional<PlayerEntity> optional = arg.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
        if (optional.isPresent()) {
            class_4838.method_24727(arg, optional.get());
        } else {
            class_4838.method_24774(arg);
        }
    }

    private static void method_24774(class_4836 arg) {
        class_4838.method_24731(arg, class_4838.method_24776(arg), class_4838.method_24788(arg));
    }

    private static void method_24727(class_4836 arg, PlayerEntity playerEntity) {
        class_4838.method_24731(arg, class_4838.method_24776(arg), playerEntity.getPos());
    }

    private static void method_24731(class_4836 arg, ItemStack itemStack, Vec3d vec3d) {
        arg.swingHand(Hand.OFF_HAND);
        if (!itemStack.isEmpty()) {
            LookTargetUtil.give(arg, itemStack, vec3d.add(0.0, 1.0, 0.0));
        }
    }

    private static ItemStack method_24776(class_4836 arg) {
        LootTable lootTable = arg.world.getServer().getLootManager().getTable(LootTables.PIGLIN_BARTERING_GAMEPLAY);
        List<ItemStack> list = lootTable.getDrops(new LootContext.Builder((ServerWorld)arg.world).put(LootContextParameters.THIS_ENTITY, arg).setRandom(arg.world.random).build(LootContextTypes.BARTER));
        return list.isEmpty() ? ItemStack.EMPTY : list.get(0);
    }

    protected static boolean method_24730(class_4836 arg, ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (!arg.method_24706() || class_4838.method_24714(arg) || class_4838.method_24752(item) && class_4838.method_24789(arg)) {
            return false;
        }
        return field_22394.contains(item) || class_4838.method_24735(item) || class_4838.method_24744(arg, itemStack);
    }

    private static boolean method_24744(class_4836 arg, ItemStack itemStack) {
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
        ItemStack itemStack2 = arg.getEquippedStack(equipmentSlot);
        return arg.isBetterItemFor(itemStack, itemStack2, equipmentSlot);
    }

    public static boolean method_24735(Item item) {
        return field_22395.contains(item) || item instanceof ToolItem && ((ToolItem)item).getMaterial() == ToolMaterials.GOLD || item instanceof ArmorItem && ((ArmorItem)item).getMaterial() == ArmorMaterials.GOLD;
    }

    private static boolean method_24723(class_4836 arg, Entity entity) {
        if (entity instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity)entity;
            return !mobEntity.isBaby() || !mobEntity.isAlive() || class_4838.method_24769(arg) || class_4838.method_24769(mobEntity) || mobEntity instanceof class_4836 && mobEntity.getVehicle() == null;
        }
        return false;
    }

    private static boolean method_24755(class_4836 arg, LivingEntity livingEntity) {
        return class_4838.method_24777(arg).filter(livingEntity2 -> livingEntity2 == livingEntity).isPresent();
    }

    private static Optional<? extends LivingEntity> method_24777(class_4836 arg) {
        Brain<class_4836> brain = arg.getBrain();
        Optional<LivingEntity> optional = LookTargetUtil.method_24560(arg, MemoryModuleType.ANGRY_AT);
        if (optional.isPresent() && class_4838.method_24757(optional.get())) {
            return optional;
        }
        Optional<WitherSkeletonEntity> optional2 = brain.getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_WITHER_SKELETON);
        if (optional2.isPresent() && class_4838.method_24779(arg)) {
            return optional2;
        }
        Optional<PlayerEntity> optional3 = brain.getOptionalMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);
        if (optional3.isPresent() && class_4838.method_24757(optional3.get())) {
            return optional3;
        }
        return Optional.empty();
    }

    public static void method_24733(PlayerEntity playerEntity) {
        if (!class_4838.method_24757(playerEntity)) {
            return;
        }
        List<class_4836> list = playerEntity.world.getNonSpectatingEntities(class_4836.class, playerEntity.getBoundingBox().expand(16.0));
        list.stream().filter(class_4838::method_24766).filter(arg -> LookTargetUtil.method_24565(arg, playerEntity)).forEach(arg -> class_4838.method_24750(arg, playerEntity));
    }

    public static boolean method_24728(class_4836 arg, PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        Item item = itemStack.getItem();
        if (!class_4838.method_24790(arg) && arg.method_24712() && class_4838.method_24746(item) && !class_4838.method_24714(arg)) {
            itemStack.decrement(1);
            arg.setStackInHand(Hand.OFF_HAND, new ItemStack(item, 1));
            class_4838.method_24753(arg);
            class_4838.method_24727(arg, playerEntity);
            return true;
        }
        return false;
    }

    protected static void method_24724(class_4836 arg, LivingEntity livingEntity) {
        arg.method_24698();
        if (livingEntity instanceof class_4836) {
            return;
        }
        class_4838.method_24741(arg);
        Brain<class_4836> brain = arg.getBrain();
        brain.forget(MemoryModuleType.CELEBRATE_LOCATION);
        brain.forget(MemoryModuleType.ADMIRING_ITEM);
        if (livingEntity instanceof PlayerEntity) {
            brain.method_24525(MemoryModuleType.WAS_HIT_BY_PLAYER, (PlayerEntity)livingEntity, arg.world.getTime(), 400L);
        }
        if (arg.isBaby()) {
            brain.method_24525(MemoryModuleType.AVOID_TARGET, livingEntity, arg.world.getTime(), 100L);
            return;
        }
        if (livingEntity.getType() == EntityType.HOGLIN && class_4838.method_24785(arg)) {
            class_4838.method_24773(arg, livingEntity);
            class_4838.method_24767(arg, livingEntity);
            return;
        }
        class_4838.method_24759(arg, livingEntity);
    }

    private static void method_24759(class_4836 arg, LivingEntity livingEntity) {
        if (arg.getBrain().hasActivity(Activity.AVOID) && livingEntity.getType() == EntityType.HOGLIN) {
            return;
        }
        if (!class_4838.method_24757(livingEntity)) {
            return;
        }
        if (LookTargetUtil.method_24558(arg, livingEntity, 4.0)) {
            return;
        }
        class_4838.method_24750(arg, livingEntity);
        class_4838.method_24742(arg, livingEntity);
    }

    private static void method_24778(class_4836 arg) {
        arg.getBrain().method_24538().ifPresent(activity -> {
            if (activity == Activity.FIGHT) {
                arg.method_24710();
            } else if (activity == Activity.AVOID || arg.method_24704()) {
                arg.method_24699();
            } else if (activity == Activity.ADMIRE_ITEM) {
                arg.method_24709();
            } else if (activity == Activity.CELEBRATE) {
                arg.method_24697();
            } else if (class_4838.method_24761(arg)) {
                arg.method_24700();
            } else if (class_4838.method_24713(arg) || class_4838.method_24791(arg)) {
                arg.method_24699();
            }
        });
    }

    protected static void method_24749(class_4836 arg) {
        if ((double)arg.world.random.nextFloat() < 0.0125) {
            class_4838.method_24778(arg);
        }
    }

    private static boolean method_24779(class_4836 arg) {
        Brain<class_4836> brain = arg.getBrain();
        if (!brain.hasMemoryModule(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS)) {
            return false;
        }
        return brain.getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS).get().stream().anyMatch(class_4836::method_24712);
    }

    public static boolean method_24754(class_4836 arg2) {
        return arg2.getBrain().hasMemoryModule(MemoryModuleType.HUNTED_RECENTLY) || class_4838.method_24780(arg2).stream().anyMatch(arg -> arg.getBrain().hasMemoryModule(MemoryModuleType.HUNTED_RECENTLY));
    }

    private static List<class_4836> method_24780(class_4836 arg) {
        if (arg.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS)) {
            return arg.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS).get();
        }
        return Lists.newArrayList();
    }

    public static boolean method_24719(LivingEntity livingEntity) {
        Iterable<ItemStack> iterable = livingEntity.getArmorItems();
        for (ItemStack itemStack : iterable) {
            Item item = itemStack.getItem();
            if (!(item instanceof ArmorItem) || ((ArmorItem)item).getMaterial() != ArmorMaterials.GOLD) continue;
            return true;
        }
        return false;
    }

    private static void method_24781(class_4836 arg) {
        arg.getBrain().forget(MemoryModuleType.WALK_TARGET);
        arg.getNavigation().stop();
    }

    private static class_4821<class_4836> method_24737() {
        return new class_4821<class_4836>(new class_4809<class_4836, Entity>(class_4836::isBaby, MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.RIDE_TARGET, field_22390), field_22389);
    }

    public static void method_24742(class_4836 arg2, LivingEntity livingEntity) {
        class_4838.method_24780(arg2).forEach(arg -> class_4838.method_24763(arg, livingEntity));
    }

    public static void method_24758(class_4836 arg2) {
        class_4838.method_24780(arg2).forEach(arg -> class_4838.method_24762(arg));
    }

    public static void method_24750(class_4836 arg, LivingEntity livingEntity) {
        arg.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        arg.getBrain().method_24525(MemoryModuleType.ANGRY_AT, new class_4844(livingEntity.getUuid()), arg.world.getTime(), 600L);
        if (livingEntity.getType() == EntityType.HOGLIN) {
            class_4838.method_24762(arg);
        }
    }

    private static void method_24763(class_4836 arg, LivingEntity livingEntity) {
        Optional<LivingEntity> optional = class_4838.method_24782(arg);
        LivingEntity livingEntity2 = LookTargetUtil.method_24562(arg, optional, livingEntity);
        class_4838.method_24750(arg, livingEntity2);
    }

    private static Optional<LivingEntity> method_24782(class_4836 arg) {
        return LookTargetUtil.method_24560(arg, MemoryModuleType.ANGRY_AT);
    }

    private static void method_24767(class_4836 arg2, LivingEntity livingEntity) {
        class_4838.method_24780(arg2).forEach(arg -> class_4838.method_24771(arg, livingEntity));
    }

    private static void method_24771(class_4836 arg, LivingEntity livingEntity) {
        Brain<class_4836> brain = arg.getBrain();
        LivingEntity livingEntity2 = livingEntity;
        livingEntity2 = LookTargetUtil.method_24562(arg, brain.getOptionalMemory(MemoryModuleType.AVOID_TARGET), livingEntity2);
        livingEntity2 = LookTargetUtil.method_24562(arg, brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET), livingEntity2);
        class_4838.method_24773(arg, livingEntity2);
    }

    private static boolean method_24783(class_4836 arg) {
        return arg.method_24712() && class_4838.method_24784(arg);
    }

    private static boolean method_24784(class_4836 arg) {
        return !class_4838.method_24785(arg);
    }

    private static boolean method_24785(class_4836 arg) {
        int i = arg.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT).orElse(0) + 1;
        int j = arg.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT).orElse(0);
        return j > i;
    }

    private static void method_24773(class_4836 arg, LivingEntity livingEntity) {
        arg.getBrain().forget(MemoryModuleType.ANGRY_AT);
        arg.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
        arg.getBrain().method_24525(MemoryModuleType.AVOID_TARGET, livingEntity, arg.world.getTime(), field_22391.method_24503(arg.world.random));
        class_4838.method_24762(arg);
    }

    public static void method_24762(class_4836 arg) {
        arg.getBrain().method_24525(MemoryModuleType.HUNTED_RECENTLY, true, arg.world.getTime(), field_22388.method_24503(arg.world.random));
    }

    private static boolean method_24786(class_4836 arg) {
        return arg.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM);
    }

    private static void method_24787(class_4836 arg) {
        arg.getBrain().method_24525(MemoryModuleType.ATE_RECENTLY, true, arg.world.getTime(), 200L);
    }

    private static Vec3d method_24788(class_4836 arg) {
        Vec3d vec3d = TargetFinder.findGroundTarget(arg, 4, 2);
        return vec3d == null ? arg.getPos() : vec3d;
    }

    private static boolean method_24789(class_4836 arg) {
        return arg.getBrain().hasMemoryModule(MemoryModuleType.ATE_RECENTLY);
    }

    static boolean method_24766(class_4836 arg) {
        return arg.getBrain().hasActivity(Activity.IDLE);
    }

    private static boolean method_24748(LivingEntity livingEntity) {
        return livingEntity.method_24518(Items.CROSSBOW);
    }

    private static void method_24753(LivingEntity livingEntity) {
        livingEntity.getBrain().method_24525(MemoryModuleType.ADMIRING_ITEM, true, livingEntity.world.getTime(), 120L);
    }

    private static boolean method_24790(class_4836 arg) {
        return arg.getBrain().hasMemoryModule(MemoryModuleType.ADMIRING_ITEM);
    }

    private static boolean method_24746(Item item) {
        return item == Items.GOLD_INGOT;
    }

    private static boolean method_24752(Item item) {
        return field_22392.contains(item);
    }

    private static boolean method_24757(LivingEntity livingEntity) {
        return EntityPredicates.field_22280.test(livingEntity);
    }

    private static boolean method_24791(class_4836 arg) {
        return arg.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_VISIBLE_SOUL_FIRE_ITEM);
    }

    private static boolean method_24713(class_4836 arg) {
        return arg.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED_PIGLIN);
    }

    private static boolean method_24761(LivingEntity livingEntity) {
        return livingEntity.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM);
    }

    private static boolean method_24765(LivingEntity livingEntity) {
        return !class_4838.method_24761(livingEntity);
    }

    public static boolean method_24739(LivingEntity livingEntity) {
        return livingEntity.getType() == EntityType.PLAYER && livingEntity.method_24520(class_4838::method_24735);
    }

    private static boolean method_24714(class_4836 arg) {
        return arg.getBrain().hasMemoryModule(MemoryModuleType.WAS_HIT_BY_PLAYER);
    }

    private static boolean method_24769(LivingEntity livingEntity) {
        return livingEntity.getBrain().hasMemoryModule(MemoryModuleType.HURT_BY);
    }

    protected static boolean method_24770(class_4836 arg) {
        return arg.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_PLAYERS);
    }
}

