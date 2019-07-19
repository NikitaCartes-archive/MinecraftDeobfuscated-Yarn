package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
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

	private <T> Dynamic<T> method_5152(Dynamic<T> tag) {
		return tag.update("Children", SavedDataVillageCropFix::method_5157);
	}

	private static <T> Dynamic<T> method_5157(Dynamic<T> tag) {
		return (Dynamic<T>)tag.asStreamOpt().map(SavedDataVillageCropFix::fixVillageChildren).map(tag::createList).orElse(tag);
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

	private static <T> Dynamic<T> fixSmallPlotCropIds(Dynamic<T> tag) {
		tag = fixCropId(tag, "CA");
		return fixCropId(tag, "CB");
	}

	private static <T> Dynamic<T> fixLargePlotCropIds(Dynamic<T> tag) {
		tag = fixCropId(tag, "CA");
		tag = fixCropId(tag, "CB");
		tag = fixCropId(tag, "CC");
		return fixCropId(tag, "CD");
	}

	private static <T> Dynamic<T> fixCropId(Dynamic<T> tag, String cropId) {
		return tag.get(cropId).asNumber().isPresent() ? tag.set(cropId, BlockStateFlattening.lookupState(tag.get(cropId).asInt(0) << 4)) : tag;
	}
}
