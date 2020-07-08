package net.minecraft.client.gui.screen.pack;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class AbstractPackScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Text DROP_INFO = new TranslatableText("pack.dropInfo").formatted(Formatting.DARK_GRAY);
	private static final Text FOLDER_INFO = new TranslatableText("pack.folderInfo");
	private static final Identifier field_25786 = new Identifier("textures/misc/unknown_pack.png");
	private final ResourcePackOrganizer organizer;
	private final Screen parent;
	@Nullable
	private AbstractPackScreen.class_5426 field_25787;
	private long field_25788;
	private PackListWidget availablePackList;
	private PackListWidget selectedPackList;
	private final File field_25474;
	private ButtonWidget doneButton;
	private final Map<String, Identifier> field_25789 = Maps.<String, Identifier>newHashMap();

	public AbstractPackScreen(
		Screen screen, ResourcePackManager resourcePackManager, Consumer<ResourcePackManager> consumer, File file, TranslatableText translatableText
	) {
		super(translatableText);
		this.parent = screen;
		this.organizer = new ResourcePackOrganizer(this::updatePackLists, this::method_30287, resourcePackManager, consumer);
		this.field_25474 = file;
		this.field_25787 = AbstractPackScreen.class_5426.method_30293(file);
	}

	@Override
	public void onClose() {
		this.organizer.apply();
		this.client.openScreen(this.parent);
		this.method_30291();
	}

	private void method_30291() {
		if (this.field_25787 != null) {
			try {
				this.field_25787.close();
				this.field_25787 = null;
			} catch (Exception var2) {
			}
		}
	}

	@Override
	protected void init() {
		this.doneButton = this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 48, 150, 20, ScreenTexts.DONE, buttonWidget -> this.onClose()));
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 154,
				this.height - 48,
				150,
				20,
				new TranslatableText("pack.openFolder"),
				buttonWidget -> Util.getOperatingSystem().open(this.field_25474),
				(buttonWidget, matrixStack, i, j) -> this.renderTooltip(matrixStack, FOLDER_INFO, i, j)
			)
		);
		this.availablePackList = new PackListWidget(this.client, 200, this.height, new TranslatableText("pack.available.title"));
		this.availablePackList.setLeftPos(this.width / 2 - 4 - 200);
		this.children.add(this.availablePackList);
		this.selectedPackList = new PackListWidget(this.client, 200, this.height, new TranslatableText("pack.selected.title"));
		this.selectedPackList.setLeftPos(this.width / 2 + 4);
		this.children.add(this.selectedPackList);
		this.method_29680();
	}

	@Override
	public void tick() {
		if (this.field_25787 != null) {
			try {
				if (this.field_25787.method_30292()) {
					this.field_25788 = 20L;
				}
			} catch (IOException var2) {
				LOGGER.warn("Failed to poll for directory {} changes, stopping", this.field_25474);
				this.method_30291();
			}
		}

		if (this.field_25788 > 0L && --this.field_25788 == 0L) {
			this.method_29680();
		}
	}

	private void updatePackLists() {
		this.updatePackList(this.selectedPackList, this.organizer.getEnabledPacks());
		this.updatePackList(this.availablePackList, this.organizer.getDisabledPacks());
		this.doneButton.active = !this.selectedPackList.children().isEmpty();
	}

	private void updatePackList(PackListWidget widget, Stream<ResourcePackOrganizer.Pack> packs) {
		widget.children().clear();
		packs.forEach(pack -> widget.children().add(new PackListWidget.ResourcePackEntry(this.client, widget, this, pack)));
	}

	private void method_29680() {
		this.organizer.method_29981();
		this.updatePackLists();
		this.field_25788 = 0L;
		this.field_25789.clear();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(0);
		this.availablePackList.render(matrices, mouseX, mouseY, delta);
		this.selectedPackList.render(matrices, mouseX, mouseY, delta);
		this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
		this.drawCenteredText(matrices, this.textRenderer, DROP_INFO, this.width / 2, 20, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}

	protected static void method_29669(MinecraftClient minecraftClient, List<Path> list, Path path) {
		MutableBoolean mutableBoolean = new MutableBoolean();
		list.forEach(path2 -> {
			try {
				Stream<Path> stream = Files.walk(path2);
				Throwable var4 = null;

				try {
					stream.forEach(path3 -> {
						try {
							Util.method_29775(path2.getParent(), path, path3);
						} catch (IOException var5) {
							LOGGER.warn("Failed to copy datapack file  from {} to {}", path3, path, var5);
							mutableBoolean.setTrue();
						}
					});
				} catch (Throwable var14) {
					var4 = var14;
					throw var14;
				} finally {
					if (stream != null) {
						if (var4 != null) {
							try {
								stream.close();
							} catch (Throwable var13) {
								var4.addSuppressed(var13);
							}
						} else {
							stream.close();
						}
					}
				}
			} catch (IOException var16) {
				LOGGER.warn("Failed to copy datapack file from {} to {}", path2, path);
				mutableBoolean.setTrue();
			}
		});
		if (mutableBoolean.isTrue()) {
			SystemToast.addPackCopyFailure(minecraftClient, path.toString());
		}
	}

	@Override
	public void filesDragged(List<Path> paths) {
		String string = (String)paths.stream().map(Path::getFileName).map(Path::toString).collect(Collectors.joining(", "));
		this.client.openScreen(new ConfirmScreen(bl -> {
			if (bl) {
				method_29669(this.client, paths, this.field_25474.toPath());
				this.method_29680();
			}

			this.client.openScreen(this);
		}, new TranslatableText("pack.dropConfirm"), new LiteralText(string)));
	}

	private Identifier method_30289(TextureManager textureManager, ResourcePackProfile resourcePackProfile) {
		try (ResourcePack resourcePack = resourcePackProfile.createResourcePack()) {
			InputStream inputStream = resourcePack.openRoot("pack.png");
			Throwable var6 = null;

			Identifier var10;
			try {
				String string = resourcePackProfile.getName();
				Identifier identifier = new Identifier(
					"minecraft", "pack/" + Util.method_30309(string, Identifier::method_29184) + "/" + Hashing.sha1().hashUnencodedChars(string) + "/icon"
				);
				NativeImage nativeImage = NativeImage.read(inputStream);
				textureManager.registerTexture(identifier, new NativeImageBackedTexture(nativeImage));
				var10 = identifier;
			} catch (Throwable var37) {
				var6 = var37;
				throw var37;
			} finally {
				if (inputStream != null) {
					if (var6 != null) {
						try {
							inputStream.close();
						} catch (Throwable var36) {
							var6.addSuppressed(var36);
						}
					} else {
						inputStream.close();
					}
				}
			}

			return var10;
		} catch (FileNotFoundException var41) {
		} catch (Exception var42) {
			LOGGER.warn("Failed to load icon from pack {}", resourcePackProfile.getName(), var42);
		}

		return field_25786;
	}

	private Identifier method_30287(ResourcePackProfile resourcePackProfile) {
		return (Identifier)this.field_25789
			.computeIfAbsent(resourcePackProfile.getName(), string -> this.method_30289(this.client.getTextureManager(), resourcePackProfile));
	}

	@Environment(EnvType.CLIENT)
	static class class_5426 implements AutoCloseable {
		private final WatchService field_25790;
		private final Path field_25791;

		public class_5426(File file) throws IOException {
			this.field_25791 = file.toPath();
			this.field_25790 = this.field_25791.getFileSystem().newWatchService();

			try {
				this.method_30294(this.field_25791);
				DirectoryStream<Path> directoryStream = Files.newDirectoryStream(this.field_25791);
				Throwable var3 = null;

				try {
					for (Path path : directoryStream) {
						if (Files.isDirectory(path, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
							this.method_30294(path);
						}
					}
				} catch (Throwable var14) {
					var3 = var14;
					throw var14;
				} finally {
					if (directoryStream != null) {
						if (var3 != null) {
							try {
								directoryStream.close();
							} catch (Throwable var13) {
								var3.addSuppressed(var13);
							}
						} else {
							directoryStream.close();
						}
					}
				}
			} catch (Exception var16) {
				this.field_25790.close();
				throw var16;
			}
		}

		@Nullable
		public static AbstractPackScreen.class_5426 method_30293(File file) {
			try {
				return new AbstractPackScreen.class_5426(file);
			} catch (IOException var2) {
				AbstractPackScreen.LOGGER.warn("Failed to initialize pack directory {} monitoring", file, var2);
				return null;
			}
		}

		private void method_30294(Path path) throws IOException {
			path.register(this.field_25790, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
		}

		public boolean method_30292() throws IOException {
			boolean bl = false;

			WatchKey watchKey;
			while ((watchKey = this.field_25790.poll()) != null) {
				for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
					bl = true;
					if (watchKey.watchable() == this.field_25791 && watchEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
						Path path = this.field_25791.resolve((Path)watchEvent.context());
						if (Files.isDirectory(path, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
							this.method_30294(path);
						}
					}
				}

				watchKey.reset();
			}

			return bl;
		}

		public void close() throws IOException {
			this.field_25790.close();
		}
	}
}
