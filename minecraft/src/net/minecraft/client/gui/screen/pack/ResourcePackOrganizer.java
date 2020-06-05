package net.minecraft.client.gui.screen.pack;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ResourcePackOrganizer<T extends ResourcePackProfile> {
	private final List<T> enabledPacks;
	private final List<T> disabledPacks;
	private final BiConsumer<T, TextureManager> renderer;
	private final Runnable updateCallback;
	private final ResourcePackOrganizer.Applier<T> applier;

	public ResourcePackOrganizer(
		Runnable updateCallback,
		BiConsumer<T, TextureManager> renderer,
		Collection<T> enabledPacks,
		Collection<T> disabledPacks,
		ResourcePackOrganizer.Applier<T> applier
	) {
		this.updateCallback = updateCallback;
		this.renderer = renderer;
		this.enabledPacks = Lists.<T>newArrayList(enabledPacks);
		this.disabledPacks = Lists.<T>newArrayList(disabledPacks);
		this.applier = applier;
	}

	public Stream<ResourcePackOrganizer.Pack> getDisabledPacks() {
		return this.disabledPacks.stream().map(resourcePackProfile -> new ResourcePackOrganizer.DisabledPack(resourcePackProfile));
	}

	public Stream<ResourcePackOrganizer.Pack> getEnabledPacks() {
		return this.enabledPacks.stream().map(resourcePackProfile -> new ResourcePackOrganizer.EnabledPack(resourcePackProfile));
	}

	public void apply(boolean unchanged) {
		this.applier.accept(ImmutableList.copyOf(this.enabledPacks), ImmutableList.copyOf(this.disabledPacks), unchanged);
	}

	@Environment(EnvType.CLIENT)
	abstract class AbstractPack implements ResourcePackOrganizer.Pack {
		private final T profile;

		public AbstractPack(T profile) {
			this.profile = profile;
		}

		protected abstract List<T> getCurrentList();

		protected abstract List<T> getOppositeList();

		@Override
		public void render(TextureManager textureManager) {
			ResourcePackOrganizer.this.renderer.accept(this.profile, textureManager);
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
			List<T> list = this.getCurrentList();
			int i = list.indexOf(this.profile);
			list.remove(i);
			list.add(i + offset, this.profile);
			ResourcePackOrganizer.this.updateCallback.run();
		}

		@Override
		public boolean canMoveTowardStart() {
			List<T> list = this.getCurrentList();
			int i = list.indexOf(this.profile);
			return i > 0 && !((ResourcePackProfile)list.get(i - 1)).isPinned();
		}

		@Override
		public void moveTowardStart() {
			this.move(-1);
		}

		@Override
		public boolean canMoveTowardEnd() {
			List<T> list = this.getCurrentList();
			int i = list.indexOf(this.profile);
			return i >= 0 && i < list.size() - 1 && !((ResourcePackProfile)list.get(i + 1)).isPinned();
		}

		@Override
		public void moveTowardEnd() {
			this.move(1);
		}
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface Applier<T extends ResourcePackProfile> {
		void accept(List<T> enabled, List<T> disabled, boolean unchanged);
	}

	@Environment(EnvType.CLIENT)
	class DisabledPack extends ResourcePackOrganizer<T>.AbstractPack {
		public DisabledPack(T resourcePackProfile) {
			super(resourcePackProfile);
		}

		@Override
		protected List<T> getCurrentList() {
			return ResourcePackOrganizer.this.disabledPacks;
		}

		@Override
		protected List<T> getOppositeList() {
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

	@Environment(EnvType.CLIENT)
	class EnabledPack extends ResourcePackOrganizer<T>.AbstractPack {
		public EnabledPack(T resourcePackProfile) {
			super(resourcePackProfile);
		}

		@Override
		protected List<T> getCurrentList() {
			return ResourcePackOrganizer.this.enabledPacks;
		}

		@Override
		protected List<T> getOppositeList() {
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

	@Environment(EnvType.CLIENT)
	public interface Pack {
		void render(TextureManager textureManager);

		ResourcePackCompatibility getCompatibility();

		Text getDisplayName();

		Text getDescription();

		ResourcePackSource getSource();

		default StringRenderable getDecoratedDescription() {
			return this.getSource().decorate(this.getDescription());
		}

		boolean isPinned();

		boolean isAlwaysEnabled();

		void enable();

		void disable();

		void moveTowardStart();

		void moveTowardEnd();

		boolean isEnabled();

		default boolean canBeEnabled() {
			return !this.isEnabled();
		}

		default boolean canBeDisabled() {
			return this.isEnabled() && !this.isAlwaysEnabled();
		}

		boolean canMoveTowardStart();

		boolean canMoveTowardEnd();
	}
}
