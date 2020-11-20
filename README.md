# KotlinVaro (KVaro)

This is a Paper plugin to provide useful functions for playing Varo (Minecraft vanilla team pvp game). It's just a really basic implementation with some funny personal jokes, lol.

Be careful, everything text is written in German.

This game uses many discord webhooks (or may use many discord webhooks) to announce deaths/joins/worldborder changes...

## Compiling
Run `./gradlew shadowJar`

## Usage
Place the compile output from `build/libs/KVaro-1.0-all.jar` or [here](https://github.com/MrKinau/KVaro/releases/latest) and the [KotlinBukkitAPI](https://github.com/DevSrSouza/KotlinBukkitAPI) from [here](http://jenkins.devsrsouza.com.br/job/KotlinBukkitAPI/) to your plugins folder and restart/reload your server.

You can edit the `varoConfig.yml` to change some settings.

You can use `/start` to start the Varo game or `/leaklocation <player>` to leak someones location.