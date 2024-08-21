package net.minecraft.client.render;

import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DefaultFramebufferSet implements PostEffectProcessor.FramebufferSet {
	public static final Identifier MAIN = PostEffectProcessor.MAIN;
	public static final Identifier TRANSLUCENT = Identifier.ofVanilla("translucent");
	public static final Identifier ITEM_ENTITY = Identifier.ofVanilla("item_entity");
	public static final Identifier PARTICLES = Identifier.ofVanilla("particles");
	public static final Identifier WEATHER = Identifier.ofVanilla("weather");
	public static final Identifier CLOUDS = Identifier.ofVanilla("clouds");
	public static final Identifier ENTITY_OUTLINE = Identifier.ofVanilla("entity_outline");
	public static final Set<Identifier> field_53902 = Set.of(MAIN);
	public static final Set<Identifier> field_53903 = Set.of(MAIN, ENTITY_OUTLINE);
	public static final Set<Identifier> STAGES = Set.of(MAIN, TRANSLUCENT, ITEM_ENTITY, PARTICLES, WEATHER, CLOUDS);
	public Handle<Framebuffer> mainFramebuffer = Handle.empty();
	@Nullable
	public Handle<Framebuffer> translucentFramebuffer;
	@Nullable
	public Handle<Framebuffer> itemEntityFramebuffer;
	@Nullable
	public Handle<Framebuffer> particlesFramebuffer;
	@Nullable
	public Handle<Framebuffer> weatherFramebuffer;
	@Nullable
	public Handle<Framebuffer> cloudsFramebuffer;
	@Nullable
	public Handle<Framebuffer> entityOutlineFramebuffer;

	@Override
	public void set(Identifier id, Handle<Framebuffer> framebuffer) {
		if (id.equals(MAIN)) {
			this.mainFramebuffer = framebuffer;
		} else if (id.equals(TRANSLUCENT)) {
			this.translucentFramebuffer = framebuffer;
		} else if (id.equals(ITEM_ENTITY)) {
			this.itemEntityFramebuffer = framebuffer;
		} else if (id.equals(PARTICLES)) {
			this.particlesFramebuffer = framebuffer;
		} else if (id.equals(WEATHER)) {
			this.weatherFramebuffer = framebuffer;
		} else if (id.equals(CLOUDS)) {
			this.cloudsFramebuffer = framebuffer;
		} else {
			if (!id.equals(ENTITY_OUTLINE)) {
				throw new IllegalArgumentException("No target with id " + id);
			}

			this.entityOutlineFramebuffer = framebuffer;
		}
	}

	@Nullable
	@Override
	public Handle<Framebuffer> get(Identifier id) {
		if (id.equals(MAIN)) {
			return this.mainFramebuffer;
		} else if (id.equals(TRANSLUCENT)) {
			return this.translucentFramebuffer;
		} else if (id.equals(ITEM_ENTITY)) {
			return this.itemEntityFramebuffer;
		} else if (id.equals(PARTICLES)) {
			return this.particlesFramebuffer;
		} else if (id.equals(WEATHER)) {
			return this.weatherFramebuffer;
		} else if (id.equals(CLOUDS)) {
			return this.cloudsFramebuffer;
		} else {
			return id.equals(ENTITY_OUTLINE) ? this.entityOutlineFramebuffer : null;
		}
	}

	public void clear() {
		this.mainFramebuffer = Handle.empty();
		this.translucentFramebuffer = null;
		this.itemEntityFramebuffer = null;
		this.particlesFramebuffer = null;
		this.weatherFramebuffer = null;
		this.cloudsFramebuffer = null;
		this.entityOutlineFramebuffer = null;
	}
}
