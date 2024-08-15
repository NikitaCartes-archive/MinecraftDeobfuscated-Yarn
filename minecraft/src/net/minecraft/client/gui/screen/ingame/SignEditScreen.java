package net.minecraft.client.gui.screen.ingame;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class SignEditScreen extends AbstractSignEditScreen {
	public static final float BACKGROUND_SCALE = 62.500004F;
	public static final float TEXT_SCALE_MULTIPLIER = 0.9765628F;
	private static final Vector3f TEXT_SCALE = new Vector3f(0.9765628F, 0.9765628F, 0.9765628F);
	@Nullable
	private Model model;

	public SignEditScreen(SignBlockEntity sign, boolean filtered, boolean bl) {
		super(sign, filtered, bl);
	}

	@Override
	protected void init() {
		super.init();
		boolean bl = this.blockEntity.getCachedState().getBlock() instanceof SignBlock;
		this.model = SignBlockEntityRenderer.createSignModel(this.client.getEntityModelLoader(), this.signType, bl);
	}

	@Override
	protected void translateForRender(DrawContext context, BlockState state) {
		super.translateForRender(context, state);
		boolean bl = state.getBlock() instanceof SignBlock;
		if (!bl) {
			context.getMatrices().translate(0.0F, 35.0F, 0.0F);
		}
	}

	@Override
	protected void renderSignBackground(DrawContext context) {
		if (this.model != null) {
			context.getMatrices().translate(0.0F, 31.0F, 0.0F);
			context.getMatrices().scale(62.500004F, 62.500004F, -62.500004F);
			SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getSignTextureId(this.signType);
			VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(context.getVertexConsumers(), this.model::getLayer);
			this.model.render(context.getMatrices(), vertexConsumer, 15728880, OverlayTexture.DEFAULT_UV);
		}
	}

	@Override
	protected Vector3f getTextScale() {
		return TEXT_SCALE;
	}
}
