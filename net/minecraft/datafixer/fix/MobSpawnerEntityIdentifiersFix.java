/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;

public class MobSpawnerEntityIdentifiersFix
extends DataFix {
    public MobSpawnerEntityIdentifiersFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    private Dynamic<?> fixSpawner(Dynamic<?> dynamic2) {
        Optional<Stream<Dynamic<?>>> optional2;
        if (!"MobSpawner".equals(dynamic2.get("id").asString(""))) {
            return dynamic2;
        }
        Optional<String> optional = dynamic2.get("EntityId").asString().result();
        if (optional.isPresent()) {
            Dynamic dynamic22 = DataFixUtils.orElse(dynamic2.get("SpawnData").result(), dynamic2.emptyMap());
            dynamic22 = dynamic22.set("id", dynamic22.createString(optional.get().isEmpty() ? "Pig" : optional.get()));
            dynamic2 = dynamic2.set("SpawnData", dynamic22);
            dynamic2 = dynamic2.remove("EntityId");
        }
        if ((optional2 = dynamic2.get("SpawnPotentials").asStreamOpt().result()).isPresent()) {
            dynamic2 = dynamic2.set("SpawnPotentials", dynamic2.createList(optional2.get().map(dynamic -> {
                Optional<String> optional = dynamic.get("Type").asString().result();
                if (optional.isPresent()) {
                    Dynamic dynamic2 = DataFixUtils.orElse(dynamic.get("Properties").result(), dynamic.emptyMap()).set("id", dynamic.createString(optional.get()));
                    return dynamic.set("Entity", dynamic2).remove("Type").remove("Properties");
                }
                return dynamic;
            })));
        }
        return dynamic2;
    }

    @Override
    public TypeRewriteRule makeRule() {
        Type<?> type = this.getOutputSchema().getType(TypeReferences.UNTAGGED_SPAWNER);
        return this.fixTypeEverywhereTyped("MobSpawnerEntityIdentifiersFix", this.getInputSchema().getType(TypeReferences.UNTAGGED_SPAWNER), type, (Typed<?> typed) -> {
            Dynamic dynamic = typed.get(DSL.remainderFinder());
            DataResult dataResult = type.readTyped(this.fixSpawner(dynamic = dynamic.set("id", dynamic.createString("MobSpawner"))));
            if (!dataResult.result().isPresent()) {
                return typed;
            }
            return dataResult.result().get().getFirst();
        });
    }
}

