/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.util.math.ChunkSectionPos;

public class BlendingDataFix
extends DataFix {
    private final String name;
    private static final Set<String> field_37415 = Set.of("minecraft:empty", "minecraft:structure_starts", "minecraft:structure_references", "minecraft:biomes");

    public BlendingDataFix(Schema schema, String name) {
        super(schema, false);
        this.name = name;
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getOutputSchema().getType(TypeReferences.CHUNK);
        return this.fixTypeEverywhereTyped(this.name, type, typed -> typed.update(DSL.remainderFinder(), BlendingDataFix::method_41312));
    }

    private static Dynamic<?> method_41312(Dynamic<?> dynamic) {
        Optional<Dynamic<?>> optional = (dynamic = dynamic.remove("blending_data")).get("Status").result();
        if (optional.isPresent()) {
            Dynamic<?> dynamic2;
            String string2;
            String string = IdentifierNormalizingSchema.normalize(optional.get().asString("empty"));
            Optional<Dynamic<?>> optional2 = dynamic.get("below_zero_retrogen").result();
            if (!field_37415.contains(string)) {
                dynamic = BlendingDataFix.method_41313(dynamic, 384, -64);
            } else if (optional2.isPresent() && !field_37415.contains(string2 = IdentifierNormalizingSchema.normalize((dynamic2 = optional2.get()).get("target_status").asString("empty")))) {
                dynamic = BlendingDataFix.method_41313(dynamic, 256, 0);
            }
        }
        return dynamic;
    }

    private static Dynamic<?> method_41313(Dynamic<?> dynamic, int i, int j) {
        return dynamic.set("blending_data", dynamic.createMap(Map.of(dynamic.createString("min_section"), dynamic.createInt(ChunkSectionPos.getSectionCoord(j)), dynamic.createString("max_section"), dynamic.createInt(ChunkSectionPos.getSectionCoord(j + i)))));
    }
}

