# Validation for 1.0.0

Target: Minecraft 1.20.1, Forge 47.3.22, Java 17.0.17, ForgeGradle 6.0.36, Gradle 8.8.

- Gradle `build`: successful; generated reobfuscated `imperial_sabre-1.0.0.jar`.
- Forge `runGameTestServer`: all **4 required GameTests passed**, successful task exit.
- GameTests verify dedicated server registration/attributes, forward-cone/range rejection, effect and particle network round trips, and charged skill mob damage/cooldown/durability.
- JAR CRCs, all JSON resources, OBJ material references and embedded generated atlas bytes checked.
- Software OpenGL client smoke test reached the title screen and loaded a creative flat world.
- Actual first-person weapon rendering checked; OBJ vertical UV mapping corrected and hilt placement adjusted.
- Third-person charging pose, upward-facing rotating seal and release VFX checked in-game after corrections.
- The original image-generation atlas was copied unchanged and sampled using quadrant UVs.

The client smoke test used an isolated local display. The null OpenAL backend verified sound resource initialization; audible playback was not verified. Authentication/update-check requests were unavailable in the cloud test environment; local offline gameplay and the GameTests completed. Compatibility with other combat/animation mods and multi-client visual synchronization were not tested.
