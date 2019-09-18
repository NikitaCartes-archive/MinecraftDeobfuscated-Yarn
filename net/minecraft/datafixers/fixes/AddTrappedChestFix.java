/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.types.templates.TaggedChoice;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.datafixers.fixes.ChoiceTypesFix;
import net.minecraft.datafixers.fixes.LeavesFix;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class AddTrappedChestFix
extends DataFix {
    private static final Logger LOGGER = LogManager.getLogger();

    public AddTrappedChestFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    @Override
    public TypeRewriteRule makeRule() {
        Type<?> type = this.getOutputSchema().getType(TypeReferences.CHUNK);
        Type<?> type2 = type.findFieldType("Level");
        Type<?> type3 = type2.findFieldType("TileEntities");
        if (!(type3 instanceof List.ListType)) {
            throw new IllegalStateException("Tile entity type is not a list type.");
        }
        List.ListType listType = (List.ListType)type3;
        OpticFinder opticFinder = DSL.fieldFinder("TileEntities", listType);
        Type<?> type4 = this.getInputSchema().getType(TypeReferences.CHUNK);
        OpticFinder<?> opticFinder2 = type4.findField("Level");
        OpticFinder<?> opticFinder3 = opticFinder2.type().findField("Sections");
        Type<?> type5 = opticFinder3.type();
        if (!(type5 instanceof List.ListType)) {
            throw new IllegalStateException("Expecting sections to be a list.");
        }
        Type type6 = ((List.ListType)type5).getElement();
        OpticFinder opticFinder4 = DSL.typeFinder(type6);
        return TypeRewriteRule.seq(new ChoiceTypesFix(this.getOutputSchema(), "AddTrappedChestFix", TypeReferences.BLOCK_ENTITY).makeRule(), this.fixTypeEverywhereTyped("Trapped Chest fix", type4, typed2 -> typed2.updateTyped(opticFinder2, typed -> {
            Optional optional = typed.getOptionalTyped(opticFinder3);
            if (!optional.isPresent()) {
                return typed;
            }
            List list = optional.get().getAllTyped(opticFinder4);
            IntOpenHashSet intSet = new IntOpenHashSet();
            for (Typed typed22 : list) {
                class_1216 lv = new class_1216(typed22, this.getInputSchema());
                if (lv.method_5079()) continue;
                for (int i = 0; i < 4096; ++i) {
                    int j = lv.method_5075(i);
                    if (!lv.method_5180(j)) continue;
                    intSet.add(lv.method_5077() << 12 | i);
                }
            }
            Dynamic<?> dynamic = typed.get(DSL.remainderFinder());
            int k = dynamic.get("xPos").asInt(0);
            int l = dynamic.get("zPos").asInt(0);
            TaggedChoice.TaggedChoiceType<?> taggedChoiceType = this.getInputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY);
            return typed.updateTyped(opticFinder, typed2 -> typed2.updateTyped(taggedChoiceType.finder(), typed -> {
                int m;
                int l;
                Dynamic<?> dynamic = typed.getOrCreate(DSL.remainderFinder());
                int k = dynamic.get("x").asInt(0) - (k << 4);
                if (intSet.contains(LeavesFix.method_5051(k, l = dynamic.get("y").asInt(0), m = dynamic.get("z").asInt(0) - (l << 4)))) {
                    return typed.update(taggedChoiceType.finder(), pair -> pair.mapFirst(string -> {
                        if (!Objects.equals(string, "minecraft:chest")) {
                            LOGGER.warn("Block Entity was expected to be a chest");
                        }
                        return "minecraft:trapped_chest";
                    }));
                }
                return typed;
            }));
        })));
    }

    public static final class class_1216
    extends LeavesFix.class_1193 {
        @Nullable
        private IntSet field_5741;

        public class_1216(Typed<?> typed, Schema schema) {
            super(typed, schema);
        }

        @Override
        protected boolean method_5076() {
            this.field_5741 = new IntOpenHashSet();
            for (int i = 0; i < this.field_5692.size(); ++i) {
                Dynamic dynamic = (Dynamic)this.field_5692.get(i);
                String string = dynamic.get("Name").asString("");
                if (!Objects.equals(string, "minecraft:trapped_chest")) continue;
                this.field_5741.add(i);
            }
            return this.field_5741.isEmpty();
        }

        public boolean method_5180(int i) {
            return this.field_5741.contains(i);
        }
    }
}

