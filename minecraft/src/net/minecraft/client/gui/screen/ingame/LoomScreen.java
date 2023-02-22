package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BannerPatterns;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
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
	private List<Pair<RegistryEntry<BannerPattern>, DyeColor>> bannerPatterns;
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
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	private int getRows() {
		return MathHelper.ceilDiv(this.handler.getBannerPatterns().size(), 4);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		this.renderBackground(matrices);
		RenderSystem.setShaderTexture(0, TEXTURE);
		int i = this.x;
		int j = this.y;
		drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
		Slot slot = this.handler.getBannerSlot();
		Slot slot2 = this.handler.getDyeSlot();
		Slot slot3 = this.handler.getPatternSlot();
		Slot slot4 = this.handler.getOutputSlot();
		if (!slot.hasStack()) {
			drawTexture(matrices, i + slot.x, j + slot.y, this.backgroundWidth, 0, 16, 16);
		}

		if (!slot2.hasStack()) {
			drawTexture(matrices, i + slot2.x, j + slot2.y, this.backgroundWidth + 16, 0, 16, 16);
		}

		if (!slot3.hasStack()) {
			drawTexture(matrices, i + slot3.x, j + slot3.y, this.backgroundWidth + 32, 0, 16, 16);
		}

		int k = (int)(41.0F * this.scrollPosition);
		drawTexture(matrices, i + 119, j + 13 + k, 232 + (this.canApplyDyePattern ? 0 : 12), 0, 12, 15);
		DiffuseLighting.disableGuiDepthLighting();
		if (this.bannerPatterns != null && !this.hasTooManyPatterns) {
			VertexConsumerProvider.Immediate immediate = this.client.getBufferBuilders().getEntityVertexConsumers();
			matrices.push();
			matrices.translate((float)(i + 139), (float)(j + 52), 0.0F);
			matrices.scale(24.0F, -24.0F, 1.0F);
			matrices.translate(0.5F, 0.5F, 0.5F);
			float f = 0.6666667F;
			matrices.scale(0.6666667F, -0.6666667F, -0.6666667F);
			this.bannerField.pitch = 0.0F;
			this.bannerField.pivotY = -32.0F;
			BannerBlockEntityRenderer.renderCanvas(
				matrices, immediate, 15728880, OverlayTexture.DEFAULT_UV, this.bannerField, ModelLoader.BANNER_BASE, true, this.bannerPatterns
			);
			matrices.pop();
			immediate.draw();
		} else if (this.hasTooManyPatterns) {
			drawTexture(matrices, i + slot4.x - 2, j + slot4.y - 2, this.backgroundWidth, 17, 17, 16);
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

					RenderSystem.setShaderTexture(0, TEXTURE);
					int r = l + o * 14;
					int s = m + n * 14;
					boolean bl = mouseX >= r && mouseY >= s && mouseX < r + 14 && mouseY < s + 14;
					int t;
					if (q == this.handler.getSelectedPattern()) {
						t = this.backgroundHeight + 14;
					} else if (bl) {
						t = this.backgroundHeight + 28;
					} else {
						t = this.backgroundHeight;
					}

					drawTexture(matrices, r, s, 0, t, 14, 14);
					this.drawBanner((RegistryEntry<BannerPattern>)list.get(q), r, s);
				}
			}
		}

		DiffuseLighting.enableGuiDepthLighting();
	}

	private void drawBanner(RegistryEntry<BannerPattern> bannerPattern, int x, int y) {
		NbtCompound nbtCompound = new NbtCompound();
		NbtList nbtList = new BannerPattern.Patterns().add(BannerPatterns.BASE, DyeColor.GRAY).add(bannerPattern, DyeColor.WHITE).toNbt();
		nbtCompound.put("Patterns", nbtList);
		ItemStack itemStack = new ItemStack(Items.GRAY_BANNER);
		BlockItem.setBlockEntityNbt(itemStack, BlockEntityType.BANNER, nbtCompound);
		MatrixStack matrixStack = new MatrixStack();
		matrixStack.push();
		matrixStack.translate((float)x + 0.5F, (float)(y + 16), 0.0F);
		matrixStack.scale(6.0F, -6.0F, 1.0F);
		matrixStack.translate(0.5F, 0.5F, 0.0F);
		matrixStack.translate(0.5F, 0.5F, 0.5F);
		float f = 0.6666667F;
		matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
		VertexConsumerProvider.Immediate immediate = this.client.getBufferBuilders().getEntityVertexConsumers();
		this.bannerField.pitch = 0.0F;
		this.bannerField.pivotY = -32.0F;
		List<Pair<RegistryEntry<BannerPattern>, DyeColor>> list = BannerBlockEntity.getPatternsFromNbt(DyeColor.GRAY, BannerBlockEntity.getPatternListNbt(itemStack));
		BannerBlockEntityRenderer.renderCanvas(matrixStack, immediate, 15728880, OverlayTexture.DEFAULT_UV, this.bannerField, ModelLoader.BANNER_BASE, true, list);
		matrixStack.pop();
		immediate.draw();
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
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		int i = this.getRows() - 4;
		if (this.canApplyDyePattern && i > 0) {
			float f = (float)amount / (float)i;
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
			this.bannerPatterns = BannerBlockEntity.getPatternsFromNbt(((BannerItem)itemStack.getItem()).getColor(), BannerBlockEntity.getPatternListNbt(itemStack));
		}

		ItemStack itemStack2 = this.handler.getBannerSlot().getStack();
		ItemStack itemStack3 = this.handler.getDyeSlot().getStack();
		ItemStack itemStack4 = this.handler.getPatternSlot().getStack();
		NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(itemStack2);
		this.hasTooManyPatterns = nbtCompound != null
			&& nbtCompound.contains("Patterns", NbtElement.LIST_TYPE)
			&& !itemStack2.isEmpty()
			&& nbtCompound.getList("Patterns", NbtElement.COMPOUND_TYPE).size() >= 6;
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
