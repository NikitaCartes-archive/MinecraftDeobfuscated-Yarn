/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;
import org.jetbrains.annotations.Nullable;

public class HoldingPatternPhase
extends AbstractPhase {
    private static final TargetPredicate PLAYERS_IN_RANGE_PREDICATE = new TargetPredicate().setBaseMaxDistance(64.0);
    private Path field_7043;
    private Vec3d field_7045;
    private boolean field_7044;

    public HoldingPatternPhase(EnderDragonEntity enderDragonEntity) {
        super(enderDragonEntity);
    }

    public PhaseType<HoldingPatternPhase> getType() {
        return PhaseType.HOLDING_PATTERN;
    }

    @Override
    public void serverTick() {
        double d;
        double d2 = d = this.field_7045 == null ? 0.0 : this.field_7045.squaredDistanceTo(this.dragon.x, this.dragon.y, this.dragon.z);
        if (d < 100.0 || d > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            this.method_6841();
        }
    }

    @Override
    public void beginPhase() {
        this.field_7043 = null;
        this.field_7045 = null;
    }

    @Override
    @Nullable
    public Vec3d getTarget() {
        return this.field_7045;
    }

    private void method_6841() {
        int i;
        if (this.field_7043 != null && this.field_7043.isFinished()) {
            BlockPos blockPos = this.dragon.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(EndPortalFeature.ORIGIN));
            int n = i = this.dragon.getFight() == null ? 0 : this.dragon.getFight().getAliveEndCrystals();
            if (this.dragon.getRandom().nextInt(i + 3) == 0) {
                this.dragon.getPhaseManager().setPhase(PhaseType.LANDING_APPROACH);
                return;
            }
            double d = 64.0;
            PlayerEntity playerEntity = this.dragon.world.getClosestPlayer(PLAYERS_IN_RANGE_PREDICATE, blockPos.getX(), blockPos.getY(), blockPos.getZ());
            if (playerEntity != null) {
                d = blockPos.getSquaredDistance(playerEntity.getPos(), true) / 512.0;
            }
            if (!(playerEntity == null || playerEntity.abilities.invulnerable || this.dragon.getRandom().nextInt(MathHelper.abs((int)d) + 2) != 0 && this.dragon.getRandom().nextInt(i + 2) != 0)) {
                this.method_6843(playerEntity);
                return;
            }
        }
        if (this.field_7043 == null || this.field_7043.isFinished()) {
            int j;
            i = j = this.dragon.method_6818();
            if (this.dragon.getRandom().nextInt(8) == 0) {
                this.field_7044 = !this.field_7044;
                i += 6;
            }
            i = this.field_7044 ? ++i : --i;
            if (this.dragon.getFight() == null || this.dragon.getFight().getAliveEndCrystals() < 0) {
                i -= 12;
                i &= 7;
                i += 12;
            } else if ((i %= 12) < 0) {
                i += 12;
            }
            this.field_7043 = this.dragon.method_6833(j, i, null);
            if (this.field_7043 != null) {
                this.field_7043.next();
            }
        }
        this.method_6842();
    }

    private void method_6843(PlayerEntity playerEntity) {
        this.dragon.getPhaseManager().setPhase(PhaseType.STRAFE_PLAYER);
        this.dragon.getPhaseManager().create(PhaseType.STRAFE_PLAYER).method_6862(playerEntity);
    }

    private void method_6842() {
        if (this.field_7043 != null && !this.field_7043.isFinished()) {
            double f;
            Vec3d vec3d = this.field_7043.getCurrentPosition();
            this.field_7043.next();
            double d = vec3d.x;
            double e = vec3d.z;
            while ((f = vec3d.y + (double)(this.dragon.getRandom().nextFloat() * 20.0f)) < vec3d.y) {
            }
            this.field_7045 = new Vec3d(d, f, e);
        }
    }

    @Override
    public void crystalDestroyed(EnderCrystalEntity enderCrystalEntity, BlockPos blockPos, DamageSource damageSource, @Nullable PlayerEntity playerEntity) {
        if (playerEntity != null && !playerEntity.abilities.invulnerable) {
            this.method_6843(playerEntity);
        }
    }
}

