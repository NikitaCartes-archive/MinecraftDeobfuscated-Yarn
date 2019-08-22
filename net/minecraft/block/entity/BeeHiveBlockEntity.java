/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.block.BeeHiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TagHelper;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BeeHiveBlockEntity
extends BlockEntity
implements Tickable {
    private final Map<UUID, Bee> bees = Maps.newHashMap();
    private BlockPos flowerPos = BlockPos.ORIGIN;

    public BeeHiveBlockEntity() {
        super(BlockEntityType.BEEHIVE);
    }

    public boolean isFullOfBees() {
        return this.bees.size() == 3;
    }

    public void angerBees(@Nullable PlayerEntity playerEntity, BeeState beeState) {
        List<Entity> list = this.tryReleaseBee(beeState);
        if (playerEntity != null) {
            for (Entity entity : list) {
                if (!(entity instanceof BeeEntity)) continue;
                BeeEntity beeEntity = (BeeEntity)entity;
                if (!(playerEntity.getPos().squaredDistanceTo(entity.getPos()) <= 16.0)) continue;
                if (!this.isBeingSmoked(this.world, this.getPos())) {
                    beeEntity.setBeeAttacker(playerEntity);
                    continue;
                }
                beeEntity.setCannotEnterHiveTicks(400);
            }
        }
    }

    private boolean isBeingSmoked(World world, BlockPos blockPos) {
        for (int i = 1; i <= 5; ++i) {
            BlockState blockState = world.getBlockState(blockPos.down(i));
            if (blockState.isAir()) continue;
            return blockState.getBlock() == Blocks.CAMPFIRE;
        }
        return false;
    }

    private List<Entity> tryReleaseBee(BeeState beeState) {
        ArrayList<Entity> list = Lists.newArrayList();
        this.bees.entrySet().removeIf(entry -> this.releaseBee(((Bee)entry.getValue()).entityData, list, beeState));
        return list;
    }

    public void tryEnterHive(Entity entity, boolean bl) {
        this.tryEnterHive(entity, bl, 0);
    }

    public void tryEnterHive(Entity entity, boolean bl, int i) {
        if (this.bees.size() >= 3) {
            return;
        }
        CompoundTag compoundTag = new CompoundTag();
        entity.saveToTag(compoundTag);
        this.bees.put(entity.getUuid(), new Bee(compoundTag, i, bl ? 2400 : 600));
        if (this.world != null) {
            if (entity instanceof BeeEntity) {
                BeeEntity beeEntity = (BeeEntity)entity;
                if (!this.hasFlowerPos() || beeEntity.hasFlower() && this.world.random.nextBoolean()) {
                    this.flowerPos = beeEntity.getFlowerPos();
                }
            }
            BlockPos blockPos = this.getPos();
            this.world.playSound(null, (double)blockPos.getX(), (double)blockPos.getY(), blockPos.getZ(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        entity.remove();
    }

    private boolean releaseBee(CompoundTag compoundTag, @Nullable List<Entity> list, BeeState beeState) {
        BlockPos blockPos4;
        Entity entity;
        BlockPos blockPos3;
        Iterator<Direction> direction;
        BlockPos blockPos2;
        Optional<Object> optional = Optional.empty();
        BlockPos blockPos = this.getPos();
        BlockState blockState = this.getCachedState();
        if (blockState.contains(BeeHiveBlock.FACING) && this.world.getBlockState(blockPos2 = blockPos.add(((Direction)((Object)(direction = blockState.get(BeeHiveBlock.FACING)))).getOffsetX() * 2, ((Direction)((Object)direction)).getOffsetY(), ((Direction)((Object)direction)).getOffsetZ() * 2)).getCollisionShape(this.world, blockPos2).isEmpty()) {
            optional = Optional.of(blockPos2);
        }
        if (!optional.isPresent()) {
            for (Direction direction2 : Direction.Type.HORIZONTAL) {
                blockPos3 = blockPos.add(direction2.getOffsetX() * 2, direction2.getOffsetY(), direction2.getOffsetZ() * 2);
                if (!this.world.getBlockState(blockPos3).getCollisionShape(this.world, blockPos3).isEmpty()) continue;
                optional = Optional.of(blockPos3);
                break;
            }
        }
        if (!optional.isPresent()) {
            for (Direction direction2 : Direction.Type.VERTICAL) {
                blockPos3 = blockPos.add(direction2.getOffsetX() * 2, direction2.getOffsetY(), direction2.getOffsetZ() * 2);
                if (!this.world.getBlockState(blockPos3).getCollisionShape(this.world, blockPos3).isEmpty()) continue;
                optional = Optional.of(blockPos3);
            }
        }
        if (optional.isPresent() && (entity = EntityType.loadEntityWithPassengers(compoundTag, this.world, arg_0 -> BeeHiveBlockEntity.method_21853(blockPos4 = (BlockPos)optional.get(), arg_0))) != null) {
            if (entity instanceof BeeEntity) {
                BeeEntity beeEntity = (BeeEntity)entity;
                if (this.hasFlowerPos() && !beeEntity.hasFlower() && this.world.random.nextFloat() < 0.9f) {
                    beeEntity.setFlowerPos(this.flowerPos);
                }
                if (beeState == BeeState.HONEY_DELIVERED) {
                    int i;
                    beeEntity.onHoneyDelivered();
                    if (blockState.getBlock() == Blocks.BEE_NEST && (i = blockState.get(BeeHiveBlock.HONEY_LEVEL).intValue()) < 5) {
                        int j;
                        int n = j = this.world.random.nextInt(100) == 0 ? 2 : 1;
                        if (i + j > 5) {
                            --j;
                        }
                        this.world.setBlockState(this.getPos(), (BlockState)blockState.with(BeeHiveBlock.HONEY_LEVEL, i + j));
                    }
                }
                if (list != null) {
                    beeEntity.resetPollinationTicks();
                    list.add(beeEntity);
                }
            }
            blockPos3 = this.getPos();
            this.world.playSound(null, (double)blockPos3.getX(), (double)blockPos3.getY(), blockPos3.getZ(), SoundEvents.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            return this.world.spawnEntity(entity);
        }
        return false;
    }

    private boolean hasFlowerPos() {
        return this.flowerPos != BlockPos.ORIGIN;
    }

    private void tickBees() {
        Iterator<Map.Entry<UUID, Bee>> iterator = this.bees.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Bee> entry = iterator.next();
            Bee bee = entry.getValue();
            if (bee.ticksInHive > bee.minOccupationTIcks) {
                CompoundTag compoundTag;
                BeeState beeState = (compoundTag = bee.entityData).getBoolean("HasNectar") ? BeeState.HONEY_DELIVERED : BeeState.BEE_RELEASED;
                if (!this.releaseBee(compoundTag, null, beeState)) continue;
                iterator.remove();
                continue;
            }
            bee.ticksInHive++;
        }
    }

    @Override
    public void tick() {
        this.tickBees();
        BlockPos blockPos = this.getPos();
        if (this.bees.size() > 0 && this.world.getRandom().nextDouble() < 0.005) {
            double d = (double)blockPos.getX() + 0.5;
            double e = blockPos.getY();
            double f = (double)blockPos.getZ() + 0.5;
            this.world.playSound(null, d, e, f, SoundEvents.BLOCK_BEEHIVE_WORK, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.bees.clear();
        ListTag listTag = compoundTag.getList("Bees", 10);
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag2 = listTag.getCompoundTag(i);
            Bee bee = new Bee(compoundTag2.getCompound("EntityData"), compoundTag2.getInt("TicksInHive"), compoundTag2.getInt("MinOccupationTicks"));
            this.bees.put(bee.entityData.getUuid("UUID"), bee);
        }
        this.flowerPos = TagHelper.deserializeBlockPos(compoundTag.getCompound("FlowerPos"));
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        super.toTag(compoundTag);
        compoundTag.put("Bees", this.getBees());
        compoundTag.put("FlowerPos", TagHelper.serializeBlockPos(this.flowerPos));
        return compoundTag;
    }

    private ListTag getBees() {
        ListTag listTag = new ListTag();
        for (Map.Entry<UUID, Bee> entry : this.bees.entrySet()) {
            Bee bee = entry.getValue();
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.put("EntityData", bee.entityData);
            compoundTag.putInt("TicksInHive", bee.ticksInHive);
            compoundTag.putInt("MinOccupationTicks", bee.minOccupationTIcks);
            listTag.add(compoundTag);
        }
        return listTag;
    }

    private static /* synthetic */ Entity method_21853(BlockPos blockPos, Entity entity) {
        entity.setPositionAndAngles(blockPos.getX(), blockPos.getY(), blockPos.getZ(), entity.yaw, entity.pitch);
        return entity;
    }

    static class Bee {
        private final CompoundTag entityData;
        private int ticksInHive;
        private final int minOccupationTIcks;

        private Bee(CompoundTag compoundTag, int i, int j) {
            this.entityData = compoundTag;
            this.ticksInHive = i;
            this.minOccupationTIcks = j;
        }
    }

    public static enum BeeState {
        HONEY_DELIVERED,
        BEE_RELEASED;

    }
}

