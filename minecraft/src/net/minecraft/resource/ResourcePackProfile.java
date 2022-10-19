package net.minecraft.resource;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.metadata.PackFeatureSetMetadata;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
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
	private final Text description;
	private final ResourcePackCompatibility compatibility;
	private final FeatureSet requestedFeatures;
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
		ResourcePackProfile.Metadata metadata = loadMetadata(name, packFactory);
		return metadata != null ? of(name, displayName, alwaysEnabled, packFactory, metadata, type, position, false, source) : null;
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
		ResourceType type,
		ResourcePackProfile.InsertionPosition position,
		boolean pinned,
		ResourcePackSource source
	) {
		return new ResourcePackProfile(name, alwaysEnabled, packFactory, displayName, metadata, metadata.getCompatibility(type), position, pinned, source);
	}

	private ResourcePackProfile(
		String name,
		boolean alwaysEnabled,
		ResourcePackProfile.PackFactory packFactory,
		Text displayName,
		ResourcePackProfile.Metadata metadata,
		ResourcePackCompatibility compatibility,
		ResourcePackProfile.InsertionPosition position,
		boolean pinned,
		ResourcePackSource source
	) {
		this.name = name;
		this.packFactory = packFactory;
		this.displayName = displayName;
		this.description = metadata.description();
		this.compatibility = compatibility;
		this.requestedFeatures = metadata.requestedFeatures();
		this.alwaysEnabled = alwaysEnabled;
		this.position = position;
		this.pinned = pinned;
		this.source = source;
	}

	@Nullable
	public static ResourcePackProfile.Metadata loadMetadata(String name, ResourcePackProfile.PackFactory packFactory) {
		try {
			ResourcePackProfile.Metadata var6;
			try (ResourcePack resourcePack = packFactory.open(name)) {
				PackResourceMetadata packResourceMetadata = resourcePack.parseMetadata(PackResourceMetadata.SERIALIZER);
				if (packResourceMetadata == null) {
					LOGGER.warn("Missing metadata in pack {}", name);
					return null;
				}

				PackFeatureSetMetadata packFeatureSetMetadata = resourcePack.parseMetadata(PackFeatureSetMetadata.SERIALIZER);
				FeatureSet featureSet = packFeatureSetMetadata != null ? packFeatureSetMetadata.flags() : FeatureSet.empty();
				var6 = new ResourcePackProfile.Metadata(packResourceMetadata.getDescription(), packResourceMetadata.getPackFormat(), featureSet);
			}

			return var6;
		} catch (Exception var9) {
			LOGGER.warn("Failed to read pack metadata", (Throwable)var9);
			return null;
		}
	}

	public Text getDisplayName() {
		return this.displayName;
	}

	public Text getDescription() {
		return this.description;
	}

	public Text getInformationText(boolean enabled) {
		return Texts.bracketed(this.source.decorate(Text.literal(this.name)))
			.styled(
				style -> style.withColor(enabled ? Formatting.GREEN : Formatting.RED)
						.withInsertion(StringArgumentType.escapeIfRequired(this.name))
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.empty().append(this.displayName).append("\n").append(this.description)))
			);
	}

	public ResourcePackCompatibility getCompatibility() {
		return this.compatibility;
	}

	public FeatureSet getRequestedFeatures() {
		return this.requestedFeatures;
	}

	public ResourcePack createResourcePack() {
		return this.packFactory.open(this.name);
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

	public static record Metadata(Text description, int format, FeatureSet requestedFeatures) {
		public ResourcePackCompatibility getCompatibility(ResourceType type) {
			return ResourcePackCompatibility.from(this.format, type);
		}
	}

	@FunctionalInterface
	public interface PackFactory {
		ResourcePack open(String name);
	}
}
