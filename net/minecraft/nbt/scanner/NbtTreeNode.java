/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt.scanner;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtScanQuery;

/**
 * The tree node for representing NBT.
 * 
 *  * @param depth the depth of the node, starting from {@code 1}
 * @param selectedFields the keys to select ("leaves")
 * @param fieldsToRecurse The keys to check recursively ("branches")
 */
public record NbtTreeNode(int depth, Map<String, NbtType<?>> selectedFields, Map<String, NbtTreeNode> fieldsToRecurse) {
    private NbtTreeNode(int depth) {
        this(depth, new HashMap(), new HashMap<String, NbtTreeNode>());
    }

    /**
     * {@return the root node}
     * 
     * @implNote The root node has the depth of {@code 1}.
     */
    public static NbtTreeNode createRoot() {
        return new NbtTreeNode(1);
    }

    public void add(NbtScanQuery query) {
        if (this.depth <= query.path().size()) {
            this.fieldsToRecurse.computeIfAbsent(query.path().get(this.depth - 1), path -> new NbtTreeNode(this.depth + 1)).add(query);
        } else {
            this.selectedFields.put(query.key(), query.type());
        }
    }

    /**
     * {@return whether the queried type for the key {@code key} matches {@code type}}
     */
    public boolean isTypeEqual(NbtType<?> type, String key) {
        return type.equals(this.selectedFields().get(key));
    }
}

