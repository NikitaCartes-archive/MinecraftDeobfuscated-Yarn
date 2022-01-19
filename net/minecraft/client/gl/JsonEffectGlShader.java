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
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InvalidClassException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.EffectGlShader;
import net.minecraft.client.gl.EffectProgram;
import net.minecraft.client.gl.GlBlendState;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.Program;
import net.minecraft.client.gl.ShaderParseException;
import net.minecraft.client.gl.Uniform;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class JsonEffectGlShader
implements EffectGlShader,
AutoCloseable {
    private static final String PROGRAM_DIRECTORY = "shaders/program/";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Uniform DEFAULT_UNIFORM = new Uniform();
    private static final boolean field_32683 = true;
    private static JsonEffectGlShader activeShader;
    private static int activeProgramRef;
    private final Map<String, IntSupplier> samplerBinds = Maps.newHashMap();
    private final List<String> samplerNames = Lists.newArrayList();
    private final List<Integer> samplerShaderLocs = Lists.newArrayList();
    private final List<GlUniform> uniformData = Lists.newArrayList();
    private final List<Integer> uniformLocs = Lists.newArrayList();
    private final Map<String, GlUniform> uniformByName = Maps.newHashMap();
    private final int programRef;
    private final String name;
    private boolean uniformStateDirty;
    private final GlBlendState blendState;
    private final List<Integer> attribLocs;
    private final List<String> attribNames;
    private final EffectProgram vertexShader;
    private final EffectProgram fragmentShader;

    public JsonEffectGlShader(ResourceManager resource, String name) throws IOException {
        Identifier identifier = new Identifier(PROGRAM_DIRECTORY + name + ".json");
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
            this.blendState = JsonEffectGlShader.deserializeBlendState(JsonHelper.getObject(jsonObject, "blend", null));
            this.vertexShader = JsonEffectGlShader.loadEffect(resource, Program.Type.VERTEX, string);
            this.fragmentShader = JsonEffectGlShader.loadEffect(resource, Program.Type.FRAGMENT, string2);
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
            Object string2 = resource2 != null ? " (" + resource2.getResourcePackName() + ")" : "";
            ShaderParseException shaderParseException4 = ShaderParseException.wrap(exception4);
            shaderParseException4.addFaultyFile(identifier.getPath() + (String)string2);
            throw shaderParseException4;
        } finally {
            IOUtils.closeQuietly((Closeable)resource2);
        }
        this.markUniformsDirty();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static EffectProgram loadEffect(ResourceManager resourceManager, Program.Type type, String name) throws IOException {
        EffectProgram effectProgram;
        Program program = type.getProgramCache().get(name);
        if (program != null && !(program instanceof EffectProgram)) {
            throw new InvalidClassException("Program is not of type EffectProgram");
        }
        if (program == null) {
            Identifier identifier = new Identifier(PROGRAM_DIRECTORY + name + type.getFileExtension());
            Resource resource = resourceManager.getResource(identifier);
            try {
                effectProgram = EffectProgram.createFromResource(type, name, resource.getInputStream(), resource.getResourcePackName());
            } finally {
                IOUtils.closeQuietly((Closeable)resource);
            }
        } else {
            effectProgram = (EffectProgram)program;
        }
        return effectProgram;
    }

    public static GlBlendState deserializeBlendState(@Nullable JsonObject json) {
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
        RenderSystem.assertOnRenderThread();
        GlProgramManager.useProgram(0);
        activeProgramRef = -1;
        activeShader = null;
        for (int i = 0; i < this.samplerShaderLocs.size(); ++i) {
            if (this.samplerBinds.get(this.samplerNames.get(i)) == null) continue;
            GlStateManager._activeTexture(33984 + i);
            GlStateManager._disableTexture();
            GlStateManager._bindTexture(0);
        }
    }

    public void enable() {
        RenderSystem.assertOnGameThread();
        this.uniformStateDirty = false;
        activeShader = this;
        this.blendState.enable();
        if (this.programRef != activeProgramRef) {
            GlProgramManager.useProgram(this.programRef);
            activeProgramRef = this.programRef;
        }
        for (int i = 0; i < this.samplerShaderLocs.size(); ++i) {
            String string = this.samplerNames.get(i);
            IntSupplier intSupplier = this.samplerBinds.get(string);
            if (intSupplier == null) continue;
            RenderSystem.activeTexture(33984 + i);
            RenderSystem.enableTexture();
            int j = intSupplier.getAsInt();
            if (j == -1) continue;
            RenderSystem.bindTexture(j);
            GlUniform.uniform1(this.samplerShaderLocs.get(i), i);
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
        RenderSystem.assertOnRenderThread();
        return this.uniformByName.get(name);
    }

    public Uniform getUniformByNameOrDummy(String name) {
        RenderSystem.assertOnGameThread();
        GlUniform glUniform = this.getUniformByName(name);
        return glUniform == null ? DEFAULT_UNIFORM : glUniform;
    }

    private void finalizeUniformsAndSamplers() {
        int i;
        RenderSystem.assertOnRenderThread();
        IntArrayList intList = new IntArrayList();
        for (i = 0; i < this.samplerNames.size(); ++i) {
            String string = this.samplerNames.get(i);
            int j = GlUniform.getUniformLocation(this.programRef, string);
            if (j == -1) {
                LOGGER.warn("Shader {} could not find sampler named {} in the specified shader program.", (Object)this.name, (Object)string);
                this.samplerBinds.remove(string);
                intList.add(i);
                continue;
            }
            this.samplerShaderLocs.add(j);
        }
        for (i = intList.size() - 1; i >= 0; --i) {
            this.samplerNames.remove(intList.getInt(i));
        }
        for (GlUniform glUniform : this.uniformData) {
            String string2 = glUniform.getName();
            int k = GlUniform.getUniformLocation(this.programRef, string2);
            if (k == -1) {
                LOGGER.warn("Shader {} could not find uniform named {} in the specified shader program.", (Object)this.name, (Object)string2);
                continue;
            }
            this.uniformLocs.add(k);
            glUniform.setLoc(k);
            this.uniformByName.put(string2, glUniform);
        }
    }

    private void addSampler(JsonElement json) {
        JsonObject jsonObject = JsonHelper.asObject(json, "sampler");
        String string = JsonHelper.getString(jsonObject, "name");
        if (!JsonHelper.hasString(jsonObject, "file")) {
            this.samplerBinds.put(string, null);
            this.samplerNames.add(string);
            return;
        }
        this.samplerNames.add(string);
    }

    public void bindSampler(String samplerName, IntSupplier intSupplier) {
        if (this.samplerBinds.containsKey(samplerName)) {
            this.samplerBinds.remove(samplerName);
        }
        this.samplerBinds.put(samplerName, intSupplier);
        this.markUniformsDirty();
    }

    private void addUniform(JsonElement json) throws ShaderParseException {
        JsonObject jsonObject = JsonHelper.asObject(json, "uniform");
        String string = JsonHelper.getString(jsonObject, "name");
        int i = GlUniform.getTypeIndex(JsonHelper.getString(jsonObject, "type"));
        int j = JsonHelper.getInt(jsonObject, "count");
        float[] fs = new float[Math.max(j, 16)];
        JsonArray jsonArray = JsonHelper.getArray(jsonObject, "values");
        if (jsonArray.size() != j && jsonArray.size() > 1) {
            throw new ShaderParseException("Invalid amount of values specified (expected " + j + ", found " + jsonArray.size() + ")");
        }
        int k = 0;
        for (JsonElement jsonElement : jsonArray) {
            try {
                fs[k] = JsonHelper.asFloat(jsonElement, "value");
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
            glUniform.setForDataType((int)fs[0], (int)fs[1], (int)fs[2], (int)fs[3]);
        } else if (i <= 7) {
            glUniform.setForDataType(fs[0], fs[1], fs[2], fs[3]);
        } else {
            glUniform.set(fs);
        }
        this.uniformData.add(glUniform);
    }

    @Override
    public Program getVertexShader() {
        return this.vertexShader;
    }

    @Override
    public Program getFragmentShader() {
        return this.fragmentShader;
    }

    @Override
    public void attachReferencedShaders() {
        this.fragmentShader.attachTo(this);
        this.vertexShader.attachTo(this);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public int getProgramRef() {
        return this.programRef;
    }

    static {
        activeProgramRef = -1;
    }
}

