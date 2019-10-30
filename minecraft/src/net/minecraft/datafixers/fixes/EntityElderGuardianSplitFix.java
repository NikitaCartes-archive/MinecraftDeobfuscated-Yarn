package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class EntityElderGuardianSplitFix extends EntitySimpleTransformFix {
	public EntityElderGuardianSplitFix(Schema outputSchema, boolean changesType) {
		super("EntityElderGuardianSplitFix", outputSchema, changesType);
	}

	@Override
	protected Pair<String, Dynamic<?>> transform(String choice, Dynamic<?> tag) {
		return Pair.of(Objects.equals(choice, "Guardian") && tag.get("Elder").asBoolean(false) ? "ElderGuardian" : choice, tag);
	}
}
