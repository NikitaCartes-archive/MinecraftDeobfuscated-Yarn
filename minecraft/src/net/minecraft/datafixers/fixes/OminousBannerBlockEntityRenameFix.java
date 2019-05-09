package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import net.minecraft.datafixers.TypeReferences;

public class OminousBannerBlockEntityRenameFix extends ChoiceFix {
	public OminousBannerBlockEntityRenameFix(Schema schema, boolean bl) {
		super(schema, bl, "OminousBannerBlockEntityRenameFix", TypeReferences.BLOCK_ENTITY, "minecraft:banner");
	}

	@Override
	protected Typed<?> transform(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::fixBannerName);
	}

	private Dynamic<?> fixBannerName(Dynamic<?> dynamic) {
		Optional<String> optional = dynamic.get("CustomName").asString();
		if (optional.isPresent()) {
			String string = (String)optional.get();
			string = string.replace("\"translate\":\"block.minecraft.illager_banner\"", "\"translate\":\"block.minecraft.ominous_banner\"");
			return dynamic.set("CustomName", dynamic.createString(string));
		} else {
			return dynamic;
		}
	}
}
