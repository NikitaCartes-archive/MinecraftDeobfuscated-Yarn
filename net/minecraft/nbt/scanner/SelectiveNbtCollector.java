/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt.scanner;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtCollector;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.scanner.Query;
import net.minecraft.nbt.scanner.Tree;

/**
 * A selective NBT collector builds an NBT object including only the
 * prescribed queries.
 */
public class SelectiveNbtCollector
extends NbtCollector {
    private int queriesLeft;
    private final Set<NbtType<?>> allPossibleTypes;
    private final Deque<Tree> selectionStack = new ArrayDeque<Tree>();

    public SelectiveNbtCollector(Query ... queries) {
        this.queriesLeft = queries.length;
        ImmutableSet.Builder builder = ImmutableSet.builder();
        Tree tree = Tree.method_40060();
        for (Query query : queries) {
            tree.add(query);
            builder.add(query.type());
        }
        this.selectionStack.push(tree);
        builder.add(NbtCompound.TYPE);
        this.allPossibleTypes = builder.build();
    }

    @Override
    public NbtScanner.Result start(NbtType<?> rootType) {
        if (rootType != NbtCompound.TYPE) {
            return NbtScanner.Result.HALT;
        }
        return super.start(rootType);
    }

    @Override
    public NbtScanner.NestedResult visitSubNbtType(NbtType<?> type) {
        Tree tree = this.selectionStack.element();
        if (this.getDepth() > tree.depth()) {
            return super.visitSubNbtType(type);
        }
        if (this.queriesLeft <= 0) {
            return NbtScanner.NestedResult.HALT;
        }
        if (!this.allPossibleTypes.contains(type)) {
            return NbtScanner.NestedResult.SKIP;
        }
        return super.visitSubNbtType(type);
    }

    @Override
    public NbtScanner.NestedResult startSubNbt(NbtType<?> type, String key) {
        Tree tree2;
        Tree tree = this.selectionStack.element();
        if (this.getDepth() > tree.depth()) {
            return super.startSubNbt(type, key);
        }
        if (tree.selectedFields().remove(key, type)) {
            --this.queriesLeft;
            return super.startSubNbt(type, key);
        }
        if (type == NbtCompound.TYPE && (tree2 = tree.fieldsToRecurse().get(key)) != null) {
            this.selectionStack.push(tree2);
            return super.startSubNbt(type, key);
        }
        return NbtScanner.NestedResult.SKIP;
    }

    @Override
    public NbtScanner.Result endNested() {
        if (this.getDepth() == this.selectionStack.element().depth()) {
            this.selectionStack.pop();
        }
        return super.endNested();
    }

    public int getQueriesLeft() {
        return this.queriesLeft;
    }
}

