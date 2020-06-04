package net.minecraft;

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
import net.minecraft.client.gui.screen.resourcepack.ResourcePackListWidget;
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
public abstract class class_5375 extends Screen {
	private static final Logger field_25464 = LogManager.getLogger();
	private static final Text field_25465 = new TranslatableText("pack.dropInfo").formatted(Formatting.DARK_GRAY);
	private static final Text field_25466 = new TranslatableText("pack.folderInfo");
	private final Function<Runnable, class_5369<?>> field_25467;
	private class_5369<?> field_25468;
	private final Screen field_25469;
	private boolean field_25470;
	private boolean field_25471;
	private ResourcePackListWidget field_25472;
	private ResourcePackListWidget field_25473;
	private final Function<MinecraftClient, File> field_25474;
	private ButtonWidget field_25475;

	public class_5375(Screen screen, TranslatableText translatableText, Function<Runnable, class_5369<?>> function, Function<MinecraftClient, File> function2) {
		super(translatableText);
		this.field_25469 = screen;
		this.field_25467 = function;
		this.field_25468 = (class_5369<?>)function.apply(this::method_29679);
		this.field_25474 = function2;
	}

	@Override
	public void removed() {
		if (this.field_25471) {
			this.field_25471 = false;
			this.field_25468.method_29642(false);
		}
	}

	@Override
	public void onClose() {
		this.client.openScreen(this.field_25469);
	}

	@Override
	protected void init() {
		this.field_25475 = this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 48, 150, 20, ScreenTexts.DONE, buttonWidget -> {
			this.field_25471 = this.field_25470;
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
				(buttonWidget, matrixStack, i, j) -> this.renderTooltip(matrixStack, field_25466, i, j)
			)
		);
		this.field_25472 = new ResourcePackListWidget(this.client, 200, this.height, new TranslatableText("pack.available.title"));
		this.field_25472.setLeftPos(this.width / 2 - 4 - 200);
		this.children.add(this.field_25472);
		this.field_25473 = new ResourcePackListWidget(this.client, 200, this.height, new TranslatableText("pack.selected.title"));
		this.field_25473.setLeftPos(this.width / 2 + 4);
		this.children.add(this.field_25473);
		this.method_29678();
	}

	private void method_29678() {
		this.method_29673(this.field_25473, this.field_25468.method_29643());
		this.method_29673(this.field_25472, this.field_25468.method_29639());
		this.field_25475.active = !this.field_25473.children().isEmpty();
	}

	private void method_29673(ResourcePackListWidget resourcePackListWidget, Stream<class_5369.class_5371> stream) {
		resourcePackListWidget.children().clear();
		stream.forEach(arg -> resourcePackListWidget.children().add(new ResourcePackListWidget.ResourcePackEntry(this.client, resourcePackListWidget, this, arg)));
	}

	protected void method_29679() {
		this.method_29678();
		this.field_25470 = true;
	}

	protected void method_29680() {
		this.field_25468.method_29642(true);
		this.field_25468 = (class_5369<?>)this.field_25467.apply(this::method_29679);
		this.method_29678();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(0);
		this.field_25472.render(matrices, mouseX, mouseY, delta);
		this.field_25473.render(matrices, mouseX, mouseY, delta);
		this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
		this.drawCenteredText(matrices, this.textRenderer, field_25465, this.width / 2, 20, 16777215);
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
							field_25464.warn("Failed to copy datapack file  from {} to {}", path3, path, var5);
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
				field_25464.warn("Failed to copy datapack file from {} to {}", path2, path);
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
