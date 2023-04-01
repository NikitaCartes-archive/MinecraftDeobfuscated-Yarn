package net.minecraft.client.gui.screen.ingame.vote;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Comparator;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_8289;
import net.minecraft.class_8290;
import net.minecraft.class_8440;
import net.minecraft.class_8441;
import net.minecraft.class_8444;
import net.minecraft.class_8446;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PendingVotesScreen extends Screen implements class_8444 {
	public static final Identifier TEXTURE = new Identifier("textures/gui/votes.png");
	private static final int field_44337 = 8;
	private static final int field_44338 = 236;
	private static final int field_44339 = 64;
	public static final int field_44336 = 72;
	private static final int field_44340 = 238;
	private static final int field_44341 = 36;
	private static final Text SHOW_CURRENT_RULES_TEXT = Text.translatable("vote.show_current_rules");
	private static final Text CURRENT_RULES_TEXT = Text.translatable("vote.current_rules");
	private class_8446 field_44344;

	public PendingVotesScreen() {
		super(Text.translatable("gui.pending_votes.title"));
	}

	private int method_50976() {
		return Math.max(36, this.height - 128 - 16);
	}

	private int method_50969() {
		return 80 + this.method_50976() - 8;
	}

	private int method_50970() {
		return (this.width - 238) / 2;
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	protected void init() {
		this.field_44344 = new class_8446(this.client.player.networkHandler.method_51017(), this, this.client, this.width, this.height, 68, this.method_50969(), 33);
		this.addSelectableChild(this.field_44344);
		int i = this.method_50970() + 3;
		int j = this.method_50969() + 8 + 4;
		this.addDrawableChild(
			ButtonWidget.builder(
					SHOW_CURRENT_RULES_TEXT,
					buttonWidget -> {
						Stream<Text> stream = Registries.field_44443
							.streamEntries()
							.sorted(Comparator.comparing(reference -> reference.registryKey().getValue()))
							.flatMap(reference -> ((class_8289)reference.value()).method_50119())
							.map(arg -> arg.method_50130(class_8290.APPROVE));
						this.client.setScreen(new class_8441(CURRENT_RULES_TEXT, this, class_8440.method_50950(this.client.textRenderer, stream, 320).toList()));
					}
				)
				.dimensions(i, j, 236, 20)
				.build()
		);
		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, buttonWidget -> this.close()).dimensions(i, j + 20 + 2, 236, 20).build());
		if (!this.client.options.field_44284) {
			this.client.options.field_44284 = true;
			this.client.options.write();
		}
	}

	@Override
	public void renderBackground(MatrixStack matrices) {
		int i = this.method_50970() + 3;
		super.renderBackground(matrices);
		RenderSystem.setShaderTexture(0, TEXTURE);
		drawNineSlicedTexture(matrices, i, 64, 236, this.method_50976() + 16, 8, 236, 34, 1, 1);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.field_44344.render(matrices, mouseX, mouseY, delta);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.client.options.socialInteractionsKey.matchesKey(keyCode, scanCode)) {
			this.client.setScreen(null);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public void method_50959() {
		if (!this.field_44344.method_50968()) {
			this.close();
		}
	}
}
