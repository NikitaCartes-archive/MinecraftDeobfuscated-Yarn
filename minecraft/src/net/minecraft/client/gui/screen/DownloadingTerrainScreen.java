package net.minecraft.client.gui.screen;

import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class DownloadingTerrainScreen extends Screen {
	private static final Text TEXT = Text.translatable("multiplayer.downloadingTerrain");
	private static final long MIN_LOAD_TIME_MS = 30000L;
	private final long loadStartTime;
	private final BooleanSupplier shouldClose;
	private final DownloadingTerrainScreen.class_9678 field_51485;
	@Nullable
	private Sprite field_51486;

	public DownloadingTerrainScreen(BooleanSupplier shouldClose, DownloadingTerrainScreen.class_9678 arg) {
		super(NarratorManager.EMPTY);
		this.shouldClose = shouldClose;
		this.field_51485 = arg;
		this.loadStartTime = System.currentTimeMillis();
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	protected boolean hasUsageText() {
		return false;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, TEXT, this.width / 2, this.height / 2 - 50, 16777215);
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		switch (this.field_51485) {
			case NETHER_PORTAL:
				context.drawSprite(0, 0, -90, context.getScaledWindowWidth(), context.getScaledWindowHeight(), this.method_59838());
				break;
			case END_PORTAL:
				context.fillWithLayer(RenderLayer.getEndPortal(), 0, 0, this.width, this.height, 0);
				break;
			case OTHER:
				this.renderPanoramaBackground(context, delta);
				this.applyBlur(delta);
				this.renderDarkening(context);
		}
	}

	private Sprite method_59838() {
		if (this.field_51486 != null) {
			return this.field_51486;
		} else {
			this.field_51486 = this.client.getBlockRenderManager().getModels().getModelParticleSprite(Blocks.NETHER_PORTAL.getDefaultState());
			return this.field_51486;
		}
	}

	@Override
	public void tick() {
		if (this.shouldClose.getAsBoolean() || System.currentTimeMillis() > this.loadStartTime + 30000L) {
			this.close();
		}
	}

	@Override
	public void close() {
		this.client.getNarratorManager().narrate(Text.translatable("narrator.ready_to_play"));
		super.close();
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	public static enum class_9678 {
		NETHER_PORTAL,
		END_PORTAL,
		OTHER;
	}
}
