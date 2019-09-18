package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class PointOfInterestDebugRenderer implements DebugRenderer.Renderer {
	private static final Logger field_18920 = LogManager.getLogger();
	private final MinecraftClient field_18786;
	private final Map<BlockPos, PointOfInterestDebugRenderer.class_4233> pointsOfInterest = Maps.<BlockPos, PointOfInterestDebugRenderer.class_4233>newHashMap();
	private final Set<ChunkSectionPos> field_18788 = Sets.<ChunkSectionPos>newHashSet();
	private final Map<UUID, PointOfInterestDebugRenderer.class_4232> field_18921 = Maps.<UUID, PointOfInterestDebugRenderer.class_4232>newHashMap();
	private UUID field_18922;

	public PointOfInterestDebugRenderer(MinecraftClient minecraftClient) {
		this.field_18786 = minecraftClient;
	}

	@Override
	public void clear() {
		this.pointsOfInterest.clear();
		this.field_18788.clear();
		this.field_18921.clear();
		this.field_18922 = null;
	}

	public void method_19701(PointOfInterestDebugRenderer.class_4233 arg) {
		this.pointsOfInterest.put(arg.field_18931, arg);
	}

	public void removePointOfInterest(BlockPos blockPos) {
		this.pointsOfInterest.remove(blockPos);
	}

	public void method_19702(BlockPos blockPos, int i) {
		PointOfInterestDebugRenderer.class_4233 lv = (PointOfInterestDebugRenderer.class_4233)this.pointsOfInterest.get(blockPos);
		if (lv == null) {
			field_18920.warn("Strange, setFreeTicketCount was called for an unknown POI: " + blockPos);
		} else {
			lv.field_18933 = i;
		}
	}

	public void method_19433(ChunkSectionPos chunkSectionPos) {
		this.field_18788.add(chunkSectionPos);
	}

	public void method_19435(ChunkSectionPos chunkSectionPos) {
		this.field_18788.remove(chunkSectionPos);
	}

	public void addPointOfInterest(PointOfInterestDebugRenderer.class_4232 arg) {
		this.field_18921.put(arg.field_18923, arg);
	}

	@Environment(EnvType.CLIENT)
	public static class class_4232 {
		public final UUID field_18923;
		public final int field_18924;
		public final String field_19328;
		public final String field_18925;
		public final int field_19329;
		public final Position field_18926;
		public final String field_19372;
		public final Path field_19330;
		public final boolean field_19373;
		public final List<String> field_18927 = Lists.<String>newArrayList();
		public final List<String> field_18928 = Lists.<String>newArrayList();
		public final List<String> field_19374 = Lists.<String>newArrayList();
		public final List<String> field_19375 = Lists.<String>newArrayList();
		public final Set<BlockPos> field_18930 = Sets.<BlockPos>newHashSet();

		public class_4232(UUID uUID, int i, String string, String string2, int j, Position position, String string3, @Nullable Path path, boolean bl) {
			this.field_18923 = uUID;
			this.field_18924 = i;
			this.field_19328 = string;
			this.field_18925 = string2;
			this.field_19329 = j;
			this.field_18926 = position;
			this.field_19372 = string3;
			this.field_19330 = path;
			this.field_19373 = bl;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4233 {
		public final BlockPos field_18931;
		public String field_18932;
		public int field_18933;

		public class_4233(BlockPos blockPos, String string, int i) {
			this.field_18931 = blockPos;
			this.field_18932 = string;
			this.field_18933 = i;
		}
	}
}
