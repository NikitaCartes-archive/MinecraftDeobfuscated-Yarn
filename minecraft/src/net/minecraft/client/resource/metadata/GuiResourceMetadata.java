package net.minecraft.client.resource.metadata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Scaling;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.dynamic.Codecs;

@Environment(EnvType.CLIENT)
public record GuiResourceMetadata(Scaling scaling) {
	public static final GuiResourceMetadata DEFAULT = new GuiResourceMetadata(Scaling.STRETCH);
	public static final Codec<GuiResourceMetadata> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Codecs.createStrictOptionalFieldCodec(Scaling.CODEC, "scaling", Scaling.STRETCH).forGetter(GuiResourceMetadata::scaling))
				.apply(instance, GuiResourceMetadata::new)
	);
	public static final ResourceMetadataSerializer<GuiResourceMetadata> SERIALIZER = ResourceMetadataSerializer.fromCodec("gui", CODEC);
}
