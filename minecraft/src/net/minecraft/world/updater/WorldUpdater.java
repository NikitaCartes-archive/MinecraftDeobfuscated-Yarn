package net.minecraft.world.updater;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1256;
import net.minecraft.class_37;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.SystemUtil;
import net.minecraft.world.ChunkSaveHandlerImpl;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldUpdater {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ThreadFactory UPDATE_THREAD_FACTORY = new ThreadFactoryBuilder().setDaemon(true).build();
	private final String levelName;
	private final WorldSaveHandler field_5761;
	private final class_37 field_5755;
	private final Thread updateThread;
	private volatile boolean field_5760 = true;
	private volatile boolean field_5759 = false;
	private volatile float field_5763;
	private volatile int field_5768;
	private volatile int field_5766 = 0;
	private volatile int field_5764 = 0;
	private final Object2FloatMap<DimensionType> field_5762 = Object2FloatMaps.synchronize(new Object2FloatOpenCustomHashMap<>(SystemUtil.identityHashStrategy()));
	private volatile TextComponent status = new TranslatableTextComponent("optimizeWorld.stage.counting");

	public WorldUpdater(String string, LevelStorage levelStorage, LevelProperties levelProperties) {
		this.levelName = levelProperties.getLevelName();
		this.field_5761 = levelStorage.method_242(string, null);
		this.field_5761.saveWorld(levelProperties);
		this.field_5755 = new class_37(this.field_5761);
		this.updateThread = UPDATE_THREAD_FACTORY.newThread(this::updateWorld);
		this.updateThread.setUncaughtExceptionHandler(this::method_5398);
		this.updateThread.start();
	}

	private void method_5398(Thread thread, Throwable throwable) {
		LOGGER.error("Error upgrading world", throwable);
		this.field_5760 = false;
		this.status = new TranslatableTextComponent("optimizeWorld.stage.failed");
	}

	public void method_5402() {
		this.field_5760 = false;

		try {
			this.updateThread.join();
		} catch (InterruptedException var2) {
		}
	}

	private void updateWorld() {
		File file = this.field_5761.getWorldDir();
		class_1256 lv = new class_1256(file);
		Builder<DimensionType, ChunkSaveHandlerImpl> builder = ImmutableMap.builder();

		for (DimensionType dimensionType : DimensionType.getAll()) {
			builder.put(dimensionType, new ChunkSaveHandlerImpl(dimensionType.getFile(file), this.field_5761.method_130()));
		}

		Map<DimensionType, ChunkSaveHandlerImpl> map = builder.build();
		long l = SystemUtil.getMeasuringTimeMili();
		this.field_5768 = 0;
		Builder<DimensionType, ListIterator<ChunkPos>> builder2 = ImmutableMap.builder();

		for (DimensionType dimensionType2 : DimensionType.getAll()) {
			List<ChunkPos> list = lv.method_5391(dimensionType2);
			builder2.put(dimensionType2, list.listIterator());
			this.field_5768 = this.field_5768 + list.size();
		}

		ImmutableMap<DimensionType, ListIterator<ChunkPos>> immutableMap = builder2.build();
		float f = (float)this.field_5768;
		this.status = new TranslatableTextComponent("optimizeWorld.stage.structures");

		for (Entry<DimensionType, ChunkSaveHandlerImpl> entry : map.entrySet()) {
			((ChunkSaveHandlerImpl)entry.getValue()).method_12380((DimensionType)entry.getKey(), this.field_5755);
		}

		this.field_5755.method_265();
		this.status = new TranslatableTextComponent("optimizeWorld.stage.upgrading");
		if (f <= 0.0F) {
			for (DimensionType dimensionType3 : DimensionType.getAll()) {
				this.field_5762.put(dimensionType3, 1.0F / (float)map.size());
			}
		}

		while (this.field_5760) {
			boolean bl = false;
			float g = 0.0F;

			for (DimensionType dimensionType4 : DimensionType.getAll()) {
				ListIterator<ChunkPos> listIterator = immutableMap.get(dimensionType4);
				bl |= this.method_5396((ChunkSaveHandlerImpl)map.get(dimensionType4), listIterator, dimensionType4);
				if (f > 0.0F) {
					float h = (float)listIterator.nextIndex() / f;
					this.field_5762.put(dimensionType4, h);
					g += h;
				}
			}

			this.field_5763 = g;
			if (!bl) {
				this.field_5760 = false;
			}
		}

		this.status = new TranslatableTextComponent("optimizeWorld.stage.finished");
		l = SystemUtil.getMeasuringTimeMili() - l;
		LOGGER.info("World optimizaton finished after {} ms", l);
		map.values().forEach(ChunkSaveHandlerImpl::save);
		this.field_5755.method_265();
		this.field_5759 = true;
	}

	private boolean method_5396(ChunkSaveHandlerImpl chunkSaveHandlerImpl, ListIterator<ChunkPos> listIterator, DimensionType dimensionType) {
		if (listIterator.hasNext()) {
			boolean bl;
			synchronized (chunkSaveHandlerImpl) {
				bl = chunkSaveHandlerImpl.method_12375((ChunkPos)listIterator.next(), dimensionType, this.field_5755);
			}

			if (bl) {
				this.field_5766++;
			} else {
				this.field_5764++;
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean method_5403() {
		return this.field_5759;
	}

	@Environment(EnvType.CLIENT)
	public float method_5393(DimensionType dimensionType) {
		return this.field_5762.getFloat(dimensionType);
	}

	@Environment(EnvType.CLIENT)
	public float method_5401() {
		return this.field_5763;
	}

	public int method_5397() {
		return this.field_5768;
	}

	public int method_5400() {
		return this.field_5766;
	}

	public int method_5399() {
		return this.field_5764;
	}

	public TextComponent getStatus() {
		return this.status;
	}

	@Environment(EnvType.CLIENT)
	public String getLevelName() {
		return this.levelName;
	}
}
