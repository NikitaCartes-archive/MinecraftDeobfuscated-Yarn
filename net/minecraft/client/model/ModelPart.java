/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.model;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.GlAllocationUtils;

@Environment(value=EnvType.CLIENT)
public class ModelPart {
    public float textureWidth = 64.0f;
    public float textureHeight = 32.0f;
    private int textureOffsetU;
    private int textureOffsetV;
    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    public float pitch;
    public float yaw;
    public float roll;
    private boolean compiled;
    private int renderList;
    public boolean mirror;
    public boolean visible = true;
    public boolean field_3664;
    public final List<Cuboid> cuboids = Lists.newArrayList();
    public List<ModelPart> children;
    public final String name;
    public float x;
    public float y;
    public float z;

    public ModelPart(Model model, String string) {
        model.cuboidList.add(this);
        this.name = string;
        this.setTextureSize(model.textureWidth, model.textureHeight);
    }

    public ModelPart(Model model) {
        this(model, null);
    }

    public ModelPart(Model model, int i, int j) {
        this(model);
        this.setTextureOffset(i, j);
    }

    public void copyRotation(ModelPart modelPart) {
        this.pitch = modelPart.pitch;
        this.yaw = modelPart.yaw;
        this.roll = modelPart.roll;
        this.rotationPointX = modelPart.rotationPointX;
        this.rotationPointY = modelPart.rotationPointY;
        this.rotationPointZ = modelPart.rotationPointZ;
    }

    public void addChild(ModelPart modelPart) {
        if (this.children == null) {
            this.children = Lists.newArrayList();
        }
        this.children.add(modelPart);
    }

    public void removeChild(ModelPart modelPart) {
        if (this.children != null) {
            this.children.remove(modelPart);
        }
    }

    public ModelPart setTextureOffset(int i, int j) {
        this.textureOffsetU = i;
        this.textureOffsetV = j;
        return this;
    }

    public ModelPart addCuboid(String string, float f, float g, float h, int i, int j, int k, float l, int m, int n) {
        string = this.name + "." + string;
        this.setTextureOffset(m, n);
        this.cuboids.add(new Cuboid(this, this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l).setName(string));
        return this;
    }

    public ModelPart addCuboid(float f, float g, float h, int i, int j, int k) {
        this.cuboids.add(new Cuboid(this, this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, 0.0f));
        return this;
    }

    public ModelPart addCuboid(float f, float g, float h, int i, int j, int k, boolean bl) {
        this.cuboids.add(new Cuboid(this, this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, 0.0f, bl));
        return this;
    }

    public void addCuboid(float f, float g, float h, int i, int j, int k, float l) {
        this.cuboids.add(new Cuboid(this, this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l));
    }

    public void addCuboid(float f, float g, float h, int i, int j, int k, float l, boolean bl) {
        this.cuboids.add(new Cuboid(this, this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l, bl));
    }

    public void setRotationPoint(float f, float g, float h) {
        this.rotationPointX = f;
        this.rotationPointY = g;
        this.rotationPointZ = h;
    }

    public void render(float f) {
        if (this.field_3664) {
            return;
        }
        if (!this.visible) {
            return;
        }
        if (!this.compiled) {
            this.compile(f);
        }
        RenderSystem.pushMatrix();
        RenderSystem.translatef(this.x, this.y, this.z);
        if (this.pitch != 0.0f || this.yaw != 0.0f || this.roll != 0.0f) {
            RenderSystem.pushMatrix();
            RenderSystem.translatef(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
            if (this.roll != 0.0f) {
                RenderSystem.rotatef(this.roll * 57.295776f, 0.0f, 0.0f, 1.0f);
            }
            if (this.yaw != 0.0f) {
                RenderSystem.rotatef(this.yaw * 57.295776f, 0.0f, 1.0f, 0.0f);
            }
            if (this.pitch != 0.0f) {
                RenderSystem.rotatef(this.pitch * 57.295776f, 1.0f, 0.0f, 0.0f);
            }
            RenderSystem.callList(this.renderList);
            if (this.children != null) {
                for (int i = 0; i < this.children.size(); ++i) {
                    this.children.get(i).render(f);
                }
            }
            RenderSystem.popMatrix();
        } else if (this.rotationPointX != 0.0f || this.rotationPointY != 0.0f || this.rotationPointZ != 0.0f) {
            RenderSystem.pushMatrix();
            RenderSystem.translatef(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
            RenderSystem.callList(this.renderList);
            if (this.children != null) {
                for (int i = 0; i < this.children.size(); ++i) {
                    this.children.get(i).render(f);
                }
            }
            RenderSystem.popMatrix();
        } else {
            RenderSystem.callList(this.renderList);
            if (this.children != null) {
                for (int i = 0; i < this.children.size(); ++i) {
                    this.children.get(i).render(f);
                }
            }
        }
        RenderSystem.popMatrix();
    }

    public void method_2852(float f) {
        if (this.field_3664) {
            return;
        }
        if (!this.visible) {
            return;
        }
        if (!this.compiled) {
            this.compile(f);
        }
        RenderSystem.pushMatrix();
        RenderSystem.translatef(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
        if (this.yaw != 0.0f) {
            RenderSystem.rotatef(this.yaw * 57.295776f, 0.0f, 1.0f, 0.0f);
        }
        if (this.pitch != 0.0f) {
            RenderSystem.rotatef(this.pitch * 57.295776f, 1.0f, 0.0f, 0.0f);
        }
        if (this.roll != 0.0f) {
            RenderSystem.rotatef(this.roll * 57.295776f, 0.0f, 0.0f, 1.0f);
        }
        RenderSystem.callList(this.renderList);
        RenderSystem.popMatrix();
    }

    public void applyTransform(float f) {
        if (this.field_3664) {
            return;
        }
        if (!this.visible) {
            return;
        }
        if (!this.compiled) {
            this.compile(f);
        }
        if (this.pitch != 0.0f || this.yaw != 0.0f || this.roll != 0.0f) {
            RenderSystem.translatef(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
            if (this.roll != 0.0f) {
                RenderSystem.rotatef(this.roll * 57.295776f, 0.0f, 0.0f, 1.0f);
            }
            if (this.yaw != 0.0f) {
                RenderSystem.rotatef(this.yaw * 57.295776f, 0.0f, 1.0f, 0.0f);
            }
            if (this.pitch != 0.0f) {
                RenderSystem.rotatef(this.pitch * 57.295776f, 1.0f, 0.0f, 0.0f);
            }
        } else if (this.rotationPointX != 0.0f || this.rotationPointY != 0.0f || this.rotationPointZ != 0.0f) {
            RenderSystem.translatef(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
        }
    }

    private void compile(float f) {
        this.renderList = GlAllocationUtils.genLists(1);
        RenderSystem.newList(this.renderList, 4864);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();
        for (int i = 0; i < this.cuboids.size(); ++i) {
            this.cuboids.get(i).render(bufferBuilder, f);
        }
        RenderSystem.endList();
        this.compiled = true;
    }

    public ModelPart setTextureSize(int i, int j) {
        this.textureWidth = i;
        this.textureHeight = j;
        return this;
    }
}

