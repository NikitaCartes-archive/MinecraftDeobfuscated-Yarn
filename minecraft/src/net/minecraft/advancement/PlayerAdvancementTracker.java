package net.minecraft.advancement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
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
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SelectAdvancementTabS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.PathUtil;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;

public class PlayerAdvancementTracker {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(AdvancementProgress.class, new AdvancementProgress.Serializer())
		.registerTypeAdapter(Identifier.class, new Identifier.Serializer())
		.setPrettyPrinting()
		.create();
	private static final TypeToken<Map<Identifier, AdvancementProgress>> JSON_TYPE = new TypeToken<Map<Identifier, AdvancementProgress>>() {
	};
	private final DataFixer dataFixer;
	private final PlayerManager playerManager;
	private final Path filePath;
	private final Map<Advancement, AdvancementProgress> progress = new LinkedHashMap();
	private final Set<Advancement> visibleAdvancements = new HashSet();
	private final Set<Advancement> progressUpdates = new HashSet();
	private final Set<Advancement> updatedRoots = new HashSet();
	private ServerPlayerEntity owner;
	@Nullable
	private Advancement currentDisplayTab;
	private boolean dirty = true;

	public PlayerAdvancementTracker(
		DataFixer dataFixer, PlayerManager playerManager, ServerAdvancementLoader advancementLoader, Path filePath, ServerPlayerEntity owner
	) {
		this.dataFixer = dataFixer;
		this.playerManager = playerManager;
		this.filePath = filePath;
		this.owner = owner;
		this.load(advancementLoader);
	}

	public void setOwner(ServerPlayerEntity owner) {
		this.owner = owner;
	}

	public void clearCriteria() {
		for (Criterion<?> criterion : Criteria.getCriteria()) {
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
		this.load(advancementLoader);
	}

	private void beginTrackingAllAdvancements(ServerAdvancementLoader advancementLoader) {
		for (Advancement advancement : advancementLoader.getAdvancements()) {
			this.beginTracking(advancement);
		}
	}

	private void rewardEmptyAdvancements(ServerAdvancementLoader advancementLoader) {
		for (Advancement advancement : advancementLoader.getAdvancements()) {
			if (advancement.getCriteria().isEmpty()) {
				this.grantCriterion(advancement, "");
				advancement.getRewards().apply(this.owner);
			}
		}
	}

	private void load(ServerAdvancementLoader advancementLoader) {
		if (Files.isRegularFile(this.filePath, new LinkOption[0])) {
			try {
				JsonReader jsonReader = new JsonReader(Files.newBufferedReader(this.filePath, StandardCharsets.UTF_8));

				try {
					jsonReader.setLenient(false);
					Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, Streams.parse(jsonReader));
					int i = dynamic.get("DataVersion").asInt(1343);
					dynamic = dynamic.remove("DataVersion");
					dynamic = DataFixTypes.ADVANCEMENTS.update(this.dataFixer, dynamic, i);
					Map<Identifier, AdvancementProgress> map = GSON.getAdapter(JSON_TYPE).fromJsonTree(dynamic.getValue());
					if (map == null) {
						throw new JsonParseException("Found null for advancements");
					}

					map.entrySet().stream().sorted(Entry.comparingByValue()).forEach(entry -> {
						Advancement advancement = advancementLoader.get((Identifier)entry.getKey());
						if (advancement == null) {
							LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", entry.getKey(), this.filePath);
						} else {
							this.initProgress(advancement, (AdvancementProgress)entry.getValue());
							this.progressUpdates.add(advancement);
							this.onStatusUpdate(advancement);
						}
					});
				} catch (Throwable var7) {
					try {
						jsonReader.close();
					} catch (Throwable var6) {
						var7.addSuppressed(var6);
					}

					throw var7;
				}

				jsonReader.close();
			} catch (JsonParseException var8) {
				LOGGER.error("Couldn't parse player advancements in {}", this.filePath, var8);
			} catch (IOException var9) {
				LOGGER.error("Couldn't access player advancements in {}", this.filePath, var9);
			}
		}

		this.rewardEmptyAdvancements(advancementLoader);
		this.beginTrackingAllAdvancements(advancementLoader);
	}

	public void save() {
		Map<Identifier, AdvancementProgress> map = new LinkedHashMap();

		for (Entry<Advancement, AdvancementProgress> entry : this.progress.entrySet()) {
			AdvancementProgress advancementProgress = (AdvancementProgress)entry.getValue();
			if (advancementProgress.isAnyObtained()) {
				map.put(((Advancement)entry.getKey()).getId(), advancementProgress);
			}
		}

		JsonElement jsonElement = GSON.toJsonTree(map);
		jsonElement.getAsJsonObject().addProperty("DataVersion", SharedConstants.getGameVersion().getSaveVersion().getId());

		try {
			PathUtil.createDirectories(this.filePath.getParent());
			Writer writer = Files.newBufferedWriter(this.filePath, StandardCharsets.UTF_8);

			try {
				GSON.toJson(jsonElement, writer);
			} catch (Throwable var7) {
				if (writer != null) {
					try {
						writer.close();
					} catch (Throwable var6) {
						var7.addSuppressed(var6);
					}
				}

				throw var7;
			}

			if (writer != null) {
				writer.close();
			}
		} catch (IOException var8) {
			LOGGER.error("Couldn't save player advancements to {}", this.filePath, var8);
		}
	}

	public boolean grantCriterion(Advancement advancement, String criterionName) {
		boolean bl = false;
		AdvancementProgress advancementProgress = this.getProgress(advancement);
		boolean bl2 = advancementProgress.isDone();
		if (advancementProgress.obtain(criterionName)) {
			this.endTrackingCompleted(advancement);
			this.progressUpdates.add(advancement);
			bl = true;
			if (!bl2 && advancementProgress.isDone()) {
				advancement.getRewards().apply(this.owner);
				if (advancement.getDisplay() != null
					&& advancement.getDisplay().shouldAnnounceToChat()
					&& this.owner.getWorld().getGameRules().getBoolean(GameRules.ANNOUNCE_ADVANCEMENTS)) {
					this.playerManager
						.broadcast(
							Text.translatable("chat.type.advancement." + advancement.getDisplay().getFrame().getId(), this.owner.getDisplayName(), advancement.toHoverableText()),
							false
						);
				}
			}
		}

		if (!bl2 && advancementProgress.isDone()) {
			this.onStatusUpdate(advancement);
		}

		return bl;
	}

	public boolean revokeCriterion(Advancement advancement, String criterionName) {
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

	private void onStatusUpdate(Advancement advancement) {
		this.updatedRoots.add(advancement.getRoot());
	}

	private void beginTracking(Advancement advancement) {
		AdvancementProgress advancementProgress = this.getProgress(advancement);
		if (!advancementProgress.isDone()) {
			for (Entry<String, AdvancementCriterion> entry : advancement.getCriteria().entrySet()) {
				CriterionProgress criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
				if (criterionProgress != null && !criterionProgress.isObtained()) {
					CriterionConditions criterionConditions = ((AdvancementCriterion)entry.getValue()).getConditions();
					if (criterionConditions != null) {
						Criterion<CriterionConditions> criterion = Criteria.getById(criterionConditions.getId());
						if (criterion != null) {
							criterion.beginTrackingCondition(this, new Criterion.ConditionsContainer<>(criterionConditions, advancement, (String)entry.getKey()));
						}
					}
				}
			}
		}
	}

	private void endTrackingCompleted(Advancement advancement) {
		AdvancementProgress advancementProgress = this.getProgress(advancement);

		for (Entry<String, AdvancementCriterion> entry : advancement.getCriteria().entrySet()) {
			CriterionProgress criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
			if (criterionProgress != null && (criterionProgress.isObtained() || advancementProgress.isDone())) {
				CriterionConditions criterionConditions = ((AdvancementCriterion)entry.getValue()).getConditions();
				if (criterionConditions != null) {
					Criterion<CriterionConditions> criterion = Criteria.getById(criterionConditions.getId());
					if (criterion != null) {
						criterion.endTrackingCondition(this, new Criterion.ConditionsContainer<>(criterionConditions, advancement, (String)entry.getKey()));
					}
				}
			}
		}
	}

	public void sendUpdate(ServerPlayerEntity player) {
		if (this.dirty || !this.updatedRoots.isEmpty() || !this.progressUpdates.isEmpty()) {
			Map<Identifier, AdvancementProgress> map = new HashMap();
			Set<Advancement> set = new HashSet();
			Set<Identifier> set2 = new HashSet();

			for (Advancement advancement : this.updatedRoots) {
				this.calculateDisplay(advancement, set, set2);
			}

			this.updatedRoots.clear();

			for (Advancement advancement : this.progressUpdates) {
				if (this.visibleAdvancements.contains(advancement)) {
					map.put(advancement.getId(), (AdvancementProgress)this.progress.get(advancement));
				}
			}

			this.progressUpdates.clear();
			if (!map.isEmpty() || !set.isEmpty() || !set2.isEmpty()) {
				player.networkHandler.sendPacket(new AdvancementUpdateS2CPacket(this.dirty, set, set2, map));
			}
		}

		this.dirty = false;
	}

	public void setDisplayTab(@Nullable Advancement advancement) {
		Advancement advancement2 = this.currentDisplayTab;
		if (advancement != null && advancement.getParent() == null && advancement.getDisplay() != null) {
			this.currentDisplayTab = advancement;
		} else {
			this.currentDisplayTab = null;
		}

		if (advancement2 != this.currentDisplayTab) {
			this.owner.networkHandler.sendPacket(new SelectAdvancementTabS2CPacket(this.currentDisplayTab == null ? null : this.currentDisplayTab.getId()));
		}
	}

	public AdvancementProgress getProgress(Advancement advancement) {
		AdvancementProgress advancementProgress = (AdvancementProgress)this.progress.get(advancement);
		if (advancementProgress == null) {
			advancementProgress = new AdvancementProgress();
			this.initProgress(advancement, advancementProgress);
		}

		return advancementProgress;
	}

	private void initProgress(Advancement advancement, AdvancementProgress progress) {
		progress.init(advancement.getCriteria(), advancement.getRequirements());
		this.progress.put(advancement, progress);
	}

	private void calculateDisplay(Advancement root, Set<Advancement> added, Set<Identifier> removed) {
		AdvancementDisplays.calculateDisplay(root, advancement -> this.getProgress(advancement).isDone(), (advancement, displayed) -> {
			if (displayed) {
				if (this.visibleAdvancements.add(advancement)) {
					added.add(advancement);
					if (this.progress.containsKey(advancement)) {
						this.progressUpdates.add(advancement);
					}
				}
			} else if (this.visibleAdvancements.remove(advancement)) {
				removed.add(advancement.getId());
			}
		});
	}
}
