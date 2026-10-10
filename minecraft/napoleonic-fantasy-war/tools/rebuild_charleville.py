"""Rebuild the Charleville 1777 model with a 1:1 length profile and hand-ready transforms.
Run from the Forge project root. Geometry uses the real proportions: 1490 mm musket,
1147 mm barrel and 520 mm socket bayonet represented along the X axis.
"""
import base64, json, random, uuid
from pathlib import Path
from PIL import Image, ImageDraw, ImageEnhance

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / 'src/main/resources/assets/napoleonic_fantasy'
TEX = RES / 'textures/item'
TEX.mkdir(parents=True, exist_ok=True)

# Prefer the generated, flat four-quadrant albedo atlas when present; keep the
# procedural fallback so the model can still be regenerated offline.
generated = ROOT / 'art/source/charleville_material_generated.png'
if generated.exists():
    source = Image.open(generated).convert('RGBA')
    half_w, half_h = source.width // 2, source.height // 2
    q = [source.crop((0, 0, half_w, half_h)), source.crop((half_w, 0, source.width, half_h)),
         source.crop((0, half_h, half_w, source.height)), source.crop((half_w, half_h, source.width, source.height))]
    q = [p.resize((64, 64), Image.Resampling.LANCZOS) for p in q]
    im = Image.new('RGBA', (256, 256), (0, 0, 0, 255))
    for x, tile in enumerate((q[0], q[1], q[2], q[3], ImageEnhance.Brightness(q[0]).enhance(.42), ImageEnhance.Brightness(q[3]).enhance(1.35))):
        im.paste(tile, ((x % 4) * 64, (x // 4) * 64))
else:
    rng = random.Random(1777)
    im = Image.new('RGBA', (256, 256), (0, 0, 0, 0)); d = ImageDraw.Draw(im)
    tiles = [((0, 0, 63, 63), (94, 39, 15)), ((64, 0, 127, 63), (92, 103, 108)), ((128, 0, 191, 63), (184, 132, 43)), ((192, 0, 255, 63), (52, 57, 58)), ((0, 64, 63, 127), (28, 20, 14)), ((64, 64, 127, 127), (198, 193, 163))]
    for box, col in tiles:
        d.rectangle(box, fill=col + (255,)); x0, y0 = box[:2]; x1, y1 = box[2:]
        for _ in range(420):
            x, y = rng.randrange(x0, x1 + 1), rng.randrange(y0, y1 + 1); delta = rng.randrange(-20, 21)
            d.point((x, y), fill=tuple(max(0, min(255, v + delta)) for v in col) + (255,))
    for y in range(4, 62, 7): d.line((2, y, 61, y + rng.randrange(-3, 4)), fill=(142, 64, 22, 255), width=1)
    for x in range(68, 125, 9): d.line((x, 5, x + rng.randrange(-3, 4), 59), fill=(170, 180, 184, 255), width=1)
    for x in range(132, 190, 13): d.line((x, 8, x + 5, 59), fill=(230, 181, 72, 255), width=1)
    for i in range(8):
        x = 197 + rng.randrange(5, 53); y = 7 + rng.randrange(8, 52); d.line((x, y, x + rng.randrange(4, 13), y), fill=(96, 101, 101, 255), width=1)
im.save(TEX / 'charleville_1777.png')

WOOD, STEEL, BRASS, LOCK, LEATHER, FLINT = (0, 0, 4, 4), (4, 0, 8, 4), (8, 0, 12, 4), (12, 0, 16, 4), (0, 4, 4, 8), (4, 4, 8, 8)
SIDES = ('north', 'south', 'east', 'west', 'up', 'down')
def cube(name, frm, to, uv=WOOD, rotation=None):
    # Minecraft's vanilla item-model bounds end at 32. Keep the historical
    # 1490:520 length ratio while fitting the complete socket bayonet legally.
    frm = (frm[0] * .8, frm[1], frm[2])
    to = (to[0] * .8, to[1], to[2])
    e = {'from': list(frm), 'to': list(to), 'faces': {s: {'uv': list(uv), 'texture': '#material'} for s in SIDES}, 'name': name}
    if rotation:
        e['rotation'] = rotation
    return e

E = []
# Stock and forearm, stepped to the walnut profile of the 1777 Charleville.
E += [cube('Butt heel', (0.0, 8.7, 5.7), (2.7, 10.8, 10.3), WOOD),
      cube('Butt cheek', (0.35, 6.1, 5.8), (3.3, 9.7, 10.3), WOOD),
      cube('Butt toe', (0.15, 10.25, 6.1), (2.4, 10.85, 9.9), BRASS),
      cube('Brass butt plate', (-0.05, 6.35, 6.0), (0.55, 10.55, 10.0), BRASS),
      cube('Comb', (0.65, 5.3, 6.15), (4.5, 6.45, 9.85), WOOD),
      cube('Wrist', (2.7, 5.85, 5.95), (7.9, 9.6, 10.05), WOOD),
      cube('Cheek piece', (3.0, 5.25, 5.45), (6.4, 7.15, 5.95), WOOD),
      cube('Lock bed', (6.6, 6.0, 5.8), (13.0, 9.35, 10.15), WOOD),
      cube('Forearm rear', (11.5, 6.3, 6.1), (17.5, 9.2, 9.85), WOOD),
      cube('Forearm front', (16.4, 6.55, 6.35), (20.0, 9.0, 9.6), WOOD)]
# Brass furniture, trigger guard and sling attachments.
E += [cube('Trigger guard rail', (5.1, 8.9, 6.15), (8.2, 9.45, 9.85), BRASS),
      cube('Trigger guard bow', (5.55, 9.2, 5.95), (7.65, 10.2, 6.55), BRASS),
      cube('Escutcheon', (4.6, 7.1, 5.35), (5.7, 7.65, 5.75), BRASS),
      cube('Rear sling swivel', (4.1, 10.0, 8.9), (5.2, 10.45, 9.65), BRASS),
      cube('Front sling swivel', (17.0, 9.0, 9.35), (18.0, 9.55, 9.95), BRASS)]
# Barrel: stepped breech, round tube, muzzle swell and three historically visible bands.
E += [cube('Breech plug', (11.9, 6.25, 6.25), (14.0, 9.15, 9.15), STEEL),
      cube('Barrel rear', (13.5, 6.55, 6.55), (19.6, 8.85, 8.85), STEEL),
      cube('Barrel tube', (19.0, 6.72, 6.72), (28.8, 8.68, 8.68), STEEL),
      cube('Muzzle swell', (28.25, 6.45, 6.45), (30.2, 8.95, 8.95), STEEL),
      cube('Rear barrel band', (13.0, 6.1, 6.1), (13.65, 9.3, 9.3), BRASS),
      cube('Middle barrel band', (18.0, 6.25, 6.25), (18.7, 9.15, 9.15), BRASS),
      cube('Front barrel band', (24.0, 6.3, 6.3), (24.7, 9.1, 9.1), BRASS),
      cube('Muzzle rim', (29.4, 6.25, 6.25), (30.65, 9.2, 9.2), BRASS),
      cube('Muzzle bore', (30.25, 6.75, 6.75), (30.8, 8.7, 8.7), LOCK)]
# Lock plate and working flintlock parts, all on the visible north side.
E += [cube('Lock plate', (6.8, 5.35, 5.2), (12.6, 8.55, 5.85), LOCK),
      cube('Lock tail', (11.3, 6.0, 5.15), (13.3, 7.35, 5.75), LOCK),
      cube('Tumbler boss', (8.0, 5.0, 4.95), (9.45, 6.25, 5.45), LOCK),
      cube('Frizzen foot', (10.25, 6.35, 4.95), (11.15, 7.25, 5.45), STEEL),
      cube('Frizzen face', (10.65, 4.65, 4.9), (11.35, 6.7, 5.5), STEEL),
      cube('Powder pan', (8.8, 6.15, 4.8), (10.55, 6.8, 5.4), BRASS),
      cube('Cock arm', (7.55, 3.9, 4.8), (8.25, 5.75, 5.45), STEEL),
      cube('Cock jaw', (7.2, 3.55, 4.75), (8.9, 4.25, 5.4), STEEL),
      cube('Flint', (7.6, 3.25, 4.7), (8.45, 3.85, 5.45), FLINT),
      cube('Lock screw front', (8.15, 7.0, 4.65), (8.7, 7.55, 5.0), BRASS),
      cube('Lock screw rear', (11.0, 7.35, 4.65), (11.55, 7.9, 5.0), BRASS),
      cube('Trigger', (6.0, 8.0, 5.0), (6.5, 9.2, 5.5), STEEL)]
# Ramrod and pipes below the forearm.
E += [cube('Ramrod wood', (10.5, 9.35, 9.62), (28.6, 9.78, 10.0), WOOD),
      cube('Ramrod button', (10.0, 9.25, 9.5), (10.9, 9.85, 10.1), BRASS),
      cube('Ramrod tip', (28.2, 9.25, 9.45), (29.4, 9.88, 10.1), STEEL),
      cube('Ramrod pipe rear', (13.2, 9.0, 9.25), (14.1, 9.55, 9.85), BRASS),
      cube('Ramrod pipe front', (23.9, 8.9, 9.18), (24.8, 9.45, 9.8), BRASS)]
BASE = list(E)
# Historically correct socket bayonet: socket/collar and a long triangular steel blade.
E += [cube('Bayonet socket', (28.6, 6.6, 6.6), (30.7, 9.0, 9.0), STEEL),
      cube('Bayonet locking ring', (28.8, 6.3, 6.3), (29.5, 9.3, 9.3), BRASS),
      cube('Bayonet blade ricasso', (30.0, 7.1, 7.1), (32.0, 8.35, 8.35), STEEL),
      cube('Bayonet blade', (31.5, 7.35, 7.35), (38.5, 8.1, 8.1), STEEL),
      cube('Bayonet point', (38.0, 7.58, 7.58), (39.4, 7.88, 7.88), STEEL)]

def model(elements, bayonet=False):
    d = {'textures': {'material': 'napoleonic_fantasy:item/charleville_1777', 'particle': 'napoleonic_fantasy:item/charleville_1777'},
         'elements': elements,
         'display': {
             'gui': {'rotation': [8, 140, 0], 'scale': [0.92, 0.92, 0.92]},
             'ground': {'rotation': [0, 0, 0], 'scale': [0.9, 0.9, 0.9]},
             'firstperson_righthand': {'rotation': [0, 90, 0], 'translation': [0, 1, 0], 'scale': [0.88, 0.88, 0.88]},
             'firstperson_lefthand': {'rotation': [0, -90, 0], 'translation': [0, 1, 0], 'scale': [0.88, 0.88, 0.88]},
             # The barrel runs screen-left/right in third person; Y rotation made it disappear into the body.
             'thirdperson_righthand': {'rotation': [0, 0, 315], 'translation': [1.5, 2.0, 0], 'scale': [1.25, 1.25, 1.25]},
             'thirdperson_lefthand': {'rotation': [0, 0, 315], 'translation': [1.5, 2.0, 0], 'scale': [1.25, 1.25, 1.25]}}}
    if not bayonet:
        d['overrides'] = [{'predicate': {'custom_model_data': 1}, 'model': 'napoleonic_fantasy:item/charleville_1777_bayonet'}]
    return d

(RES / 'models/item/charleville_1777.json').write_text(json.dumps(model(BASE), indent=2))
(RES / 'models/item/charleville_1777_bayonet.json').write_text(json.dumps(model(E, True), indent=2))

# Portable Blockbench source with the same named components and embedded atlas.
ids, elems = [], []
for e in E:
    ident = str(uuid.uuid4()); ids.append(ident)
    elems.append({'uuid': ident, 'type': 'cube', 'name': e['name'], 'from': e['from'], 'to': e['to'], 'origin': [8, 8, 8],
                  'faces': {s: {'uv': [0, 0, 16, 16], 'texture': 0} for s in SIDES}})
bb = {'meta': {'format_version': '4.10', 'model_format': 'free', 'box_uv': False},
      'name': 'Charleville Modele 1777 1:1 detailed musket', 'resolution': {'width': 256, 'height': 256},
      'elements': elems, 'outliner': [{'name': 'charleville_1777', 'origin': [8, 8, 8], 'uuid': str(uuid.uuid4()), 'children': ids}],
      'textures': [{'name': 'charleville_1777.png', 'id': '0', 'uuid': str(uuid.uuid4()),
                    'source': 'data:image/png;base64,' + base64.b64encode((TEX / 'charleville_1777.png').read_bytes()).decode(),
                    'width': 256, 'height': 256, 'uv_width': 256, 'uv_height': 256}]}
(ROOT / 'art/source/charleville_1777.bbmodel').write_text(json.dumps(bb, indent=2))
print('rebuilt Charleville profile: %d rifle cubes, %d bayonet cubes' % (len(BASE), len(E)))
