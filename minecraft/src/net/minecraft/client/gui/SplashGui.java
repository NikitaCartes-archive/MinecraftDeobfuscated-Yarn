package net.minecraft.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class SplashGui extends Gui {
	private static final Logger field_2485 = LogManager.getLogger();
	private static final Identifier LOGO = new Identifier("textures/gui/title/mojang.png");
	private Identifier logo;

	@Override
	protected void onInitialized() {
		try {
			InputStream inputStream = this.client.getResourcePackDownloader().getPack().open(ResourceType.ASSETS, LOGO);
			this.logo = this.client.getTextureManager().registerDynamicTexture("logo", new NativeImageBackedTexture(NativeImage.fromInputStream(inputStream)));
		} catch (IOException var2) {
			field_2485.error("Unable to load logo: {}", LOGO, var2);
		}
	}

	@Override
	public void onClosed() {
		this.client.getTextureManager().destroyTexture(this.logo);
		this.logo = null;
	}

	@Override
	public void draw(int i, int j, float f) {
		GlFramebuffer glFramebuffer = new GlFramebuffer(this.width, this.height, true, MinecraftClient.isSystemMac);
		glFramebuffer.beginWrite(false);
		this.client.getTextureManager().bindTexture(this.logo);
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		GlStateManager.disableDepthTest();
		GlStateManager.enableTexture();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
		bufferBuilder.vertex(0.0, (double)this.client.window.getFramebufferHeight(), 0.0).texture(0.0, 0.0).color(255, 255, 255, 255).next();
		bufferBuilder.vertex((double)this.client.window.getFramebufferWidth(), (double)this.client.window.getFramebufferHeight(), 0.0)
			.texture(0.0, 0.0)
			.color(255, 255, 255, 255)
			.next();
		bufferBuilder.vertex((double)this.client.window.getFramebufferWidth(), 0.0, 0.0).texture(0.0, 0.0).color(255, 255, 255, 255).next();
		bufferBuilder.vertex(0.0, 0.0, 0.0).texture(0.0, 0.0).color(255, 255, 255, 255).next();
		tessellator.draw();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int k = 256;
		int l = 256;
		this.client
			.method_1501((this.client.window.getScaledWidth() - 256) / 2, (this.client.window.getScaledHeight() - 256) / 2, 0, 0, 256, 256, 255, 255, 255, 255);
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		glFramebuffer.endWrite();
		glFramebuffer.draw(this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight());
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.1F);
	}
}
