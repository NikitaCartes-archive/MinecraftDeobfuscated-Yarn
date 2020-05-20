package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;

public class SavedDataVillageCropFix extends DataFix {
	public SavedDataVillageCropFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	public TypeRewriteRule makeRule() {
		return this.writeFixAndRead(
			"SavedDataVillageCropFix",
			this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE),
			this.getOutputSchema().getType(TypeReferences.STRUCTURE_FEATURE),
			this::method_5152
		);
	}

	private <T> Dynamic<T> method_5152(Dynamic<T> dynamic) {
		return dynamic.update("Children", SavedDataVillageCropFix::method_5157);
	}

	private static <T> Dynamic<T> method_5157(Dynamic<T> dynamic) {
		return (Dynamic<T>)dynamic.asStreamOpt().map(SavedDataVillageCropFix::fixVillageChildren).map(dynamic::createList).result().orElse(dynamic);
	}

	private static Stream<? extends Dynamic<?>> fixVillageChildren(Stream<? extends Dynamic<?>> villageChildren) {
		return villageChildren.map(dynamic -> {
			String string = dynamic.get("id").asString("");
			if ("ViF".equals(string)) {
				return fixSmallPlotCropIds(dynamic);
			} else {
				return "ViDF".equals(string) ? fixLargePlotCropIds(dynamic) : dynamic;
			}
		});
	}

	private static <T> Dynamic<T> fixSmallPlotCropIds(Dynamic<T> dynamic) {
		dynamic = fixCropId(dynamic, "CA");
		return fixCropId(dynamic, "CB");
	}

	private static <T> Dynamic<T> fixLargePlotCropIds(Dynamic<T> dynamic) {
		dynamic = fixCropId(dynamic, "CA");
		dynamic = fixCropId(dynamic, "CB");
		dynamic = fixCropId(dynamic, "CC");
		return fixCropId(dynamic, "CD");
	}

	private static <T> Dynamic<T> fixCropId(Dynamic<T> dynamic, String cropId) {
		return dynamic.get(cropId).asNumber().result().isPresent()
			? dynamic.set(cropId, BlockStateFlattening.lookupState(dynamic.get(cropId).asInt(0) << 4))
			: dynamic;
	}
}
