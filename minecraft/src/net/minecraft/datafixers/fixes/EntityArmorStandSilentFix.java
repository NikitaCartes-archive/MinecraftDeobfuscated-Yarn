package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixers.TypeReferences;

public class EntityArmorStandSilentFix extends ChoiceFix {
	public EntityArmorStandSilentFix(Schema schema, boolean bl) {
		super(schema, bl, "EntityArmorStandSilentFix", TypeReferences.ENTITY, "ArmorStand");
	}

	public Dynamic<?> method_15679(Dynamic<?> dynamic) {
		return dynamic.get("Silent").asBoolean(false) && !dynamic.get("Marker").asBoolean(false) ? dynamic.remove("Silent") : dynamic;
	}

	@Override
	protected Typed<?> transform(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::method_15679);
	}
}
