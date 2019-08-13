package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class EntityElderGuardianSplitFix extends EntitySimpleTransformFix {
	public EntityElderGuardianSplitFix(Schema schema, boolean bl) {
		super("EntityElderGuardianSplitFix", schema, bl);
	}

	@Override
	protected Pair<String, Dynamic<?>> transform(String string, Dynamic<?> dynamic) {
		return Pair.of(Objects.equals(string, "Guardian") && dynamic.get("Elder").asBoolean(false) ? "ElderGuardian" : string, dynamic);
	}
}
