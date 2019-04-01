package net.minecraft;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class class_3598 extends class_1212 {
	public class_3598(Schema schema, boolean bl) {
		super("EntityElderGuardianSplitFix", schema, bl);
	}

	@Override
	protected Pair<String, Dynamic<?>> method_5164(String string, Dynamic<?> dynamic) {
		return Pair.of(Objects.equals(string, "Guardian") && dynamic.get("Elder").asBoolean(false) ? "ElderGuardian" : string, dynamic);
	}
}
