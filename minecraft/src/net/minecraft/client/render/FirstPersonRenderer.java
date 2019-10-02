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
import net.minecraft.util.math.MatrixStack;

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

	public FirstPersonRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.renderManager = minecraftClient.getEntityRenderManager();
		this.itemRenderer = minecraftClient.getItemRenderer();
	}

	public void renderItem(
		LivingEntity livingEntity,
		ItemStack itemStack,
		ModelTransformation.Type type,
		boolean bl,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		if (!itemStack.isEmpty()) {
			this.itemRenderer
				.method_23177(livingEntity, itemStack, type, bl, matrixStack, layeredVertexConsumerStorage, livingEntity.world, livingEntity.getLightmapCoordinates());
		}
	}

	private float getMapAngle(float f) {
		float g = 1.0F - f / 45.0F + 0.1F;
		g = MathHelper.clamp(g, 0.0F, 1.0F);
		return -MathHelper.cos(g * (float) Math.PI) * 0.5F + 0.5F;
	}

	private void renderArm(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, Arm arm) {
		this.client.getTextureManager().bindTexture(this.client.player.getSkinTexture());
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.<AbstractClientPlayerEntity>getRenderer(this.client.player);
		matrixStack.push();
		float f = arm == Arm.RIGHT ? 1.0F : -1.0F;
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(92.0F, true));
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(45.0F, true));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(f * -41.0F, true));
		matrixStack.translate((double)(f * 0.3F), -1.1F, 0.45F);
		if (arm == Arm.RIGHT) {
			playerEntityRenderer.renderRightArm(matrixStack, layeredVertexConsumerStorage, this.client.player);
		} else {
			playerEntityRenderer.renderLeftArm(matrixStack, layeredVertexConsumerStorage, this.client.player);
		}

		matrixStack.pop();
	}

	private void renderMapInOneHand(
		MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, float f, Arm arm, float g, ItemStack itemStack
	) {
		float h = arm == Arm.RIGHT ? 1.0F : -1.0F;
		matrixStack.translate((double)(h * 0.125F), -0.125, 0.0);
		if (!this.client.player.isInvisible()) {
			matrixStack.push();
			matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(h * 10.0F, true));
			this.renderArmHoldingItem(matrixStack, layeredVertexConsumerStorage, f, g, arm);
			matrixStack.pop();
		}

		matrixStack.push();
		matrixStack.translate((double)(h * 0.51F), (double)(-0.08F + f * -1.2F), -0.75);
		float i = MathHelper.sqrt(g);
		float j = MathHelper.sin(i * (float) Math.PI);
		float k = -0.5F * j;
		float l = 0.4F * MathHelper.sin(i * (float) (Math.PI * 2));
		float m = -0.3F * MathHelper.sin(g * (float) Math.PI);
		matrixStack.translate((double)(h * k), (double)(l - 0.3F * j), (double)m);
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(j * -45.0F, true));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(h * j * -30.0F, true));
		this.renderFirstPersonMap(matrixStack, layeredVertexConsumerStorage, itemStack);
		matrixStack.pop();
	}

	private void renderMapInBothHands(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, float f, float g, float h) {
		float i = MathHelper.sqrt(h);
		float j = -0.2F * MathHelper.sin(h * (float) Math.PI);
		float k = -0.4F * MathHelper.sin(i * (float) Math.PI);
		matrixStack.translate(0.0, (double)(-j / 2.0F), (double)k);
		float l = this.getMapAngle(f);
		matrixStack.translate(0.0, (double)(0.04F + g * -1.2F + l * -0.5F), -0.72F);
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(l * -85.0F, true));
		if (!this.client.player.isInvisible()) {
			matrixStack.push();
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(90.0F, true));
			this.renderArm(matrixStack, layeredVertexConsumerStorage, Arm.RIGHT);
			this.renderArm(matrixStack, layeredVertexConsumerStorage, Arm.LEFT);
			matrixStack.pop();
		}

		float m = MathHelper.sin(i * (float) Math.PI);
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(m * 20.0F, true));
		matrixStack.scale(2.0F, 2.0F, 2.0F);
		this.renderFirstPersonMap(matrixStack, layeredVertexConsumerStorage, this.mainHand);
	}

	private void renderFirstPersonMap(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, ItemStack itemStack) {
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F, true));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(180.0F, true));
		matrixStack.scale(0.38F, 0.38F, 0.38F);
		matrixStack.translate(-0.5, -0.5, 0.0);
		matrixStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23028(MapRenderer.field_21056));
		Matrix4f matrix4f = matrixStack.peek();
		vertexConsumer.vertex(matrix4f, -7.0F, 135.0F, 0.0F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(15728880).next();
		vertexConsumer.vertex(matrix4f, 135.0F, 135.0F, 0.0F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(15728880).next();
		vertexConsumer.vertex(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(15728880).next();
		vertexConsumer.vertex(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(15728880).next();
		MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, this.client.world);
		if (mapState != null) {
			this.client.gameRenderer.getMapRenderer().draw(matrixStack, layeredVertexConsumerStorage, mapState, false, 15728880);
		}
	}

	private void renderArmHoldingItem(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, float f, float g, Arm arm) {
		boolean bl = arm != Arm.LEFT;
		float h = bl ? 1.0F : -1.0F;
		float i = MathHelper.sqrt(g);
		float j = -0.3F * MathHelper.sin(i * (float) Math.PI);
		float k = 0.4F * MathHelper.sin(i * (float) (Math.PI * 2));
		float l = -0.4F * MathHelper.sin(g * (float) Math.PI);
		matrixStack.translate((double)(h * (j + 0.64000005F)), (double)(k + -0.6F + f * -0.6F), (double)(l + -0.71999997F));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(h * 45.0F, true));
		float m = MathHelper.sin(g * g * (float) Math.PI);
		float n = MathHelper.sin(i * (float) Math.PI);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(h * n * 70.0F, true));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(h * m * -20.0F, true));
		AbstractClientPlayerEntity abstractClientPlayerEntity = this.client.player;
		this.client.getTextureManager().bindTexture(abstractClientPlayerEntity.getSkinTexture());
		matrixStack.translate((double)(h * -1.0F), 3.6F, 3.5);
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(h * 120.0F, true));
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(200.0F, true));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(h * -135.0F, true));
		matrixStack.translate((double)(h * 5.6F), 0.0, 0.0);
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.<AbstractClientPlayerEntity>getRenderer(abstractClientPlayerEntity);
		if (bl) {
			playerEntityRenderer.renderRightArm(matrixStack, layeredVertexConsumerStorage, abstractClientPlayerEntity);
		} else {
			playerEntityRenderer.renderLeftArm(matrixStack, layeredVertexConsumerStorage, abstractClientPlayerEntity);
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
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)j * i * 90.0F, true));
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(i * 10.0F, true));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)j * i * 30.0F, true));
	}

	private void method_3217(MatrixStack matrixStack, Arm arm, float f) {
		int i = arm == Arm.RIGHT ? 1 : -1;
		float g = MathHelper.sin(f * f * (float) Math.PI);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)i * (45.0F + g * -20.0F), true));
		float h = MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI);
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)i * h * -20.0F, true));
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(h * -80.0F, true));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)i * -45.0F, true));
	}

	private void applyHandOffset(MatrixStack matrixStack, Arm arm, float f) {
		int i = arm == Arm.RIGHT ? 1 : -1;
		matrixStack.translate((double)((float)i * 0.56F), (double)(-0.52F + f * -0.6F), -0.72F);
	}

	public void method_22976(float f, MatrixStack matrixStack, LayeredVertexConsumerStorage.class_4598 arg) {
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
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion((clientPlayerEntity.getPitch(f) - i) * 0.1F, true));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((clientPlayerEntity.getYaw(f) - j) * 0.1F, true));
		if (bl) {
			float k = hand == Hand.MAIN_HAND ? g : 0.0F;
			float l = 1.0F - MathHelper.lerp(f, this.prevEquipProgressMainHand, this.equipProgressMainHand);
			this.renderFirstPersonItem(clientPlayerEntity, f, h, Hand.MAIN_HAND, k, this.mainHand, l, matrixStack, arg);
		}

		if (bl2) {
			float k = hand == Hand.OFF_HAND ? g : 0.0F;
			float l = 1.0F - MathHelper.lerp(f, this.prevEquipProgressOffHand, this.equipProgressOffHand);
			this.renderFirstPersonItem(clientPlayerEntity, f, h, Hand.OFF_HAND, k, this.offHand, l, matrixStack, arg);
		}

		arg.method_22993();
	}

	private void renderFirstPersonItem(
		AbstractClientPlayerEntity abstractClientPlayerEntity,
		float f,
		float g,
		Hand hand,
		float h,
		ItemStack itemStack,
		float i,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		boolean bl = hand == Hand.MAIN_HAND;
		Arm arm = bl ? abstractClientPlayerEntity.getMainArm() : abstractClientPlayerEntity.getMainArm().getOpposite();
		matrixStack.push();
		if (itemStack.isEmpty()) {
			if (bl && !abstractClientPlayerEntity.isInvisible()) {
				this.renderArmHoldingItem(matrixStack, layeredVertexConsumerStorage, i, h, arm);
			}
		} else if (itemStack.getItem() == Items.FILLED_MAP) {
			if (bl && this.offHand.isEmpty()) {
				this.renderMapInBothHands(matrixStack, layeredVertexConsumerStorage, g, i, h);
			} else {
				this.renderMapInOneHand(matrixStack, layeredVertexConsumerStorage, i, arm, h, itemStack);
			}
		} else if (itemStack.getItem() == Items.CROSSBOW) {
			boolean bl2 = CrossbowItem.isCharged(itemStack);
			boolean bl3 = arm == Arm.RIGHT;
			int j = bl3 ? 1 : -1;
			if (abstractClientPlayerEntity.isUsingItem() && abstractClientPlayerEntity.getItemUseTimeLeft() > 0 && abstractClientPlayerEntity.getActiveHand() == hand) {
				this.applyHandOffset(matrixStack, arm, i);
				matrixStack.translate((double)((float)j * -0.4785682F), -0.094387F, 0.05731531F);
				matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-11.935F, true));
				matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)j * 65.3F, true));
				matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)j * -9.785F, true));
				float k = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0F);
				float l = k / (float)CrossbowItem.getPullTime(itemStack);
				if (l > 1.0F) {
					l = 1.0F;
				}

				if (l > 0.1F) {
					float m = MathHelper.sin((k - 0.1F) * 1.3F);
					float n = l - 0.1F;
					float o = m * n;
					matrixStack.translate((double)(o * 0.0F), (double)(o * 0.004F), (double)(o * 0.0F));
				}

				matrixStack.translate((double)(l * 0.0F), (double)(l * 0.0F), (double)(l * 0.04F));
				matrixStack.scale(1.0F, 1.0F, 1.0F + l * 0.2F);
				matrixStack.multiply(Vector3f.NEGATIVE_Y.getRotationQuaternion((float)j * 45.0F, true));
			} else {
				float kx = -0.4F * MathHelper.sin(MathHelper.sqrt(h) * (float) Math.PI);
				float lx = 0.2F * MathHelper.sin(MathHelper.sqrt(h) * (float) (Math.PI * 2));
				float m = -0.2F * MathHelper.sin(h * (float) Math.PI);
				matrixStack.translate((double)((float)j * kx), (double)lx, (double)m);
				this.applyHandOffset(matrixStack, arm, i);
				this.method_3217(matrixStack, arm, h);
				if (bl2 && h < 0.001F) {
					matrixStack.translate((double)((float)j * -0.641864F), 0.0, 0.0);
					matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)j * 10.0F, true));
				}
			}

			this.renderItem(
				abstractClientPlayerEntity,
				itemStack,
				bl3 ? ModelTransformation.Type.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Type.FIRST_PERSON_LEFT_HAND,
				!bl3,
				matrixStack,
				layeredVertexConsumerStorage
			);
		} else {
			boolean bl2 = arm == Arm.RIGHT;
			if (abstractClientPlayerEntity.isUsingItem() && abstractClientPlayerEntity.getItemUseTimeLeft() > 0 && abstractClientPlayerEntity.getActiveHand() == hand) {
				int p = bl2 ? 1 : -1;
				switch (itemStack.getUseAction()) {
					case NONE:
						this.applyHandOffset(matrixStack, arm, i);
						break;
					case EAT:
					case DRINK:
						this.applyEatOrDrinkTransformation(matrixStack, f, arm, itemStack);
						this.applyHandOffset(matrixStack, arm, i);
						break;
					case BLOCK:
						this.applyHandOffset(matrixStack, arm, i);
						break;
					case BOW:
						this.applyHandOffset(matrixStack, arm, i);
						matrixStack.translate((double)((float)p * -0.2785682F), 0.18344387F, 0.15731531F);
						matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-13.935F, true));
						matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)p * 35.3F, true));
						matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)p * -9.785F, true));
						float qx = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0F);
						float kxx = qx / 20.0F;
						kxx = (kxx * kxx + kxx * 2.0F) / 3.0F;
						if (kxx > 1.0F) {
							kxx = 1.0F;
						}

						if (kxx > 0.1F) {
							float lx = MathHelper.sin((qx - 0.1F) * 1.3F);
							float m = kxx - 0.1F;
							float n = lx * m;
							matrixStack.translate((double)(n * 0.0F), (double)(n * 0.004F), (double)(n * 0.0F));
						}

						matrixStack.translate((double)(kxx * 0.0F), (double)(kxx * 0.0F), (double)(kxx * 0.04F));
						matrixStack.scale(1.0F, 1.0F, 1.0F + kxx * 0.2F);
						matrixStack.multiply(Vector3f.NEGATIVE_Y.getRotationQuaternion((float)p * 45.0F, true));
						break;
					case SPEAR:
						this.applyHandOffset(matrixStack, arm, i);
						matrixStack.translate((double)((float)p * -0.5F), 0.7F, 0.1F);
						matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-55.0F, true));
						matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)p * 35.3F, true));
						matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)p * -9.785F, true));
						float q = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0F);
						float kx = q / 10.0F;
						if (kx > 1.0F) {
							kx = 1.0F;
						}

						if (kx > 0.1F) {
							float lx = MathHelper.sin((q - 0.1F) * 1.3F);
							float m = kx - 0.1F;
							float n = lx * m;
							matrixStack.translate((double)(n * 0.0F), (double)(n * 0.004F), (double)(n * 0.0F));
						}

						matrixStack.translate(0.0, 0.0, (double)(kx * 0.2F));
						matrixStack.scale(1.0F, 1.0F, 1.0F + kx * 0.2F);
						matrixStack.multiply(Vector3f.NEGATIVE_Y.getRotationQuaternion((float)p * 45.0F, true));
				}
			} else if (abstractClientPlayerEntity.isUsingRiptide()) {
				this.applyHandOffset(matrixStack, arm, i);
				int p = bl2 ? 1 : -1;
				matrixStack.translate((double)((float)p * -0.4F), 0.8F, 0.3F);
				matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)p * 65.0F, true));
				matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)p * -85.0F, true));
			} else {
				float r = -0.4F * MathHelper.sin(MathHelper.sqrt(h) * (float) Math.PI);
				float qxx = 0.2F * MathHelper.sin(MathHelper.sqrt(h) * (float) (Math.PI * 2));
				float kxxx = -0.2F * MathHelper.sin(h * (float) Math.PI);
				int s = bl2 ? 1 : -1;
				matrixStack.translate((double)((float)s * r), (double)qxx, (double)kxxx);
				this.applyHandOffset(matrixStack, arm, i);
				this.method_3217(matrixStack, arm, h);
			}

			this.renderItem(
				abstractClientPlayerEntity,
				itemStack,
				bl2 ? ModelTransformation.Type.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Type.FIRST_PERSON_LEFT_HAND,
				!bl2,
				matrixStack,
				layeredVertexConsumerStorage
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
