package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class EntitySkeletonSplitFix extends EntitySimpleTransformFix {
	public EntitySkeletonSplitFix(Schema outputSchema, boolean changesType) {
		super("EntitySkeletonSplitFix", outputSchema, changesType);
	}

	@Override
	protected Pair<String, Dynamic<?>> transform(String choice, Dynamic<?> tag) {
		if (Objects.equals(choice, "Skeleton")) {
			int i = tag.get("SkeletonType").asInt(0);
			if (i == 1) {
				choice = "WitherSkeleton";
			} else if (i == 2) {
				choice = "Stray";
			}
		}

		return Pair.of(choice, tag);
	}
}
