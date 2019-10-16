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
				.method_23177(
					livingEntity,
					itemStack,
					type,
					bl,
					matrixStack,
					layeredVertexConsumerStorage,
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

	private void renderArm(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, Arm arm) {
		this.client.getTextureManager().bindTexture(this.client.player.getSkinTexture());
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.<AbstractClientPlayerEntity>getRenderer(this.client.player);
		matrixStack.push();
		float f = arm == Arm.RIGHT ? 1.0F : -1.0F;
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(92.0F));
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(45.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(f * -41.0F));
		matrixStack.translate((double)(f * 0.3F), -1.1F, 0.45F);
		if (arm == Arm.RIGHT) {
			playerEntityRenderer.renderRightArm(matrixStack, layeredVertexConsumerStorage, this.client.player);
		} else {
			playerEntityRenderer.renderLeftArm(matrixStack, layeredVertexConsumerStorage, this.client.player);
		}

		matrixStack.pop();
	}

	private void renderMapInOneHand(
		MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, float f, Arm arm, float g, ItemStack itemStack
	) {
		float h = arm == Arm.RIGHT ? 1.0F : -1.0F;
		matrixStack.translate((double)(h * 0.125F), -0.125, 0.0);
		if (!this.client.player.isInvisible()) {
			matrixStack.push();
			matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(h * 10.0F));
			this.renderArmHoldingItem(matrixStack, layeredVertexConsumerStorage, f, g, arm);
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
		this.renderFirstPersonMap(matrixStack, layeredVertexConsumerStorage, i, itemStack);
		matrixStack.pop();
	}

	private void renderMapInBothHands(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, float f, float g, float h) {
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
			this.renderArm(matrixStack, layeredVertexConsumerStorage, Arm.RIGHT);
			this.renderArm(matrixStack, layeredVertexConsumerStorage, Arm.LEFT);
			matrixStack.pop();
		}

		float n = MathHelper.sin(j * (float) Math.PI);
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(n * 20.0F));
		matrixStack.scale(2.0F, 2.0F, 2.0F);
		this.renderFirstPersonMap(matrixStack, layeredVertexConsumerStorage, i, this.mainHand);
	}

	private void renderFirstPersonMap(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, ItemStack itemStack) {
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(180.0F));
		matrixStack.scale(0.38F, 0.38F, 0.38F);
		matrixStack.translate(-0.5, -0.5, 0.0);
		matrixStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getText(MapRenderer.field_21056));
		Matrix4f matrix4f = matrixStack.peek();
		vertexConsumer.vertex(matrix4f, -7.0F, 135.0F, 0.0F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(i).next();
		vertexConsumer.vertex(matrix4f, 135.0F, 135.0F, 0.0F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(i).next();
		vertexConsumer.vertex(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(i).next();
		vertexConsumer.vertex(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(i).next();
		MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, this.client.world);
		if (mapState != null) {
			this.client.gameRenderer.getMapRenderer().draw(matrixStack, layeredVertexConsumerStorage, mapState, false, i);
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

	public void method_22976(float f, MatrixStack matrixStack, LayeredVertexConsumerStorage.Drawer drawer) {
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
			this.renderFirstPersonItem(clientPlayerEntity, f, h, Hand.MAIN_HAND, k, this.mainHand, l, matrixStack, drawer);
		}

		if (bl2) {
			float k = hand == Hand.OFF_HAND ? g : 0.0F;
			float l = 1.0F - MathHelper.lerp(f, this.prevEquipProgressOffHand, this.equipProgressOffHand);
			this.renderFirstPersonItem(clientPlayerEntity, f, h, Hand.OFF_HAND, k, this.offHand, l, matrixStack, drawer);
		}

		drawer.draw();
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
			int j = abstractClientPlayerEntity.getLightmapCoordinates();
			if (bl && this.offHand.isEmpty()) {
				this.renderMapInBothHands(matrixStack, layeredVertexConsumerStorage, j, g, i, h);
			} else {
				this.renderMapInOneHand(matrixStack, layeredVertexConsumerStorage, j, i, arm, h, itemStack);
			}
		} else if (itemStack.getItem() == Items.CROSSBOW) {
			boolean bl2 = CrossbowItem.isCharged(itemStack);
			boolean bl3 = arm == Arm.RIGHT;
			int k = bl3 ? 1 : -1;
			if (abstractClientPlayerEntity.isUsingItem() && abstractClientPlayerEntity.getItemUseTimeLeft() > 0 && abstractClientPlayerEntity.getActiveHand() == hand) {
				this.applyHandOffset(matrixStack, arm, i);
				matrixStack.translate((double)((float)k * -0.4785682F), -0.094387F, 0.05731531F);
				matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-11.935F));
				matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)k * 65.3F));
				matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)k * -9.785F));
				float l = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0F);
				float m = l / (float)CrossbowItem.getPullTime(itemStack);
				if (m > 1.0F) {
					m = 1.0F;
				}

				if (m > 0.1F) {
					float n = MathHelper.sin((l - 0.1F) * 1.3F);
					float o = m - 0.1F;
					float p = n * o;
					matrixStack.translate((double)(p * 0.0F), (double)(p * 0.004F), (double)(p * 0.0F));
				}

				matrixStack.translate((double)(m * 0.0F), (double)(m * 0.0F), (double)(m * 0.04F));
				matrixStack.scale(1.0F, 1.0F, 1.0F + m * 0.2F);
				matrixStack.multiply(Vector3f.NEGATIVE_Y.getRotationQuaternion((float)k * 45.0F));
			} else {
				float lx = -0.4F * MathHelper.sin(MathHelper.sqrt(h) * (float) Math.PI);
				float mx = 0.2F * MathHelper.sin(MathHelper.sqrt(h) * (float) (Math.PI * 2));
				float n = -0.2F * MathHelper.sin(h * (float) Math.PI);
				matrixStack.translate((double)((float)k * lx), (double)mx, (double)n);
				this.applyHandOffset(matrixStack, arm, i);
				this.method_3217(matrixStack, arm, h);
				if (bl2 && h < 0.001F) {
					matrixStack.translate((double)((float)k * -0.641864F), 0.0, 0.0);
					matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)k * 10.0F));
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
				int q = bl2 ? 1 : -1;
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
						matrixStack.translate((double)((float)q * -0.2785682F), 0.18344387F, 0.15731531F);
						matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-13.935F));
						matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)q * 35.3F));
						matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)q * -9.785F));
						float rx = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0F);
						float lxx = rx / 20.0F;
						lxx = (lxx * lxx + lxx * 2.0F) / 3.0F;
						if (lxx > 1.0F) {
							lxx = 1.0F;
						}

						if (lxx > 0.1F) {
							float mx = MathHelper.sin((rx - 0.1F) * 1.3F);
							float n = lxx - 0.1F;
							float o = mx * n;
							matrixStack.translate((double)(o * 0.0F), (double)(o * 0.004F), (double)(o * 0.0F));
						}

						matrixStack.translate((double)(lxx * 0.0F), (double)(lxx * 0.0F), (double)(lxx * 0.04F));
						matrixStack.scale(1.0F, 1.0F, 1.0F + lxx * 0.2F);
						matrixStack.multiply(Vector3f.NEGATIVE_Y.getRotationQuaternion((float)q * 45.0F));
						break;
					case SPEAR:
						this.applyHandOffset(matrixStack, arm, i);
						matrixStack.translate((double)((float)q * -0.5F), 0.7F, 0.1F);
						matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-55.0F));
						matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)q * 35.3F));
						matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)q * -9.785F));
						float r = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0F);
						float lx = r / 10.0F;
						if (lx > 1.0F) {
							lx = 1.0F;
						}

						if (lx > 0.1F) {
							float mx = MathHelper.sin((r - 0.1F) * 1.3F);
							float n = lx - 0.1F;
							float o = mx * n;
							matrixStack.translate((double)(o * 0.0F), (double)(o * 0.004F), (double)(o * 0.0F));
						}

						matrixStack.translate(0.0, 0.0, (double)(lx * 0.2F));
						matrixStack.scale(1.0F, 1.0F, 1.0F + lx * 0.2F);
						matrixStack.multiply(Vector3f.NEGATIVE_Y.getRotationQuaternion((float)q * 45.0F));
				}
			} else if (abstractClientPlayerEntity.isUsingRiptide()) {
				this.applyHandOffset(matrixStack, arm, i);
				int q = bl2 ? 1 : -1;
				matrixStack.translate((double)((float)q * -0.4F), 0.8F, 0.3F);
				matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)q * 65.0F));
				matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)q * -85.0F));
			} else {
				float s = -0.4F * MathHelper.sin(MathHelper.sqrt(h) * (float) Math.PI);
				float rxx = 0.2F * MathHelper.sin(MathHelper.sqrt(h) * (float) (Math.PI * 2));
				float lxxx = -0.2F * MathHelper.sin(h * (float) Math.PI);
				int t = bl2 ? 1 : -1;
				matrixStack.translate((double)((float)t * s), (double)rxx, (double)lxxx);
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
