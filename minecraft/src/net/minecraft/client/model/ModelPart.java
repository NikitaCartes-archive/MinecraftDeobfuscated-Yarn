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
public class ModelPart {
	public float textureWidth = 64.0F;
	public float textureHeight = 32.0F;
	private int textureOffsetU;
	private int textureOffsetV;
	public float pivotX;
	public float pivotY;
	public float pivotZ;
	public float pitch;
	public float yaw;
	public float roll;
	private boolean compiled;
	private int list;
	public boolean mirror;
	public boolean visible = true;
	public boolean field_3664;
	public final List<Box> boxes = Lists.<Box>newArrayList();
	public List<ModelPart> children;
	public final String name;
	public float x;
	public float y;
	public float z;

	public ModelPart(Model owner, String name) {
		owner.cuboidList.add(this);
		this.name = name;
		this.setTextureSize(owner.textureWidth, owner.textureHeight);
	}

	public ModelPart(Model model) {
		this(model, null);
	}

	public ModelPart(Model model, int textureOffsetU, int textureOffsetV) {
		this(model);
		this.setTextureOffset(textureOffsetU, textureOffsetV);
	}

	public void copyPositionAndRotation(ModelPart modelPart) {
		this.pitch = modelPart.pitch;
		this.yaw = modelPart.yaw;
		this.roll = modelPart.roll;
		this.pivotX = modelPart.pivotX;
		this.pivotY = modelPart.pivotY;
		this.pivotZ = modelPart.pivotZ;
	}

	public void addChild(ModelPart part) {
		if (this.children == null) {
			this.children = Lists.<ModelPart>newArrayList();
		}

		this.children.add(part);
	}

	public void removeChild(ModelPart modelPart) {
		if (this.children != null) {
			this.children.remove(modelPart);
		}
	}

	public ModelPart setTextureOffset(int textureOffsetU, int textureOffsetV) {
		this.textureOffsetU = textureOffsetU;
		this.textureOffsetV = textureOffsetV;
		return this;
	}

	public ModelPart addCuboid(String name, float x, float y, float z, int sizeX, int sizeY, int sizeZ, float extra, int textureOffsetU, int textureOffsetV) {
		name = this.name + "." + name;
		this.setTextureOffset(textureOffsetU, textureOffsetV);
		this.boxes.add(new Box(this, this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, extra).setName(name));
		return this;
	}

	public ModelPart addCuboid(float x, float y, float z, int sizeX, int sizeY, int sizeZ) {
		this.boxes.add(new Box(this, this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, 0.0F));
		return this;
	}

	public ModelPart addCuboid(float x, float y, float z, int sizeX, int sizeY, int sizeZ, boolean mirror) {
		this.boxes.add(new Box(this, this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, 0.0F, mirror));
		return this;
	}

	public void addCuboid(float x, float y, float z, int sizeX, int sizeY, int sizeZ, float extra) {
		this.boxes.add(new Box(this, this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, extra));
	}

	public void addCuboid(float x, float y, float z, int sizeX, int sizeY, int sizeZ, float extra, boolean mirror) {
		this.boxes.add(new Box(this, this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, extra, mirror));
	}

	public void setPivot(float x, float y, float z) {
		this.pivotX = x;
		this.pivotY = y;
		this.pivotZ = z;
	}

	public void render(float scale) {
		if (!this.field_3664) {
			if (this.visible) {
				if (!this.compiled) {
					this.compile(scale);
				}

				GlStateManager.pushMatrix();
				GlStateManager.translatef(this.x, this.y, this.z);
				if (this.pitch != 0.0F || this.yaw != 0.0F || this.roll != 0.0F) {
					GlStateManager.pushMatrix();
					GlStateManager.translatef(this.pivotX * scale, this.pivotY * scale, this.pivotZ * scale);
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
							((ModelPart)this.children.get(i)).render(scale);
						}
					}

					GlStateManager.popMatrix();
				} else if (this.pivotX == 0.0F && this.pivotY == 0.0F && this.pivotZ == 0.0F) {
					GlStateManager.callList(this.list);
					if (this.children != null) {
						for (int i = 0; i < this.children.size(); i++) {
							((ModelPart)this.children.get(i)).render(scale);
						}
					}
				} else {
					GlStateManager.pushMatrix();
					GlStateManager.translatef(this.pivotX * scale, this.pivotY * scale, this.pivotZ * scale);
					GlStateManager.callList(this.list);
					if (this.children != null) {
						for (int i = 0; i < this.children.size(); i++) {
							((ModelPart)this.children.get(i)).render(scale);
						}
					}

					GlStateManager.popMatrix();
				}

				GlStateManager.popMatrix();
			}
		}
	}

	public void method_2852(float scale) {
		if (!this.field_3664) {
			if (this.visible) {
				if (!this.compiled) {
					this.compile(scale);
				}

				GlStateManager.pushMatrix();
				GlStateManager.translatef(this.pivotX * scale, this.pivotY * scale, this.pivotZ * scale);
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

	public void applyTransform(float scale) {
		if (!this.field_3664) {
			if (this.visible) {
				if (!this.compiled) {
					this.compile(scale);
				}

				if (this.pitch != 0.0F || this.yaw != 0.0F || this.roll != 0.0F) {
					GlStateManager.translatef(this.pivotX * scale, this.pivotY * scale, this.pivotZ * scale);
					if (this.roll != 0.0F) {
						GlStateManager.rotatef(this.roll * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
					}

					if (this.yaw != 0.0F) {
						GlStateManager.rotatef(this.yaw * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
					}

					if (this.pitch != 0.0F) {
						GlStateManager.rotatef(this.pitch * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
					}
				} else if (this.pivotX != 0.0F || this.pivotY != 0.0F || this.pivotZ != 0.0F) {
					GlStateManager.translatef(this.pivotX * scale, this.pivotY * scale, this.pivotZ * scale);
				}
			}
		}
	}

	private void compile(float scale) {
		this.list = GlAllocationUtils.genLists(1);
		GlStateManager.newList(this.list, 4864);
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

		for (int i = 0; i < this.boxes.size(); i++) {
			((Box)this.boxes.get(i)).render(bufferBuilder, scale);
		}

		GlStateManager.endList();
		this.compiled = true;
	}

	public ModelPart setTextureSize(int width, int height) {
		this.textureWidth = (float)width;
		this.textureHeight = (float)height;
		return this;
	}
}
