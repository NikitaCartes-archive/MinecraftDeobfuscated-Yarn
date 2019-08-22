/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Collection;
import net.minecraft.class_4517;

public class class_4521 {
    public static final class_4521 field_20574 = new class_4521();
    private final Collection<class_4517> field_20575 = Lists.newCopyOnWriteArrayList();

    public void method_22227(class_4517 arg) {
        this.field_20575.add(arg);
    }

    public void method_22226() {
        this.field_20575.clear();
    }

    public void method_22228() {
        this.field_20575.forEach(class_4517::method_22165);
        this.field_20575.removeIf(class_4517::method_22180);
    }
}

