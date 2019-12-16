package net.minecraft.client.render.item;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class HeldItemRenderer {
	private static final RenderLayer MAP_BACKGROUND = RenderLayer.getText(new Identifier("textures/map/map_background.png"));
	private static final RenderLayer MAP_BACKGROUND_CHECKERBOARD = RenderLayer.getText(new Identifier("textures/map/map_background_checkerboard.png"));
	private final MinecraftClient client;
	private ItemStack mainHand = ItemStack.EMPTY;
	private ItemStack offHand = ItemStack.EMPTY;
	private float equipProgressMainHand;
	private float prevEquipProgressMainHand;
	private float equipProgressOffHand;
	private float prevEquipProgressOffHand;
	private final EntityRenderDispatcher renderManager;
	private final ItemRenderer itemRenderer;

	public HeldItemRenderer(MinecraftClient client) {
		this.client = client;
		this.renderManager = client.getEntityRenderManager();
		this.itemRenderer = client.getItemRenderer();
	}

	public void renderItem(
		LivingEntity entity,
		ItemStack stack,
		ModelTransformation.Mode renderMode,
		boolean leftHanded,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light
	) {
		if (!stack.isEmpty()) {
			this.itemRenderer.renderItem(entity, stack, renderMode, leftHanded, matrices, vertexConsumers, entity.world, light, OverlayTexture.DEFAULT_UV);
		}
	}

	private float getMapAngle(float tickDelta) {
		float f = 1.0F - tickDelta / 45.0F + 0.1F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		return -MathHelper.cos(f * (float) Math.PI) * 0.5F + 0.5F;
	}

	private void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Arm arm) {
		this.client.getTextureManager().bindTexture(this.client.player.getSkinTexture());
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.<AbstractClientPlayerEntity>getRenderer(this.client.player);
		matrices.push();
		float f = arm == Arm.RIGHT ? 1.0F : -1.0F;
		matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(92.0F));
		matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(45.0F));
		matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(f * -41.0F));
		matrices.translate((double)(f * 0.3F), -1.1F, 0.45F);
		if (arm == Arm.RIGHT) {
			playerEntityRenderer.renderRightArm(matrices, vertexConsumers, light, this.client.player);
		} else {
			playerEntityRenderer.renderLeftArm(matrices, vertexConsumers, light, this.client.player);
		}

		matrices.pop();
	}

	private void renderMapInOneHand(
		MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, Arm arm, float swingProgress, ItemStack stack
	) {
		float f = arm == Arm.RIGHT ? 1.0F : -1.0F;
		matrices.translate((double)(f * 0.125F), -0.125, 0.0);
		if (!this.client.player.isInvisible()) {
			matrices.push();
			matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(f * 10.0F));
			this.renderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
			matrices.pop();
		}

		matrices.push();
		matrices.translate((double)(f * 0.51F), (double)(-0.08F + equipProgress * -1.2F), -0.75);
		float g = MathHelper.sqrt(swingProgress);
		float h = MathHelper.sin(g * (float) Math.PI);
		float i = -0.5F * h;
		float j = 0.4F * MathHelper.sin(g * (float) (Math.PI * 2));
		float k = -0.3F * MathHelper.sin(swingProgress * (float) Math.PI);
		matrices.translate((double)(f * i), (double)(j - 0.3F * h), (double)k);
		matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(h * -45.0F));
		matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(f * h * -30.0F));
		this.renderFirstPersonMap(matrices, vertexConsumers, light, stack);
		matrices.pop();
	}

	private void renderMapInBothHands(
		MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float pitch, float equipProgress, float swingProgress
	) {
		float f = MathHelper.sqrt(swingProgress);
		float g = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
		float h = -0.4F * MathHelper.sin(f * (float) Math.PI);
		matrices.translate(0.0, (double)(-g / 2.0F), (double)h);
		float i = this.getMapAngle(pitch);
		matrices.translate(0.0, (double)(0.04F + equipProgress * -1.2F + i * -0.5F), -0.72F);
		matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(i * -85.0F));
		if (!this.client.player.isInvisible()) {
			matrices.push();
			matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
			this.renderArm(matrices, vertexConsumers, light, Arm.RIGHT);
			this.renderArm(matrices, vertexConsumers, light, Arm.LEFT);
			matrices.pop();
		}

		float j = MathHelper.sin(f * (float) Math.PI);
		matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(j * 20.0F));
		matrices.scale(2.0F, 2.0F, 2.0F);
		this.renderFirstPersonMap(matrices, vertexConsumers, light, this.mainHand);
	}

	private void renderFirstPersonMap(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int swingProgress, ItemStack stack) {
		matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
		matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
		matrices.scale(0.38F, 0.38F, 0.38F);
		matrices.translate(-0.5, -0.5, 0.0);
		matrices.scale(0.0078125F, 0.0078125F, 0.0078125F);
		MapState mapState = FilledMapItem.getOrCreateMapState(stack, this.client.world);
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(mapState == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD);
		Matrix4f matrix4f = matrices.peek().getModel();
		vertexConsumer.vertex(matrix4f, -7.0F, 135.0F, 0.0F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(swingProgress).next();
		vertexConsumer.vertex(matrix4f, 135.0F, 135.0F, 0.0F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(swingProgress).next();
		vertexConsumer.vertex(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(swingProgress).next();
		vertexConsumer.vertex(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(swingProgress).next();
		if (mapState != null) {
			this.client.gameRenderer.getMapRenderer().draw(matrices, vertexConsumers, mapState, false, swingProgress);
		}
	}

	private void renderArmHoldingItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm) {
		boolean bl = arm != Arm.LEFT;
		float f = bl ? 1.0F : -1.0F;
		float g = MathHelper.sqrt(swingProgress);
		float h = -0.3F * MathHelper.sin(g * (float) Math.PI);
		float i = 0.4F * MathHelper.sin(g * (float) (Math.PI * 2));
		float j = -0.4F * MathHelper.sin(swingProgress * (float) Math.PI);
		matrices.translate((double)(f * (h + 0.64000005F)), (double)(i + -0.6F + equipProgress * -0.6F), (double)(j + -0.71999997F));
		matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(f * 45.0F));
		float k = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
		float l = MathHelper.sin(g * (float) Math.PI);
		matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(f * l * 70.0F));
		matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(f * k * -20.0F));
		AbstractClientPlayerEntity abstractClientPlayerEntity = this.client.player;
		this.client.getTextureManager().bindTexture(abstractClientPlayerEntity.getSkinTexture());
		matrices.translate((double)(f * -1.0F), 3.6F, 3.5);
		matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(f * 120.0F));
		matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(200.0F));
		matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(f * -135.0F));
		matrices.translate((double)(f * 5.6F), 0.0, 0.0);
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.<AbstractClientPlayerEntity>getRenderer(abstractClientPlayerEntity);
		if (bl) {
			playerEntityRenderer.renderRightArm(matrices, vertexConsumers, light, abstractClientPlayerEntity);
		} else {
			playerEntityRenderer.renderLeftArm(matrices, vertexConsumers, light, abstractClientPlayerEntity);
		}
	}

	private void applyEatOrDrinkTransformation(MatrixStack matrices, float tickDelta, Arm arm, ItemStack stack) {
		float f = (float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F;
		float g = f / (float)stack.getMaxUseTime();
		if (g < 0.8F) {
			float h = MathHelper.abs(MathHelper.cos(f / 4.0F * (float) Math.PI) * 0.1F);
			matrices.translate(0.0, (double)h, 0.0);
		}

		float h = 1.0F - (float)Math.pow((double)g, 27.0);
		int i = arm == Arm.RIGHT ? 1 : -1;
		matrices.translate((double)(h * 0.6F * (float)i), (double)(h * -0.5F), (double)(h * 0.0F));
		matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)i * h * 90.0F));
		matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(h * 10.0F));
		matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)i * h * 30.0F));
	}

	private void applySwingOffset(MatrixStack matrices, Arm arm, float swingProgress) {
		int i = arm == Arm.RIGHT ? 1 : -1;
		float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
		matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)i * (45.0F + f * -20.0F)));
		float g = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
		matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)i * g * -20.0F));
		matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(g * -80.0F));
		matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)i * -45.0F));
	}

	private void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress) {
		int i = arm == Arm.RIGHT ? 1 : -1;
		matrices.translate((double)((float)i * 0.56F), (double)(-0.52F + equipProgress * -0.6F), -0.72F);
	}

	public void renderItem(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light) {
		float f = player.getHandSwingProgress(tickDelta);
		Hand hand = MoreObjects.firstNonNull(player.preferredHand, Hand.MAIN_HAND);
		float g = MathHelper.lerp(tickDelta, player.prevPitch, player.pitch);
		boolean bl = true;
		boolean bl2 = true;
		if (player.isUsingItem()) {
			ItemStack itemStack = player.getActiveItem();
			if (itemStack.getItem() == Items.BOW || itemStack.getItem() == Items.CROSSBOW) {
				bl = player.getActiveHand() == Hand.MAIN_HAND;
				bl2 = !bl;
			}

			Hand hand2 = player.getActiveHand();
			if (hand2 == Hand.MAIN_HAND) {
				ItemStack itemStack2 = player.getOffHandStack();
				if (itemStack2.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStack2)) {
					bl2 = false;
				}
			}
		} else {
			ItemStack itemStackx = player.getMainHandStack();
			ItemStack itemStack3 = player.getOffHandStack();
			if (itemStackx.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStackx)) {
				bl2 = !bl;
			}

			if (itemStack3.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStack3)) {
				bl = !itemStackx.isEmpty();
				bl2 = !bl;
			}
		}

		float h = MathHelper.lerp(tickDelta, player.lastRenderPitch, player.renderPitch);
		float i = MathHelper.lerp(tickDelta, player.lastRenderYaw, player.renderYaw);
		matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((player.getPitch(tickDelta) - h) * 0.1F));
		matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((player.getYaw(tickDelta) - i) * 0.1F));
		if (bl) {
			float j = hand == Hand.MAIN_HAND ? f : 0.0F;
			float k = 1.0F - MathHelper.lerp(tickDelta, this.prevEquipProgressMainHand, this.equipProgressMainHand);
			this.renderFirstPersonItem(player, tickDelta, g, Hand.MAIN_HAND, j, this.mainHand, k, matrices, vertexConsumers, light);
		}

		if (bl2) {
			float j = hand == Hand.OFF_HAND ? f : 0.0F;
			float k = 1.0F - MathHelper.lerp(tickDelta, this.prevEquipProgressOffHand, this.equipProgressOffHand);
			this.renderFirstPersonItem(player, tickDelta, g, Hand.OFF_HAND, j, this.offHand, k, matrices, vertexConsumers, light);
		}

		vertexConsumers.draw();
	}

	private void renderFirstPersonItem(
		AbstractClientPlayerEntity player,
		float tickDelta,
		float pitch,
		Hand hand,
		float swingProgress,
		ItemStack item,
		float equipProgress,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light
	) {
		boolean bl = hand == Hand.MAIN_HAND;
		Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
		matrices.push();
		if (item.isEmpty()) {
			if (bl && !player.isInvisible()) {
				this.renderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
			}
		} else if (item.getItem() == Items.FILLED_MAP) {
			if (bl && this.offHand.isEmpty()) {
				this.renderMapInBothHands(matrices, vertexConsumers, light, pitch, equipProgress, swingProgress);
			} else {
				this.renderMapInOneHand(matrices, vertexConsumers, light, equipProgress, arm, swingProgress, item);
			}
		} else if (item.getItem() == Items.CROSSBOW) {
			boolean bl2 = CrossbowItem.isCharged(item);
			boolean bl3 = arm == Arm.RIGHT;
			int i = bl3 ? 1 : -1;
			if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
				this.applyEquipOffset(matrices, arm, equipProgress);
				matrices.translate((double)((float)i * -0.4785682F), -0.094387F, 0.05731531F);
				matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-11.935F));
				matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)i * 65.3F));
				matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)i * -9.785F));
				float f = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F);
				float g = f / (float)CrossbowItem.getPullTime(item);
				if (g > 1.0F) {
					g = 1.0F;
				}

				if (g > 0.1F) {
					float h = MathHelper.sin((f - 0.1F) * 1.3F);
					float j = g - 0.1F;
					float k = h * j;
					matrices.translate((double)(k * 0.0F), (double)(k * 0.004F), (double)(k * 0.0F));
				}

				matrices.translate((double)(g * 0.0F), (double)(g * 0.0F), (double)(g * 0.04F));
				matrices.scale(1.0F, 1.0F, 1.0F + g * 0.2F);
				matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((float)i * 45.0F));
			} else {
				float fx = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
				float gx = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) (Math.PI * 2));
				float h = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
				matrices.translate((double)((float)i * fx), (double)gx, (double)h);
				this.applyEquipOffset(matrices, arm, equipProgress);
				this.applySwingOffset(matrices, arm, swingProgress);
				if (bl2 && swingProgress < 0.001F) {
					matrices.translate((double)((float)i * -0.641864F), 0.0, 0.0);
					matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)i * 10.0F));
				}
			}

			this.renderItem(
				player,
				item,
				bl3 ? ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND,
				!bl3,
				matrices,
				vertexConsumers,
				light
			);
		} else {
			boolean bl2 = arm == Arm.RIGHT;
			if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
				int l = bl2 ? 1 : -1;
				switch (item.getUseAction()) {
					case NONE:
						this.applyEquipOffset(matrices, arm, equipProgress);
						break;
					case EAT:
					case DRINK:
						this.applyEatOrDrinkTransformation(matrices, tickDelta, arm, item);
						this.applyEquipOffset(matrices, arm, equipProgress);
						break;
					case BLOCK:
						this.applyEquipOffset(matrices, arm, equipProgress);
						break;
					case BOW:
						this.applyEquipOffset(matrices, arm, equipProgress);
						matrices.translate((double)((float)l * -0.2785682F), 0.18344387F, 0.15731531F);
						matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-13.935F));
						matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)l * 35.3F));
						matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)l * -9.785F));
						float mx = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F);
						float fxx = mx / 20.0F;
						fxx = (fxx * fxx + fxx * 2.0F) / 3.0F;
						if (fxx > 1.0F) {
							fxx = 1.0F;
						}

						if (fxx > 0.1F) {
							float gx = MathHelper.sin((mx - 0.1F) * 1.3F);
							float h = fxx - 0.1F;
							float j = gx * h;
							matrices.translate((double)(j * 0.0F), (double)(j * 0.004F), (double)(j * 0.0F));
						}

						matrices.translate((double)(fxx * 0.0F), (double)(fxx * 0.0F), (double)(fxx * 0.04F));
						matrices.scale(1.0F, 1.0F, 1.0F + fxx * 0.2F);
						matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((float)l * 45.0F));
						break;
					case SPEAR:
						this.applyEquipOffset(matrices, arm, equipProgress);
						matrices.translate((double)((float)l * -0.5F), 0.7F, 0.1F);
						matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-55.0F));
						matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)l * 35.3F));
						matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)l * -9.785F));
						float m = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F);
						float fx = m / 10.0F;
						if (fx > 1.0F) {
							fx = 1.0F;
						}

						if (fx > 0.1F) {
							float gx = MathHelper.sin((m - 0.1F) * 1.3F);
							float h = fx - 0.1F;
							float j = gx * h;
							matrices.translate((double)(j * 0.0F), (double)(j * 0.004F), (double)(j * 0.0F));
						}

						matrices.translate(0.0, 0.0, (double)(fx * 0.2F));
						matrices.scale(1.0F, 1.0F, 1.0F + fx * 0.2F);
						matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((float)l * 45.0F));
				}
			} else if (player.isUsingRiptide()) {
				this.applyEquipOffset(matrices, arm, equipProgress);
				int l = bl2 ? 1 : -1;
				matrices.translate((double)((float)l * -0.4F), 0.8F, 0.3F);
				matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)l * 65.0F));
				matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)l * -85.0F));
			} else {
				float n = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
				float mxx = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) (Math.PI * 2));
				float fxxx = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
				int o = bl2 ? 1 : -1;
				matrices.translate((double)((float)o * n), (double)mxx, (double)fxxx);
				this.applyEquipOffset(matrices, arm, equipProgress);
				this.applySwingOffset(matrices, arm, swingProgress);
			}

			this.renderItem(
				player,
				item,
				bl2 ? ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND,
				!bl2,
				matrices,
				vertexConsumers,
				light
			);
		}

		matrices.pop();
	}

	public void updateHeldItems() {
		this.prevEquipProgressMainHand = this.equipProgressMainHand;
		this.prevEquipProgressOffHand = this.equipProgressOffHand;
		ClientPlayerEntity clientPlayerEntity = this.client.player;
		ItemStack itemStack = clientPlayerEntity.getMainHandStack();
		ItemStack itemStack2 = clientPlayerEntity.getOffHandStack();
		if (clientPlayerEntity.isRiding()) {
			this.equipProgressMainHand = MathHelper.clamp(this.equipProgressMainHand - 0.4F, 0.0F, 1.0F);
			this.equipProgressOffHand = MathHelper.clamp(this.equipProgressOffHand - 0.4F, 0.0F, 1.0F);
		} else {
			float f = clientPlayerEntity.getAttackCooldownProgress(1.0F);
			this.equipProgressMainHand = this.equipProgressMainHand
				+ MathHelper.clamp((Objects.equals(this.mainHand, itemStack) ? f * f * f : 0.0F) - this.equipProgressMainHand, -0.4F, 0.4F);
			this.equipProgressOffHand = this.equipProgressOffHand
				+ MathHelper.clamp((float)(Objects.equals(this.offHand, itemStack2) ? 1 : 0) - this.equipProgressOffHand, -0.4F, 0.4F);
		}

		if (this.equipProgressMainHand < 0.1F) {
			this.mainHand = itemStack;
		}

		if (this.equipProgressOffHand < 0.1F) {
			this.offHand = itemStack2;
		}
	}

	public void resetEquipProgress(Hand hand) {
		if (hand == Hand.MAIN_HAND) {
			this.equipProgressMainHand = 0.0F;
		} else {
			this.equipProgressOffHand = 0.0F;
		}
	}
}
