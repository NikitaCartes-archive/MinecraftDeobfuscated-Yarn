/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class PathNode {
    public final int x;
    public final int y;
    public final int z;
    private final int hashCode;
    public int heapIndex = -1;
    public float penalizedPathLength;
    public float distanceToNearestTarget;
    public float heapWeight;
    @Nullable
    public PathNode previous;
    public boolean visited;
    public float pathLength;
    public float penalty;
    public PathNodeType type = PathNodeType.BLOCKED;

    public PathNode(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.hashCode = PathNode.hash(x, y, z);
    }

    public PathNode copyWithNewPosition(int x, int y, int z) {
        PathNode pathNode = new PathNode(x, y, z);
        pathNode.heapIndex = this.heapIndex;
        pathNode.penalizedPathLength = this.penalizedPathLength;
        pathNode.distanceToNearestTarget = this.distanceToNearestTarget;
        pathNode.heapWeight = this.heapWeight;
        pathNode.previous = this.previous;
        pathNode.visited = this.visited;
        pathNode.pathLength = this.pathLength;
        pathNode.penalty = this.penalty;
        pathNode.type = this.type;
        return pathNode;
    }

    public static int hash(int x, int y, int z) {
        return y & 0xFF | (x & Short.MAX_VALUE) << 8 | (z & Short.MAX_VALUE) << 24 | (x < 0 ? Integer.MIN_VALUE : 0) | (z < 0 ? 32768 : 0);
    }

    public float getDistance(PathNode node) {
        float f = node.x - this.x;
        float g = node.y - this.y;
        float h = node.z - this.z;
        return MathHelper.sqrt(f * f + g * g + h * h);
    }

    public float getHorizontalDistance(PathNode node) {
        float f = node.x - this.x;
        float g = node.z - this.z;
        return MathHelper.sqrt(f * f + g * g);
    }

    public float getDistance(BlockPos pos) {
        float f = pos.getX() - this.x;
        float g = pos.getY() - this.y;
        float h = pos.getZ() - this.z;
        return MathHelper.sqrt(f * f + g * g + h * h);
    }

    public float getSquaredDistance(PathNode node) {
        float f = node.x - this.x;
        float g = node.y - this.y;
        float h = node.z - this.z;
        return f * f + g * g + h * h;
    }

    public float getSquaredDistance(BlockPos pos) {
        float f = pos.getX() - this.x;
        float g = pos.getY() - this.y;
        float h = pos.getZ() - this.z;
        return f * f + g * g + h * h;
    }

    public float getManhattanDistance(PathNode node) {
        float f = Math.abs(node.x - this.x);
        float g = Math.abs(node.y - this.y);
        float h = Math.abs(node.z - this.z);
        return f + g + h;
    }

    public float getManhattanDistance(BlockPos pos) {
        float f = Math.abs(pos.getX() - this.x);
        float g = Math.abs(pos.getY() - this.y);
        float h = Math.abs(pos.getZ() - this.z);
        return f + g + h;
    }

    public BlockPos getBlockPos() {
        return new BlockPos(this.x, this.y, this.z);
    }

    public Vec3d getPos() {
        return new Vec3d(this.x, this.y, this.z);
    }

    public boolean equals(Object o) {
        if (o instanceof PathNode) {
            PathNode pathNode = (PathNode)o;
            return this.hashCode == pathNode.hashCode && this.x == pathNode.x && this.y == pathNode.y && this.z == pathNode.z;
        }
        return false;
    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean isInHeap() {
        return this.heapIndex >= 0;
    }

    public String toString() {
        return "Node{x=" + this.x + ", y=" + this.y + ", z=" + this.z + "}";
    }

    public void toBuffer(PacketByteBuf buffer) {
        buffer.writeInt(this.x);
        buffer.writeInt(this.y);
        buffer.writeInt(this.z);
        buffer.writeFloat(this.pathLength);
        buffer.writeFloat(this.penalty);
        buffer.writeBoolean(this.visited);
        buffer.writeInt(this.type.ordinal());
        buffer.writeFloat(this.heapWeight);
    }

    public static PathNode readBuf(PacketByteBuf buf) {
        PathNode pathNode = new PathNode(buf.readInt(), buf.readInt(), buf.readInt());
        pathNode.pathLength = buf.readFloat();
        pathNode.penalty = buf.readFloat();
        pathNode.visited = buf.readBoolean();
        pathNode.type = PathNodeType.values()[buf.readInt()];
        pathNode.heapWeight = buf.readFloat();
        return pathNode;
    }
}

