/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

public interface EntityView {
    public List<Entity> getEntities(@Nullable Entity var1, Box var2, @Nullable Predicate<? super Entity> var3);

    public <T extends Entity> List<T> getEntities(Class<? extends T> var1, Box var2, @Nullable Predicate<? super T> var3);

    public List<? extends PlayerEntity> getPlayers();

    default public List<Entity> getEntities(@Nullable Entity entity, Box box) {
        return this.getEntities(entity, box, EntityPredicates.EXCEPT_SPECTATOR);
    }

    default public boolean intersectsEntities(@Nullable Entity entity3, VoxelShape voxelShape) {
        if (voxelShape.isEmpty()) {
            return true;
        }
        return this.getEntities(entity3, voxelShape.getBoundingBox()).stream().filter(entity2 -> !entity2.removed && entity2.field_6033 && (entity3 == null || !entity2.isConnectedThroughVehicle(entity3))).noneMatch(entity -> VoxelShapes.matchesAnywhere(voxelShape, VoxelShapes.cuboid(entity.getBoundingBox()), BooleanBiFunction.AND));
    }

    default public <T extends Entity> List<T> getEntities(Class<? extends T> class_, Box box) {
        return this.getEntities(class_, box, EntityPredicates.EXCEPT_SPECTATOR);
    }

    default public Stream<VoxelShape> getCollisionShapes(@Nullable Entity entity, VoxelShape voxelShape, Set<Entity> set) {
        if (voxelShape.isEmpty()) {
            return Stream.empty();
        }
        Box box = voxelShape.getBoundingBox();
        return this.getEntities(entity, box.expand(0.25)).stream().filter(entity2 -> !set.contains(entity2) && (entity == null || !entity.isConnectedThroughVehicle((Entity)entity2))).flatMap(entity2 -> Stream.of(entity2.getCollisionBox(), entity == null ? null : entity.method_5708((Entity)entity2)).filter(Objects::nonNull).filter(box2 -> box2.intersects(box)).map(VoxelShapes::cuboid));
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(double d, double e, double f, double g, @Nullable Predicate<Entity> predicate) {
        double h = -1.0;
        PlayerEntity playerEntity = null;
        for (PlayerEntity playerEntity2 : this.getPlayers()) {
            if (predicate != null && !predicate.test(playerEntity2)) continue;
            double i = playerEntity2.squaredDistanceTo(d, e, f);
            if (!(g < 0.0) && !(i < g * g) || h != -1.0 && !(i < h)) continue;
            h = i;
            playerEntity = playerEntity2;
        }
        return playerEntity;
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(Entity entity, double d) {
        return this.getClosestPlayer(entity.x, entity.y, entity.z, d, false);
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(double d, double e, double f, double g, boolean bl) {
        Predicate<Entity> predicate = bl ? EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR : EntityPredicates.EXCEPT_SPECTATOR;
        return this.getClosestPlayer(d, e, f, g, predicate);
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(double d, double e, double f) {
        double g = -1.0;
        PlayerEntity playerEntity = null;
        for (PlayerEntity playerEntity2 : this.getPlayers()) {
            if (!EntityPredicates.EXCEPT_SPECTATOR.test(playerEntity2)) continue;
            double h = playerEntity2.squaredDistanceTo(d, playerEntity2.y, e);
            if (!(f < 0.0) && !(h < f * f) || g != -1.0 && !(h < g)) continue;
            g = h;
            playerEntity = playerEntity2;
        }
        return playerEntity;
    }

    default public boolean isPlayerInRange(double d, double e, double f, double g) {
        for (PlayerEntity playerEntity : this.getPlayers()) {
            if (!EntityPredicates.EXCEPT_SPECTATOR.test(playerEntity) || !EntityPredicates.VALID_ENTITY_LIVING.test(playerEntity)) continue;
            double h = playerEntity.squaredDistanceTo(d, e, f);
            if (!(g < 0.0) && !(h < g * g)) continue;
            return true;
        }
        return false;
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, LivingEntity livingEntity) {
        return this.getClosestEntity(this.getPlayers(), targetPredicate, livingEntity, livingEntity.x, livingEntity.y, livingEntity.z);
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, LivingEntity livingEntity, double d, double e, double f) {
        return this.getClosestEntity(this.getPlayers(), targetPredicate, livingEntity, d, e, f);
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, double d, double e, double f) {
        return this.getClosestEntity(this.getPlayers(), targetPredicate, null, d, e, f);
    }

    @Nullable
    default public <T extends LivingEntity> T getClosestEntity(Class<? extends T> class_, TargetPredicate targetPredicate, @Nullable LivingEntity livingEntity, double d, double e, double f, Box box) {
        return this.getClosestEntity(this.getEntities(class_, box, null), targetPredicate, livingEntity, d, e, f);
    }

    @Nullable
    default public <T extends LivingEntity> T getClosestEntity(List<? extends T> list, TargetPredicate targetPredicate, @Nullable LivingEntity livingEntity, double d, double e, double f) {
        double g = -1.0;
        LivingEntity livingEntity2 = null;
        for (LivingEntity livingEntity3 : list) {
            if (!targetPredicate.test(livingEntity, livingEntity3)) continue;
            double h = livingEntity3.squaredDistanceTo(d, e, f);
            if (g != -1.0 && !(h < g)) continue;
            g = h;
            livingEntity2 = livingEntity3;
        }
        return (T)livingEntity2;
    }

    default public List<PlayerEntity> getPlayersInBox(TargetPredicate targetPredicate, LivingEntity livingEntity, Box box) {
        ArrayList<PlayerEntity> list = Lists.newArrayList();
        for (PlayerEntity playerEntity : this.getPlayers()) {
            if (!box.contains(playerEntity.x, playerEntity.y, playerEntity.z) || !targetPredicate.test(livingEntity, playerEntity)) continue;
            list.add(playerEntity);
        }
        return list;
    }

    default public <T extends LivingEntity> List<T> getTargets(Class<? extends T> class_, TargetPredicate targetPredicate, LivingEntity livingEntity, Box box) {
        List<T> list = this.getEntities(class_, box, null);
        ArrayList<LivingEntity> list2 = Lists.newArrayList();
        for (LivingEntity livingEntity2 : list) {
            if (!targetPredicate.test(livingEntity, livingEntity2)) continue;
            list2.add(livingEntity2);
        }
        return list2;
    }

    @Nullable
    default public PlayerEntity getPlayerByUuid(UUID uUID) {
        for (int i = 0; i < this.getPlayers().size(); ++i) {
            PlayerEntity playerEntity = this.getPlayers().get(i);
            if (!uUID.equals(playerEntity.getUuid())) continue;
            return playerEntity;
        }
        return null;
    }
}

