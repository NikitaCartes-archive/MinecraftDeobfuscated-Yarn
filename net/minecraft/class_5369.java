/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5348;
import net.minecraft.class_5352;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class class_5369<T extends ResourcePackProfile> {
    private final List<T> field_25455;
    private final List<T> field_25456;
    private final BiConsumer<T, TextureManager> field_25457;
    private final Runnable field_25458;
    private final class_5370<T> field_25459;

    public class_5369(Runnable runnable, BiConsumer<T, TextureManager> biConsumer, Collection<T> collection, Collection<T> collection2, class_5370<T> arg) {
        this.field_25458 = runnable;
        this.field_25457 = biConsumer;
        this.field_25455 = Lists.newArrayList(collection);
        this.field_25456 = Lists.newArrayList(collection2);
        this.field_25459 = arg;
    }

    public Stream<class_5371> method_29639() {
        return this.field_25456.stream().map(resourcePackProfile -> new class_5374(this, resourcePackProfile));
    }

    public Stream<class_5371> method_29643() {
        return this.field_25455.stream().map(resourcePackProfile -> new class_5373(this, resourcePackProfile));
    }

    public void method_29642(boolean bl) {
        this.field_25459.accept(ImmutableList.copyOf(this.field_25455), ImmutableList.copyOf(this.field_25456), bl);
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface class_5370<T extends ResourcePackProfile> {
        public void accept(List<T> var1, List<T> var2, boolean var3);
    }

    @Environment(value=EnvType.CLIENT)
    static class class_5374
    extends class_5372 {
        final /* synthetic */ class_5369 field_25463;

        public class_5374(T resourcePackProfile) {
            this.field_25463 = arg;
            super((class_5369)arg, resourcePackProfile);
        }

        @Override
        protected List<T> method_29666() {
            return this.field_25463.field_25456;
        }

        @Override
        protected List<T> method_29667() {
            return this.field_25463.field_25455;
        }

        @Override
        public boolean method_29660() {
            return false;
        }

        @Override
        public void method_29656() {
            this.method_29668();
        }

        @Override
        public void method_29657() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class class_5373
    extends class_5372 {
        final /* synthetic */ class_5369 field_25462;

        public class_5373(T resourcePackProfile) {
            this.field_25462 = arg;
            super((class_5369)arg, resourcePackProfile);
        }

        @Override
        protected List<T> method_29666() {
            return this.field_25462.field_25455;
        }

        @Override
        protected List<T> method_29667() {
            return this.field_25462.field_25456;
        }

        @Override
        public boolean method_29660() {
            return true;
        }

        @Override
        public void method_29656() {
        }

        @Override
        public void method_29657() {
            this.method_29668();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static abstract class class_5372
    implements class_5371 {
        private final T field_25461;
        final /* synthetic */ class_5369 field_25460;

        public class_5372(T resourcePackProfile) {
            this.field_25460 = arg;
            this.field_25461 = resourcePackProfile;
        }

        protected abstract List<T> method_29666();

        protected abstract List<T> method_29667();

        @Override
        public void method_29649(TextureManager textureManager) {
            this.field_25460.field_25457.accept(this.field_25461, textureManager);
        }

        @Override
        public ResourcePackCompatibility method_29648() {
            return ((ResourcePackProfile)this.field_25461).getCompatibility();
        }

        @Override
        public Text method_29650() {
            return ((ResourcePackProfile)this.field_25461).getDisplayName();
        }

        @Override
        public Text method_29651() {
            return ((ResourcePackProfile)this.field_25461).getDescription();
        }

        @Override
        public class_5352 method_29652() {
            return ((ResourcePackProfile)this.field_25461).method_29483();
        }

        @Override
        public boolean method_29654() {
            return ((ResourcePackProfile)this.field_25461).isPinned();
        }

        @Override
        public boolean method_29655() {
            return ((ResourcePackProfile)this.field_25461).isAlwaysEnabled();
        }

        protected void method_29668() {
            this.method_29666().remove(this.field_25461);
            ((ResourcePackProfile)this.field_25461).getInitialPosition().insert(this.method_29667(), this.field_25461, Function.identity(), true);
            this.field_25460.field_25458.run();
        }

        protected void method_29665(int i) {
            List list = this.method_29666();
            int j = list.indexOf(this.field_25461);
            list.remove(j);
            list.add(j + i, this.field_25461);
            this.field_25460.field_25458.run();
        }

        @Override
        public boolean method_29663() {
            List list = this.method_29666();
            int i = list.indexOf(this.field_25461);
            return i > 0 && !((ResourcePackProfile)list.get(i - 1)).isPinned();
        }

        @Override
        public void method_29658() {
            this.method_29665(-1);
        }

        @Override
        public boolean method_29664() {
            List list = this.method_29666();
            int i = list.indexOf(this.field_25461);
            return i >= 0 && i < list.size() - 1 && !((ResourcePackProfile)list.get(i + 1)).isPinned();
        }

        @Override
        public void method_29659() {
            this.method_29665(1);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface class_5371 {
        public void method_29649(TextureManager var1);

        public ResourcePackCompatibility method_29648();

        public Text method_29650();

        public Text method_29651();

        public class_5352 method_29652();

        default public class_5348 method_29653() {
            return this.method_29652().decorate(this.method_29651());
        }

        public boolean method_29654();

        public boolean method_29655();

        public void method_29656();

        public void method_29657();

        public void method_29658();

        public void method_29659();

        public boolean method_29660();

        default public boolean method_29661() {
            return !this.method_29660();
        }

        default public boolean method_29662() {
            return this.method_29660() && !this.method_29655();
        }

        public boolean method_29663();

        public boolean method_29664();
    }
}

