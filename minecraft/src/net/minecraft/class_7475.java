package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.tag.TagEntry;

public record class_7475(List<TagEntry> entries, boolean replace) {
	public static final Codec<class_7475> field_39269 = RecordCodecBuilder.create(
		instance -> instance.group(
					TagEntry.field_39265.listOf().fieldOf("values").forGetter(class_7475::entries),
					Codec.BOOL.optionalFieldOf("replace", Boolean.valueOf(false)).forGetter(class_7475::replace)
				)
				.apply(instance, class_7475::new)
	);
}
