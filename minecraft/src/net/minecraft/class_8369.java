package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Codecs;

public record class_8369(Text displayName, long start, long duration, List<class_8390.class_8391> cost) {
	public static final MapCodec<class_8369> field_43981 = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codecs.TEXT.fieldOf("display_name").forGetter(class_8369::displayName),
					Codec.LONG.fieldOf("start").forGetter(class_8369::start),
					Codec.LONG.fieldOf("duration").forGetter(class_8369::duration),
					class_8390.class_8391.field_44034.listOf().fieldOf("cost").forGetter(class_8369::cost)
				)
				.apply(instance, class_8369::new)
	);

	public long method_50461() {
		return this.start + this.duration;
	}
}
