/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.pathing;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;

public abstract class PathNodeMaker {
    protected ViewableWorld blockView;
    protected MobEntity entity;
    protected final Int2ObjectMap<PathNode> pathNodeCache = new Int2ObjectOpenHashMap<PathNode>();
    protected int field_31;
    protected int field_30;
    protected int field_28;
    protected boolean entersOpenDoors;
    protected boolean pathsThroughDoors;
    protected boolean swims;

    public void init(ViewableWorld viewableWorld, MobEntity mobEntity) {
        this.blockView = viewableWorld;
        this.entity = mobEntity;
        this.pathNodeCache.clear();
        this.field_31 = MathHelper.floor(mobEntity.getWidth() + 1.0f);
        this.field_30 = MathHelper.floor(mobEntity.getHeight() + 1.0f);
        this.field_28 = MathHelper.floor(mobEntity.getWidth() + 1.0f);
    }

    public void clear() {
        this.blockView = null;
        this.entity = null;
    }

    protected PathNode getPathNode(int i, int j, int k) {
        return this.pathNodeCache.computeIfAbsent(PathNode.calculateHashCode(i, j, k), l -> new PathNode(i, j, k));
    }

    public abstract PathNode getStart();

    public abstract PathNode getPathNode(double var1, double var3, double var5);

    public abstract int getPathNodes(PathNode[] var1, PathNode var2);

    public abstract PathNodeType getPathNodeType(BlockView var1, int var2, int var3, int var4, MobEntity var5, int var6, int var7, int var8, boolean var9, boolean var10);

    public abstract PathNodeType getPathNodeType(BlockView var1, int var2, int var3, int var4);

    public void setCanEnterOpenDoors(boolean bl) {
        this.entersOpenDoors = bl;
    }

    public void setCanPathThroughDoors(boolean bl) {
        this.pathsThroughDoors = bl;
    }

    public void setCanSwim(boolean bl) {
        this.swims = bl;
    }

    public boolean canEnterOpenDoors() {
        return this.entersOpenDoors;
    }

    public boolean canPathThroughDoors() {
        return this.pathsThroughDoors;
    }

    public boolean canSwim() {
        return this.swims;
    }
}

