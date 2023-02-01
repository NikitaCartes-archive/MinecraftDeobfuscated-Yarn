/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GLImportProcessor;
import net.minecraft.client.gl.GlBlendState;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgramSetupView;
import net.minecraft.client.gl.ShaderStage;
import net.minecraft.client.gl.Uniform;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidHierarchicalFileException;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PathUtil;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * Represents a shader program. Also known as a program object that can be
 * created with {@code glCreateProgram}.
 * 
 * <p><strong>Warning:</strong> This class is referred to as a shader in
 * strings. However, this does NOT represent a shader object that can be
 * created with {@code glCreateShader}. {@link ShaderStage} is what
 * represents a shader object.
 * 
 * @see <a href="https://www.khronos.org/opengl/wiki/GLSL_Object#Program_objects">
 * GLSL Object - OpenGL Wiki (Program objects)</a>
 */
@Environment(value=EnvType.CLIENT)
public class ShaderProgram
implements ShaderProgramSetupView,
AutoCloseable {
    public static final String SHADERS_DIRECTORY = "shaders";
    private static final String CORE_DIRECTORY = "shaders/core/";
    private static final String INCLUDE_DIRECTORY = "shaders/include/";
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Uniform DEFAULT_UNIFORM = new Uniform();
    private static final boolean field_32780 = true;
    private static ShaderProgram activeProgram;
    private static int activeProgramGlRef;
    private final Map<String, Object> samplers = Maps.newHashMap();
    private final List<String> samplerNames = Lists.newArrayList();
    private final List<Integer> loadedSamplerIds = Lists.newArrayList();
    private final List<GlUniform> uniforms = Lists.newArrayList();
    private final List<Integer> loadedUniformIds = Lists.newArrayList();
    private final Map<String, GlUniform> loadedUniforms = Maps.newHashMap();
    private final int glRef;
    private final String name;
    private boolean dirty;
    private final GlBlendState blendState;
    private final List<Integer> loadedAttributeIds;
    private final List<String> attributeNames;
    private final ShaderStage vertexShader;
    private final ShaderStage fragmentShader;
    private final VertexFormat format;
    @Nullable
    public final GlUniform modelViewMat;
    @Nullable
    public final GlUniform projectionMat;
    @Nullable
    public final GlUniform viewRotationMat;
    @Nullable
    public final GlUniform textureMat;
    @Nullable
    public final GlUniform screenSize;
    @Nullable
    public final GlUniform colorModulator;
    @Nullable
    public final GlUniform light0Direction;
    @Nullable
    public final GlUniform light1Direction;
    @Nullable
    public final GlUniform glintAlpha;
    @Nullable
    public final GlUniform fogStart;
    @Nullable
    public final GlUniform fogEnd;
    @Nullable
    public final GlUniform fogColor;
    @Nullable
    public final GlUniform fogShape;
    @Nullable
    public final GlUniform lineWidth;
    @Nullable
    public final GlUniform gameTime;
    @Nullable
    public final GlUniform chunkOffset;

    public ShaderProgram(ResourceFactory factory, String name, VertexFormat format) throws IOException {
        this.name = name;
        this.format = format;
        Identifier identifier = new Identifier(CORE_DIRECTORY + name + ".json");
        try (BufferedReader reader = factory.openAsReader(identifier);){
            JsonArray jsonArray3;
            JsonArray jsonArray2;
            JsonObject jsonObject = JsonHelper.deserialize(reader);
            String string = JsonHelper.getString(jsonObject, "vertex");
            String string2 = JsonHelper.getString(jsonObject, "fragment");
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, "samplers", null);
            if (jsonArray != null) {
                int i = 0;
                for (Object jsonElement : jsonArray) {
                    try {
                        this.readSampler((JsonElement)jsonElement);
                    } catch (Exception exception) {
                        InvalidHierarchicalFileException invalidHierarchicalFileException = InvalidHierarchicalFileException.wrap(exception);
                        invalidHierarchicalFileException.addInvalidKey("samplers[" + i + "]");
                        throw invalidHierarchicalFileException;
                    }
                    ++i;
                }
            }
            if ((jsonArray2 = JsonHelper.getArray(jsonObject, "attributes", null)) != null) {
                int j = 0;
                this.loadedAttributeIds = Lists.newArrayListWithCapacity(jsonArray2.size());
                this.attributeNames = Lists.newArrayListWithCapacity(jsonArray2.size());
                for (JsonElement jsonElement2 : jsonArray2) {
                    try {
                        this.attributeNames.add(JsonHelper.asString(jsonElement2, "attribute"));
                    } catch (Exception exception2) {
                        InvalidHierarchicalFileException invalidHierarchicalFileException2 = InvalidHierarchicalFileException.wrap(exception2);
                        invalidHierarchicalFileException2.addInvalidKey("attributes[" + j + "]");
                        throw invalidHierarchicalFileException2;
                    }
                    ++j;
                }
            } else {
                this.loadedAttributeIds = null;
                this.attributeNames = null;
            }
            if ((jsonArray3 = JsonHelper.getArray(jsonObject, "uniforms", null)) != null) {
                int k = 0;
                for (JsonElement jsonElement3 : jsonArray3) {
                    try {
                        this.addUniform(jsonElement3);
                    } catch (Exception exception3) {
                        InvalidHierarchicalFileException invalidHierarchicalFileException3 = InvalidHierarchicalFileException.wrap(exception3);
                        invalidHierarchicalFileException3.addInvalidKey("uniforms[" + k + "]");
                        throw invalidHierarchicalFileException3;
                    }
                    ++k;
                }
            }
            this.blendState = ShaderProgram.readBlendState(JsonHelper.getObject(jsonObject, "blend", null));
            this.vertexShader = ShaderProgram.loadShader(factory, ShaderStage.Type.VERTEX, string);
            this.fragmentShader = ShaderProgram.loadShader(factory, ShaderStage.Type.FRAGMENT, string2);
            this.glRef = GlProgramManager.createProgram();
            if (this.attributeNames != null) {
                int k = 0;
                for (String string3 : format.getAttributeNames()) {
                    GlUniform.bindAttribLocation(this.glRef, k, string3);
                    this.loadedAttributeIds.add(k);
                    ++k;
                }
            }
            GlProgramManager.linkProgram(this);
            this.loadReferences();
        } catch (Exception exception4) {
            InvalidHierarchicalFileException invalidHierarchicalFileException4 = InvalidHierarchicalFileException.wrap(exception4);
            invalidHierarchicalFileException4.addInvalidFile(identifier.getPath());
            throw invalidHierarchicalFileException4;
        }
        this.markUniformsDirty();
        this.modelViewMat = this.getUniform("ModelViewMat");
        this.projectionMat = this.getUniform("ProjMat");
        this.viewRotationMat = this.getUniform("IViewRotMat");
        this.textureMat = this.getUniform("TextureMat");
        this.screenSize = this.getUniform("ScreenSize");
        this.colorModulator = this.getUniform("ColorModulator");
        this.light0Direction = this.getUniform("Light0_Direction");
        this.light1Direction = this.getUniform("Light1_Direction");
        this.glintAlpha = this.getUniform("GlintAlpha");
        this.fogStart = this.getUniform("FogStart");
        this.fogEnd = this.getUniform("FogEnd");
        this.fogColor = this.getUniform("FogColor");
        this.fogShape = this.getUniform("FogShape");
        this.lineWidth = this.getUniform("LineWidth");
        this.gameTime = this.getUniform("GameTime");
        this.chunkOffset = this.getUniform("ChunkOffset");
    }

    private static ShaderStage loadShader(final ResourceFactory factory, ShaderStage.Type type, String name) throws IOException {
        ShaderStage shaderStage2;
        ShaderStage shaderStage = type.getLoadedShaders().get(name);
        if (shaderStage == null) {
            String string = CORE_DIRECTORY + name + type.getFileExtension();
            Resource resource = factory.getResourceOrThrow(new Identifier(string));
            try (InputStream inputStream = resource.getInputStream();){
                final String string2 = PathUtil.getPosixFullPath(string);
                shaderStage2 = ShaderStage.createFromResource(type, name, inputStream, resource.getResourcePackName(), new GLImportProcessor(){
                    private final Set<String> visitedImports = Sets.newHashSet();

                    @Override
                    public String loadImport(boolean inline, String name) {
                        String string;
                        block9: {
                            name = PathUtil.normalizeToPosix((inline ? string2 : ShaderProgram.INCLUDE_DIRECTORY) + name);
                            if (!this.visitedImports.add(name)) {
                                return null;
                            }
                            Identifier identifier = new Identifier(name);
                            BufferedReader reader = factory.openAsReader(identifier);
                            try {
                                string = IOUtils.toString(reader);
                                if (reader == null) break block9;
                            } catch (Throwable throwable) {
                                try {
                                    if (reader != null) {
                                        try {
                                            ((Reader)reader).close();
                                        } catch (Throwable throwable2) {
                                            throwable.addSuppressed(throwable2);
                                        }
                                    }
                                    throw throwable;
                                } catch (IOException iOException) {
                                    LOGGER.error("Could not open GLSL import {}: {}", (Object)name, (Object)iOException.getMessage());
                                    return "#error " + iOException.getMessage();
                                }
                            }
                            ((Reader)reader).close();
                        }
                        return string;
                    }
                });
            }
        } else {
            shaderStage2 = shaderStage;
        }
        return shaderStage2;
    }

    public static GlBlendState readBlendState(JsonObject json) {
        if (json == null) {
            return new GlBlendState();
        }
        int i = GlConst.GL_FUNC_ADD;
        int j = 1;
        int k = 0;
        int l = 1;
        int m = 0;
        boolean bl = true;
        boolean bl2 = false;
        if (JsonHelper.hasString(json, "func") && (i = GlBlendState.getModeFromString(json.get("func").getAsString())) != GlConst.GL_FUNC_ADD) {
            bl = false;
        }
        if (JsonHelper.hasString(json, "srcrgb") && (j = GlBlendState.getFactorFromString(json.get("srcrgb").getAsString())) != 1) {
            bl = false;
        }
        if (JsonHelper.hasString(json, "dstrgb") && (k = GlBlendState.getFactorFromString(json.get("dstrgb").getAsString())) != 0) {
            bl = false;
        }
        if (JsonHelper.hasString(json, "srcalpha")) {
            l = GlBlendState.getFactorFromString(json.get("srcalpha").getAsString());
            if (l != 1) {
                bl = false;
            }
            bl2 = true;
        }
        if (JsonHelper.hasString(json, "dstalpha")) {
            m = GlBlendState.getFactorFromString(json.get("dstalpha").getAsString());
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
        for (GlUniform glUniform : this.uniforms) {
            glUniform.close();
        }
        GlProgramManager.deleteProgram(this);
    }

    public void unbind() {
        RenderSystem.assertOnRenderThread();
        GlProgramManager.useProgram(0);
        activeProgramGlRef = -1;
        activeProgram = null;
        int i = GlStateManager._getActiveTexture();
        for (int j = 0; j < this.loadedSamplerIds.size(); ++j) {
            if (this.samplers.get(this.samplerNames.get(j)) == null) continue;
            GlStateManager._activeTexture(GlConst.GL_TEXTURE0 + j);
            GlStateManager._bindTexture(0);
        }
        GlStateManager._activeTexture(i);
    }

    public void bind() {
        RenderSystem.assertOnRenderThread();
        this.dirty = false;
        activeProgram = this;
        this.blendState.enable();
        if (this.glRef != activeProgramGlRef) {
            GlProgramManager.useProgram(this.glRef);
            activeProgramGlRef = this.glRef;
        }
        int i = GlStateManager._getActiveTexture();
        for (int j = 0; j < this.loadedSamplerIds.size(); ++j) {
            String string = this.samplerNames.get(j);
            if (this.samplers.get(string) == null) continue;
            int k = GlUniform.getUniformLocation(this.glRef, string);
            GlUniform.uniform1(k, j);
            RenderSystem.activeTexture(GlConst.GL_TEXTURE0 + j);
            Object object = this.samplers.get(string);
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
        GlStateManager._activeTexture(i);
        for (GlUniform glUniform : this.uniforms) {
            glUniform.upload();
        }
    }

    @Override
    public void markUniformsDirty() {
        this.dirty = true;
    }

    @Nullable
    public GlUniform getUniform(String name) {
        RenderSystem.assertOnRenderThread();
        return this.loadedUniforms.get(name);
    }

    public Uniform getUniformOrDefault(String name) {
        RenderSystem.assertOnGameThread();
        GlUniform glUniform = this.getUniform(name);
        return glUniform == null ? DEFAULT_UNIFORM : glUniform;
    }

    private void loadReferences() {
        int i;
        RenderSystem.assertOnRenderThread();
        IntArrayList intList = new IntArrayList();
        for (i = 0; i < this.samplerNames.size(); ++i) {
            String string = this.samplerNames.get(i);
            int j = GlUniform.getUniformLocation(this.glRef, string);
            if (j == -1) {
                LOGGER.warn("Shader {} could not find sampler named {} in the specified shader program.", (Object)this.name, (Object)string);
                this.samplers.remove(string);
                intList.add(i);
                continue;
            }
            this.loadedSamplerIds.add(j);
        }
        for (i = intList.size() - 1; i >= 0; --i) {
            int k = intList.getInt(i);
            this.samplerNames.remove(k);
        }
        for (GlUniform glUniform : this.uniforms) {
            String string2 = glUniform.getName();
            int l = GlUniform.getUniformLocation(this.glRef, string2);
            if (l == -1) {
                LOGGER.warn("Shader {} could not find uniform named {} in the specified shader program.", (Object)this.name, (Object)string2);
                continue;
            }
            this.loadedUniformIds.add(l);
            glUniform.setLocation(l);
            this.loadedUniforms.put(string2, glUniform);
        }
    }

    private void readSampler(JsonElement json) {
        JsonObject jsonObject = JsonHelper.asObject(json, "sampler");
        String string = JsonHelper.getString(jsonObject, "name");
        if (!JsonHelper.hasString(jsonObject, "file")) {
            this.samplers.put(string, null);
            this.samplerNames.add(string);
            return;
        }
        this.samplerNames.add(string);
    }

    public void addSampler(String name, Object sampler) {
        this.samplers.put(name, sampler);
        this.markUniformsDirty();
    }

    private void addUniform(JsonElement json) throws InvalidHierarchicalFileException {
        JsonObject jsonObject = JsonHelper.asObject(json, "uniform");
        String string = JsonHelper.getString(jsonObject, "name");
        int i = GlUniform.getTypeIndex(JsonHelper.getString(jsonObject, "type"));
        int j = JsonHelper.getInt(jsonObject, "count");
        float[] fs = new float[Math.max(j, 16)];
        JsonArray jsonArray = JsonHelper.getArray(jsonObject, "values");
        if (jsonArray.size() != j && jsonArray.size() > 1) {
            throw new InvalidHierarchicalFileException("Invalid amount of values specified (expected " + j + ", found " + jsonArray.size() + ")");
        }
        int k = 0;
        for (JsonElement jsonElement : jsonArray) {
            try {
                fs[k] = JsonHelper.asFloat(jsonElement, "value");
            } catch (Exception exception) {
                InvalidHierarchicalFileException invalidHierarchicalFileException = InvalidHierarchicalFileException.wrap(exception);
                invalidHierarchicalFileException.addInvalidKey("values[" + k + "]");
                throw invalidHierarchicalFileException;
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
            glUniform.set(Arrays.copyOfRange(fs, 0, j));
        }
        this.uniforms.add(glUniform);
    }

    @Override
    public ShaderStage getVertexShader() {
        return this.vertexShader;
    }

    @Override
    public ShaderStage getFragmentShader() {
        return this.fragmentShader;
    }

    @Override
    public void attachReferencedShaders() {
        this.fragmentShader.attachTo(this);
        this.vertexShader.attachTo(this);
    }

    public VertexFormat getFormat() {
        return this.format;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public int getGlRef() {
        return this.glRef;
    }

    static {
        activeProgramGlRef = -1;
    }
}

