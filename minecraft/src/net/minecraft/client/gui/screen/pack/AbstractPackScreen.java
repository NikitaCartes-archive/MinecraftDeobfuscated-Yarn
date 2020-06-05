package net.minecraft.client.gui.screen.pack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class AbstractPackScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Text DROP_INFO = new TranslatableText("pack.dropInfo").formatted(Formatting.DARK_GRAY);
	private static final Text FOLDER_INFO = new TranslatableText("pack.folderInfo");
	private final Function<Runnable, ResourcePackOrganizer<?>> field_25467;
	private ResourcePackOrganizer<?> organizer;
	private final Screen parent;
	private boolean dirty;
	private boolean shouldSave;
	private PackListWidget availablePackList;
	private PackListWidget selectedPackList;
	private final Function<MinecraftClient, File> field_25474;
	private ButtonWidget doneButton;

	public AbstractPackScreen(
		Screen parent, TranslatableText title, Function<Runnable, ResourcePackOrganizer<?>> function, Function<MinecraftClient, File> function2
	) {
		super(title);
		this.parent = parent;
		this.field_25467 = function;
		this.organizer = (ResourcePackOrganizer<?>)function.apply(this::organizerUpdated);
		this.field_25474 = function2;
	}

	@Override
	public void removed() {
		if (this.shouldSave) {
			this.shouldSave = false;
			this.organizer.apply(false);
		}
	}

	@Override
	public void onClose() {
		this.client.openScreen(this.parent);
	}

	@Override
	protected void init() {
		this.doneButton = this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 48, 150, 20, ScreenTexts.DONE, buttonWidget -> {
			this.shouldSave = this.dirty;
			this.onClose();
		}));
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 154,
				this.height - 48,
				150,
				20,
				new TranslatableText("pack.openFolder"),
				buttonWidget -> Util.getOperatingSystem().open((File)this.field_25474.apply(this.client)),
				(buttonWidget, matrixStack, i, j) -> this.renderTooltip(matrixStack, FOLDER_INFO, i, j)
			)
		);
		this.availablePackList = new PackListWidget(this.client, 200, this.height, new TranslatableText("pack.available.title"));
		this.availablePackList.setLeftPos(this.width / 2 - 4 - 200);
		this.children.add(this.availablePackList);
		this.selectedPackList = new PackListWidget(this.client, 200, this.height, new TranslatableText("pack.selected.title"));
		this.selectedPackList.setLeftPos(this.width / 2 + 4);
		this.children.add(this.selectedPackList);
		this.updatePackLists();
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

	protected void organizerUpdated() {
		this.updatePackLists();
		this.dirty = true;
	}

	protected void method_29680() {
		this.organizer.apply(true);
		this.organizer = (ResourcePackOrganizer<?>)this.field_25467.apply(this::organizerUpdated);
		this.updatePackLists();
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
			SystemToast.method_29627(minecraftClient, path.toString());
		}
	}

	@Override
	public void method_29638(List<Path> list) {
		String string = (String)list.stream().map(Path::getFileName).map(Path::toString).collect(Collectors.joining(", "));
		this.client.openScreen(new ConfirmScreen(bl -> {
			if (bl) {
				method_29669(this.client, list, ((File)this.field_25474.apply(this.client)).toPath());
				this.method_29680();
			}

			this.client.openScreen(this);
		}, new TranslatableText("pack.dropConfirm"), new LiteralText(string)));
	}
}
