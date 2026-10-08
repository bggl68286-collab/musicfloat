# Validation · Minecraft 1.20.1 / Forge 47.3.22

Toolchain: Java 17.0.17, ForgeGradle 6.0.36, Gradle 8.8; Blockbench 5.2.1.

- Forge build produces a reobfuscated, standalone `napoleonic_cannon-1.0.0.jar`.
- **7 required Forge GameTests passed**: ammunition prerequisites; cancellation without consumption; complete loading, delayed ignition, actual projectile hit, durability and cooldown; stone collision with terrain preservation; accumulating canister pellets and default player protection; chambered-ammo lock and holstering cancellation; effect/particle serialization.
- Tests run in a bounded 12×5×12 structure with a complete floor and air reset. Targets remain inside the arena. A `test NO-SOURCE` task is not counted as gameplay validation.
- Editable `.bbmodel` opened and saved in the official checksum-verified Blockbench 5.2.1 desktop: 144 meshes, 13,336 vertices, embedded texture, nine part groups and three basic animation tracks.
- Native Forge parts contain 25,868 triangles. First-person two-handed support, rammer movement, powder/shot insertion, aiming, ignition, recoil and return inspected in an actual creative test world.
- Third-person model orientation was corrected; wheels remain below the bronze barrel and the item attaches to the player's hands.
- Generated RGBA VFX atlas is embedded unchanged. Flash is aligned with the firing direction, smoke expands and rises, sparks use generated imagery, and impact dust is visual only.
- All nine OGG Vorbis assets fully decoded without errors. The actual game sound engine initialized the OpenAL **Wave File Writer** and emitted the loading/firing sequence into a recording; the included demonstration uses that engine output. No physical speaker/headphone listening test was performed.
- Saber and cannon loaded together and both items rendered in the same integrated server/client. The published saber JAR was remapped with ForgeGradle for the named development runtime using a local init script; this does not add a saber dependency to the cannon release.
- JAR/ZIP CRCs, JSON resources, source paths, original VFX bytes and delivered checksums verified during packaging.

The reference field cannon is scaled into a handheld game weapon. Effects use standard translucent particle sheets rather than volumetric fluid simulation or PBR shaders. The Foley/report masters are original layered synthesis, not field recordings. Multi-client visual synchronization and compatibility with unrelated combat/animation mods were not tested. Cloud authentication/update requests were unavailable; offline local gameplay and the game tests completed.
