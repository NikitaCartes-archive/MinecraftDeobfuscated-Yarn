package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class ArmorEntityRenderer<T extends Model> implements LayerEntityRenderer<LivingEntity> {
	protected static final Identifier SKIN = new Identifier("textures/misc/enchanted_item_glint.png");
	protected T modelLeggings;
	protected T modelBody;
	private final LivingEntityRenderer<?> renderer;
	private float alpha = 1.0F;
	private float red = 1.0F;
	private float green = 1.0F;
	private float blue = 1.0F;
	private boolean ignoreGlint;
	private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.<String, Identifier>newHashMap();

	public ArmorEntityRenderer(LivingEntityRenderer<?> livingEntityRenderer) {
		this.renderer = livingEntityRenderer;
		this.init();
	}

	@Override
	public void render(LivingEntity livingEntity, float f, float g, float h, float i, float j, float k, float l) {
		this.renderArmor(livingEntity, f, g, h, i, j, k, l, EquipmentSlot.CHEST);
		this.renderArmor(livingEntity, f, g, h, i, j, k, l, EquipmentSlot.LEGS);
		this.renderArmor(livingEntity, f, g, h, i, j, k, l, EquipmentSlot.FEET);
		this.renderArmor(livingEntity, f, g, h, i, j, k, l, EquipmentSlot.HEAD);
	}

	@Override
	public boolean shouldMergeTextures() {
		return false;
	}

	private void renderArmor(LivingEntity livingEntity, float f, float g, float h, float i, float j, float k, float l, EquipmentSlot equipmentSlot) {
		ItemStack itemStack = livingEntity.getEquippedStack(equipmentSlot);
		if (itemStack.getItem() instanceof ArmorItem) {
			ArmorItem armorItem = (ArmorItem)itemStack.getItem();
			if (armorItem.getSlotType() == equipmentSlot) {
				T model = this.getArmor(equipmentSlot);
				model.setAttributes(this.renderer.method_4038());
				model.animateModel(livingEntity, f, g, h);
				this.method_4170(model, equipmentSlot);
				boolean bl = this.isLegs(equipmentSlot);
				this.renderer.bindTexture(this.getArmorTexture(armorItem, bl));
				if (armorItem instanceof DyeableArmorItem) {
					int m = ((DyeableArmorItem)armorItem).getColor(itemStack);
					float n = (float)(m >> 16 & 0xFF) / 255.0F;
					float o = (float)(m >> 8 & 0xFF) / 255.0F;
					float p = (float)(m & 0xFF) / 255.0F;
					GlStateManager.color4f(this.red * n, this.green * o, this.blue * p, this.alpha);
					model.render(livingEntity, f, g, i, j, k, l);
					this.renderer.bindTexture(this.method_4174(armorItem, bl, "overlay"));
				}

				GlStateManager.color4f(this.red, this.green, this.blue, this.alpha);
				model.render(livingEntity, f, g, i, j, k, l);
				if (!this.ignoreGlint && itemStack.hasEnchantments()) {
					renderEnchantedGlint(this.renderer, livingEntity, model, f, g, h, i, j, k, l);
				}
			}
		}
	}

	public T getArmor(EquipmentSlot equipmentSlot) {
		return this.isLegs(equipmentSlot) ? this.modelLeggings : this.modelBody;
	}

	private boolean isLegs(EquipmentSlot equipmentSlot) {
		return equipmentSlot == EquipmentSlot.LEGS;
	}

	public static void renderEnchantedGlint(
		LivingEntityRenderer<?> livingEntityRenderer, LivingEntity livingEntity, Model model, float f, float g, float h, float i, float j, float k, float l
	) {
		float m = (float)livingEntity.age + h;
		livingEntityRenderer.bindTexture(SKIN);
		MinecraftClient.getInstance().worldRenderer.method_3201(true);
		GlStateManager.enableBlend();
		GlStateManager.depthFunc(514);
		GlStateManager.depthMask(false);
		float n = 0.5F;
		GlStateManager.color4f(0.5F, 0.5F, 0.5F, 1.0F);

		for (int o = 0; o < 2; o++) {
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(GlStateManager.SrcBlendFactor.SRC_COLOR, GlStateManager.DstBlendFactor.ONE);
			float p = 0.76F;
			GlStateManager.color4f(0.38F, 0.19F, 0.608F, 1.0F);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			float q = 0.33333334F;
			GlStateManager.scalef(0.33333334F, 0.33333334F, 0.33333334F);
			GlStateManager.rotatef(30.0F - (float)o * 60.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translatef(0.0F, m * (0.001F + (float)o * 0.003F) * 20.0F, 0.0F);
			GlStateManager.matrixMode(5888);
			model.render(livingEntity, f, g, i, j, k, l);
			GlStateManager.blendFunc(GlStateManager.SrcBlendFactor.ONE, GlStateManager.DstBlendFactor.ZERO);
		}

		GlStateManager.matrixMode(5890);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(5888);
		GlStateManager.enableLighting();
		GlStateManager.depthMask(true);
		GlStateManager.depthFunc(515);
		GlStateManager.disableBlend();
		MinecraftClient.getInstance().worldRenderer.method_3201(false);
	}

	private Identifier getArmorTexture(ArmorItem armorItem, boolean bl) {
		return this.method_4174(armorItem, bl, null);
	}

	private Identifier method_4174(ArmorItem armorItem, boolean bl, @Nullable String string) {
		String string2 = "textures/models/armor/" + armorItem.getMaterial().method_7694() + "_layer_" + (bl ? 2 : 1) + (string == null ? "" : "_" + string) + ".png";
		return (Identifier)ARMOR_TEXTURE_CACHE.computeIfAbsent(string2, Identifier::new);
	}

	protected abstract void init();

	protected abstract void method_4170(T model, EquipmentSlot equipmentSlot);
}
