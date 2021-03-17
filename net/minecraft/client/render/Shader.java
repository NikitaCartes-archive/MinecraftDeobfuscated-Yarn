/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5913;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlBlendState;
import net.minecraft.client.gl.GlProgram;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.gl.GlShader;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderParseException;
import net.minecraft.client.gl.Uniform;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class Shader
implements GlProgram,
AutoCloseable {
    private static final Logger field_29483 = LogManager.getLogger();
    private static final Uniform field_29484 = new Uniform();
    private static Shader field_29485;
    private static int field_29486;
    private final Map<String, Object> field_29487 = Maps.newHashMap();
    private final List<String> field_29488 = Lists.newArrayList();
    private final List<Integer> field_29489 = Lists.newArrayList();
    private final List<GlUniform> field_29490 = Lists.newArrayList();
    private final List<Integer> field_29491 = Lists.newArrayList();
    private final Map<String, GlUniform> field_29492 = Maps.newHashMap();
    private final int field_29493;
    private final String field_29494;
    private boolean field_29495;
    private final GlBlendState field_29464;
    private final List<Integer> field_29465;
    private final List<String> field_29466;
    private final GlShader field_29467;
    private final GlShader field_29468;
    private final VertexFormat field_29469;
    @Nullable
    public final GlUniform field_29470;
    @Nullable
    public final GlUniform field_29471;
    @Nullable
    public final GlUniform field_29472;
    @Nullable
    public final GlUniform field_29473;
    @Nullable
    public final GlUniform field_29474;
    @Nullable
    public final GlUniform field_29475;
    @Nullable
    public final GlUniform field_29476;
    @Nullable
    public final GlUniform field_29477;
    @Nullable
    public final GlUniform field_29478;
    @Nullable
    public final GlUniform field_29479;
    @Nullable
    public final GlUniform field_29480;
    @Nullable
    public final GlUniform field_29481;
    @Nullable
    public final GlUniform field_29482;

    public Shader(ResourceFactory resourceFactory, String string, VertexFormat vertexFormat) throws IOException {
        this.field_29494 = string;
        this.field_29469 = vertexFormat;
        Identifier identifier = new Identifier("shaders/core/" + string + ".json");
        Resource resource = null;
        try {
            JsonArray jsonArray3;
            JsonArray jsonArray2;
            resource = resourceFactory.getResource(identifier);
            JsonObject jsonObject = JsonHelper.deserialize(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            String string2 = JsonHelper.getString(jsonObject, "vertex");
            String string3 = JsonHelper.getString(jsonObject, "fragment");
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, "samplers", null);
            if (jsonArray != null) {
                int i = 0;
                for (Object jsonElement : jsonArray) {
                    try {
                        this.method_34580((JsonElement)jsonElement);
                    } catch (Exception exception) {
                        ShaderParseException shaderParseException = ShaderParseException.wrap(exception);
                        shaderParseException.addFaultyElement("samplers[" + i + "]");
                        throw shaderParseException;
                    }
                    ++i;
                }
            }
            if ((jsonArray2 = JsonHelper.getArray(jsonObject, "attributes", null)) != null) {
                int j = 0;
                this.field_29465 = Lists.newArrayListWithCapacity(jsonArray2.size());
                this.field_29466 = Lists.newArrayListWithCapacity(jsonArray2.size());
                for (JsonElement jsonElement2 : jsonArray2) {
                    try {
                        this.field_29466.add(JsonHelper.asString(jsonElement2, "attribute"));
                    } catch (Exception exception2) {
                        ShaderParseException shaderParseException2 = ShaderParseException.wrap(exception2);
                        shaderParseException2.addFaultyElement("attributes[" + j + "]");
                        throw shaderParseException2;
                    }
                    ++j;
                }
            } else {
                this.field_29465 = null;
                this.field_29466 = null;
            }
            if ((jsonArray3 = JsonHelper.getArray(jsonObject, "uniforms", null)) != null) {
                int k = 0;
                for (JsonElement jsonElement3 : jsonArray3) {
                    try {
                        this.method_34584(jsonElement3);
                    } catch (Exception exception3) {
                        ShaderParseException shaderParseException3 = ShaderParseException.wrap(exception3);
                        shaderParseException3.addFaultyElement("uniforms[" + k + "]");
                        throw shaderParseException3;
                    }
                    ++k;
                }
            }
            this.field_29464 = Shader.method_34581(JsonHelper.getObject(jsonObject, "blend", null));
            this.field_29467 = Shader.method_34579(resourceFactory, GlShader.Type.VERTEX, string2);
            this.field_29468 = Shader.method_34579(resourceFactory, GlShader.Type.FRAGMENT, string3);
            this.field_29493 = GlProgramManager.createProgram();
            if (this.field_29466 != null) {
                int k = 0;
                for (String string4 : vertexFormat.method_34445()) {
                    GlUniform.method_34419(this.field_29493, k, string4);
                    this.field_29465.add(k);
                    ++k;
                }
            }
            GlProgramManager.linkProgram(this);
            this.method_34588();
        } catch (Exception exception4) {
            ShaderParseException shaderParseException4 = ShaderParseException.wrap(exception4);
            shaderParseException4.addFaultyFile(identifier.getPath());
            throw shaderParseException4;
        } finally {
            IOUtils.closeQuietly((Closeable)resource);
        }
        this.markUniformsDirty();
        this.field_29470 = this.method_34582("ModelViewMat");
        this.field_29471 = this.method_34582("ProjMat");
        this.field_29472 = this.method_34582("TextureMat");
        this.field_29473 = this.method_34582("ScreenSize");
        this.field_29474 = this.method_34582("ColorModulator");
        this.field_29475 = this.method_34582("Light0_Direction");
        this.field_29476 = this.method_34582("Light1_Direction");
        this.field_29477 = this.method_34582("FogStart");
        this.field_29478 = this.method_34582("FogEnd");
        this.field_29479 = this.method_34582("FogColor");
        this.field_29480 = this.method_34582("LineWidth");
        this.field_29481 = this.method_34582("GameTime");
        this.field_29482 = this.method_34582("ChunkOffset");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static GlShader method_34579(final ResourceFactory resourceFactory, GlShader.Type type, String string) throws IOException {
        GlShader glShader2;
        GlShader glShader = type.getLoadedShaders().get(string);
        if (glShader == null) {
            String string2 = "shaders/core/" + string + type.getFileExtension();
            Identifier identifier = new Identifier(string2);
            Resource resource = resourceFactory.getResource(identifier);
            final String string3 = FileNameUtil.getPosixFullPath(string2);
            try {
                glShader2 = GlShader.createFromResource(type, string, resource.getInputStream(), resource.getResourcePackName(), new class_5913(){
                    private final Set<String> field_29498 = Sets.newHashSet();

                    /*
                     * Enabled aggressive block sorting
                     * Enabled unnecessary exception pruning
                     * Enabled aggressive exception aggregation
                     */
                    @Override
                    public String method_34233(boolean bl, String string) {
                        string = FileNameUtil.normalizeToPosix((bl ? string3 : "shaders/include/") + string);
                        if (!this.field_29498.add(string)) {
                            return null;
                        }
                        Identifier identifier = new Identifier(string);
                        try (Resource resource = resourceFactory.getResource(identifier);){
                            String string2 = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
                            return string2;
                        } catch (IOException iOException) {
                            field_29483.error("Could not open GLSL import {}: {}", (Object)string, (Object)iOException.getMessage());
                            return "#error " + iOException.getMessage();
                        }
                    }
                });
            } finally {
                IOUtils.closeQuietly((Closeable)resource);
            }
        } else {
            glShader2 = glShader;
        }
        return glShader2;
    }

    public static GlBlendState method_34581(JsonObject jsonObject) {
        if (jsonObject == null) {
            return new GlBlendState();
        }
        int i = 32774;
        int j = 1;
        int k = 0;
        int l = 1;
        int m = 0;
        boolean bl = true;
        boolean bl2 = false;
        if (JsonHelper.hasString(jsonObject, "func") && (i = GlBlendState.getFuncFromString(jsonObject.get("func").getAsString())) != 32774) {
            bl = false;
        }
        if (JsonHelper.hasString(jsonObject, "srcrgb") && (j = GlBlendState.getComponentFromString(jsonObject.get("srcrgb").getAsString())) != 1) {
            bl = false;
        }
        if (JsonHelper.hasString(jsonObject, "dstrgb") && (k = GlBlendState.getComponentFromString(jsonObject.get("dstrgb").getAsString())) != 0) {
            bl = false;
        }
        if (JsonHelper.hasString(jsonObject, "srcalpha")) {
            l = GlBlendState.getComponentFromString(jsonObject.get("srcalpha").getAsString());
            if (l != 1) {
                bl = false;
            }
            bl2 = true;
        }
        if (JsonHelper.hasString(jsonObject, "dstalpha")) {
            m = GlBlendState.getComponentFromString(jsonObject.get("dstalpha").getAsString());
            if (m != 0) {
                bl = false;
            }
            bl2 = true;
        }
        if (bl) {
            return new GlBlendState();
        }
        if (bl2) {
            return new GlBlendState(j, k, l, m, i);
        }
        return new GlBlendState(j, k, i);
    }

    @Override
    public void close() {
        for (GlUniform glUniform : this.field_29490) {
            glUniform.close();
        }
        GlProgramManager.deleteProgram(this);
    }

    public void method_34585() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlProgramManager.useProgram(0);
        field_29486 = -1;
        field_29485 = null;
        int i = GlStateManager.method_34411();
        for (int j = 0; j < this.field_29489.size(); ++j) {
            if (this.field_29487.get(this.field_29488.get(j)) == null) continue;
            GlStateManager.activeTexture(33984 + j);
            GlStateManager.bindTexture(0);
        }
        GlStateManager.activeTexture(i);
    }

    public void method_34586() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        this.field_29495 = false;
        field_29485 = this;
        this.field_29464.enable();
        if (this.field_29493 != field_29486) {
            GlProgramManager.useProgram(this.field_29493);
            field_29486 = this.field_29493;
        }
        int i = GlStateManager.method_34411();
        for (int j = 0; j < this.field_29489.size(); ++j) {
            String string = this.field_29488.get(j);
            if (this.field_29487.get(string) == null) continue;
            int k = GlUniform.getUniformLocation(this.field_29493, string);
            GlUniform.uniform1(k, j);
            RenderSystem.activeTexture(33984 + j);
            RenderSystem.enableTexture();
            Object object = this.field_29487.get(string);
            int l = -1;
            if (object instanceof Framebuffer) {
                l = ((Framebuffer)object).getColorAttachment();
            } else if (object instanceof AbstractTexture) {
                l = ((AbstractTexture)object).getGlId();
            } else if (object instanceof Integer) {
                l = (Integer)object;
            }
            if (l == -1) continue;
            RenderSystem.bindTexture(l);
        }
        GlStateManager.activeTexture(i);
        for (GlUniform glUniform : this.field_29490) {
            glUniform.upload();
        }
    }

    @Override
    public void markUniformsDirty() {
        this.field_29495 = true;
    }

    @Nullable
    public GlUniform method_34582(String string) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return this.field_29492.get(string);
    }

    private void method_34588() {
        int i;
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        IntArrayList intList = new IntArrayList();
        for (i = 0; i < this.field_29488.size(); ++i) {
            String string = this.field_29488.get(i);
            int j = GlUniform.getUniformLocation(this.field_29493, string);
            if (j == -1) {
                field_29483.warn("Shader {} could not find sampler named {} in the specified shader program.", (Object)this.field_29494, (Object)string);
                this.field_29487.remove(string);
                intList.add(i);
                continue;
            }
            this.field_29489.add(j);
        }
        for (i = intList.size() - 1; i >= 0; --i) {
            int k = intList.getInt(i);
            this.field_29488.remove(k);
        }
        for (GlUniform glUniform : this.field_29490) {
            String string2 = glUniform.getName();
            int l = GlUniform.getUniformLocation(this.field_29493, string2);
            if (l == -1) {
                field_29483.warn("Shader {} could not find uniform named {} in the specified shader program.", (Object)this.field_29494, (Object)string2);
                continue;
            }
            this.field_29491.add(l);
            glUniform.setLoc(l);
            this.field_29492.put(string2, glUniform);
        }
    }

    private void method_34580(JsonElement jsonElement) {
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "sampler");
        String string = JsonHelper.getString(jsonObject, "name");
        if (!JsonHelper.hasString(jsonObject, "file")) {
            this.field_29487.put(string, null);
            this.field_29488.add(string);
            return;
        }
        this.field_29488.add(string);
    }

    public void method_34583(String string, Object object) {
        this.field_29487.put(string, object);
        this.markUniformsDirty();
    }

    private void method_34584(JsonElement jsonElement) throws ShaderParseException {
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "uniform");
        String string = JsonHelper.getString(jsonObject, "name");
        int i = GlUniform.getTypeIndex(JsonHelper.getString(jsonObject, "type"));
        int j = JsonHelper.getInt(jsonObject, "count");
        float[] fs = new float[Math.max(j, 16)];
        JsonArray jsonArray = JsonHelper.getArray(jsonObject, "values");
        if (jsonArray.size() != j && jsonArray.size() > 1) {
            throw new ShaderParseException("Invalid amount of values specified (expected " + j + ", found " + jsonArray.size() + ")");
        }
        int k = 0;
        for (JsonElement jsonElement2 : jsonArray) {
            try {
                fs[k] = JsonHelper.asFloat(jsonElement2, "value");
            } catch (Exception exception) {
                ShaderParseException shaderParseException = ShaderParseException.wrap(exception);
                shaderParseException.addFaultyElement("values[" + k + "]");
                throw shaderParseException;
            }
            ++k;
        }
        if (j > 1 && jsonArray.size() == 1) {
            while (k < j) {
                fs[k] = fs[0];
                ++k;
            }
        }
        int l = j > 1 && j <= 4 && i < 8 ? j - 1 : 0;
        GlUniform glUniform = new GlUniform(string, i + l, j, this);
        if (i <= 3) {
            glUniform.set((int)fs[0], (int)fs[1], (int)fs[2], (int)fs[3]);
        } else if (i <= 7) {
            glUniform.setForDataType(fs[0], fs[1], fs[2], fs[3]);
        } else {
            glUniform.set(fs);
        }
        this.field_29490.add(glUniform);
    }

    @Override
    public GlShader getVertexShader() {
        return this.field_29467;
    }

    @Override
    public GlShader getFragmentShader() {
        return this.field_29468;
    }

    @Override
    public void method_34418() {
        this.field_29468.attachTo(this);
        this.field_29467.attachTo(this);
    }

    @Override
    public int getProgramRef() {
        return this.field_29493;
    }

    static {
        field_29486 = -1;
    }
}

