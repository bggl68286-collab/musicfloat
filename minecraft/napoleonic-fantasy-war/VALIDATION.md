# Validation

- `gradle compileJava processResources`: passed.
- `gradle runGameTestServer`: passed, **15 required tests** (including musket load/fire and bayonet tests).
- `gradle runClient`: loaded the unified `napoleonic_fantasy` mod and all cannon, sabre, armor, musket item-model, texture and sound resources.
- Client smoke test: equipped all four armor pieces with `/item replace`, set daytime, switched to third person, and confirmed the fitted black jacket, red collar/cuffs, silver chest braids, gray breeches, boots and skull shako render on the player with the face visible.
- Blockbench sources: `art/source/prussian_skull_hussar_uniform.bbmodel` and `art/source/prussian_skull_hussar_slim.bbmodel` contain the 192-cube classic/Slim geometry, while `art/source/hussar_materials.png` is the shared material atlas.
- Charleville Blockbench source: `art/source/charleville_1777.bbmodel`; the item JSON and bayonet override use a wood/steel/brass material atlas and the historical lock, barrel bands, ramrod and socket bayonet silhouette.

The isolated client showed only expected vanilla offline authentication/OpenAL warnings; no mod model or missing-texture warnings remained after the final resource check.
