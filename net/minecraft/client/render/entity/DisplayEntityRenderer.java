/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AffineTransformation;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@Environment(value=EnvType.CLIENT)
public abstract class DisplayEntityRenderer<T extends DisplayEntity>
extends EntityRenderer<T> {
    private static final float field_42524 = 64.0f;
    private final EntityRenderDispatcher renderDispatcher;

    protected DisplayEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.renderDispatcher = context.getRenderDispatcher();
    }

    @Override
    public Identifier getTexture(T displayEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }

    @Override
    public void render(T displayEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        float h = ((DisplayEntity)displayEntity).getLerpProgress(g);
        this.shadowRadius = Math.min(((DisplayEntity)displayEntity).lerpShadowRadius(h), 64.0f);
        this.shadowOpacity = ((DisplayEntity)displayEntity).lerpShadowStrength(h);
        int j = ((DisplayEntity)displayEntity).getBrightness();
        int k = j != -1 ? j : i;
        super.render(displayEntity, f, g, matrixStack, vertexConsumerProvider, k);
        matrixStack.push();
        matrixStack.multiply(this.getBillboardRotation(displayEntity));
        AffineTransformation affineTransformation = ((DisplayEntity)displayEntity).lerpTransformation(h);
        matrixStack.multiplyPositionMatrix(affineTransformation.getMatrix());
        matrixStack.peek().getNormalMatrix().rotate(affineTransformation.getLeftRotation()).rotate(affineTransformation.getRightRotation());
        this.render(displayEntity, matrixStack, vertexConsumerProvider, k, h);
        matrixStack.pop();
    }

    private Quaternionf getBillboardRotation(T display) {
        Camera camera = this.renderDispatcher.camera;
        return switch (((DisplayEntity)display).getBillboardMode()) {
            default -> throw new IncompatibleClassChangeError();
            case DisplayEntity.BillboardMode.FIXED -> ((DisplayEntity)display).getFixedRotation();
            case DisplayEntity.BillboardMode.HORIZONTAL -> new Quaternionf().rotationYXZ((float)(-Math.PI) / 180 * ((Entity)display).getYaw(), (float)(-Math.PI) / 180 * camera.getPitch(), 0.0f);
            case DisplayEntity.BillboardMode.VERTICAL -> new Quaternionf().rotationYXZ((float)Math.PI - (float)Math.PI / 180 * camera.getYaw(), (float)Math.PI / 180 * ((Entity)display).getPitch(), 0.0f);
            case DisplayEntity.BillboardMode.CENTER -> new Quaternionf().rotationYXZ((float)Math.PI - (float)Math.PI / 180 * camera.getYaw(), (float)(-Math.PI) / 180 * camera.getPitch(), 0.0f);
        };
    }

    protected abstract void render(T var1, MatrixStack var2, VertexConsumerProvider var3, int var4, float var5);

    @Environment(value=EnvType.CLIENT)
    public static class TextDisplayEntityRenderer
    extends DisplayEntityRenderer<DisplayEntity.TextDisplayEntity> {
        private final TextRenderer displayTextRenderer;

        protected TextDisplayEntityRenderer(EntityRendererFactory.Context context) {
            super(context);
            this.displayTextRenderer = context.getTextRenderer();
        }

        private DisplayEntity.TextDisplayEntity.TextLines getLines(Text text, int width) {
            List<OrderedText> list = this.displayTextRenderer.wrapLines(text, width);
            ArrayList<DisplayEntity.TextDisplayEntity.TextLine> list2 = new ArrayList<DisplayEntity.TextDisplayEntity.TextLine>(list.size());
            int i = 0;
            for (OrderedText orderedText : list) {
                int j = this.displayTextRenderer.getWidth(orderedText);
                i = Math.max(i, j);
                list2.add(new DisplayEntity.TextDisplayEntity.TextLine(orderedText, j));
            }
            return new DisplayEntity.TextDisplayEntity.TextLines(list2, i);
        }

        @Override
        public void render(DisplayEntity.TextDisplayEntity textDisplayEntity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f) {
            int j;
            float g;
            byte b = textDisplayEntity.getDisplayFlags();
            boolean bl = (b & 2) != 0;
            boolean bl2 = (b & 4) != 0;
            boolean bl3 = (b & 1) != 0;
            DisplayEntity.TextDisplayEntity.TextAlignment textAlignment = DisplayEntity.TextDisplayEntity.getAlignment(b);
            byte c = textDisplayEntity.lerpTextOpacity(f);
            if (bl2) {
                g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f);
                j = (int)(g * 255.0f) << 24;
            } else {
                j = textDisplayEntity.lerpBackground(f);
            }
            g = 0.0f;
            Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
            matrix4f.rotate((float)Math.PI, 0.0f, 1.0f, 0.0f);
            matrix4f.scale(-0.025f, -0.025f, -0.025f);
            DisplayEntity.TextDisplayEntity.TextLines textLines = textDisplayEntity.splitLines(this::getLines);
            int k = this.displayTextRenderer.fontHeight + 1;
            int l = textLines.width();
            int m = textLines.lines().size() * k;
            matrix4f.translate(1.0f - (float)l / 2.0f, -m, 0.0f);
            if (j != 0) {
                VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(bl ? RenderLayer.getTextBackgroundSeeThrough() : RenderLayer.getTextBackground());
                vertexConsumer.vertex(matrix4f, -1.0f, -1.0f, 0.0f).color(j).light(i).next();
                vertexConsumer.vertex(matrix4f, -1.0f, m, 0.0f).color(j).light(i).next();
                vertexConsumer.vertex(matrix4f, l, m, 0.0f).color(j).light(i).next();
                vertexConsumer.vertex(matrix4f, l, -1.0f, 0.0f).color(j).light(i).next();
            }
            for (DisplayEntity.TextDisplayEntity.TextLine textLine : textLines.lines()) {
                float h = switch (textAlignment) {
                    default -> throw new IncompatibleClassChangeError();
                    case DisplayEntity.TextDisplayEntity.TextAlignment.LEFT -> 0.0f;
                    case DisplayEntity.TextDisplayEntity.TextAlignment.RIGHT -> l - textLine.width();
                    case DisplayEntity.TextDisplayEntity.TextAlignment.CENTER -> (float)l / 2.0f - (float)textLine.width() / 2.0f;
                };
                this.displayTextRenderer.draw(textLine.contents(), h, g, c << 24 | 0xFFFFFF, bl3, matrix4f, vertexConsumerProvider, bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.POLYGON_OFFSET, 0, i);
                g += (float)k;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class ItemDisplayEntityRenderer
    extends DisplayEntityRenderer<DisplayEntity.ItemDisplayEntity> {
        private final ItemRenderer itemRenderer;

        protected ItemDisplayEntityRenderer(EntityRendererFactory.Context context) {
            super(context);
            this.itemRenderer = context.getItemRenderer();
        }

        @Override
        public void render(DisplayEntity.ItemDisplayEntity itemDisplayEntity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f) {
            this.itemRenderer.renderItem(itemDisplayEntity.getItemStack(), itemDisplayEntity.getTransformationMode(), i, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, itemDisplayEntity.getWorld(), itemDisplayEntity.getId());
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class BlockDisplayEntityRenderer
    extends DisplayEntityRenderer<DisplayEntity.BlockDisplayEntity> {
        private final BlockRenderManager blockRenderManager;

        protected BlockDisplayEntityRenderer(EntityRendererFactory.Context context) {
            super(context);
            this.blockRenderManager = context.getBlockRenderManager();
        }

        @Override
        public void render(DisplayEntity.BlockDisplayEntity blockDisplayEntity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f) {
            this.blockRenderManager.renderBlockAsEntity(blockDisplayEntity.getBlockState(), matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV);
        }
    }
}

