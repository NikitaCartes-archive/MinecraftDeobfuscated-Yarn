/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class JigsawRotationFix
extends DataFix {
    private static final Map<String, String> ORIENTATION_UPDATES = ImmutableMap.builder().put("down", "down_south").put("up", "up_north").put("north", "north_up").put("south", "south_up").put("west", "west_up").put("east", "east_up").build();

    public JigsawRotationFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    private static Dynamic<?> updateBlockState(Dynamic<?> dynamic2) {
        Optional<String> optional = dynamic2.get("Name").asString().result();
        if (optional.equals(Optional.of("minecraft:jigsaw"))) {
            return dynamic2.update("Properties", dynamic -> {
                String string = dynamic.get("facing").asString("north");
                return dynamic.remove("facing").set("orientation", dynamic.createString(ORIENTATION_UPDATES.getOrDefault(string, string)));
            });
        }
        return dynamic2;
    }

    @Override
    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("jigsaw_rotation_fix", this.getInputSchema().getType(TypeReferences.BLOCK_STATE), typed -> typed.update(DSL.remainderFinder(), JigsawRotationFix::updateBlockState));
    }
}

