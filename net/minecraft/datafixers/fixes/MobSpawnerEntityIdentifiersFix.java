/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.datafixers.TypeReferences;

public class MobSpawnerEntityIdentifiersFix
extends DataFix {
    public MobSpawnerEntityIdentifiersFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    private Dynamic<?> fixSpawner(Dynamic<?> tag) {
        Optional<Stream<Dynamic<?>>> optional2;
        if (!"MobSpawner".equals(tag.get("id").asString(""))) {
            return tag;
        }
        Optional<String> optional = tag.get("EntityId").asString();
        if (optional.isPresent()) {
            Dynamic dynamic2 = DataFixUtils.orElse(tag.get("SpawnData").get(), tag.emptyMap());
            dynamic2 = dynamic2.set("id", dynamic2.createString(optional.get().isEmpty() ? "Pig" : optional.get()));
            tag = tag.set("SpawnData", dynamic2);
            tag = tag.remove("EntityId");
        }
        if ((optional2 = tag.get("SpawnPotentials").asStreamOpt()).isPresent()) {
            tag = tag.set("SpawnPotentials", tag.createList(optional2.get().map(dynamic -> {
                Optional<String> optional = dynamic.get("Type").asString();
                if (optional.isPresent()) {
                    Dynamic dynamic2 = DataFixUtils.orElse(dynamic.get("Properties").get(), dynamic.emptyMap()).set("id", dynamic.createString(optional.get()));
                    return dynamic.set("Entity", dynamic2).remove("Type").remove("Properties");
                }
                return dynamic;
            })));
        }
        return tag;
    }

    @Override
    public TypeRewriteRule makeRule() {
        Type<?> type = this.getOutputSchema().getType(TypeReferences.UNTAGGED_SPAWNER);
        return this.fixTypeEverywhereTyped("MobSpawnerEntityIdentifiersFix", this.getInputSchema().getType(TypeReferences.UNTAGGED_SPAWNER), type, (Typed<?> typed) -> {
            Dynamic dynamic = typed.get(DSL.remainderFinder());
            Pair pair = type.readTyped(this.fixSpawner(dynamic = dynamic.set("id", dynamic.createString("MobSpawner"))));
            if (!pair.getSecond().isPresent()) {
                return typed;
            }
            return pair.getSecond().get();
        });
    }
}

