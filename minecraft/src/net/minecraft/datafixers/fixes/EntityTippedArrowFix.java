package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;

public class EntityTippedArrowFix extends EntityRenameFix {
	public EntityTippedArrowFix(Schema schema, boolean bl) {
		super("EntityTippedArrowFix", schema, bl);
	}

	@Override
	protected String rename(String string) {
		return Objects.equals(string, "TippedArrow") ? "Arrow" : string;
	}
}
