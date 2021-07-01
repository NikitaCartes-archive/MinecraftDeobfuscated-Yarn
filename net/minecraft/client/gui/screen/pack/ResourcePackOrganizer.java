/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.pack;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ResourcePackOrganizer {
    private final ResourcePackManager resourcePackManager;
    final List<ResourcePackProfile> enabledPacks;
    final List<ResourcePackProfile> disabledPacks;
    final Function<ResourcePackProfile, Identifier> iconIdSupplier;
    final Runnable updateCallback;
    private final Consumer<ResourcePackManager> applier;

    public ResourcePackOrganizer(Runnable updateCallback, Function<ResourcePackProfile, Identifier> iconIdSupplier, ResourcePackManager resourcePackManager, Consumer<ResourcePackManager> applier) {
        this.updateCallback = updateCallback;
        this.iconIdSupplier = iconIdSupplier;
        this.resourcePackManager = resourcePackManager;
        this.enabledPacks = Lists.newArrayList(resourcePackManager.getEnabledProfiles());
        Collections.reverse(this.enabledPacks);
        this.disabledPacks = Lists.newArrayList(resourcePackManager.getProfiles());
        this.disabledPacks.removeAll(this.enabledPacks);
        this.applier = applier;
    }

    public Stream<Pack> getDisabledPacks() {
        return this.disabledPacks.stream().map(pack -> new DisabledPack((ResourcePackProfile)pack));
    }

    public Stream<Pack> getEnabledPacks() {
        return this.enabledPacks.stream().map(pack -> new EnabledPack((ResourcePackProfile)pack));
    }

    public void apply() {
        this.resourcePackManager.setEnabledProfiles(Lists.reverse(this.enabledPacks).stream().map(ResourcePackProfile::getName).collect(ImmutableList.toImmutableList()));
        this.applier.accept(this.resourcePackManager);
    }

    public void refresh() {
        this.resourcePackManager.scanPacks();
        this.enabledPacks.retainAll(this.resourcePackManager.getProfiles());
        this.disabledPacks.clear();
        this.disabledPacks.addAll(this.resourcePackManager.getProfiles());
        this.disabledPacks.removeAll(this.enabledPacks);
    }

    @Environment(value=EnvType.CLIENT)
    class EnabledPack
    extends AbstractPack {
        public EnabledPack(ResourcePackProfile resourcePackProfile) {
            super(resourcePackProfile);
        }

        @Override
        protected List<ResourcePackProfile> getCurrentList() {
            return ResourcePackOrganizer.this.enabledPacks;
        }

        @Override
        protected List<ResourcePackProfile> getOppositeList() {
            return ResourcePackOrganizer.this.disabledPacks;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void enable() {
        }

        @Override
        public void disable() {
            this.toggle();
        }
    }

    @Environment(value=EnvType.CLIENT)
    class DisabledPack
    extends AbstractPack {
        public DisabledPack(ResourcePackProfile resourcePackProfile) {
            super(resourcePackProfile);
        }

        @Override
        protected List<ResourcePackProfile> getCurrentList() {
            return ResourcePackOrganizer.this.disabledPacks;
        }

        @Override
        protected List<ResourcePackProfile> getOppositeList() {
            return ResourcePackOrganizer.this.enabledPacks;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public void enable() {
            this.toggle();
        }

        @Override
        public void disable() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    abstract class AbstractPack
    implements Pack {
        private final ResourcePackProfile profile;

        public AbstractPack(ResourcePackProfile profile) {
            this.profile = profile;
        }

        protected abstract List<ResourcePackProfile> getCurrentList();

        protected abstract List<ResourcePackProfile> getOppositeList();

        @Override
        public Identifier getIconId() {
            return ResourcePackOrganizer.this.iconIdSupplier.apply(this.profile);
        }

        @Override
        public ResourcePackCompatibility getCompatibility() {
            return this.profile.getCompatibility();
        }

        @Override
        public Text getDisplayName() {
            return this.profile.getDisplayName();
        }

        @Override
        public Text getDescription() {
            return this.profile.getDescription();
        }

        @Override
        public ResourcePackSource getSource() {
            return this.profile.getSource();
        }

        @Override
        public boolean isPinned() {
            return this.profile.isPinned();
        }

        @Override
        public boolean isAlwaysEnabled() {
            return this.profile.isAlwaysEnabled();
        }

        protected void toggle() {
            this.getCurrentList().remove(this.profile);
            this.profile.getInitialPosition().insert(this.getOppositeList(), this.profile, Function.identity(), true);
            ResourcePackOrganizer.this.updateCallback.run();
        }

        protected void move(int offset) {
            List<ResourcePackProfile> list = this.getCurrentList();
            int i = list.indexOf(this.profile);
            list.remove(i);
            list.add(i + offset, this.profile);
            ResourcePackOrganizer.this.updateCallback.run();
        }

        @Override
        public boolean canMoveTowardStart() {
            List<ResourcePackProfile> list = this.getCurrentList();
            int i = list.indexOf(this.profile);
            return i > 0 && !list.get(i - 1).isPinned();
        }

        @Override
        public void moveTowardStart() {
            this.move(-1);
        }

        @Override
        public boolean canMoveTowardEnd() {
            List<ResourcePackProfile> list = this.getCurrentList();
            int i = list.indexOf(this.profile);
            return i >= 0 && i < list.size() - 1 && !list.get(i + 1).isPinned();
        }

        @Override
        public void moveTowardEnd() {
            this.move(1);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Pack {
        public Identifier getIconId();

        public ResourcePackCompatibility getCompatibility();

        public Text getDisplayName();

        public Text getDescription();

        public ResourcePackSource getSource();

        default public Text getDecoratedDescription() {
            return this.getSource().decorate(this.getDescription());
        }

        public boolean isPinned();

        public boolean isAlwaysEnabled();

        public void enable();

        public void disable();

        public void moveTowardStart();

        public void moveTowardEnd();

        public boolean isEnabled();

        default public boolean canBeEnabled() {
            return !this.isEnabled();
        }

        default public boolean canBeDisabled() {
            return this.isEnabled() && !this.isAlwaysEnabled();
        }

        public boolean canMoveTowardStart();

        public boolean canMoveTowardEnd();
    }
}

