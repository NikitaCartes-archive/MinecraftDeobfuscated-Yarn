/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.filter;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TextStream {
    public static final TextStream field_28862 = new TextStream(){

        @Override
        public void onConnect() {
        }

        @Override
        public void onDisconnect() {
        }

        @Override
        public CompletableFuture<class_5837> filterText(String text) {
            return CompletableFuture.completedFuture(class_5837.method_33802(text));
        }

        @Override
        public CompletableFuture<List<class_5837>> filterTexts(List<String> texts) {
            return CompletableFuture.completedFuture(texts.stream().map(class_5837::method_33802).collect(ImmutableList.toImmutableList()));
        }
    };

    public void onConnect();

    public void onDisconnect();

    public CompletableFuture<class_5837> filterText(String var1);

    public CompletableFuture<List<class_5837>> filterTexts(List<String> var1);

    public static class class_5837 {
        public static final class_5837 field_28863 = new class_5837("", "");
        private final String field_28864;
        private final String field_28865;

        public class_5837(String string, String string2) {
            this.field_28864 = string;
            this.field_28865 = string2;
        }

        public String method_33801() {
            return this.field_28864;
        }

        public String method_33803() {
            return this.field_28865;
        }

        public static class_5837 method_33802(String string) {
            return new class_5837(string, string);
        }

        public static class_5837 method_33804(String string) {
            return new class_5837(string, "");
        }
    }
}

