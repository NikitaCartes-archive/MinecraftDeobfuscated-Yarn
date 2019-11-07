package net.minecraft.client.render.block.entity;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.Texts;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class SignBlockEntityRenderer extends BlockEntityRenderer<SignBlockEntity> {
	private final SignBlockEntityRenderer.class_4702 field_21529 = new SignBlockEntityRenderer.class_4702();

	public SignBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	public void method_23083(SignBlockEntity signBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		BlockState blockState = signBlockEntity.getCachedState();
		matrixStack.push();
		float g = 0.6666667F;
		if (blockState.getBlock() instanceof SignBlock) {
			matrixStack.translate(0.5, 0.5, 0.5);
			float h = -((float)((Integer)blockState.get(SignBlock.ROTATION) * 360) / 16.0F);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(h));
			this.field_21529.field_21531.visible = true;
		} else {
			matrixStack.translate(0.5, 0.5, 0.5);
			float h = -((Direction)blockState.get(WallSignBlock.FACING)).asRotation();
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(h));
			matrixStack.translate(0.0, -0.3125, -0.4375);
			this.field_21529.field_21531.visible = false;
		}

		Sprite sprite = this.getSprite(getModelTexture(blockState.getBlock()));
		matrixStack.push();
		matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
		this.field_21529.field_21530.render(matrixStack, vertexConsumer, i, j, sprite);
		this.field_21529.field_21531.render(matrixStack, vertexConsumer, i, j, sprite);
		matrixStack.pop();
		TextRenderer textRenderer = this.blockEntityRenderDispatcher.getTextRenderer();
		float k = 0.010416667F;
		matrixStack.translate(0.0, 0.33333334F, 0.046666667F);
		matrixStack.scale(0.010416667F, -0.010416667F, 0.010416667F);
		int l = signBlockEntity.getTextColor().getSignColor();

		for (int m = 0; m < 4; m++) {
			String string = signBlockEntity.getTextBeingEditedOnRow(m, text -> {
				List<Text> list = Texts.wrapLines(text, 90, textRenderer, false, true);
				return list.isEmpty() ? "" : ((Text)list.get(0)).asFormattedString();
			});
			if (string != null) {
				float n = (float)(-textRenderer.getStringWidth(string) / 2);
				textRenderer.draw(
					string, n, (float)(m * 10 - signBlockEntity.text.length * 5), l, false, matrixStack.method_23760().method_23761(), vertexConsumerProvider, false, 0, i
				);
			}
		}

		matrixStack.pop();
	}

	public static Identifier getModelTexture(Block block) {
		if (block == Blocks.OAK_SIGN || block == Blocks.OAK_WALL_SIGN) {
			return ModelLoader.field_21014;
		} else if (block == Blocks.SPRUCE_SIGN || block == Blocks.SPRUCE_WALL_SIGN) {
			return ModelLoader.field_21015;
		} else if (block == Blocks.BIRCH_SIGN || block == Blocks.BIRCH_WALL_SIGN) {
			return ModelLoader.field_21016;
		} else if (block == Blocks.ACACIA_SIGN || block == Blocks.ACACIA_WALL_SIGN) {
			return ModelLoader.field_21017;
		} else if (block == Blocks.JUNGLE_SIGN || block == Blocks.JUNGLE_WALL_SIGN) {
			return ModelLoader.field_21018;
		} else {
			return block != Blocks.DARK_OAK_SIGN && block != Blocks.DARK_OAK_WALL_SIGN ? ModelLoader.field_21014 : ModelLoader.field_21019;
		}
	}

	@Environment(EnvType.CLIENT)
	public static final class class_4702 extends Model {
		public final ModelPart field_21530 = new ModelPart(64, 32, 0, 0);
		public final ModelPart field_21531;

		public class_4702() {
			super(RenderLayer::getEntityCutoutNoCull);
			this.field_21530.addCuboid(-12.0F, -14.0F, -1.0F, 24.0F, 12.0F, 2.0F, 0.0F);
			this.field_21531 = new ModelPart(64, 32, 0, 14);
			this.field_21531.addCuboid(-1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F, 0.0F);
		}

		@Override
		public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float r, float g, float b) {
			this.field_21530.render(matrixStack, vertexConsumer, i, j, null, r, g, b);
			this.field_21531.render(matrixStack, vertexConsumer, i, j, null, r, g, b);
		}
	}
}
