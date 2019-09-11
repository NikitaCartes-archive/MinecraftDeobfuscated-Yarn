/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class Path {
    private final List<PathNode> nodes;
    private PathNode[] field_57 = new PathNode[0];
    private PathNode[] field_55 = new PathNode[0];
    @Environment(value=EnvType.CLIENT)
    private Set<TargetPathNode> field_20300;
    private int currentNodeIndex;
    private final BlockPos target;
    private final float manhattanDistanceFromTarget;
    private final boolean reachesTarget;

    public Path(List<PathNode> list, BlockPos blockPos, boolean bl) {
        this.nodes = list;
        this.target = blockPos;
        this.manhattanDistanceFromTarget = list.isEmpty() ? Float.MAX_VALUE : this.nodes.get(this.nodes.size() - 1).getManhattanDistance(this.target);
        this.reachesTarget = bl;
    }

    public void next() {
        ++this.currentNodeIndex;
    }

    public boolean isFinished() {
        return this.currentNodeIndex >= this.nodes.size();
    }

    @Nullable
    public PathNode getEnd() {
        if (!this.nodes.isEmpty()) {
            return this.nodes.get(this.nodes.size() - 1);
        }
        return null;
    }

    public PathNode getNode(int i) {
        return this.nodes.get(i);
    }

    public List<PathNode> getNodes() {
        return this.nodes;
    }

    public void setLength(int i) {
        if (this.nodes.size() > i) {
            this.nodes.subList(i, this.nodes.size()).clear();
        }
    }

    public void setNode(int i, PathNode pathNode) {
        this.nodes.set(i, pathNode);
    }

    public int getLength() {
        return this.nodes.size();
    }

    public int getCurrentNodeIndex() {
        return this.currentNodeIndex;
    }

    public void setCurrentNodeIndex(int i) {
        this.currentNodeIndex = i;
    }

    public Vec3d getNodePosition(Entity entity, int i) {
        PathNode pathNode = this.nodes.get(i);
        double d = (double)pathNode.x + (double)((int)(entity.getWidth() + 1.0f)) * 0.5;
        double e = pathNode.y;
        double f = (double)pathNode.z + (double)((int)(entity.getWidth() + 1.0f)) * 0.5;
        return new Vec3d(d, e, f);
    }

    public Vec3d getNodePosition(Entity entity) {
        return this.getNodePosition(entity, this.currentNodeIndex);
    }

    public Vec3d getCurrentPosition() {
        PathNode pathNode = this.nodes.get(this.currentNodeIndex);
        return new Vec3d(pathNode.x, pathNode.y, pathNode.z);
    }

    public boolean equalsPath(@Nullable Path path) {
        if (path == null) {
            return false;
        }
        if (path.nodes.size() != this.nodes.size()) {
            return false;
        }
        for (int i = 0; i < this.nodes.size(); ++i) {
            PathNode pathNode = this.nodes.get(i);
            PathNode pathNode2 = path.nodes.get(i);
            if (pathNode.x == pathNode2.x && pathNode.y == pathNode2.y && pathNode.z == pathNode2.z) continue;
            return false;
        }
        return true;
    }

    public boolean reachesTarget() {
        return this.reachesTarget;
    }

    @Environment(value=EnvType.CLIENT)
    public PathNode[] method_43() {
        return this.field_57;
    }

    @Environment(value=EnvType.CLIENT)
    public PathNode[] method_37() {
        return this.field_55;
    }

    @Environment(value=EnvType.CLIENT)
    public static Path fromBuffer(PacketByteBuf packetByteBuf) {
        boolean bl = packetByteBuf.readBoolean();
        int i = packetByteBuf.readInt();
        int j = packetByteBuf.readInt();
        HashSet<TargetPathNode> set = Sets.newHashSet();
        for (int k = 0; k < j; ++k) {
            set.add(TargetPathNode.fromBuffer(packetByteBuf));
        }
        BlockPos blockPos = new BlockPos(packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt());
        ArrayList<PathNode> list = Lists.newArrayList();
        int l = packetByteBuf.readInt();
        for (int m = 0; m < l; ++m) {
            list.add(PathNode.fromBuffer(packetByteBuf));
        }
        PathNode[] pathNodes = new PathNode[packetByteBuf.readInt()];
        for (int n = 0; n < pathNodes.length; ++n) {
            pathNodes[n] = PathNode.fromBuffer(packetByteBuf);
        }
        PathNode[] pathNodes2 = new PathNode[packetByteBuf.readInt()];
        for (int o = 0; o < pathNodes2.length; ++o) {
            pathNodes2[o] = PathNode.fromBuffer(packetByteBuf);
        }
        Path path = new Path(list, blockPos, bl);
        path.field_57 = pathNodes;
        path.field_55 = pathNodes2;
        path.field_20300 = set;
        path.currentNodeIndex = i;
        return path;
    }

    public String toString() {
        return "Path(length=" + this.nodes.size() + ")";
    }

    public BlockPos getTarget() {
        return this.target;
    }

    public float getManhattanDistanceFromTarget() {
        return this.manhattanDistanceFromTarget;
    }
}

