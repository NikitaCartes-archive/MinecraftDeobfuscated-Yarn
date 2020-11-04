/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameRules;

public class RevengeGoal
extends TrackTargetGoal {
    private static final TargetPredicate VALID_AVOIDABLES_PREDICATE = new TargetPredicate().includeHidden().ignoreDistanceScalingFactor();
    private boolean groupRevenge;
    private int lastAttackedTime;
    private final Class<?>[] noRevengeTypes;
    private Class<?>[] noHelpTypes;

    public RevengeGoal(PathAwareEntity mob, Class<?> ... noRevengeTypes) {
        super(mob, true);
        this.noRevengeTypes = noRevengeTypes;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    public boolean canStart() {
        int i = this.mob.getLastAttackedTime();
        LivingEntity livingEntity = this.mob.getAttacker();
        if (i == this.lastAttackedTime || livingEntity == null) {
            return false;
        }
        if (livingEntity.getType() == EntityType.PLAYER && this.mob.world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER)) {
            return false;
        }
        for (Class<?> class_ : this.noRevengeTypes) {
            if (!class_.isAssignableFrom(livingEntity.getClass())) continue;
            return false;
        }
        return this.canTrack(livingEntity, VALID_AVOIDABLES_PREDICATE);
    }

    public RevengeGoal setGroupRevenge(Class<?> ... noHelpTypes) {
        this.groupRevenge = true;
        this.noHelpTypes = noHelpTypes;
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
        Box box = Box.method_29968(this.mob.getPos()).expand(d, 10.0, d);
        List<Entity> list = this.mob.world.getEntitiesByClass(this.mob.getClass(), box, EntityPredicates.EXCEPT_SPECTATOR);
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

    protected void setMobEntityTarget(MobEntity mob, LivingEntity target) {
        mob.setTarget(target);
    }
}

