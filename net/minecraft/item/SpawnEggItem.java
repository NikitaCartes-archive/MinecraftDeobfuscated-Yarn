/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SpawnEggItem
extends Item {
    private static final Map<EntityType<?>, SpawnEggItem> SPAWN_EGGS = Maps.newIdentityHashMap();
    private final int primaryColor;
    private final int secondaryColor;
    private final EntityType<?> type;

    public SpawnEggItem(EntityType<?> type, int primaryColor, int secondaryColor, Item.Settings settings) {
        super(settings);
        this.type = type;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        SPAWN_EGGS.put(type, this);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockEntity blockEntity;
        World world = context.getWorld();
        if (!(world instanceof ServerWorld)) {
            return ActionResult.SUCCESS;
        }
        ItemStack itemStack = context.getStack();
        BlockPos blockPos = context.getBlockPos();
        Direction direction = context.getSide();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isOf(Blocks.SPAWNER) && (blockEntity = world.getBlockEntity(blockPos)) instanceof MobSpawnerBlockEntity) {
            MobSpawnerLogic mobSpawnerLogic = ((MobSpawnerBlockEntity)blockEntity).getLogic();
            EntityType<?> entityType = this.getEntityType(itemStack.getTag());
            mobSpawnerLogic.setEntityId(entityType);
            blockEntity.markDirty();
            world.updateListeners(blockPos, blockState, blockState, 3);
            itemStack.decrement(1);
            return ActionResult.CONSUME;
        }
        BlockPos blockPos2 = blockState.getCollisionShape(world, blockPos).isEmpty() ? blockPos : blockPos.offset(direction);
        EntityType<?> entityType2 = this.getEntityType(itemStack.getTag());
        if (entityType2.spawnFromItemStack((ServerWorld)world, itemStack, context.getPlayer(), blockPos2, SpawnReason.SPAWN_EGG, true, !Objects.equals(blockPos, blockPos2) && direction == Direction.UP) != null) {
            itemStack.decrement(1);
        }
        return ActionResult.CONSUME;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult hitResult = SpawnEggItem.rayTrace(world, user, RayTraceContext.FluidHandling.SOURCE_ONLY);
        if (((HitResult)hitResult).getType() != HitResult.Type.BLOCK) {
            return TypedActionResult.pass(itemStack);
        }
        if (!(world instanceof ServerWorld)) {
            return TypedActionResult.success(itemStack);
        }
        BlockHitResult blockHitResult = hitResult;
        BlockPos blockPos = blockHitResult.getBlockPos();
        if (!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock)) {
            return TypedActionResult.pass(itemStack);
        }
        if (!world.canPlayerModifyAt(user, blockPos) || !user.canPlaceOn(blockPos, blockHitResult.getSide(), itemStack)) {
            return TypedActionResult.fail(itemStack);
        }
        EntityType<?> entityType = this.getEntityType(itemStack.getTag());
        if (entityType.spawnFromItemStack((ServerWorld)world, itemStack, user, blockPos, SpawnReason.SPAWN_EGG, false, false) == null) {
            return TypedActionResult.pass(itemStack);
        }
        if (!user.abilities.creativeMode) {
            itemStack.decrement(1);
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.consume(itemStack);
    }

    public boolean isOfSameEntityType(@Nullable CompoundTag tag, EntityType<?> type) {
        return Objects.equals(this.getEntityType(tag), type);
    }

    @Environment(value=EnvType.CLIENT)
    public int getColor(int num) {
        return num == 0 ? this.primaryColor : this.secondaryColor;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static SpawnEggItem forEntity(@Nullable EntityType<?> type) {
        return SPAWN_EGGS.get(type);
    }

    public static Iterable<SpawnEggItem> getAll() {
        return Iterables.unmodifiableIterable(SPAWN_EGGS.values());
    }

    public EntityType<?> getEntityType(@Nullable CompoundTag tag) {
        CompoundTag compoundTag;
        if (tag != null && tag.contains("EntityTag", 10) && (compoundTag = tag.getCompound("EntityTag")).contains("id", 8)) {
            return EntityType.get(compoundTag.getString("id")).orElse(this.type);
        }
        return this.type;
    }

    public Optional<MobEntity> spawnBaby(PlayerEntity user, MobEntity mobEntity, EntityType<? extends MobEntity> entityType, ServerWorld serverWorld, Vec3d vec3d, ItemStack itemStack) {
        if (!this.isOfSameEntityType(itemStack.getTag(), entityType)) {
            return Optional.empty();
        }
        MobEntity mobEntity2 = mobEntity instanceof PassiveEntity ? ((PassiveEntity)mobEntity).createChild(serverWorld, (PassiveEntity)mobEntity) : entityType.create(serverWorld);
        if (mobEntity2 == null) {
            return Optional.empty();
        }
        mobEntity2.setBaby(true);
        if (!mobEntity2.isBaby()) {
            return Optional.empty();
        }
        mobEntity2.refreshPositionAndAngles(vec3d.getX(), vec3d.getY(), vec3d.getZ(), 0.0f, 0.0f);
        serverWorld.spawnEntity(mobEntity2);
        if (itemStack.hasCustomName()) {
            mobEntity2.setCustomName(itemStack.getName());
        }
        if (!user.abilities.creativeMode) {
            itemStack.decrement(1);
        }
        return Optional.of(mobEntity2);
    }
}

