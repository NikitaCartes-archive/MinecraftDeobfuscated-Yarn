package net.minecraft.client.gui.widget;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class PlayerSkinWidget extends ClickableWidget {
	private static final float field_45996 = 0.0625F;
	private static final float field_45997 = 2.125F;
	private static final float field_45998 = 100.0F;
	private static final float field_45999 = 2.5F;
	private static final float field_46000 = -5.0F;
	private static final float field_46001 = 30.0F;
	private static final float field_46002 = 50.0F;
	private final PlayerSkinWidget.Models models;
	private final Supplier<SkinTextures> skinSupplier;
	private float xRotation = -5.0F;
	private float yRotation = 30.0F;

	public PlayerSkinWidget(int width, int height, EntityModelLoader entityModelLoader, Supplier<SkinTextures> skinSupplier) {
		super(0, 0, width, height, ScreenTexts.EMPTY);
		this.models = PlayerSkinWidget.Models.create(entityModelLoader);
		this.skinSupplier = skinSupplier;
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		context.getMatrices().push();
		context.getMatrices().translate((float)this.getX() + (float)this.getWidth() / 2.0F, (float)(this.getY() + this.getHeight()), 100.0F);
		float f = (float)this.getHeight() / 2.125F;
		context.getMatrices().scale(f, f, f);
		context.getMatrices().translate(0.0F, -0.0625F, 0.0F);
		context.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(this.xRotation), 0.0F, -1.0625F, 0.0F);
		context.getMatrices().multiply(RotationAxis.POSITIVE_Y.rotationDegrees(this.yRotation));
		context.draw();
		DiffuseLighting.method_56819(RotationAxis.POSITIVE_X.rotationDegrees(this.xRotation));
		this.models.draw(context, (SkinTextures)this.skinSupplier.get());
		context.draw();
		DiffuseLighting.enableGuiDepthLighting();
		context.getMatrices().pop();
	}

	@Override
	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
		this.xRotation = MathHelper.clamp(this.xRotation - (float)deltaY * 2.5F, -50.0F, 50.0F);
		this.yRotation += (float)deltaX * 2.5F;
	}

	@Override
	public void playDownSound(SoundManager soundManager) {
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}

	@Override
	public boolean isNarratable() {
		return false;
	}

	@Nullable
	@Override
	public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
		return null;
	}

	@Environment(EnvType.CLIENT)
	static record Models(PlayerEntityModel<?> wideModel, PlayerEntityModel<?> slimModel) {
		public static PlayerSkinWidget.Models create(EntityModelLoader entityModelLoader) {
			PlayerEntityModel<?> playerEntityModel = new PlayerEntityModel(entityModelLoader.getModelPart(EntityModelLayers.PLAYER), false);
			PlayerEntityModel<?> playerEntityModel2 = new PlayerEntityModel(entityModelLoader.getModelPart(EntityModelLayers.PLAYER_SLIM), true);
			playerEntityModel.child = false;
			playerEntityModel2.child = false;
			return new PlayerSkinWidget.Models(playerEntityModel, playerEntityModel2);
		}

		public void draw(DrawContext context, SkinTextures skinTextures) {
			context.getMatrices().push();
			context.getMatrices().scale(1.0F, 1.0F, -1.0F);
			context.getMatrices().translate(0.0F, -1.5F, 0.0F);
			PlayerEntityModel<?> playerEntityModel = skinTextures.model() == SkinTextures.Model.SLIM ? this.slimModel : this.wideModel;
			RenderLayer renderLayer = playerEntityModel.getLayer(skinTextures.texture());
			playerEntityModel.render(context.getMatrices(), context.getVertexConsumers().getBuffer(renderLayer), 15728880, OverlayTexture.DEFAULT_UV);
			context.getMatrices().pop();
		}
	}
}
