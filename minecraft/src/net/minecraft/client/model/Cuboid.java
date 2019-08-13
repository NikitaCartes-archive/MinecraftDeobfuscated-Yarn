package net.minecraft.client.model;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.GlAllocationUtils;

@Environment(EnvType.CLIENT)
public class Cuboid {
	public float textureWidth = 64.0F;
	public float textureHeight = 32.0F;
	private int textureOffsetU;
	private int textureOffsetV;
	public float rotationPointX;
	public float rotationPointY;
	public float rotationPointZ;
	public float pitch;
	public float yaw;
	public float roll;
	private boolean compiled;
	private int list;
	public boolean mirror;
	public boolean visible = true;
	public boolean field_3664;
	public final List<Box> boxes = Lists.<Box>newArrayList();
	public List<Cuboid> children;
	public final String name;
	public float x;
	public float y;
	public float z;

	public Cuboid(Model model, String string) {
		model.cuboidList.add(this);
		this.name = string;
		this.setTextureSize(model.textureWidth, model.textureHeight);
	}

	public Cuboid(Model model) {
		this(model, null);
	}

	public Cuboid(Model model, int i, int j) {
		this(model);
		this.setTextureOffset(i, j);
	}

	public void copyRotation(Cuboid cuboid) {
		this.pitch = cuboid.pitch;
		this.yaw = cuboid.yaw;
		this.roll = cuboid.roll;
		this.rotationPointX = cuboid.rotationPointX;
		this.rotationPointY = cuboid.rotationPointY;
		this.rotationPointZ = cuboid.rotationPointZ;
	}

	public void addChild(Cuboid cuboid) {
		if (this.children == null) {
			this.children = Lists.<Cuboid>newArrayList();
		}

		this.children.add(cuboid);
	}

	public void removeChild(Cuboid cuboid) {
		if (this.children != null) {
			this.children.remove(cuboid);
		}
	}

	public Cuboid setTextureOffset(int i, int j) {
		this.textureOffsetU = i;
		this.textureOffsetV = j;
		return this;
	}

	public Cuboid addBox(String string, float f, float g, float h, int i, int j, int k, float l, int m, int n) {
		string = this.name + "." + string;
		this.setTextureOffset(m, n);
		this.boxes.add(new Box(this, this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l).setName(string));
		return this;
	}

	public Cuboid addBox(float f, float g, float h, int i, int j, int k) {
		this.boxes.add(new Box(this, this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, 0.0F));
		return this;
	}

	public Cuboid addBox(float f, float g, float h, int i, int j, int k, boolean bl) {
		this.boxes.add(new Box(this, this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, 0.0F, bl));
		return this;
	}

	public void addBox(float f, float g, float h, int i, int j, int k, float l) {
		this.boxes.add(new Box(this, this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l));
	}

	public void addBox(float f, float g, float h, int i, int j, int k, float l, boolean bl) {
		this.boxes.add(new Box(this, this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l, bl));
	}

	public void setRotationPoint(float f, float g, float h) {
		this.rotationPointX = f;
		this.rotationPointY = g;
		this.rotationPointZ = h;
	}

	public void render(float f) {
		if (!this.field_3664) {
			if (this.visible) {
				if (!this.compiled) {
					this.compile(f);
				}

				GlStateManager.pushMatrix();
				GlStateManager.translatef(this.x, this.y, this.z);
				if (this.pitch != 0.0F || this.yaw != 0.0F || this.roll != 0.0F) {
					GlStateManager.pushMatrix();
					GlStateManager.translatef(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
					if (this.roll != 0.0F) {
						GlStateManager.rotatef(this.roll * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
					}

					if (this.yaw != 0.0F) {
						GlStateManager.rotatef(this.yaw * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
					}

					if (this.pitch != 0.0F) {
						GlStateManager.rotatef(this.pitch * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
					}

					GlStateManager.callList(this.list);
					if (this.children != null) {
						for (int i = 0; i < this.children.size(); i++) {
							((Cuboid)this.children.get(i)).render(f);
						}
					}

					GlStateManager.popMatrix();
				} else if (this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F) {
					GlStateManager.callList(this.list);
					if (this.children != null) {
						for (int i = 0; i < this.children.size(); i++) {
							((Cuboid)this.children.get(i)).render(f);
						}
					}
				} else {
					GlStateManager.pushMatrix();
					GlStateManager.translatef(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
					GlStateManager.callList(this.list);
					if (this.children != null) {
						for (int i = 0; i < this.children.size(); i++) {
							((Cuboid)this.children.get(i)).render(f);
						}
					}

					GlStateManager.popMatrix();
				}

				GlStateManager.popMatrix();
			}
		}
	}

	public void method_2852(float f) {
		if (!this.field_3664) {
			if (this.visible) {
				if (!this.compiled) {
					this.compile(f);
				}

				GlStateManager.pushMatrix();
				GlStateManager.translatef(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
				if (this.yaw != 0.0F) {
					GlStateManager.rotatef(this.yaw * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
				}

				if (this.pitch != 0.0F) {
					GlStateManager.rotatef(this.pitch * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
				}

				if (this.roll != 0.0F) {
					GlStateManager.rotatef(this.roll * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
				}

				GlStateManager.callList(this.list);
				GlStateManager.popMatrix();
			}
		}
	}

	public void applyTransform(float f) {
		if (!this.field_3664) {
			if (this.visible) {
				if (!this.compiled) {
					this.compile(f);
				}

				if (this.pitch != 0.0F || this.yaw != 0.0F || this.roll != 0.0F) {
					GlStateManager.translatef(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
					if (this.roll != 0.0F) {
						GlStateManager.rotatef(this.roll * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
					}

					if (this.yaw != 0.0F) {
						GlStateManager.rotatef(this.yaw * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
					}

					if (this.pitch != 0.0F) {
						GlStateManager.rotatef(this.pitch * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
					}
				} else if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
					GlStateManager.translatef(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
				}
			}
		}
	}

	private void compile(float f) {
		this.list = GlAllocationUtils.genLists(1);
		GlStateManager.newList(this.list, 4864);
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();

		for (int i = 0; i < this.boxes.size(); i++) {
			((Box)this.boxes.get(i)).render(bufferBuilder, f);
		}

		GlStateManager.endList();
		this.compiled = true;
	}

	public Cuboid setTextureSize(int i, int j) {
		this.textureWidth = (float)i;
		this.textureHeight = (float)j;
		return this;
	}
}
