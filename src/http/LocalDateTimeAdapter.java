package http;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import util.Const;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    private static final DateTimeFormatter formatterWriter = DateTimeFormatter.ofPattern(Const.DATE_TIME_FORMAT);
    private static final DateTimeFormatter formatterReader = DateTimeFormatter.ofPattern(Const.DATE_TIME_FORMAT);

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        jsonWriter.value(localDateTime == null ? "" : localDateTime.format(formatterWriter));
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        final String jsonString = jsonReader.nextString();
        return "".equals(jsonString) ? null : LocalDateTime.parse(jsonString, formatterReader);
    }
}

