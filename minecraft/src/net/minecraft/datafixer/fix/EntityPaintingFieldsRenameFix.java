package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class EntityPaintingFieldsRenameFix extends ChoiceFix {
	public EntityPaintingFieldsRenameFix(Schema outputSchema) {
		super(outputSchema, false, "EntityPaintingFieldsRenameFix", TypeReferences.ENTITY, "minecraft:painting");
	}

	public Dynamic<?> rename(Dynamic<?> dynamic) {
		return dynamic.renameField("Motive", "variant").renameField("Facing", "facing");
	}

	@Override
	protected Typed<?> transform(Typed<?> inputTyped) {
		return inputTyped.update(DSL.remainderFinder(), this::rename);
	}
}
