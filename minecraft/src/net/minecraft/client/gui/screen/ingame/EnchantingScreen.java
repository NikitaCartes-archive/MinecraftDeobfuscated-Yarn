package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.container.EnchantingTableContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class EnchantingScreen extends AbstractContainerScreen<EnchantingTableContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/enchanting_table.png");
	private static final Identifier BOOK_TEXURE = new Identifier("textures/entity/enchanting_table_book.png");
	private static final BookModel bookModel = new BookModel();
	private final Random random = new Random();
	public int field_2915;
	public float nextPageAngle;
	public float pageAngle;
	public float field_2909;
	public float field_2906;
	public float nextPageTurningSpeed;
	public float pageTurningSpeed;
	private ItemStack field_2913 = ItemStack.EMPTY;

	public EnchantingScreen(EnchantingTableContainer enchantingTableContainer, PlayerInventory playerInventory, Text text) {
		super(enchantingTableContainer, playerInventory, text);
	}

	@Override
	protected void drawForeground(int i, int j) {
		this.font.draw(this.title.asFormattedString(), 12.0F, 5.0F, 4210752);
		this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	public void tick() {
		super.tick();
		this.method_2478();
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		int j = (this.width - this.containerWidth) / 2;
		int k = (this.height - this.containerHeight) / 2;

		for (int l = 0; l < 3; l++) {
			double f = d - (double)(j + 60);
			double g = e - (double)(k + 14 + 19 * l);
			if (f >= 0.0 && g >= 0.0 && f < 108.0 && g < 19.0 && this.container.onButtonClick(this.minecraft.player, l)) {
				this.minecraft.interactionManager.clickButton(this.container.syncId, l);
				return true;
			}
		}

		return super.mouseClicked(d, e, i);
	}

	@Override
	protected void drawBackground(float f, int i, int j) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int k = (this.width - this.containerWidth) / 2;
		int l = (this.height - this.containerHeight) / 2;
		this.blit(k, l, 0, 0, this.containerWidth, this.containerHeight);
		RenderSystem.pushMatrix();
		RenderSystem.matrixMode(5889);
		RenderSystem.pushMatrix();
		RenderSystem.loadIdentity();
		int m = (int)this.minecraft.method_22683().getScaleFactor();
		RenderSystem.viewport((this.width - 320) / 2 * m, (this.height - 240) / 2 * m, 320 * m, 240 * m);
		RenderSystem.translatef(-0.34F, 0.23F, 0.0F);
		RenderSystem.multMatrix(Matrix4f.method_4929(90.0, 1.3333334F, 9.0F, 80.0F));
		float g = 1.0F;
		RenderSystem.matrixMode(5888);
		RenderSystem.loadIdentity();
		GuiLighting.enable();
		RenderSystem.translatef(0.0F, 3.3F, -16.0F);
		RenderSystem.scalef(1.0F, 1.0F, 1.0F);
		float h = 5.0F;
		RenderSystem.scalef(5.0F, 5.0F, 5.0F);
		RenderSystem.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(BOOK_TEXURE);
		RenderSystem.rotatef(20.0F, 1.0F, 0.0F, 0.0F);
		float n = MathHelper.lerp(f, this.pageTurningSpeed, this.nextPageTurningSpeed);
		RenderSystem.translatef((1.0F - n) * 0.2F, (1.0F - n) * 0.1F, (1.0F - n) * 0.25F);
		RenderSystem.rotatef(-(1.0F - n) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
		RenderSystem.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
		float o = MathHelper.lerp(f, this.pageAngle, this.nextPageAngle) + 0.25F;
		float p = MathHelper.lerp(f, this.pageAngle, this.nextPageAngle) + 0.75F;
		o = (o - (float)MathHelper.fastFloor((double)o)) * 1.6F - 0.3F;
		p = (p - (float)MathHelper.fastFloor((double)p)) * 1.6F - 0.3F;
		if (o < 0.0F) {
			o = 0.0F;
		}

		if (p < 0.0F) {
			p = 0.0F;
		}

		if (o > 1.0F) {
			o = 1.0F;
		}

		if (p > 1.0F) {
			p = 1.0F;
		}

		RenderSystem.enableRescaleNormal();
		bookModel.setPageAngles(0.0F, o, p, n);
		bookModel.method_22693(0.0625F);
		RenderSystem.disableRescaleNormal();
		GuiLighting.disable();
		RenderSystem.matrixMode(5889);
		RenderSystem.viewport(0, 0, this.minecraft.method_22683().getFramebufferWidth(), this.minecraft.method_22683().getFramebufferHeight());
		RenderSystem.popMatrix();
		RenderSystem.matrixMode(5888);
		RenderSystem.popMatrix();
		GuiLighting.disable();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		EnchantingPhrases.getInstance().setSeed((long)this.container.getSeed());
		int q = this.container.getLapisCount();

		for (int r = 0; r < 3; r++) {
			int s = k + 60;
			int t = s + 20;
			this.setBlitOffset(0);
			this.minecraft.getTextureManager().bindTexture(TEXTURE);
			int u = this.container.enchantmentPower[r];
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			if (u == 0) {
				this.blit(s, l + 14 + 19 * r, 0, 185, 108, 19);
			} else {
				String string = "" + u;
				int v = 86 - this.font.getStringWidth(string);
				String string2 = EnchantingPhrases.getInstance().generatePhrase(this.font, v);
				TextRenderer textRenderer = this.minecraft.getFontManager().getTextRenderer(MinecraftClient.ALT_TEXT_RENDERER_ID);
				int w = 6839882;
				if ((q < r + 1 || this.minecraft.player.experienceLevel < u) && !this.minecraft.player.abilities.creativeMode) {
					this.blit(s, l + 14 + 19 * r, 0, 185, 108, 19);
					this.blit(s + 1, l + 15 + 19 * r, 16 * r, 239, 16, 16);
					textRenderer.drawStringBounded(string2, t, l + 16 + 19 * r, v, (w & 16711422) >> 1);
					w = 4226832;
				} else {
					int x = i - (k + 60);
					int y = j - (l + 14 + 19 * r);
					if (x >= 0 && y >= 0 && x < 108 && y < 19) {
						this.blit(s, l + 14 + 19 * r, 0, 204, 108, 19);
						w = 16777088;
					} else {
						this.blit(s, l + 14 + 19 * r, 0, 166, 108, 19);
					}

					this.blit(s + 1, l + 15 + 19 * r, 16 * r, 223, 16, 16);
					textRenderer.drawStringBounded(string2, t, l + 16 + 19 * r, v, w);
					w = 8453920;
				}

				textRenderer = this.minecraft.textRenderer;
				textRenderer.drawWithShadow(string, (float)(t + 86 - textRenderer.getStringWidth(string)), (float)(l + 16 + 19 * r + 7), w);
			}
		}
	}

	@Override
	public void render(int i, int j, float f) {
		f = this.minecraft.getTickDelta();
		this.renderBackground();
		super.render(i, j, f);
		this.drawMouseoverTooltip(i, j);
		boolean bl = this.minecraft.player.abilities.creativeMode;
		int k = this.container.getLapisCount();

		for (int l = 0; l < 3; l++) {
			int m = this.container.enchantmentPower[l];
			Enchantment enchantment = Enchantment.byRawId(this.container.enchantmentId[l]);
			int n = this.container.enchantmentLevel[l];
			int o = l + 1;
			if (this.isPointWithinBounds(60, 14 + 19 * l, 108, 17, (double)i, (double)j) && m > 0 && n >= 0 && enchantment != null) {
				List<String> list = Lists.<String>newArrayList();
				list.add("" + Formatting.WHITE + Formatting.ITALIC + I18n.translate("container.enchant.clue", enchantment.getName(n).asFormattedString()));
				if (!bl) {
					list.add("");
					if (this.minecraft.player.experienceLevel < m) {
						list.add(Formatting.RED + I18n.translate("container.enchant.level.requirement", this.container.enchantmentPower[l]));
					} else {
						String string;
						if (o == 1) {
							string = I18n.translate("container.enchant.lapis.one");
						} else {
							string = I18n.translate("container.enchant.lapis.many", o);
						}

						Formatting formatting = k >= o ? Formatting.GRAY : Formatting.RED;
						list.add(formatting + "" + string);
						if (o == 1) {
							string = I18n.translate("container.enchant.level.one");
						} else {
							string = I18n.translate("container.enchant.level.many", o);
						}

						list.add(Formatting.GRAY + "" + string);
					}
				}

				this.renderTooltip(list, i, j);
				break;
			}
		}
	}

	public void method_2478() {
		ItemStack itemStack = this.container.getSlot(0).getStack();
		if (!ItemStack.areEqualIgnoreDamage(itemStack, this.field_2913)) {
			this.field_2913 = itemStack;

			do {
				this.field_2909 = this.field_2909 + (float)(this.random.nextInt(4) - this.random.nextInt(4));
			} while (this.nextPageAngle <= this.field_2909 + 1.0F && this.nextPageAngle >= this.field_2909 - 1.0F);
		}

		this.field_2915++;
		this.pageAngle = this.nextPageAngle;
		this.pageTurningSpeed = this.nextPageTurningSpeed;
		boolean bl = false;

		for (int i = 0; i < 3; i++) {
			if (this.container.enchantmentPower[i] != 0) {
				bl = true;
			}
		}

		if (bl) {
			this.nextPageTurningSpeed += 0.2F;
		} else {
			this.nextPageTurningSpeed -= 0.2F;
		}

		this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0F, 1.0F);
		float f = (this.field_2909 - this.nextPageAngle) * 0.4F;
		float g = 0.2F;
		f = MathHelper.clamp(f, -0.2F, 0.2F);
		this.field_2906 = this.field_2906 + (f - this.field_2906) * 0.9F;
		this.nextPageAngle = this.nextPageAngle + this.field_2906;
	}
}
