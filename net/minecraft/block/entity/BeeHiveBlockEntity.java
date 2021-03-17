/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.yarn.constants.NbtTypeIds;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BeehiveBlockEntity
extends BlockEntity {
    private final List<Bee> bees = Lists.newArrayList();
    @Nullable
    private BlockPos flowerPos;

    public BeehiveBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.BEEHIVE, pos, state);
    }

    @Override
    public void markDirty() {
        if (this.isNearFire()) {
            this.angerBees(null, this.world.getBlockState(this.getPos()), BeeState.EMERGENCY);
        }
        super.markDirty();
    }

    public boolean isNearFire() {
        if (this.world == null) {
            return false;
        }
        for (BlockPos blockPos : BlockPos.iterate(this.pos.add(-1, -1, -1), this.pos.add(1, 1, 1))) {
            if (!(this.world.getBlockState(blockPos).getBlock() instanceof FireBlock)) continue;
            return true;
        }
        return false;
    }

    public boolean hasNoBees() {
        return this.bees.isEmpty();
    }

    public boolean isFullOfBees() {
        return this.bees.size() == 3;
    }

    public void angerBees(@Nullable PlayerEntity player, BlockState state, BeeState beeState) {
        List<Entity> list = this.tryReleaseBee(state, beeState);
        if (player != null) {
            for (Entity entity : list) {
                if (!(entity instanceof BeeEntity)) continue;
                BeeEntity beeEntity = (BeeEntity)entity;
                if (!(player.getPos().squaredDistanceTo(entity.getPos()) <= 16.0)) continue;
                if (!this.isSmoked()) {
                    beeEntity.setTarget(player);
                    continue;
                }
                beeEntity.setCannotEnterHiveTicks(400);
            }
        }
    }

    private List<Entity> tryReleaseBee(BlockState state, BeeState beeState) {
        ArrayList<Entity> list = Lists.newArrayList();
        this.bees.removeIf(bee -> BeehiveBlockEntity.releaseBee(this.world, this.pos, state, bee, list, beeState, this.flowerPos));
        return list;
    }

    public void tryEnterHive(Entity entity, boolean hasNectar) {
        this.tryEnterHive(entity, hasNectar, 0);
    }

    public int getBeeCount() {
        return this.bees.size();
    }

    public static int getHoneyLevel(BlockState state) {
        return state.get(BeehiveBlock.HONEY_LEVEL);
    }

    public boolean isSmoked() {
        return CampfireBlock.isLitCampfireInRange(this.world, this.getPos());
    }

    public void tryEnterHive(Entity entity, boolean hasNectar, int ticksInHive) {
        if (this.bees.size() >= 3) {
            return;
        }
        entity.stopRiding();
        entity.removeAllPassengers();
        NbtCompound nbtCompound = new NbtCompound();
        entity.saveToTag(nbtCompound);
        this.bees.add(new Bee(nbtCompound, ticksInHive, hasNectar ? 2400 : 600));
        if (this.world != null) {
            BeeEntity beeEntity;
            if (entity instanceof BeeEntity && (beeEntity = (BeeEntity)entity).hasFlower() && (!this.hasFlowerPos() || this.world.random.nextBoolean())) {
                this.flowerPos = beeEntity.getFlowerPos();
            }
            BlockPos blockPos = this.getPos();
            this.world.playSound(null, (double)blockPos.getX(), (double)blockPos.getY(), blockPos.getZ(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        entity.discard();
    }

    private static boolean releaseBee(World world, BlockPos blockPos, BlockState blockState, Bee bee, @Nullable List<Entity> list, BeeState beeState, @Nullable BlockPos blockPos2) {
        boolean bl;
        if ((world.isNight() || world.isRaining()) && beeState != BeeState.EMERGENCY) {
            return false;
        }
        NbtCompound nbtCompound = bee.entityData;
        nbtCompound.remove("Passengers");
        nbtCompound.remove("Leash");
        nbtCompound.remove("UUID");
        Direction direction = blockState.get(BeehiveBlock.FACING);
        BlockPos blockPos3 = blockPos.offset(direction);
        boolean bl2 = bl = !world.getBlockState(blockPos3).getCollisionShape(world, blockPos3).isEmpty();
        if (bl && beeState != BeeState.EMERGENCY) {
            return false;
        }
        Entity entity2 = EntityType.loadEntityWithPassengers(nbtCompound, world, entity -> entity);
        if (entity2 != null) {
            if (!entity2.getType().isIn(EntityTypeTags.BEEHIVE_INHABITORS)) {
                return false;
            }
            if (entity2 instanceof BeeEntity) {
                BeeEntity beeEntity = (BeeEntity)entity2;
                if (blockPos2 != null && !beeEntity.hasFlower() && world.random.nextFloat() < 0.9f) {
                    beeEntity.setFlowerPos(blockPos2);
                }
                if (beeState == BeeState.HONEY_DELIVERED) {
                    int i;
                    beeEntity.onHoneyDelivered();
                    if (blockState.isIn(BlockTags.BEEHIVES) && (i = BeehiveBlockEntity.getHoneyLevel(blockState)) < 5) {
                        int j;
                        int n = j = world.random.nextInt(100) == 0 ? 2 : 1;
                        if (i + j > 5) {
                            --j;
                        }
                        world.setBlockState(blockPos, (BlockState)blockState.with(BeehiveBlock.HONEY_LEVEL, i + j));
                    }
                }
                BeehiveBlockEntity.ageBee(bee.ticksInHive, beeEntity);
                if (list != null) {
                    list.add(beeEntity);
                }
                float f = entity2.getWidth();
                double d = bl ? 0.0 : 0.55 + (double)(f / 2.0f);
                double e = (double)blockPos.getX() + 0.5 + d * (double)direction.getOffsetX();
                double g = (double)blockPos.getY() + 0.5 - (double)(entity2.getHeight() / 2.0f);
                double h = (double)blockPos.getZ() + 0.5 + d * (double)direction.getOffsetZ();
                entity2.refreshPositionAndAngles(e, g, h, entity2.yaw, entity2.pitch);
            }
            world.playSound(null, blockPos, SoundEvents.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            return world.spawnEntity(entity2);
        }
        return false;
    }

    private static void ageBee(int i, BeeEntity beeEntity) {
        int j = beeEntity.getBreedingAge();
        if (j < 0) {
            beeEntity.setBreedingAge(Math.min(0, j + i));
        } else if (j > 0) {
            beeEntity.setBreedingAge(Math.max(0, j - i));
        }
        beeEntity.setLoveTicks(Math.max(0, beeEntity.getLoveTicks() - i));
        beeEntity.resetPollinationTicks();
    }

    private boolean hasFlowerPos() {
        return this.flowerPos != null;
    }

    private static void tickBees(World world, BlockPos blockPos, BlockState blockState, List<Bee> list, @Nullable BlockPos blockPos2) {
        Iterator<Bee> iterator = list.iterator();
        while (iterator.hasNext()) {
            Bee bee = iterator.next();
            if (bee.ticksInHive > bee.minOccupationTicks) {
                BeeState beeState;
                BeeState beeState2 = beeState = bee.entityData.getBoolean("HasNectar") ? BeeState.HONEY_DELIVERED : BeeState.BEE_RELEASED;
                if (BeehiveBlockEntity.releaseBee(world, blockPos, blockState, bee, null, beeState, blockPos2)) {
                    iterator.remove();
                }
            }
            bee.ticksInHive++;
        }
    }

    public static void serverTick(World world, BlockPos blockPos, BlockState blockState, BeehiveBlockEntity beehiveBlockEntity) {
        BeehiveBlockEntity.tickBees(world, blockPos, blockState, beehiveBlockEntity.bees, beehiveBlockEntity.flowerPos);
        if (!beehiveBlockEntity.bees.isEmpty() && world.getRandom().nextDouble() < 0.005) {
            double d = (double)blockPos.getX() + 0.5;
            double e = blockPos.getY();
            double f = (double)blockPos.getZ() + 0.5;
            world.playSound(null, d, e, f, SoundEvents.BLOCK_BEEHIVE_WORK, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        DebugInfoSender.sendBeehiveDebugData(world, blockPos, blockState, beehiveBlockEntity);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.bees.clear();
        NbtList nbtList = tag.getList("Bees", NbtTypeIds.COMPOUND);
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            Bee bee = new Bee(nbtCompound.getCompound("EntityData"), nbtCompound.getInt("TicksInHive"), nbtCompound.getInt("MinOccupationTicks"));
            this.bees.add(bee);
        }
        this.flowerPos = null;
        if (tag.contains("FlowerPos")) {
            this.flowerPos = NbtHelper.toBlockPos(tag.getCompound("FlowerPos"));
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.put("Bees", this.getBees());
        if (this.hasFlowerPos()) {
            tag.put("FlowerPos", NbtHelper.fromBlockPos(this.flowerPos));
        }
        return tag;
    }

    public NbtList getBees() {
        NbtList nbtList = new NbtList();
        for (Bee bee : this.bees) {
            bee.entityData.remove("UUID");
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.put("EntityData", bee.entityData);
            nbtCompound.putInt("TicksInHive", bee.ticksInHive);
            nbtCompound.putInt("MinOccupationTicks", bee.minOccupationTicks);
            nbtList.add(nbtCompound);
        }
        return nbtList;
    }

    static class Bee {
        private final NbtCompound entityData;
        private int ticksInHive;
        private final int minOccupationTicks;

        private Bee(NbtCompound entityData, int ticksInHive, int minOccupationTicks) {
            entityData.remove("UUID");
            this.entityData = entityData;
            this.ticksInHive = ticksInHive;
            this.minOccupationTicks = minOccupationTicks;
        }
    }

    public static enum BeeState {
        HONEY_DELIVERED,
        BEE_RELEASED,
        EMERGENCY;

    }
}

