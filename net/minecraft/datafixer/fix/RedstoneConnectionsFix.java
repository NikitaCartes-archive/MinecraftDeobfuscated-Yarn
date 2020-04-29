/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;

public class RedstoneConnectionsFix
extends DataFix {
    public RedstoneConnectionsFix(Schema outputSchema) {
        super(outputSchema, false);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Schema schema = this.getInputSchema();
        return this.fixTypeEverywhereTyped("RedstoneConnectionsFix", schema.getType(TypeReferences.BLOCK_STATE), typed -> typed.update(DSL.remainderFinder(), this::updateBlockState));
    }

    private <T> Dynamic<T> updateBlockState(Dynamic<T> data) {
        boolean bl = data.get("Name").asString().filter("minecraft:redstone_wire"::equals).isPresent();
        if (!bl) {
            return data;
        }
        return data.update("Properties", dynamic2 -> {
            String string = dynamic2.get("east").asString().orElseGet(() -> "none");
            String string2 = dynamic2.get("west").asString().orElseGet(() -> "none");
            String string3 = dynamic2.get("north").asString().orElseGet(() -> "none");
            String string4 = dynamic2.get("south").asString().orElseGet(() -> "none");
            boolean bl = RedstoneConnectionsFix.hasObsoleteValue(string) || RedstoneConnectionsFix.hasObsoleteValue(string2);
            boolean bl2 = RedstoneConnectionsFix.hasObsoleteValue(string3) || RedstoneConnectionsFix.hasObsoleteValue(string4);
            String string5 = !RedstoneConnectionsFix.hasObsoleteValue(string) && !bl2 ? "side" : string;
            String string6 = !RedstoneConnectionsFix.hasObsoleteValue(string2) && !bl2 ? "side" : string2;
            String string7 = !RedstoneConnectionsFix.hasObsoleteValue(string3) && !bl ? "side" : string3;
            String string8 = !RedstoneConnectionsFix.hasObsoleteValue(string4) && !bl ? "side" : string4;
            return dynamic2.update("east", dynamic -> dynamic.createString(string5)).update("west", dynamic -> dynamic.createString(string6)).update("north", dynamic -> dynamic.createString(string7)).update("south", dynamic -> dynamic.createString(string8));
        });
    }

    private static boolean hasObsoleteValue(String string) {
        return !"none".equals(string);
    }
}

