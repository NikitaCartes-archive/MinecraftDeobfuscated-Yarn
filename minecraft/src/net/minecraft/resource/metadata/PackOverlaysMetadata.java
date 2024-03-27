package net.minecraft.resource.metadata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.util.dynamic.Range;

public record PackOverlaysMetadata(List<PackOverlaysMetadata.Entry> overlays) {
	private static final Pattern DIRECTORY_NAME_PATTERN = Pattern.compile("[-_a-zA-Z0-9.]+");
	private static final Codec<PackOverlaysMetadata> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(PackOverlaysMetadata.Entry.CODEC.listOf().fieldOf("entries").forGetter(PackOverlaysMetadata::overlays))
				.apply(instance, PackOverlaysMetadata::new)
	);
	public static final ResourceMetadataSerializer<PackOverlaysMetadata> SERIALIZER = ResourceMetadataSerializer.fromCodec("overlays", CODEC);

	private static DataResult<String> validate(String directoryName) {
		return !DIRECTORY_NAME_PATTERN.matcher(directoryName).matches()
			? DataResult.error(() -> directoryName + " is not accepted directory name")
			: DataResult.success(directoryName);
	}

	public List<String> getAppliedOverlays(int packFormat) {
		return this.overlays.stream().filter(overlay -> overlay.isValid(packFormat)).map(PackOverlaysMetadata.Entry::overlay).toList();
	}

	public static record Entry(Range<Integer> format, String overlay) {
		static final Codec<PackOverlaysMetadata.Entry> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Range.createCodec(Codec.INT).fieldOf("formats").forGetter(PackOverlaysMetadata.Entry::format),
						Codec.STRING.validate(PackOverlaysMetadata::validate).fieldOf("directory").forGetter(PackOverlaysMetadata.Entry::overlay)
					)
					.apply(instance, PackOverlaysMetadata.Entry::new)
		);

		public boolean isValid(int packFormat) {
			return this.format.contains(packFormat);
		}
	}
}
