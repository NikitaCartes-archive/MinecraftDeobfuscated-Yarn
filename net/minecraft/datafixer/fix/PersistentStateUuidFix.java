/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.AbstractUuidFix;
import org.slf4j.Logger;

public class PersistentStateUuidFix
extends AbstractUuidFix {
    private static final Logger field_36330 = LogUtils.getLogger();

    public PersistentStateUuidFix(Schema outputSchema) {
        super(outputSchema, TypeReferences.SAVED_DATA);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("SavedDataUUIDFix", this.getInputSchema().getType(this.typeReference), typed2 -> typed2.updateTyped(typed2.getType().findField("data"), typed -> typed.update(DSL.remainderFinder(), dynamic -> dynamic.update("Raids", dynamic2 -> dynamic2.createList(dynamic2.asStream().map(dynamic -> dynamic.update("HeroesOfTheVillage", dynamic2 -> dynamic2.createList(dynamic2.asStream().map(dynamic -> PersistentStateUuidFix.createArrayFromMostLeastTags(dynamic, "UUIDMost", "UUIDLeast").orElseGet(() -> {
            field_36330.warn("HeroesOfTheVillage contained invalid UUIDs.");
            return dynamic;
        }))))))))));
    }
}

