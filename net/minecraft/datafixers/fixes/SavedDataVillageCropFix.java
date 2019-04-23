/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.stream.Stream;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.datafixers.fixes.BlockStateFlattening;

public class SavedDataVillageCropFix
extends DataFix {
    public SavedDataVillageCropFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    @Override
    public TypeRewriteRule makeRule() {
        return this.writeFixAndRead("SavedDataVillageCropFix", this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE), this.getOutputSchema().getType(TypeReferences.STRUCTURE_FEATURE), this::method_5152);
    }

    private <T> Dynamic<T> method_5152(Dynamic<T> dynamic) {
        return dynamic.update("Children", SavedDataVillageCropFix::method_5157);
    }

    private static <T> Dynamic<T> method_5157(Dynamic<T> dynamic) {
        return dynamic.asStreamOpt().map(SavedDataVillageCropFix::method_5151).map(dynamic::createList).orElse(dynamic);
    }

    private static Stream<? extends Dynamic<?>> method_5151(Stream<? extends Dynamic<?>> stream) {
        return stream.map(dynamic -> {
            String string = dynamic.get("id").asString("");
            if ("ViF".equals(string)) {
                return SavedDataVillageCropFix.method_5154(dynamic);
            }
            if ("ViDF".equals(string)) {
                return SavedDataVillageCropFix.method_5155(dynamic);
            }
            return dynamic;
        });
    }

    private static <T> Dynamic<T> method_5154(Dynamic<T> dynamic) {
        dynamic = SavedDataVillageCropFix.method_5156(dynamic, "CA");
        return SavedDataVillageCropFix.method_5156(dynamic, "CB");
    }

    private static <T> Dynamic<T> method_5155(Dynamic<T> dynamic) {
        dynamic = SavedDataVillageCropFix.method_5156(dynamic, "CA");
        dynamic = SavedDataVillageCropFix.method_5156(dynamic, "CB");
        dynamic = SavedDataVillageCropFix.method_5156(dynamic, "CC");
        return SavedDataVillageCropFix.method_5156(dynamic, "CD");
    }

    private static <T> Dynamic<T> method_5156(Dynamic<T> dynamic, String string) {
        if (dynamic.get(string).asNumber().isPresent()) {
            return dynamic.set(string, BlockStateFlattening.lookupState(dynamic.get(string).asInt(0) << 4));
        }
        return dynamic;
    }
}

