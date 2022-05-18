package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;

public class EntityTippedArrowFix extends EntityRenameFix {
	public EntityTippedArrowFix(Schema schema, boolean bl) {
		super("EntityTippedArrowFix", schema, bl);
	}

	@Override
	protected String rename(String oldName) {
		return Objects.equals(oldName, "TippedArrow") ? "Arrow" : oldName;
	}
}
