/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt.scanner;

import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtCollector;
import net.minecraft.nbt.scanner.NbtScanQuery;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.scanner.NbtTreeNode;

/**
 * An exclusive NBT collector builds an NBT object including everything
 * except the prescribed queries.
 * 
 * @see SelectiveNbtCollector
 */
public class ExclusiveNbtCollector
extends NbtCollector {
    private final Deque<NbtTreeNode> treeStack = new ArrayDeque<NbtTreeNode>();

    public ExclusiveNbtCollector(NbtScanQuery ... excludedQueries) {
        NbtTreeNode nbtTreeNode = NbtTreeNode.createRoot();
        for (NbtScanQuery nbtScanQuery : excludedQueries) {
            nbtTreeNode.add(nbtScanQuery);
        }
        this.treeStack.push(nbtTreeNode);
    }

    @Override
    public NbtScanner.NestedResult startSubNbt(NbtType<?> type, String key) {
        NbtTreeNode nbtTreeNode2;
        NbtTreeNode nbtTreeNode = this.treeStack.element();
        if (nbtTreeNode.isTypeEqual(type, key)) {
            return NbtScanner.NestedResult.SKIP;
        }
        if (type == NbtCompound.TYPE && (nbtTreeNode2 = nbtTreeNode.fieldsToRecurse().get(key)) != null) {
            this.treeStack.push(nbtTreeNode2);
        }
        return super.startSubNbt(type, key);
    }

    @Override
    public NbtScanner.Result endNested() {
        if (this.getDepth() == this.treeStack.element().depth()) {
            this.treeStack.pop();
        }
        return super.endNested();
    }
}

