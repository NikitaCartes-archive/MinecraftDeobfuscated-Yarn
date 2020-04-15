package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
public class EnchantmentScreen extends HandledScreen<EnchantmentScreenHandler> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/enchanting_table.png");
	private static final Identifier BOOK_TEXURE = new Identifier("textures/entity/enchanting_table_book.png");
	private static final BookModel BOOK_MODEL = new BookModel();
	private final Random random = new Random();
	public int ticks;
	public float nextPageAngle;
	public float pageAngle;
	public float approximatePageAngle;
	public float pageRotationSpeed;
	public float nextPageTurningSpeed;
	public float pageTurningSpeed;
	private ItemStack stack = ItemStack.EMPTY;

	public EnchantmentScreen(EnchantmentScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		this.textRenderer.draw(this.title.asFormattedString(), 12.0F, 5.0F, 4210752);
		this.textRenderer.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0F, (float)(this.backgroundHeight - 96 + 2), 4210752);
	}

	@Override
	public void tick() {
		super.tick();
		this.doTick();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;

		for (int k = 0; k < 3; k++) {
			double d = mouseX - (double)(i + 60);
			double e = mouseY - (double)(j + 14 + 19 * k);
			if (d >= 0.0 && e >= 0.0 && d < 108.0 && e < 19.0 && this.handler.onButtonClick(this.client.player, k)) {
				this.client.interactionManager.clickButton(this.handler.syncId, k);
				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		DiffuseLighting.disableGuiDepthLighting();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
		RenderSystem.matrixMode(5889);
		RenderSystem.pushMatrix();
		RenderSystem.loadIdentity();
		int k = (int)this.client.getWindow().getScaleFactor();
		RenderSystem.viewport((this.width - 320) / 2 * k, (this.height - 240) / 2 * k, 320 * k, 240 * k);
		RenderSystem.translatef(-0.34F, 0.23F, 0.0F);
		RenderSystem.multMatrix(Matrix4f.viewboxMatrix(90.0, 1.3333334F, 9.0F, 80.0F));
		RenderSystem.matrixMode(5888);
		MatrixStack matrixStack = new MatrixStack();
		matrixStack.push();
		MatrixStack.Entry entry = matrixStack.peek();
		entry.getModel().loadIdentity();
		entry.getNormal().loadIdentity();
		matrixStack.translate(0.0, 3.3F, 1984.0);
		float f = 5.0F;
		matrixStack.scale(5.0F, 5.0F, 5.0F);
		matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(20.0F));
		float g = MathHelper.lerp(delta, this.pageTurningSpeed, this.nextPageTurningSpeed);
		matrixStack.translate((double)((1.0F - g) * 0.2F), (double)((1.0F - g) * 0.1F), (double)((1.0F - g) * 0.25F));
		float h = -(1.0F - g) * 90.0F - 90.0F;
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h));
		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0F));
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

		RenderSystem.enableRescaleNormal();
		BOOK_MODEL.setPageAngles(0.0F, l, m, g);
		VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
		VertexConsumer vertexConsumer = immediate.getBuffer(BOOK_MODEL.getLayer(BOOK_TEXURE));
		BOOK_MODEL.render(matrixStack, vertexConsumer, 15728880, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
		immediate.draw();
		matrixStack.pop();
		RenderSystem.matrixMode(5889);
		RenderSystem.viewport(0, 0, this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
		RenderSystem.popMatrix();
		RenderSystem.matrixMode(5888);
		DiffuseLighting.enableGuiDepthLighting();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		EnchantingPhrases.getInstance().setSeed((long)this.handler.getSeed());
		int n = this.handler.getLapisCount();

		for (int o = 0; o < 3; o++) {
			int p = i + 60;
			int q = p + 20;
			this.setZOffset(0);
			this.client.getTextureManager().bindTexture(TEXTURE);
			int r = this.handler.enchantmentPower[o];
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			if (r == 0) {
				this.drawTexture(p, j + 14 + 19 * o, 0, 185, 108, 19);
			} else {
				String string = "" + r;
				int s = 86 - this.textRenderer.getStringWidth(string);
				String string2 = EnchantingPhrases.getInstance().generatePhrase(this.textRenderer, s);
				TextRenderer textRenderer = this.client.getFontManager().getTextRenderer(MinecraftClient.ALT_TEXT_RENDERER_ID);
				int t = 6839882;
				if ((n < o + 1 || this.client.player.experienceLevel < r) && !this.client.player.abilities.creativeMode) {
					this.drawTexture(p, j + 14 + 19 * o, 0, 185, 108, 19);
					this.drawTexture(p + 1, j + 15 + 19 * o, 16 * o, 239, 16, 16);
					textRenderer.drawTrimmed(string2, q, j + 16 + 19 * o, s, (t & 16711422) >> 1);
					t = 4226832;
				} else {
					int u = mouseX - (i + 60);
					int v = mouseY - (j + 14 + 19 * o);
					if (u >= 0 && v >= 0 && u < 108 && v < 19) {
						this.drawTexture(p, j + 14 + 19 * o, 0, 204, 108, 19);
						t = 16777088;
					} else {
						this.drawTexture(p, j + 14 + 19 * o, 0, 166, 108, 19);
					}

					this.drawTexture(p + 1, j + 15 + 19 * o, 16 * o, 223, 16, 16);
					textRenderer.drawTrimmed(string2, q, j + 16 + 19 * o, s, t);
					t = 8453920;
				}

				textRenderer = this.client.textRenderer;
				textRenderer.drawWithShadow(string, (float)(q + 86 - textRenderer.getStringWidth(string)), (float)(j + 16 + 19 * o + 7), t);
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		delta = this.client.getTickDelta();
		this.renderBackground();
		super.render(mouseX, mouseY, delta);
		this.drawMouseoverTooltip(mouseX, mouseY);
		boolean bl = this.client.player.abilities.creativeMode;
		int i = this.handler.getLapisCount();

		for (int j = 0; j < 3; j++) {
			int k = this.handler.enchantmentPower[j];
			Enchantment enchantment = Enchantment.byRawId(this.handler.enchantmentId[j]);
			int l = this.handler.enchantmentLevel[j];
			int m = j + 1;
			if (this.isPointWithinBounds(60, 14 + 19 * j, 108, 17, (double)mouseX, (double)mouseY) && k > 0 && l >= 0 && enchantment != null) {
				List<String> list = Lists.<String>newArrayList();
				list.add("" + Formatting.WHITE + Formatting.ITALIC + I18n.translate("container.enchant.clue", enchantment.getName(l).asFormattedString()));
				if (!bl) {
					list.add("");
					if (this.client.player.experienceLevel < k) {
						list.add(Formatting.RED + I18n.translate("container.enchant.level.requirement", this.handler.enchantmentPower[j]));
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

	public void doTick() {
		ItemStack itemStack = this.handler.getSlot(0).getStack();
		if (!ItemStack.areEqual(itemStack, this.stack)) {
			this.stack = itemStack;

			do {
				this.approximatePageAngle = this.approximatePageAngle + (float)(this.random.nextInt(4) - this.random.nextInt(4));
			} while (this.nextPageAngle <= this.approximatePageAngle + 1.0F && this.nextPageAngle >= this.approximatePageAngle - 1.0F);
		}

		this.ticks++;
		this.pageAngle = this.nextPageAngle;
		this.pageTurningSpeed = this.nextPageTurningSpeed;
		boolean bl = false;

		for (int i = 0; i < 3; i++) {
			if (this.handler.enchantmentPower[i] != 0) {
				bl = true;
			}
		}

		if (bl) {
			this.nextPageTurningSpeed += 0.2F;
		} else {
			this.nextPageTurningSpeed -= 0.2F;
		}

		this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0F, 1.0F);
		float f = (this.approximatePageAngle - this.nextPageAngle) * 0.4F;
		float g = 0.2F;
		f = MathHelper.clamp(f, -0.2F, 0.2F);
		this.pageRotationSpeed = this.pageRotationSpeed + (f - this.pageRotationSpeed) * 0.9F;
		this.nextPageAngle = this.nextPageAngle + this.pageRotationSpeed;
	}
}