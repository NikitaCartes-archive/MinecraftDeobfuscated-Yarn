/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CompassAnglePredicateProvider
implements UnclampedModelPredicateProvider {
    public static final int field_38798 = 0;
    private final AngleInterpolator aimedInterpolator = new AngleInterpolator();
    private final AngleInterpolator aimlessInterpolator = new AngleInterpolator();
    public final CompassTarget compassTarget;

    public CompassAnglePredicateProvider(CompassTarget compassTarget) {
        this.compassTarget = compassTarget;
    }

    @Override
    public float unclampedCall(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i) {
        Entity entity;
        Entity entity2 = entity = livingEntity != null ? livingEntity : itemStack.getHolder();
        if (entity == null) {
            return 0.0f;
        }
        if ((clientWorld = this.getClientWorld(entity, clientWorld)) == null) {
            return 0.0f;
        }
        return this.getAngle(itemStack, clientWorld, i, entity);
    }

    private float getAngle(ItemStack stack, ClientWorld world, int seed, Entity entity) {
        GlobalPos globalPos = this.compassTarget.getPos(world, stack, entity);
        long l = world.getTime();
        if (!this.canPointTo(entity, globalPos)) {
            return this.getAimlessAngle(seed, l);
        }
        return this.getAngleTo(entity, l, globalPos.getPos());
    }

    private float getAimlessAngle(int seed, long time) {
        if (this.aimlessInterpolator.shouldUpdate(time)) {
            this.aimlessInterpolator.update(time, Math.random());
        }
        double d = this.aimlessInterpolator.value + (double)((float)this.scatter(seed) / 2.14748365E9f);
        return MathHelper.floorMod((float)d, 1.0f);
    }

    private float getAngleTo(Entity entity, long time, BlockPos pos) {
        double f;
        PlayerEntity playerEntity;
        double d = this.getAngleTo(entity, pos);
        double e = this.getBodyYaw(entity);
        if (entity instanceof PlayerEntity && (playerEntity = (PlayerEntity)entity).isMainPlayer()) {
            if (this.aimedInterpolator.shouldUpdate(time)) {
                this.aimedInterpolator.update(time, 0.5 - (e - 0.25));
            }
            f = d + this.aimedInterpolator.value;
        } else {
            f = 0.5 - (e - 0.25 - d);
        }
        return MathHelper.floorMod((float)f, 1.0f);
    }

    @Nullable
    private ClientWorld getClientWorld(Entity entity, @Nullable ClientWorld world) {
        if (world == null && entity.world instanceof ClientWorld) {
            return (ClientWorld)entity.world;
        }
        return world;
    }

    private boolean canPointTo(Entity entity, @Nullable GlobalPos pos) {
        return pos != null && pos.getDimension() == entity.world.getRegistryKey() && !(pos.getPos().getSquaredDistance(entity.getPos()) < (double)1.0E-5f);
    }

    private double getAngleTo(Entity entity, BlockPos pos) {
        Vec3d vec3d = Vec3d.ofCenter(pos);
        return Math.atan2(vec3d.getZ() - entity.getZ(), vec3d.getX() - entity.getX()) / 6.2831854820251465;
    }

    private double getBodyYaw(Entity entity) {
        return MathHelper.floorMod((double)(entity.getBodyYaw() / 360.0f), 1.0);
    }

    /**
     * Scatters a seed by integer overflow in multiplication onto the whole
     * int domain.
     */
    private int scatter(int seed) {
        return seed * 1327217883;
    }

    @Environment(value=EnvType.CLIENT)
    static class AngleInterpolator {
        double value;
        private double speed;
        private long lastUpdateTime;

        AngleInterpolator() {
        }

        boolean shouldUpdate(long time) {
            return this.lastUpdateTime != time;
        }

        void update(long time, double target) {
            this.lastUpdateTime = time;
            double d = target - this.value;
            d = MathHelper.floorMod(d + 0.5, 1.0) - 0.5;
            this.speed += d * 0.1;
            this.speed *= 0.8;
            this.value = MathHelper.floorMod(this.value + this.speed, 1.0);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface CompassTarget {
        @Nullable
        public GlobalPos getPos(ClientWorld var1, ItemStack var2, Entity var3);
    }
}

