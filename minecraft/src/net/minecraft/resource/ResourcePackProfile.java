package net.minecraft.resource;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
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
	private final Supplier<ResourcePack> packFactory;
	private final Text displayName;
	private final Text description;
	private final ResourcePackCompatibility compatibility;
	private final ResourcePackProfile.InsertionPosition position;
	private final boolean alwaysEnabled;
	private final boolean pinned;
	private final ResourcePackSource source;

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
	@Nullable
	public static ResourcePackProfile of(
		String name,
		boolean alwaysEnabled,
		Supplier<ResourcePack> packFactory,
		ResourcePackProfile.Factory profileFactory,
		ResourcePackProfile.InsertionPosition insertionPosition,
		ResourcePackSource packSource
	) {
		try {
			ResourcePackProfile var8;
			try (ResourcePack resourcePack = (ResourcePack)packFactory.get()) {
				PackResourceMetadata packResourceMetadata = resourcePack.parseMetadata(PackResourceMetadata.READER);
				if (packResourceMetadata == null) {
					LOGGER.warn("Couldn't find pack meta for pack {}", name);
					return null;
				}

				var8 = profileFactory.create(name, new LiteralText(resourcePack.getName()), alwaysEnabled, packFactory, packResourceMetadata, insertionPosition, packSource);
			}

			return var8;
		} catch (IOException var11) {
			LOGGER.warn("Couldn't get pack info for: {}", var11.toString());
			return null;
		}
	}

	public ResourcePackProfile(
		String name,
		boolean alwaysEnabled,
		Supplier<ResourcePack> packFactory,
		Text displayName,
		Text description,
		ResourcePackCompatibility compatibility,
		ResourcePackProfile.InsertionPosition direction,
		boolean pinned,
		ResourcePackSource source
	) {
		this.name = name;
		this.packFactory = packFactory;
		this.displayName = displayName;
		this.description = description;
		this.compatibility = compatibility;
		this.alwaysEnabled = alwaysEnabled;
		this.position = direction;
		this.pinned = pinned;
		this.source = source;
	}

	public ResourcePackProfile(
		String name,
		Text displayName,
		boolean alwaysEnabled,
		Supplier<ResourcePack> packFactory,
		PackResourceMetadata metadata,
		ResourceType type,
		ResourcePackProfile.InsertionPosition direction,
		ResourcePackSource source
	) {
		this(name, alwaysEnabled, packFactory, displayName, metadata.getDescription(), ResourcePackCompatibility.from(metadata, type), direction, false, source);
	}

	public Text getDisplayName() {
		return this.displayName;
	}

	public Text getDescription() {
		return this.description;
	}

	public Text getInformationText(boolean enabled) {
		return Texts.bracketed(this.source.decorate(new LiteralText(this.name)))
			.styled(
				style -> style.withColor(enabled ? Formatting.GREEN : Formatting.RED)
						.withInsertion(StringArgumentType.escapeIfRequired(this.name))
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("").append(this.displayName).append("\n").append(this.description)))
			);
	}

	public ResourcePackCompatibility getCompatibility() {
		return this.compatibility;
	}

	public ResourcePack createResourcePack() {
		return (ResourcePack)this.packFactory.get();
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

	/**
	 * A factory for resource pack profiles, somewhat resembling the constructor
	 * of {@link ResourcePackProfile} but allowing more customization.
	 */
	@FunctionalInterface
	public interface Factory {
		/**
		 * Creates a proper resource pack profile from the given parameters.
		 * 
		 * @apiNote Instead of calling this method, users usually call {@link
		 * ResourcePackProfile#of}, which fills some of the parameters for a call to this
		 * method.
		 */
		@Nullable
		ResourcePackProfile create(
			String name,
			Text displayName,
			boolean alwaysEnabled,
			Supplier<ResourcePack> packFactory,
			PackResourceMetadata metadata,
			ResourcePackProfile.InsertionPosition initialPosition,
			ResourcePackSource source
		);
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
}
