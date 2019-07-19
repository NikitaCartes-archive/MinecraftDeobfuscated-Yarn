/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.PngFile;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class Sprite {
    private final Identifier id;
    protected final int width;
    protected final int height;
    protected NativeImage[] images;
    @Nullable
    protected int[] frameXs;
    @Nullable
    protected int[] frameYs;
    protected NativeImage[] interpolatedImages;
    private AnimationResourceMetadata animationMetadata;
    protected int x;
    protected int y;
    private float uMin;
    private float uMax;
    private float vMin;
    private float vMax;
    protected int frameIndex;
    protected int frameTicks;
    private static final float[] srgbLinearMap = Util.make(new float[256], fs -> {
        for (int i = 0; i < ((float[])fs).length; ++i) {
            fs[i] = (float)Math.pow((float)i / 255.0f, 2.2);
        }
    });

    protected Sprite(Identifier identifier, int i, int j) {
        this.id = identifier;
        this.width = i;
        this.height = j;
    }

    protected Sprite(Identifier identifier, PngFile pngFile, @Nullable AnimationResourceMetadata animationResourceMetadata) {
        this.id = identifier;
        if (animationResourceMetadata != null) {
            Pair<Integer, Integer> pair = Sprite.getDimensions(animationResourceMetadata.getWidth(), animationResourceMetadata.getHeight(), pngFile.width, pngFile.height);
            this.width = pair.getFirst();
            this.height = pair.getSecond();
            if (!Sprite.isDivisibleBy(pngFile.width, this.width) || !Sprite.isDivisibleBy(pngFile.height, this.height)) {
                throw new IllegalArgumentException(String.format("Image size %s,%s is not multiply of frame size %s,%s", this.width, this.height, pngFile.width, pngFile.height));
            }
        } else {
            this.width = pngFile.width;
            this.height = pngFile.height;
        }
        this.animationMetadata = animationResourceMetadata;
    }

    private static Pair<Integer, Integer> getDimensions(int i, int j, int k, int l) {
        if (i != -1) {
            if (j != -1) {
                return Pair.of(i, j);
            }
            return Pair.of(i, l);
        }
        if (j != -1) {
            return Pair.of(k, j);
        }
        int m = Math.min(k, l);
        return Pair.of(m, m);
    }

    private static boolean isDivisibleBy(int i, int j) {
        return i / j * j == i;
    }

    private void generateMipmapsInternal(int i) {
        NativeImage[] nativeImages = new NativeImage[i + 1];
        nativeImages[0] = this.images[0];
        if (i > 0) {
            int j;
            boolean bl = false;
            block0: for (j = 0; j < this.images[0].getWidth(); ++j) {
                for (int k = 0; k < this.images[0].getHeight(); ++k) {
                    if (this.images[0].getPixelRgba(j, k) >> 24 != 0) continue;
                    bl = true;
                    break block0;
                }
            }
            for (j = 1; j <= i; ++j) {
                if (this.images.length > j && this.images[j] != null) {
                    nativeImages[j] = this.images[j];
                    continue;
                }
                NativeImage nativeImage = nativeImages[j - 1];
                NativeImage nativeImage2 = new NativeImage(nativeImage.getWidth() >> 1, nativeImage.getHeight() >> 1, false);
                int l = nativeImage2.getWidth();
                int m = nativeImage2.getHeight();
                for (int n = 0; n < l; ++n) {
                    for (int o = 0; o < m; ++o) {
                        nativeImage2.setPixelRgba(n, o, Sprite.blendPixels(nativeImage.getPixelRgba(n * 2 + 0, o * 2 + 0), nativeImage.getPixelRgba(n * 2 + 1, o * 2 + 0), nativeImage.getPixelRgba(n * 2 + 0, o * 2 + 1), nativeImage.getPixelRgba(n * 2 + 1, o * 2 + 1), bl));
                    }
                }
                nativeImages[j] = nativeImage2;
            }
            for (j = i + 1; j < this.images.length; ++j) {
                if (this.images[j] == null) continue;
                this.images[j].close();
            }
        }
        this.images = nativeImages;
    }

    private static int blendPixels(int i, int j, int k, int l, boolean bl) {
        if (bl) {
            float f = 0.0f;
            float g = 0.0f;
            float h = 0.0f;
            float m = 0.0f;
            if (i >> 24 != 0) {
                f += Sprite.srgbToLinear(i >> 24);
                g += Sprite.srgbToLinear(i >> 16);
                h += Sprite.srgbToLinear(i >> 8);
                m += Sprite.srgbToLinear(i >> 0);
            }
            if (j >> 24 != 0) {
                f += Sprite.srgbToLinear(j >> 24);
                g += Sprite.srgbToLinear(j >> 16);
                h += Sprite.srgbToLinear(j >> 8);
                m += Sprite.srgbToLinear(j >> 0);
            }
            if (k >> 24 != 0) {
                f += Sprite.srgbToLinear(k >> 24);
                g += Sprite.srgbToLinear(k >> 16);
                h += Sprite.srgbToLinear(k >> 8);
                m += Sprite.srgbToLinear(k >> 0);
            }
            if (l >> 24 != 0) {
                f += Sprite.srgbToLinear(l >> 24);
                g += Sprite.srgbToLinear(l >> 16);
                h += Sprite.srgbToLinear(l >> 8);
                m += Sprite.srgbToLinear(l >> 0);
            }
            int n = (int)(Math.pow(f /= 4.0f, 0.45454545454545453) * 255.0);
            int o = (int)(Math.pow(g /= 4.0f, 0.45454545454545453) * 255.0);
            int p = (int)(Math.pow(h /= 4.0f, 0.45454545454545453) * 255.0);
            int q = (int)(Math.pow(m /= 4.0f, 0.45454545454545453) * 255.0);
            if (n < 96) {
                n = 0;
            }
            return n << 24 | o << 16 | p << 8 | q;
        }
        int r = Sprite.blendPixelsComponent(i, j, k, l, 24);
        int s = Sprite.blendPixelsComponent(i, j, k, l, 16);
        int t = Sprite.blendPixelsComponent(i, j, k, l, 8);
        int u = Sprite.blendPixelsComponent(i, j, k, l, 0);
        return r << 24 | s << 16 | t << 8 | u;
    }

    private static int blendPixelsComponent(int i, int j, int k, int l, int m) {
        float f = Sprite.srgbToLinear(i >> m);
        float g = Sprite.srgbToLinear(j >> m);
        float h = Sprite.srgbToLinear(k >> m);
        float n = Sprite.srgbToLinear(l >> m);
        float o = (float)((double)((float)Math.pow((double)(f + g + h + n) * 0.25, 0.45454545454545453)));
        return (int)((double)o * 255.0);
    }

    private static float srgbToLinear(int i) {
        return srgbLinearMap[i & 0xFF];
    }

    private void upload(int i) {
        int j = 0;
        int k = 0;
        if (this.frameXs != null) {
            j = this.frameXs[i] * this.width;
            k = this.frameYs[i] * this.height;
        }
        this.upload(j, k, this.images);
    }

    private void upload(int i, int j, NativeImage[] nativeImages) {
        for (int k = 0; k < this.images.length; ++k) {
            nativeImages[k].upload(k, this.x >> k, this.y >> k, i >> k, j >> k, this.width >> k, this.height >> k, this.images.length > 1);
        }
    }

    public void init(int i, int j, int k, int l) {
        this.x = k;
        this.y = l;
        this.uMin = (float)k / (float)i;
        this.uMax = (float)(k + this.width) / (float)i;
        this.vMin = (float)l / (float)j;
        this.vMax = (float)(l + this.height) / (float)j;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public float getMinU() {
        return this.uMin;
    }

    public float getMaxU() {
        return this.uMax;
    }

    public float getFrameU(double d) {
        float f = this.uMax - this.uMin;
        return this.uMin + f * (float)d / 16.0f;
    }

    public float getXFromU(float f) {
        float g = this.uMax - this.uMin;
        return (f - this.uMin) / g * 16.0f;
    }

    public float getMinV() {
        return this.vMin;
    }

    public float getMaxV() {
        return this.vMax;
    }

    public float getFrameV(double d) {
        float f = this.vMax - this.vMin;
        return this.vMin + f * (float)d / 16.0f;
    }

    public float getYFromV(float f) {
        float g = this.vMax - this.vMin;
        return (f - this.vMin) / g * 16.0f;
    }

    public Identifier getId() {
        return this.id;
    }

    public void tickAnimation() {
        ++this.frameTicks;
        if (this.frameTicks >= this.animationMetadata.getFrameTime(this.frameIndex)) {
            int i = this.animationMetadata.getFrameIndex(this.frameIndex);
            int j = this.animationMetadata.getFrameCount() == 0 ? this.getFrameCount() : this.animationMetadata.getFrameCount();
            this.frameIndex = (this.frameIndex + 1) % j;
            this.frameTicks = 0;
            int k = this.animationMetadata.getFrameIndex(this.frameIndex);
            if (i != k && k >= 0 && k < this.getFrameCount()) {
                this.upload(k);
            }
        } else if (this.animationMetadata.shouldInterpolate()) {
            this.interpolateFrames();
        }
    }

    private void interpolateFrames() {
        int j;
        int k;
        double d = 1.0 - (double)this.frameTicks / (double)this.animationMetadata.getFrameTime(this.frameIndex);
        int i = this.animationMetadata.getFrameIndex(this.frameIndex);
        if (i != (k = this.animationMetadata.getFrameIndex((this.frameIndex + 1) % (j = this.animationMetadata.getFrameCount() == 0 ? this.getFrameCount() : this.animationMetadata.getFrameCount()))) && k >= 0 && k < this.getFrameCount()) {
            if (this.interpolatedImages == null || this.interpolatedImages.length != this.images.length) {
                if (this.interpolatedImages != null) {
                    for (NativeImage nativeImage : this.interpolatedImages) {
                        if (nativeImage == null) continue;
                        nativeImage.close();
                    }
                }
                this.interpolatedImages = new NativeImage[this.images.length];
            }
            for (int l = 0; l < this.images.length; ++l) {
                int m = this.width >> l;
                int n = this.height >> l;
                if (this.interpolatedImages[l] == null) {
                    this.interpolatedImages[l] = new NativeImage(m, n, false);
                }
                for (int o = 0; o < n; ++o) {
                    for (int p = 0; p < m; ++p) {
                        int q = this.getFramePixel(i, l, p, o);
                        int r = this.getFramePixel(k, l, p, o);
                        int s = this.lerp(d, q >> 16 & 0xFF, r >> 16 & 0xFF);
                        int t = this.lerp(d, q >> 8 & 0xFF, r >> 8 & 0xFF);
                        int u = this.lerp(d, q & 0xFF, r & 0xFF);
                        this.interpolatedImages[l].setPixelRgba(p, o, q & 0xFF000000 | s << 16 | t << 8 | u);
                    }
                }
            }
            this.upload(0, 0, this.interpolatedImages);
        }
    }

    private int lerp(double d, int i, int j) {
        return (int)(d * (double)i + (1.0 - d) * (double)j);
    }

    public int getFrameCount() {
        return this.frameXs == null ? 0 : this.frameXs.length;
    }

    public void load(Resource resource, int i) throws IOException {
        NativeImage nativeImage = NativeImage.read(resource.getInputStream());
        this.images = new NativeImage[i];
        this.images[0] = nativeImage;
        int j = this.animationMetadata != null && this.animationMetadata.getWidth() != -1 ? nativeImage.getWidth() / this.animationMetadata.getWidth() : nativeImage.getWidth() / this.width;
        int k = this.animationMetadata != null && this.animationMetadata.getHeight() != -1 ? nativeImage.getHeight() / this.animationMetadata.getHeight() : nativeImage.getHeight() / this.height;
        if (this.animationMetadata != null && this.animationMetadata.getFrameCount() > 0) {
            int l = (Integer)this.animationMetadata.getFrameIndexSet().stream().max(Integer::compareTo).get() + 1;
            this.frameXs = new int[l];
            this.frameYs = new int[l];
            Arrays.fill(this.frameXs, -1);
            Arrays.fill(this.frameYs, -1);
            for (int m : this.animationMetadata.getFrameIndexSet()) {
                int o;
                if (m >= j * k) {
                    throw new RuntimeException("invalid frameindex " + m);
                }
                int n = m / j;
                this.frameXs[m] = o = m % j;
                this.frameYs[m] = n;
            }
        } else {
            int m;
            ArrayList<AnimationFrameResourceMetadata> list = Lists.newArrayList();
            int p = j * k;
            this.frameXs = new int[p];
            this.frameYs = new int[p];
            for (m = 0; m < k; ++m) {
                int n = 0;
                while (n < j) {
                    int o = m * j + n;
                    this.frameXs[o] = n++;
                    this.frameYs[o] = m;
                    list.add(new AnimationFrameResourceMetadata(o, -1));
                }
            }
            m = 1;
            boolean bl = false;
            if (this.animationMetadata != null) {
                m = this.animationMetadata.getDefaultFrameTime();
                bl = this.animationMetadata.shouldInterpolate();
            }
            this.animationMetadata = new AnimationResourceMetadata(list, this.width, this.height, m, bl);
        }
    }

    public void generateMipmaps(int i) {
        try {
            this.generateMipmapsInternal(i);
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Generating mipmaps for frame");
            CrashReportSection crashReportSection = crashReport.addElement("Frame being iterated");
            crashReportSection.add("Frame sizes", () -> {
                StringBuilder stringBuilder = new StringBuilder();
                for (NativeImage nativeImage : this.images) {
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(nativeImage == null ? "null" : nativeImage.getWidth() + "x" + nativeImage.getHeight());
                }
                return stringBuilder.toString();
            });
            throw new CrashException(crashReport);
        }
    }

    public void destroy() {
        if (this.images != null) {
            for (NativeImage nativeImage : this.images) {
                if (nativeImage == null) continue;
                nativeImage.close();
            }
        }
        this.images = null;
        if (this.interpolatedImages != null) {
            for (NativeImage nativeImage : this.interpolatedImages) {
                if (nativeImage == null) continue;
                nativeImage.close();
            }
        }
        this.interpolatedImages = null;
    }

    public boolean isAnimated() {
        return this.animationMetadata != null && this.animationMetadata.getFrameCount() > 1;
    }

    public String toString() {
        int i = this.frameXs == null ? 0 : this.frameXs.length;
        return "TextureAtlasSprite{name='" + this.id + '\'' + ", frameCount=" + i + ", x=" + this.x + ", y=" + this.y + ", height=" + this.height + ", width=" + this.width + ", u0=" + this.uMin + ", u1=" + this.uMax + ", v0=" + this.vMin + ", v1=" + this.vMax + '}';
    }

    private int getFramePixel(int i, int j, int k, int l) {
        return this.images[j].getPixelRgba(k + (this.frameXs[i] * this.width >> j), l + (this.frameYs[i] * this.height >> j));
    }

    public boolean isPixelTransparent(int i, int j, int k) {
        return (this.images[0].getPixelRgba(j + this.frameXs[i] * this.width, k + this.frameYs[i] * this.height) >> 24 & 0xFF) == 0;
    }

    public void upload() {
        this.upload(0);
    }
}

