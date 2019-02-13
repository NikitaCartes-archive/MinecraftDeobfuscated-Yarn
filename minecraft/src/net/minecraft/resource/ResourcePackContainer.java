package net.minecraft.resource;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.event.HoverEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackContainer implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final PackResourceMetadata BROKEN_PACK_META = new PackResourceMetadata(
		new TranslatableTextComponent("resourcePack.broken_assets").applyFormat(new TextFormat[]{TextFormat.field_1061, TextFormat.field_1056}),
		SharedConstants.getGameVersion().getPackVersion()
	);
	private final String name;
	private final Supplier<ResourcePack> packCreator;
	private final TextComponent displayName;
	private final TextComponent description;
	private final ResourcePackCompatibility compatibility;
	private final ResourcePackContainer.SortingDirection direction;
	private final boolean notSorting;
	private final boolean tillEnd;

	@Nullable
	public static <T extends ResourcePackContainer> T of(
		String string, boolean bl, Supplier<ResourcePack> supplier, ResourcePackContainer.Factory<T> factory, ResourcePackContainer.SortingDirection sortingDirection
	) {
		try {
			ResourcePack resourcePack = (ResourcePack)supplier.get();
			Throwable var6 = null;

			ResourcePackContainer var8;
			try {
				PackResourceMetadata packResourceMetadata = resourcePack.parseMetadata(PackResourceMetadata.READER);
				if (bl && packResourceMetadata == null) {
					LOGGER.error(
						"Broken/missing pack.mcmeta detected, fudging it into existance. Please check that your launcher has downloaded all assets for the game correctly!"
					);
					packResourceMetadata = BROKEN_PACK_META;
				}

				if (packResourceMetadata == null) {
					LOGGER.warn("Couldn't find pack meta for pack {}", string);
					return null;
				}

				var8 = factory.create(string, bl, supplier, resourcePack, packResourceMetadata, sortingDirection);
			} catch (Throwable var19) {
				var6 = var19;
				throw var19;
			} finally {
				if (resourcePack != null) {
					if (var6 != null) {
						try {
							resourcePack.close();
						} catch (Throwable var18) {
							var6.addSuppressed(var18);
						}
					} else {
						resourcePack.close();
					}
				}
			}

			return (T)var8;
		} catch (IOException var21) {
			LOGGER.warn("Couldn't get pack info for: {}", var21.toString());
			return null;
		}
	}

	public ResourcePackContainer(
		String string,
		boolean bl,
		Supplier<ResourcePack> supplier,
		TextComponent textComponent,
		TextComponent textComponent2,
		ResourcePackCompatibility resourcePackCompatibility,
		ResourcePackContainer.SortingDirection sortingDirection,
		boolean bl2
	) {
		this.name = string;
		this.packCreator = supplier;
		this.displayName = textComponent;
		this.description = textComponent2;
		this.compatibility = resourcePackCompatibility;
		this.notSorting = bl;
		this.direction = sortingDirection;
		this.tillEnd = bl2;
	}

	public ResourcePackContainer(
		String string,
		boolean bl,
		Supplier<ResourcePack> supplier,
		ResourcePack resourcePack,
		PackResourceMetadata packResourceMetadata,
		ResourcePackContainer.SortingDirection sortingDirection
	) {
		this(
			string,
			bl,
			supplier,
			new StringTextComponent(resourcePack.getName()),
			packResourceMetadata.getDescription(),
			ResourcePackCompatibility.from(packResourceMetadata.getPackFormat()),
			sortingDirection,
			false
		);
	}

	@Environment(EnvType.CLIENT)
	public TextComponent displayName() {
		return this.displayName;
	}

	@Environment(EnvType.CLIENT)
	public TextComponent getDescription() {
		return this.description;
	}

	public TextComponent getInformationText(boolean bl) {
		return TextFormatter.bracketed(new StringTextComponent(this.name))
			.modifyStyle(
				style -> style.setColor(bl ? TextFormat.field_1060 : TextFormat.field_1061)
						.setInsertion(StringArgumentType.escapeIfRequired(this.name))
						.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("").append(this.displayName).append("\n").append(this.description)))
			);
	}

	public ResourcePackCompatibility getCompatibility() {
		return this.compatibility;
	}

	public ResourcePack createResourcePack() {
		return (ResourcePack)this.packCreator.get();
	}

	public String getName() {
		return this.name;
	}

	public boolean canBeSorted() {
		return this.notSorting;
	}

	public boolean sortsTillEnd() {
		return this.tillEnd;
	}

	public ResourcePackContainer.SortingDirection getSortingDirection() {
		return this.direction;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof ResourcePackContainer)) {
			return false;
		} else {
			ResourcePackContainer resourcePackContainer = (ResourcePackContainer)object;
			return this.name.equals(resourcePackContainer.name);
		}
	}

	public int hashCode() {
		return this.name.hashCode();
	}

	public void close() {
	}

	@FunctionalInterface
	public interface Factory<T extends ResourcePackContainer> {
		@Nullable
		T create(
			String string,
			boolean bl,
			Supplier<ResourcePack> supplier,
			ResourcePack resourcePack,
			PackResourceMetadata packResourceMetadata,
			ResourcePackContainer.SortingDirection sortingDirection
		);
	}

	public static enum SortingDirection {
		field_14280,
		field_14281;

		public <T, P extends ResourcePackContainer> int locate(List<T> list, T object, Function<T, P> function, boolean bl) {
			ResourcePackContainer.SortingDirection sortingDirection = bl ? this.inverse() : this;
			if (sortingDirection == field_14281) {
				int i;
				for (i = 0; i < list.size(); i++) {
					P resourcePackContainer = (P)function.apply(list.get(i));
					if (!resourcePackContainer.sortsTillEnd() || resourcePackContainer.getSortingDirection() != this) {
						break;
					}
				}

				list.add(i, object);
				return i;
			} else {
				int i;
				for (i = list.size() - 1; i >= 0; i--) {
					P resourcePackContainer = (P)function.apply(list.get(i));
					if (!resourcePackContainer.sortsTillEnd() || resourcePackContainer.getSortingDirection() != this) {
						break;
					}
				}

				list.add(i + 1, object);
				return i + 1;
			}
		}

		public ResourcePackContainer.SortingDirection inverse() {
			return this == field_14280 ? field_14281 : field_14280;
		}
	}
}
