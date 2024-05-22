package net.minecraft.client.render.block.entity;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
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
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.OrderedText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class SignBlockEntityRenderer implements BlockEntityRenderer<SignBlockEntity> {
	private static final String STICK = "stick";
	private static final int GLOWING_BLACK_COLOR = -988212;
	private static final int RENDER_DISTANCE = MathHelper.square(16);
	private static final float SCALE = 0.6666667F;
	private static final Vec3d TEXT_OFFSET = new Vec3d(0.0, 0.33333334F, 0.046666667F);
	private final Map<WoodType, SignBlockEntityRenderer.SignModel> typeToModel;
	private final TextRenderer textRenderer;

	public SignBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		this.typeToModel = (Map<WoodType, SignBlockEntityRenderer.SignModel>)WoodType.stream()
			.collect(
				ImmutableMap.toImmutableMap(
					signType -> signType, signType -> new SignBlockEntityRenderer.SignModel(ctx.getLayerModelPart(EntityModelLayers.createSign(signType)))
				)
			);
		this.textRenderer = ctx.getTextRenderer();
	}

	public void render(SignBlockEntity signBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		BlockState blockState = signBlockEntity.getCachedState();
		AbstractSignBlock abstractSignBlock = (AbstractSignBlock)blockState.getBlock();
		WoodType woodType = AbstractSignBlock.getWoodType(abstractSignBlock);
		SignBlockEntityRenderer.SignModel signModel = (SignBlockEntityRenderer.SignModel)this.typeToModel.get(woodType);
		signModel.stick.visible = blockState.getBlock() instanceof SignBlock;
		this.render(signBlockEntity, matrixStack, vertexConsumerProvider, i, j, blockState, abstractSignBlock, woodType, signModel);
	}

	public float getSignScale() {
		return 0.6666667F;
	}

	public float getTextScale() {
		return 0.6666667F;
	}

	void render(
		SignBlockEntity entity,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		int overlay,
		BlockState state,
		AbstractSignBlock block,
		WoodType woodType,
		Model model
	) {
		matrices.push();
		this.setAngles(matrices, -block.getRotationDegrees(state), state);
		this.renderSign(matrices, vertexConsumers, light, overlay, woodType, model);
		this.renderText(entity.getPos(), entity.getFrontText(), matrices, vertexConsumers, light, entity.getTextLineHeight(), entity.getMaxTextWidth(), true);
		this.renderText(entity.getPos(), entity.getBackText(), matrices, vertexConsumers, light, entity.getTextLineHeight(), entity.getMaxTextWidth(), false);
		matrices.pop();
	}

	void setAngles(MatrixStack matrices, float rotationDegrees, BlockState state) {
		matrices.translate(0.5F, 0.75F * this.getSignScale(), 0.5F);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationDegrees));
		if (!(state.getBlock() instanceof SignBlock)) {
			matrices.translate(0.0F, -0.3125F, -0.4375F);
		}
	}

	void renderSign(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, WoodType woodType, Model model) {
		matrices.push();
		float f = this.getSignScale();
		matrices.scale(f, -f, -f);
		SpriteIdentifier spriteIdentifier = this.getTextureId(woodType);
		VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumers, model::getLayer);
		this.renderSignModel(matrices, light, overlay, model, vertexConsumer);
		matrices.pop();
	}

	void renderSignModel(MatrixStack matrices, int light, int overlay, Model model, VertexConsumer vertexConsumers) {
		SignBlockEntityRenderer.SignModel signModel = (SignBlockEntityRenderer.SignModel)model;
		signModel.root.render(matrices, vertexConsumers, light, overlay);
	}

	SpriteIdentifier getTextureId(WoodType signType) {
		return TexturedRenderLayers.getSignTextureId(signType);
	}

	void renderText(
		BlockPos pos, SignText signText, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int lineHeight, int lineWidth, boolean front
	) {
		matrices.push();
		this.setTextAngles(matrices, front, this.getTextOffset());
		int i = getColor(signText);
		int j = 4 * lineHeight / 2;
		OrderedText[] orderedTexts = signText.getOrderedMessages(MinecraftClient.getInstance().shouldFilterText(), text -> {
			List<OrderedText> list = this.textRenderer.wrapLines(text, lineWidth);
			return list.isEmpty() ? OrderedText.EMPTY : (OrderedText)list.get(0);
		});
		int k;
		boolean bl;
		int l;
		if (signText.isGlowing()) {
			k = signText.getColor().getSignColor();
			bl = shouldRender(pos, k);
			l = 15728880;
		} else {
			k = i;
			bl = false;
			l = light;
		}

		for (int m = 0; m < 4; m++) {
			OrderedText orderedText = orderedTexts[m];
			float f = (float)(-this.textRenderer.getWidth(orderedText) / 2);
			if (bl) {
				this.textRenderer.drawWithOutline(orderedText, f, (float)(m * lineHeight - j), k, i, matrices.peek().getPositionMatrix(), vertexConsumers, l);
			} else {
				this.textRenderer
					.draw(
						orderedText,
						f,
						(float)(m * lineHeight - j),
						k,
						false,
						matrices.peek().getPositionMatrix(),
						vertexConsumers,
						TextRenderer.TextLayerType.POLYGON_OFFSET,
						0,
						l
					);
			}
		}

		matrices.pop();
	}

	private void setTextAngles(MatrixStack matrices, boolean front, Vec3d translation) {
		if (!front) {
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
		}

		float f = 0.015625F * this.getTextScale();
		matrices.translate(translation.x, translation.y, translation.z);
		matrices.scale(f, -f, f);
	}

	Vec3d getTextOffset() {
		return TEXT_OFFSET;
	}

	static boolean shouldRender(BlockPos pos, int signColor) {
		if (signColor == DyeColor.BLACK.getSignColor()) {
			return true;
		} else {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
			if (clientPlayerEntity != null && minecraftClient.options.getPerspective().isFirstPerson() && clientPlayerEntity.isUsingSpyglass()) {
				return true;
			} else {
				Entity entity = minecraftClient.getCameraEntity();
				return entity != null && entity.squaredDistanceTo(Vec3d.ofCenter(pos)) < (double)RENDER_DISTANCE;
			}
		}
	}

	public static int getColor(SignText sign) {
		int i = sign.getColor().getSignColor();
		if (i == DyeColor.BLACK.getSignColor() && sign.isGlowing()) {
			return -988212;
		} else {
			double d = 0.4;
			int j = (int)((double)ColorHelper.Argb.getRed(i) * 0.4);
			int k = (int)((double)ColorHelper.Argb.getGreen(i) * 0.4);
			int l = (int)((double)ColorHelper.Argb.getBlue(i) * 0.4);
			return ColorHelper.Argb.getArgb(0, j, k, l);
		}
	}

	public static SignBlockEntityRenderer.SignModel createSignModel(EntityModelLoader entityModelLoader, WoodType type) {
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
		public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
			this.root.render(matrices, vertices, light, overlay, color);
		}
	}
}
