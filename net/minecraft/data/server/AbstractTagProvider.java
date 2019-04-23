/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractTagProvider<T>
implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected final DataGenerator root;
    protected final Registry<T> registry;
    protected final Map<Tag<T>, Tag.Builder<T>> field_11481 = Maps.newLinkedHashMap();

    protected AbstractTagProvider(DataGenerator dataGenerator, Registry<T> registry) {
        this.root = dataGenerator;
        this.registry = registry;
    }

    protected abstract void configure();

    @Override
    public void run(DataCache dataCache) throws IOException {
        this.field_11481.clear();
        this.configure();
        TagContainer<Object> tagContainer = new TagContainer<Object>(identifier -> Optional.empty(), "", false, "generated");
        for (Map.Entry<Tag<T>, Tag.Builder<T>> entry : this.field_11481.entrySet()) {
            Identifier identifier2 = entry.getKey().getId();
            if (!entry.getValue().applyTagGetter(tagContainer::get)) {
                throw new UnsupportedOperationException("Unsupported referencing of tags!");
            }
            Tag<Object> tag = entry.getValue().build(identifier2);
            JsonObject jsonObject = tag.toJson(this.registry::getId);
            Path path = this.getOutput(identifier2);
            tagContainer.add(tag);
            this.method_10511(tagContainer);
            try {
                String string = GSON.toJson(jsonObject);
                String string2 = SHA1.hashUnencodedChars(string).toString();
                if (!Objects.equals(dataCache.getOldSha1(path), string2) || !Files.exists(path, new LinkOption[0])) {
                    Files.createDirectories(path.getParent(), new FileAttribute[0]);
                    try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, new OpenOption[0]);){
                        bufferedWriter.write(string);
                    }
                }
                dataCache.updateSha1(path, string2);
            } catch (IOException iOException) {
                LOGGER.error("Couldn't save tags to {}", (Object)path, (Object)iOException);
            }
        }
    }

    protected abstract void method_10511(TagContainer<T> var1);

    protected abstract Path getOutput(Identifier var1);

    protected Tag.Builder<T> method_10512(Tag<T> tag2) {
        return this.field_11481.computeIfAbsent(tag2, tag -> Tag.Builder.create());
    }
}

