package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;

public class EntityElderGuardianSplitFix extends EntitySimpleTransformFix {
	public EntityElderGuardianSplitFix(Schema schema, boolean bl) {
		super("EntityElderGuardianSplitFix", schema, bl);
	}

	@Override
	protected Pair<String, Dynamic<?>> transform(String choice, Dynamic<?> dynamic) {
		return Pair.of(Objects.equals(choice, "Guardian") && dynamic.get("Elder").asBoolean(false) ? "ElderGuardian" : choice, dynamic);
	}
}
