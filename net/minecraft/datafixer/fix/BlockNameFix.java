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
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;

public abstract class BlockNameFix
extends DataFix {
    private final String name;

    public BlockNameFix(Schema schema, String string) {
        super(schema, false);
        this.name = string;
    }

    @Override
    public TypeRewriteRule makeRule() {
        Type<Pair<String, String>> type2;
        Type<?> type = this.getInputSchema().getType(TypeReferences.BLOCK_NAME);
        if (!Objects.equals(type, type2 = DSL.named(TypeReferences.BLOCK_NAME.typeName(), DSL.namespacedString()))) {
            throw new IllegalStateException("block type is not what was expected.");
        }
        TypeRewriteRule typeRewriteRule = this.fixTypeEverywhere(this.name + " for block", type2, dynamicOps -> pair -> pair.mapSecond(this::rename));
        TypeRewriteRule typeRewriteRule2 = this.fixTypeEverywhereTyped(this.name + " for block_state", this.getInputSchema().getType(TypeReferences.BLOCK_STATE), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            Optional<String> optional = dynamic.get("Name").asString();
            if (optional.isPresent()) {
                return dynamic.set("Name", dynamic.createString(this.rename(optional.get())));
            }
            return dynamic;
        }));
        return TypeRewriteRule.seq(typeRewriteRule, typeRewriteRule2);
    }

    protected abstract String rename(String var1);

    public static DataFix create(Schema schema, String string, final Function<String, String> function) {
        return new BlockNameFix(schema, string){

            @Override
            protected String rename(String string) {
                return (String)function.apply(string);
            }
        };
    }
}

