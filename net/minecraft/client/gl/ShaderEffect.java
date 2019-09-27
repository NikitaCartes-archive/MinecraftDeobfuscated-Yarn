/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlFramebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.PostProcessShader;
import net.minecraft.client.gl.ShaderParseException;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;

@Environment(value=EnvType.CLIENT)
public class ShaderEffect
implements AutoCloseable {
    private final GlFramebuffer mainTarget;
    private final ResourceManager resourceManager;
    private final String name;
    private final List<PostProcessShader> passes = Lists.newArrayList();
    private final Map<String, GlFramebuffer> targetsByName = Maps.newHashMap();
    private final List<GlFramebuffer> defaultSizedTargets = Lists.newArrayList();
    private Matrix4f projectionMatrix;
    private int width;
    private int height;
    private float time;
    private float lastTickDelta;

    public ShaderEffect(TextureManager textureManager, ResourceManager resourceManager, GlFramebuffer glFramebuffer, Identifier identifier) throws IOException, JsonSyntaxException {
        this.resourceManager = resourceManager;
        this.mainTarget = glFramebuffer;
        this.time = 0.0f;
        this.lastTickDelta = 0.0f;
        this.width = glFramebuffer.viewWidth;
        this.height = glFramebuffer.viewHeight;
        this.name = identifier.toString();
        this.setupProjectionMatrix();
        this.parseEffect(textureManager, identifier);
    }

    private void parseEffect(TextureManager textureManager, Identifier identifier) throws IOException, JsonSyntaxException {
        Resource resource;
        block11: {
            resource = null;
            try {
                int i;
                JsonArray jsonArray;
                resource = this.resourceManager.getResource(identifier);
                JsonObject jsonObject = JsonHelper.deserialize(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
                if (JsonHelper.hasArray(jsonObject, "targets")) {
                    jsonArray = jsonObject.getAsJsonArray("targets");
                    i = 0;
                    for (JsonElement jsonElement : jsonArray) {
                        try {
                            this.parseTarget(jsonElement);
                        } catch (Exception exception) {
                            ShaderParseException shaderParseException = ShaderParseException.wrap(exception);
                            shaderParseException.addFaultyElement("targets[" + i + "]");
                            throw shaderParseException;
                        }
                        ++i;
                    }
                }
                if (!JsonHelper.hasArray(jsonObject, "passes")) break block11;
                jsonArray = jsonObject.getAsJsonArray("passes");
                i = 0;
                for (JsonElement jsonElement : jsonArray) {
                    try {
                        this.parsePass(textureManager, jsonElement);
                    } catch (Exception exception) {
                        ShaderParseException shaderParseException = ShaderParseException.wrap(exception);
                        shaderParseException.addFaultyElement("passes[" + i + "]");
                        throw shaderParseException;
                    }
                    ++i;
                }
            } catch (Exception exception2) {
                try {
                    ShaderParseException shaderParseException2 = ShaderParseException.wrap(exception2);
                    shaderParseException2.addFaultyFile(identifier.getPath());
                    throw shaderParseException2;
                } catch (Throwable throwable) {
                    IOUtils.closeQuietly(resource);
                    throw throwable;
                }
            }
        }
        IOUtils.closeQuietly((Closeable)resource);
    }

    private void parseTarget(JsonElement jsonElement) throws ShaderParseException {
        if (JsonHelper.isString(jsonElement)) {
            this.addTarget(jsonElement.getAsString(), this.width, this.height);
        } else {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "target");
            String string = JsonHelper.getString(jsonObject, "name");
            int i = JsonHelper.getInt(jsonObject, "width", this.width);
            int j = JsonHelper.getInt(jsonObject, "height", this.height);
            if (this.targetsByName.containsKey(string)) {
                throw new ShaderParseException(string + " is already defined");
            }
            this.addTarget(string, i, j);
        }
    }

    private void parsePass(TextureManager textureManager, JsonElement jsonElement) throws IOException {
        JsonArray jsonArray2;
        JsonObject jsonObject;
        block16: {
            jsonObject = JsonHelper.asObject(jsonElement, "pass");
            String string = JsonHelper.getString(jsonObject, "name");
            String string2 = JsonHelper.getString(jsonObject, "intarget");
            String string3 = JsonHelper.getString(jsonObject, "outtarget");
            GlFramebuffer glFramebuffer = this.getTarget(string2);
            GlFramebuffer glFramebuffer2 = this.getTarget(string3);
            if (glFramebuffer == null) {
                throw new ShaderParseException("Input target '" + string2 + "' does not exist");
            }
            if (glFramebuffer2 == null) {
                throw new ShaderParseException("Output target '" + string3 + "' does not exist");
            }
            PostProcessShader postProcessShader = this.addPass(string, glFramebuffer, glFramebuffer2);
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, "auxtargets", null);
            if (jsonArray == null) break block16;
            int i = 0;
            for (JsonElement jsonElement2 : jsonArray) {
                block15: {
                    try {
                        GlFramebuffer glFramebuffer3;
                        String string4;
                        block17: {
                            JsonObject jsonObject2 = JsonHelper.asObject(jsonElement2, "auxtarget");
                            string4 = JsonHelper.getString(jsonObject2, "name");
                            String string5 = JsonHelper.getString(jsonObject2, "id");
                            glFramebuffer3 = this.getTarget(string5);
                            if (glFramebuffer3 != null) break block17;
                            Identifier identifier = new Identifier("textures/effect/" + string5 + ".png");
                            Resource resource = null;
                            try {
                                resource = this.resourceManager.getResource(identifier);
                            } catch (FileNotFoundException fileNotFoundException) {
                                try {
                                    throw new ShaderParseException("Render target or texture '" + string5 + "' does not exist");
                                } catch (Throwable throwable) {
                                    IOUtils.closeQuietly(resource);
                                    throw throwable;
                                }
                            }
                            IOUtils.closeQuietly((Closeable)resource);
                            textureManager.bindTexture(identifier);
                            AbstractTexture abstractTexture = textureManager.getTexture(identifier);
                            int j = JsonHelper.getInt(jsonObject2, "width");
                            int k = JsonHelper.getInt(jsonObject2, "height");
                            boolean bl = JsonHelper.getBoolean(jsonObject2, "bilinear");
                            if (bl) {
                                RenderSystem.texParameter(3553, 10241, 9729);
                                RenderSystem.texParameter(3553, 10240, 9729);
                            } else {
                                RenderSystem.texParameter(3553, 10241, 9728);
                                RenderSystem.texParameter(3553, 10240, 9728);
                            }
                            postProcessShader.addAuxTarget(string4, abstractTexture.getGlId(), j, k);
                            break block15;
                        }
                        postProcessShader.addAuxTarget(string4, glFramebuffer3, glFramebuffer3.texWidth, glFramebuffer3.texHeight);
                    } catch (Exception exception) {
                        ShaderParseException shaderParseException = ShaderParseException.wrap(exception);
                        shaderParseException.addFaultyElement("auxtargets[" + i + "]");
                        throw shaderParseException;
                    }
                }
                ++i;
            }
        }
        if ((jsonArray2 = JsonHelper.getArray(jsonObject, "uniforms", null)) != null) {
            int l = 0;
            for (JsonElement jsonElement3 : jsonArray2) {
                try {
                    this.parseUniform(jsonElement3);
                } catch (Exception exception2) {
                    ShaderParseException shaderParseException2 = ShaderParseException.wrap(exception2);
                    shaderParseException2.addFaultyElement("uniforms[" + l + "]");
                    throw shaderParseException2;
                }
                ++l;
            }
        }
    }

    private void parseUniform(JsonElement jsonElement) throws ShaderParseException {
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "uniform");
        String string = JsonHelper.getString(jsonObject, "name");
        GlUniform glUniform = this.passes.get(this.passes.size() - 1).getProgram().getUniformByName(string);
        if (glUniform == null) {
            throw new ShaderParseException("Uniform '" + string + "' does not exist");
        }
        float[] fs = new float[4];
        int i = 0;
        JsonArray jsonArray = JsonHelper.getArray(jsonObject, "values");
        for (JsonElement jsonElement2 : jsonArray) {
            try {
                fs[i] = JsonHelper.asFloat(jsonElement2, "value");
            } catch (Exception exception) {
                ShaderParseException shaderParseException = ShaderParseException.wrap(exception);
                shaderParseException.addFaultyElement("values[" + i + "]");
                throw shaderParseException;
            }
            ++i;
        }
        switch (i) {
            case 0: {
                break;
            }
            case 1: {
                glUniform.set(fs[0]);
                break;
            }
            case 2: {
                glUniform.set(fs[0], fs[1]);
                break;
            }
            case 3: {
                glUniform.set(fs[0], fs[1], fs[2]);
                break;
            }
            case 4: {
                glUniform.set(fs[0], fs[1], fs[2], fs[3]);
            }
        }
    }

    public GlFramebuffer getSecondaryTarget(String string) {
        return this.targetsByName.get(string);
    }

    public void addTarget(String string, int i, int j) {
        GlFramebuffer glFramebuffer = new GlFramebuffer(i, j, true, MinecraftClient.IS_SYSTEM_MAC);
        glFramebuffer.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.targetsByName.put(string, glFramebuffer);
        if (i == this.width && j == this.height) {
            this.defaultSizedTargets.add(glFramebuffer);
        }
    }

    @Override
    public void close() {
        for (GlFramebuffer glFramebuffer : this.targetsByName.values()) {
            glFramebuffer.delete();
        }
        for (PostProcessShader postProcessShader : this.passes) {
            postProcessShader.close();
        }
        this.passes.clear();
    }

    public PostProcessShader addPass(String string, GlFramebuffer glFramebuffer, GlFramebuffer glFramebuffer2) throws IOException {
        PostProcessShader postProcessShader = new PostProcessShader(this.resourceManager, string, glFramebuffer, glFramebuffer2);
        this.passes.add(this.passes.size(), postProcessShader);
        return postProcessShader;
    }

    private void setupProjectionMatrix() {
        this.projectionMatrix = Matrix4f.projectionMatrix(this.mainTarget.texWidth, this.mainTarget.texHeight, 0.1f, 1000.0f);
    }

    public void setupDimensions(int i, int j) {
        this.width = this.mainTarget.texWidth;
        this.height = this.mainTarget.texHeight;
        this.setupProjectionMatrix();
        for (PostProcessShader postProcessShader : this.passes) {
            postProcessShader.setProjectionMatrix(this.projectionMatrix);
        }
        for (GlFramebuffer glFramebuffer : this.defaultSizedTargets) {
            glFramebuffer.resize(i, j, MinecraftClient.IS_SYSTEM_MAC);
        }
    }

    public void render(float f) {
        if (f < this.lastTickDelta) {
            this.time += 1.0f - this.lastTickDelta;
            this.time += f;
        } else {
            this.time += f - this.lastTickDelta;
        }
        this.lastTickDelta = f;
        while (this.time > 20.0f) {
            this.time -= 20.0f;
        }
        for (PostProcessShader postProcessShader : this.passes) {
            postProcessShader.render(this.time / 20.0f);
        }
    }

    public final String getName() {
        return this.name;
    }

    private GlFramebuffer getTarget(String string) {
        if (string == null) {
            return null;
        }
        if (string.equals("minecraft:main")) {
            return this.mainTarget;
        }
        return this.targetsByName.get(string);
    }
}

