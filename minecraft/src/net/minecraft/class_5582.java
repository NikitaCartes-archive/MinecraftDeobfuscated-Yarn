package net.minecraft;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.EntityLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_5582<T extends class_5568> {
	private static final Logger field_27279 = LogManager.getLogger();
	private final EntityLoader<T> field_27280;
	private final class_5570<T> field_27281;
	private final class_5573<T> field_27282;
	private final LongSet field_27283 = new LongOpenHashSet();
	private final class_5577<T> field_27284;

	public class_5582(Class<T> class_, EntityLoader<T> entityLoader) {
		this.field_27281 = new class_5570<>();
		this.field_27282 = new class_5573<>(class_, l -> this.field_27283.contains(l) ? class_5584.TICKING : class_5584.TRACKED);
		this.field_27280 = entityLoader;
		this.field_27284 = new class_5578<>(this.field_27281, this.field_27282);
	}

	public void method_31869(ChunkPos chunkPos) {
		long l = chunkPos.toLong();
		this.field_27283.add(l);
		this.field_27282.method_31782(l).forEach(arg -> {
			class_5584 lv = arg.method_31763(class_5584.TICKING);
			if (!lv.shouldTick()) {
				arg.method_31766().filter(argx -> !argx.isPlayer()).forEach(this.field_27280::addEntity);
			}
		});
	}

	public void method_31875(ChunkPos chunkPos) {
		long l = chunkPos.toLong();
		this.field_27283.remove(l);
		this.field_27282.method_31782(l).forEach(arg -> {
			class_5584 lv = arg.method_31763(class_5584.TRACKED);
			if (lv.shouldTick()) {
				arg.method_31766().filter(argx -> !argx.isPlayer()).forEach(this.field_27280::removeEntity);
			}
		});
	}

	public class_5577<T> method_31866() {
		return this.field_27284;
	}

	public void method_31870(T arg) {
		this.field_27281.addEntity(arg);
		long l = class_5573.method_31779(arg.getBlockPos());
		class_5572<T> lv = this.field_27282.method_31784(l);
		lv.method_31764(arg);
		arg.method_31744(new class_5582.class_5583(arg, l, lv));
		this.field_27280.method_31802(arg);
		this.field_27280.onLoadEntity(arg);
		if (arg.isPlayer() || lv.method_31768().shouldTick()) {
			this.field_27280.addEntity(arg);
		}
	}

	public int method_31874() {
		return this.field_27281.getEntityCount();
	}

	private void method_31868(long l, class_5572<T> arg) {
		if (arg.method_31761()) {
			this.field_27282.method_31786(l);
		}
	}

	public String method_31879() {
		return this.field_27281.getEntityCount() + "," + this.field_27282.method_31781() + "," + this.field_27283.size();
	}

	@Environment(EnvType.CLIENT)
	class class_5583 implements class_5569 {
		private final T entity;
		private long field_27287;
		private class_5572<T> field_27288;

		private class_5583(T entity, long l, class_5572<T> arg2) {
			this.entity = entity;
			this.field_27287 = l;
			this.field_27288 = arg2;
		}

		@Override
		public void updateEntityPosition() {
			BlockPos blockPos = this.entity.getBlockPos();
			long l = class_5573.method_31779(blockPos);
			if (l != this.field_27287) {
				class_5584 lv = this.field_27288.method_31768();
				if (!this.field_27288.method_31767(this.entity)) {
					class_5582.field_27279.warn("Entity {} wasn't found in section {} (moving to {})", this.entity, ChunkSectionPos.from(this.field_27287), l);
				}

				class_5582.this.method_31868(this.field_27287, this.field_27288);
				class_5572<T> lv2 = class_5582.this.field_27282.method_31784(l);
				lv2.method_31764(this.entity);
				this.field_27288 = lv2;
				this.field_27287 = l;
				if (!this.entity.isPlayer()) {
					boolean bl = lv.shouldTick();
					boolean bl2 = lv2.method_31768().shouldTick();
					if (bl && !bl2) {
						class_5582.this.field_27280.removeEntity(this.entity);
					} else if (!bl && bl2) {
						class_5582.this.field_27280.addEntity(this.entity);
					}
				}
			}
		}

		@Override
		public void remove(Entity.RemovalReason reason) {
			if (!this.field_27288.method_31767(this.entity)) {
				class_5582.field_27279.warn("Entity {} wasn't found in section {} (destroying due to {})", this.entity, ChunkSectionPos.from(this.field_27287), reason);
			}

			class_5584 lv = this.field_27288.method_31768();
			if (lv.shouldTick() || this.entity.isPlayer()) {
				class_5582.this.field_27280.removeEntity(this.entity);
			}

			class_5582.this.field_27280.onUnloadEntity(this.entity);
			class_5582.this.field_27280.destroyEntity(this.entity);
			class_5582.this.field_27281.removeEntity(this.entity);
			this.entity.method_31744(field_27243);
			class_5582.this.method_31868(this.field_27287, this.field_27288);
		}
	}
}
