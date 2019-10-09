/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.Iterator;
import java.util.List;
import net.minecraft.class_4512;
import net.minecraft.class_4692;
import net.minecraft.test.GameTest;

public class class_4693 {
    private final GameTest field_21457;
    private final List<class_4692> field_21458;
    private long field_21459;

    public void method_23643(long l) {
        try {
            this.method_23645(l);
        } catch (Exception exception) {
            // empty catch block
        }
    }

    public void method_23644(long l) {
        try {
            this.method_23645(l);
        } catch (Exception exception) {
            this.field_21457.fail(exception);
        }
    }

    private void method_23645(long l) {
        Iterator<class_4692> iterator = this.field_21458.iterator();
        while (iterator.hasNext()) {
            class_4692 lv = iterator.next();
            lv.field_21451.run();
            iterator.remove();
            long m = l - this.field_21459;
            long n = this.field_21459;
            this.field_21459 = l;
            if (lv.field_21450 == null || lv.field_21450 == m) continue;
            this.field_21457.fail(new class_4512("Succeeded in invalid tick: expected " + (n + lv.field_21450) + ", but current tick is " + l));
            break;
        }
    }
}

