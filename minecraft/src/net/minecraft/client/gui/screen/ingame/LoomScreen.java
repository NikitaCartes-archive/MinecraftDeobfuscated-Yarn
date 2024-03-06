package net.minecraft.client.gui.screen.ingame;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.LoomScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class LoomScreen extends HandledScreen<LoomScreenHandler> {
	private static final Identifier BANNER_SLOT_TEXTURE = new Identifier("container/loom/banner_slot");
	private static final Identifier DYE_SLOT_TEXTURE = new Identifier("container/loom/dye_slot");
	private static final Identifier PATTERN_SLOT_TEXTURE = new Identifier("container/loom/pattern_slot");
	private static final Identifier SCROLLER_TEXTURE = new Identifier("container/loom/scroller");
	private static final Identifier SCROLLER_DISABLED_TEXTURE = new Identifier("container/loom/scroller_disabled");
	private static final Identifier PATTERN_SELECTED_TEXTURE = new Identifier("container/loom/pattern_selected");
	private static final Identifier PATTERN_HIGHLIGHTED_TEXTURE = new Identifier("container/loom/pattern_highlighted");
	private static final Identifier PATTERN_TEXTURE = new Identifier("container/loom/pattern");
	private static final Identifier ERROR_TEXTURE = new Identifier("container/loom/error");
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/loom.png");
	private static final int PATTERN_LIST_COLUMNS = 4;
	private static final int PATTERN_LIST_ROWS = 4;
	private static final int SCROLLBAR_WIDTH = 12;
	private static final int SCROLLBAR_HEIGHT = 15;
	private static final int PATTERN_ENTRY_SIZE = 14;
	private static final int SCROLLBAR_AREA_HEIGHT = 56;
	private static final int PATTERN_LIST_OFFSET_X = 60;
	private static final int PATTERN_LIST_OFFSET_Y = 13;
	private ModelPart bannerField;
	@Nullable
	private BannerPatternsComponent bannerPatterns;
	private ItemStack banner = ItemStack.EMPTY;
	private ItemStack dye = ItemStack.EMPTY;
	private ItemStack pattern = ItemStack.EMPTY;
	private boolean canApplyDyePattern;
	private boolean hasTooManyPatterns;
	private float scrollPosition;
	private boolean scrollbarClicked;
	private int visibleTopRow;

	public LoomScreen(LoomScreenHandler screenHandler, PlayerInventory inventory, Text title) {
		super(screenHandler, inventory, title);
		screenHandler.setInventoryChangeListener(this::onInventoryChanged);
		this.titleY -= 2;
	}

	@Override
	protected void init() {
		super.init();
		this.bannerField = this.client.getEntityModelLoader().getModelPart(EntityModelLayers.BANNER).getChild("flag");
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(context, mouseX, mouseY);
	}

	private int getRows() {
		return MathHelper.ceilDiv(this.handler.getBannerPatterns().size(), 4);
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		int i = this.x;
		int j = this.y;
		context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
		Slot slot = this.handler.getBannerSlot();
		Slot slot2 = this.handler.getDyeSlot();
		Slot slot3 = this.handler.getPatternSlot();
		Slot slot4 = this.handler.getOutputSlot();
		if (!slot.hasStack()) {
			context.drawGuiTexture(BANNER_SLOT_TEXTURE, i + slot.x, j + slot.y, 16, 16);
		}

		if (!slot2.hasStack()) {
			context.drawGuiTexture(DYE_SLOT_TEXTURE, i + slot2.x, j + slot2.y, 16, 16);
		}

		if (!slot3.hasStack()) {
			context.drawGuiTexture(PATTERN_SLOT_TEXTURE, i + slot3.x, j + slot3.y, 16, 16);
		}

		int k = (int)(41.0F * this.scrollPosition);
		Identifier identifier = this.canApplyDyePattern ? SCROLLER_TEXTURE : SCROLLER_DISABLED_TEXTURE;
		context.drawGuiTexture(identifier, i + 119, j + 13 + k, 12, 15);
		DiffuseLighting.disableGuiDepthLighting();
		if (this.bannerPatterns != null && !this.hasTooManyPatterns) {
			context.getMatrices().push();
			context.getMatrices().translate((float)(i + 139), (float)(j + 52), 0.0F);
			context.getMatrices().scale(24.0F, 24.0F, 1.0F);
			context.getMatrices().translate(0.5F, -0.5F, 0.5F);
			float f = 0.6666667F;
			context.getMatrices().scale(0.6666667F, 0.6666667F, -0.6666667F);
			this.bannerField.pitch = 0.0F;
			this.bannerField.pivotY = -32.0F;
			DyeColor dyeColor = ((BannerItem)slot4.getStack().getItem()).getColor();
			BannerBlockEntityRenderer.renderCanvas(
				context.getMatrices(),
				context.getVertexConsumers(),
				15728880,
				OverlayTexture.DEFAULT_UV,
				this.bannerField,
				ModelLoader.BANNER_BASE,
				true,
				dyeColor,
				this.bannerPatterns
			);
			context.getMatrices().pop();
			context.draw();
		} else if (this.hasTooManyPatterns) {
			context.drawGuiTexture(ERROR_TEXTURE, i + slot4.x - 5, j + slot4.y - 5, 26, 26);
		}

		if (this.canApplyDyePattern) {
			int l = i + 60;
			int m = j + 13;
			List<RegistryEntry<BannerPattern>> list = this.handler.getBannerPatterns();

			label64:
			for (int n = 0; n < 4; n++) {
				for (int o = 0; o < 4; o++) {
					int p = n + this.visibleTopRow;
					int q = p * 4 + o;
					if (q >= list.size()) {
						break label64;
					}

					int r = l + o * 14;
					int s = m + n * 14;
					boolean bl = mouseX >= r && mouseY >= s && mouseX < r + 14 && mouseY < s + 14;
					Identifier identifier2;
					if (q == this.handler.getSelectedPattern()) {
						identifier2 = PATTERN_SELECTED_TEXTURE;
					} else if (bl) {
						identifier2 = PATTERN_HIGHLIGHTED_TEXTURE;
					} else {
						identifier2 = PATTERN_TEXTURE;
					}

					context.drawGuiTexture(identifier2, r, s, 14, 14);
					this.drawBanner(context, (RegistryEntry<BannerPattern>)list.get(q), r, s);
				}
			}
		}

		DiffuseLighting.enableGuiDepthLighting();
	}

	private void drawBanner(DrawContext context, RegistryEntry<BannerPattern> pattern, int x, int y) {
		MatrixStack matrixStack = new MatrixStack();
		matrixStack.push();
		matrixStack.translate((float)x + 0.5F, (float)(y + 16), 0.0F);
		matrixStack.scale(6.0F, -6.0F, 1.0F);
		matrixStack.translate(0.5F, 0.5F, 0.0F);
		matrixStack.translate(0.5F, 0.5F, 0.5F);
		float f = 0.6666667F;
		matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
		this.bannerField.pitch = 0.0F;
		this.bannerField.pivotY = -32.0F;
		BannerPatternsComponent bannerPatternsComponent = new BannerPatternsComponent.Builder().add(pattern, DyeColor.WHITE).build();
		BannerBlockEntityRenderer.renderCanvas(
			matrixStack,
			context.getVertexConsumers(),
			15728880,
			OverlayTexture.DEFAULT_UV,
			this.bannerField,
			ModelLoader.BANNER_BASE,
			true,
			DyeColor.GRAY,
			bannerPatternsComponent
		);
		matrixStack.pop();
		context.draw();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.scrollbarClicked = false;
		if (this.canApplyDyePattern) {
			int i = this.x + 60;
			int j = this.y + 13;

			for (int k = 0; k < 4; k++) {
				for (int l = 0; l < 4; l++) {
					double d = mouseX - (double)(i + l * 14);
					double e = mouseY - (double)(j + k * 14);
					int m = k + this.visibleTopRow;
					int n = m * 4 + l;
					if (d >= 0.0 && e >= 0.0 && d < 14.0 && e < 14.0 && this.handler.onButtonClick(this.client.player, n)) {
						MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0F));
						this.client.interactionManager.clickButton(this.handler.syncId, n);
						return true;
					}
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
		int i = this.getRows() - 4;
		if (this.scrollbarClicked && this.canApplyDyePattern && i > 0) {
			int j = this.y + 13;
			int k = j + 56;
			this.scrollPosition = ((float)mouseY - (float)j - 7.5F) / ((float)(k - j) - 15.0F);
			this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
			this.visibleTopRow = Math.max((int)((double)(this.scrollPosition * (float)i) + 0.5), 0);
			return true;
		} else {
			return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		int i = this.getRows() - 4;
		if (this.canApplyDyePattern && i > 0) {
			float f = (float)verticalAmount / (float)i;
			this.scrollPosition = MathHelper.clamp(this.scrollPosition - f, 0.0F, 1.0F);
			this.visibleTopRow = Math.max((int)(this.scrollPosition * (float)i + 0.5F), 0);
		}

		return true;
	}

	@Override
	protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
		return mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
	}

	private void onInventoryChanged() {
		ItemStack itemStack = this.handler.getOutputSlot().getStack();
		if (itemStack.isEmpty()) {
			this.bannerPatterns = null;
		} else {
			this.bannerPatterns = itemStack.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
		}

		ItemStack itemStack2 = this.handler.getBannerSlot().getStack();
		ItemStack itemStack3 = this.handler.getDyeSlot().getStack();
		ItemStack itemStack4 = this.handler.getPatternSlot().getStack();
		BannerPatternsComponent bannerPatternsComponent = itemStack2.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
		this.hasTooManyPatterns = bannerPatternsComponent.layers().size() >= 6;
		if (this.hasTooManyPatterns) {
			this.bannerPatterns = null;
		}

		if (!ItemStack.areEqual(itemStack2, this.banner) || !ItemStack.areEqual(itemStack3, this.dye) || !ItemStack.areEqual(itemStack4, this.pattern)) {
			this.canApplyDyePattern = !itemStack2.isEmpty() && !itemStack3.isEmpty() && !this.hasTooManyPatterns && !this.handler.getBannerPatterns().isEmpty();
		}

		if (this.visibleTopRow >= this.getRows()) {
			this.visibleTopRow = 0;
			this.scrollPosition = 0.0F;
		}

		this.banner = itemStack2.copy();
		this.dye = itemStack3.copy();
		this.pattern = itemStack4.copy();
	}
}
