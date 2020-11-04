package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceManager;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface class_5617<T extends Entity> {
	EntityRenderer<T> create(class_5617.class_5618 arg);

	@Environment(EnvType.CLIENT)
	public static class class_5618 {
		private final EntityRenderDispatcher field_27762;
		private final ItemRenderer field_27763;
		private final ResourceManager field_27764;
		private final class_5599 field_27765;
		private final TextRenderer field_27766;

		public class_5618(
			EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer, ResourceManager resourceManager, class_5599 arg, TextRenderer textRenderer
		) {
			this.field_27762 = entityRenderDispatcher;
			this.field_27763 = itemRenderer;
			this.field_27764 = resourceManager;
			this.field_27765 = arg;
			this.field_27766 = textRenderer;
		}

		public EntityRenderDispatcher method_32166() {
			return this.field_27762;
		}

		public ItemRenderer method_32168() {
			return this.field_27763;
		}

		public ResourceManager method_32169() {
			return this.field_27764;
		}

		public class_5599 method_32170() {
			return this.field_27765;
		}

		public ModelPart method_32167(EntityModelLayer entityModelLayer) {
			return this.field_27765.method_32072(entityModelLayer);
		}

		public TextRenderer method_32171() {
			return this.field_27766;
		}
	}
}
