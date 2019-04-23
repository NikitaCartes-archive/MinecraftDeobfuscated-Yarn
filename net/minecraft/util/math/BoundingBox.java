/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class BoundingBox {
    public final double minX;
    public final double minY;
    public final double minZ;
    public final double maxX;
    public final double maxY;
    public final double maxZ;

    public BoundingBox(double d, double e, double f, double g, double h, double i) {
        this.minX = Math.min(d, g);
        this.minY = Math.min(e, h);
        this.minZ = Math.min(f, i);
        this.maxX = Math.max(d, g);
        this.maxY = Math.max(e, h);
        this.maxZ = Math.max(f, i);
    }

    public BoundingBox(BlockPos blockPos) {
        this(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1);
    }

    public BoundingBox(BlockPos blockPos, BlockPos blockPos2) {
        this(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
    }

    public BoundingBox(Vec3d vec3d, Vec3d vec3d2) {
        this(vec3d.x, vec3d.y, vec3d.z, vec3d2.x, vec3d2.y, vec3d2.z);
    }

    public static BoundingBox from(MutableIntBoundingBox mutableIntBoundingBox) {
        return new BoundingBox(mutableIntBoundingBox.minX, mutableIntBoundingBox.minY, mutableIntBoundingBox.minZ, mutableIntBoundingBox.maxX + 1, mutableIntBoundingBox.maxY + 1, mutableIntBoundingBox.maxZ + 1);
    }

    public double getMin(Direction.Axis axis) {
        return axis.choose(this.minX, this.minY, this.minZ);
    }

    public double getMax(Direction.Axis axis) {
        return axis.choose(this.maxX, this.maxY, this.maxZ);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof BoundingBox)) {
            return false;
        }
        BoundingBox boundingBox = (BoundingBox)object;
        if (Double.compare(boundingBox.minX, this.minX) != 0) {
            return false;
        }
        if (Double.compare(boundingBox.minY, this.minY) != 0) {
            return false;
        }
        if (Double.compare(boundingBox.minZ, this.minZ) != 0) {
            return false;
        }
        if (Double.compare(boundingBox.maxX, this.maxX) != 0) {
            return false;
        }
        if (Double.compare(boundingBox.maxY, this.maxY) != 0) {
            return false;
        }
        return Double.compare(boundingBox.maxZ, this.maxZ) == 0;
    }

    public int hashCode() {
        long l = Double.doubleToLongBits(this.minX);
        int i = (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.minY);
        i = 31 * i + (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.minZ);
        i = 31 * i + (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.maxX);
        i = 31 * i + (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.maxY);
        i = 31 * i + (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.maxZ);
        i = 31 * i + (int)(l ^ l >>> 32);
        return i;
    }

    public BoundingBox shrink(double d, double e, double f) {
        double g = this.minX;
        double h = this.minY;
        double i = this.minZ;
        double j = this.maxX;
        double k = this.maxY;
        double l = this.maxZ;
        if (d < 0.0) {
            g -= d;
        } else if (d > 0.0) {
            j -= d;
        }
        if (e < 0.0) {
            h -= e;
        } else if (e > 0.0) {
            k -= e;
        }
        if (f < 0.0) {
            i -= f;
        } else if (f > 0.0) {
            l -= f;
        }
        return new BoundingBox(g, h, i, j, k, l);
    }

    public BoundingBox stretch(Vec3d vec3d) {
        return this.stretch(vec3d.x, vec3d.y, vec3d.z);
    }

    public BoundingBox stretch(double d, double e, double f) {
        double g = this.minX;
        double h = this.minY;
        double i = this.minZ;
        double j = this.maxX;
        double k = this.maxY;
        double l = this.maxZ;
        if (d < 0.0) {
            g += d;
        } else if (d > 0.0) {
            j += d;
        }
        if (e < 0.0) {
            h += e;
        } else if (e > 0.0) {
            k += e;
        }
        if (f < 0.0) {
            i += f;
        } else if (f > 0.0) {
            l += f;
        }
        return new BoundingBox(g, h, i, j, k, l);
    }

    public BoundingBox expand(double d, double e, double f) {
        double g = this.minX - d;
        double h = this.minY - e;
        double i = this.minZ - f;
        double j = this.maxX + d;
        double k = this.maxY + e;
        double l = this.maxZ + f;
        return new BoundingBox(g, h, i, j, k, l);
    }

    public BoundingBox expand(double d) {
        return this.expand(d, d, d);
    }

    public BoundingBox intersection(BoundingBox boundingBox) {
        double d = Math.max(this.minX, boundingBox.minX);
        double e = Math.max(this.minY, boundingBox.minY);
        double f = Math.max(this.minZ, boundingBox.minZ);
        double g = Math.min(this.maxX, boundingBox.maxX);
        double h = Math.min(this.maxY, boundingBox.maxY);
        double i = Math.min(this.maxZ, boundingBox.maxZ);
        return new BoundingBox(d, e, f, g, h, i);
    }

    public BoundingBox union(BoundingBox boundingBox) {
        double d = Math.min(this.minX, boundingBox.minX);
        double e = Math.min(this.minY, boundingBox.minY);
        double f = Math.min(this.minZ, boundingBox.minZ);
        double g = Math.max(this.maxX, boundingBox.maxX);
        double h = Math.max(this.maxY, boundingBox.maxY);
        double i = Math.max(this.maxZ, boundingBox.maxZ);
        return new BoundingBox(d, e, f, g, h, i);
    }

    public BoundingBox offset(double d, double e, double f) {
        return new BoundingBox(this.minX + d, this.minY + e, this.minZ + f, this.maxX + d, this.maxY + e, this.maxZ + f);
    }

    public BoundingBox offset(BlockPos blockPos) {
        return new BoundingBox(this.minX + (double)blockPos.getX(), this.minY + (double)blockPos.getY(), this.minZ + (double)blockPos.getZ(), this.maxX + (double)blockPos.getX(), this.maxY + (double)blockPos.getY(), this.maxZ + (double)blockPos.getZ());
    }

    public BoundingBox offset(Vec3d vec3d) {
        return this.offset(vec3d.x, vec3d.y, vec3d.z);
    }

    public boolean intersects(BoundingBox boundingBox) {
        return this.intersects(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
    }

    public boolean intersects(double d, double e, double f, double g, double h, double i) {
        return this.minX < g && this.maxX > d && this.minY < h && this.maxY > e && this.minZ < i && this.maxZ > f;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean intersects(Vec3d vec3d, Vec3d vec3d2) {
        return this.intersects(Math.min(vec3d.x, vec3d2.x), Math.min(vec3d.y, vec3d2.y), Math.min(vec3d.z, vec3d2.z), Math.max(vec3d.x, vec3d2.x), Math.max(vec3d.y, vec3d2.y), Math.max(vec3d.z, vec3d2.z));
    }

    public boolean contains(Vec3d vec3d) {
        return this.contains(vec3d.x, vec3d.y, vec3d.z);
    }

    public boolean contains(double d, double e, double f) {
        return d >= this.minX && d < this.maxX && e >= this.minY && e < this.maxY && f >= this.minZ && f < this.maxZ;
    }

    public double averageDimension() {
        double d = this.getXSize();
        double e = this.getYSize();
        double f = this.getZSize();
        return (d + e + f) / 3.0;
    }

    public double getXSize() {
        return this.maxX - this.minX;
    }

    public double getYSize() {
        return this.maxY - this.minY;
    }

    public double getZSize() {
        return this.maxZ - this.minZ;
    }

    public BoundingBox contract(double d) {
        return this.expand(-d);
    }

    public Optional<Vec3d> rayTrace(Vec3d vec3d, Vec3d vec3d2) {
        double[] ds = new double[]{1.0};
        double d = vec3d2.x - vec3d.x;
        double e = vec3d2.y - vec3d.y;
        double f = vec3d2.z - vec3d.z;
        Direction direction = BoundingBox.method_1007(this, vec3d, ds, null, d, e, f);
        if (direction == null) {
            return Optional.empty();
        }
        double g = ds[0];
        return Optional.of(vec3d.add(g * d, g * e, g * f));
    }

    @Nullable
    public static BlockHitResult rayTrace(Iterable<BoundingBox> iterable, Vec3d vec3d, Vec3d vec3d2, BlockPos blockPos) {
        double[] ds = new double[]{1.0};
        Direction direction = null;
        double d = vec3d2.x - vec3d.x;
        double e = vec3d2.y - vec3d.y;
        double f = vec3d2.z - vec3d.z;
        for (BoundingBox boundingBox : iterable) {
            direction = BoundingBox.method_1007(boundingBox.offset(blockPos), vec3d, ds, direction, d, e, f);
        }
        if (direction == null) {
            return null;
        }
        double g = ds[0];
        return new BlockHitResult(vec3d.add(g * d, g * e, g * f), direction, blockPos, false);
    }

    @Nullable
    private static Direction method_1007(BoundingBox boundingBox, Vec3d vec3d, double[] ds, @Nullable Direction direction, double d, double e, double f) {
        if (d > 1.0E-7) {
            direction = BoundingBox.method_998(ds, direction, d, e, f, boundingBox.minX, boundingBox.minY, boundingBox.maxY, boundingBox.minZ, boundingBox.maxZ, Direction.WEST, vec3d.x, vec3d.y, vec3d.z);
        } else if (d < -1.0E-7) {
            direction = BoundingBox.method_998(ds, direction, d, e, f, boundingBox.maxX, boundingBox.minY, boundingBox.maxY, boundingBox.minZ, boundingBox.maxZ, Direction.EAST, vec3d.x, vec3d.y, vec3d.z);
        }
        if (e > 1.0E-7) {
            direction = BoundingBox.method_998(ds, direction, e, f, d, boundingBox.minY, boundingBox.minZ, boundingBox.maxZ, boundingBox.minX, boundingBox.maxX, Direction.DOWN, vec3d.y, vec3d.z, vec3d.x);
        } else if (e < -1.0E-7) {
            direction = BoundingBox.method_998(ds, direction, e, f, d, boundingBox.maxY, boundingBox.minZ, boundingBox.maxZ, boundingBox.minX, boundingBox.maxX, Direction.UP, vec3d.y, vec3d.z, vec3d.x);
        }
        if (f > 1.0E-7) {
            direction = BoundingBox.method_998(ds, direction, f, d, e, boundingBox.minZ, boundingBox.minX, boundingBox.maxX, boundingBox.minY, boundingBox.maxY, Direction.NORTH, vec3d.z, vec3d.x, vec3d.y);
        } else if (f < -1.0E-7) {
            direction = BoundingBox.method_998(ds, direction, f, d, e, boundingBox.maxZ, boundingBox.minX, boundingBox.maxX, boundingBox.minY, boundingBox.maxY, Direction.SOUTH, vec3d.z, vec3d.x, vec3d.y);
        }
        return direction;
    }

    @Nullable
    private static Direction method_998(double[] ds, @Nullable Direction direction, double d, double e, double f, double g, double h, double i, double j, double k, Direction direction2, double l, double m, double n) {
        double o = (g - l) / d;
        double p = m + o * e;
        double q = n + o * f;
        if (0.0 < o && o < ds[0] && h - 1.0E-7 < p && p < i + 1.0E-7 && j - 1.0E-7 < q && q < k + 1.0E-7) {
            ds[0] = o;
            return direction2;
        }
        return direction;
    }

    public String toString() {
        return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + "] -> [" + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isValid() {
        return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX) || Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
    }

    public Vec3d getCenter() {
        return new Vec3d(MathHelper.lerp(0.5, this.minX, this.maxX), MathHelper.lerp(0.5, this.minY, this.maxY), MathHelper.lerp(0.5, this.minZ, this.maxZ));
    }
}

