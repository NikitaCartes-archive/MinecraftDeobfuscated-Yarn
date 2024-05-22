package net.minecraft.datafixer.fix;

import com.google.common.collect.Streams;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;

public class UpdateSignTextFormatFix extends ChoiceFix {
	public static final String FILTERED_CORRECT = "_filtered_correct";
	private static final String DEFAULT_COLOR = "black";

	public UpdateSignTextFormatFix(Schema outputSchema, String name, String blockEntityId) {
		super(outputSchema, false, name, TypeReferences.BLOCK_ENTITY, blockEntityId);
	}

	private static <T> Dynamic<T> updateSignTextFormat(Dynamic<T> signData) {
		return signData.set("front_text", updateFront(signData)).set("back_text", updateBack(signData)).set("is_waxed", signData.createBoolean(false));
	}

	private static <T> Dynamic<T> updateFront(Dynamic<T> signData) {
		Dynamic<T> dynamic = TextFixes.empty(signData.getOps());
		List<Dynamic<T>> list = streamKeys(signData, "Text").map(text -> (Dynamic)text.orElse(dynamic)).toList();
		Dynamic<T> dynamic2 = signData.emptyMap()
			.set("messages", signData.createList(list.stream()))
			.set("color", (Dynamic<?>)signData.get("Color").result().orElse(signData.createString("black")))
			.set("has_glowing_text", (Dynamic<?>)signData.get("GlowingText").result().orElse(signData.createBoolean(false)))
			.set("_filtered_correct", signData.createBoolean(true));
		List<Optional<Dynamic<T>>> list2 = streamKeys(signData, "FilteredText").toList();
		if (list2.stream().anyMatch(Optional::isPresent)) {
			dynamic2 = dynamic2.set("filtered_messages", signData.createList(Streams.mapWithIndex(list2.stream(), (message, index) -> {
				Dynamic<T> dynamicx = (Dynamic<T>)list.get((int)index);
				return (Dynamic<?>)message.orElse(dynamicx);
			})));
		}

		return dynamic2;
	}

	private static <T> Stream<Optional<Dynamic<T>>> streamKeys(Dynamic<T> signData, String prefix) {
		return Stream.of(
			signData.get(prefix + "1").result(), signData.get(prefix + "2").result(), signData.get(prefix + "3").result(), signData.get(prefix + "4").result()
		);
	}

	private static <T> Dynamic<T> updateBack(Dynamic<T> signData) {
		return signData.emptyMap()
			.set("messages", emptySignData(signData))
			.set("color", signData.createString("black"))
			.set("has_glowing_text", signData.createBoolean(false));
	}

	private static <T> Dynamic<T> emptySignData(Dynamic<T> signData) {
		Dynamic<T> dynamic = TextFixes.empty(signData.getOps());
		return signData.createList(Stream.of(dynamic, dynamic, dynamic, dynamic));
	}

	@Override
	protected Typed<?> transform(Typed<?> inputTyped) {
		return inputTyped.update(DSL.remainderFinder(), UpdateSignTextFormatFix::updateSignTextFormat);
	}
}
