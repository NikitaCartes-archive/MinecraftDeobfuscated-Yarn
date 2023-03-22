package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class UpdateSignTextFormatFix extends ChoiceFix {
	public UpdateSignTextFormatFix(Schema outputSchema, String name, String blockEntityId) {
		super(outputSchema, false, name, TypeReferences.BLOCK_ENTITY, blockEntityId);
	}

	private static Dynamic<?> updateSignTextFormat(Dynamic<?> dynamic) {
		String string = "black";
		Dynamic<?> dynamic2 = dynamic.emptyMap();
		dynamic2 = dynamic2.set("messages", updateFrontText(dynamic, "Text"));
		dynamic2 = dynamic2.set("filtered_messages", updateFrontText(dynamic, "FilteredText"));
		Optional<? extends Dynamic<?>> optional = dynamic.get("Color").result();
		dynamic2 = dynamic2.set("color", optional.isPresent() ? (Dynamic)optional.get() : dynamic2.createString("black"));
		Optional<? extends Dynamic<?>> optional2 = dynamic.get("GlowingText").result();
		dynamic2 = dynamic2.set("has_glowing_text", optional2.isPresent() ? (Dynamic)optional2.get() : dynamic2.createBoolean(false));
		Dynamic<?> dynamic3 = dynamic.emptyMap();
		Dynamic<?> dynamic4 = updateBackText(dynamic);
		dynamic3 = dynamic3.set("messages", dynamic4);
		dynamic3 = dynamic3.set("filtered_messages", dynamic4);
		dynamic3 = dynamic3.set("color", dynamic3.createString("black"));
		dynamic3 = dynamic3.set("has_glowing_text", dynamic3.createBoolean(false));
		dynamic = dynamic.set("front_text", dynamic2);
		return dynamic.set("back_text", dynamic3);
	}

	private static <T> Dynamic<T> updateFrontText(Dynamic<T> dynamic, String textKey) {
		Dynamic<T> dynamic2 = dynamic.createString(getEmptyText());
		return dynamic.createList(
			Stream.of(
				(Dynamic)dynamic.get(textKey + "1").result().orElse(dynamic2),
				(Dynamic)dynamic.get(textKey + "2").result().orElse(dynamic2),
				(Dynamic)dynamic.get(textKey + "3").result().orElse(dynamic2),
				(Dynamic)dynamic.get(textKey + "4").result().orElse(dynamic2)
			)
		);
	}

	private static <T> Dynamic<T> updateBackText(Dynamic<T> dynamic) {
		Dynamic<T> dynamic2 = dynamic.createString(getEmptyText());
		return dynamic.createList(Stream.of(dynamic2, dynamic2, dynamic2, dynamic2));
	}

	private static String getEmptyText() {
		return Text.Serializer.toJson(ScreenTexts.EMPTY);
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), UpdateSignTextFormatFix::updateSignTextFormat);
	}
}
