package net.minecraft.client.gl;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceManager;
import net.minecraft.sortme.Matrix4f;

@Environment(EnvType.CLIENT)
public class Shader implements AutoCloseable {
	private final GlProgram field_1540;
	public final GlFramebuffer field_1536;
	public final GlFramebuffer field_1538;
	private final List<Object> field_1534 = Lists.<Object>newArrayList();
	private final List<String> field_1539 = Lists.<String>newArrayList();
	private final List<Integer> field_1533 = Lists.<Integer>newArrayList();
	private final List<Integer> field_1537 = Lists.<Integer>newArrayList();
	private Matrix4f field_1535;

	public Shader(ResourceManager resourceManager, String string, GlFramebuffer glFramebuffer, GlFramebuffer glFramebuffer2) throws IOException {
		this.field_1540 = new GlProgram(resourceManager, string);
		this.field_1536 = glFramebuffer;
		this.field_1538 = glFramebuffer2;
	}

	public void close() {
		this.field_1540.close();
	}

	public void method_1292(String string, Object object, int i, int j) {
		this.field_1539.add(this.field_1539.size(), string);
		this.field_1534.add(this.field_1534.size(), object);
		this.field_1533.add(this.field_1533.size(), i);
		this.field_1537.add(this.field_1537.size(), j);
	}

	private void method_1294() {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.disableDepthTest();
		GlStateManager.disableAlphaTest();
		GlStateManager.disableFog();
		GlStateManager.disableLighting();
		GlStateManager.disableColorMaterial();
		GlStateManager.enableTexture();
		GlStateManager.bindTexture(0);
	}

	public void method_1291(Matrix4f matrix4f) {
		this.field_1535 = matrix4f;
	}

	public void method_1293(float f) {
		this.method_1294();
		this.field_1536.endWrite();
		float g = (float)this.field_1538.texWidth;
		float h = (float)this.field_1538.texHeight;
		GlStateManager.viewport(0, 0, (int)g, (int)h);
		this.field_1540.bindSampler("DiffuseSampler", this.field_1536);

		for (int i = 0; i < this.field_1534.size(); i++) {
			this.field_1540.bindSampler((String)this.field_1539.get(i), this.field_1534.get(i));
			this.field_1540
				.getUniformByNameOrDummy("AuxSize" + i)
				.method_1255((float)((Integer)this.field_1533.get(i)).intValue(), (float)((Integer)this.field_1537.get(i)).intValue());
		}

		this.field_1540.getUniformByNameOrDummy("ProjMat").method_1250(this.field_1535);
		this.field_1540.getUniformByNameOrDummy("InSize").method_1255((float)this.field_1536.texWidth, (float)this.field_1536.texHeight);
		this.field_1540.getUniformByNameOrDummy("OutSize").method_1255(g, h);
		this.field_1540.getUniformByNameOrDummy("Time").method_1251(f);
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		this.field_1540
			.getUniformByNameOrDummy("ScreenSize")
			.method_1255((float)minecraftClient.window.getWindowWidth(), (float)minecraftClient.window.getWindowHeight());
		this.field_1540.enable();
		this.field_1538.clear(MinecraftClient.isSystemMac);
		this.field_1538.beginWrite(false);
		GlStateManager.depthMask(false);
		GlStateManager.colorMask(true, true, true, true);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		vertexBuffer.begin(7, VertexFormats.POSITION_COLOR);
		vertexBuffer.vertex(0.0, 0.0, 500.0).color(255, 255, 255, 255).next();
		vertexBuffer.vertex((double)g, 0.0, 500.0).color(255, 255, 255, 255).next();
		vertexBuffer.vertex((double)g, (double)h, 500.0).color(255, 255, 255, 255).next();
		vertexBuffer.vertex(0.0, (double)h, 500.0).color(255, 255, 255, 255).next();
		tessellator.draw();
		GlStateManager.depthMask(true);
		GlStateManager.colorMask(true, true, true, true);
		this.field_1540.disable();
		this.field_1538.endWrite();
		this.field_1536.endRead();

		for (Object object : this.field_1534) {
			if (object instanceof GlFramebuffer) {
				((GlFramebuffer)object).endRead();
			}
		}
	}

	public GlProgram method_1295() {
		return this.field_1540;
	}
}
