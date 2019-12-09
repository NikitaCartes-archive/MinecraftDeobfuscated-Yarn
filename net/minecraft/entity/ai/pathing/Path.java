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

    public Path(List<PathNode> nodes, BlockPos target, boolean reachesTarget) {
        this.nodes = nodes;
        this.target = target;
        this.manhattanDistanceFromTarget = nodes.isEmpty() ? Float.MAX_VALUE : this.nodes.get(this.nodes.size() - 1).getManhattanDistance(this.target);
        this.reachesTarget = reachesTarget;
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

    public PathNode getNode(int index) {
        return this.nodes.get(index);
    }

    public List<PathNode> getNodes() {
        return this.nodes;
    }

    public void setLength(int length) {
        if (this.nodes.size() > length) {
            this.nodes.subList(length, this.nodes.size()).clear();
        }
    }

    public void setNode(int index, PathNode node) {
        this.nodes.set(index, node);
    }

    public int getLength() {
        return this.nodes.size();
    }

    public int getCurrentNodeIndex() {
        return this.currentNodeIndex;
    }

    public void setCurrentNodeIndex(int index) {
        this.currentNodeIndex = index;
    }

    public Vec3d getNodePosition(Entity entity, int index) {
        PathNode pathNode = this.nodes.get(index);
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
    public PathNode[] method_22880() {
        return this.field_57;
    }

    @Environment(value=EnvType.CLIENT)
    public PathNode[] method_22881() {
        return this.field_55;
    }

    @Environment(value=EnvType.CLIENT)
    public static Path fromBuffer(PacketByteBuf buffer) {
        boolean bl = buffer.readBoolean();
        int i = buffer.readInt();
        int j = buffer.readInt();
        HashSet<TargetPathNode> set = Sets.newHashSet();
        for (int k = 0; k < j; ++k) {
            set.add(TargetPathNode.fromBuffer(buffer));
        }
        BlockPos blockPos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
        ArrayList<PathNode> list = Lists.newArrayList();
        int l = buffer.readInt();
        for (int m = 0; m < l; ++m) {
            list.add(PathNode.fromBuffer(buffer));
        }
        PathNode[] pathNodes = new PathNode[buffer.readInt()];
        for (int n = 0; n < pathNodes.length; ++n) {
            pathNodes[n] = PathNode.fromBuffer(buffer);
        }
        PathNode[] pathNodes2 = new PathNode[buffer.readInt()];
        for (int o = 0; o < pathNodes2.length; ++o) {
            pathNodes2[o] = PathNode.fromBuffer(buffer);
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

