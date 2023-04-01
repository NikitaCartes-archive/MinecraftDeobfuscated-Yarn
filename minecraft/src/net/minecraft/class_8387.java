package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Uuids;

public record class_8387(List<class_8291> approved, Map<UUID, class_8376> pending, class_8374 votes, int totalProposalCount) {
	public static final Codec<class_8387> field_44024 = RecordCodecBuilder.create(
		instance -> instance.group(
					class_8291.field_43504.listOf().fieldOf("approved").forGetter(class_8387::approved),
					Codec.unboundedMap(Uuids.STRING_CODEC, class_8376.field_43995).fieldOf("pending").forGetter(class_8387::pending),
					class_8374.field_43990.fieldOf("votes").forGetter(class_8387::votes),
					Codec.INT.fieldOf("total_proposal_count").forGetter(class_8387::totalProposalCount)
				)
				.apply(instance, class_8387::new)
	);

	public class_8387() {
		this(List.of(), Map.of(), new class_8374(Map.of()), 0);
	}

	public static class_8387 method_50580(Stream<RegistryEntry.Reference<class_8289>> stream, Map<UUID, class_8376> map, class_8374 arg, int i) {
		List<class_8291> list = (List<class_8291>)stream.flatMap(reference -> ((class_8289)reference.value()).method_50119()).collect(Collectors.toList());
		return new class_8387(list, map, arg, i);
	}
}
