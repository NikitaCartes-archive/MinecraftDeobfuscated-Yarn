/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlBlendState;
import net.minecraft.client.gl.GlProgram;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.gl.GlShader;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderParseException;
import net.minecraft.client.gl.Uniform;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class JsonGlProgram
implements GlProgram,
AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Uniform dummyUniform = new Uniform();
    private static JsonGlProgram activeProgram;
    private static int activeProgramRef;
    private final Map<String, Object> samplerBinds = Maps.newHashMap();
    private final List<String> samplerNames = Lists.newArrayList();
    private final List<Integer> samplerShaderLocs = Lists.newArrayList();
    private final List<GlUniform> uniformData = Lists.newArrayList();
    private final List<Integer> uniformLocs = Lists.newArrayList();
    private final Map<String, GlUniform> uniformByName = Maps.newHashMap();
    private final int programRef;
    private final String name;
    private final boolean useCullFace;
    private boolean uniformStateDirty;
    private final GlBlendState blendState;
    private final List<Integer> attribLocs;
    private final List<String> attribNames;
    private final GlShader vertexShader;
    private final GlShader fragmentShader;

    public JsonGlProgram(ResourceManager resource, String name) throws IOException {
        Identifier identifier = new Identifier("shaders/program/" + name + ".json");
        this.name = name;
        Resource resource2 = null;
        try {
            JsonArray jsonArray3;
            JsonArray jsonArray2;
            resource2 = resource.getResource(identifier);
            JsonObject jsonObject = JsonHelper.deserialize(new InputStreamReader(resource2.getInputStream(), StandardCharsets.UTF_8));
            String string = JsonHelper.getString(jsonObject, "vertex");
            String string2 = JsonHelper.getString(jsonObject, "fragment");
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, "samplers", null);
            if (jsonArray != null) {
                int i = 0;
                for (Object jsonElement : jsonArray) {
                    try {
                        this.addSampler((JsonElement)jsonElement);
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
                this.attribLocs = Lists.newArrayListWithCapacity(jsonArray2.size());
                this.attribNames = Lists.newArrayListWithCapacity(jsonArray2.size());
                for (Object jsonElement2 : jsonArray2) {
                    try {
                        this.attribNames.add(JsonHelper.asString((JsonElement)jsonElement2, "attribute"));
                    } catch (Exception exception2) {
                        ShaderParseException shaderParseException2 = ShaderParseException.wrap(exception2);
                        shaderParseException2.addFaultyElement("attributes[" + j + "]");
                        throw shaderParseException2;
                    }
                    ++j;
                }
            } else {
                this.attribLocs = null;
                this.attribNames = null;
            }
            if ((jsonArray3 = JsonHelper.getArray(jsonObject, "uniforms", null)) != null) {
                int k = 0;
                for (JsonElement jsonElement3 : jsonArray3) {
                    try {
                        this.addUniform(jsonElement3);
                    } catch (Exception exception3) {
                        ShaderParseException shaderParseException3 = ShaderParseException.wrap(exception3);
                        shaderParseException3.addFaultyElement("uniforms[" + k + "]");
                        throw shaderParseException3;
                    }
                    ++k;
                }
            }
            this.blendState = JsonGlProgram.deserializeBlendState(JsonHelper.getObject(jsonObject, "blend", null));
            this.useCullFace = JsonHelper.getBoolean(jsonObject, "cull", true);
            this.vertexShader = JsonGlProgram.getShader(resource, GlShader.Type.VERTEX, string);
            this.fragmentShader = JsonGlProgram.getShader(resource, GlShader.Type.FRAGMENT, string2);
            this.programRef = GlProgramManager.createProgram();
            GlProgramManager.linkProgram(this);
            this.finalizeUniformsAndSamplers();
            if (this.attribNames != null) {
                for (String string3 : this.attribNames) {
                    int l = GlUniform.getAttribLocation(this.programRef, string3);
                    this.attribLocs.add(l);
                }
            }
        } catch (Exception exception4) {
            ShaderParseException shaderParseException4 = ShaderParseException.wrap(exception4);
            shaderParseException4.addFaultyFile(identifier.getPath());
            throw shaderParseException4;
        } finally {
            IOUtils.closeQuietly((Closeable)resource2);
        }
        this.markUniformsDirty();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static GlShader getShader(ResourceManager resourceManager, GlShader.Type type, String name) throws IOException {
        GlShader glShader = type.getLoadedShaders().get(name);
        if (glShader == null) {
            Identifier identifier = new Identifier("shaders/program/" + name + type.getFileExtension());
            Resource resource = resourceManager.getResource(identifier);
            try {
                glShader = GlShader.createFromResource(type, name, resource.getInputStream());
            } finally {
                IOUtils.closeQuietly((Closeable)resource);
            }
        }
        return glShader;
    }

    public static GlBlendState deserializeBlendState(JsonObject json) {
        if (json == null) {
            return new GlBlendState();
        }
        int i = 32774;
        int j = 1;
        int k = 0;
        int l = 1;
        int m = 0;
        boolean bl = true;
        boolean bl2 = false;
        if (JsonHelper.hasString(json, "func") && (i = GlBlendState.getFuncFromString(json.get("func").getAsString())) != 32774) {
            bl = false;
        }
        if (JsonHelper.hasString(json, "srcrgb") && (j = GlBlendState.getComponentFromString(json.get("srcrgb").getAsString())) != 1) {
            bl = false;
        }
        if (JsonHelper.hasString(json, "dstrgb") && (k = GlBlendState.getComponentFromString(json.get("dstrgb").getAsString())) != 0) {
            bl = false;
        }
        if (JsonHelper.hasString(json, "srcalpha")) {
            l = GlBlendState.getComponentFromString(json.get("srcalpha").getAsString());
            if (l != 1) {
                bl = false;
            }
            bl2 = true;
        }
        if (JsonHelper.hasString(json, "dstalpha")) {
            m = GlBlendState.getComponentFromString(json.get("dstalpha").getAsString());
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
        for (GlUniform glUniform : this.uniformData) {
            glUniform.close();
        }
        GlProgramManager.deleteProgram(this);
    }

    public void disable() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlProgramManager.useProgram(0);
        activeProgramRef = -1;
        activeProgram = null;
        for (int i = 0; i < this.samplerShaderLocs.size(); ++i) {
            if (this.samplerBinds.get(this.samplerNames.get(i)) == null) continue;
            GlStateManager.activeTexture(33984 + i);
            GlStateManager.bindTexture(0);
        }
    }

    public void enable() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        this.uniformStateDirty = false;
        activeProgram = this;
        this.blendState.enable();
        if (this.programRef != activeProgramRef) {
            GlProgramManager.useProgram(this.programRef);
            activeProgramRef = this.programRef;
        }
        if (this.useCullFace) {
            RenderSystem.enableCull();
        } else {
            RenderSystem.disableCull();
        }
        for (int i = 0; i < this.samplerShaderLocs.size(); ++i) {
            if (this.samplerBinds.get(this.samplerNames.get(i)) == null) continue;
            RenderSystem.activeTexture(33984 + i);
            RenderSystem.enableTexture();
            Object object = this.samplerBinds.get(this.samplerNames.get(i));
            int j = -1;
            if (object instanceof Framebuffer) {
                j = ((Framebuffer)object).colorAttachment;
            } else if (object instanceof AbstractTexture) {
                j = ((AbstractTexture)object).getGlId();
            } else if (object instanceof Integer) {
                j = (Integer)object;
            }
            if (j == -1) continue;
            RenderSystem.bindTexture(j);
            GlUniform.uniform1(GlUniform.getUniformLocation(this.programRef, this.samplerNames.get(i)), i);
        }
        for (GlUniform glUniform : this.uniformData) {
            glUniform.upload();
        }
    }

    @Override
    public void markUniformsDirty() {
        this.uniformStateDirty = true;
    }

    @Nullable
    public GlUniform getUniformByName(String name) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return this.uniformByName.get(name);
    }

    public Uniform getUniformByNameOrDummy(String name) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlUniform glUniform = this.getUniformByName(name);
        return glUniform == null ? dummyUniform : glUniform;
    }

    private void finalizeUniformsAndSamplers() {
        int k;
        String string;
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        int i = 0;
        int j = 0;
        while (i < this.samplerNames.size()) {
            string = this.samplerNames.get(i);
            k = GlUniform.getUniformLocation(this.programRef, string);
            if (k == -1) {
                LOGGER.warn("Shader {}could not find sampler named {} in the specified shader program.", (Object)this.name, (Object)string);
                this.samplerBinds.remove(string);
                this.samplerNames.remove(j);
                --j;
            } else {
                this.samplerShaderLocs.add(k);
            }
            ++i;
            ++j;
        }
        for (GlUniform glUniform : this.uniformData) {
            string = glUniform.getName();
            k = GlUniform.getUniformLocation(this.programRef, string);
            if (k == -1) {
                LOGGER.warn("Could not find uniform named {} in the specified shader program.", (Object)string);
                continue;
            }
            this.uniformLocs.add(k);
            glUniform.setLoc(k);
            this.uniformByName.put(string, glUniform);
        }
    }

    private void addSampler(JsonElement jsonElement) {
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "sampler");
        String string = JsonHelper.getString(jsonObject, "name");
        if (!JsonHelper.hasString(jsonObject, "file")) {
            this.samplerBinds.put(string, null);
            this.samplerNames.add(string);
            return;
        }
        this.samplerNames.add(string);
    }

    public void bindSampler(String samplerName, Object object) {
        if (this.samplerBinds.containsKey(samplerName)) {
            this.samplerBinds.remove(samplerName);
        }
        this.samplerBinds.put(samplerName, object);
        this.markUniformsDirty();
    }

    private void addUniform(JsonElement jsonElement) throws ShaderParseException {
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
        this.uniformData.add(glUniform);
    }

    @Override
    public GlShader getVertexShader() {
        return this.vertexShader;
    }

    @Override
    public GlShader getFragmentShader() {
        return this.fragmentShader;
    }

    @Override
    public int getProgramRef() {
        return this.programRef;
    }

    static {
        activeProgramRef = -1;
    }
}

