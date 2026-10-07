"""Convert the editable Blockbench sword to Forge OBJ; keep the generated VFX atlas intact."""
import json, shutil, math, struct, gzip
from pathlib import Path
import numpy as np

ROOT=Path(__file__).resolve().parents[1]
RES=ROOT/'src/main/resources'
A=RES/'assets/imperial_sabre'
SOURCE=ROOT/'art/source'
ART=SOURCE/'imperial_fx.png'
for directory in ['models/item','textures/item','textures/particle','particles','lang']:
    (A/directory).mkdir(parents=True,exist_ok=True)

model=json.loads((SOURCE/'eagle_sabre.bbmodel').read_text())
sabre_ids=set(model['outliner'][0]['children'])
lines=['# Imperial eagle-head sabre, converted from the editable bbmodel','mtllib eagle_sabre.mtl','usemtl gilded_sabre']
vertex_index=1;uv_index=1;normal_index=1;triangle_count=0
for element in model['elements']:
    if element['uuid'] not in sabre_ids:continue
    keys=list(element['vertices']);ind={key:vertex_index+i for i,key in enumerate(keys)}
    coords={key:np.array([(-v[0]*.5+8)/16,((3.4-v[1])*.5+8)/16,(v[2]*.5+8)/16]) for key,v in element['vertices'].items()}
    lines.append('g part_'+str(vertex_index))
    for key in keys:lines.append('v '+' '.join(f'{x:.8f}' for x in coords[key]))
    vertex_index+=len(keys)
    for face in element['faces'].values():
        ks=face['vertices'];uvs={}
        for key in ks:
            u,v=face['uv'][key]
            lines.append(f'vt {u/256:.8f} {1-v/256:.8f}');uvs[key]=uv_index;uv_index+=1
        # Forge's OBJ loader expects triangles or quads; triangulate all polygons consistently.
        for i in range(1,len(ks)-1):
            tri=[ks[0],ks[i],ks[i+1]]
            normal=np.cross(coords[tri[1]]-coords[tri[0]],coords[tri[2]]-coords[tri[0]])
            length=np.linalg.norm(normal)
            if length<1e-12:continue
            normal/=length
            lines.append('vn '+' '.join(f'{x:.8f}' for x in normal))
            lines.append('f '+' '.join(f'{ind[key]}/{uvs[key]}/{normal_index}' for key in tri))
            normal_index+=1;triangle_count+=1
(A/'models/item/eagle_sabre.obj').write_text('\n'.join(lines)+'\n')
(A/'models/item/eagle_sabre.mtl').write_text('newmtl gilded_sabre\nKa 1.0 1.0 1.0\nKd 1.0 1.0 1.0\nd 1.0\nmap_Kd imperial_sabre:item/sabre_palette\n')
shutil.copy2(SOURCE/'sabre_palette.png',A/'textures/item/sabre_palette.png')
shutil.copy2(ART,A/'textures/particle/imperial_fx.png')
(A/'particles/imperial_fx.json').write_text(json.dumps({'textures':['imperial_sabre:imperial_fx']}))
display={
 'gui':{'rotation':[0,0,-35],'translation':[1,-3,0],'scale':[.55,.55,.55]},
 'ground':{'rotation':[0,0,0],'translation':[0,3,0],'scale':[.5,.5,.5]},
 'fixed':{'rotation':[0,0,0],'translation':[0,-4,0],'scale':[.55,.55,.55]},
 'thirdperson_righthand':{'rotation':[0,90,55],'translation':[0,4,.5],'scale':[.8,.8,.8]},
 'thirdperson_lefthand':{'rotation':[0,-90,-55],'translation':[0,4,.5],'scale':[.8,.8,.8]},
 'firstperson_righthand':{'rotation':[0,-90,25],'translation':[1.13,4.5,1.13],'scale':[.72,.72,.72]},
 'firstperson_lefthand':{'rotation':[0,90,-25],'translation':[1.13,4.5,1.13],'scale':[.72,.72,.72]},
}
(A/'models/item/eagle_sabre.json').write_text(json.dumps({'loader':'forge:obj','model':'imperial_sabre:models/item/eagle_sabre.obj','automatic_culling':False,'shade_quads':True,'emissive_ambient':False,'flip_v':True,'textures':{'particle':'imperial_sabre:item/sabre_palette'},'display':display},indent=2))
langs={
 'zh_cn':{'item.imperial_sabre.eagle_sabre':'帝国鹰首军剑','tooltip.imperial_sabre.wave':'短按右键：黄金剑气 · 18 格 · 3 秒冷却','tooltip.imperial_sabre.nova':'按住右键 1 秒后松开：帝国加冕 · 12 格冲击 · 8 秒冷却','tooltip.imperial_sabre.style':'三维鹰首军剑 / 金色法阵 / 挥斩与蓄力动作'},
 'en_us':{'item.imperial_sabre.eagle_sabre':'Imperial Eagle Sabre','tooltip.imperial_sabre.wave':'Tap right-click: Golden Wave · 18 blocks · 3s cooldown','tooltip.imperial_sabre.nova':'Hold right-click for 1s, release: Imperial Coronation · 12-block blast · 8s cooldown','tooltip.imperial_sabre.style':'3D eagle hilt / golden seals / slash and charge animations'}
}
for lang,strings in langs.items():(A/'lang'/f'{lang}.json').write_text(json.dumps(strings,ensure_ascii=False,indent=2))
recipe={'type':'minecraft:crafting_shaped','pattern':[' NG',' SG','B  '],'key':{'N':{'item':'minecraft:netherite_ingot'},'G':{'item':'minecraft:gold_ingot'},'S':{'item':'minecraft:netherite_sword'},'B':{'item':'minecraft:blaze_rod'}},'result':{'item':'imperial_sabre:eagle_sabre'}}
rp=RES/'data/imperial_sabre/recipes';rp.mkdir(parents=True,exist_ok=True)
(rp/'eagle_sabre.json').write_text(json.dumps(recipe,indent=2))

# A minimal self-contained GameTest structure, avoiding assumptions about external templates.
def string(v):
    b=v.encode();return struct.pack('>H',len(b))+b
def tag(typ,name,payload):return bytes([typ])+string(name)+payload
def integer(name,val):return tag(3,name,struct.pack('>i',val))
def nbt_list(name,typ,items):return tag(9,name,bytes([typ])+struct.pack('>i',len(items))+b''.join(items))
def compound(tags):return b''.join(tags)+b'\x00'
palette=[compound([tag(8,'Name',string('minecraft:stone'))])]
blocks=[compound([integer('state',0),nbt_list('pos',3,[struct.pack('>i',v) for v in [x,0,z]])]) for x in range(5) for z in range(5)]
root=tag(10,'',compound([integer('DataVersion',3465),nbt_list('size',3,[struct.pack('>i',v) for v in [5,5,5]]),nbt_list('palette',10,palette),nbt_list('blocks',10,blocks),nbt_list('entities',10,[])]))
sp=RES/'data/imperial_sabre/structures';sp.mkdir(parents=True,exist_ok=True);(sp/'empty.nbt').write_bytes(gzip.compress(root,mtime=0))
print(f'OBJ: {vertex_index-1} vertices, {triangle_count} triangles; VFX atlas copied unchanged.')
