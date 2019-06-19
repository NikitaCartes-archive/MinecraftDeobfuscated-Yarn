package net.minecraft.client.render;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
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
import net.minecraft.util.AbsoluteHand;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class FirstPersonRenderer {
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

	public FirstPersonRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.renderManager = minecraftClient.getEntityRenderManager();
		this.itemRenderer = minecraftClient.getItemRenderer();
	}

	public void renderItem(LivingEntity livingEntity, ItemStack itemStack, ModelTransformation.Type type) {
		this.renderItemFromSide(livingEntity, itemStack, type, false);
	}

	public void renderItemFromSide(LivingEntity livingEntity, ItemStack itemStack, ModelTransformation.Type type, boolean bl) {
		if (!itemStack.isEmpty()) {
			Item item = itemStack.getItem();
			Block block = Block.getBlockFromItem(item);
			GlStateManager.pushMatrix();
			boolean bl2 = this.itemRenderer.hasDepthInGui(itemStack) && block.getRenderLayer() == BlockRenderLayer.field_9179;
			if (bl2) {
				GlStateManager.depthMask(false);
			}

			this.itemRenderer.renderHeldItem(itemStack, livingEntity, type, bl);
			if (bl2) {
				GlStateManager.depthMask(true);
			}

			GlStateManager.popMatrix();
		}
	}

	private void rotate(float f, float g) {
		GlStateManager.pushMatrix();
		GlStateManager.rotatef(f, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(g, 0.0F, 1.0F, 0.0F);
		GuiLighting.enable();
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

	private void applyCameraAngles(float f) {
		ClientPlayerEntity clientPlayerEntity = this.client.player;
		float g = MathHelper.lerp(f, clientPlayerEntity.lastRenderPitch, clientPlayerEntity.renderPitch);
		float h = MathHelper.lerp(f, clientPlayerEntity.lastRenderYaw, clientPlayerEntity.renderYaw);
		GlStateManager.rotatef((clientPlayerEntity.getPitch(f) - g) * 0.1F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef((clientPlayerEntity.getYaw(f) - h) * 0.1F, 0.0F, 1.0F, 0.0F);
	}

	private float getMapAngle(float f) {
		float g = 1.0F - f / 45.0F + 0.1F;
		g = MathHelper.clamp(g, 0.0F, 1.0F);
		return -MathHelper.cos(g * (float) Math.PI) * 0.5F + 0.5F;
	}

	private void renderArms() {
		if (!this.client.player.isInvisible()) {
			GlStateManager.disableCull();
			GlStateManager.pushMatrix();
			GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
			this.renderArm(AbsoluteHand.field_6183);
			this.renderArm(AbsoluteHand.field_6182);
			GlStateManager.popMatrix();
			GlStateManager.enableCull();
		}
	}

	private void renderArm(AbsoluteHand absoluteHand) {
		this.client.getTextureManager().bindTexture(this.client.player.getSkinTexture());
		EntityRenderer<AbstractClientPlayerEntity> entityRenderer = this.renderManager.getRenderer(this.client.player);
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)entityRenderer;
		GlStateManager.pushMatrix();
		float f = absoluteHand == AbsoluteHand.field_6183 ? 1.0F : -1.0F;
		GlStateManager.rotatef(92.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(45.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(f * -41.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translatef(f * 0.3F, -1.1F, 0.45F);
		if (absoluteHand == AbsoluteHand.field_6183) {
			playerEntityRenderer.renderRightArm(this.client.player);
		} else {
			playerEntityRenderer.renderLeftArm(this.client.player);
		}

		GlStateManager.popMatrix();
	}

	private void renderMapInOneHand(float f, AbsoluteHand absoluteHand, float g, ItemStack itemStack) {
		float h = absoluteHand == AbsoluteHand.field_6183 ? 1.0F : -1.0F;
		GlStateManager.translatef(h * 0.125F, -0.125F, 0.0F);
		if (!this.client.player.isInvisible()) {
			GlStateManager.pushMatrix();
			GlStateManager.rotatef(h * 10.0F, 0.0F, 0.0F, 1.0F);
			this.renderArmHoldingItem(f, g, absoluteHand);
			GlStateManager.popMatrix();
		}

		GlStateManager.pushMatrix();
		GlStateManager.translatef(h * 0.51F, -0.08F + f * -1.2F, -0.75F);
		float i = MathHelper.sqrt(g);
		float j = MathHelper.sin(i * (float) Math.PI);
		float k = -0.5F * j;
		float l = 0.4F * MathHelper.sin(i * (float) (Math.PI * 2));
		float m = -0.3F * MathHelper.sin(g * (float) Math.PI);
		GlStateManager.translatef(h * k, l - 0.3F * j, m);
		GlStateManager.rotatef(j * -45.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(h * j * -30.0F, 0.0F, 1.0F, 0.0F);
		this.renderFirstPersonMap(itemStack);
		GlStateManager.popMatrix();
	}

	private void renderMapInBothHands(float f, float g, float h) {
		float i = MathHelper.sqrt(h);
		float j = -0.2F * MathHelper.sin(h * (float) Math.PI);
		float k = -0.4F * MathHelper.sin(i * (float) Math.PI);
		GlStateManager.translatef(0.0F, -j / 2.0F, k);
		float l = this.getMapAngle(f);
		GlStateManager.translatef(0.0F, 0.04F + g * -1.2F + l * -0.5F, -0.72F);
		GlStateManager.rotatef(l * -85.0F, 1.0F, 0.0F, 0.0F);
		this.renderArms();
		float m = MathHelper.sin(i * (float) Math.PI);
		GlStateManager.rotatef(m * 20.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.scalef(2.0F, 2.0F, 2.0F);
		this.renderFirstPersonMap(this.mainHand);
	}

	private void renderFirstPersonMap(ItemStack itemStack) {
		GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.scalef(0.38F, 0.38F, 0.38F);
		GlStateManager.disableLighting();
		this.client.getTextureManager().bindTexture(MAP_BACKGROUND_TEX);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		GlStateManager.translatef(-0.5F, -0.5F, 0.0F);
		GlStateManager.scalef(0.0078125F, 0.0078125F, 0.0078125F);
		bufferBuilder.begin(7, VertexFormats.POSITION_UV);
		bufferBuilder.vertex(-7.0, 135.0, 0.0).texture(0.0, 1.0).next();
		bufferBuilder.vertex(135.0, 135.0, 0.0).texture(1.0, 1.0).next();
		bufferBuilder.vertex(135.0, -7.0, 0.0).texture(1.0, 0.0).next();
		bufferBuilder.vertex(-7.0, -7.0, 0.0).texture(0.0, 0.0).next();
		tessellator.draw();
		MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, this.client.world);
		if (mapState != null) {
			this.client.gameRenderer.getMapRenderer().draw(mapState, false);
		}

		GlStateManager.enableLighting();
	}

	private void renderArmHoldingItem(float f, float g, AbsoluteHand absoluteHand) {
		boolean bl = absoluteHand != AbsoluteHand.field_6182;
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

	private void applyEatOrDrinkTransformation(float f, AbsoluteHand absoluteHand, ItemStack itemStack) {
		float g = (float)this.client.player.getItemUseTimeLeft() - f + 1.0F;
		float h = g / (float)itemStack.getMaxUseTime();
		if (h < 0.8F) {
			float i = MathHelper.abs(MathHelper.cos(g / 4.0F * (float) Math.PI) * 0.1F);
			GlStateManager.translatef(0.0F, i, 0.0F);
		}

		float i = 1.0F - (float)Math.pow((double)h, 27.0);
		int j = absoluteHand == AbsoluteHand.field_6183 ? 1 : -1;
		GlStateManager.translatef(i * 0.6F * (float)j, i * -0.5F, i * 0.0F);
		GlStateManager.rotatef((float)j * i * 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(i * 10.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef((float)j * i * 30.0F, 0.0F, 0.0F, 1.0F);
	}

	private void method_3217(AbsoluteHand absoluteHand, float f) {
		int i = absoluteHand == AbsoluteHand.field_6183 ? 1 : -1;
		float g = MathHelper.sin(f * f * (float) Math.PI);
		GlStateManager.rotatef((float)i * (45.0F + g * -20.0F), 0.0F, 1.0F, 0.0F);
		float h = MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI);
		GlStateManager.rotatef((float)i * h * -20.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotatef(h * -80.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef((float)i * -45.0F, 0.0F, 1.0F, 0.0F);
	}

	private void applyHandOffset(AbsoluteHand absoluteHand, float f) {
		int i = absoluteHand == AbsoluteHand.field_6183 ? 1 : -1;
		GlStateManager.translatef((float)i * 0.56F, -0.52F + f * -0.6F, -0.72F);
	}

	public void renderFirstPersonItem(float f) {
		AbstractClientPlayerEntity abstractClientPlayerEntity = this.client.player;
		float g = abstractClientPlayerEntity.getHandSwingProgress(f);
		Hand hand = MoreObjects.firstNonNull(abstractClientPlayerEntity.preferredHand, Hand.field_5808);
		float h = MathHelper.lerp(f, abstractClientPlayerEntity.prevPitch, abstractClientPlayerEntity.pitch);
		float i = MathHelper.lerp(f, abstractClientPlayerEntity.prevYaw, abstractClientPlayerEntity.yaw);
		boolean bl = true;
		boolean bl2 = true;
		if (abstractClientPlayerEntity.isUsingItem()) {
			ItemStack itemStack = abstractClientPlayerEntity.getActiveItem();
			if (itemStack.getItem() == Items.field_8102 || itemStack.getItem() == Items.field_8399) {
				bl = abstractClientPlayerEntity.getActiveHand() == Hand.field_5808;
				bl2 = !bl;
			}

			Hand hand2 = abstractClientPlayerEntity.getActiveHand();
			if (hand2 == Hand.field_5808) {
				ItemStack itemStack2 = abstractClientPlayerEntity.getOffHandStack();
				if (itemStack2.getItem() == Items.field_8399 && CrossbowItem.isCharged(itemStack2)) {
					bl2 = false;
				}
			}
		} else {
			ItemStack itemStackx = abstractClientPlayerEntity.getMainHandStack();
			ItemStack itemStack3 = abstractClientPlayerEntity.getOffHandStack();
			if (itemStackx.getItem() == Items.field_8399 && CrossbowItem.isCharged(itemStackx)) {
				bl2 = !bl;
			}

			if (itemStack3.getItem() == Items.field_8399 && CrossbowItem.isCharged(itemStack3)) {
				bl = !itemStackx.isEmpty();
				bl2 = !bl;
			}
		}

		this.rotate(h, i);
		this.applyLightmap();
		this.applyCameraAngles(f);
		GlStateManager.enableRescaleNormal();
		if (bl) {
			float j = hand == Hand.field_5808 ? g : 0.0F;
			float k = 1.0F - MathHelper.lerp(f, this.prevEquipProgressMainHand, this.equipProgressMainHand);
			this.renderFirstPersonItem(abstractClientPlayerEntity, f, h, Hand.field_5808, j, this.mainHand, k);
		}

		if (bl2) {
			float j = hand == Hand.field_5810 ? g : 0.0F;
			float k = 1.0F - MathHelper.lerp(f, this.prevEquipProgressOffHand, this.equipProgressOffHand);
			this.renderFirstPersonItem(abstractClientPlayerEntity, f, h, Hand.field_5810, j, this.offHand, k);
		}

		GlStateManager.disableRescaleNormal();
		GuiLighting.disable();
	}

	public void renderFirstPersonItem(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, Hand hand, float h, ItemStack itemStack, float i) {
		boolean bl = hand == Hand.field_5808;
		AbsoluteHand absoluteHand = bl ? abstractClientPlayerEntity.getMainHand() : abstractClientPlayerEntity.getMainHand().getOpposite();
		GlStateManager.pushMatrix();
		if (itemStack.isEmpty()) {
			if (bl && !abstractClientPlayerEntity.isInvisible()) {
				this.renderArmHoldingItem(i, h, absoluteHand);
			}
		} else if (itemStack.getItem() == Items.field_8204) {
			if (bl && this.offHand.isEmpty()) {
				this.renderMapInBothHands(g, i, h);
			} else {
				this.renderMapInOneHand(i, absoluteHand, h, itemStack);
			}
		} else if (itemStack.getItem() == Items.field_8399) {
			boolean bl2 = CrossbowItem.isCharged(itemStack);
			boolean bl3 = absoluteHand == AbsoluteHand.field_6183;
			int j = bl3 ? 1 : -1;
			if (abstractClientPlayerEntity.isUsingItem() && abstractClientPlayerEntity.getItemUseTimeLeft() > 0 && abstractClientPlayerEntity.getActiveHand() == hand) {
				this.applyHandOffset(absoluteHand, i);
				GlStateManager.translatef((float)j * -0.4785682F, -0.094387F, 0.05731531F);
				GlStateManager.rotatef(-11.935F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef((float)j * 65.3F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotatef((float)j * -9.785F, 0.0F, 0.0F, 1.0F);
				float k = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0F);
				float l = k / (float)CrossbowItem.getPullTime(itemStack);
				if (l > 1.0F) {
					l = 1.0F;
				}

				if (l > 0.1F) {
					float m = MathHelper.sin((k - 0.1F) * 1.3F);
					float n = l - 0.1F;
					float o = m * n;
					GlStateManager.translatef(o * 0.0F, o * 0.004F, o * 0.0F);
				}

				GlStateManager.translatef(l * 0.0F, l * 0.0F, l * 0.04F);
				GlStateManager.scalef(1.0F, 1.0F, 1.0F + l * 0.2F);
				GlStateManager.rotatef((float)j * 45.0F, 0.0F, -1.0F, 0.0F);
			} else {
				float kx = -0.4F * MathHelper.sin(MathHelper.sqrt(h) * (float) Math.PI);
				float lx = 0.2F * MathHelper.sin(MathHelper.sqrt(h) * (float) (Math.PI * 2));
				float m = -0.2F * MathHelper.sin(h * (float) Math.PI);
				GlStateManager.translatef((float)j * kx, lx, m);
				this.applyHandOffset(absoluteHand, i);
				this.method_3217(absoluteHand, h);
				if (bl2 && h < 0.001F) {
					GlStateManager.translatef((float)j * -0.641864F, 0.0F, 0.0F);
					GlStateManager.rotatef((float)j * 10.0F, 0.0F, 1.0F, 0.0F);
				}
			}

			this.renderItemFromSide(abstractClientPlayerEntity, itemStack, bl3 ? ModelTransformation.Type.field_4322 : ModelTransformation.Type.field_4321, !bl3);
		} else {
			boolean bl2 = absoluteHand == AbsoluteHand.field_6183;
			if (abstractClientPlayerEntity.isUsingItem() && abstractClientPlayerEntity.getItemUseTimeLeft() > 0 && abstractClientPlayerEntity.getActiveHand() == hand) {
				int p = bl2 ? 1 : -1;
				switch (itemStack.getUseAction()) {
					case field_8952:
						this.applyHandOffset(absoluteHand, i);
						break;
					case field_8950:
					case field_8946:
						this.applyEatOrDrinkTransformation(f, absoluteHand, itemStack);
						this.applyHandOffset(absoluteHand, i);
						break;
					case field_8949:
						this.applyHandOffset(absoluteHand, i);
						break;
					case field_8953:
						this.applyHandOffset(absoluteHand, i);
						GlStateManager.translatef((float)p * -0.2785682F, 0.18344387F, 0.15731531F);
						GlStateManager.rotatef(-13.935F, 1.0F, 0.0F, 0.0F);
						GlStateManager.rotatef((float)p * 35.3F, 0.0F, 1.0F, 0.0F);
						GlStateManager.rotatef((float)p * -9.785F, 0.0F, 0.0F, 1.0F);
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
							GlStateManager.translatef(n * 0.0F, n * 0.004F, n * 0.0F);
						}

						GlStateManager.translatef(kxx * 0.0F, kxx * 0.0F, kxx * 0.04F);
						GlStateManager.scalef(1.0F, 1.0F, 1.0F + kxx * 0.2F);
						GlStateManager.rotatef((float)p * 45.0F, 0.0F, -1.0F, 0.0F);
						break;
					case field_8951:
						this.applyHandOffset(absoluteHand, i);
						GlStateManager.translatef((float)p * -0.5F, 0.7F, 0.1F);
						GlStateManager.rotatef(-55.0F, 1.0F, 0.0F, 0.0F);
						GlStateManager.rotatef((float)p * 35.3F, 0.0F, 1.0F, 0.0F);
						GlStateManager.rotatef((float)p * -9.785F, 0.0F, 0.0F, 1.0F);
						float q = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0F);
						float kx = q / 10.0F;
						if (kx > 1.0F) {
							kx = 1.0F;
						}

						if (kx > 0.1F) {
							float lx = MathHelper.sin((q - 0.1F) * 1.3F);
							float m = kx - 0.1F;
							float n = lx * m;
							GlStateManager.translatef(n * 0.0F, n * 0.004F, n * 0.0F);
						}

						GlStateManager.translatef(0.0F, 0.0F, kx * 0.2F);
						GlStateManager.scalef(1.0F, 1.0F, 1.0F + kx * 0.2F);
						GlStateManager.rotatef((float)p * 45.0F, 0.0F, -1.0F, 0.0F);
				}
			} else if (abstractClientPlayerEntity.isUsingRiptide()) {
				this.applyHandOffset(absoluteHand, i);
				int p = bl2 ? 1 : -1;
				GlStateManager.translatef((float)p * -0.4F, 0.8F, 0.3F);
				GlStateManager.rotatef((float)p * 65.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotatef((float)p * -85.0F, 0.0F, 0.0F, 1.0F);
			} else {
				float r = -0.4F * MathHelper.sin(MathHelper.sqrt(h) * (float) Math.PI);
				float qxx = 0.2F * MathHelper.sin(MathHelper.sqrt(h) * (float) (Math.PI * 2));
				float kxxx = -0.2F * MathHelper.sin(h * (float) Math.PI);
				int s = bl2 ? 1 : -1;
				GlStateManager.translatef((float)s * r, qxx, kxxx);
				this.applyHandOffset(absoluteHand, i);
				this.method_3217(absoluteHand, h);
			}

			this.renderItemFromSide(abstractClientPlayerEntity, itemStack, bl2 ? ModelTransformation.Type.field_4322 : ModelTransformation.Type.field_4321, !bl2);
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

			if (blockState.getRenderType() != BlockRenderType.field_11455) {
				this.renderBlock(this.client.getBlockRenderManager().getModels().getSprite(blockState));
			}
		}

		if (!this.client.player.isSpectator()) {
			if (this.client.player.isInFluid(FluidTags.field_15517)) {
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
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
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
		bufferBuilder.begin(7, VertexFormats.POSITION_UV);
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
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
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
		bufferBuilder.begin(7, VertexFormats.POSITION_UV);
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
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
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
			bufferBuilder.begin(7, VertexFormats.POSITION_UV);
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
		if (hand == Hand.field_5808) {
			this.equipProgressMainHand = 0.0F;
		} else {
			this.equipProgressOffHand = 0.0F;
		}
	}
}
