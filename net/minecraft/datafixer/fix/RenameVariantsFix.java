/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.datafixer.fix.ChoiceFix;

public class RenameVariantsFix
extends ChoiceFix {
    private final Map<String, String> oldToNewNames;

    public RenameVariantsFix(Schema schema, String name, DSL.TypeReference type, String choiceName, Map<String, String> oldToNewNames) {
        super(schema, false, name, type, choiceName);
        this.oldToNewNames = oldToNewNames;
    }

    @Override
    protected Typed<?> transform(Typed<?> inputType) {
        return inputType.update(DSL.remainderFinder(), dynamic2 -> dynamic2.update("variant", dynamic -> DataFixUtils.orElse(dynamic.asString().map(variantName -> dynamic.createString(this.oldToNewNames.getOrDefault(variantName, (String)variantName))).result(), dynamic)));
    }
}

