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
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.PostProcessShader;
import net.minecraft.client.gl.ShaderParseException;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Matrix4f;
import org.apache.commons.io.IOUtils;

@Environment(value=EnvType.CLIENT)
public class ShaderEffect
implements AutoCloseable {
    private static final String MAIN_TARGET_NAME = "minecraft:main";
    private final Framebuffer mainTarget;
    private final ResourceManager resourceManager;
    private final String name;
    private final List<PostProcessShader> passes = Lists.newArrayList();
    private final Map<String, Framebuffer> targetsByName = Maps.newHashMap();
    private final List<Framebuffer> defaultSizedTargets = Lists.newArrayList();
    private Matrix4f projectionMatrix;
    private int width;
    private int height;
    private float time;
    private float lastTickDelta;

    public ShaderEffect(TextureManager textureManager, ResourceManager resourceManager, Framebuffer framebuffer, Identifier location) throws IOException, JsonSyntaxException {
        this.resourceManager = resourceManager;
        this.mainTarget = framebuffer;
        this.time = 0.0f;
        this.lastTickDelta = 0.0f;
        this.width = framebuffer.viewportWidth;
        this.height = framebuffer.viewportHeight;
        this.name = location.toString();
        this.setupProjectionMatrix();
        this.parseEffect(textureManager, location);
    }

    private void parseEffect(TextureManager textureManager, Identifier location) throws IOException, JsonSyntaxException {
        Resource resource;
        block11: {
            resource = null;
            try {
                int i;
                JsonArray jsonArray;
                resource = this.resourceManager.getResource(location);
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
                    Object string = resource != null ? " (" + resource.getResourcePackName() + ")" : "";
                    ShaderParseException shaderParseException2 = ShaderParseException.wrap(exception2);
                    shaderParseException2.addFaultyFile(location.getPath() + (String)string);
                    throw shaderParseException2;
                } catch (Throwable throwable) {
                    IOUtils.closeQuietly(resource);
                    throw throwable;
                }
            }
        }
        IOUtils.closeQuietly((Closeable)resource);
    }

    private void parseTarget(JsonElement jsonTarget) throws ShaderParseException {
        if (JsonHelper.isString(jsonTarget)) {
            this.addTarget(jsonTarget.getAsString(), this.width, this.height);
        } else {
            JsonObject jsonObject = JsonHelper.asObject(jsonTarget, "target");
            String string = JsonHelper.getString(jsonObject, "name");
            int i = JsonHelper.getInt(jsonObject, "width", this.width);
            int j = JsonHelper.getInt(jsonObject, "height", this.height);
            if (this.targetsByName.containsKey(string)) {
                throw new ShaderParseException(string + " is already defined");
            }
            this.addTarget(string, i, j);
        }
    }

    private void parsePass(TextureManager textureManager, JsonElement jsonPass) throws IOException {
        JsonArray jsonArray2;
        JsonObject jsonObject;
        block21: {
            jsonObject = JsonHelper.asObject(jsonPass, "pass");
            String string = JsonHelper.getString(jsonObject, "name");
            String string2 = JsonHelper.getString(jsonObject, "intarget");
            String string3 = JsonHelper.getString(jsonObject, "outtarget");
            Framebuffer framebuffer = this.getTarget(string2);
            Framebuffer framebuffer2 = this.getTarget(string3);
            if (framebuffer == null) {
                throw new ShaderParseException("Input target '" + string2 + "' does not exist");
            }
            if (framebuffer2 == null) {
                throw new ShaderParseException("Output target '" + string3 + "' does not exist");
            }
            PostProcessShader postProcessShader = this.addPass(string, framebuffer, framebuffer2);
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, "auxtargets", null);
            if (jsonArray == null) break block21;
            int i = 0;
            for (JsonElement jsonElement : jsonArray) {
                block20: {
                    try {
                        Framebuffer framebuffer3;
                        boolean bl;
                        String string4;
                        block22: {
                            String string6;
                            JsonObject jsonObject2 = JsonHelper.asObject(jsonElement, "auxtarget");
                            string4 = JsonHelper.getString(jsonObject2, "name");
                            String string5 = JsonHelper.getString(jsonObject2, "id");
                            if (string5.endsWith(":depth")) {
                                bl = true;
                                string6 = string5.substring(0, string5.lastIndexOf(58));
                            } else {
                                bl = false;
                                string6 = string5;
                            }
                            framebuffer3 = this.getTarget(string6);
                            if (framebuffer3 != null) break block22;
                            if (bl) {
                                throw new ShaderParseException("Render target '" + string6 + "' can't be used as depth buffer");
                            }
                            Identifier identifier = new Identifier("textures/effect/" + string6 + ".png");
                            Resource resource = null;
                            try {
                                resource = this.resourceManager.getResource(identifier);
                            } catch (FileNotFoundException fileNotFoundException) {
                                try {
                                    throw new ShaderParseException("Render target or texture '" + string6 + "' does not exist");
                                } catch (Throwable throwable) {
                                    IOUtils.closeQuietly(resource);
                                    throw throwable;
                                }
                            }
                            IOUtils.closeQuietly((Closeable)resource);
                            RenderSystem.setShaderTexture(0, identifier);
                            textureManager.bindTexture(identifier);
                            AbstractTexture abstractTexture = textureManager.getTexture(identifier);
                            int j = JsonHelper.getInt(jsonObject2, "width");
                            int k = JsonHelper.getInt(jsonObject2, "height");
                            boolean bl2 = JsonHelper.getBoolean(jsonObject2, "bilinear");
                            if (bl2) {
                                RenderSystem.texParameter(3553, 10241, 9729);
                                RenderSystem.texParameter(3553, 10240, 9729);
                            } else {
                                RenderSystem.texParameter(3553, 10241, 9728);
                                RenderSystem.texParameter(3553, 10240, 9728);
                            }
                            postProcessShader.addAuxTarget(string4, abstractTexture::getGlId, j, k);
                            break block20;
                        }
                        if (bl) {
                            postProcessShader.addAuxTarget(string4, framebuffer3::getDepthAttachment, framebuffer3.textureWidth, framebuffer3.textureHeight);
                        } else {
                            postProcessShader.addAuxTarget(string4, framebuffer3::getColorAttachment, framebuffer3.textureWidth, framebuffer3.textureHeight);
                        }
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
            for (JsonElement jsonElement2 : jsonArray2) {
                try {
                    this.parseUniform(jsonElement2);
                } catch (Exception exception2) {
                    ShaderParseException shaderParseException2 = ShaderParseException.wrap(exception2);
                    shaderParseException2.addFaultyElement("uniforms[" + l + "]");
                    throw shaderParseException2;
                }
                ++l;
            }
        }
    }

    private void parseUniform(JsonElement jsonUniform) throws ShaderParseException {
        JsonObject jsonObject = JsonHelper.asObject(jsonUniform, "uniform");
        String string = JsonHelper.getString(jsonObject, "name");
        GlUniform glUniform = this.passes.get(this.passes.size() - 1).getProgram().getUniformByName(string);
        if (glUniform == null) {
            throw new ShaderParseException("Uniform '" + string + "' does not exist");
        }
        float[] fs = new float[4];
        int i = 0;
        JsonArray jsonArray = JsonHelper.getArray(jsonObject, "values");
        for (JsonElement jsonElement : jsonArray) {
            try {
                fs[i] = JsonHelper.asFloat(jsonElement, "value");
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

    public Framebuffer getSecondaryTarget(String name) {
        return this.targetsByName.get(name);
    }

    public void addTarget(String name, int width, int height) {
        SimpleFramebuffer framebuffer = new SimpleFramebuffer(width, height, true, MinecraftClient.IS_SYSTEM_MAC);
        framebuffer.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.targetsByName.put(name, framebuffer);
        if (width == this.width && height == this.height) {
            this.defaultSizedTargets.add(framebuffer);
        }
    }

    @Override
    public void close() {
        for (Framebuffer framebuffer : this.targetsByName.values()) {
            framebuffer.delete();
        }
        for (PostProcessShader postProcessShader : this.passes) {
            postProcessShader.close();
        }
        this.passes.clear();
    }

    public PostProcessShader addPass(String programName, Framebuffer source, Framebuffer dest) throws IOException {
        PostProcessShader postProcessShader = new PostProcessShader(this.resourceManager, programName, source, dest);
        this.passes.add(this.passes.size(), postProcessShader);
        return postProcessShader;
    }

    private void setupProjectionMatrix() {
        this.projectionMatrix = Matrix4f.projectionMatrix(0.0f, this.mainTarget.textureWidth, this.mainTarget.textureHeight, 0.0f, 0.1f, 1000.0f);
    }

    public void setupDimensions(int targetsWidth, int targetsHeight) {
        this.width = this.mainTarget.textureWidth;
        this.height = this.mainTarget.textureHeight;
        this.setupProjectionMatrix();
        for (PostProcessShader postProcessShader : this.passes) {
            postProcessShader.setProjectionMatrix(this.projectionMatrix);
        }
        for (Framebuffer framebuffer : this.defaultSizedTargets) {
            framebuffer.resize(targetsWidth, targetsHeight, MinecraftClient.IS_SYSTEM_MAC);
        }
    }

    public void render(float tickDelta) {
        if (tickDelta < this.lastTickDelta) {
            this.time += 1.0f - this.lastTickDelta;
            this.time += tickDelta;
        } else {
            this.time += tickDelta - this.lastTickDelta;
        }
        this.lastTickDelta = tickDelta;
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

    private Framebuffer getTarget(String name) {
        if (name == null) {
            return null;
        }
        if (name.equals(MAIN_TARGET_NAME)) {
            return this.mainTarget;
        }
        return this.targetsByName.get(name);
    }
}

