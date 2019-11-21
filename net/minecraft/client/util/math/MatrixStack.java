/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util.math;

import com.google.common.collect.Queues;
import java.util.Deque;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(value=EnvType.CLIENT)
public class MatrixStack {
    private final Deque<Entry> stack = Util.create(Queues.newArrayDeque(), arrayDeque -> {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.loadIdentity();
        Matrix3f matrix3f = new Matrix3f();
        matrix3f.loadIdentity();
        arrayDeque.add(new Entry(matrix4f, matrix3f));
    });

    public void translate(double d, double e, double f) {
        Entry entry = this.stack.getLast();
        entry.modelMatrix.multiply(Matrix4f.method_24021((float)d, (float)e, (float)f));
    }

    public void scale(float f, float g, float h) {
        Entry entry = this.stack.getLast();
        entry.modelMatrix.multiply(Matrix4f.method_24019(f, g, h));
        if (f == g && g == h) {
            return;
        }
        float i = 1.0f / f;
        float j = 1.0f / g;
        float k = 1.0f / h;
        float l = MathHelper.fastInverseCbrt(i * j * k);
        entry.normalMatrix.multiply(Matrix3f.method_23963(l * i, l * j, l * k));
    }

    public void multiply(Quaternion quaternion) {
        Entry entry = this.stack.getLast();
        entry.modelMatrix.multiply(quaternion);
        entry.normalMatrix.multiply(quaternion);
    }

    public void push() {
        Entry entry = this.stack.getLast();
        this.stack.addLast(new Entry(entry.modelMatrix.copy(), entry.normalMatrix.copy()));
    }

    public void pop() {
        this.stack.removeLast();
    }

    public Entry peek() {
        return this.stack.getLast();
    }

    public boolean isEmpty() {
        return this.stack.size() == 1;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Entry {
        private final Matrix4f modelMatrix;
        private final Matrix3f normalMatrix;

        private Entry(Matrix4f matrix4f, Matrix3f matrix3f) {
            this.modelMatrix = matrix4f;
            this.normalMatrix = matrix3f;
        }

        public Matrix4f getModel() {
            return this.modelMatrix;
        }

        public Matrix3f getNormal() {
            return this.normalMatrix;
        }
    }
}

