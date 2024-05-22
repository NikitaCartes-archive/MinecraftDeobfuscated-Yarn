package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class PlayerHeadBlockProfileFix extends ChoiceFix {
	public PlayerHeadBlockProfileFix(Schema outputSchema) {
		super(outputSchema, false, "PlayerHeadBlockProfileFix", TypeReferences.BLOCK_ENTITY, "minecraft:skull");
	}

	@Override
	protected Typed<?> transform(Typed<?> inputTyped) {
		return inputTyped.update(DSL.remainderFinder(), this::fixProfile);
	}

	private <T> Dynamic<T> fixProfile(Dynamic<T> dynamic) {
		Optional<Dynamic<T>> optional = dynamic.get("SkullOwner").result();
		Optional<Dynamic<T>> optional2 = dynamic.get("ExtraType").result();
		Optional<Dynamic<T>> optional3 = optional.or(() -> optional2);
		if (optional3.isEmpty()) {
			return dynamic;
		} else {
			dynamic = dynamic.remove("SkullOwner").remove("ExtraType");
			return dynamic.set("profile", ItemStackComponentizationFix.createProfileDynamic((Dynamic<?>)optional3.get()));
		}
	}
}
