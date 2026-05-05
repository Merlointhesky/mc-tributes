package com.tributes;

import java.time.Instant;
import java.util.UUID;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class DeathRecord {

    private UUID playerId;
    private String playerName;
    private String deathMessage;
    private Instant timestamp;

    public DeathRecord() {}

    public DeathRecord(UUID playerId, String playerName, String deathMessage, Instant timestamp) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.deathMessage = deathMessage;
        this.timestamp = timestamp;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getDeathMessage() {
        return deathMessage;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public boolean isOlderThan24Hours() {
        return timestamp.isBefore(Instant.now().minusSeconds(24 * 60 * 60));
    }

    public static class InstantAdapter extends TypeAdapter<Instant> {
        @Override
        public void write(JsonWriter out, Instant value) throws java.io.IOException {
            out.value(value.toString());
        }

        @Override
        public Instant read(JsonReader in) throws java.io.IOException {
            return Instant.parse(in.nextString());
        }
    }
}
