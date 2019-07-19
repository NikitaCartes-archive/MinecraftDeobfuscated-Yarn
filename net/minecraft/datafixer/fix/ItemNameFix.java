/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;

public abstract class ItemNameFix
extends DataFix {
    private final String name;

    public ItemNameFix(Schema schema, String string) {
        super(schema, false);
        this.name = string;
    }

    @Override
    public TypeRewriteRule makeRule() {
        Type<Pair<String, String>> type = DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString());
        if (!Objects.equals(this.getInputSchema().getType(TypeReferences.ITEM_NAME), type)) {
            throw new IllegalStateException("item name type is not what was expected.");
        }
        return this.fixTypeEverywhere(this.name, type, dynamicOps -> pair -> pair.mapSecond(this::rename));
    }

    protected abstract String rename(String var1);

    public static DataFix create(Schema schema, String string, final Function<String, String> function) {
        return new ItemNameFix(schema, string){

            @Override
            protected String rename(String string) {
                return (String)function.apply(string);
            }
        };
    }
}

