package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.DiffuseLighting;
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
public class EnchantingScreen extends ContainerScreen<EnchantingTableContainer> {
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

	public EnchantingScreen(EnchantingTableContainer container, PlayerInventory inventory, Text title) {
		super(container, inventory, title);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		this.font.draw(this.title.asFormattedString(), 12.0F, 5.0F, 4210752);
		this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
	}

	@Override
	public void tick() {
		super.tick();
		this.method_2478();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		int i = (this.width - this.containerWidth) / 2;
		int j = (this.height - this.containerHeight) / 2;

		for (int k = 0; k < 3; k++) {
			double d = mouseX - (double)(i + 60);
			double e = mouseY - (double)(j + 14 + 19 * k);
			if (d >= 0.0 && e >= 0.0 && d < 108.0 && e < 19.0 && this.container.onButtonClick(this.minecraft.player, k)) {
				this.minecraft.interactionManager.clickButton(this.container.syncId, k);
				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.containerWidth) / 2;
		int j = (this.height - this.containerHeight) / 2;
		this.blit(i, j, 0, 0, this.containerWidth, this.containerHeight);
		GlStateManager.pushMatrix();
		GlStateManager.matrixMode(5889);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		int k = (int)this.minecraft.window.getScaleFactor();
		GlStateManager.viewport((this.width - 320) / 2 * k, (this.height - 240) / 2 * k, 320 * k, 240 * k);
		GlStateManager.translatef(-0.34F, 0.23F, 0.0F);
		GlStateManager.multMatrix(Matrix4f.method_4929(90.0, 1.3333334F, 9.0F, 80.0F));
		float f = 1.0F;
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		DiffuseLighting.enable();
		GlStateManager.translatef(0.0F, 3.3F, -16.0F);
		GlStateManager.scalef(1.0F, 1.0F, 1.0F);
		float g = 5.0F;
		GlStateManager.scalef(5.0F, 5.0F, 5.0F);
		GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(BOOK_TEXURE);
		GlStateManager.rotatef(20.0F, 1.0F, 0.0F, 0.0F);
		float h = MathHelper.lerp(delta, this.pageTurningSpeed, this.nextPageTurningSpeed);
		GlStateManager.translatef((1.0F - h) * 0.2F, (1.0F - h) * 0.1F, (1.0F - h) * 0.25F);
		GlStateManager.rotatef(-(1.0F - h) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
		float l = MathHelper.lerp(delta, this.pageAngle, this.nextPageAngle) + 0.25F;
		float m = MathHelper.lerp(delta, this.pageAngle, this.nextPageAngle) + 0.75F;
		l = (l - (float)MathHelper.fastFloor((double)l)) * 1.6F - 0.3F;
		m = (m - (float)MathHelper.fastFloor((double)m)) * 1.6F - 0.3F;
		if (l < 0.0F) {
			l = 0.0F;
		}

		if (m < 0.0F) {
			m = 0.0F;
		}

		if (l > 1.0F) {
			l = 1.0F;
		}

		if (m > 1.0F) {
			m = 1.0F;
		}

		GlStateManager.enableRescaleNormal();
		bookModel.render(0.0F, l, m, h, 0.0F, 0.0625F);
		GlStateManager.disableRescaleNormal();
		DiffuseLighting.disable();
		GlStateManager.matrixMode(5889);
		GlStateManager.viewport(0, 0, this.minecraft.window.getFramebufferWidth(), this.minecraft.window.getFramebufferHeight());
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		DiffuseLighting.disable();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		EnchantingPhrases.getInstance().setSeed((long)this.container.getSeed());
		int n = this.container.getLapisCount();

		for (int o = 0; o < 3; o++) {
			int p = i + 60;
			int q = p + 20;
			this.blitOffset = 0;
			this.minecraft.getTextureManager().bindTexture(TEXTURE);
			int r = this.container.enchantmentPower[o];
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			if (r == 0) {
				this.blit(p, j + 14 + 19 * o, 0, 185, 108, 19);
			} else {
				String string = "" + r;
				int s = 86 - this.font.getStringWidth(string);
				String string2 = EnchantingPhrases.getInstance().generatePhrase(this.font, s);
				TextRenderer textRenderer = this.minecraft.getFontManager().getTextRenderer(MinecraftClient.ALT_TEXT_RENDERER_ID);
				int t = 6839882;
				if ((n < o + 1 || this.minecraft.player.experienceLevel < r) && !this.minecraft.player.abilities.creativeMode) {
					this.blit(p, j + 14 + 19 * o, 0, 185, 108, 19);
					this.blit(p + 1, j + 15 + 19 * o, 16 * o, 239, 16, 16);
					textRenderer.drawTrimmed(string2, q, j + 16 + 19 * o, s, (t & 16711422) >> 1);
					t = 4226832;
				} else {
					int u = mouseX - (i + 60);
					int v = mouseY - (j + 14 + 19 * o);
					if (u >= 0 && v >= 0 && u < 108 && v < 19) {
						this.blit(p, j + 14 + 19 * o, 0, 204, 108, 19);
						t = 16777088;
					} else {
						this.blit(p, j + 14 + 19 * o, 0, 166, 108, 19);
					}

					this.blit(p + 1, j + 15 + 19 * o, 16 * o, 223, 16, 16);
					textRenderer.drawTrimmed(string2, q, j + 16 + 19 * o, s, t);
					t = 8453920;
				}

				textRenderer = this.minecraft.textRenderer;
				textRenderer.drawWithShadow(string, (float)(q + 86 - textRenderer.getStringWidth(string)), (float)(j + 16 + 19 * o + 7), t);
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		delta = this.minecraft.getTickDelta();
		this.renderBackground();
		super.render(mouseX, mouseY, delta);
		this.drawMouseoverTooltip(mouseX, mouseY);
		boolean bl = this.minecraft.player.abilities.creativeMode;
		int i = this.container.getLapisCount();

		for (int j = 0; j < 3; j++) {
			int k = this.container.enchantmentPower[j];
			Enchantment enchantment = Enchantment.byRawId(this.container.enchantmentId[j]);
			int l = this.container.enchantmentLevel[j];
			int m = j + 1;
			if (this.isPointWithinBounds(60, 14 + 19 * j, 108, 17, (double)mouseX, (double)mouseY) && k > 0 && l >= 0 && enchantment != null) {
				List<String> list = Lists.<String>newArrayList();
				list.add("" + Formatting.WHITE + Formatting.ITALIC + I18n.translate("container.enchant.clue", enchantment.getName(l).asFormattedString()));
				if (!bl) {
					list.add("");
					if (this.minecraft.player.experienceLevel < k) {
						list.add(Formatting.RED + I18n.translate("container.enchant.level.requirement", this.container.enchantmentPower[j]));
					} else {
						String string;
						if (m == 1) {
							string = I18n.translate("container.enchant.lapis.one");
						} else {
							string = I18n.translate("container.enchant.lapis.many", m);
						}

						Formatting formatting = i >= m ? Formatting.GRAY : Formatting.RED;
						list.add(formatting + "" + string);
						if (m == 1) {
							string = I18n.translate("container.enchant.level.one");
						} else {
							string = I18n.translate("container.enchant.level.many", m);
						}

						list.add(Formatting.GRAY + "" + string);
					}
				}

				this.renderTooltip(list, mouseX, mouseY);
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
