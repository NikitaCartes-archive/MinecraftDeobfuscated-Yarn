package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

public record class_8374(Map<class_8373, class_8375> options) {
	public static final Codec<class_8374> field_43990 = RecordCodecBuilder.create(
		instance -> instance.group(Codec.unboundedMap(class_8373.field_43987, class_8375.field_43992).fieldOf("options").forGetter(class_8374::options))
				.apply(instance, class_8374::new)
	);

	public class_8384 method_50503() {
		Map<UUID, class_8388> map = new HashMap();
		List<class_8384.class_8385> list = new ArrayList();

		for (Entry<class_8373, class_8375> entry : this.options.entrySet()) {
			class_8375 lv = (class_8375)entry.getValue();
			list.add(new class_8384.class_8385((class_8373)entry.getKey(), new class_8384.class_8386(lv)));
			lv.voters().forEach((uUID, arg) -> map.compute(uUID, (uUIDx, arg2) -> class_8388.method_50581(arg2, arg.displayName(), arg.voteCount())));
		}

		return new class_8384(new class_8384.class_8386(new class_8375(map)), List.copyOf(list));
	}
}
