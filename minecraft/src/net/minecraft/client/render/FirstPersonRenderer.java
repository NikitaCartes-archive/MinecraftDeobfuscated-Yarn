package net.minecraft.client.render;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class FirstPersonRenderer {
	private static final Identifier MAP_BACKGROUND_TEX = new Identifier("textures/map/map_background.png");
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

	public void renderItem(LivingEntity livingEntity, ItemStack itemStack, ModelTransformation.Type type, boolean bl, class_4587 arg, class_4597 arg2) {
		if (!itemStack.isEmpty()) {
			this.itemRenderer.method_23177(livingEntity, itemStack, type, bl, arg, arg2, livingEntity.world, livingEntity.getLightmapCoordinates());
		}
	}

	private float getMapAngle(float f) {
		float g = 1.0F - f / 45.0F + 0.1F;
		g = MathHelper.clamp(g, 0.0F, 1.0F);
		return -MathHelper.cos(g * (float) Math.PI) * 0.5F + 0.5F;
	}

	private void renderArm(class_4587 arg, class_4597 arg2, Arm arm) {
		this.client.getTextureManager().bindTexture(this.client.player.getSkinTexture());
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.<AbstractClientPlayerEntity>getRenderer(this.client.player);
		arg.method_22903();
		float f = arm == Arm.RIGHT ? 1.0F : -1.0F;
		arg.method_22907(Vector3f.field_20705.method_23214(92.0F, true));
		arg.method_22907(Vector3f.field_20703.method_23214(45.0F, true));
		arg.method_22907(Vector3f.field_20707.method_23214(f * -41.0F, true));
		arg.method_22904((double)(f * 0.3F), -1.1F, 0.45F);
		if (arm == Arm.RIGHT) {
			playerEntityRenderer.renderRightArm(arg, arg2, this.client.player);
		} else {
			playerEntityRenderer.renderLeftArm(arg, arg2, this.client.player);
		}

		arg.method_22909();
	}

	private void renderMapInOneHand(class_4587 arg, class_4597 arg2, float f, Arm arm, float g, ItemStack itemStack) {
		float h = arm == Arm.RIGHT ? 1.0F : -1.0F;
		arg.method_22904((double)(h * 0.125F), -0.125, 0.0);
		if (!this.client.player.isInvisible()) {
			arg.method_22903();
			arg.method_22907(Vector3f.field_20707.method_23214(h * 10.0F, true));
			this.renderArmHoldingItem(arg, arg2, f, g, arm);
			arg.method_22909();
		}

		arg.method_22903();
		arg.method_22904((double)(h * 0.51F), (double)(-0.08F + f * -1.2F), -0.75);
		float i = MathHelper.sqrt(g);
		float j = MathHelper.sin(i * (float) Math.PI);
		float k = -0.5F * j;
		float l = 0.4F * MathHelper.sin(i * (float) (Math.PI * 2));
		float m = -0.3F * MathHelper.sin(g * (float) Math.PI);
		arg.method_22904((double)(h * k), (double)(l - 0.3F * j), (double)m);
		arg.method_22907(Vector3f.field_20703.method_23214(j * -45.0F, true));
		arg.method_22907(Vector3f.field_20705.method_23214(h * j * -30.0F, true));
		this.renderFirstPersonMap(arg, arg2, itemStack);
		arg.method_22909();
	}

	private void renderMapInBothHands(class_4587 arg, class_4597 arg2, float f, float g, float h) {
		float i = MathHelper.sqrt(h);
		float j = -0.2F * MathHelper.sin(h * (float) Math.PI);
		float k = -0.4F * MathHelper.sin(i * (float) Math.PI);
		arg.method_22904(0.0, (double)(-j / 2.0F), (double)k);
		float l = this.getMapAngle(f);
		arg.method_22904(0.0, (double)(0.04F + g * -1.2F + l * -0.5F), -0.72F);
		arg.method_22907(Vector3f.field_20703.method_23214(l * -85.0F, true));
		if (!this.client.player.isInvisible()) {
			arg.method_22903();
			arg.method_22907(Vector3f.field_20705.method_23214(90.0F, true));
			this.renderArm(arg, arg2, Arm.RIGHT);
			this.renderArm(arg, arg2, Arm.LEFT);
			arg.method_22909();
		}

		float m = MathHelper.sin(i * (float) Math.PI);
		arg.method_22907(Vector3f.field_20703.method_23214(m * 20.0F, true));
		arg.method_22905(2.0F, 2.0F, 2.0F);
		this.renderFirstPersonMap(arg, arg2, this.mainHand);
	}

	private void renderFirstPersonMap(class_4587 arg, class_4597 arg2, ItemStack itemStack) {
		arg.method_22907(Vector3f.field_20705.method_23214(180.0F, true));
		arg.method_22907(Vector3f.field_20707.method_23214(180.0F, true));
		arg.method_22905(0.38F, 0.38F, 0.38F);
		this.client.getTextureManager().bindTexture(MAP_BACKGROUND_TEX);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		arg.method_22904(-0.5, -0.5, 0.0);
		arg.method_22905(0.0078125F, 0.0078125F, 0.0078125F);
		bufferBuilder.begin(7, VertexFormats.POSITION_UV);
		Matrix4f matrix4f = arg.method_22910();
		bufferBuilder.method_22918(matrix4f, -7.0F, 135.0F, 0.0F).texture(0.0F, 1.0F).next();
		bufferBuilder.method_22918(matrix4f, 135.0F, 135.0F, 0.0F).texture(1.0F, 1.0F).next();
		bufferBuilder.method_22918(matrix4f, 135.0F, -7.0F, 0.0F).texture(1.0F, 0.0F).next();
		bufferBuilder.method_22918(matrix4f, -7.0F, -7.0F, 0.0F).texture(0.0F, 0.0F).next();
		tessellator.draw();
		MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, this.client.world);
		if (mapState != null) {
			this.client.gameRenderer.getMapRenderer().draw(arg, arg2, mapState, false);
		}
	}

	private void renderArmHoldingItem(class_4587 arg, class_4597 arg2, float f, float g, Arm arm) {
		boolean bl = arm != Arm.LEFT;
		float h = bl ? 1.0F : -1.0F;
		float i = MathHelper.sqrt(g);
		float j = -0.3F * MathHelper.sin(i * (float) Math.PI);
		float k = 0.4F * MathHelper.sin(i * (float) (Math.PI * 2));
		float l = -0.4F * MathHelper.sin(g * (float) Math.PI);
		arg.method_22904((double)(h * (j + 0.64000005F)), (double)(k + -0.6F + f * -0.6F), (double)(l + -0.71999997F));
		arg.method_22907(Vector3f.field_20705.method_23214(h * 45.0F, true));
		float m = MathHelper.sin(g * g * (float) Math.PI);
		float n = MathHelper.sin(i * (float) Math.PI);
		arg.method_22907(Vector3f.field_20705.method_23214(h * n * 70.0F, true));
		arg.method_22907(Vector3f.field_20707.method_23214(h * m * -20.0F, true));
		AbstractClientPlayerEntity abstractClientPlayerEntity = this.client.player;
		this.client.getTextureManager().bindTexture(abstractClientPlayerEntity.getSkinTexture());
		arg.method_22904((double)(h * -1.0F), 3.6F, 3.5);
		arg.method_22907(Vector3f.field_20707.method_23214(h * 120.0F, true));
		arg.method_22907(Vector3f.field_20703.method_23214(200.0F, true));
		arg.method_22907(Vector3f.field_20705.method_23214(h * -135.0F, true));
		arg.method_22904((double)(h * 5.6F), 0.0, 0.0);
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.<AbstractClientPlayerEntity>getRenderer(abstractClientPlayerEntity);
		if (bl) {
			playerEntityRenderer.renderRightArm(arg, arg2, abstractClientPlayerEntity);
		} else {
			playerEntityRenderer.renderLeftArm(arg, arg2, abstractClientPlayerEntity);
		}
	}

	private void applyEatOrDrinkTransformation(class_4587 arg, float f, Arm arm, ItemStack itemStack) {
		float g = (float)this.client.player.getItemUseTimeLeft() - f + 1.0F;
		float h = g / (float)itemStack.getMaxUseTime();
		if (h < 0.8F) {
			float i = MathHelper.abs(MathHelper.cos(g / 4.0F * (float) Math.PI) * 0.1F);
			arg.method_22904(0.0, (double)i, 0.0);
		}

		float i = 1.0F - (float)Math.pow((double)h, 27.0);
		int j = arm == Arm.RIGHT ? 1 : -1;
		arg.method_22904((double)(i * 0.6F * (float)j), (double)(i * -0.5F), (double)(i * 0.0F));
		arg.method_22907(Vector3f.field_20705.method_23214((float)j * i * 90.0F, true));
		arg.method_22907(Vector3f.field_20703.method_23214(i * 10.0F, true));
		arg.method_22907(Vector3f.field_20707.method_23214((float)j * i * 30.0F, true));
	}

	private void method_3217(class_4587 arg, Arm arm, float f) {
		int i = arm == Arm.RIGHT ? 1 : -1;
		float g = MathHelper.sin(f * f * (float) Math.PI);
		arg.method_22907(Vector3f.field_20705.method_23214((float)i * (45.0F + g * -20.0F), true));
		float h = MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI);
		arg.method_22907(Vector3f.field_20707.method_23214((float)i * h * -20.0F, true));
		arg.method_22907(Vector3f.field_20703.method_23214(h * -80.0F, true));
		arg.method_22907(Vector3f.field_20705.method_23214((float)i * -45.0F, true));
	}

	private void applyHandOffset(class_4587 arg, Arm arm, float f) {
		int i = arm == Arm.RIGHT ? 1 : -1;
		arg.method_22904((double)((float)i * 0.56F), (double)(-0.52F + f * -0.6F), -0.72F);
	}

	public void method_22976(float f, class_4587 arg, class_4597.class_4598 arg2) {
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
		arg.method_22907(Vector3f.field_20703.method_23214((clientPlayerEntity.getPitch(f) - i) * 0.1F, true));
		arg.method_22907(Vector3f.field_20705.method_23214((clientPlayerEntity.getYaw(f) - j) * 0.1F, true));
		if (bl) {
			float k = hand == Hand.MAIN_HAND ? g : 0.0F;
			float l = 1.0F - MathHelper.lerp(f, this.prevEquipProgressMainHand, this.equipProgressMainHand);
			this.renderFirstPersonItem(clientPlayerEntity, f, h, Hand.MAIN_HAND, k, this.mainHand, l, arg, arg2);
		}

		if (bl2) {
			float k = hand == Hand.OFF_HAND ? g : 0.0F;
			float l = 1.0F - MathHelper.lerp(f, this.prevEquipProgressOffHand, this.equipProgressOffHand);
			this.renderFirstPersonItem(clientPlayerEntity, f, h, Hand.OFF_HAND, k, this.offHand, l, arg, arg2);
		}

		arg2.method_22993();
	}

	private void renderFirstPersonItem(
		AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, Hand hand, float h, ItemStack itemStack, float i, class_4587 arg, class_4597 arg2
	) {
		boolean bl = hand == Hand.MAIN_HAND;
		Arm arm = bl ? abstractClientPlayerEntity.getMainArm() : abstractClientPlayerEntity.getMainArm().getOpposite();
		arg.method_22903();
		if (itemStack.isEmpty()) {
			if (bl && !abstractClientPlayerEntity.isInvisible()) {
				this.renderArmHoldingItem(arg, arg2, i, h, arm);
			}
		} else if (itemStack.getItem() == Items.FILLED_MAP) {
			if (bl && this.offHand.isEmpty()) {
				this.renderMapInBothHands(arg, arg2, g, i, h);
			} else {
				this.renderMapInOneHand(arg, arg2, i, arm, h, itemStack);
			}
		} else if (itemStack.getItem() == Items.CROSSBOW) {
			boolean bl2 = CrossbowItem.isCharged(itemStack);
			boolean bl3 = arm == Arm.RIGHT;
			int j = bl3 ? 1 : -1;
			if (abstractClientPlayerEntity.isUsingItem() && abstractClientPlayerEntity.getItemUseTimeLeft() > 0 && abstractClientPlayerEntity.getActiveHand() == hand) {
				this.applyHandOffset(arg, arm, i);
				arg.method_22904((double)((float)j * -0.4785682F), -0.094387F, 0.05731531F);
				arg.method_22907(Vector3f.field_20703.method_23214(-11.935F, true));
				arg.method_22907(Vector3f.field_20705.method_23214((float)j * 65.3F, true));
				arg.method_22907(Vector3f.field_20707.method_23214((float)j * -9.785F, true));
				float k = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0F);
				float l = k / (float)CrossbowItem.getPullTime(itemStack);
				if (l > 1.0F) {
					l = 1.0F;
				}

				if (l > 0.1F) {
					float m = MathHelper.sin((k - 0.1F) * 1.3F);
					float n = l - 0.1F;
					float o = m * n;
					arg.method_22904((double)(o * 0.0F), (double)(o * 0.004F), (double)(o * 0.0F));
				}

				arg.method_22904((double)(l * 0.0F), (double)(l * 0.0F), (double)(l * 0.04F));
				arg.method_22905(1.0F, 1.0F, 1.0F + l * 0.2F);
				arg.method_22907(Vector3f.field_20704.method_23214((float)j * 45.0F, true));
			} else {
				float kx = -0.4F * MathHelper.sin(MathHelper.sqrt(h) * (float) Math.PI);
				float lx = 0.2F * MathHelper.sin(MathHelper.sqrt(h) * (float) (Math.PI * 2));
				float m = -0.2F * MathHelper.sin(h * (float) Math.PI);
				arg.method_22904((double)((float)j * kx), (double)lx, (double)m);
				this.applyHandOffset(arg, arm, i);
				this.method_3217(arg, arm, h);
				if (bl2 && h < 0.001F) {
					arg.method_22904((double)((float)j * -0.641864F), 0.0, 0.0);
					arg.method_22907(Vector3f.field_20705.method_23214((float)j * 10.0F, true));
				}
			}

			this.renderItem(
				abstractClientPlayerEntity,
				itemStack,
				bl3 ? ModelTransformation.Type.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Type.FIRST_PERSON_LEFT_HAND,
				!bl3,
				arg,
				arg2
			);
		} else {
			boolean bl2 = arm == Arm.RIGHT;
			if (abstractClientPlayerEntity.isUsingItem() && abstractClientPlayerEntity.getItemUseTimeLeft() > 0 && abstractClientPlayerEntity.getActiveHand() == hand) {
				int p = bl2 ? 1 : -1;
				switch (itemStack.getUseAction()) {
					case NONE:
						this.applyHandOffset(arg, arm, i);
						break;
					case EAT:
					case DRINK:
						this.applyEatOrDrinkTransformation(arg, f, arm, itemStack);
						this.applyHandOffset(arg, arm, i);
						break;
					case BLOCK:
						this.applyHandOffset(arg, arm, i);
						break;
					case BOW:
						this.applyHandOffset(arg, arm, i);
						arg.method_22904((double)((float)p * -0.2785682F), 0.18344387F, 0.15731531F);
						arg.method_22907(Vector3f.field_20703.method_23214(-13.935F, true));
						arg.method_22907(Vector3f.field_20705.method_23214((float)p * 35.3F, true));
						arg.method_22907(Vector3f.field_20707.method_23214((float)p * -9.785F, true));
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
							arg.method_22904((double)(n * 0.0F), (double)(n * 0.004F), (double)(n * 0.0F));
						}

						arg.method_22904((double)(kxx * 0.0F), (double)(kxx * 0.0F), (double)(kxx * 0.04F));
						arg.method_22905(1.0F, 1.0F, 1.0F + kxx * 0.2F);
						arg.method_22907(Vector3f.field_20704.method_23214((float)p * 45.0F, true));
						break;
					case SPEAR:
						this.applyHandOffset(arg, arm, i);
						arg.method_22904((double)((float)p * -0.5F), 0.7F, 0.1F);
						arg.method_22907(Vector3f.field_20703.method_23214(-55.0F, true));
						arg.method_22907(Vector3f.field_20705.method_23214((float)p * 35.3F, true));
						arg.method_22907(Vector3f.field_20707.method_23214((float)p * -9.785F, true));
						float q = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0F);
						float kx = q / 10.0F;
						if (kx > 1.0F) {
							kx = 1.0F;
						}

						if (kx > 0.1F) {
							float lx = MathHelper.sin((q - 0.1F) * 1.3F);
							float m = kx - 0.1F;
							float n = lx * m;
							arg.method_22904((double)(n * 0.0F), (double)(n * 0.004F), (double)(n * 0.0F));
						}

						arg.method_22904(0.0, 0.0, (double)(kx * 0.2F));
						arg.method_22905(1.0F, 1.0F, 1.0F + kx * 0.2F);
						arg.method_22907(Vector3f.field_20704.method_23214((float)p * 45.0F, true));
				}
			} else if (abstractClientPlayerEntity.isUsingRiptide()) {
				this.applyHandOffset(arg, arm, i);
				int p = bl2 ? 1 : -1;
				arg.method_22904((double)((float)p * -0.4F), 0.8F, 0.3F);
				arg.method_22907(Vector3f.field_20705.method_23214((float)p * 65.0F, true));
				arg.method_22907(Vector3f.field_20707.method_23214((float)p * -85.0F, true));
			} else {
				float r = -0.4F * MathHelper.sin(MathHelper.sqrt(h) * (float) Math.PI);
				float qxx = 0.2F * MathHelper.sin(MathHelper.sqrt(h) * (float) (Math.PI * 2));
				float kxxx = -0.2F * MathHelper.sin(h * (float) Math.PI);
				int s = bl2 ? 1 : -1;
				arg.method_22904((double)((float)s * r), (double)qxx, (double)kxxx);
				this.applyHandOffset(arg, arm, i);
				this.method_3217(arg, arm, h);
			}

			this.renderItem(
				abstractClientPlayerEntity,
				itemStack,
				bl2 ? ModelTransformation.Type.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Type.FIRST_PERSON_LEFT_HAND,
				!bl2,
				arg,
				arg2
			);
		}

		arg.method_22909();
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
