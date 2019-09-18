/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.class_4570;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.context.LootContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4568
implements class_4570 {
    private static final Logger field_20763 = LogManager.getLogger();
    private final Identifier field_20764;

    public class_4568(Identifier identifier) {
        this.field_20764 = identifier;
    }

    @Override
    public void check(LootTableReporter lootTableReporter) {
        if (lootTableReporter.method_22572(this.field_20764)) {
            lootTableReporter.report("Condition " + this.field_20764 + " is recursively called");
            return;
        }
        class_4570.super.check(lootTableReporter);
        class_4570 lv = lootTableReporter.method_22576(this.field_20764);
        if (lv == null) {
            lootTableReporter.report("Unknown condition table called " + this.field_20764);
        } else {
            lv.check(lootTableReporter.method_22569(".{" + this.field_20764 + "}", this.field_20764));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean method_22579(LootContext lootContext) {
        class_4570 lv = lootContext.method_22558(this.field_20764);
        if (lootContext.method_22555(lv)) {
            try {
                boolean bl = lv.test(lootContext);
                return bl;
            } finally {
                lootContext.method_22557(lv);
            }
        }
        field_20763.warn("Detected infinite loop in loot tables");
        return false;
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.method_22579((LootContext)object);
    }

    public static class class_4569
    extends class_4570.Factory<class_4568> {
        protected class_4569() {
            super(new Identifier("reference"), class_4568.class);
        }

        public void method_22582(JsonObject jsonObject, class_4568 arg, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("name", arg.field_20764.toString());
        }

        public class_4568 method_22581(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
            return new class_4568(identifier);
        }

        @Override
        public /* synthetic */ class_4570 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.method_22581(jsonObject, jsonDeserializationContext);
        }
    }
}

