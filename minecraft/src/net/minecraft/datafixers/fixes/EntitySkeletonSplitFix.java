package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class EntitySkeletonSplitFix extends EntitySimpleTransformFix {
	public EntitySkeletonSplitFix(Schema schema, boolean bl) {
		super("EntitySkeletonSplitFix", schema, bl);
	}

	@Override
	protected Pair<String, Dynamic<?>> transform(String string, Dynamic<?> dynamic) {
		if (Objects.equals(string, "Skeleton")) {
			int i = dynamic.getInt("SkeletonType");
			if (i == 1) {
				string = "WitherSkeleton";
			} else if (i == 2) {
				string = "Stray";
			}
		}

		return Pair.of(string, dynamic);
	}
}
