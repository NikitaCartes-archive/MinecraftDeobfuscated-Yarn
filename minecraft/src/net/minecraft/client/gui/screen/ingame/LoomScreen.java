package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.TextureCache;
import net.minecraft.container.LoomContainer;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class LoomScreen extends ContainerScreen<LoomContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/loom.png");
	private static final int PATTERN_BUTTON_ROW_COUNT = (BannerPattern.COUNT - 5 - 1 + 4 - 1) / 4;
	private static final DyeColor PATTERN_BUTTON_BACKGROUND_COLOR = DyeColor.GRAY;
	private static final DyeColor PATTERN_BUTTON_FOREGROUND_COLOR = DyeColor.WHITE;
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

	public LoomScreen(LoomContainer loomContainer, PlayerInventory playerInventory, Text text) {
		super(loomContainer, playerInventory, text);
		loomContainer.setInventoryChangeListener(this::onInventoryChanged);
	}

	@Override
	public void tick() {
		super.tick();
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
	public void render(int mouseX, int mouseY, float delta) {
		super.render(mouseX, mouseY, delta);
		this.drawMouseoverTooltip(mouseX, mouseY);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		this.font.draw(this.title.asFormattedString(), 8.0F, 4.0F, 4210752);
		this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		this.renderBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int i = this.x;
		int j = this.y;
		this.blit(i, j, 0, 0, this.containerWidth, this.containerHeight);
		Slot slot = this.container.getBannerSlot();
		Slot slot2 = this.container.getDyeSlot();
		Slot slot3 = this.container.getPatternSlot();
		Slot slot4 = this.container.getOutputSlot();
		if (!slot.hasStack()) {
			this.blit(i + slot.xPosition, j + slot.yPosition, this.containerWidth, 0, 16, 16);
		}

		if (!slot2.hasStack()) {
			this.blit(i + slot2.xPosition, j + slot2.yPosition, this.containerWidth + 16, 0, 16, 16);
		}

		if (!slot3.hasStack()) {
			this.blit(i + slot3.xPosition, j + slot3.yPosition, this.containerWidth + 32, 0, 16, 16);
		}

		int k = (int)(41.0F * this.scrollPosition);
		this.blit(i + 119, j + 13 + k, 232 + (this.canApplyDyePattern ? 0 : 12), 0, 12, 15);
		if (this.output != null && !this.hasTooManyPatterns) {
			this.minecraft.getTextureManager().bindTexture(this.output);
			blit(i + 141, j + 8, 20, 40, 1.0F, 1.0F, 20, 40, 64, 64);
		} else if (this.hasTooManyPatterns) {
			this.blit(i + slot4.xPosition - 2, j + slot4.yPosition - 2, this.containerWidth, 17, 17, 16);
		}

		if (this.canApplyDyePattern) {
			int l = i + 60;
			int m = j + 13;
			int n = this.firstPatternButtonId + 16;

			for (int o = this.firstPatternButtonId; o < n && o < this.patternButtonTextureIds.length - 5; o++) {
				int p = o - this.firstPatternButtonId;
				int q = l + p % 4 * 14;
				int r = m + p / 4 * 14;
				this.minecraft.getTextureManager().bindTexture(TEXTURE);
				int s = this.containerHeight;
				if (o == this.container.getSelectedPattern()) {
					s += 14;
				} else if (mouseX >= q && mouseY >= r && mouseX < q + 14 && mouseY < r + 14) {
					s += 28;
				}

				this.blit(q, r, 0, s, 14, 14);
				if (this.patternButtonTextureIds[o] != null) {
					this.minecraft.getTextureManager().bindTexture(this.patternButtonTextureIds[o]);
					blit(q + 4, r + 2, 5, 10, 1.0F, 1.0F, 20, 40, 64, 64);
				}
			}
		} else if (this.canApplySpecialPattern) {
			int l = i + 60;
			int m = j + 13;
			this.minecraft.getTextureManager().bindTexture(TEXTURE);
			this.blit(l, m, 0, this.containerHeight, 14, 14);
			int n = this.container.getSelectedPattern();
			if (this.patternButtonTextureIds[n] != null) {
				this.minecraft.getTextureManager().bindTexture(this.patternButtonTextureIds[n]);
				blit(l + 4, m + 2, 5, 10, 1.0F, 1.0F, 20, 40, 64, 64);
			}
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.scrollbarClicked = false;
		if (this.canApplyDyePattern) {
			int i = this.x + 60;
			int j = this.y + 13;
			int k = this.firstPatternButtonId + 16;

			for (int l = this.firstPatternButtonId; l < k; l++) {
				int m = l - this.firstPatternButtonId;
				double d = mouseX - (double)(i + m % 4 * 14);
				double e = mouseY - (double)(j + m / 4 * 14);
				if (d >= 0.0 && e >= 0.0 && d < 14.0 && e < 14.0 && this.container.onButtonClick(this.minecraft.player, l)) {
					MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0F));
					this.minecraft.interactionManager.clickButton(this.container.syncId, l);
					return true;
				}
			}

			i = this.x + 119;
			j = this.y + 9;
			if (mouseX >= (double)i && mouseX < (double)(i + 12) && mouseY >= (double)j && mouseY < (double)(j + 56)) {
				this.scrollbarClicked = true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.scrollbarClicked && this.canApplyDyePattern) {
			int i = this.y + 13;
			int j = i + 56;
			this.scrollPosition = ((float)mouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
			this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
			int k = PATTERN_BUTTON_ROW_COUNT - 4;
			int l = (int)((double)(this.scrollPosition * (float)k) + 0.5);
			if (l < 0) {
				l = 0;
			}

			this.firstPatternButtonId = 1 + l * 4;
			return true;
		} else {
			return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}
	}

	@Override
	public boolean mouseScrolled(double d, double e, double amount) {
		if (this.canApplyDyePattern) {
			int i = PATTERN_BUTTON_ROW_COUNT - 4;
			this.scrollPosition = (float)((double)this.scrollPosition - amount / (double)i);
			this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
			this.firstPatternButtonId = 1 + (int)((double)(this.scrollPosition * (float)i) + 0.5) * 4;
		}

		return true;
	}

	@Override
	protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
		return mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.containerWidth) || mouseY >= (double)(top + this.containerHeight);
	}

	private void onInventoryChanged() {
		ItemStack itemStack = this.container.getOutputSlot().getStack();
		if (itemStack.isEmpty()) {
			this.output = null;
		} else {
			BannerBlockEntity bannerBlockEntity = new BannerBlockEntity();
			bannerBlockEntity.readFrom(itemStack, ((BannerItem)itemStack.getItem()).getColor());
			this.output = TextureCache.BANNER.get(bannerBlockEntity.getPatternCacheKey(), bannerBlockEntity.getPatterns(), bannerBlockEntity.getPatternColors());
		}

		ItemStack itemStack2 = this.container.getBannerSlot().getStack();
		ItemStack itemStack3 = this.container.getDyeSlot().getStack();
		ItemStack itemStack4 = this.container.getPatternSlot().getStack();
		CompoundTag compoundTag = itemStack2.getOrCreateSubTag("BlockEntityTag");
		this.hasTooManyPatterns = compoundTag.contains("Patterns", 9) && !itemStack2.isEmpty() && compoundTag.getList("Patterns", 10).size() >= 6;
		if (this.hasTooManyPatterns) {
			this.output = null;
		}

		if (!ItemStack.areEqualIgnoreDamage(itemStack2, this.banner)
			|| !ItemStack.areEqualIgnoreDamage(itemStack3, this.dye)
			|| !ItemStack.areEqualIgnoreDamage(itemStack4, this.pattern)) {
			this.canApplyDyePattern = !itemStack2.isEmpty() && !itemStack3.isEmpty() && itemStack4.isEmpty() && !this.hasTooManyPatterns;
			this.canApplySpecialPattern = !this.hasTooManyPatterns && !itemStack4.isEmpty() && !itemStack2.isEmpty() && !itemStack3.isEmpty();
		}

		this.banner = itemStack2.copy();
		this.dye = itemStack3.copy();
		this.pattern = itemStack4.copy();
	}
}
