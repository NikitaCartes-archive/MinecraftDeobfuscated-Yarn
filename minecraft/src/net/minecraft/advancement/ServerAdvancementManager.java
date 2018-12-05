package net.minecraft.advancement;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.class_2779;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.CriterionCriterions;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.client.network.packet.UnlockRecipesClientPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerAdvancementManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(AdvancementProgress.class, new AdvancementProgress.Serializer())
		.registerTypeAdapter(Identifier.class, new Identifier.DeSerializer())
		.setPrettyPrinting()
		.create();
	private static final TypeToken<Map<Identifier, AdvancementProgress>> JSON_TYPE = new TypeToken<Map<Identifier, AdvancementProgress>>() {
	};
	private final MinecraftServer server;
	private final File advancementFile;
	private final Map<SimpleAdvancement, AdvancementProgress> field_13389 = Maps.<SimpleAdvancement, AdvancementProgress>newLinkedHashMap();
	private final Set<SimpleAdvancement> field_13390 = Sets.<SimpleAdvancement>newLinkedHashSet();
	private final Set<SimpleAdvancement> field_13386 = Sets.<SimpleAdvancement>newLinkedHashSet();
	private final Set<SimpleAdvancement> field_13388 = Sets.<SimpleAdvancement>newLinkedHashSet();
	private ServerPlayerEntity field_13391;
	@Nullable
	private SimpleAdvancement field_13387;
	private boolean field_13396 = true;

	public ServerAdvancementManager(MinecraftServer minecraftServer, File file, ServerPlayerEntity serverPlayerEntity) {
		this.server = minecraftServer;
		this.advancementFile = file;
		this.field_13391 = serverPlayerEntity;
		this.load();
	}

	public void method_12875(ServerPlayerEntity serverPlayerEntity) {
		this.field_13391 = serverPlayerEntity;
	}

	public void method_12881() {
		for (Criterion<?> criterion : CriterionCriterions.getAllCriterions()) {
			criterion.removePlayer(this);
		}
	}

	public void reload() {
		this.method_12881();
		this.field_13389.clear();
		this.field_13390.clear();
		this.field_13386.clear();
		this.field_13388.clear();
		this.field_13396 = true;
		this.field_13387 = null;
		this.load();
	}

	private void method_12889() {
		for (SimpleAdvancement simpleAdvancement : this.server.getAdvancementManager().getAdvancements()) {
			this.method_12874(simpleAdvancement);
		}
	}

	private void method_12887() {
		List<SimpleAdvancement> list = Lists.<SimpleAdvancement>newArrayList();

		for (Entry<SimpleAdvancement, AdvancementProgress> entry : this.field_13389.entrySet()) {
			if (((AdvancementProgress)entry.getValue()).isDone()) {
				list.add(entry.getKey());
				this.field_13388.add(entry.getKey());
			}
		}

		for (SimpleAdvancement simpleAdvancement : list) {
			this.method_12885(simpleAdvancement);
		}
	}

	private void method_12872() {
		for (SimpleAdvancement simpleAdvancement : this.server.getAdvancementManager().getAdvancements()) {
			if (simpleAdvancement.getCriteria().isEmpty()) {
				this.onAdvancement(simpleAdvancement, "");
				simpleAdvancement.getRewards().apply(this.field_13391);
			}
		}
	}

	private void load() {
		if (this.advancementFile.isFile()) {
			try {
				JsonReader jsonReader = new JsonReader(new StringReader(Files.toString(this.advancementFile, StandardCharsets.UTF_8)));
				Throwable var2 = null;

				try {
					jsonReader.setLenient(false);
					Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, Streams.parse(jsonReader));
					if (!dynamic.get("DataVersion").flatMap(Dynamic::getNumberValue).isPresent()) {
						dynamic = dynamic.set("DataVersion", dynamic.createInt(1343));
					}

					dynamic = this.server
						.getDataFixer()
						.update(DataFixTypes.ADVANCEMENTS, dynamic, dynamic.getInt("DataVersion"), SharedConstants.getGameVersion().getWorldVersion());
					dynamic = dynamic.remove("DataVersion");
					Map<Identifier, AdvancementProgress> map = GSON.getAdapter(JSON_TYPE).fromJsonTree(dynamic.getValue());
					if (map == null) {
						throw new JsonParseException("Found null for advancements");
					}

					Stream<Entry<Identifier, AdvancementProgress>> stream = map.entrySet().stream().sorted(Comparator.comparing(Entry::getValue));

					for (Entry<Identifier, AdvancementProgress> entry : (List)stream.collect(Collectors.toList())) {
						SimpleAdvancement simpleAdvancement = this.server.getAdvancementManager().get((Identifier)entry.getKey());
						if (simpleAdvancement == null) {
							LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", entry.getKey(), this.advancementFile);
						} else {
							this.method_12884(simpleAdvancement, (AdvancementProgress)entry.getValue());
						}
					}
				} catch (Throwable var18) {
					var2 = var18;
					throw var18;
				} finally {
					if (jsonReader != null) {
						if (var2 != null) {
							try {
								jsonReader.close();
							} catch (Throwable var17) {
								var2.addSuppressed(var17);
							}
						} else {
							jsonReader.close();
						}
					}
				}
			} catch (JsonParseException var20) {
				LOGGER.error("Couldn't parse player advancements in {}", this.advancementFile, var20);
			} catch (IOException var21) {
				LOGGER.error("Couldn't access player advancements in {}", this.advancementFile, var21);
			}
		}

		this.method_12872();
		this.method_12887();
		this.method_12889();
	}

	public void save() {
		Map<Identifier, AdvancementProgress> map = Maps.<Identifier, AdvancementProgress>newHashMap();

		for (Entry<SimpleAdvancement, AdvancementProgress> entry : this.field_13389.entrySet()) {
			AdvancementProgress advancementProgress = (AdvancementProgress)entry.getValue();
			if (advancementProgress.isAnyObtained()) {
				map.put(((SimpleAdvancement)entry.getKey()).getId(), advancementProgress);
			}
		}

		if (this.advancementFile.getParentFile() != null) {
			this.advancementFile.getParentFile().mkdirs();
		}

		JsonElement jsonElement = GSON.toJsonTree(map);
		jsonElement.getAsJsonObject().addProperty("DataVersion", SharedConstants.getGameVersion().getWorldVersion());

		try {
			OutputStream outputStream = new FileOutputStream(this.advancementFile);
			Throwable var38 = null;

			try {
				Writer writer = new OutputStreamWriter(outputStream, Charsets.UTF_8.newEncoder());
				Throwable var6 = null;

				try {
					GSON.toJson(jsonElement, writer);
				} catch (Throwable var31) {
					var6 = var31;
					throw var31;
				} finally {
					if (writer != null) {
						if (var6 != null) {
							try {
								writer.close();
							} catch (Throwable var30) {
								var6.addSuppressed(var30);
							}
						} else {
							writer.close();
						}
					}
				}
			} catch (Throwable var33) {
				var38 = var33;
				throw var33;
			} finally {
				if (outputStream != null) {
					if (var38 != null) {
						try {
							outputStream.close();
						} catch (Throwable var29) {
							var38.addSuppressed(var29);
						}
					} else {
						outputStream.close();
					}
				}
			}
		} catch (IOException var35) {
			LOGGER.error("Couldn't save player advancements to {}", this.advancementFile, var35);
		}
	}

	public boolean onAdvancement(SimpleAdvancement simpleAdvancement, String string) {
		boolean bl = false;
		AdvancementProgress advancementProgress = this.method_12882(simpleAdvancement);
		boolean bl2 = advancementProgress.isDone();
		if (advancementProgress.obtain(string)) {
			this.method_12880(simpleAdvancement);
			this.field_13388.add(simpleAdvancement);
			bl = true;
			if (!bl2 && advancementProgress.isDone()) {
				simpleAdvancement.getRewards().apply(this.field_13391);
				if (simpleAdvancement.getDisplay() != null
					&& simpleAdvancement.getDisplay().shouldAnnounceToChat()
					&& this.field_13391.world.getGameRules().getBoolean("announceAdvancements")) {
					this.server
						.getConfigurationManager()
						.sendToAll(
							new TranslatableTextComponent(
								"chat.type.advancement." + simpleAdvancement.getDisplay().getFrame().getId(), this.field_13391.getDisplayName(), simpleAdvancement.getTextComponent()
							)
						);
				}
			}
		}

		if (advancementProgress.isDone()) {
			this.method_12885(simpleAdvancement);
		}

		return bl;
	}

	public boolean method_12883(SimpleAdvancement simpleAdvancement, String string) {
		boolean bl = false;
		AdvancementProgress advancementProgress = this.method_12882(simpleAdvancement);
		if (advancementProgress.reset(string)) {
			this.method_12874(simpleAdvancement);
			this.field_13388.add(simpleAdvancement);
			bl = true;
		}

		if (!advancementProgress.isAnyObtained()) {
			this.method_12885(simpleAdvancement);
		}

		return bl;
	}

	private void method_12874(SimpleAdvancement simpleAdvancement) {
		AdvancementProgress advancementProgress = this.method_12882(simpleAdvancement);
		if (!advancementProgress.isDone()) {
			for (Entry<String, AdvancementCriterion> entry : simpleAdvancement.getCriteria().entrySet()) {
				CriterionProgress criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
				if (criterionProgress != null && !criterionProgress.isObtained()) {
					CriterionConditions criterionConditions = ((AdvancementCriterion)entry.getValue()).getConditions();
					if (criterionConditions != null) {
						Criterion<CriterionConditions> criterion = CriterionCriterions.getById(criterionConditions.getId());
						if (criterion != null) {
							criterion.addCondition(this, new Criterion.ConditionsContainer<>(criterionConditions, simpleAdvancement, (String)entry.getKey()));
						}
					}
				}
			}
		}
	}

	private void method_12880(SimpleAdvancement simpleAdvancement) {
		AdvancementProgress advancementProgress = this.method_12882(simpleAdvancement);

		for (Entry<String, AdvancementCriterion> entry : simpleAdvancement.getCriteria().entrySet()) {
			CriterionProgress criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
			if (criterionProgress != null && (criterionProgress.isObtained() || advancementProgress.isDone())) {
				CriterionConditions criterionConditions = ((AdvancementCriterion)entry.getValue()).getConditions();
				if (criterionConditions != null) {
					Criterion<CriterionConditions> criterion = CriterionCriterions.getById(criterionConditions.getId());
					if (criterion != null) {
						criterion.removeCondition(this, new Criterion.ConditionsContainer<>(criterionConditions, simpleAdvancement, (String)entry.getKey()));
					}
				}
			}
		}
	}

	public void method_12876(ServerPlayerEntity serverPlayerEntity) {
		if (this.field_13396 || !this.field_13386.isEmpty() || !this.field_13388.isEmpty()) {
			Map<Identifier, AdvancementProgress> map = Maps.<Identifier, AdvancementProgress>newHashMap();
			Set<SimpleAdvancement> set = Sets.<SimpleAdvancement>newLinkedHashSet();
			Set<Identifier> set2 = Sets.<Identifier>newLinkedHashSet();

			for (SimpleAdvancement simpleAdvancement : this.field_13388) {
				if (this.field_13390.contains(simpleAdvancement)) {
					map.put(simpleAdvancement.getId(), this.field_13389.get(simpleAdvancement));
				}
			}

			for (SimpleAdvancement simpleAdvancementx : this.field_13386) {
				if (this.field_13390.contains(simpleAdvancementx)) {
					set.add(simpleAdvancementx);
				} else {
					set2.add(simpleAdvancementx.getId());
				}
			}

			if (this.field_13396 || !map.isEmpty() || !set.isEmpty() || !set2.isEmpty()) {
				serverPlayerEntity.networkHandler.sendPacket(new class_2779(this.field_13396, set, set2, map));
				this.field_13386.clear();
				this.field_13388.clear();
			}
		}

		this.field_13396 = false;
	}

	public void method_12888(@Nullable SimpleAdvancement simpleAdvancement) {
		SimpleAdvancement simpleAdvancement2 = this.field_13387;
		if (simpleAdvancement != null && simpleAdvancement.getParent() == null && simpleAdvancement.getDisplay() != null) {
			this.field_13387 = simpleAdvancement;
		} else {
			this.field_13387 = null;
		}

		if (simpleAdvancement2 != this.field_13387) {
			this.field_13391.networkHandler.sendPacket(new UnlockRecipesClientPacket(this.field_13387 == null ? null : this.field_13387.getId()));
		}
	}

	public AdvancementProgress method_12882(SimpleAdvancement simpleAdvancement) {
		AdvancementProgress advancementProgress = (AdvancementProgress)this.field_13389.get(simpleAdvancement);
		if (advancementProgress == null) {
			advancementProgress = new AdvancementProgress();
			this.method_12884(simpleAdvancement, advancementProgress);
		}

		return advancementProgress;
	}

	private void method_12884(SimpleAdvancement simpleAdvancement, AdvancementProgress advancementProgress) {
		advancementProgress.method_727(simpleAdvancement.getCriteria(), simpleAdvancement.getRequirements());
		this.field_13389.put(simpleAdvancement, advancementProgress);
	}

	private void method_12885(SimpleAdvancement simpleAdvancement) {
		boolean bl = this.method_12879(simpleAdvancement);
		boolean bl2 = this.field_13390.contains(simpleAdvancement);
		if (bl && !bl2) {
			this.field_13390.add(simpleAdvancement);
			this.field_13386.add(simpleAdvancement);
			if (this.field_13389.containsKey(simpleAdvancement)) {
				this.field_13388.add(simpleAdvancement);
			}
		} else if (!bl && bl2) {
			this.field_13390.remove(simpleAdvancement);
			this.field_13386.add(simpleAdvancement);
		}

		if (bl != bl2 && simpleAdvancement.getParent() != null) {
			this.method_12885(simpleAdvancement.getParent());
		}

		for (SimpleAdvancement simpleAdvancement2 : simpleAdvancement.getChildren()) {
			this.method_12885(simpleAdvancement2);
		}
	}

	private boolean method_12879(SimpleAdvancement simpleAdvancement) {
		for (int i = 0; simpleAdvancement != null && i <= 2; i++) {
			if (i == 0 && this.method_12877(simpleAdvancement)) {
				return true;
			}

			if (simpleAdvancement.getDisplay() == null) {
				return false;
			}

			AdvancementProgress advancementProgress = this.method_12882(simpleAdvancement);
			if (advancementProgress.isDone()) {
				return true;
			}

			if (simpleAdvancement.getDisplay().isHidden()) {
				return false;
			}

			simpleAdvancement = simpleAdvancement.getParent();
		}

		return false;
	}

	private boolean method_12877(SimpleAdvancement simpleAdvancement) {
		AdvancementProgress advancementProgress = this.method_12882(simpleAdvancement);
		if (advancementProgress.isDone()) {
			return true;
		} else {
			for (SimpleAdvancement simpleAdvancement2 : simpleAdvancement.getChildren()) {
				if (this.method_12877(simpleAdvancement2)) {
					return true;
				}
			}

			return false;
		}
	}
}
