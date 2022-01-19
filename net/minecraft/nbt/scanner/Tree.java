/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt.scanner;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.Query;

public record Tree(int depth, Map<String, NbtType<?>> selectedFields, Map<String, Tree> fieldsToRecurse) {
    private Tree(int depth) {
        this(depth, new HashMap(), new HashMap<String, Tree>());
    }

    public static Tree method_40060() {
        return new Tree(1);
    }

    public void add(Query query) {
        if (this.depth <= query.path().size()) {
            this.fieldsToRecurse.computeIfAbsent(query.path().get(this.depth - 1), path -> new Tree(this.depth + 1)).add(query);
        } else {
            this.selectedFields.put(query.key(), query.type());
        }
    }

    public boolean method_40061(NbtType<?> nbtType, String string) {
        return nbtType.equals(this.selectedFields().get(string));
    }
}

