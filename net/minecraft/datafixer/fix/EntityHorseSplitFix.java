/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.EntityTransformFix;

public class EntityHorseSplitFix
extends EntityTransformFix {
    public EntityHorseSplitFix(Schema outputSchema, boolean changesType) {
        super("EntityHorseSplitFix", outputSchema, changesType);
    }

    @Override
    protected Pair<String, Typed<?>> transform(String choice, Typed<?> typed) {
        Dynamic<?> dynamic = typed.get(DSL.remainderFinder());
        if (Objects.equals("EntityHorse", choice)) {
            String string;
            int i = dynamic.get("Type").asInt(0);
            switch (i) {
                default: {
                    string = "Horse";
                    break;
                }
                case 1: {
                    string = "Donkey";
                    break;
                }
                case 2: {
                    string = "Mule";
                    break;
                }
                case 3: {
                    string = "ZombieHorse";
                    break;
                }
                case 4: {
                    string = "SkeletonHorse";
                }
            }
            dynamic.remove("Type");
            Type<?> type = this.getOutputSchema().findChoiceType(TypeReferences.ENTITY).types().get(string);
            return Pair.of(string, type.readTyped(typed.write()).getSecond().orElseThrow(() -> new IllegalStateException("Could not parse the new horse")));
        }
        return Pair.of(choice, typed);
    }
}

