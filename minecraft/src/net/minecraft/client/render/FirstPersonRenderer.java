package net.minecraft.client.render;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
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
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class FirstPersonRenderer {
	private final MinecraftClient client;
	private ItemStack mainHand = ItemStack.EMPTY;
	private ItemStack offHand = ItemStack.EMPTY;
	private float equipProgressMainHand;
	private float prevEquipProgressMainHand;
	private float equipProgressOffHand;
	private float prevEquipProgressOffHand;
	private final EntityRenderDispatcher renderManager;
	private final ItemRenderer itemRenderer;

	public FirstPersonRenderer(MinecraftClient client) {
		this.client = client;
		this.renderManager = client.getEntityRenderManager();
		this.itemRenderer = client.getItemRenderer();
	}

	public void renderItem(
		LivingEntity livingEntity,
		ItemStack itemStack,
		ModelTransformation.Type type,
		boolean bl,
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider
	) {
		if (!itemStack.isEmpty()) {
			this.itemRenderer
				.method_23177(
					livingEntity,
					itemStack,
					type,
					bl,
					matrixStack,
					vertexConsumerProvider,
					livingEntity.world,
					livingEntity.getLightmapCoordinates(),
					OverlayTexture.DEFAULT_UV
				);
		}
	}

	private float getMapAngle(float f) {
		float g = 1.0F - f / 45.0F + 0.1F;
		g = MathHelper.clamp(g, 0.0F, 1.0F);
		return -MathHelper.cos(g * (float) Math.PI) * 0.5F + 0.5F;
	}

	private void renderArm(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, Arm arm) {
		this.client.getTextureManager().bindTexture(this.client.player.getSkinTexture());
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.<AbstractClientPlayerEntity>getRenderer(this.client.player);
		matrixStack.push();
		float f = arm == Arm.RIGHT ? 1.0F : -1.0F;
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(92.0F));
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(45.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(f * -41.0F));
		matrixStack.translate((double)(f * 0.3F), -1.1F, 0.45F);
		if (arm == Arm.RIGHT) {
			playerEntityRenderer.renderRightArm(matrixStack, vertexConsumerProvider, this.client.player);
		} else {
			playerEntityRenderer.renderLeftArm(matrixStack, vertexConsumerProvider, this.client.player);
		}

		matrixStack.pop();
	}

	private void renderMapInOneHand(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f, Arm arm, float g, ItemStack itemStack) {
		float h = arm == Arm.RIGHT ? 1.0F : -1.0F;
		matrixStack.translate((double)(h * 0.125F), -0.125, 0.0);
		if (!this.client.player.isInvisible()) {
			matrixStack.push();
			matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(h * 10.0F));
			this.renderArmHoldingItem(matrixStack, vertexConsumerProvider, f, g, arm);
			matrixStack.pop();
		}

		matrixStack.push();
		matrixStack.translate((double)(h * 0.51F), (double)(-0.08F + f * -1.2F), -0.75);
		float j = MathHelper.sqrt(g);
		float k = MathHelper.sin(j * (float) Math.PI);
		float l = -0.5F * k;
		float m = 0.4F * MathHelper.sin(j * (float) (Math.PI * 2));
		float n = -0.3F * MathHelper.sin(g * (float) Math.PI);
		matrixStack.translate((double)(h * l), (double)(m - 0.3F * k), (double)n);
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(k * -45.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(h * k * -30.0F));
		this.renderFirstPersonMap(matrixStack, vertexConsumerProvider, i, itemStack);
		matrixStack.pop();
	}

	private void renderMapInBothHands(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f, float g, float h) {
		float j = MathHelper.sqrt(h);
		float k = -0.2F * MathHelper.sin(h * (float) Math.PI);
		float l = -0.4F * MathHelper.sin(j * (float) Math.PI);
		matrixStack.translate(0.0, (double)(-k / 2.0F), (double)l);
		float m = this.getMapAngle(f);
		matrixStack.translate(0.0, (double)(0.04F + g * -1.2F + m * -0.5F), -0.72F);
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(m * -85.0F));
		if (!this.client.player.isInvisible()) {
			matrixStack.push();
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(90.0F));
			this.renderArm(matrixStack, vertexConsumerProvider, Arm.RIGHT);
			this.renderArm(matrixStack, vertexConsumerProvider, Arm.LEFT);
			matrixStack.pop();
		}

		float n = MathHelper.sin(j * (float) Math.PI);
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(n * 20.0F));
		matrixStack.scale(2.0F, 2.0F, 2.0F);
		this.renderFirstPersonMap(matrixStack, vertexConsumerProvider, i, this.mainHand);
	}

	private void renderFirstPersonMap(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, ItemStack itemStack) {
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(180.0F));
		matrixStack.scale(0.38F, 0.38F, 0.38F);
		matrixStack.translate(-0.5, -0.5, 0.0);
		matrixStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getText(MapRenderer.field_21056));
		Matrix4f matrix4f = matrixStack.peekModel();
		vertexConsumer.vertex(matrix4f, -7.0F, 135.0F, 0.0F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(i).next();
		vertexConsumer.vertex(matrix4f, 135.0F, 135.0F, 0.0F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(i).next();
		vertexConsumer.vertex(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(i).next();
		vertexConsumer.vertex(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(i).next();
		MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, this.client.world);
		if (mapState != null) {
			this.client.gameRenderer.getMapRenderer().draw(matrixStack, vertexConsumerProvider, mapState, false, i);
		}
	}

	private void renderArmHoldingItem(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, float f, float g, Arm arm) {
		boolean bl = arm != Arm.LEFT;
		float h = bl ? 1.0F : -1.0F;
		float i = MathHelper.sqrt(g);
		float j = -0.3F * MathHelper.sin(i * (float) Math.PI);
		float k = 0.4F * MathHelper.sin(i * (float) (Math.PI * 2));
		float l = -0.4F * MathHelper.sin(g * (float) Math.PI);
		matrixStack.translate((double)(h * (j + 0.64000005F)), (double)(k + -0.6F + f * -0.6F), (double)(l + -0.71999997F));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(h * 45.0F));
		float m = MathHelper.sin(g * g * (float) Math.PI);
		float n = MathHelper.sin(i * (float) Math.PI);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(h * n * 70.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(h * m * -20.0F));
		AbstractClientPlayerEntity abstractClientPlayerEntity = this.client.player;
		this.client.getTextureManager().bindTexture(abstractClientPlayerEntity.getSkinTexture());
		matrixStack.translate((double)(h * -1.0F), 3.6F, 3.5);
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(h * 120.0F));
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(200.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(h * -135.0F));
		matrixStack.translate((double)(h * 5.6F), 0.0, 0.0);
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.<AbstractClientPlayerEntity>getRenderer(abstractClientPlayerEntity);
		if (bl) {
			playerEntityRenderer.renderRightArm(matrixStack, vertexConsumerProvider, abstractClientPlayerEntity);
		} else {
			playerEntityRenderer.renderLeftArm(matrixStack, vertexConsumerProvider, abstractClientPlayerEntity);
		}
	}

	private void applyEatOrDrinkTransformation(MatrixStack matrixStack, float f, Arm arm, ItemStack itemStack) {
		float g = (float)this.client.player.getItemUseTimeLeft() - f + 1.0F;
		float h = g / (float)itemStack.getMaxUseTime();
		if (h < 0.8F) {
			float i = MathHelper.abs(MathHelper.cos(g / 4.0F * (float) Math.PI) * 0.1F);
			matrixStack.translate(0.0, (double)i, 0.0);
		}

		float i = 1.0F - (float)Math.pow((double)h, 27.0);
		int j = arm == Arm.RIGHT ? 1 : -1;
		matrixStack.translate((double)(i * 0.6F * (float)j), (double)(i * -0.5F), (double)(i * 0.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)j * i * 90.0F));
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(i * 10.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)j * i * 30.0F));
	}

	private void method_3217(MatrixStack matrixStack, Arm arm, float f) {
		int i = arm == Arm.RIGHT ? 1 : -1;
		float g = MathHelper.sin(f * f * (float) Math.PI);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)i * (45.0F + g * -20.0F)));
		float h = MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI);
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)i * h * -20.0F));
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(h * -80.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)i * -45.0F));
	}

	private void applyHandOffset(MatrixStack matrixStack, Arm arm, float f) {
		int i = arm == Arm.RIGHT ? 1 : -1;
		matrixStack.translate((double)((float)i * 0.56F), (double)(-0.52F + f * -0.6F), -0.72F);
	}

	public void method_22976(float f, MatrixStack matrixStack, VertexConsumerProvider.Immediate immediate) {
		ClientPlayerEntity clientPlayerEntity = this.client.player;
		float g = clientPlayerEntity.getHandSwingProgress(f);
		Hand hand = MoreObjects.firstNonNull(clientPlayerEntity.preferredHand, Hand.MAIN_HAND);
		float h = MathHelper.lerp(f, clientPlayerEntity.prevPitch, clientPlayerEntity.pitch);
		boolean bl = true;
		boolean bl2 = true;
		if (clientPlayerEntity.isUsingItem()) {
			ItemStack itemStack = clientPlayerEntity.getActiveItem();
			if (itemStack.getItem() == Items.BOW || itemStack.getItem() == Items.CROSSBOW) {
				bl = clientPlayerEntity.getActiveHand() == Hand.MAIN_HAND;
				bl2 = !bl;
			}

			Hand hand2 = clientPlayerEntity.getActiveHand();
			if (hand2 == Hand.MAIN_HAND) {
				ItemStack itemStack2 = clientPlayerEntity.getOffHandStack();
				if (itemStack2.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStack2)) {
					bl2 = false;
				}
			}
		} else {
			ItemStack itemStackx = clientPlayerEntity.getMainHandStack();
			ItemStack itemStack3 = clientPlayerEntity.getOffHandStack();
			if (itemStackx.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStackx)) {
				bl2 = !bl;
			}

			if (itemStack3.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStack3)) {
				bl = !itemStackx.isEmpty();
				bl2 = !bl;
			}
		}

		float i = MathHelper.lerp(f, clientPlayerEntity.lastRenderPitch, clientPlayerEntity.renderPitch);
		float j = MathHelper.lerp(f, clientPlayerEntity.lastRenderYaw, clientPlayerEntity.renderYaw);
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion((clientPlayerEntity.getPitch(f) - i) * 0.1F));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((clientPlayerEntity.getYaw(f) - j) * 0.1F));
		if (bl) {
			float k = hand == Hand.MAIN_HAND ? g : 0.0F;
			float l = 1.0F - MathHelper.lerp(f, this.prevEquipProgressMainHand, this.equipProgressMainHand);
			this.renderFirstPersonItem(clientPlayerEntity, f, h, Hand.MAIN_HAND, k, this.mainHand, l, matrixStack, immediate);
		}

		if (bl2) {
			float k = hand == Hand.OFF_HAND ? g : 0.0F;
			float l = 1.0F - MathHelper.lerp(f, this.prevEquipProgressOffHand, this.equipProgressOffHand);
			this.renderFirstPersonItem(clientPlayerEntity, f, h, Hand.OFF_HAND, k, this.offHand, l, matrixStack, immediate);
		}

		immediate.draw();
	}

	private void renderFirstPersonItem(
		AbstractClientPlayerEntity player,
		float tickDelta,
		float pitch,
		Hand hand,
		float f,
		ItemStack item,
		float equipProgress,
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider
	) {
		boolean bl = hand == Hand.MAIN_HAND;
		Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
		matrixStack.push();
		if (item.isEmpty()) {
			if (bl && !player.isInvisible()) {
				this.renderArmHoldingItem(matrixStack, vertexConsumerProvider, equipProgress, f, arm);
			}
		} else if (item.getItem() == Items.FILLED_MAP) {
			int i = player.getLightmapCoordinates();
			if (bl && this.offHand.isEmpty()) {
				this.renderMapInBothHands(matrixStack, vertexConsumerProvider, i, pitch, equipProgress, f);
			} else {
				this.renderMapInOneHand(matrixStack, vertexConsumerProvider, i, equipProgress, arm, f, item);
			}
		} else if (item.getItem() == Items.CROSSBOW) {
			boolean bl2 = CrossbowItem.isCharged(item);
			boolean bl3 = arm == Arm.RIGHT;
			int j = bl3 ? 1 : -1;
			if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
				this.applyHandOffset(matrixStack, arm, equipProgress);
				matrixStack.translate((double)((float)j * -0.4785682F), -0.094387F, 0.05731531F);
				matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-11.935F));
				matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)j * 65.3F));
				matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)j * -9.785F));
				float g = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F);
				float h = g / (float)CrossbowItem.getPullTime(item);
				if (h > 1.0F) {
					h = 1.0F;
				}

				if (h > 0.1F) {
					float k = MathHelper.sin((g - 0.1F) * 1.3F);
					float l = h - 0.1F;
					float m = k * l;
					matrixStack.translate((double)(m * 0.0F), (double)(m * 0.004F), (double)(m * 0.0F));
				}

				matrixStack.translate((double)(h * 0.0F), (double)(h * 0.0F), (double)(h * 0.04F));
				matrixStack.scale(1.0F, 1.0F, 1.0F + h * 0.2F);
				matrixStack.multiply(Vector3f.NEGATIVE_Y.getRotationQuaternion((float)j * 45.0F));
			} else {
				float gx = -0.4F * MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI);
				float hx = 0.2F * MathHelper.sin(MathHelper.sqrt(f) * (float) (Math.PI * 2));
				float k = -0.2F * MathHelper.sin(f * (float) Math.PI);
				matrixStack.translate((double)((float)j * gx), (double)hx, (double)k);
				this.applyHandOffset(matrixStack, arm, equipProgress);
				this.method_3217(matrixStack, arm, f);
				if (bl2 && f < 0.001F) {
					matrixStack.translate((double)((float)j * -0.641864F), 0.0, 0.0);
					matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)j * 10.0F));
				}
			}

			this.renderItem(
				player,
				item,
				bl3 ? ModelTransformation.Type.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Type.FIRST_PERSON_LEFT_HAND,
				!bl3,
				matrixStack,
				vertexConsumerProvider
			);
		} else {
			boolean bl2 = arm == Arm.RIGHT;
			if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
				int n = bl2 ? 1 : -1;
				switch (item.getUseAction()) {
					case NONE:
						this.applyHandOffset(matrixStack, arm, equipProgress);
						break;
					case EAT:
					case DRINK:
						this.applyEatOrDrinkTransformation(matrixStack, tickDelta, arm, item);
						this.applyHandOffset(matrixStack, arm, equipProgress);
						break;
					case BLOCK:
						this.applyHandOffset(matrixStack, arm, equipProgress);
						break;
					case BOW:
						this.applyHandOffset(matrixStack, arm, equipProgress);
						matrixStack.translate((double)((float)n * -0.2785682F), 0.18344387F, 0.15731531F);
						matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-13.935F));
						matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)n * 35.3F));
						matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)n * -9.785F));
						float ox = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F);
						float gxx = ox / 20.0F;
						gxx = (gxx * gxx + gxx * 2.0F) / 3.0F;
						if (gxx > 1.0F) {
							gxx = 1.0F;
						}

						if (gxx > 0.1F) {
							float hx = MathHelper.sin((ox - 0.1F) * 1.3F);
							float k = gxx - 0.1F;
							float l = hx * k;
							matrixStack.translate((double)(l * 0.0F), (double)(l * 0.004F), (double)(l * 0.0F));
						}

						matrixStack.translate((double)(gxx * 0.0F), (double)(gxx * 0.0F), (double)(gxx * 0.04F));
						matrixStack.scale(1.0F, 1.0F, 1.0F + gxx * 0.2F);
						matrixStack.multiply(Vector3f.NEGATIVE_Y.getRotationQuaternion((float)n * 45.0F));
						break;
					case SPEAR:
						this.applyHandOffset(matrixStack, arm, equipProgress);
						matrixStack.translate((double)((float)n * -0.5F), 0.7F, 0.1F);
						matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-55.0F));
						matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)n * 35.3F));
						matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)n * -9.785F));
						float o = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F);
						float gx = o / 10.0F;
						if (gx > 1.0F) {
							gx = 1.0F;
						}

						if (gx > 0.1F) {
							float hx = MathHelper.sin((o - 0.1F) * 1.3F);
							float k = gx - 0.1F;
							float l = hx * k;
							matrixStack.translate((double)(l * 0.0F), (double)(l * 0.004F), (double)(l * 0.0F));
						}

						matrixStack.translate(0.0, 0.0, (double)(gx * 0.2F));
						matrixStack.scale(1.0F, 1.0F, 1.0F + gx * 0.2F);
						matrixStack.multiply(Vector3f.NEGATIVE_Y.getRotationQuaternion((float)n * 45.0F));
				}
			} else if (player.isUsingRiptide()) {
				this.applyHandOffset(matrixStack, arm, equipProgress);
				int n = bl2 ? 1 : -1;
				matrixStack.translate((double)((float)n * -0.4F), 0.8F, 0.3F);
				matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)n * 65.0F));
				matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)n * -85.0F));
			} else {
				float p = -0.4F * MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI);
				float oxx = 0.2F * MathHelper.sin(MathHelper.sqrt(f) * (float) (Math.PI * 2));
				float gxxx = -0.2F * MathHelper.sin(f * (float) Math.PI);
				int q = bl2 ? 1 : -1;
				matrixStack.translate((double)((float)q * p), (double)oxx, (double)gxxx);
				this.applyHandOffset(matrixStack, arm, equipProgress);
				this.method_3217(matrixStack, arm, f);
			}

			this.renderItem(
				player,
				item,
				bl2 ? ModelTransformation.Type.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Type.FIRST_PERSON_LEFT_HAND,
				!bl2,
				matrixStack,
				vertexConsumerProvider
			);
		}

		matrixStack.pop();
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
