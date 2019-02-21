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
import net.minecraft.client.gui.ContainerScreen;
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
public class LoomScreen extends ContainerScreen<LoomContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/loom.png");
	private static final int PATTERN_BUTTON_ROW_COUNT = (BannerPattern.COUNT - 4 - 1 + 4 - 1) / 4;
	private static final DyeColor PATTERN_BUTTON_BACKGROUND_COLOR = DyeColor.field_7944;
	private static final DyeColor PATTERN_BUTTON_FOREGROUND_COLOR = DyeColor.field_7952;
	private static final List<DyeColor> PATTERN_BUTTON_COLORS = Lists.<DyeColor>newArrayList(PATTERN_BUTTON_BACKGROUND_COLOR, PATTERN_BUTTON_FOREGROUND_COLOR);
	private Identifier output;
	private ItemStack banner = ItemStack.EMPTY;
	private ItemStack dye = ItemStack.EMPTY;
	private ItemStack pattern = ItemStack.EMPTY;
	private final Identifier[] patternButtonTextureIds = new Identifier[BannerPattern.COUNT];
	private boolean canApplyDyePattern;
	private boolean canApplySpecialPattern;
	private boolean hasTooManyPatterns;
	private float scrollPosition;
	private boolean scrollbarClicked;
	private int firstPatternButtonId = 1;
	private int lastCachedPatternButtonTextureId = 1;

	public LoomScreen(LoomContainer loomContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		super(loomContainer, playerInventory, textComponent);
		loomContainer.setInventoryChangeListener(this::onInventoryChanged);
	}

	@Override
	public void update() {
		super.update();
		if (this.lastCachedPatternButtonTextureId < BannerPattern.COUNT) {
			BannerPattern bannerPattern = BannerPattern.values()[this.lastCachedPatternButtonTextureId];
			String string = "b" + PATTERN_BUTTON_BACKGROUND_COLOR.getId();
			String string2 = bannerPattern.getId() + PATTERN_BUTTON_FOREGROUND_COLOR.getId();
			this.patternButtonTextureIds[this.lastCachedPatternButtonTextureId] = TextureCache.BANNER
				.get(string + string2, Lists.<BannerPattern>newArrayList(BannerPattern.BASE, bannerPattern), PATTERN_BUTTON_COLORS);
			this.lastCachedPatternButtonTextureId++;
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		super.draw(i, j, f);
		this.drawMouseoverTooltip(i, j);
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.fontRenderer.draw(this.name.getFormattedText(), 8.0F, 4.0F, 4210752);
		this.fontRenderer.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.height - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		this.drawBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int k = this.left;
		int l = this.top;
		this.drawTexturedRect(k, l, 0, 0, this.width, this.height);
		Slot slot = this.container.getBannerSlot();
		Slot slot2 = this.container.getDyeSlot();
		Slot slot3 = this.container.getPatternSlot();
		Slot slot4 = this.container.getOutputSlot();
		if (!slot.hasStack()) {
			this.drawTexturedRect(k + slot.xPosition, l + slot.yPosition, this.width, 0, 16, 16);
		}

		if (!slot2.hasStack()) {
			this.drawTexturedRect(k + slot2.xPosition, l + slot2.yPosition, this.width + 16, 0, 16, 16);
		}

		if (!slot3.hasStack()) {
			this.drawTexturedRect(k + slot3.xPosition, l + slot3.yPosition, this.width + 32, 0, 16, 16);
		}

		int m = (int)(41.0F * this.scrollPosition);
		this.drawTexturedRect(k + 119, l + 13 + m, 232 + (this.canApplyDyePattern ? 0 : 12), 0, 12, 15);
		if (this.output != null && !this.hasTooManyPatterns) {
			this.client.getTextureManager().bindTexture(this.output);
			drawTexturedRect(k + 141, l + 8, 1.0F, 1.0F, 20, 40, 20, 40, 64.0F, 64.0F);
		} else if (this.hasTooManyPatterns) {
			this.drawTexturedRect(k + slot4.xPosition - 2, l + slot4.yPosition - 2, this.width, 17, 17, 16);
		}

		if (this.canApplyDyePattern) {
			int n = k + 60;
			int o = l + 13;
			int p = this.firstPatternButtonId + 16;

			for (int q = this.firstPatternButtonId; q < p && q < this.patternButtonTextureIds.length - 4; q++) {
				int r = q - this.firstPatternButtonId;
				int s = n + r % 4 * 14;
				int t = o + r / 4 * 14;
				this.client.getTextureManager().bindTexture(TEXTURE);
				int u = this.height;
				if (q == this.container.getSelectedPattern()) {
					u += 14;
				} else if (i >= s && j >= t && i < s + 14 && j < t + 14) {
					u += 28;
				}

				this.drawTexturedRect(s, t, 0, u, 14, 14);
				if (this.patternButtonTextureIds[q] != null) {
					this.client.getTextureManager().bindTexture(this.patternButtonTextureIds[q]);
					drawTexturedRect(s + 4, t + 2, 1.0F, 1.0F, 20, 40, 5, 10, 64.0F, 64.0F);
				}
			}
		} else if (this.canApplySpecialPattern) {
			int n = k + 60;
			int o = l + 13;
			this.client.getTextureManager().bindTexture(TEXTURE);
			this.drawTexturedRect(n, o, 0, this.height, 14, 14);
			int p = this.container.getSelectedPattern();
			if (this.patternButtonTextureIds[p] != null) {
				this.client.getTextureManager().bindTexture(this.patternButtonTextureIds[p]);
				drawTexturedRect(n + 4, o + 2, 1.0F, 1.0F, 20, 40, 5, 10, 64.0F, 64.0F);
			}
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		this.scrollbarClicked = false;
		if (this.canApplyDyePattern) {
			int j = this.left + 60;
			int k = this.top + 13;
			int l = this.firstPatternButtonId + 16;

			for (int m = this.firstPatternButtonId; m < l; m++) {
				int n = m - this.firstPatternButtonId;
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
				this.scrollbarClicked = true;
			}
		}

		return super.mouseClicked(d, e, i);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		if (this.scrollbarClicked && this.canApplyDyePattern) {
			int j = this.top + 13;
			int k = j + 56;
			this.scrollPosition = ((float)e - (float)j - 7.5F) / ((float)(k - j) - 15.0F);
			this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
			int l = PATTERN_BUTTON_ROW_COUNT - 4;
			int m = (int)((double)(this.scrollPosition * (float)l) + 0.5);
			if (m < 0) {
				m = 0;
			}

			this.firstPatternButtonId = 1 + m * 4;
			return true;
		} else {
			return super.mouseDragged(d, e, i, f, g);
		}
	}

	@Override
	public boolean mouseScrolled(double d) {
		if (this.canApplyDyePattern) {
			int i = PATTERN_BUTTON_ROW_COUNT - 4;
			this.scrollPosition = (float)((double)this.scrollPosition - d / (double)i);
			this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
			this.firstPatternButtonId = 1 + (int)((double)(this.scrollPosition * (float)i) + 0.5) * 4;
		}

		return true;
	}

	@Override
	protected boolean isClickOutsideBounds(double d, double e, int i, int j, int k) {
		return d < (double)i || e < (double)j || d >= (double)(i + this.width) || e >= (double)(j + this.height);
	}

	private void onInventoryChanged() {
		ItemStack itemStack = this.container.getOutputSlot().getStack();
		if (itemStack.isEmpty()) {
			this.output = null;
		} else {
			BannerBlockEntity bannerBlockEntity = new BannerBlockEntity();
			bannerBlockEntity.deserialize(itemStack, ((BannerItem)itemStack.getItem()).getColor());
			this.output = TextureCache.BANNER.get(bannerBlockEntity.getPatternCacheKey(), bannerBlockEntity.getPatterns(), bannerBlockEntity.getPatternColors());
		}

		ItemStack itemStack2 = this.container.getBannerSlot().getStack();
		ItemStack itemStack3 = this.container.getDyeSlot().getStack();
		ItemStack itemStack4 = this.container.getPatternSlot().getStack();
		CompoundTag compoundTag = itemStack2.getOrCreateSubCompoundTag("BlockEntityTag");
		this.hasTooManyPatterns = compoundTag.containsKey("Patterns", 9) && !itemStack2.isEmpty() && compoundTag.getList("Patterns", 10).size() >= 6;
		if (this.hasTooManyPatterns) {
			this.output = null;
		}

		if (!ItemStack.areEqual(itemStack2, this.banner) || !ItemStack.areEqual(itemStack3, this.dye) || !ItemStack.areEqual(itemStack4, this.pattern)) {
			this.canApplyDyePattern = !itemStack2.isEmpty() && !itemStack3.isEmpty() && itemStack4.isEmpty() && !this.hasTooManyPatterns;
			this.canApplySpecialPattern = !itemStack4.isEmpty() && !this.hasTooManyPatterns;
		}

		this.banner = itemStack2.copy();
		this.dye = itemStack3.copy();
		this.pattern = itemStack4.copy();
	}
}
