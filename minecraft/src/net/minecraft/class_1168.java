package net.minecraft;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class class_1168 extends class_1212 {
	public class_1168(Schema schema, boolean bl) {
		super("EntitySkeletonSplitFix", schema, bl);
	}

	@Override
	protected Pair<String, Dynamic<?>> method_5164(String string, Dynamic<?> dynamic) {
		if (Objects.equals(string, "Skeleton")) {
			int i = dynamic.get("SkeletonType").asInt(0);
			if (i == 1) {
				string = "WitherSkeleton";
			} else if (i == 2) {
				string = "Stray";
			}
		}

		return Pair.of(string, dynamic);
	}
}
