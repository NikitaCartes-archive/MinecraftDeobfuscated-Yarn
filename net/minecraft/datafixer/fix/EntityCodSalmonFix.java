/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.datafixer.fix.EntityRenameFix;

public class EntityCodSalmonFix
extends EntityRenameFix {
    public static final Map<String, String> ENTITIES = ImmutableMap.builder().put("minecraft:salmon_mob", "minecraft:salmon").put("minecraft:cod_mob", "minecraft:cod").build();
    public static final Map<String, String> SPAWN_EGGS = ImmutableMap.builder().put("minecraft:salmon_mob_spawn_egg", "minecraft:salmon_spawn_egg").put("minecraft:cod_mob_spawn_egg", "minecraft:cod_spawn_egg").build();

    public EntityCodSalmonFix(Schema schema, boolean bl) {
        super("EntityCodSalmonFix", schema, bl);
    }

    @Override
    protected String rename(String string) {
        return ENTITIES.getOrDefault(string, string);
    }
}

