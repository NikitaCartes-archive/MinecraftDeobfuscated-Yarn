/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import net.minecraft.datafixer.fix.EntitySimpleTransformFix;

public class EntityElderGuardianSplitFix
extends EntitySimpleTransformFix {
    public EntityElderGuardianSplitFix(Schema outputSchema, boolean changesType) {
        super("EntityElderGuardianSplitFix", outputSchema, changesType);
    }

    @Override
    protected Pair<String, Dynamic<?>> transform(String choice, Dynamic<?> tag) {
        return Pair.of(Objects.equals(choice, "Guardian") && tag.get("Elder").asBoolean(false) ? "ElderGuardian" : choice, tag);
    }
}

