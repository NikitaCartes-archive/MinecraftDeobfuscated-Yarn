package net.minecraft.client.render.item;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class HeldItemRenderer {
	private static final Identifier MAP_BACKGROUND_TEX = new Identifier("textures/map/map_background.png");
	private static final Identifier UNDERWATER_TEX = new Identifier("textures/misc/underwater.png");
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

	public void renderItem(LivingEntity holder, ItemStack stack, ModelTransformation.Type type) {
		this.renderItemFromSide(holder, stack, type, false);
	}

	public void renderItemFromSide(LivingEntity holder, ItemStack stack, ModelTransformation.Type transformation, boolean bl) {
		if (!stack.isEmpty()) {
			Item item = stack.getItem();
			Block block = Block.getBlockFromItem(item);
			GlStateManager.pushMatrix();
			boolean bl2 = this.itemRenderer.hasDepthInGui(stack) && block.getRenderLayer() == RenderLayer.TRANSLUCENT;
			if (bl2) {
				GlStateManager.depthMask(false);
			}

			this.itemRenderer.renderHeldItem(stack, holder, transformation, bl);
			if (bl2) {
				GlStateManager.depthMask(true);
			}

			GlStateManager.popMatrix();
		}
	}

	private void rotate(float pitch, float yaw) {
		GlStateManager.pushMatrix();
		GlStateManager.rotatef(pitch, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(yaw, 0.0F, 1.0F, 0.0F);
		DiffuseLighting.enable();
		GlStateManager.popMatrix();
	}

	private void applyLightmap() {
		AbstractClientPlayerEntity abstractClientPlayerEntity = this.client.player;
		int i = this.client
			.world
			.getLightmapIndex(
				new BlockPos(
					abstractClientPlayerEntity.x, abstractClientPlayerEntity.y + (double)abstractClientPlayerEntity.getStandingEyeHeight(), abstractClientPlayerEntity.z
				),
				0
			);
		float f = (float)(i & 65535);
		float g = (float)(i >> 16);
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, f, g);
	}

	private void applyCameraAngles(float tickDelta) {
		ClientPlayerEntity clientPlayerEntity = this.client.player;
		float f = MathHelper.lerp(tickDelta, clientPlayerEntity.lastRenderPitch, clientPlayerEntity.renderPitch);
		float g = MathHelper.lerp(tickDelta, clientPlayerEntity.lastRenderYaw, clientPlayerEntity.renderYaw);
		GlStateManager.rotatef((clientPlayerEntity.getPitch(tickDelta) - f) * 0.1F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef((clientPlayerEntity.getYaw(tickDelta) - g) * 0.1F, 0.0F, 1.0F, 0.0F);
	}

	private float getMapAngle(float tickDelta) {
		float f = 1.0F - tickDelta / 45.0F + 0.1F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		return -MathHelper.cos(f * (float) Math.PI) * 0.5F + 0.5F;
	}

	private void renderArms() {
		if (!this.client.player.isInvisible()) {
			GlStateManager.disableCull();
			GlStateManager.pushMatrix();
			GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
			this.renderArm(Arm.RIGHT);
			this.renderArm(Arm.LEFT);
			GlStateManager.popMatrix();
			GlStateManager.enableCull();
		}
	}

	private void renderArm(Arm arm) {
		this.client.getTextureManager().bindTexture(this.client.player.getSkinTexture());
		EntityRenderer<AbstractClientPlayerEntity> entityRenderer = this.renderManager.getRenderer(this.client.player);
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)entityRenderer;
		GlStateManager.pushMatrix();
		float f = arm == Arm.RIGHT ? 1.0F : -1.0F;
		GlStateManager.rotatef(92.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(45.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(f * -41.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translatef(f * 0.3F, -1.1F, 0.45F);
		if (arm == Arm.RIGHT) {
			playerEntityRenderer.renderRightArm(this.client.player);
		} else {
			playerEntityRenderer.renderLeftArm(this.client.player);
		}

		GlStateManager.popMatrix();
	}

	private void renderMapInOneHand(float equipProgress, Arm hand, float f, ItemStack item) {
		float g = hand == Arm.RIGHT ? 1.0F : -1.0F;
		GlStateManager.translatef(g * 0.125F, -0.125F, 0.0F);
		if (!this.client.player.isInvisible()) {
			GlStateManager.pushMatrix();
			GlStateManager.rotatef(g * 10.0F, 0.0F, 0.0F, 1.0F);
			this.renderArmHoldingItem(equipProgress, f, hand);
			GlStateManager.popMatrix();
		}

		GlStateManager.pushMatrix();
		GlStateManager.translatef(g * 0.51F, -0.08F + equipProgress * -1.2F, -0.75F);
		float h = MathHelper.sqrt(f);
		float i = MathHelper.sin(h * (float) Math.PI);
		float j = -0.5F * i;
		float k = 0.4F * MathHelper.sin(h * (float) (Math.PI * 2));
		float l = -0.3F * MathHelper.sin(f * (float) Math.PI);
		GlStateManager.translatef(g * j, k - 0.3F * i, l);
		GlStateManager.rotatef(i * -45.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(g * i * -30.0F, 0.0F, 1.0F, 0.0F);
		this.renderFirstPersonMap(item);
		GlStateManager.popMatrix();
	}

	private void renderMapInBothHands(float pitch, float equipProgress, float f) {
		float g = MathHelper.sqrt(f);
		float h = -0.2F * MathHelper.sin(f * (float) Math.PI);
		float i = -0.4F * MathHelper.sin(g * (float) Math.PI);
		GlStateManager.translatef(0.0F, -h / 2.0F, i);
		float j = this.getMapAngle(pitch);
		GlStateManager.translatef(0.0F, 0.04F + equipProgress * -1.2F + j * -0.5F, -0.72F);
		GlStateManager.rotatef(j * -85.0F, 1.0F, 0.0F, 0.0F);
		this.renderArms();
		float k = MathHelper.sin(g * (float) Math.PI);
		GlStateManager.rotatef(k * 20.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.scalef(2.0F, 2.0F, 2.0F);
		this.renderFirstPersonMap(this.mainHand);
	}

	private void renderFirstPersonMap(ItemStack map) {
		GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.scalef(0.38F, 0.38F, 0.38F);
		GlStateManager.disableLighting();
		this.client.getTextureManager().bindTexture(MAP_BACKGROUND_TEX);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		GlStateManager.translatef(-0.5F, -0.5F, 0.0F);
		GlStateManager.scalef(0.0078125F, 0.0078125F, 0.0078125F);
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(-7.0, 135.0, 0.0).texture(0.0, 1.0).next();
		bufferBuilder.vertex(135.0, 135.0, 0.0).texture(1.0, 1.0).next();
		bufferBuilder.vertex(135.0, -7.0, 0.0).texture(1.0, 0.0).next();
		bufferBuilder.vertex(-7.0, -7.0, 0.0).texture(0.0, 0.0).next();
		tessellator.draw();
		MapState mapState = FilledMapItem.getOrCreateMapState(map, this.client.world);
		if (mapState != null) {
			this.client.gameRenderer.getMapRenderer().draw(mapState, false);
		}

		GlStateManager.enableLighting();
	}

	private void renderArmHoldingItem(float f, float g, Arm arm) {
		boolean bl = arm != Arm.LEFT;
		float h = bl ? 1.0F : -1.0F;
		float i = MathHelper.sqrt(g);
		float j = -0.3F * MathHelper.sin(i * (float) Math.PI);
		float k = 0.4F * MathHelper.sin(i * (float) (Math.PI * 2));
		float l = -0.4F * MathHelper.sin(g * (float) Math.PI);
		GlStateManager.translatef(h * (j + 0.64000005F), k + -0.6F + f * -0.6F, l + -0.71999997F);
		GlStateManager.rotatef(h * 45.0F, 0.0F, 1.0F, 0.0F);
		float m = MathHelper.sin(g * g * (float) Math.PI);
		float n = MathHelper.sin(i * (float) Math.PI);
		GlStateManager.rotatef(h * n * 70.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(h * m * -20.0F, 0.0F, 0.0F, 1.0F);
		AbstractClientPlayerEntity abstractClientPlayerEntity = this.client.player;
		this.client.getTextureManager().bindTexture(abstractClientPlayerEntity.getSkinTexture());
		GlStateManager.translatef(h * -1.0F, 3.6F, 3.5F);
		GlStateManager.rotatef(h * 120.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotatef(200.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(h * -135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(h * 5.6F, 0.0F, 0.0F);
		PlayerEntityRenderer playerEntityRenderer = this.renderManager.getRenderer(abstractClientPlayerEntity);
		GlStateManager.disableCull();
		if (bl) {
			playerEntityRenderer.renderRightArm(abstractClientPlayerEntity);
		} else {
			playerEntityRenderer.renderLeftArm(abstractClientPlayerEntity);
		}

		GlStateManager.enableCull();
	}

	private void applyEatOrDrinkTransformation(float tickDelta, Arm hand, ItemStack item) {
		float f = (float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F;
		float g = f / (float)item.getMaxUseTime();
		if (g < 0.8F) {
			float h = MathHelper.abs(MathHelper.cos(f / 4.0F * (float) Math.PI) * 0.1F);
			GlStateManager.translatef(0.0F, h, 0.0F);
		}

		float h = 1.0F - (float)Math.pow((double)g, 27.0);
		int i = hand == Arm.RIGHT ? 1 : -1;
		GlStateManager.translatef(h * 0.6F * (float)i, h * -0.5F, h * 0.0F);
		GlStateManager.rotatef((float)i * h * 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(h * 10.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef((float)i * h * 30.0F, 0.0F, 0.0F, 1.0F);
	}

	private void method_3217(Arm arm, float f) {
		int i = arm == Arm.RIGHT ? 1 : -1;
		float g = MathHelper.sin(f * f * (float) Math.PI);
		GlStateManager.rotatef((float)i * (45.0F + g * -20.0F), 0.0F, 1.0F, 0.0F);
		float h = MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI);
		GlStateManager.rotatef((float)i * h * -20.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotatef(h * -80.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef((float)i * -45.0F, 0.0F, 1.0F, 0.0F);
	}

	private void applyHandOffset(Arm hand, float f) {
		int i = hand == Arm.RIGHT ? 1 : -1;
		GlStateManager.translatef((float)i * 0.56F, -0.52F + f * -0.6F, -0.72F);
	}

	public void renderFirstPersonItem(float tickDelta) {
		AbstractClientPlayerEntity abstractClientPlayerEntity = this.client.player;
		float f = abstractClientPlayerEntity.getHandSwingProgress(tickDelta);
		Hand hand = MoreObjects.firstNonNull(abstractClientPlayerEntity.preferredHand, Hand.MAIN_HAND);
		float g = MathHelper.lerp(tickDelta, abstractClientPlayerEntity.prevPitch, abstractClientPlayerEntity.pitch);
		float h = MathHelper.lerp(tickDelta, abstractClientPlayerEntity.prevYaw, abstractClientPlayerEntity.yaw);
		boolean bl = true;
		boolean bl2 = true;
		if (abstractClientPlayerEntity.isUsingItem()) {
			ItemStack itemStack = abstractClientPlayerEntity.getActiveItem();
			if (itemStack.getItem() == Items.BOW || itemStack.getItem() == Items.CROSSBOW) {
				bl = abstractClientPlayerEntity.getActiveHand() == Hand.MAIN_HAND;
				bl2 = !bl;
			}

			Hand hand2 = abstractClientPlayerEntity.getActiveHand();
			if (hand2 == Hand.MAIN_HAND) {
				ItemStack itemStack2 = abstractClientPlayerEntity.getOffHandStack();
				if (itemStack2.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStack2)) {
					bl2 = false;
				}
			}
		} else {
			ItemStack itemStackx = abstractClientPlayerEntity.getMainHandStack();
			ItemStack itemStack3 = abstractClientPlayerEntity.getOffHandStack();
			if (itemStackx.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStackx)) {
				bl2 = !bl;
			}

			if (itemStack3.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStack3)) {
				bl = !itemStackx.isEmpty();
				bl2 = !bl;
			}
		}

		this.rotate(g, h);
		this.applyLightmap();
		this.applyCameraAngles(tickDelta);
		GlStateManager.enableRescaleNormal();
		if (bl) {
			float i = hand == Hand.MAIN_HAND ? f : 0.0F;
			float j = 1.0F - MathHelper.lerp(tickDelta, this.prevEquipProgressMainHand, this.equipProgressMainHand);
			this.renderFirstPersonItem(abstractClientPlayerEntity, tickDelta, g, Hand.MAIN_HAND, i, this.mainHand, j);
		}

		if (bl2) {
			float i = hand == Hand.OFF_HAND ? f : 0.0F;
			float j = 1.0F - MathHelper.lerp(tickDelta, this.prevEquipProgressOffHand, this.equipProgressOffHand);
			this.renderFirstPersonItem(abstractClientPlayerEntity, tickDelta, g, Hand.OFF_HAND, i, this.offHand, j);
		}

		GlStateManager.disableRescaleNormal();
		DiffuseLighting.disable();
	}

	public void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float f, ItemStack item, float equipProgress) {
		boolean bl = hand == Hand.MAIN_HAND;
		Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
		GlStateManager.pushMatrix();
		if (item.isEmpty()) {
			if (bl && !player.isInvisible()) {
				this.renderArmHoldingItem(equipProgress, f, arm);
			}
		} else if (item.getItem() == Items.FILLED_MAP) {
			if (bl && this.offHand.isEmpty()) {
				this.renderMapInBothHands(pitch, equipProgress, f);
			} else {
				this.renderMapInOneHand(equipProgress, arm, f, item);
			}
		} else if (item.getItem() == Items.CROSSBOW) {
			boolean bl2 = CrossbowItem.isCharged(item);
			boolean bl3 = arm == Arm.RIGHT;
			int i = bl3 ? 1 : -1;
			if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
				this.applyHandOffset(arm, equipProgress);
				GlStateManager.translatef((float)i * -0.4785682F, -0.094387F, 0.05731531F);
				GlStateManager.rotatef(-11.935F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef((float)i * 65.3F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotatef((float)i * -9.785F, 0.0F, 0.0F, 1.0F);
				float g = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F);
				float h = g / (float)CrossbowItem.getPullTime(item);
				if (h > 1.0F) {
					h = 1.0F;
				}

				if (h > 0.1F) {
					float j = MathHelper.sin((g - 0.1F) * 1.3F);
					float k = h - 0.1F;
					float l = j * k;
					GlStateManager.translatef(l * 0.0F, l * 0.004F, l * 0.0F);
				}

				GlStateManager.translatef(h * 0.0F, h * 0.0F, h * 0.04F);
				GlStateManager.scalef(1.0F, 1.0F, 1.0F + h * 0.2F);
				GlStateManager.rotatef((float)i * 45.0F, 0.0F, -1.0F, 0.0F);
			} else {
				float gx = -0.4F * MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI);
				float hx = 0.2F * MathHelper.sin(MathHelper.sqrt(f) * (float) (Math.PI * 2));
				float j = -0.2F * MathHelper.sin(f * (float) Math.PI);
				GlStateManager.translatef((float)i * gx, hx, j);
				this.applyHandOffset(arm, equipProgress);
				this.method_3217(arm, f);
				if (bl2 && f < 0.001F) {
					GlStateManager.translatef((float)i * -0.641864F, 0.0F, 0.0F);
					GlStateManager.rotatef((float)i * 10.0F, 0.0F, 1.0F, 0.0F);
				}
			}

			this.renderItemFromSide(player, item, bl3 ? ModelTransformation.Type.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Type.FIRST_PERSON_LEFT_HAND, !bl3);
		} else {
			boolean bl2 = arm == Arm.RIGHT;
			if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
				int m = bl2 ? 1 : -1;
				switch (item.getUseAction()) {
					case NONE:
						this.applyHandOffset(arm, equipProgress);
						break;
					case EAT:
					case DRINK:
						this.applyEatOrDrinkTransformation(tickDelta, arm, item);
						this.applyHandOffset(arm, equipProgress);
						break;
					case BLOCK:
						this.applyHandOffset(arm, equipProgress);
						break;
					case BOW:
						this.applyHandOffset(arm, equipProgress);
						GlStateManager.translatef((float)m * -0.2785682F, 0.18344387F, 0.15731531F);
						GlStateManager.rotatef(-13.935F, 1.0F, 0.0F, 0.0F);
						GlStateManager.rotatef((float)m * 35.3F, 0.0F, 1.0F, 0.0F);
						GlStateManager.rotatef((float)m * -9.785F, 0.0F, 0.0F, 1.0F);
						float nx = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F);
						float gxx = nx / 20.0F;
						gxx = (gxx * gxx + gxx * 2.0F) / 3.0F;
						if (gxx > 1.0F) {
							gxx = 1.0F;
						}

						if (gxx > 0.1F) {
							float hx = MathHelper.sin((nx - 0.1F) * 1.3F);
							float j = gxx - 0.1F;
							float k = hx * j;
							GlStateManager.translatef(k * 0.0F, k * 0.004F, k * 0.0F);
						}

						GlStateManager.translatef(gxx * 0.0F, gxx * 0.0F, gxx * 0.04F);
						GlStateManager.scalef(1.0F, 1.0F, 1.0F + gxx * 0.2F);
						GlStateManager.rotatef((float)m * 45.0F, 0.0F, -1.0F, 0.0F);
						break;
					case SPEAR:
						this.applyHandOffset(arm, equipProgress);
						GlStateManager.translatef((float)m * -0.5F, 0.7F, 0.1F);
						GlStateManager.rotatef(-55.0F, 1.0F, 0.0F, 0.0F);
						GlStateManager.rotatef((float)m * 35.3F, 0.0F, 1.0F, 0.0F);
						GlStateManager.rotatef((float)m * -9.785F, 0.0F, 0.0F, 1.0F);
						float n = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F);
						float gx = n / 10.0F;
						if (gx > 1.0F) {
							gx = 1.0F;
						}

						if (gx > 0.1F) {
							float hx = MathHelper.sin((n - 0.1F) * 1.3F);
							float j = gx - 0.1F;
							float k = hx * j;
							GlStateManager.translatef(k * 0.0F, k * 0.004F, k * 0.0F);
						}

						GlStateManager.translatef(0.0F, 0.0F, gx * 0.2F);
						GlStateManager.scalef(1.0F, 1.0F, 1.0F + gx * 0.2F);
						GlStateManager.rotatef((float)m * 45.0F, 0.0F, -1.0F, 0.0F);
				}
			} else if (player.isUsingRiptide()) {
				this.applyHandOffset(arm, equipProgress);
				int m = bl2 ? 1 : -1;
				GlStateManager.translatef((float)m * -0.4F, 0.8F, 0.3F);
				GlStateManager.rotatef((float)m * 65.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotatef((float)m * -85.0F, 0.0F, 0.0F, 1.0F);
			} else {
				float o = -0.4F * MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI);
				float nxx = 0.2F * MathHelper.sin(MathHelper.sqrt(f) * (float) (Math.PI * 2));
				float gxxx = -0.2F * MathHelper.sin(f * (float) Math.PI);
				int p = bl2 ? 1 : -1;
				GlStateManager.translatef((float)p * o, nxx, gxxx);
				this.applyHandOffset(arm, equipProgress);
				this.method_3217(arm, f);
			}

			this.renderItemFromSide(player, item, bl2 ? ModelTransformation.Type.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Type.FIRST_PERSON_LEFT_HAND, !bl2);
		}

		GlStateManager.popMatrix();
	}

	public void renderOverlays(float f) {
		GlStateManager.disableAlphaTest();
		if (this.client.player.isInsideWall()) {
			BlockState blockState = this.client.world.getBlockState(new BlockPos(this.client.player));
			PlayerEntity playerEntity = this.client.player;

			for (int i = 0; i < 8; i++) {
				double d = playerEntity.x + (double)(((float)((i >> 0) % 2) - 0.5F) * playerEntity.getWidth() * 0.8F);
				double e = playerEntity.y + (double)(((float)((i >> 1) % 2) - 0.5F) * 0.1F);
				double g = playerEntity.z + (double)(((float)((i >> 2) % 2) - 0.5F) * playerEntity.getWidth() * 0.8F);
				BlockPos blockPos = new BlockPos(d, e + (double)playerEntity.getStandingEyeHeight(), g);
				BlockState blockState2 = this.client.world.getBlockState(blockPos);
				if (blockState2.canSuffocate(this.client.world, blockPos)) {
					blockState = blockState2;
				}
			}

			if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
				this.renderBlock(this.client.getBlockRenderManager().getModels().getSprite(blockState));
			}
		}

		if (!this.client.player.isSpectator()) {
			if (this.client.player.isInFluid(FluidTags.WATER)) {
				this.renderWaterOverlay(f);
			}

			if (this.client.player.isOnFire()) {
				this.renderFireOverlay();
			}
		}

		GlStateManager.enableAlphaTest();
	}

	private void renderBlock(Sprite sprite) {
		this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		float f = 0.1F;
		GlStateManager.color4f(0.1F, 0.1F, 0.1F, 0.5F);
		GlStateManager.pushMatrix();
		float g = -1.0F;
		float h = 1.0F;
		float i = -1.0F;
		float j = 1.0F;
		float k = -0.5F;
		float l = sprite.getMinU();
		float m = sprite.getMaxU();
		float n = sprite.getMinV();
		float o = sprite.getMaxV();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(-1.0, -1.0, -0.5).texture((double)m, (double)o).next();
		bufferBuilder.vertex(1.0, -1.0, -0.5).texture((double)l, (double)o).next();
		bufferBuilder.vertex(1.0, 1.0, -0.5).texture((double)l, (double)n).next();
		bufferBuilder.vertex(-1.0, 1.0, -0.5).texture((double)m, (double)n).next();
		tessellator.draw();
		GlStateManager.popMatrix();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderWaterOverlay(float f) {
		this.client.getTextureManager().bindTexture(UNDERWATER_TEX);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		float g = this.client.player.getBrightnessAtEyes();
		GlStateManager.color4f(g, g, g, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.pushMatrix();
		float h = 4.0F;
		float i = -1.0F;
		float j = 1.0F;
		float k = -1.0F;
		float l = 1.0F;
		float m = -0.5F;
		float n = -this.client.player.yaw / 64.0F;
		float o = this.client.player.pitch / 64.0F;
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(-1.0, -1.0, -0.5).texture((double)(4.0F + n), (double)(4.0F + o)).next();
		bufferBuilder.vertex(1.0, -1.0, -0.5).texture((double)(0.0F + n), (double)(4.0F + o)).next();
		bufferBuilder.vertex(1.0, 1.0, -0.5).texture((double)(0.0F + n), (double)(0.0F + o)).next();
		bufferBuilder.vertex(-1.0, 1.0, -0.5).texture((double)(4.0F + n), (double)(0.0F + o)).next();
		tessellator.draw();
		GlStateManager.popMatrix();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
	}

	private void renderFireOverlay() {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.9F);
		GlStateManager.depthFunc(519);
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		float f = 1.0F;

		for (int i = 0; i < 2; i++) {
			GlStateManager.pushMatrix();
			Sprite sprite = this.client.getSpriteAtlas().getSprite(ModelLoader.FIRE_1);
			this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			float g = sprite.getMinU();
			float h = sprite.getMaxU();
			float j = sprite.getMinV();
			float k = sprite.getMaxV();
			float l = -0.5F;
			float m = 0.5F;
			float n = -0.5F;
			float o = 0.5F;
			float p = -0.5F;
			GlStateManager.translatef((float)(-(i * 2 - 1)) * 0.24F, -0.3F, 0.0F);
			GlStateManager.rotatef((float)(i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
			bufferBuilder.vertex(-0.5, -0.5, -0.5).texture((double)h, (double)k).next();
			bufferBuilder.vertex(0.5, -0.5, -0.5).texture((double)g, (double)k).next();
			bufferBuilder.vertex(0.5, 0.5, -0.5).texture((double)g, (double)j).next();
			bufferBuilder.vertex(-0.5, 0.5, -0.5).texture((double)h, (double)j).next();
			tessellator.draw();
			GlStateManager.popMatrix();
		}

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
		GlStateManager.depthFunc(515);
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
