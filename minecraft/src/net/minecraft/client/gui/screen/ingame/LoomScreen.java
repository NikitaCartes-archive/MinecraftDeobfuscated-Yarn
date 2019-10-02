package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.container.LoomContainer;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class LoomScreen extends AbstractContainerScreen<LoomContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/loom.png");
	private static final int PATTERN_BUTTON_ROW_COUNT = (BannerPattern.COUNT - 5 - 1 + 4 - 1) / 4;
	@Nullable
	private BannerBlockEntity preview;
	private ItemStack banner = ItemStack.EMPTY;
	private ItemStack dye = ItemStack.EMPTY;
	private ItemStack pattern = ItemStack.EMPTY;
	private boolean canApplyDyePattern;
	private boolean canApplySpecialPattern;
	private boolean hasTooManyPatterns;
	private float scrollPosition;
	private boolean scrollbarClicked;
	private int firstPatternButtonId = 1;

	public LoomScreen(LoomContainer loomContainer, PlayerInventory playerInventory, Text text) {
		super(loomContainer, playerInventory, text);
		loomContainer.setInventoryChangeListener(this::onInventoryChanged);
	}

	@Override
	public void render(int i, int j, float f) {
		super.render(i, j, f);
		this.drawMouseoverTooltip(i, j);
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.font.draw(this.title.asFormattedString(), 8.0F, 4.0F, 4210752);
		this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		this.renderBackground();
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int k = this.left;
		int l = this.top;
		this.blit(k, l, 0, 0, this.containerWidth, this.containerHeight);
		Slot slot = this.container.getBannerSlot();
		Slot slot2 = this.container.getDyeSlot();
		Slot slot3 = this.container.getPatternSlot();
		Slot slot4 = this.container.getOutputSlot();
		if (!slot.hasStack()) {
			this.blit(k + slot.xPosition, l + slot.yPosition, this.containerWidth, 0, 16, 16);
		}

		if (!slot2.hasStack()) {
			this.blit(k + slot2.xPosition, l + slot2.yPosition, this.containerWidth + 16, 0, 16, 16);
		}

		if (!slot3.hasStack()) {
			this.blit(k + slot3.xPosition, l + slot3.yPosition, this.containerWidth + 32, 0, 16, 16);
		}

		int m = (int)(41.0F * this.scrollPosition);
		this.blit(k + 119, l + 13 + m, 232 + (this.canApplyDyePattern ? 0 : 12), 0, 12, 15);
		if (this.preview != null && !this.hasTooManyPatterns) {
			RenderSystem.pushMatrix();
			RenderSystem.translatef((float)(k + 139), (float)(l + 52), 0.0F);
			RenderSystem.scalef(24.0F, -24.0F, 1.0F);
			this.preview.method_22534(true);
			BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.preview, new MatrixStack(), 15728880);
			this.preview.method_22534(false);
			RenderSystem.popMatrix();
		} else if (this.hasTooManyPatterns) {
			this.blit(k + slot4.xPosition - 2, l + slot4.yPosition - 2, this.containerWidth, 17, 17, 16);
		}

		if (this.canApplyDyePattern) {
			int n = k + 60;
			int o = l + 13;
			int p = this.firstPatternButtonId + 16;

			for (int q = this.firstPatternButtonId; q < p && q < BannerPattern.COUNT - 5; q++) {
				int r = q - this.firstPatternButtonId;
				int s = n + r % 4 * 14;
				int t = o + r / 4 * 14;
				this.minecraft.getTextureManager().bindTexture(TEXTURE);
				int u = this.containerHeight;
				if (q == this.container.getSelectedPattern()) {
					u += 14;
				} else if (i >= s && j >= t && i < s + 14 && j < t + 14) {
					u += 28;
				}

				this.blit(s, t, 0, u, 14, 14);
				this.method_22692(q, s, t);
			}
		} else if (this.canApplySpecialPattern) {
			int n = k + 60;
			int o = l + 13;
			this.minecraft.getTextureManager().bindTexture(TEXTURE);
			this.blit(n, o, 0, this.containerHeight, 14, 14);
			int p = this.container.getSelectedPattern();
			this.method_22692(p, n, o);
		}
	}

	private void method_22692(int i, int j, int k) {
		this.minecraft.getSpriteAtlas().method_23207();
		RenderSystem.texParameter(3553, 10241, 9728);
		BannerBlockEntity bannerBlockEntity = new BannerBlockEntity();
		bannerBlockEntity.method_22534(true);
		ItemStack itemStack = new ItemStack(Items.GRAY_BANNER);
		CompoundTag compoundTag = itemStack.getOrCreateSubTag("BlockEntityTag");
		ListTag listTag = new BannerPattern.Builder().with(BannerPattern.BASE, DyeColor.GRAY).with(BannerPattern.values()[i], DyeColor.WHITE).build();
		compoundTag.put("Patterns", listTag);
		bannerBlockEntity.deserialize(itemStack, DyeColor.GRAY);
		RenderSystem.pushMatrix();
		RenderSystem.translatef((float)j + 0.5F, (float)(k + 16), 0.0F);
		RenderSystem.scalef(6.0F, -6.0F, 1.0F);
		RenderSystem.translatef(0.5F, 0.5F, 0.0F);
		BlockEntityRenderDispatcher.INSTANCE.renderEntity(bannerBlockEntity, new MatrixStack(), 15728880);
		RenderSystem.popMatrix();
		this.minecraft.getSpriteAtlas().method_23207();
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
				if (f >= 0.0 && g >= 0.0 && f < 14.0 && g < 14.0 && this.container.onButtonClick(this.minecraft.player, m)) {
					MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0F));
					this.minecraft.interactionManager.clickButton(this.container.syncId, m);
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
	public boolean mouseScrolled(double d, double e, double f) {
		if (this.canApplyDyePattern) {
			int i = PATTERN_BUTTON_ROW_COUNT - 4;
			this.scrollPosition = (float)((double)this.scrollPosition - f / (double)i);
			this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
			this.firstPatternButtonId = 1 + (int)((double)(this.scrollPosition * (float)i) + 0.5) * 4;
		}

		return true;
	}

	@Override
	protected boolean isClickOutsideBounds(double d, double e, int i, int j, int k) {
		return d < (double)i || e < (double)j || d >= (double)(i + this.containerWidth) || e >= (double)(j + this.containerHeight);
	}

	private void onInventoryChanged() {
		ItemStack itemStack = this.container.getOutputSlot().getStack();
		if (itemStack.isEmpty()) {
			this.preview = null;
		} else {
			this.preview = new BannerBlockEntity();
			this.preview.deserialize(itemStack, ((BannerItem)itemStack.getItem()).getColor());
		}

		ItemStack itemStack2 = this.container.getBannerSlot().getStack();
		ItemStack itemStack3 = this.container.getDyeSlot().getStack();
		ItemStack itemStack4 = this.container.getPatternSlot().getStack();
		CompoundTag compoundTag = itemStack2.getOrCreateSubTag("BlockEntityTag");
		this.hasTooManyPatterns = compoundTag.contains("Patterns", 9) && !itemStack2.isEmpty() && compoundTag.getList("Patterns", 10).size() >= 6;
		if (this.hasTooManyPatterns) {
			this.preview = null;
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
