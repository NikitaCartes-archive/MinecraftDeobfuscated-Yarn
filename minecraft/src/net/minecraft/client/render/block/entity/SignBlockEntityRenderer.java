package net.minecraft.client.render.block.entity;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.OrderedText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.SignType;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class SignBlockEntityRenderer implements BlockEntityRenderer<SignBlockEntity> {
	private static final String STICK = "stick";
	private static final int GLOWING_BLACK_COLOR = -988212;
	private static final int RENDER_DISTANCE = MathHelper.square(16);
	private final Map<SignType, SignBlockEntityRenderer.SignModel> typeToModel;
	private final TextRenderer textRenderer;

	public SignBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		this.typeToModel = (Map<SignType, SignBlockEntityRenderer.SignModel>)SignType.stream()
			.collect(
				ImmutableMap.toImmutableMap(
					signType -> signType, signType -> new SignBlockEntityRenderer.SignModel(ctx.getLayerModelPart(EntityModelLayers.createSign(signType)))
				)
			);
		this.textRenderer = ctx.getTextRenderer();
	}

	public void render(SignBlockEntity signBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		BlockState blockState = signBlockEntity.getCachedState();
		matrixStack.push();
		float g = 0.6666667F;
		SignType signType = AbstractSignBlock.getSignType(blockState.getBlock());
		SignBlockEntityRenderer.SignModel signModel = (SignBlockEntityRenderer.SignModel)this.typeToModel.get(signType);
		if (blockState.getBlock() instanceof SignBlock) {
			matrixStack.translate(0.5F, 0.5F, 0.5F);
			float h = -RotationPropertyHelper.toDegrees((Integer)blockState.get(SignBlock.ROTATION));
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(h));
			signModel.stick.visible = true;
		} else {
			matrixStack.translate(0.5F, 0.5F, 0.5F);
			float h = -((Direction)blockState.get(WallSignBlock.FACING)).asRotation();
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(h));
			matrixStack.translate(0.0F, -0.3125F, -0.4375F);
			signModel.stick.visible = false;
		}

		this.renderSign(matrixStack, vertexConsumerProvider, i, j, 0.6666667F, signType, signModel);
		this.renderText(signBlockEntity, matrixStack, vertexConsumerProvider, i, 0.6666667F);
	}

	void renderSign(MatrixStack matrices, VertexConsumerProvider verticesProvider, int light, int overlay, float scale, SignType type, Model model) {
		matrices.push();
		matrices.scale(scale, -scale, -scale);
		SpriteIdentifier spriteIdentifier = this.getTextureId(type);
		VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(verticesProvider, model::getLayer);
		this.renderSignModel(matrices, light, overlay, model, vertexConsumer);
		matrices.pop();
	}

	void renderSignModel(MatrixStack matrices, int light, int overlay, Model model, VertexConsumer vertices) {
		SignBlockEntityRenderer.SignModel signModel = (SignBlockEntityRenderer.SignModel)model;
		signModel.root.render(matrices, vertices, light, overlay);
	}

	SpriteIdentifier getTextureId(SignType signType) {
		return TexturedRenderLayers.getSignTextureId(signType);
	}

	void renderText(SignBlockEntity blockEntity, MatrixStack matrices, VertexConsumerProvider verticesProvider, int light, float scale) {
		float f = 0.015625F * scale;
		Vec3d vec3d = this.getTextOffset(scale);
		matrices.translate(vec3d.x, vec3d.y, vec3d.z);
		matrices.scale(f, -f, f);
		int i = getColor(blockEntity);
		int j = 4 * blockEntity.getTextLineHeight() / 2;
		OrderedText[] orderedTexts = blockEntity.updateSign(MinecraftClient.getInstance().shouldFilterText(), text -> {
			List<OrderedText> list = this.textRenderer.wrapLines(text, blockEntity.getMaxTextWidth());
			return list.isEmpty() ? OrderedText.EMPTY : (OrderedText)list.get(0);
		});
		int k;
		boolean bl;
		int l;
		if (blockEntity.isGlowingText()) {
			k = blockEntity.getTextColor().getSignColor();
			bl = shouldRender(blockEntity, k);
			l = 15728880;
		} else {
			k = i;
			bl = false;
			l = light;
		}

		for (int m = 0; m < 4; m++) {
			OrderedText orderedText = orderedTexts[m];
			float g = (float)(-this.textRenderer.getWidth(orderedText) / 2);
			if (bl) {
				this.textRenderer
					.drawWithOutline(orderedText, g, (float)(m * blockEntity.getTextLineHeight() - j), k, i, matrices.peek().getPositionMatrix(), verticesProvider, l);
			} else {
				this.textRenderer
					.draw(orderedText, g, (float)(m * blockEntity.getTextLineHeight() - j), k, false, matrices.peek().getPositionMatrix(), verticesProvider, false, 0, l);
			}
		}

		matrices.pop();
	}

	Vec3d getTextOffset(float scale) {
		return new Vec3d(0.0, (double)(0.5F * scale), (double)(0.07F * scale));
	}

	static boolean shouldRender(SignBlockEntity sign, int signColor) {
		if (signColor == DyeColor.BLACK.getSignColor()) {
			return true;
		} else {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
			if (clientPlayerEntity != null && minecraftClient.options.getPerspective().isFirstPerson() && clientPlayerEntity.isUsingSpyglass()) {
				return true;
			} else {
				Entity entity = minecraftClient.getCameraEntity();
				return entity != null && entity.squaredDistanceTo(Vec3d.ofCenter(sign.getPos())) < (double)RENDER_DISTANCE;
			}
		}
	}

	static int getColor(SignBlockEntity sign) {
		int i = sign.getTextColor().getSignColor();
		double d = 0.4;
		int j = (int)((double)NativeImage.getRed(i) * 0.4);
		int k = (int)((double)NativeImage.getGreen(i) * 0.4);
		int l = (int)((double)NativeImage.getBlue(i) * 0.4);
		return i == DyeColor.BLACK.getSignColor() && sign.isGlowingText() ? -988212 : NativeImage.packColor(0, l, k, j);
	}

	public static SignBlockEntityRenderer.SignModel createSignModel(EntityModelLoader entityModelLoader, SignType type) {
		return new SignBlockEntityRenderer.SignModel(entityModelLoader.getModelPart(EntityModelLayers.createSign(type)));
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("sign", ModelPartBuilder.create().uv(0, 0).cuboid(-12.0F, -14.0F, -1.0F, 24.0F, 12.0F, 2.0F), ModelTransform.NONE);
		modelPartData.addChild("stick", ModelPartBuilder.create().uv(0, 14).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F), ModelTransform.NONE);
		return TexturedModelData.of(modelData, 64, 32);
	}

	@Environment(EnvType.CLIENT)
	public static final class SignModel extends Model {
		public final ModelPart root;
		public final ModelPart stick;

		public SignModel(ModelPart root) {
			super(RenderLayer::getEntityCutoutNoCull);
			this.root = root;
			this.stick = root.getChild("stick");
		}

		@Override
		public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
			this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
		}
	}
}
