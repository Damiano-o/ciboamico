package it.uniroma2.ispw.ciboamico.persistence.impl.fs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Configurazione Gson condivisa: TypeAdapter per java.time (LocalDate/LocalDateTime)
 * — necessari su Java 17+ dove la reflection su java.time è bloccata dal module system.
 */
public final class GsonConfig {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATA_ORA = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private GsonConfig() { }

    public static Gson gson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
                    @Override
                    public void write(JsonWriter out, LocalDate value) throws IOException {
                        if (value == null) { out.nullValue(); return; }
                        out.value(value.format(DATA));
                    }
                    @Override
                    public LocalDate read(JsonReader in) throws IOException {
                        if (in.peek() == JsonToken.NULL) { in.nextNull(); return null; }
                        return LocalDate.parse(in.nextString(), DATA);
                    }
                })
                .registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
                    @Override
                    public void write(JsonWriter out, LocalDateTime value) throws IOException {
                        if (value == null) { out.nullValue(); return; }
                        out.value(value.format(DATA_ORA));
                    }
                    @Override
                    public LocalDateTime read(JsonReader in) throws IOException {
                        if (in.peek() == JsonToken.NULL) { in.nextNull(); return null; }
                        return LocalDateTime.parse(in.nextString(), DATA_ORA);
                    }
                })
                .create();
    }
}
