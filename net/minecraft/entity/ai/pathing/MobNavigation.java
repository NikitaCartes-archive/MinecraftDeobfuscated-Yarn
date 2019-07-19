/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.pathing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MobNavigation
extends EntityNavigation {
    private boolean avoidSunlight;

    public MobNavigation(MobEntity mobEntity, World world) {
        super(mobEntity, world);
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int i) {
        this.nodeMaker = new LandPathNodeMaker();
        this.nodeMaker.setCanEnterOpenDoors(true);
        return new PathNodeNavigator(this.nodeMaker, i);
    }

    @Override
    protected boolean isAtValidPosition() {
        return this.entity.onGround || this.isInLiquid() || this.entity.hasVehicle();
    }

    @Override
    protected Vec3d getPos() {
        return new Vec3d(this.entity.x, this.method_6362(), this.entity.z);
    }

    @Override
    public Path findPathTo(BlockPos blockPos, int i) {
        BlockPos blockPos2;
        if (this.world.getBlockState(blockPos).isAir()) {
            blockPos2 = blockPos.down();
            while (blockPos2.getY() > 0 && this.world.getBlockState(blockPos2).isAir()) {
                blockPos2 = blockPos2.down();
            }
            if (blockPos2.getY() > 0) {
                return super.findPathTo(blockPos2.up(), i);
            }
            while (blockPos2.getY() < this.world.getHeight() && this.world.getBlockState(blockPos2).isAir()) {
                blockPos2 = blockPos2.up();
            }
            blockPos = blockPos2;
        }
        if (this.world.getBlockState(blockPos).getMaterial().isSolid()) {
            blockPos2 = blockPos.up();
            while (blockPos2.getY() < this.world.getHeight() && this.world.getBlockState(blockPos2).getMaterial().isSolid()) {
                blockPos2 = blockPos2.up();
            }
            return super.findPathTo(blockPos2, i);
        }
        return super.findPathTo(blockPos, i);
    }

    @Override
    public Path findPathTo(Entity entity, int i) {
        return this.findPathTo(new BlockPos(entity), i);
    }

    private int method_6362() {
        if (!this.entity.isTouchingWater() || !this.canSwim()) {
            return MathHelper.floor(this.entity.getBoundingBox().y1 + 0.5);
        }
        int i = MathHelper.floor(this.entity.getBoundingBox().y1);
        Block block = this.world.getBlockState(new BlockPos(this.entity.x, (double)i, this.entity.z)).getBlock();
        int j = 0;
        while (block == Blocks.WATER) {
            block = this.world.getBlockState(new BlockPos(this.entity.x, (double)(++i), this.entity.z)).getBlock();
            if (++j <= 16) continue;
            return MathHelper.floor(this.entity.getBoundingBox().y1);
        }
        return i;
    }

    @Override
    protected void method_6359() {
        super.method_6359();
        if (this.avoidSunlight) {
            if (this.world.isSkyVisible(new BlockPos(this.entity.x, this.entity.getBoundingBox().y1 + 0.5, this.entity.z))) {
                return;
            }
            for (int i = 0; i < this.currentPath.getLength(); ++i) {
                PathNode pathNode = this.currentPath.getNode(i);
                if (!this.world.isSkyVisible(new BlockPos(pathNode.x, pathNode.y, pathNode.z))) continue;
                this.currentPath.setLength(i);
                return;
            }
        }
    }

    @Override
    protected boolean canPathDirectlyThrough(Vec3d vec3d, Vec3d vec3d2, int i, int j, int k) {
        int l = MathHelper.floor(vec3d.x);
        int m = MathHelper.floor(vec3d.z);
        double d = vec3d2.x - vec3d.x;
        double e = vec3d2.z - vec3d.z;
        double f = d * d + e * e;
        if (f < 1.0E-8) {
            return false;
        }
        double g = 1.0 / Math.sqrt(f);
        if (!this.method_6364(l, MathHelper.floor(vec3d.y), m, i += 2, j, k += 2, vec3d, d *= g, e *= g)) {
            return false;
        }
        i -= 2;
        k -= 2;
        double h = 1.0 / Math.abs(d);
        double n = 1.0 / Math.abs(e);
        double o = (double)l - vec3d.x;
        double p = (double)m - vec3d.z;
        if (d >= 0.0) {
            o += 1.0;
        }
        if (e >= 0.0) {
            p += 1.0;
        }
        o /= d;
        p /= e;
        int q = d < 0.0 ? -1 : 1;
        int r = e < 0.0 ? -1 : 1;
        int s = MathHelper.floor(vec3d2.x);
        int t = MathHelper.floor(vec3d2.z);
        int u = s - l;
        int v = t - m;
        while (u * q > 0 || v * r > 0) {
            if (o < p) {
                o += h;
                u = s - (l += q);
            } else {
                p += n;
                v = t - (m += r);
            }
            if (this.method_6364(l, MathHelper.floor(vec3d.y), m, i, j, k, vec3d, d, e)) continue;
            return false;
        }
        return true;
    }

    private boolean method_6364(int i, int j, int k, int l, int m, int n, Vec3d vec3d, double d, double e) {
        int o = i - l / 2;
        int p = k - n / 2;
        if (!this.method_6367(o, j, p, l, m, n, vec3d, d, e)) {
            return false;
        }
        for (int q = o; q < o + l; ++q) {
            for (int r = p; r < p + n; ++r) {
                double f = (double)q + 0.5 - vec3d.x;
                double g = (double)r + 0.5 - vec3d.z;
                if (f * d + g * e < 0.0) continue;
                PathNodeType pathNodeType = this.nodeMaker.getNodeType(this.world, q, j - 1, r, this.entity, l, m, n, true, true);
                if (pathNodeType == PathNodeType.WATER) {
                    return false;
                }
                if (pathNodeType == PathNodeType.LAVA) {
                    return false;
                }
                if (pathNodeType == PathNodeType.OPEN) {
                    return false;
                }
                pathNodeType = this.nodeMaker.getNodeType(this.world, q, j, r, this.entity, l, m, n, true, true);
                float h = this.entity.getPathfindingPenalty(pathNodeType);
                if (h < 0.0f || h >= 8.0f) {
                    return false;
                }
                if (pathNodeType != PathNodeType.DAMAGE_FIRE && pathNodeType != PathNodeType.DANGER_FIRE && pathNodeType != PathNodeType.DAMAGE_OTHER) continue;
                return false;
            }
        }
        return true;
    }

    private boolean method_6367(int i, int j, int k, int l, int m, int n, Vec3d vec3d, double d, double e) {
        for (BlockPos blockPos : BlockPos.iterate(new BlockPos(i, j, k), new BlockPos(i + l - 1, j + m - 1, k + n - 1))) {
            double g;
            double f = (double)blockPos.getX() + 0.5 - vec3d.x;
            if (f * d + (g = (double)blockPos.getZ() + 0.5 - vec3d.z) * e < 0.0 || this.world.getBlockState(blockPos).canPlaceAtSide(this.world, blockPos, BlockPlacementEnvironment.LAND)) continue;
            return false;
        }
        return true;
    }

    public void setCanPathThroughDoors(boolean bl) {
        this.nodeMaker.setCanOpenDoors(bl);
    }

    public boolean canEnterOpenDoors() {
        return this.nodeMaker.canEnterOpenDoors();
    }

    public void setAvoidSunlight(boolean bl) {
        this.avoidSunlight = bl;
    }
}

