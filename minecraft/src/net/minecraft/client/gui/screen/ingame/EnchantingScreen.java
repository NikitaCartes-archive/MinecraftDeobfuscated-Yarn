package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
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
		RenderSystem.matrixMode(5889);
		RenderSystem.pushMatrix();
		RenderSystem.loadIdentity();
		int m = (int)this.minecraft.getWindow().getScaleFactor();
		RenderSystem.viewport((this.width - 320) / 2 * m, (this.height - 240) / 2 * m, 320 * m, 240 * m);
		RenderSystem.translatef(-0.34F, 0.23F, 0.0F);
		RenderSystem.multMatrix(Matrix4f.method_4929(90.0, 1.3333334F, 9.0F, 80.0F));
		RenderSystem.matrixMode(5888);
		class_4587 lv = new class_4587();
		lv.method_22903();
		lv.method_22910().method_22668();
		lv.method_22904(0.0, 3.3F, 1984.0);
		float g = 5.0F;
		lv.method_22905(5.0F, 5.0F, 5.0F);
		lv.method_22907(Vector3f.field_20707.method_23214(180.0F, true));
		lv.method_22907(Vector3f.field_20703.method_23214(20.0F, true));
		float h = MathHelper.lerp(f, this.pageTurningSpeed, this.nextPageTurningSpeed);
		lv.method_22904((double)((1.0F - h) * 0.2F), (double)((1.0F - h) * 0.1F), (double)((1.0F - h) * 0.25F));
		lv.method_22907(Vector3f.field_20705.method_23214(-(1.0F - h) * 90.0F - 90.0F, true));
		lv.method_22907(Vector3f.field_20703.method_23214(180.0F, true));
		float n = MathHelper.lerp(f, this.pageAngle, this.nextPageAngle) + 0.25F;
		float o = MathHelper.lerp(f, this.pageAngle, this.nextPageAngle) + 0.75F;
		n = (n - (float)MathHelper.fastFloor((double)n)) * 1.6F - 0.3F;
		o = (o - (float)MathHelper.fastFloor((double)o)) * 1.6F - 0.3F;
		if (n < 0.0F) {
			n = 0.0F;
		}

		if (o < 0.0F) {
			o = 0.0F;
		}

		if (n > 1.0F) {
			n = 1.0F;
		}

		if (o > 1.0F) {
			o = 1.0F;
		}

		RenderSystem.enableRescaleNormal();
		bookModel.setPageAngles(0.0F, n, o, h);
		class_4597.class_4598 lv2 = class_4597.method_22991(Tessellator.getInstance().getBufferBuilder());
		class_4588 lv3 = lv2.getBuffer(BlockRenderLayer.method_23017(BOOK_TEXURE));
		class_4608.method_23211(lv3);
		bookModel.render(lv, lv3, 0.0625F, 15728880, null);
		lv3.method_22923();
		lv2.method_22993();
		lv.method_22909();
		RenderSystem.matrixMode(5889);
		RenderSystem.viewport(0, 0, this.minecraft.getWindow().getFramebufferWidth(), this.minecraft.getWindow().getFramebufferHeight());
		RenderSystem.popMatrix();
		RenderSystem.matrixMode(5888);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		EnchantingPhrases.getInstance().setSeed((long)this.container.getSeed());
		int p = this.container.getLapisCount();

		for (int q = 0; q < 3; q++) {
			int r = k + 60;
			int s = r + 20;
			this.setBlitOffset(0);
			this.minecraft.getTextureManager().bindTexture(TEXTURE);
			int t = this.container.enchantmentPower[q];
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			if (t == 0) {
				this.blit(r, l + 14 + 19 * q, 0, 185, 108, 19);
			} else {
				String string = "" + t;
				int u = 86 - this.font.getStringWidth(string);
				String string2 = EnchantingPhrases.getInstance().generatePhrase(this.font, u);
				TextRenderer textRenderer = this.minecraft.getFontManager().getTextRenderer(MinecraftClient.ALT_TEXT_RENDERER_ID);
				int v = 6839882;
				if ((p < q + 1 || this.minecraft.player.experienceLevel < t) && !this.minecraft.player.abilities.creativeMode) {
					this.blit(r, l + 14 + 19 * q, 0, 185, 108, 19);
					this.blit(r + 1, l + 15 + 19 * q, 16 * q, 239, 16, 16);
					textRenderer.drawStringBounded(string2, s, l + 16 + 19 * q, u, (v & 16711422) >> 1);
					v = 4226832;
				} else {
					int w = i - (k + 60);
					int x = j - (l + 14 + 19 * q);
					if (w >= 0 && x >= 0 && w < 108 && x < 19) {
						this.blit(r, l + 14 + 19 * q, 0, 204, 108, 19);
						v = 16777088;
					} else {
						this.blit(r, l + 14 + 19 * q, 0, 166, 108, 19);
					}

					this.blit(r + 1, l + 15 + 19 * q, 16 * q, 223, 16, 16);
					textRenderer.drawStringBounded(string2, s, l + 16 + 19 * q, u, v);
					v = 8453920;
				}

				textRenderer = this.minecraft.textRenderer;
				textRenderer.drawWithShadow(string, (float)(s + 86 - textRenderer.getStringWidth(string)), (float)(l + 16 + 19 * q + 7), v);
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
