/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.Box;

public class RevengeGoal
extends TrackTargetGoal {
    private static final TargetPredicate VALID_AVOIDABLES_PREDICATE = new TargetPredicate().includeHidden().ignoreDistanceScalingFactor();
    private boolean groupRevenge;
    private int lastAttackedTime;
    private final Class<?>[] noRevengeTypes;
    private Class<?>[] noHelpTypes;

    public RevengeGoal(MobEntityWithAi mobEntityWithAi, Class<?> ... classs) {
        super(mobEntityWithAi, true);
        this.noRevengeTypes = classs;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    public boolean canStart() {
        int i = this.mob.getLastAttackedTime();
        LivingEntity livingEntity = this.mob.getAttacker();
        if (i == this.lastAttackedTime || livingEntity == null) {
            return false;
        }
        for (Class<?> class_ : this.noRevengeTypes) {
            if (!class_.isAssignableFrom(livingEntity.getClass())) continue;
            return false;
        }
        return this.canTrack(livingEntity, VALID_AVOIDABLES_PREDICATE);
    }

    public RevengeGoal setGroupRevenge(Class<?> ... classs) {
        this.groupRevenge = true;
        this.noHelpTypes = classs;
        return this;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.mob.getAttacker());
        this.target = this.mob.getTarget();
        this.lastAttackedTime = this.mob.getLastAttackedTime();
        this.maxTimeWithoutVisibility = 300;
        if (this.groupRevenge) {
            this.callSameTypeForRevenge();
        }
        super.start();
    }

    protected void callSameTypeForRevenge() {
        double d = this.getFollowRange();
        List<?> list = this.mob.world.method_21728(this.mob.getClass(), new Box(this.mob.x, this.mob.y, this.mob.z, this.mob.x + 1.0, this.mob.y + 1.0, this.mob.z + 1.0).expand(d, 10.0, d));
        for (MobEntity mobEntity : list) {
            if (this.mob == mobEntity || mobEntity.getTarget() != null || this.mob instanceof TameableEntity && ((TameableEntity)this.mob).getOwner() != ((TameableEntity)mobEntity).getOwner() || mobEntity.isTeammate(this.mob.getAttacker())) continue;
            if (this.noHelpTypes != null) {
                boolean bl = false;
                for (Class<?> class_ : this.noHelpTypes) {
                    if (mobEntity.getClass() != class_) continue;
                    bl = true;
                    break;
                }
                if (bl) continue;
            }
            this.setMobEntityTarget(mobEntity, this.mob.getAttacker());
        }
    }

    protected void setMobEntityTarget(MobEntity mobEntity, LivingEntity livingEntity) {
        mobEntity.setTarget(livingEntity);
    }
}

