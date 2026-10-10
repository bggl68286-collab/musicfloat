# Validation

- `gradle compileJava processResources`: passed.
- `gradle runGameTestServer`: passed, **15 required tests** (including musket load/fire and bayonet tests).
- `gradle runClient`: loaded the unified `napoleonic_fantasy` mod and all cannon, sabre, armor, musket item-model, texture and sound resources.
- Client smoke test: equipped all four armor pieces with `/item replace`, set daytime, switched to third person, and confirmed the fitted black jacket, red collar/cuffs, silver chest braids, gray breeches, boots and skull shako render on the player with the face visible.
- Blockbench sources: `art/source/prussian_skull_hussar_uniform.bbmodel` and `art/source/prussian_skull_hussar_slim.bbmodel` contain the 192-cube classic/Slim geometry, while `art/source/hussar_materials.png` is the shared material atlas.
- Charleville Blockbench source: `art/source/charleville_1777.bbmodel`; the detailed item JSON contains 36 rifle cubes and the detachable-bayonet override contains 40, covering the stock, lock plate, cock, frizzen, pan, trigger, barrel bands, ramrod and socket bayonet.
- The Charleville atlas is 256×256 with item-model UVs kept in Minecraft's 0–16 range; the previous noisy/black material sampling is fixed. First-person and third-person smoke screenshots were checked after the rebuild.
- `tools/make_audio.py` produces 15 original mono Vorbis effects and lossless WAV masters in `art/audio/`. The muzzle flash, flint spark, dense smoke and bayonet glint were designed from a generated VFX reference sheet and implemented as layered in-game particles.

The isolated client showed only expected vanilla offline authentication/OpenAL warnings; no mod model or missing-texture warnings remained after the final resource check.
