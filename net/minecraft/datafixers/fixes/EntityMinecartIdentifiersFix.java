/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixers.fixes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Objects;
import net.minecraft.datafixers.TypeReferences;

public class EntityMinecartIdentifiersFix
extends DataFix {
    private static final List<String> MINECARTS = Lists.newArrayList("MinecartRideable", "MinecartChest", "MinecartFurnace");

    public EntityMinecartIdentifiersFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    @Override
    public TypeRewriteRule makeRule() {
        TaggedChoice.TaggedChoiceType<?> taggedChoiceType = this.getInputSchema().findChoiceType(TypeReferences.ENTITY);
        TaggedChoice.TaggedChoiceType<?> taggedChoiceType2 = this.getOutputSchema().findChoiceType(TypeReferences.ENTITY);
        return this.fixTypeEverywhere("EntityMinecartIdentifiersFix", taggedChoiceType, taggedChoiceType2, dynamicOps -> pair -> {
            if (Objects.equals(pair.getFirst(), "Minecart")) {
                Typed<Pair<String, ?>> typed = taggedChoiceType.point((DynamicOps<?>)dynamicOps, "Minecart", pair.getSecond()).orElseThrow(IllegalStateException::new);
                Dynamic<?> dynamic = typed.getOrCreate(DSL.remainderFinder());
                int i = dynamic.get("Type").asInt(0);
                String string = i > 0 && i < MINECARTS.size() ? MINECARTS.get(i) : "MinecartRideable";
                return Pair.of(string, taggedChoiceType2.types().get(string).read(typed.write()).getSecond().orElseThrow(() -> new IllegalStateException("Could not read the new minecart.")));
            }
            return pair;
        });
    }
}

