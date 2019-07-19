/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.goal.ChaseBoatState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ChaseBoatGoal
extends Goal {
    private int field_6428;
    private final MobEntityWithAi mob;
    private LivingEntity passenger;
    private ChaseBoatState state;

    public ChaseBoatGoal(MobEntityWithAi mobEntityWithAi) {
        this.mob = mobEntityWithAi;
    }

    @Override
    public boolean canStart() {
        List<BoatEntity> list = this.mob.world.getNonSpectatingEntities(BoatEntity.class, this.mob.getBoundingBox().expand(5.0));
        boolean bl = false;
        for (BoatEntity boatEntity : list) {
            if (boatEntity.getPrimaryPassenger() == null || !(MathHelper.abs(((LivingEntity)boatEntity.getPrimaryPassenger()).sidewaysSpeed) > 0.0f) && !(MathHelper.abs(((LivingEntity)boatEntity.getPrimaryPassenger()).forwardSpeed) > 0.0f)) continue;
            bl = true;
            break;
        }
        return this.passenger != null && (MathHelper.abs(this.passenger.sidewaysSpeed) > 0.0f || MathHelper.abs(this.passenger.forwardSpeed) > 0.0f) || bl;
    }

    @Override
    public boolean canStop() {
        return true;
    }

    @Override
    public boolean shouldContinue() {
        return this.passenger != null && this.passenger.hasVehicle() && (MathHelper.abs(this.passenger.sidewaysSpeed) > 0.0f || MathHelper.abs(this.passenger.forwardSpeed) > 0.0f);
    }

    @Override
    public void start() {
        List<BoatEntity> list = this.mob.world.getNonSpectatingEntities(BoatEntity.class, this.mob.getBoundingBox().expand(5.0));
        for (BoatEntity boatEntity : list) {
            if (boatEntity.getPrimaryPassenger() == null || !(boatEntity.getPrimaryPassenger() instanceof LivingEntity)) continue;
            this.passenger = (LivingEntity)boatEntity.getPrimaryPassenger();
            break;
        }
        this.field_6428 = 0;
        this.state = ChaseBoatState.GO_TO_BOAT;
    }

    @Override
    public void stop() {
        this.passenger = null;
    }

    @Override
    public void tick() {
        boolean bl;
        boolean bl2 = bl = MathHelper.abs(this.passenger.sidewaysSpeed) > 0.0f || MathHelper.abs(this.passenger.forwardSpeed) > 0.0f;
        float f = this.state == ChaseBoatState.GO_IN_BOAT_DIRECTION ? (bl ? 0.17999999f : 0.0f) : 0.135f;
        this.mob.updateVelocity(f, new Vec3d(this.mob.sidewaysSpeed, this.mob.upwardSpeed, this.mob.forwardSpeed));
        this.mob.move(MovementType.SELF, this.mob.getVelocity());
        if (--this.field_6428 > 0) {
            return;
        }
        this.field_6428 = 10;
        if (this.state == ChaseBoatState.GO_TO_BOAT) {
            BlockPos blockPos = new BlockPos(this.passenger).offset(this.passenger.getHorizontalFacing().getOpposite());
            blockPos = blockPos.add(0, -1, 0);
            this.mob.getNavigation().startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0);
            if (this.mob.distanceTo(this.passenger) < 4.0f) {
                this.field_6428 = 0;
                this.state = ChaseBoatState.GO_IN_BOAT_DIRECTION;
            }
        } else if (this.state == ChaseBoatState.GO_IN_BOAT_DIRECTION) {
            Direction direction = this.passenger.getMovementDirection();
            BlockPos blockPos2 = new BlockPos(this.passenger).offset(direction, 10);
            this.mob.getNavigation().startMovingTo(blockPos2.getX(), blockPos2.getY() - 1, blockPos2.getZ(), 1.0);
            if (this.mob.distanceTo(this.passenger) > 12.0f) {
                this.field_6428 = 0;
                this.state = ChaseBoatState.GO_TO_BOAT;
            }
        }
    }
}

