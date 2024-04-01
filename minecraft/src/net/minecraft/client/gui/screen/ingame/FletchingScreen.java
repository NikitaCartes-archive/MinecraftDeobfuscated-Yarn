package net.minecraft.client.gui.screen.ingame;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.FletchingTableBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.FletchingScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FletchingScreen extends HandledScreen<FletchingScreenHandler> {
	private static final Identifier PROGRESS_TEXTURE = new Identifier("container/fletching/progresss");
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/fletching.png");
	private int processTime = 100;
	private final long openTime;
	@Nullable
	private Text fletchingTitle = null;
	private boolean explored = false;

	public FletchingScreen(FletchingScreenHandler handler, PlayerInventory playerInventory, Text title) {
		super(handler, playerInventory, title);
		this.backgroundWidth += 320;
		this.playerInventoryTitleX += 160;
		this.openTime = MinecraftClient.getInstance().world.getTime();
	}

	@Override
	protected void init() {
		super.init();
		this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.getTitle())) / 2;
	}

	private Text getTitleText(char impurities, char nextLevelImpurities, char quality, boolean explored) {
		Text text = Text.empty()
			.append(FletchingTableBlockEntity.ResinComponent.getQualityTooltip(quality), ", ", FletchingTableBlockEntity.ResinComponent.getImpuritiesTooltip(impurities));
		Text text2 = quality >= 'j'
			? Text.translatable("item.minecraft.amber_gem")
			: Text.empty()
				.append(
					FletchingTableBlockEntity.ResinComponent.getQualityTooltip((char)(quality + 1)),
					", ",
					explored
						? FletchingTableBlockEntity.ResinComponent.getImpuritiesTooltip(nextLevelImpurities)
						: FletchingTableBlockEntity.ResinComponent.getImpuritiesTooltip("unknown")
				);
		return Text.translatable("screen.fletching.title", text, text2);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		int i = this.handler.getProcessTime();
		boolean bl = this.handler.isExplored();
		if (i != 0 && this.fletchingTitle == null || bl != this.explored) {
			this.fletchingTitle = this.getTitleText(this.handler.getImpurities(), this.handler.getNextLevelImpurities(), this.handler.getQuality(), bl);
			this.processTime = i;
			this.explored = bl;
			this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.getTitle())) / 2;
		}

		super.render(context, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(context, mouseX, mouseY);
	}

	@Override
	public Text getTitle() {
		return this.fletchingTitle != null ? this.fletchingTitle : super.getTitle();
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		long l = MinecraftClient.getInstance().world.getTime() - this.openTime;
		l = Math.max(0L, l - 20L);
		if (l > 160L) {
			context.drawTexture(TEXTURE, i, j, 0, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 512, 512);
		} else {
			context.drawTexture(TEXTURE, i + 160, j + 4, 0, 160.0F, 4.0F, this.backgroundWidth - 320, this.backgroundHeight - 4, 512, 512);
			int k = 160 - (int)l;
			context.drawTexture(TEXTURE, i + k, j, 0, 0.0F, 0.0F, 164, 19, 512, 512);
			context.drawTexture(TEXTURE, i + this.backgroundWidth - 160 - k - 4, j, 0, (float)(this.backgroundWidth - 160 - 4), 0.0F, 164, 19, 512, 512);
			context.drawTexture(TEXTURE, i + 160 + 4, j, 0, 164.0F, 0.0F, this.backgroundWidth - 320 - 8, this.backgroundHeight, 512, 512);
		}

		int k = this.handler.getProgress();
		if (k > 0) {
			float f = ((float)k + delta) / (float)this.processTime;
			double d = (Math.PI * 2) * (double)f;
			double e = (1.0 - Math.cos(d)) * 59.0;
			double g = Math.sin(2.0 * d) * 21.0;
			this.drawItem(context, Items.FEATHER.getDefaultStack(), (float)(i + 160 + 79 - 59) + (float)e, (float)(j + 38) + (float)g, (float)d);
			int m = (int)(21.0F * (1.0F - (float)k / (float)this.processTime));
			if (m > 0) {
				context.drawGuiTexture(PROGRESS_TEXTURE, 9, 21, 0, 0, i + 160 + 83, j + 35, 9, m);
			}
		}
	}
}
