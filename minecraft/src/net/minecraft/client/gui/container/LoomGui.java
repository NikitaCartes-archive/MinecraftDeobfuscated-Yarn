package net.minecraft.client.gui.container;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.client.texture.TextureCache;
import net.minecraft.container.LoomContainer;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.block.BannerItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TextComponent;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class LoomGui extends ContainerGui<LoomContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/loom.png");
	private static final int field_2963 = (BannerPattern.COUNT - 4 - 1 + 4 - 1) / 4;
	private static final DyeColor field_2964 = DyeColor.GRAY;
	private static final DyeColor field_2956 = DyeColor.WHITE;
	private static final List<DyeColor> field_2959 = Lists.<DyeColor>newArrayList(field_2964, field_2956);
	private Identifier field_2957;
	private ItemStack field_2955 = ItemStack.EMPTY;
	private ItemStack field_2954 = ItemStack.EMPTY;
	private ItemStack field_2967 = ItemStack.EMPTY;
	private final Identifier[] field_2972 = new Identifier[BannerPattern.COUNT];
	private boolean field_2965;
	private boolean field_2962;
	private boolean field_2961;
	private float field_2968;
	private boolean field_2958;
	private int field_2970 = 1;
	private int field_2969 = 1;

	public LoomGui(LoomContainer loomContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		super(loomContainer, playerInventory, textComponent);
		loomContainer.method_17423(this::method_17576);
	}

	@Override
	public void update() {
		super.update();
		if (this.field_2969 < BannerPattern.COUNT) {
			BannerPattern bannerPattern = BannerPattern.values()[this.field_2969];
			String string = "b" + field_2964.getId();
			String string2 = bannerPattern.getId() + field_2956.getId();
			this.field_2972[this.field_2969] = TextureCache.BANNER
				.get(string + string2, Lists.<BannerPattern>newArrayList(BannerPattern.BASE, bannerPattern), field_2959);
			this.field_2969++;
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		super.draw(i, j, f);
		this.drawMousoverTooltip(i, j);
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.fontRenderer.draw(this.name.getFormattedText(), 8.0F, 4.0F, 4210752);
		this.fontRenderer.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		this.drawBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int k = this.left;
		int l = this.top;
		this.drawTexturedRect(k, l, 0, 0, this.containerWidth, this.containerHeight);
		Slot slot = this.container.method_17428();
		Slot slot2 = this.container.method_17429();
		Slot slot3 = this.container.method_17430();
		Slot slot4 = this.container.method_17431();
		if (!slot.hasStack()) {
			this.drawTexturedRect(k + slot.xPosition, l + slot.yPosition, this.containerWidth, 0, 16, 16);
		}

		if (!slot2.hasStack()) {
			this.drawTexturedRect(k + slot2.xPosition, l + slot2.yPosition, this.containerWidth + 16, 0, 16, 16);
		}

		if (!slot3.hasStack()) {
			this.drawTexturedRect(k + slot3.xPosition, l + slot3.yPosition, this.containerWidth + 32, 0, 16, 16);
		}

		int m = (int)(41.0F * this.field_2968);
		this.drawTexturedRect(k + 119, l + 13 + m, 232 + (this.field_2965 ? 0 : 12), 0, 12, 15);
		if (this.field_2957 != null && !this.field_2961) {
			this.client.getTextureManager().bindTexture(this.field_2957);
			drawTexturedRect(k + 141, l + 8, 1.0F, 1.0F, 20, 40, 20, 40, 64.0F, 64.0F);
		} else if (this.field_2961) {
			this.drawTexturedRect(k + slot4.xPosition - 2, l + slot4.yPosition - 2, this.containerWidth, 17, 17, 16);
		}

		if (this.field_2965) {
			int n = k + 60;
			int o = l + 13;
			int p = this.field_2970 + 16;

			for (int q = this.field_2970; q < p && q < this.field_2972.length - 4; q++) {
				int r = q - this.field_2970;
				int s = n + r % 4 * 14;
				int t = o + r / 4 * 14;
				this.client.getTextureManager().bindTexture(TEXTURE);
				int u = this.containerHeight;
				if (q == this.container.method_7647()) {
					u += 14;
				} else if (i >= s && j >= t && i < s + 14 && j < t + 14) {
					u += 28;
				}

				this.drawTexturedRect(s, t, 0, u, 14, 14);
				if (this.field_2972[q] != null) {
					this.client.getTextureManager().bindTexture(this.field_2972[q]);
					drawTexturedRect(s + 4, t + 2, 1.0F, 1.0F, 20, 40, 5, 10, 64.0F, 64.0F);
				}
			}
		} else if (this.field_2962) {
			int n = k + 60;
			int o = l + 13;
			this.client.getTextureManager().bindTexture(TEXTURE);
			this.drawTexturedRect(n, o, 0, this.containerHeight, 14, 14);
			int p = this.container.method_7647();
			if (this.field_2972[p] != null) {
				this.client.getTextureManager().bindTexture(this.field_2972[p]);
				drawTexturedRect(n + 4, o + 2, 1.0F, 1.0F, 20, 40, 5, 10, 64.0F, 64.0F);
			}
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		this.field_2958 = false;
		if (this.field_2965) {
			int j = this.left + 60;
			int k = this.top + 13;
			int l = this.field_2970 + 16;

			for (int m = this.field_2970; m < l; m++) {
				int n = m - this.field_2970;
				double f = d - (double)(j + n % 4 * 14);
				double g = e - (double)(k + n / 4 * 14);
				if (f >= 0.0 && g >= 0.0 && f < 14.0 && g < 14.0 && this.container.onButtonClick(this.client.player, m)) {
					MinecraftClient.getInstance().getSoundLoader().play(PositionedSoundInstance.master(SoundEvents.field_14920, 1.0F));
					this.client.interactionManager.clickButton(this.container.syncId, m);
					return true;
				}
			}

			j = this.left + 119;
			k = this.top + 9;
			if (d >= (double)j && d < (double)(j + 12) && e >= (double)k && e < (double)(k + 56)) {
				this.field_2958 = true;
			}
		}

		return super.mouseClicked(d, e, i);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		if (this.field_2958 && this.field_2965) {
			int j = this.top + 13;
			int k = j + 56;
			this.field_2968 = ((float)e - (float)j - 7.5F) / ((float)(k - j) - 15.0F);
			this.field_2968 = MathHelper.clamp(this.field_2968, 0.0F, 1.0F);
			int l = field_2963 - 4;
			int m = (int)((double)(this.field_2968 * (float)l) + 0.5);
			if (m < 0) {
				m = 0;
			}

			this.field_2970 = 1 + m * 4;
			return true;
		} else {
			return super.mouseDragged(d, e, i, f, g);
		}
	}

	@Override
	public boolean mouseScrolled(double d) {
		if (this.field_2965) {
			int i = field_2963 - 4;
			this.field_2968 = (float)((double)this.field_2968 - d / (double)i);
			this.field_2968 = MathHelper.clamp(this.field_2968, 0.0F, 1.0F);
			this.field_2970 = 1 + (int)((double)(this.field_2968 * (float)i) + 0.5) * 4;
		}

		return true;
	}

	@Override
	protected boolean isClickOutsideBounds(double d, double e, int i, int j, int k) {
		return d < (double)i || e < (double)j || d >= (double)(i + this.containerWidth) || e >= (double)(j + this.containerHeight);
	}

	private void method_17576() {
		ItemStack itemStack = this.container.method_17431().getStack();
		if (itemStack.isEmpty()) {
			this.field_2957 = null;
		} else {
			BannerBlockEntity bannerBlockEntity = new BannerBlockEntity();
			bannerBlockEntity.deserialize(itemStack, ((BannerItem)itemStack.getItem()).getColor());
			this.field_2957 = TextureCache.BANNER.get(bannerBlockEntity.method_10915(), bannerBlockEntity.getPatternList(), bannerBlockEntity.getPatternColorList());
		}

		ItemStack itemStack2 = this.container.method_17428().getStack();
		ItemStack itemStack3 = this.container.method_17429().getStack();
		ItemStack itemStack4 = this.container.method_17430().getStack();
		CompoundTag compoundTag = itemStack2.getOrCreateSubCompoundTag("BlockEntityTag");
		this.field_2961 = compoundTag.containsKey("Patterns", 9) && !itemStack2.isEmpty() && compoundTag.getList("Patterns", 10).size() >= 6;
		if (this.field_2961) {
			this.field_2957 = null;
		}

		if (!ItemStack.areEqual(itemStack2, this.field_2955) || !ItemStack.areEqual(itemStack3, this.field_2954) || !ItemStack.areEqual(itemStack4, this.field_2967)) {
			this.field_2965 = !itemStack2.isEmpty() && !itemStack3.isEmpty() && itemStack4.isEmpty() && !this.field_2961;
			this.field_2962 = !itemStack4.isEmpty() && !this.field_2961;
		}

		this.field_2955 = itemStack2.copy();
		this.field_2954 = itemStack3.copy();
		this.field_2967 = itemStack4.copy();
	}
}
