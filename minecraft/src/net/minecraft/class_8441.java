package net.minecraft;

import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class class_8441 extends Screen {
	private static final int field_44308 = 40;
	public static final int field_44307 = 320;
	private static final int field_44309 = 8;
	private static final int field_44310 = 150;
	private static final int field_44311 = 20;
	private static final MutableText field_44312 = Text.translatable("chat.copy");
	private final Screen field_44313;
	private final List<class_8440> field_44314;
	private class_8441.class_8442 field_44315;

	public class_8441(Text text, Screen screen, List<class_8440> list) {
		super(text);
		this.field_44313 = screen;
		this.field_44314 = list;
	}

	@Override
	public void close() {
		this.client.setScreen(this.field_44313);
	}

	@Override
	protected void init() {
		this.field_44315 = new class_8441.class_8442(this.client, this.field_44314);
		this.field_44315.setRenderBackground(false);
		this.addSelectableChild(this.field_44315);
		int i = this.width / 2 - 150 - 5;
		int j = this.width / 2 + 5;
		int k = this.height - 20 - 8;
		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, buttonWidget -> this.close()).dimensions(j, k, 150, 20).build());
		this.addDrawableChild(ButtonWidget.builder(field_44312, buttonWidget -> {
			String string = (String)this.field_44314.stream().filter(arg -> arg.index() == 0L).map(arg -> arg.original().getString()).collect(Collectors.joining("\n"));
			this.client.keyboard.setClipboard(string);
		}).dimensions(i, k, 150, 20).build());
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.field_44315.render(matrices, mouseX, mouseY, delta);
		drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 16, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Environment(EnvType.CLIENT)
	public class class_8442 extends AlwaysSelectedEntryListWidget<class_8441.class_8442.class_8443> {
		public class_8442(MinecraftClient minecraftClient, List<class_8440> list) {
			super(minecraftClient, class_8441.this.width, class_8441.this.height, 40, class_8441.this.height - 40, 18);

			for (class_8440 lv : list) {
				this.addEntry(new class_8441.class_8442.class_8443(lv));
			}
		}

		@Override
		public int getRowWidth() {
			return 320;
		}

		@Override
		protected int getScrollbarPositionX() {
			return this.getRowRight() - 2;
		}

		@Environment(EnvType.CLIENT)
		public class class_8443 extends AlwaysSelectedEntryListWidget.Entry<class_8441.class_8442.class_8443> {
			private final class_8440 field_44318;

			public class_8443(class_8440 arg2) {
				this.field_44318 = arg2;
			}

			@Override
			public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				int i = x + 1 + (this.field_44318.index() > 0L ? 16 : 0);
				int j = y + (entryHeight - 9) / 2 + 1;
				DrawableHelper.drawTextWithShadow(matrices, class_8441.this.textRenderer, this.field_44318.contents(), i, j, -1);
			}

			@Override
			public Text getNarration() {
				return Text.translatable("narrator.select", this.field_44318.original());
			}

			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				if (button == 0) {
					class_8442.this.setSelected(this);
					return true;
				} else {
					return false;
				}
			}
		}
	}
}
