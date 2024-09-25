package net.minecraft.advancement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SelectAdvancementTabS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PathUtil;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;

public class PlayerAdvancementTracker {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final PlayerManager playerManager;
	private final Path filePath;
	private AdvancementManager advancementManager;
	private final Map<AdvancementEntry, AdvancementProgress> progress = new LinkedHashMap();
	private final Set<AdvancementEntry> visibleAdvancements = new HashSet();
	private final Set<AdvancementEntry> progressUpdates = new HashSet();
	private final Set<PlacedAdvancement> updatedRoots = new HashSet();
	private ServerPlayerEntity owner;
	@Nullable
	private AdvancementEntry currentDisplayTab;
	private boolean dirty = true;
	private final Codec<PlayerAdvancementTracker.ProgressMap> progressMapCodec;

	public PlayerAdvancementTracker(
		DataFixer dataFixer, PlayerManager playerManager, ServerAdvancementLoader advancementLoader, Path filePath, ServerPlayerEntity owner
	) {
		this.playerManager = playerManager;
		this.filePath = filePath;
		this.owner = owner;
		this.advancementManager = advancementLoader.getManager();
		int i = 1343;
		this.progressMapCodec = DataFixTypes.ADVANCEMENTS.createDataFixingCodec(PlayerAdvancementTracker.ProgressMap.CODEC, dataFixer, 1343);
		this.load(advancementLoader);
	}

	public void setOwner(ServerPlayerEntity owner) {
		this.owner = owner;
	}

	public void clearCriteria() {
		for (Criterion<?> criterion : Registries.CRITERION) {
			criterion.endTracking(this);
		}
	}

	public void reload(ServerAdvancementLoader advancementLoader) {
		this.clearCriteria();
		this.progress.clear();
		this.visibleAdvancements.clear();
		this.updatedRoots.clear();
		this.progressUpdates.clear();
		this.dirty = true;
		this.currentDisplayTab = null;
		this.advancementManager = advancementLoader.getManager();
		this.load(advancementLoader);
	}

	private void beginTrackingAllAdvancements(ServerAdvancementLoader advancementLoader) {
		for (AdvancementEntry advancementEntry : advancementLoader.getAdvancements()) {
			this.beginTracking(advancementEntry);
		}
	}

	private void rewardEmptyAdvancements(ServerAdvancementLoader advancementLoader) {
		for (AdvancementEntry advancementEntry : advancementLoader.getAdvancements()) {
			Advancement advancement = advancementEntry.value();
			if (advancement.criteria().isEmpty()) {
				this.grantCriterion(advancementEntry, "");
				advancement.rewards().apply(this.owner);
			}
		}
	}

	private void load(ServerAdvancementLoader advancementLoader) {
		if (Files.isRegularFile(this.filePath, new LinkOption[0])) {
			try {
				JsonReader jsonReader = new JsonReader(Files.newBufferedReader(this.filePath, StandardCharsets.UTF_8));

				try {
					jsonReader.setLenient(false);
					JsonElement jsonElement = Streams.parse(jsonReader);
					PlayerAdvancementTracker.ProgressMap progressMap = this.progressMapCodec.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonParseException::new);
					this.loadProgressMap(advancementLoader, progressMap);
				} catch (Throwable var6) {
					try {
						jsonReader.close();
					} catch (Throwable var5) {
						var6.addSuppressed(var5);
					}

					throw var6;
				}

				jsonReader.close();
			} catch (JsonIOException | IOException var7) {
				LOGGER.error("Couldn't access player advancements in {}", this.filePath, var7);
			} catch (JsonParseException var8) {
				LOGGER.error("Couldn't parse player advancements in {}", this.filePath, var8);
			}
		}

		this.rewardEmptyAdvancements(advancementLoader);
		this.beginTrackingAllAdvancements(advancementLoader);
	}

	public void save() {
		JsonElement jsonElement = this.progressMapCodec.encodeStart(JsonOps.INSTANCE, this.createProgressMap()).getOrThrow();

		try {
			PathUtil.createDirectories(this.filePath.getParent());
			Writer writer = Files.newBufferedWriter(this.filePath, StandardCharsets.UTF_8);

			try {
				GSON.toJson(jsonElement, GSON.newJsonWriter(writer));
			} catch (Throwable var6) {
				if (writer != null) {
					try {
						writer.close();
					} catch (Throwable var5) {
						var6.addSuppressed(var5);
					}
				}

				throw var6;
			}

			if (writer != null) {
				writer.close();
			}
		} catch (JsonIOException | IOException var7) {
			LOGGER.error("Couldn't save player advancements to {}", this.filePath, var7);
		}
	}

	private void loadProgressMap(ServerAdvancementLoader loader, PlayerAdvancementTracker.ProgressMap progressMap) {
		progressMap.forEach((id, progress) -> {
			AdvancementEntry advancementEntry = loader.get(id);
			if (advancementEntry == null) {
				LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", id, this.filePath);
			} else {
				this.initProgress(advancementEntry, progress);
				this.progressUpdates.add(advancementEntry);
				this.onStatusUpdate(advancementEntry);
			}
		});
	}

	private PlayerAdvancementTracker.ProgressMap createProgressMap() {
		Map<Identifier, AdvancementProgress> map = new LinkedHashMap();
		this.progress.forEach((entry, progress) -> {
			if (progress.isAnyObtained()) {
				map.put(entry.id(), progress);
			}
		});
		return new PlayerAdvancementTracker.ProgressMap(map);
	}

	public boolean grantCriterion(AdvancementEntry advancement, String criterionName) {
		boolean bl = false;
		AdvancementProgress advancementProgress = this.getProgress(advancement);
		boolean bl2 = advancementProgress.isDone();
		if (advancementProgress.obtain(criterionName)) {
			this.endTrackingCompleted(advancement);
			this.progressUpdates.add(advancement);
			bl = true;
			if (!bl2 && advancementProgress.isDone()) {
				advancement.value().rewards().apply(this.owner);
				advancement.value().display().ifPresent(display -> {
					if (display.shouldAnnounceToChat() && this.owner.getServerWorld().getGameRules().getBoolean(GameRules.ANNOUNCE_ADVANCEMENTS)) {
						this.playerManager.broadcast(display.getFrame().getChatAnnouncementText(advancement, this.owner), false);
					}
				});
			}
		}

		if (!bl2 && advancementProgress.isDone()) {
			this.onStatusUpdate(advancement);
		}

		return bl;
	}

	public boolean revokeCriterion(AdvancementEntry advancement, String criterionName) {
		boolean bl = false;
		AdvancementProgress advancementProgress = this.getProgress(advancement);
		boolean bl2 = advancementProgress.isDone();
		if (advancementProgress.reset(criterionName)) {
			this.beginTracking(advancement);
			this.progressUpdates.add(advancement);
			bl = true;
		}

		if (bl2 && !advancementProgress.isDone()) {
			this.onStatusUpdate(advancement);
		}

		return bl;
	}

	private void onStatusUpdate(AdvancementEntry advancement) {
		PlacedAdvancement placedAdvancement = this.advancementManager.get(advancement);
		if (placedAdvancement != null) {
			this.updatedRoots.add(placedAdvancement.getRoot());
		}
	}

	private void beginTracking(AdvancementEntry advancement) {
		AdvancementProgress advancementProgress = this.getProgress(advancement);
		if (!advancementProgress.isDone()) {
			for (Entry<String, AdvancementCriterion<?>> entry : advancement.value().criteria().entrySet()) {
				CriterionProgress criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
				if (criterionProgress != null && !criterionProgress.isObtained()) {
					this.beginTracking(advancement, (String)entry.getKey(), (AdvancementCriterion)entry.getValue());
				}
			}
		}
	}

	private <T extends CriterionConditions> void beginTracking(AdvancementEntry advancement, String id, AdvancementCriterion<T> criterion) {
		criterion.trigger().beginTrackingCondition(this, new Criterion.ConditionsContainer<>(criterion.conditions(), advancement, id));
	}

	private void endTrackingCompleted(AdvancementEntry advancement) {
		AdvancementProgress advancementProgress = this.getProgress(advancement);

		for (Entry<String, AdvancementCriterion<?>> entry : advancement.value().criteria().entrySet()) {
			CriterionProgress criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
			if (criterionProgress != null && (criterionProgress.isObtained() || advancementProgress.isDone())) {
				this.endTrackingCompleted(advancement, (String)entry.getKey(), (AdvancementCriterion)entry.getValue());
			}
		}
	}

	private <T extends CriterionConditions> void endTrackingCompleted(AdvancementEntry advancement, String id, AdvancementCriterion<T> criterion) {
		criterion.trigger().endTrackingCondition(this, new Criterion.ConditionsContainer<>(criterion.conditions(), advancement, id));
	}

	public void sendUpdate(ServerPlayerEntity player) {
		if (this.dirty || !this.updatedRoots.isEmpty() || !this.progressUpdates.isEmpty()) {
			Map<Identifier, AdvancementProgress> map = new HashMap();
			Set<AdvancementEntry> set = new HashSet();
			Set<Identifier> set2 = new HashSet();

			for (PlacedAdvancement placedAdvancement : this.updatedRoots) {
				this.calculateDisplay(placedAdvancement, set, set2);
			}

			this.updatedRoots.clear();

			for (AdvancementEntry advancementEntry : this.progressUpdates) {
				if (this.visibleAdvancements.contains(advancementEntry)) {
					map.put(advancementEntry.id(), (AdvancementProgress)this.progress.get(advancementEntry));
				}
			}

			this.progressUpdates.clear();
			if (!map.isEmpty() || !set.isEmpty() || !set2.isEmpty()) {
				player.networkHandler.sendPacket(new AdvancementUpdateS2CPacket(this.dirty, set, set2, map));
			}
		}

		this.dirty = false;
	}

	public void setDisplayTab(@Nullable AdvancementEntry advancement) {
		AdvancementEntry advancementEntry = this.currentDisplayTab;
		if (advancement != null && advancement.value().isRoot() && advancement.value().display().isPresent()) {
			this.currentDisplayTab = advancement;
		} else {
			this.currentDisplayTab = null;
		}

		if (advancementEntry != this.currentDisplayTab) {
			this.owner.networkHandler.sendPacket(new SelectAdvancementTabS2CPacket(this.currentDisplayTab == null ? null : this.currentDisplayTab.id()));
		}
	}

	public AdvancementProgress getProgress(AdvancementEntry advancement) {
		AdvancementProgress advancementProgress = (AdvancementProgress)this.progress.get(advancement);
		if (advancementProgress == null) {
			advancementProgress = new AdvancementProgress();
			this.initProgress(advancement, advancementProgress);
		}

		return advancementProgress;
	}

	private void initProgress(AdvancementEntry advancement, AdvancementProgress progress) {
		progress.init(advancement.value().requirements());
		this.progress.put(advancement, progress);
	}

	private void calculateDisplay(PlacedAdvancement root, Set<AdvancementEntry> added, Set<Identifier> removed) {
		AdvancementDisplays.calculateDisplay(root, advancement -> this.getProgress(advancement.getAdvancementEntry()).isDone(), (advancement, displayed) -> {
			AdvancementEntry advancementEntry = advancement.getAdvancementEntry();
			if (displayed) {
				if (this.visibleAdvancements.add(advancementEntry)) {
					added.add(advancementEntry);
					if (this.progress.containsKey(advancementEntry)) {
						this.progressUpdates.add(advancementEntry);
					}
				}
			} else if (this.visibleAdvancements.remove(advancementEntry)) {
				removed.add(advancementEntry.id());
			}
		});
	}

	static record ProgressMap(Map<Identifier, AdvancementProgress> map) {
		public static final Codec<PlayerAdvancementTracker.ProgressMap> CODEC = Codec.unboundedMap(Identifier.CODEC, AdvancementProgress.CODEC)
			.xmap(PlayerAdvancementTracker.ProgressMap::new, PlayerAdvancementTracker.ProgressMap::map);

		public void forEach(BiConsumer<Identifier, AdvancementProgress> consumer) {
			this.map
				.entrySet()
				.stream()
				.sorted(Entry.comparingByValue())
				.forEach(entry -> consumer.accept((Identifier)entry.getKey(), (AdvancementProgress)entry.getValue()));
		}
	}
}
