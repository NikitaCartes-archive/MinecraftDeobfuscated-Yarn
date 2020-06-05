package net.minecraft.client.gui.screen.pack;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DataPackScreen extends AbstractPackScreen {
	private static final Identifier UNKNOWN_PACK_TEXTURE = new Identifier("textures/misc/unknown_pack.png");

	public DataPackScreen(
		Screen parent, DataPackSettings dataPackSettings, BiConsumer<DataPackSettings, ResourcePackManager<ResourcePackProfile>> biConsumer, File file
	) {
		super(
			parent,
			new TranslatableText("dataPack.title"),
			new Function<Runnable, ResourcePackOrganizer<?>>() {
				private DataPackSettings settings = dataPackSettings;
				private final ResourcePackManager<ResourcePackProfile> packManager = new ResourcePackManager<>(
					ResourcePackProfile::new, new VanillaDataPackProvider(), new FileResourcePackProvider(file, ResourcePackSource.field_25347)
				);

				public ResourcePackOrganizer<?> apply(Runnable runnable) {
					this.packManager.scanPacks();
					List<String> list = this.settings.getEnabled();
					List<ResourcePackProfile> list2 = DataPackScreen.method_29630(this.packManager, list.stream());
					List<ResourcePackProfile> list3 = DataPackScreen.method_29630(
						this.packManager, this.packManager.getNames().stream().filter(string -> !list.contains(string))
					);
					return new ResourcePackOrganizer(
						runnable,
						(resourcePackProfile, textureManager) -> textureManager.bindTexture(DataPackScreen.UNKNOWN_PACK_TEXTURE),
						Lists.reverse(list2),
						list3,
						(listx, list2x, bl) -> {
							List<String> list3x = (List<String>)Lists.reverse(listx).stream().map(ResourcePackProfile::getName).collect(ImmutableList.toImmutableList());
							List<String> list4 = (List<String>)list2x.stream().map(ResourcePackProfile::getName).collect(ImmutableList.toImmutableList());
							this.settings = new DataPackSettings(list3x, list4);
							if (!bl) {
								this.packManager.setEnabledProfiles(list3x);
								biConsumer.accept(this.settings, this.packManager);
							}
						}
					);
				}
			},
			minecraftClient -> file
		);
	}

	private static List<ResourcePackProfile> method_29630(ResourcePackManager<ResourcePackProfile> packManager, Stream<String> stream) {
		return (List<ResourcePackProfile>)stream.map(packManager::getProfile).filter(Objects::nonNull).collect(ImmutableList.toImmutableList());
	}
}
