/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class EntityPaintingFieldsRenameFix
extends ChoiceFix {
    public EntityPaintingFieldsRenameFix(Schema schema) {
        super(schema, false, "EntityPaintingFieldsRenameFix", TypeReferences.ENTITY, "minecraft:painting");
    }

    public Dynamic<?> rename(Dynamic<?> dynamic) {
        return this.rename(this.rename(dynamic, "Motive", "variant"), "Facing", "facing");
    }

    private Dynamic<?> rename(Dynamic<?> dynamic, String oldKey, String newKey) {
        Optional<Dynamic<?>> optional = dynamic.get(oldKey).result();
        Optional<Dynamic> optional2 = optional.map(value -> dynamic.remove(oldKey).set(newKey, (Dynamic<?>)value));
        return DataFixUtils.orElse(optional2, dynamic);
    }

    @Override
    protected Typed<?> transform(Typed<?> inputType) {
        return inputType.update(DSL.remainderFinder(), this::rename);
    }
}

