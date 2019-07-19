/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.BirdPathNodeMaker;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BirdNavigation
extends EntityNavigation {
    public BirdNavigation(MobEntity mobEntity, World world) {
        super(mobEntity, world);
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int i) {
        this.nodeMaker = new BirdPathNodeMaker();
        this.nodeMaker.setCanEnterOpenDoors(true);
        return new PathNodeNavigator(this.nodeMaker, i);
    }

    @Override
    protected boolean isAtValidPosition() {
        return this.canSwim() && this.isInLiquid() || !this.entity.hasVehicle();
    }

    @Override
    protected Vec3d getPos() {
        return new Vec3d(this.entity.x, this.entity.y, this.entity.z);
    }

    @Override
    public Path findPathTo(Entity entity, int i) {
        return this.findPathTo(new BlockPos(entity), i);
    }

    @Override
    public void tick() {
        Vec3d vec3d;
        ++this.tickCount;
        if (this.shouldRecalculate) {
            this.recalculatePath();
        }
        if (this.isIdle()) {
            return;
        }
        if (this.isAtValidPosition()) {
            this.method_6339();
        } else if (this.currentPath != null && this.currentPath.getCurrentNodeIndex() < this.currentPath.getLength()) {
            vec3d = this.currentPath.getNodePosition(this.entity, this.currentPath.getCurrentNodeIndex());
            if (MathHelper.floor(this.entity.x) == MathHelper.floor(vec3d.x) && MathHelper.floor(this.entity.y) == MathHelper.floor(vec3d.y) && MathHelper.floor(this.entity.z) == MathHelper.floor(vec3d.z)) {
                this.currentPath.setCurrentNodeIndex(this.currentPath.getCurrentNodeIndex() + 1);
            }
        }
        DebugInfoSender.sendPathfindingData(this.world, this.entity, this.currentPath, this.field_6683);
        if (this.isIdle()) {
            return;
        }
        vec3d = this.currentPath.getNodePosition(this.entity);
        this.entity.getMoveControl().moveTo(vec3d.x, vec3d.y, vec3d.z, this.speed);
    }

    @Override
    protected boolean canPathDirectlyThrough(Vec3d vec3d, Vec3d vec3d2, int i, int j, int k) {
        int l = MathHelper.floor(vec3d.x);
        int m = MathHelper.floor(vec3d.y);
        int n = MathHelper.floor(vec3d.z);
        double d = vec3d2.x - vec3d.x;
        double e = vec3d2.y - vec3d.y;
        double f = vec3d2.z - vec3d.z;
        double g = d * d + e * e + f * f;
        if (g < 1.0E-8) {
            return false;
        }
        double h = 1.0 / Math.sqrt(g);
        double o = 1.0 / Math.abs(d *= h);
        double p = 1.0 / Math.abs(e *= h);
        double q = 1.0 / Math.abs(f *= h);
        double r = (double)l - vec3d.x;
        double s = (double)m - vec3d.y;
        double t = (double)n - vec3d.z;
        if (d >= 0.0) {
            r += 1.0;
        }
        if (e >= 0.0) {
            s += 1.0;
        }
        if (f >= 0.0) {
            t += 1.0;
        }
        r /= d;
        s /= e;
        t /= f;
        int u = d < 0.0 ? -1 : 1;
        int v = e < 0.0 ? -1 : 1;
        int w = f < 0.0 ? -1 : 1;
        int x = MathHelper.floor(vec3d2.x);
        int y = MathHelper.floor(vec3d2.y);
        int z = MathHelper.floor(vec3d2.z);
        int aa = x - l;
        int ab = y - m;
        int ac = z - n;
        while (aa * u > 0 || ab * v > 0 || ac * w > 0) {
            if (r < t && r <= s) {
                r += o;
                aa = x - (l += u);
                continue;
            }
            if (s < r && s <= t) {
                s += p;
                ab = y - (m += v);
                continue;
            }
            t += q;
            ac = z - (n += w);
        }
        return true;
    }

    public void setCanPathThroughDoors(boolean bl) {
        this.nodeMaker.setCanOpenDoors(bl);
    }

    public void setCanEnterOpenDoors(boolean bl) {
        this.nodeMaker.setCanEnterOpenDoors(bl);
    }

    @Override
    public boolean isValidPosition(BlockPos blockPos) {
        return this.world.getBlockState(blockPos).hasSolidTopSurface(this.world, blockPos, this.entity);
    }
}

