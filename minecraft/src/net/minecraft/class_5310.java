package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class class_5310 {
	public static final Codec<class_5310> field_24817 = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.INT.fieldOf("target").forGetter(class_5310::method_28594),
					Codec.INT.fieldOf("size").forGetter(class_5310::method_28596),
					Codec.INT.fieldOf("offset").forGetter(class_5310::method_28597)
				)
				.apply(instance, class_5310::new)
	);
	private final int field_24818;
	private final int field_24819;
	private final int field_24820;

	public class_5310(int i, int j, int k) {
		this.field_24818 = i;
		this.field_24819 = j;
		this.field_24820 = k;
	}

	public int method_28594() {
		return this.field_24818;
	}

	public int method_28596() {
		return this.field_24819;
	}

	public int method_28597() {
		return this.field_24820;
	}
}
