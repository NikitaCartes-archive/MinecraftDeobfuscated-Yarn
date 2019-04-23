package net.minecraft.resource;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Components;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resource.metadata.PackResourceMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackContainer implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final PackResourceMetadata BROKEN_PACK_META = new PackResourceMetadata(
		new TranslatableComponent("resourcePack.broken_assets").applyFormat(new ChatFormat[]{ChatFormat.field_1061, ChatFormat.field_1056}),
		SharedConstants.getGameVersion().getPackVersion()
	);
	private final String name;
	private final Supplier<ResourcePack> packCreator;
	private final Component displayName;
	private final Component description;
	private final ResourcePackCompatibility compatibility;
	private final ResourcePackContainer.InsertionPosition position;
	private final boolean notSorting;
	private final boolean positionFixed;

	@Nullable
	public static <T extends ResourcePackContainer> T of(
		String string,
		boolean bl,
		Supplier<ResourcePack> supplier,
		ResourcePackContainer.Factory<T> factory,
		ResourcePackContainer.InsertionPosition insertionPosition
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

				var8 = factory.create(string, bl, supplier, resourcePack, packResourceMetadata, insertionPosition);
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
		Component component,
		Component component2,
		ResourcePackCompatibility resourcePackCompatibility,
		ResourcePackContainer.InsertionPosition insertionPosition,
		boolean bl2
	) {
		this.name = string;
		this.packCreator = supplier;
		this.displayName = component;
		this.description = component2;
		this.compatibility = resourcePackCompatibility;
		this.notSorting = bl;
		this.position = insertionPosition;
		this.positionFixed = bl2;
	}

	public ResourcePackContainer(
		String string,
		boolean bl,
		Supplier<ResourcePack> supplier,
		ResourcePack resourcePack,
		PackResourceMetadata packResourceMetadata,
		ResourcePackContainer.InsertionPosition insertionPosition
	) {
		this(
			string,
			bl,
			supplier,
			new TextComponent(resourcePack.getName()),
			packResourceMetadata.getDescription(),
			ResourcePackCompatibility.from(packResourceMetadata.getPackFormat()),
			insertionPosition,
			false
		);
	}

	@Environment(EnvType.CLIENT)
	public Component getDisplayName() {
		return this.displayName;
	}

	@Environment(EnvType.CLIENT)
	public Component getDescription() {
		return this.description;
	}

	public Component getInformationText(boolean bl) {
		return Components.bracketed(new TextComponent(this.name))
			.modifyStyle(
				style -> style.setColor(bl ? ChatFormat.field_1060 : ChatFormat.field_1061)
						.setInsertion(StringArgumentType.escapeIfRequired(this.name))
						.setHoverEvent(new HoverEvent(HoverEvent.Action.field_11762, new TextComponent("").append(this.displayName).append("\n").append(this.description)))
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

	public boolean isPositionFixed() {
		return this.positionFixed;
	}

	public ResourcePackContainer.InsertionPosition getInitialPosition() {
		return this.position;
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
			ResourcePackContainer.InsertionPosition insertionPosition
		);
	}

	public static enum InsertionPosition {
		field_14280,
		field_14281;

		public <T, P extends ResourcePackContainer> int insert(List<T> list, T object, Function<T, P> function, boolean bl) {
			ResourcePackContainer.InsertionPosition insertionPosition = bl ? this.inverse() : this;
			if (insertionPosition == field_14281) {
				int i;
				for (i = 0; i < list.size(); i++) {
					P resourcePackContainer = (P)function.apply(list.get(i));
					if (!resourcePackContainer.isPositionFixed() || resourcePackContainer.getInitialPosition() != this) {
						break;
					}
				}

				list.add(i, object);
				return i;
			} else {
				int i;
				for (i = list.size() - 1; i >= 0; i--) {
					P resourcePackContainer = (P)function.apply(list.get(i));
					if (!resourcePackContainer.isPositionFixed() || resourcePackContainer.getInitialPosition() != this) {
						break;
					}
				}

				list.add(i + 1, object);
				return i + 1;
			}
		}

		public ResourcePackContainer.InsertionPosition inverse() {
			return this == field_14280 ? field_14281 : field_14280;
		}
	}
}
