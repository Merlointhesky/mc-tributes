# Tributes

A [Paper](https://papermc.io) Minecraft plugin that tracks player deaths as **tributes** and announces them Hunger-Games-style with cannon sounds.

## Features

- **Death tracking** — Every player death is recorded with the player's name, UUID, death message, and timestamp.
- **Cannon sound** — An explosion sound is played for all online players when someone dies.
- **Broadcast announcement** — A bold red/gold message is broadcast: *"A tribute has fallen: [Player]"*.
- **Recent tributes list** — Players joining the server see a list of all tributes fallen in the last 24 hours.
- **Custom MOTD** — A configurable welcome message is sent on join. Edit `plugins/Tributes/motd.txt` and use `{player}` as a placeholder.
- **Persistent storage** — Death records are saved to `plugins/Tributes/deaths.json` and automatically pruned after 24 hours.

## Requirements

- Paper 1.21+ server
- Java 21+

## Installation

1. Download the latest `Tributes-*.jar` from the [releases page](../../releases).
2. Drop the JAR into your server's `plugins/` folder.
3. Restart the server.
4. (Optional) Edit `plugins/Tributes/motd.txt` to customize the welcome message.

## Building from Source

```bash
./gradlew build
```

The compiled JAR will be in `build/libs/`.

## Configuration Files

| File | Location | Description |
|------|----------|-------------|
| `motd.txt` | `plugins/Tributes/motd.txt` | Welcome message shown on join. Use `{player}` for the player's name. |
| `deaths.json` | `plugins/Tributes/deaths.json` | Auto-generated file storing recent death records. Do not edit manually. |

## License

This project is open source. See the repository for license details.
