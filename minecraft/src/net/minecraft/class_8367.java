package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Codecs;

public record class_8367(class_8369 header, Map<class_8373, class_8367.class_8368> options) {
	public static final Codec<class_8367> field_43979 = RecordCodecBuilder.create(
		instance -> instance.group(
					class_8369.field_43981.forGetter(class_8367::header),
					Codec.unboundedMap(class_8373.field_43987, class_8367.class_8368.field_43980).fieldOf("options").forGetter(class_8367::options)
				)
				.apply(instance, class_8367::new)
	);

	public static record class_8368(Text displayName, boolean irregular) {
		public static final Codec<class_8367.class_8368> field_43980 = RecordCodecBuilder.create(
			instance -> instance.group(
						Codecs.TEXT.fieldOf("display_name").forGetter(class_8367.class_8368::displayName),
						Codec.BOOL.fieldOf("irregular").forGetter(class_8367.class_8368::irregular)
					)
					.apply(instance, class_8367.class_8368::new)
		);
	}
}
