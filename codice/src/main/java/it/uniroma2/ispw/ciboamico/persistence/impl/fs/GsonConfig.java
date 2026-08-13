package it.uniroma2.ispw.ciboamico.persistence.impl.fs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import it.uniroma2.ispw.ciboamico.entity.Ruolo;
import it.uniroma2.ispw.ciboamico.entity.RuoloCliente;
import it.uniroma2.ispw.ciboamico.entity.RuoloVenditore;
import it.uniroma2.ispw.ciboamico.entity.StatoVenditoreEnum;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Configurazione Gson condivisa: TypeAdapter per java.time e Ruolo.

public final class GsonConfig {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATA_ORA = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final String CAMPO_RECAPITO = "recapito";

    private GsonConfig() { }

    public static Gson gson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, localDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, localDateTimeAdapter())
                .registerTypeAdapter(Ruolo.class, ruoloAdapter())
                .create();
    }

    private static TypeAdapter<LocalDate> localDateAdapter() {
        return new TypeAdapter<LocalDate>() {
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
        };
    }

    private static TypeAdapter<LocalDateTime> localDateTimeAdapter() {
        return new TypeAdapter<LocalDateTime>() {
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
        };
    }

    private static TypeAdapter<Ruolo> ruoloAdapter() {
        return new TypeAdapter<Ruolo>() {
            @Override
            public void write(JsonWriter out, Ruolo ruolo) throws IOException {
                if (ruolo == null) { out.nullValue(); return; }
                out.beginObject();
                out.name("tipo").value(ruolo.getNomeRuolo());
                if (ruolo instanceof RuoloVenditore rv) {
                    scriviVenditore(out, rv);
                }
                out.endObject();
            }
            @Override
            public Ruolo read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) { in.nextNull(); return null; }
                JsonObject obj = JsonParser.parseReader(in).getAsJsonObject();
                return leggiRuolo(obj);
            }
        };
    }

    private static Ruolo leggiRuolo(JsonObject obj) {
        String tipo = obj.has("tipo") ? obj.getAsJsonPrimitive("tipo").getAsString() : null;
        if ("VENDITORE".equals(tipo)) {
            return venditoreDa(obj);
        }
        return new RuoloCliente();
    }

    private static RuoloVenditore venditoreDa(JsonObject obj) {
        RuoloVenditore rv = new RuoloVenditore(
                str(obj, "zona"), str(obj, CAMPO_RECAPITO));
        JsonElement st = obj.get("stato");
        if (st != null && !st.isJsonNull()) {
            rv.setStato(StatoVenditoreEnum.valueOf(st.getAsString()));
        }
        return rv;
    }

    private static void scriviVenditore(JsonWriter out, RuoloVenditore rv) throws IOException {
        out.name("zona").value(rv.getZona());
        out.name(CAMPO_RECAPITO).value(rv.getRecapito());
        if (rv.getStato() != null) {
            out.name("stato").value(rv.getStato().name());
        }
    }

    private static String str(JsonObject obj, String chiave) {
        return obj.has(chiave) ? obj.get(chiave).getAsString() : null;
    }
}
