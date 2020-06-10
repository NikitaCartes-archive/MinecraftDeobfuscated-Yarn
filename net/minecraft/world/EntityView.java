/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

public interface EntityView {
    public List<Entity> getEntities(@Nullable Entity var1, Box var2, @Nullable Predicate<? super Entity> var3);

    public <T extends Entity> List<T> getEntities(Class<? extends T> var1, Box var2, @Nullable Predicate<? super T> var3);

    default public <T extends Entity> List<T> getEntitiesIncludingUngeneratedChunks(Class<? extends T> entityClass, Box box, @Nullable Predicate<? super T> predicate) {
        return this.getEntities(entityClass, box, predicate);
    }

    public List<? extends PlayerEntity> getPlayers();

    default public List<Entity> getEntities(@Nullable Entity except, Box box) {
        return this.getEntities(except, box, EntityPredicates.EXCEPT_SPECTATOR);
    }

    default public boolean intersectsEntities(@Nullable Entity entity, VoxelShape shape) {
        if (shape.isEmpty()) {
            return true;
        }
        for (Entity entity2 : this.getEntities(entity, shape.getBoundingBox())) {
            if (entity2.removed || !entity2.inanimate || entity != null && entity2.isConnectedThroughVehicle(entity) || !VoxelShapes.matchesAnywhere(shape, VoxelShapes.cuboid(entity2.getBoundingBox()), BooleanBiFunction.AND)) continue;
            return false;
        }
        return true;
    }

    default public <T extends Entity> List<T> getNonSpectatingEntities(Class<? extends T> entityClass, Box box) {
        return this.getEntities(entityClass, box, EntityPredicates.EXCEPT_SPECTATOR);
    }

    default public <T extends Entity> List<T> getEntitiesIncludingUngeneratedChunks(Class<? extends T> entityClass, Box box) {
        return this.getEntitiesIncludingUngeneratedChunks(entityClass, box, EntityPredicates.EXCEPT_SPECTATOR);
    }

    default public Stream<VoxelShape> getEntityCollisions(@Nullable Entity entity, Box box, Predicate<Entity> predicate) {
        if (box.getAverageSideLength() < 1.0E-7) {
            return Stream.empty();
        }
        Box box2 = box.expand(1.0E-7);
        return this.getEntities(entity, box2, predicate.and(e -> entity == null || !entity.isConnectedThroughVehicle((Entity)e))).stream().flatMap(entity2 -> {
            Box box2;
            if (entity != null && (box2 = entity.getHardCollisionBox((Entity)entity2)) != null && box2.intersects(box2)) {
                return Stream.of(entity2.getCollisionBox(), box2);
            }
            return Stream.of(entity2.getCollisionBox());
        }).filter(Objects::nonNull).map(VoxelShapes::cuboid);
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(double x, double y, double z, double maxDistance, @Nullable Predicate<Entity> targetPredicate) {
        double d = -1.0;
        PlayerEntity playerEntity = null;
        for (PlayerEntity playerEntity2 : this.getPlayers()) {
            if (targetPredicate != null && !targetPredicate.test(playerEntity2)) continue;
            double e = playerEntity2.squaredDistanceTo(x, y, z);
            if (!(maxDistance < 0.0) && !(e < maxDistance * maxDistance) || d != -1.0 && !(e < d)) continue;
            d = e;
            playerEntity = playerEntity2;
        }
        return playerEntity;
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(Entity entity, double maxDistance) {
        return this.getClosestPlayer(entity.getX(), entity.getY(), entity.getZ(), maxDistance, false);
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(double x, double y, double z, double maxDistance, boolean ignoreCreative) {
        Predicate<Entity> predicate = ignoreCreative ? EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR : EntityPredicates.EXCEPT_SPECTATOR;
        return this.getClosestPlayer(x, y, z, maxDistance, predicate);
    }

    default public boolean isPlayerInRange(double x, double y, double z, double range) {
        for (PlayerEntity playerEntity : this.getPlayers()) {
            if (!EntityPredicates.EXCEPT_SPECTATOR.test(playerEntity) || !EntityPredicates.VALID_LIVING_ENTITY.test(playerEntity)) continue;
            double d = playerEntity.squaredDistanceTo(x, y, z);
            if (!(range < 0.0) && !(d < range * range)) continue;
            return true;
        }
        return false;
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, LivingEntity entity) {
        return this.getClosestEntity(this.getPlayers(), targetPredicate, entity, entity.getX(), entity.getY(), entity.getZ());
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, LivingEntity entity, double x, double y, double z) {
        return this.getClosestEntity(this.getPlayers(), targetPredicate, entity, x, y, z);
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, double x, double y, double z) {
        return this.getClosestEntity(this.getPlayers(), targetPredicate, null, x, y, z);
    }

    @Nullable
    default public <T extends LivingEntity> T getClosestEntity(Class<? extends T> entityClass, TargetPredicate targetPredicate, @Nullable LivingEntity entity, double x, double y, double z, Box box) {
        return this.getClosestEntity(this.getEntities(entityClass, box, null), targetPredicate, entity, x, y, z);
    }

    @Nullable
    default public <T extends LivingEntity> T getClosestEntityIncludingUngeneratedChunks(Class<? extends T> entityClass, TargetPredicate targetPredicate, @Nullable LivingEntity entity, double x, double y, double z, Box box) {
        return this.getClosestEntity(this.getEntitiesIncludingUngeneratedChunks(entityClass, box, null), targetPredicate, entity, x, y, z);
    }

    @Nullable
    default public <T extends LivingEntity> T getClosestEntity(List<? extends T> entityList, TargetPredicate targetPredicate, @Nullable LivingEntity entity, double x, double y, double z) {
        double d = -1.0;
        LivingEntity livingEntity = null;
        for (LivingEntity livingEntity2 : entityList) {
            if (!targetPredicate.test(entity, livingEntity2)) continue;
            double e = livingEntity2.squaredDistanceTo(x, y, z);
            if (d != -1.0 && !(e < d)) continue;
            d = e;
            livingEntity = livingEntity2;
        }
        return (T)livingEntity;
    }

    default public List<PlayerEntity> getPlayers(TargetPredicate targetPredicate, LivingEntity entity, Box box) {
        ArrayList<PlayerEntity> list = Lists.newArrayList();
        for (PlayerEntity playerEntity : this.getPlayers()) {
            if (!box.contains(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ()) || !targetPredicate.test(entity, playerEntity)) continue;
            list.add(playerEntity);
        }
        return list;
    }

    default public <T extends LivingEntity> List<T> getTargets(Class<? extends T> entityClass, TargetPredicate targetPredicate, LivingEntity targettingEntity, Box box) {
        List<T> list = this.getEntities(entityClass, box, null);
        ArrayList<LivingEntity> list2 = Lists.newArrayList();
        for (LivingEntity livingEntity : list) {
            if (!targetPredicate.test(targettingEntity, livingEntity)) continue;
            list2.add(livingEntity);
        }
        return list2;
    }

    @Nullable
    default public PlayerEntity getPlayerByUuid(UUID uuid) {
        for (int i = 0; i < this.getPlayers().size(); ++i) {
            PlayerEntity playerEntity = this.getPlayers().get(i);
            if (!uuid.equals(playerEntity.getUuid())) continue;
            return playerEntity;
        }
        return null;
    }
}

