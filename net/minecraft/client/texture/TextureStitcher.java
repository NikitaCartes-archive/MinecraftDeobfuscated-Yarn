/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureStitcherCannotFitException;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TextureStitcher<T extends Stitchable> {
    private static final Comparator<Holder<?>> COMPARATOR = Comparator.comparing(holder -> -holder.height).thenComparing(holder -> -holder.width).thenComparing(holder -> holder.sprite.getId());
    private final int mipLevel;
    private final List<Holder<T>> holders = new ArrayList<Holder<T>>();
    private final List<Slot<T>> slots = new ArrayList<Slot<T>>();
    private int width;
    private int height;
    private final int maxWidth;
    private final int maxHeight;

    public TextureStitcher(int maxWidth, int maxHeight, int mipLevel) {
        this.mipLevel = mipLevel;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void add(T info) {
        Holder<T> holder = new Holder<T>(info, this.mipLevel);
        this.holders.add(holder);
    }

    public void stitch() {
        ArrayList<Holder<T>> list = new ArrayList<Holder<T>>(this.holders);
        list.sort(COMPARATOR);
        for (Holder holder2 : list) {
            if (this.fit(holder2)) continue;
            throw new TextureStitcherCannotFitException((Stitchable)holder2.sprite, list.stream().map(holder -> holder.sprite).collect(ImmutableList.toImmutableList()));
        }
    }

    public void getStitchedSprites(SpriteConsumer<T> consumer) {
        for (Slot<T> slot : this.slots) {
            slot.addAllFilledSlots(consumer);
        }
    }

    static int applyMipLevel(int size, int mipLevel) {
        return (size >> mipLevel) + ((size & (1 << mipLevel) - 1) == 0 ? 0 : 1) << mipLevel;
    }

    private boolean fit(Holder<T> holder) {
        for (Slot<T> slot : this.slots) {
            if (!slot.fit(holder)) continue;
            return true;
        }
        return this.growAndFit(holder);
    }

    private boolean growAndFit(Holder<T> holder) {
        Slot<T> slot;
        boolean bl5;
        boolean bl4;
        boolean bl2;
        int i = MathHelper.smallestEncompassingPowerOfTwo(this.width);
        int j = MathHelper.smallestEncompassingPowerOfTwo(this.height);
        int k = MathHelper.smallestEncompassingPowerOfTwo(this.width + holder.width);
        int l = MathHelper.smallestEncompassingPowerOfTwo(this.height + holder.height);
        boolean bl = k <= this.maxWidth;
        boolean bl3 = bl2 = l <= this.maxHeight;
        if (!bl && !bl2) {
            return false;
        }
        boolean bl32 = bl && i != k;
        boolean bl6 = bl4 = bl2 && j != l;
        if (bl32 ^ bl4) {
            bl5 = bl32;
        } else {
            boolean bl7 = bl5 = bl && i <= j;
        }
        if (bl5) {
            if (this.height == 0) {
                this.height = l;
            }
            slot = new Slot(this.width, 0, k - this.width, this.height);
            this.width = k;
        } else {
            slot = new Slot<T>(0, this.height, this.width, l - this.height);
            this.height = l;
        }
        slot.fit(holder);
        this.slots.add(slot);
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    record Holder<T extends Stitchable>(T sprite, int width, int height) {
        public Holder(T sprite, int mipLevel) {
            this(sprite, TextureStitcher.applyMipLevel(sprite.getWidth(), mipLevel), TextureStitcher.applyMipLevel(sprite.getHeight(), mipLevel));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Stitchable {
        public int getWidth();

        public int getHeight();

        public Identifier getId();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Slot<T extends Stitchable> {
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        @Nullable
        private List<Slot<T>> subSlots;
        @Nullable
        private Holder<T> texture;

        public Slot(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public boolean fit(Holder<T> holder) {
            if (this.texture != null) {
                return false;
            }
            int i = holder.width;
            int j = holder.height;
            if (i > this.width || j > this.height) {
                return false;
            }
            if (i == this.width && j == this.height) {
                this.texture = holder;
                return true;
            }
            if (this.subSlots == null) {
                this.subSlots = new ArrayList<Slot<T>>(1);
                this.subSlots.add(new Slot<T>(this.x, this.y, i, j));
                int k = this.width - i;
                int l = this.height - j;
                if (l > 0 && k > 0) {
                    int n;
                    int m = Math.max(this.height, k);
                    if (m >= (n = Math.max(this.width, l))) {
                        this.subSlots.add(new Slot<T>(this.x, this.y + j, i, l));
                        this.subSlots.add(new Slot<T>(this.x + i, this.y, k, this.height));
                    } else {
                        this.subSlots.add(new Slot<T>(this.x + i, this.y, k, j));
                        this.subSlots.add(new Slot<T>(this.x, this.y + j, this.width, l));
                    }
                } else if (k == 0) {
                    this.subSlots.add(new Slot<T>(this.x, this.y + j, i, l));
                } else if (l == 0) {
                    this.subSlots.add(new Slot<T>(this.x + i, this.y, k, j));
                }
            }
            for (Slot<T> slot : this.subSlots) {
                if (!slot.fit(holder)) continue;
                return true;
            }
            return false;
        }

        public void addAllFilledSlots(SpriteConsumer<T> consumer) {
            if (this.texture != null) {
                consumer.load(this.texture.sprite, this.getX(), this.getY());
            } else if (this.subSlots != null) {
                for (Slot slot : this.subSlots) {
                    slot.addAllFilledSlots(consumer);
                }
            }
        }

        public String toString() {
            return "Slot{originX=" + this.x + ", originY=" + this.y + ", width=" + this.width + ", height=" + this.height + ", texture=" + this.texture + ", subSlots=" + this.subSlots + "}";
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface SpriteConsumer<T extends Stitchable> {
        public void load(T var1, int var2, int var3);
    }
}

