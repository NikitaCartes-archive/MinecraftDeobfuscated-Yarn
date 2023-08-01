package net.minecraft.resource.metadata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.dynamic.Range;

public record PackResourceMetadata(Text description, int packFormat, Optional<Range<Integer>> supportedFormats) {
	public static final Codec<PackResourceMetadata> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.TEXT.fieldOf("description").forGetter(PackResourceMetadata::description),
					Codec.INT.fieldOf("pack_format").forGetter(PackResourceMetadata::packFormat),
					Range.createCodec(Codec.INT).optionalFieldOf("supported_formats").forGetter(PackResourceMetadata::supportedFormats)
				)
				.apply(instance, PackResourceMetadata::new)
	);
	public static final ResourceMetadataSerializer<PackResourceMetadata> SERIALIZER = ResourceMetadataSerializer.fromCodec("pack", CODEC);
}
