package net.minecraft.client.gui.screen.pack;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.SymlinkWarningScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackOpener;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.path.SymlinkEntry;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class PackScreen extends Screen {
	static final Logger LOGGER = LogUtils.getLogger();
	private static final Text AVAILABLE_TITLE = Text.translatable("pack.available.title");
	private static final Text SELECTED_TITLE = Text.translatable("pack.selected.title");
	private static final Text OPEN_FOLDER = Text.translatable("pack.openFolder");
	private static final int field_32395 = 200;
	private static final Text DROP_INFO = Text.translatable("pack.dropInfo").formatted(Formatting.GRAY);
	private static final Text FOLDER_INFO = Text.translatable("pack.folderInfo");
	private static final int field_32396 = 20;
	private static final Identifier UNKNOWN_PACK = Identifier.ofVanilla("textures/misc/unknown_pack.png");
	private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
	private final ResourcePackOrganizer organizer;
	@Nullable
	private PackScreen.DirectoryWatcher directoryWatcher;
	private long refreshTimeout;
	private PackListWidget availablePackList;
	private PackListWidget selectedPackList;
	private final Path file;
	private ButtonWidget doneButton;
	private final Map<String, Identifier> iconTextures = Maps.<String, Identifier>newHashMap();

	public PackScreen(ResourcePackManager resourcePackManager, Consumer<ResourcePackManager> applier, Path file, Text title) {
		super(title);
		this.organizer = new ResourcePackOrganizer(this::updatePackLists, this::getPackIconTexture, resourcePackManager, applier);
		this.file = file;
		this.directoryWatcher = PackScreen.DirectoryWatcher.create(file);
	}

	@Override
	public void close() {
		this.organizer.apply();
		this.closeDirectoryWatcher();
	}

	private void closeDirectoryWatcher() {
		if (this.directoryWatcher != null) {
			try {
				this.directoryWatcher.close();
				this.directoryWatcher = null;
			} catch (Exception var2) {
			}
		}
	}

	@Override
	protected void init() {
		DirectionalLayoutWidget directionalLayoutWidget = this.layout.addHeader(DirectionalLayoutWidget.vertical().spacing(5));
		directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
		directionalLayoutWidget.add(new TextWidget(this.getTitle(), this.textRenderer));
		directionalLayoutWidget.add(new TextWidget(DROP_INFO, this.textRenderer));
		this.availablePackList = this.addDrawableChild(new PackListWidget(this.client, this, 200, this.height - 66, AVAILABLE_TITLE));
		this.selectedPackList = this.addDrawableChild(new PackListWidget(this.client, this, 200, this.height - 66, SELECTED_TITLE));
		DirectionalLayoutWidget directionalLayoutWidget2 = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
		directionalLayoutWidget2.add(ButtonWidget.builder(OPEN_FOLDER, button -> Util.getOperatingSystem().open(this.file)).tooltip(Tooltip.of(FOLDER_INFO)).build());
		this.doneButton = directionalLayoutWidget2.add(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).build());
		this.refresh();
		this.layout.forEachChild(element -> {
			ClickableWidget var10000 = this.addDrawableChild(element);
		});
		this.initTabNavigation();
	}

	@Override
	protected void initTabNavigation() {
		this.layout.refreshPositions();
		this.availablePackList.position(200, this.layout);
		this.availablePackList.setX(this.width / 2 - 15 - 200);
		this.selectedPackList.position(200, this.layout);
		this.selectedPackList.setX(this.width / 2 + 15);
	}

	@Override
	public void tick() {
		if (this.directoryWatcher != null) {
			try {
				if (this.directoryWatcher.pollForChange()) {
					this.refreshTimeout = 20L;
				}
			} catch (IOException var2) {
				LOGGER.warn("Failed to poll for directory {} changes, stopping", this.file);
				this.closeDirectoryWatcher();
			}
		}

		if (this.refreshTimeout > 0L && --this.refreshTimeout == 0L) {
			this.refresh();
		}
	}

	private void updatePackLists() {
		this.updatePackList(this.selectedPackList, this.organizer.getEnabledPacks());
		this.updatePackList(this.availablePackList, this.organizer.getDisabledPacks());
		this.doneButton.active = !this.selectedPackList.children().isEmpty();
	}

	private void updatePackList(PackListWidget widget, Stream<ResourcePackOrganizer.Pack> packs) {
		widget.children().clear();
		PackListWidget.ResourcePackEntry resourcePackEntry = widget.getSelectedOrNull();
		String string = resourcePackEntry == null ? "" : resourcePackEntry.getName();
		widget.setSelected(null);
		packs.forEach(pack -> {
			PackListWidget.ResourcePackEntry resourcePackEntryx = new PackListWidget.ResourcePackEntry(this.client, widget, pack);
			widget.children().add(resourcePackEntryx);
			if (pack.getName().equals(string)) {
				widget.setSelected(resourcePackEntryx);
			}
		});
	}

	public void switchFocusedList(PackListWidget listWidget) {
		PackListWidget packListWidget = this.selectedPackList == listWidget ? this.availablePackList : this.selectedPackList;
		this.switchFocus(GuiNavigationPath.of(packListWidget.getFirst(), packListWidget, this));
	}

	public void clearSelection() {
		this.selectedPackList.setSelected(null);
		this.availablePackList.setSelected(null);
	}

	private void refresh() {
		this.organizer.refresh();
		this.updatePackLists();
		this.refreshTimeout = 0L;
		this.iconTextures.clear();
	}

	protected static void copyPacks(MinecraftClient client, List<Path> srcPaths, Path destPath) {
		MutableBoolean mutableBoolean = new MutableBoolean();
		srcPaths.forEach(src -> {
			try {
				Stream<Path> stream = Files.walk(src);

				try {
					stream.forEach(toCopy -> {
						try {
							Util.relativeCopy(src.getParent(), destPath, toCopy);
						} catch (IOException var5) {
							LOGGER.warn("Failed to copy datapack file  from {} to {}", toCopy, destPath, var5);
							mutableBoolean.setTrue();
						}
					});
				} catch (Throwable var7) {
					if (stream != null) {
						try {
							stream.close();
						} catch (Throwable var6) {
							var7.addSuppressed(var6);
						}
					}

					throw var7;
				}

				if (stream != null) {
					stream.close();
				}
			} catch (IOException var8) {
				LOGGER.warn("Failed to copy datapack file from {} to {}", src, destPath);
				mutableBoolean.setTrue();
			}
		});
		if (mutableBoolean.isTrue()) {
			SystemToast.addPackCopyFailure(client, destPath.toString());
		}
	}

	@Override
	public void filesDragged(List<Path> paths) {
		String string = (String)streamFileNames(paths).collect(Collectors.joining(", "));
		this.client
			.setScreen(
				new ConfirmScreen(
					confirmed -> {
						if (confirmed) {
							List<Path> list2 = new ArrayList(paths.size());
							Set<Path> set = new HashSet(paths);
							ResourcePackOpener<Path> resourcePackOpener = new ResourcePackOpener<Path>(this.client.getSymlinkFinder()) {
								protected Path openZip(Path path) {
									return path;
								}

								protected Path openDirectory(Path path) {
									return path;
								}
							};
							List<SymlinkEntry> list3 = new ArrayList();

							for (Path path : paths) {
								try {
									Path path2 = resourcePackOpener.open(path, list3);
									if (path2 == null) {
										LOGGER.warn("Path {} does not seem like pack", path);
									} else {
										list2.add(path2);
										set.remove(path2);
									}
								} catch (IOException var10) {
									LOGGER.warn("Failed to check {} for packs", path, var10);
								}
							}

							if (!list3.isEmpty()) {
								this.client.setScreen(SymlinkWarningScreen.pack(() -> this.client.setScreen(this)));
								return;
							}

							if (!list2.isEmpty()) {
								copyPacks(this.client, list2, this.file);
								this.refresh();
							}

							if (!set.isEmpty()) {
								String stringx = (String)streamFileNames(set).collect(Collectors.joining(", "));
								this.client
									.setScreen(
										new NoticeScreen(
											() -> this.client.setScreen(this), Text.translatable("pack.dropRejected.title"), Text.translatable("pack.dropRejected.message", stringx)
										)
									);
								return;
							}
						}

						this.client.setScreen(this);
					},
					Text.translatable("pack.dropConfirm"),
					Text.literal(string)
				)
			);
	}

	private static Stream<String> streamFileNames(Collection<Path> paths) {
		return paths.stream().map(Path::getFileName).map(Path::toString);
	}

	private Identifier loadPackIcon(TextureManager textureManager, ResourcePackProfile resourcePackProfile) {
		try {
			Identifier var9;
			try (ResourcePack resourcePack = resourcePackProfile.createResourcePack()) {
				InputSupplier<InputStream> inputSupplier = resourcePack.openRoot("pack.png");
				if (inputSupplier == null) {
					return UNKNOWN_PACK;
				}

				String string = resourcePackProfile.getId();
				Identifier identifier = Identifier.ofVanilla(
					"pack/" + Util.replaceInvalidChars(string, Identifier::isPathCharacterValid) + "/" + Hashing.sha1().hashUnencodedChars(string) + "/icon"
				);
				InputStream inputStream = inputSupplier.get();

				try {
					NativeImage nativeImage = NativeImage.read(inputStream);
					textureManager.registerTexture(identifier, new NativeImageBackedTexture(nativeImage));
					var9 = identifier;
				} catch (Throwable var12) {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (Throwable var11) {
							var12.addSuppressed(var11);
						}
					}

					throw var12;
				}

				if (inputStream != null) {
					inputStream.close();
				}
			}

			return var9;
		} catch (Exception var14) {
			LOGGER.warn("Failed to load icon from pack {}", resourcePackProfile.getId(), var14);
			return UNKNOWN_PACK;
		}
	}

	private Identifier getPackIconTexture(ResourcePackProfile resourcePackProfile) {
		return (Identifier)this.iconTextures
			.computeIfAbsent(resourcePackProfile.getId(), profileName -> this.loadPackIcon(this.client.getTextureManager(), resourcePackProfile));
	}

	@Environment(EnvType.CLIENT)
	static class DirectoryWatcher implements AutoCloseable {
		private final WatchService watchService;
		private final Path path;

		public DirectoryWatcher(Path path) throws IOException {
			this.path = path;
			this.watchService = path.getFileSystem().newWatchService();

			try {
				this.watchDirectory(path);
				DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);

				try {
					for (Path path2 : directoryStream) {
						if (Files.isDirectory(path2, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
							this.watchDirectory(path2);
						}
					}
				} catch (Throwable var6) {
					if (directoryStream != null) {
						try {
							directoryStream.close();
						} catch (Throwable var5) {
							var6.addSuppressed(var5);
						}
					}

					throw var6;
				}

				if (directoryStream != null) {
					directoryStream.close();
				}
			} catch (Exception var7) {
				this.watchService.close();
				throw var7;
			}
		}

		@Nullable
		public static PackScreen.DirectoryWatcher create(Path path) {
			try {
				return new PackScreen.DirectoryWatcher(path);
			} catch (IOException var2) {
				PackScreen.LOGGER.warn("Failed to initialize pack directory {} monitoring", path, var2);
				return null;
			}
		}

		private void watchDirectory(Path path) throws IOException {
			path.register(this.watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
		}

		public boolean pollForChange() throws IOException {
			boolean bl = false;

			WatchKey watchKey;
			while ((watchKey = this.watchService.poll()) != null) {
				for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
					bl = true;
					if (watchKey.watchable() == this.path && watchEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
						Path path = this.path.resolve((Path)watchEvent.context());
						if (Files.isDirectory(path, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
							this.watchDirectory(path);
						}
					}
				}

				watchKey.reset();
			}

			return bl;
		}

		public void close() throws IOException {
			this.watchService.close();
		}
	}
}
