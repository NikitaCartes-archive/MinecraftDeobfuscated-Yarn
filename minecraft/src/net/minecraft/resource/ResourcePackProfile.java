package net.minecraft.resource;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.metadata.PackFeatureSetMetadata;
import net.minecraft.resource.metadata.PackOverlaysMetadata;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.Range;
import org.slf4j.Logger;

/**
 * Represents a resource pack in a {@link ResourcePackManager}.
 * 
 * <p>Compared to a single-use {@link ResourcePack}, a profile is persistent
 * and serves as {@linkplain #createResourcePack a factory} for the single-use
 * packs. It also contains user-friendly information about resource packs.
 * 
 * <p>The profiles are registered by {@link ResourcePackProvider}s.
 * 
 * <p>Closing the profile doesn't have any effect.
 */
public class ResourcePackProfile {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final String name;
	private final ResourcePackProfile.PackFactory packFactory;
	private final Text displayName;
	private final ResourcePackProfile.Metadata metadata;
	private final ResourcePackProfile.InsertionPosition position;
	private final boolean alwaysEnabled;
	private final boolean pinned;
	private final ResourcePackSource source;

	@Nullable
	public static ResourcePackProfile create(
		String name,
		Text displayName,
		boolean alwaysEnabled,
		ResourcePackProfile.PackFactory packFactory,
		ResourceType type,
		ResourcePackProfile.InsertionPosition position,
		ResourcePackSource source
	) {
		int i = SharedConstants.getGameVersion().getResourceVersion(type);
		ResourcePackProfile.Metadata metadata = loadMetadata(name, packFactory, i);
		return metadata != null ? of(name, displayName, alwaysEnabled, packFactory, metadata, position, false, source) : null;
	}

	/**
	 * Creates a resource pack profile from the given parameters.
	 * 
	 * <p>Compared to calling the factory directly, this utility method obtains the
	 * pack's metadata information from the pack created by the {@code packFactory}.
	 * If the created pack doesn't have metadata information, this method returns
	 * {@code null}.
	 * 
	 * @return the created profile, or {@code null} if missing metadata
	 */
	public static ResourcePackProfile of(
		String name,
		Text displayName,
		boolean alwaysEnabled,
		ResourcePackProfile.PackFactory packFactory,
		ResourcePackProfile.Metadata metadata,
		ResourcePackProfile.InsertionPosition position,
		boolean pinned,
		ResourcePackSource source
	) {
		return new ResourcePackProfile(name, alwaysEnabled, packFactory, displayName, metadata, position, pinned, source);
	}

	private ResourcePackProfile(
		String name,
		boolean alwaysEnabled,
		ResourcePackProfile.PackFactory packFactory,
		Text displayName,
		ResourcePackProfile.Metadata metadata,
		ResourcePackProfile.InsertionPosition position,
		boolean pinned,
		ResourcePackSource source
	) {
		this.name = name;
		this.packFactory = packFactory;
		this.displayName = displayName;
		this.metadata = metadata;
		this.alwaysEnabled = alwaysEnabled;
		this.position = position;
		this.pinned = pinned;
		this.source = source;
	}

	@Nullable
	public static ResourcePackProfile.Metadata loadMetadata(String name, ResourcePackProfile.PackFactory packFactory, int currentPackFormat) {
		try {
			ResourcePackProfile.Metadata var11;
			try (ResourcePack resourcePack = packFactory.open(name)) {
				PackResourceMetadata packResourceMetadata = resourcePack.parseMetadata(PackResourceMetadata.SERIALIZER);
				if (packResourceMetadata == null) {
					LOGGER.warn("Missing metadata in pack {}", name);
					return null;
				}

				PackFeatureSetMetadata packFeatureSetMetadata = resourcePack.parseMetadata(PackFeatureSetMetadata.SERIALIZER);
				FeatureSet featureSet = packFeatureSetMetadata != null ? packFeatureSetMetadata.flags() : FeatureSet.empty();
				Range<Integer> range = getSupportedFormats(name, packResourceMetadata);
				ResourcePackCompatibility resourcePackCompatibility = ResourcePackCompatibility.from(range, currentPackFormat);
				PackOverlaysMetadata packOverlaysMetadata = resourcePack.parseMetadata(PackOverlaysMetadata.SERIALIZER);
				List<String> list = packOverlaysMetadata != null ? packOverlaysMetadata.getAppliedOverlays(currentPackFormat) : List.of();
				var11 = new ResourcePackProfile.Metadata(packResourceMetadata.description(), resourcePackCompatibility, featureSet, list);
			}

			return var11;
		} catch (Exception var14) {
			LOGGER.warn("Failed to read pack {} metadata", name, var14);
			return null;
		}
	}

	private static Range<Integer> getSupportedFormats(String packName, PackResourceMetadata metadata) {
		int i = metadata.packFormat();
		if (metadata.supportedFormats().isEmpty()) {
			return new Range(i);
		} else {
			Range<Integer> range = (Range<Integer>)metadata.supportedFormats().get();
			if (!range.contains(i)) {
				LOGGER.warn("Pack {} declared support for versions {} but declared main format is {}, defaulting to {}", packName, range, i, i);
				return new Range(i);
			} else {
				return range;
			}
		}
	}

	public Text getDisplayName() {
		return this.displayName;
	}

	public Text getDescription() {
		return this.metadata.description();
	}

	public Text getInformationText(boolean enabled) {
		return Texts.bracketed(this.source.decorate(Text.literal(this.name)))
			.styled(
				style -> style.withColor(enabled ? Formatting.GREEN : Formatting.RED)
						.withInsertion(StringArgumentType.escapeIfRequired(this.name))
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.empty().append(this.displayName).append("\n").append(this.metadata.description)))
			);
	}

	public ResourcePackCompatibility getCompatibility() {
		return this.metadata.compatibility();
	}

	public FeatureSet getRequestedFeatures() {
		return this.metadata.requestedFeatures();
	}

	public ResourcePack createResourcePack() {
		return this.packFactory.openWithOverlays(this.name, this.metadata);
	}

	public String getName() {
		return this.name;
	}

	public boolean isAlwaysEnabled() {
		return this.alwaysEnabled;
	}

	public boolean isPinned() {
		return this.pinned;
	}

	public ResourcePackProfile.InsertionPosition getInitialPosition() {
		return this.position;
	}

	public ResourcePackSource getSource() {
		return this.source;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			return !(o instanceof ResourcePackProfile resourcePackProfile) ? false : this.name.equals(resourcePackProfile.name);
		}
	}

	public int hashCode() {
		return this.name.hashCode();
	}

	public static enum InsertionPosition {
		TOP,
		BOTTOM;

		public <T> int insert(List<T> items, T item, Function<T, ResourcePackProfile> profileGetter, boolean listInverted) {
			ResourcePackProfile.InsertionPosition insertionPosition = listInverted ? this.inverse() : this;
			if (insertionPosition == BOTTOM) {
				int i;
				for (i = 0; i < items.size(); i++) {
					ResourcePackProfile resourcePackProfile = (ResourcePackProfile)profileGetter.apply(items.get(i));
					if (!resourcePackProfile.isPinned() || resourcePackProfile.getInitialPosition() != this) {
						break;
					}
				}

				items.add(i, item);
				return i;
			} else {
				int i;
				for (i = items.size() - 1; i >= 0; i--) {
					ResourcePackProfile resourcePackProfile = (ResourcePackProfile)profileGetter.apply(items.get(i));
					if (!resourcePackProfile.isPinned() || resourcePackProfile.getInitialPosition() != this) {
						break;
					}
				}

				items.add(i + 1, item);
				return i + 1;
			}
		}

		public ResourcePackProfile.InsertionPosition inverse() {
			return this == TOP ? BOTTOM : TOP;
		}
	}

	public static record Metadata(Text description, ResourcePackCompatibility compatibility, FeatureSet requestedFeatures, List<String> overlays) {
	}

	public interface PackFactory {
		ResourcePack open(String name);

		ResourcePack openWithOverlays(String name, ResourcePackProfile.Metadata metadata);
	}
}
