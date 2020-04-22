package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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
	protected void drawForeground(MatrixStack matrixStack, int i, int j) {
		this.textRenderer.draw(matrixStack, this.title, 12.0F, 5.0F, 4210752);
		this.textRenderer.draw(matrixStack, this.playerInventory.getDisplayName(), 8.0F, (float)(this.backgroundHeight - 96 + 2), 4210752);
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
	protected void drawBackground(MatrixStack matrixStack, float f, int mouseY, int i) {
		DiffuseLighting.disableGuiDepthLighting();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int j = (this.width - this.backgroundWidth) / 2;
		int k = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrixStack, j, k, 0, 0, this.backgroundWidth, this.backgroundHeight);
		RenderSystem.matrixMode(5889);
		RenderSystem.pushMatrix();
		RenderSystem.loadIdentity();
		int l = (int)this.client.getWindow().getScaleFactor();
		RenderSystem.viewport((this.width - 320) / 2 * l, (this.height - 240) / 2 * l, 320 * l, 240 * l);
		RenderSystem.translatef(-0.34F, 0.23F, 0.0F);
		RenderSystem.multMatrix(Matrix4f.viewboxMatrix(90.0, 1.3333334F, 9.0F, 80.0F));
		RenderSystem.matrixMode(5888);
		matrixStack.push();
		MatrixStack.Entry entry = matrixStack.peek();
		entry.getModel().loadIdentity();
		entry.getNormal().loadIdentity();
		matrixStack.translate(0.0, 3.3F, 1984.0);
		float g = 5.0F;
		matrixStack.scale(5.0F, 5.0F, 5.0F);
		matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(20.0F));
		float h = MathHelper.lerp(f, this.pageTurningSpeed, this.nextPageTurningSpeed);
		matrixStack.translate((double)((1.0F - h) * 0.2F), (double)((1.0F - h) * 0.1F), (double)((1.0F - h) * 0.25F));
		float m = -(1.0F - h) * 90.0F - 90.0F;
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(m));
		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0F));
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
		BOOK_MODEL.setPageAngles(0.0F, n, o, h);
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
		int p = this.handler.getLapisCount();

		for (int q = 0; q < 3; q++) {
			int r = j + 60;
			int s = r + 20;
			this.setZOffset(0);
			this.client.getTextureManager().bindTexture(TEXTURE);
			int t = this.handler.enchantmentPower[q];
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			if (t == 0) {
				this.drawTexture(matrixStack, r, k + 14 + 19 * q, 0, 185, 108, 19);
			} else {
				String string = "" + t;
				int u = 86 - this.textRenderer.getWidth(string);
				Text text = EnchantingPhrases.getInstance().generatePhrase(this.textRenderer, u);
				int v = 6839882;
				if ((p < q + 1 || this.client.player.experienceLevel < t) && !this.client.player.abilities.creativeMode) {
					this.drawTexture(matrixStack, r, k + 14 + 19 * q, 0, 185, 108, 19);
					this.drawTexture(matrixStack, r + 1, k + 15 + 19 * q, 16 * q, 239, 16, 16);
					this.textRenderer.drawTrimmed(text, s, k + 16 + 19 * q, u, (v & 16711422) >> 1);
					v = 4226832;
				} else {
					int w = mouseY - (j + 60);
					int x = i - (k + 14 + 19 * q);
					if (w >= 0 && x >= 0 && w < 108 && x < 19) {
						this.drawTexture(matrixStack, r, k + 14 + 19 * q, 0, 204, 108, 19);
						v = 16777088;
					} else {
						this.drawTexture(matrixStack, r, k + 14 + 19 * q, 0, 166, 108, 19);
					}

					this.drawTexture(matrixStack, r + 1, k + 15 + 19 * q, 16 * q, 223, 16, 16);
					this.textRenderer.drawTrimmed(text, s, k + 16 + 19 * q, u, v);
					v = 8453920;
				}

				this.textRenderer.drawWithShadow(matrixStack, string, (float)(s + 86 - this.textRenderer.getWidth(string)), (float)(k + 16 + 19 * q + 7), v);
			}
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		delta = this.client.getTickDelta();
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
		boolean bl = this.client.player.abilities.creativeMode;
		int i = this.handler.getLapisCount();

		for (int j = 0; j < 3; j++) {
			int k = this.handler.enchantmentPower[j];
			Enchantment enchantment = Enchantment.byRawId(this.handler.enchantmentId[j]);
			int l = this.handler.enchantmentLevel[j];
			int m = j + 1;
			if (this.isPointWithinBounds(60, 14 + 19 * j, 108, 17, (double)mouseX, (double)mouseY) && k > 0 && l >= 0 && enchantment != null) {
				List<Text> list = Lists.<Text>newArrayList();
				list.add(new TranslatableText("container.enchant.clue", enchantment.getName(l)).formatted(new Formatting[]{Formatting.WHITE, Formatting.ITALIC}));
				if (!bl) {
					list.add(LiteralText.EMPTY);
					if (this.client.player.experienceLevel < k) {
						list.add(new TranslatableText("container.enchant.level.requirement", this.handler.enchantmentPower[j]).formatted(Formatting.RED));
					} else {
						MutableText mutableText;
						if (m == 1) {
							mutableText = new TranslatableText("container.enchant.lapis.one");
						} else {
							mutableText = new TranslatableText("container.enchant.lapis.many", m);
						}

						list.add(mutableText.formatted(i >= m ? Formatting.GRAY : Formatting.RED));
						MutableText mutableText2;
						if (m == 1) {
							mutableText2 = new TranslatableText("container.enchant.level.one");
						} else {
							mutableText2 = new TranslatableText("container.enchant.level.many", m);
						}

						list.add(mutableText2.formatted(Formatting.GRAY));
					}
				}

				this.renderTooltip(matrices, list, mouseX, mouseY);
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
