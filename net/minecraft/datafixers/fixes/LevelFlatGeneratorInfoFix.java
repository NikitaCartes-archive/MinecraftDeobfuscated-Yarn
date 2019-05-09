/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixers.fixes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.datafixers.fixes.BlockStateFlattening;
import net.minecraft.datafixers.fixes.EntityBlockStateFix;
import org.apache.commons.lang3.math.NumberUtils;

public class LevelFlatGeneratorInfoFix
extends DataFix {
    private static final Splitter SPLIT_ON_SEMICOLON = Splitter.on(';').limit(5);
    private static final Splitter SPLIT_ON_COMMA = Splitter.on(',');
    private static final Splitter SPLIT_ON_LOWER_X = Splitter.on('x').limit(2);
    private static final Splitter SPLIT_ON_ASTERISK = Splitter.on('*').limit(2);
    private static final Splitter SPLIT_ON_COLON = Splitter.on(':').limit(3);

    public LevelFlatGeneratorInfoFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    @Override
    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("LevelFlatGeneratorInfoFix", this.getInputSchema().getType(TypeReferences.LEVEL), typed -> typed.update(DSL.remainderFinder(), this::fixGeneratorOptions));
    }

    private Dynamic<?> fixGeneratorOptions(Dynamic<?> dynamic2) {
        if (dynamic2.get("generatorName").asString("").equalsIgnoreCase("flat")) {
            return dynamic2.update("generatorOptions", dynamic -> DataFixUtils.orElse(dynamic.asString().map(this::fixFlatGeneratorOptions).map(dynamic::createString), dynamic));
        }
        return dynamic2;
    }

    @VisibleForTesting
    String fixFlatGeneratorOptions(String string2) {
        String string3;
        int i;
        if (string2.isEmpty()) {
            return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
        }
        Iterator<String> iterator = SPLIT_ON_SEMICOLON.split(string2).iterator();
        String string22 = iterator.next();
        if (iterator.hasNext()) {
            i = NumberUtils.toInt(string22, 0);
            string3 = iterator.next();
        } else {
            i = 0;
            string3 = string22;
        }
        if (i < 0 || i > 3) {
            return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
        }
        StringBuilder stringBuilder = new StringBuilder();
        Splitter splitter = i < 3 ? SPLIT_ON_LOWER_X : SPLIT_ON_ASTERISK;
        stringBuilder.append(StreamSupport.stream(SPLIT_ON_COMMA.split(string3).spliterator(), false).map(string -> {
            String string2;
            int j;
            List<String> list = splitter.splitToList((CharSequence)string);
            if (list.size() == 2) {
                j = NumberUtils.toInt(list.get(0));
                string2 = list.get(1);
            } else {
                j = 1;
                string2 = list.get(0);
            }
            List<String> list2 = SPLIT_ON_COLON.splitToList(string2);
            int k = list2.get(0).equals("minecraft") ? 1 : 0;
            String string3 = list2.get(k);
            int l = i == 3 ? EntityBlockStateFix.getNumericalBlockId("minecraft:" + string3) : NumberUtils.toInt(string3, 0);
            int m = k + 1;
            int n = list2.size() > m ? NumberUtils.toInt(list2.get(m), 0) : 0;
            return (j == 1 ? "" : j + "*") + BlockStateFlattening.lookupState(l << 4 | n).get("Name").asString("");
        }).collect(Collectors.joining(",")));
        while (iterator.hasNext()) {
            stringBuilder.append(';').append(iterator.next());
        }
        return stringBuilder.toString();
    }
}

