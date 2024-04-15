package net.minecraft.client.realms.gui.screen;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ScrollableTextWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BuyRealmsScreen extends RealmsScreen {
	private static final Text POPUP_TEXT = Text.translatable("mco.selectServer.popup");
	private static final Text CLOSE_TEXT = Text.translatable("mco.selectServer.close");
	private static final Identifier POPUP_BACKGROUND_TEXTURE = new Identifier("popup/background");
	private static final Identifier TRIAL_AVAILABLE_TEXTURE = new Identifier("icon/trial_available");
	private static final ButtonTextures CROSS_BUTTON_TEXTURES = new ButtonTextures(
		new Identifier("widget/cross_button"), new Identifier("widget/cross_button_highlighted")
	);
	private static final int field_45255 = 236;
	private static final int field_45256 = 34;
	private static final int field_45257 = 6;
	private static final int field_45258 = 195;
	private static final int field_45259 = 152;
	private static final int field_45260 = 4;
	private static final int field_45261 = 10;
	private static final int field_45262 = 320;
	private static final int field_45263 = 172;
	private static final int field_45264 = 100;
	private static final int field_45265 = 99;
	private static final int field_45266 = 100;
	private static List<Identifier> realmsImages = List.of();
	private final Screen parent;
	private final boolean trialAvailable;
	@Nullable
	private ButtonWidget trialButton;
	private int realmsImageIndex;
	private int realmsImageDisplayTime;

	public BuyRealmsScreen(Screen parent, boolean trialAvailable) {
		super(POPUP_TEXT);
		this.parent = parent;
		this.trialAvailable = trialAvailable;
	}

	public static void refreshImages(ResourceManager resourceManager) {
		Collection<Identifier> collection = resourceManager.findResources("textures/gui/images", id -> id.getPath().endsWith(".png")).keySet();
		realmsImages = collection.stream().filter(id -> id.getNamespace().equals("realms")).toList();
	}

	@Override
	protected void init() {
		this.parent.resize(this.client, this.width, this.height);
		if (this.trialAvailable) {
			this.trialButton = this.addDrawableChild(
				ButtonWidget.builder(Text.translatable("mco.selectServer.trial"), ConfirmLinkScreen.opening(this, "https://aka.ms/startjavarealmstrial"))
					.dimensions(this.getRight() - 10 - 99, this.getBottom() - 10 - 4 - 40, 99, 20)
					.build()
			);
		}

		this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("mco.selectServer.buy"), ConfirmLinkScreen.opening(this, "https://aka.ms/BuyJavaRealms"))
				.dimensions(this.getRight() - 10 - 99, this.getBottom() - 10 - 20, 99, 20)
				.build()
		);
		TexturedButtonWidget texturedButtonWidget = this.addDrawableChild(
			new TexturedButtonWidget(this.getLeft() + 4, this.getTop() + 4, 14, 14, CROSS_BUTTON_TEXTURES, button -> this.close(), CLOSE_TEXT)
		);
		texturedButtonWidget.setTooltip(Tooltip.of(CLOSE_TEXT));
		int i = 142 - (this.trialAvailable ? 40 : 20);
		ScrollableTextWidget scrollableTextWidget = new ScrollableTextWidget(this.getRight() - 10 - 100, this.getTop() + 10, 100, i, POPUP_TEXT, this.textRenderer);
		if (scrollableTextWidget.textOverflows()) {
			scrollableTextWidget.setWidth(100 - scrollableTextWidget.getScrollerWidth());
		}

		this.addDrawableChild(scrollableTextWidget);
	}

	@Override
	public void tick() {
		super.tick();
		if (++this.realmsImageDisplayTime > 100) {
			this.realmsImageDisplayTime = 0;
			this.realmsImageIndex = (this.realmsImageIndex + 1) % realmsImages.size();
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		if (this.trialButton != null) {
			drawTrialAvailableTexture(context, this.trialButton);
		}
	}

	public static void drawTrialAvailableTexture(DrawContext context, ButtonWidget button) {
		int i = 8;
		context.getMatrices().push();
		context.getMatrices().translate(0.0F, 0.0F, 110.0F);
		context.drawGuiTexture(TRIAL_AVAILABLE_TEXTURE, button.getX() + button.getWidth() - 8 - 4, button.getY() + button.getHeight() / 2 - 4, 8, 8);
		context.getMatrices().pop();
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		this.parent.render(context, -1, -1, delta);
		context.draw();
		RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
		this.method_59840();
		this.renderInGameBackground(context);
		context.drawGuiTexture(POPUP_BACKGROUND_TEXTURE, this.getLeft(), this.getTop(), 320, 172);
		if (!realmsImages.isEmpty()) {
			context.drawTexture((Identifier)realmsImages.get(this.realmsImageIndex), this.getLeft() + 10, this.getTop() + 10, 0, 0.0F, 0.0F, 195, 152, 195, 152);
		}
	}

	private int getLeft() {
		return (this.width - 320) / 2;
	}

	private int getTop() {
		return (this.height - 172) / 2;
	}

	private int getRight() {
		return this.getLeft() + 320;
	}

	private int getBottom() {
		return this.getTop() + 172;
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}
}
