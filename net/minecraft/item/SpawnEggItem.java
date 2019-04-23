/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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

    public SpawnEggItem(EntityType<?> entityType, int i, int j, Item.Settings settings) {
        super(settings);
        this.type = entityType;
        this.primaryColor = i;
        this.secondaryColor = j;
        SPAWN_EGGS.put(entityType, this);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
        BlockEntity blockEntity;
        World world = itemUsageContext.getWorld();
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        ItemStack itemStack = itemUsageContext.getItemStack();
        BlockPos blockPos = itemUsageContext.getBlockPos();
        Direction direction = itemUsageContext.getFacing();
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (block == Blocks.SPAWNER && (blockEntity = world.getBlockEntity(blockPos)) instanceof MobSpawnerBlockEntity) {
            MobSpawnerLogic mobSpawnerLogic = ((MobSpawnerBlockEntity)blockEntity).getLogic();
            EntityType<?> entityType = this.entityTypeFromTag(itemStack.getTag());
            mobSpawnerLogic.setEntityId(entityType);
            blockEntity.markDirty();
            world.updateListeners(blockPos, blockState, blockState, 3);
            itemStack.subtractAmount(1);
            return ActionResult.SUCCESS;
        }
        BlockPos blockPos2 = blockState.getCollisionShape(world, blockPos).isEmpty() ? blockPos : blockPos.offset(direction);
        EntityType<?> entityType2 = this.entityTypeFromTag(itemStack.getTag());
        if (entityType2.spawnFromItemStack(world, itemStack, itemUsageContext.getPlayer(), blockPos2, SpawnType.SPAWN_EGG, true, !Objects.equals(blockPos, blockPos2) && direction == Direction.UP) != null) {
            itemStack.subtractAmount(1);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        if (world.isClient) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack);
        }
        HitResult hitResult = SpawnEggItem.getHitResult(world, playerEntity, RayTraceContext.FluidHandling.SOURCE_ONLY);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack);
        }
        BlockHitResult blockHitResult = (BlockHitResult)hitResult;
        BlockPos blockPos = blockHitResult.getBlockPos();
        if (!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock)) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack);
        }
        if (!world.canPlayerModifyAt(playerEntity, blockPos) || !playerEntity.canPlaceOn(blockPos, blockHitResult.getSide(), itemStack)) {
            return new TypedActionResult<ItemStack>(ActionResult.FAIL, itemStack);
        }
        EntityType<?> entityType = this.entityTypeFromTag(itemStack.getTag());
        if (entityType.spawnFromItemStack(world, itemStack, playerEntity, blockPos, SpawnType.SPAWN_EGG, false, false) == null) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack);
        }
        if (!playerEntity.abilities.creativeMode) {
            itemStack.subtractAmount(1);
        }
        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, itemStack);
    }

    public boolean isOfSameEntityType(@Nullable CompoundTag compoundTag, EntityType<?> entityType) {
        return Objects.equals(this.entityTypeFromTag(compoundTag), entityType);
    }

    @Environment(value=EnvType.CLIENT)
    public int getColor(int i) {
        return i == 0 ? this.primaryColor : this.secondaryColor;
    }

    @Environment(value=EnvType.CLIENT)
    public static SpawnEggItem forEntity(@Nullable EntityType<?> entityType) {
        return SPAWN_EGGS.get(entityType);
    }

    public static Iterable<SpawnEggItem> iterator() {
        return Iterables.unmodifiableIterable(SPAWN_EGGS.values());
    }

    public EntityType<?> entityTypeFromTag(@Nullable CompoundTag compoundTag) {
        CompoundTag compoundTag2;
        if (compoundTag != null && compoundTag.containsKey("EntityTag", 10) && (compoundTag2 = compoundTag.getCompound("EntityTag")).containsKey("id", 8)) {
            return EntityType.get(compoundTag2.getString("id")).orElse(this.type);
        }
        return this.type;
    }
}

