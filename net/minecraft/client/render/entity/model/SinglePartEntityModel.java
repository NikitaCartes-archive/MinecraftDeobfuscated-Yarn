/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public abstract class SinglePartEntityModel<E extends Entity>
extends EntityModel<E> {
    private static final Vec3f field_39195 = new Vec3f();

    public SinglePartEntityModel() {
        this(RenderLayer::getEntityCutoutNoCull);
    }

    public SinglePartEntityModel(Function<Identifier, RenderLayer> function) {
        super(function);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.getPart().render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    public abstract ModelPart getPart();

    public Optional<ModelPart> getChild(String name) {
        return this.getPart().traverse().filter(part -> part.hasChild(name)).findFirst().map(part -> part.getChild(name));
    }

    protected void updateAnimation(AnimationState animationState, Animation animation) {
        this.updateAnimation(animationState, animation, 1.0f);
    }

    protected void updateAnimation(AnimationState animationState2, Animation animation, float f) {
        animationState2.update(MinecraftClient.getInstance().isPaused(), f);
        animationState2.run(animationState -> AnimationHelper.animate(this, animation, animationState.getTimeRunning(), 1.0f, field_39195));
    }
}

