/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class TrackedPosition {
    private static final double COORDINATE_SCALE = 4096.0;
    private Vec3d pos = Vec3d.ZERO;

    private static long pack(double value) {
        return MathHelper.lfloor(value * 4096.0);
    }

    private static double unpack(long value) {
        return (double)value / 4096.0;
    }

    public Vec3d withDelta(long x, long y, long z) {
        if (x == 0L && y == 0L && z == 0L) {
            return this.pos;
        }
        double d = x == 0L ? this.pos.x : TrackedPosition.unpack(TrackedPosition.pack(this.pos.x) + x);
        double e = y == 0L ? this.pos.y : TrackedPosition.unpack(TrackedPosition.pack(this.pos.y) + y);
        double f = z == 0L ? this.pos.z : TrackedPosition.unpack(TrackedPosition.pack(this.pos.z) + z);
        return new Vec3d(d, e, f);
    }

    public long getDeltaX(Vec3d pos) {
        return TrackedPosition.pack(pos.x - this.pos.x);
    }

    public long getDeltaY(Vec3d pos) {
        return TrackedPosition.pack(pos.y - this.pos.y);
    }

    public long getDeltaZ(Vec3d pos) {
        return TrackedPosition.pack(pos.z - this.pos.z);
    }

    public Vec3d subtract(Vec3d pos) {
        return pos.subtract(this.pos);
    }

    public void setPos(Vec3d pos) {
        this.pos = pos;
    }
}

