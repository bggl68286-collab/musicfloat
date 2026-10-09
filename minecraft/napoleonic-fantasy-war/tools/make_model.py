"""Author an editable Blockbench mesh and export its articulated parts to Forge OBJ."""
from pathlib import Path
import math, json, uuid, base64, shutil
import numpy as np
from PIL import Image
from scipy.ndimage import gaussian_filter

ROOT=Path(__file__).resolve().parents[1]
ART=ROOT/'art/source'; ART.mkdir(parents=True,exist_ok=True)
AS=ROOT/'src/main/resources/assets/napoleonic_fantasy'
for d in ['models/item','textures/item','textures/particle','particles','lang']:(AS/d).mkdir(parents=True,exist_ok=True)
rng=np.random.default_rng(1804)
h=w=512; yy,xx=np.mgrid[0:h,0:w]
n=gaussian_filter(rng.normal(size=(h,w)),7);n=(n-n.min())/(n.max()-n.min())
fine=rng.normal(0,3,(h,w,1))
bronze=np.zeros((h,w,3))+[153,116,54]
bronze+=((n-.5)*65)[...,None]+fine
patina=np.clip((gaussian_filter(rng.random((h,w)),22)-.495)*95,0,.55)[...,None]
bronze=bronze*(1-patina)+np.array([53,94,75])*patina
grain=np.sin(xx*.19+np.sin(yy*.035)*3+n*6)+np.sin(xx*.75+n*8)*.28
wood=np.zeros_like(bronze)+[113,80,44]+grain[...,None]*13+(n-.5)[...,None]*36+fine
olive=np.zeros_like(bronze)+[70,76,41]+(n-.5)[...,None]*35+fine
wear=(n>.73)[...,None]*.65;olive=olive*(1-wear)+wood*wear
iron=np.zeros_like(bronze)+[46,46,42]+(n-.5)[...,None]*28+fine
rust=np.clip((n-.65)*2,0,.35)[...,None];iron=iron*(1-rust)+np.array([103,54,29])*rust
atlas=np.vstack([np.hstack([bronze,olive]),np.hstack([iron,wood])])
Image.fromarray(np.uint8(np.clip(atlas,0,255))).save(ART/'cannon_materials.png')
shutil.copy2(ART/'cannon_materials.png',AS/'textures/item/cannon_materials.png')
els=[];groups={k:[] for k in ['carriage','barrel','wheel_left','wheel_right','ramrod','ball','powder','canister','wick']}
mat={'bronze':0,'olive':1,'iron':2,'wood':3}

def mesh(name,part,v,faces,material='bronze',smooth=False,uvs=None):
    uid=str(uuid.uuid5(uuid.NAMESPACE_URL,'cannon/'+name));vs={f'v{i}':list(map(float,p)) for i,p in enumerate(v)}
    fs={}
    tile=mat[material];tx=(tile%2)*512;ty=(tile//2)*512
    for i,face in enumerate(faces):
        keys=[f'v{j}' for j in face]
        uv={}
        for j,k in enumerate(keys):
            u,t=uvs[i][j] if uvs else [(0,0),(1,0),(1,1),(0,1)][j%4]
            uv[k]=[tx+4+u*504,ty+4+t*504]
        fs[str(i)]={'vertices':keys,'uv':uv,'texture':0}
    e={'name':name,'type':'mesh','uuid':uid,'origin':[0,0,0],'rotation':[0,0,0],'color':0,'visibility':part not in ['ramrod','ball','powder','canister','wick'],'export':True,'vertices':vs,'faces':fs,'smooth':smooth}
    els.append(e);groups[part].append(uid)

def box(name,part,center,size,material='olive',basis=None):
    c=np.array(center);b=np.eye(3) if basis is None else np.array(basis).T
    v=[c+b@np.array([sx*size[0]/2,sy*size[1]/2,sz*size[2]/2]) for sx,sy,sz in [(-1,-1,-1),(1,-1,-1),(1,1,-1),(-1,1,-1),(-1,-1,1),(1,-1,1),(1,1,1),(-1,1,1)]]
    mesh(name,part,v,[[0,3,2,1],[4,5,6,7],[0,1,5,4],[3,7,6,2],[0,4,7,3],[1,2,6,5]],material)

def lathe(name,part,profile,center=(0,0,0),axis='z',material='bronze',segments=64,caps=True):
    v=[];faces=[];uvs=[];c=np.array(center)
    for a,r in profile:
        for j in range(segments):
            t=j*math.tau/segments;p=np.array([r*math.cos(t),r*math.sin(t),a])
            if axis=='x':p=np.array([a,p[0],p[1]])
            if axis=='y':p=np.array([p[0],a,p[1]])
            v.append(c+p)
    for i in range(len(profile)-1):
        for j in range(segments):
            k=(j+1)%segments;faces.append([i*segments+j,i*segments+k,(i+1)*segments+k,(i+1)*segments+j])
            uvs.append([(j/segments,i/(len(profile)-1)),((j+1)/segments,i/(len(profile)-1)),((j+1)/segments,(i+1)/(len(profile)-1)),(j/segments,(i+1)/(len(profile)-1))])
    if caps:
        faces.extend([list(range(segments-1,-1,-1)),list(range((len(profile)-1)*segments,len(profile)*segments))])
        uvs.extend([[(.5+.48*math.cos(j*math.tau/segments),.5+.48*math.sin(j*math.tau/segments)) for j in range(segments-1,-1,-1)],[(.5+.48*math.cos(j*math.tau/segments),.5+.48*math.sin(j*math.tau/segments)) for j in range(segments)]])
    mesh(name,part,v,faces,material,True,uvs)

def torus(name,part,center,radius,tube,axis='z',material='bronze',seg=64,cross=8):
    v=[];f=[];uv=[]
    for i in range(seg):
        a=math.tau*i/seg
        for j in range(cross):
            t=math.tau*j/cross;r=radius+tube*math.cos(t);p=np.array([r*math.cos(a),r*math.sin(a),tube*math.sin(t)])
            if axis=='x':p=np.array([p[2],p[0],p[1]])
            if axis=='y':p=np.array([p[0],p[2],p[1]])
            v.append(np.array(center)+p)
    for i in range(seg):
        for j in range(cross):
            f.append([i*cross+j,((i+1)%seg)*cross+j,((i+1)%seg)*cross+(j+1)%cross,i*cross+(j+1)%cross]);uv.append([(i/seg,j/cross),((i+1)/seg,j/cross),((i+1)/seg,(j+1)/cross),(i/seg,(j+1)/cross)])
    mesh(name,part,v,f,material,True,uv)

# Hollow, tapered bronze barrel; 64 segments keep the muzzle genuinely round.
profile=[(-25,3.0),(-24.7,3.3),(-24,3.3),(-23.5,2.9),(-22,2.6),(-18,2.55),(-14,2.65),(-10,2.85),(-6,3.08),(0,3.45),(5,3.6),(8,3.65),(10,3.3),(11,2.9),(12,1.9)]
lathe('Bronze tapered tube','barrel',profile,caps=False)
lathe('Dark recessed bore','barrel',[(-25,1.75),(-24,1.74),(-19,1.69),(-10,1.6)],material='iron',caps=False)
lathe('Muzzle annular lip','barrel',[(-25,1.75),(-25,3.0)],caps=False)
lathe('Closed breech','barrel',[(10,3.3),(12,1.9),(12.5,.85),(13.2,.85)],caps=True)
lathe('Cascabel knob','barrel',[(12.8,.85),(13.3,1.2),(14,1.6),(14.9,1.65),(15.7,1.2),(16.2,.1)],segments=48)
for z,r in [(-23.8,3.3),(-18,2.57),(-4,3.21),(6.4,3.65),(9.1,3.55)]:torus('Cast reinforcement '+str(z),'barrel',(0,0,z),r,.18)
for side in [-1,1]:
    lathe('Trunnion '+str(side),'barrel',[(side*3,1.45),(side*5.8,1.45)],axis='x',segments=40)
    box('Trunnion bearing '+str(side),'carriage',(side*5.05,-.9,0),(2.2,1.6,4.1),'iron')
    torus('Dolphin lift arch '+str(side),'barrel',(side*1.25,3.0,4),1.25,.35,axis='x',seg=32)
box('Foundry insignia tablet','barrel',(0,3.65,7.0),(2,.15,2.5),'bronze')
for x in [-.6,.6]:box('Insignia stroke '+str(x),'barrel',(x,3.76,7),(.18,.08,1.8),'iron')
box('Insignia diagonal','barrel',(0,3.76,7),(.16,.08,2.1),'iron',basis=[[.86,0,.5],[0,1,0],[-.5,0,.86]])
lathe('Touch hole collar','barrel',[(0,.48),(.4,.38)],center=(0,3.5,9),axis='y',material='iron',segments=20)

# Timber cheeks, iron straps, crossmembers, elevating wedge, trail and tow eye.
for side in [-1,1]:
    box('Front cheek '+str(side),'carriage',(side*4.6,-5.1,0),(2.4,7.7,13),'olive')
    box('Sloped trail cheek '+str(side),'carriage',(side*3.9,-11.3,16.5),(2.2,4,24),'olive',basis=[[1,0,0],[0,.969,.247],[0,-.247,.969]])
    for z in [-4,3,12,21]:
        box('Iron strap '+str((side,z)),'carriage',(side*5.85,-5.2-(max(z,0)*.31),z),(.28,4.8,1.15),'iron')
        for dy in [-1.4,1.4]:lathe('Strap bolt '+str((side,z,dy)),'carriage',[(0,.32),(.25,.32)],center=(side*6,-5.2-max(z,0)*.31+dy,z),axis='x',material='iron',segments=12)
for z,y in [(-4,-7),(4,-8),(14,-11.5),(25,-15.3)]:box('Carriage crossmember '+str(z),'carriage',(0,y,z),(11,2.7,2.7),'olive')
box('Iron axle','carriage',(0,-8.5,1),(28.5,1.5,1.5),'iron')
box('Elevation wedge','carriage',(0,-3.4,10),(5.8,2.2,5),'wood')
lathe('Elevation screw','carriage',[(-7,.35),(-2,.35)],center=(0,0,11),axis='y',material='iron',segments=20)
torus('Trail tow eye','carriage',(0,-16,28),1.6,.4,axis='y',material='iron',seg=36)
for side in [-1,1]:
    for k in range(9):torus('Safety chain '+str((side,k)),'carriage',(side*(6.2+k*.52),-7-math.sin(k/8*math.pi)*2,3),.45,.13,axis='x' if k%2 else 'z',material='iron',seg=14,cross=6)

for side,part in [(-1,'wheel_left'),(1,'wheel_right')]:
    x=side*13.9;c=(x,-8.5,1)
    torus('Iron tyre '+part,part,c,11.65,.38,axis='x',material='iron')
    torus('Wooden felloe '+part,part,c,11.0,.73,axis='x',material='olive',cross=10)
    lathe('Wheel hub '+part,part,[(-1.4,1.8),(1.4,1.8)],center=c,axis='x',material='olive',segments=40)
    lathe('Hub iron cap '+part,part,[(-1.6,1.1),(1.6,1.1)],center=c,axis='x',material='iron',segments=32)
    for k in range(12):
        a=math.tau*k/12;rad=np.array([0,math.cos(a),math.sin(a)]);tan=np.array([0,-math.sin(a),math.cos(a)])
        box('Spoke '+str((side,k)),part,np.array(c)+rad*6.35,(1.25,9.1,1.15),'olive',basis=[[1,0,0],rad,tan])
        lathe('Tyre rivet '+str((side,k)),part,[(0,.22),(.22,.22)],center=np.array(c)+rad*11.55+np.array([side*.45,0,0]),axis='x',material='iron',segments=10)

lathe('Two ended ash rammer','ramrod',[(-36,.35),(-3,.35)],material='wood',segments=24)
lathe('Wood rammer head','ramrod',[(-38,1.2),(-36,1.2)],material='wood',segments=32)
lathe('Wool sponge head','ramrod',[(-4,1.2),(-3,1.5),(-1,1.5),(0,1.1)],material='wood',segments=32)
for z in [-3.8,-2.9,-2.1,-1.3]:torus('Sponge binding '+str(z),'ramrod',(0,0,z),1.35,.18,material='iron',seg=32)

lathe('Solid round iron ball','ball',[(-1.5,.01),(-1.3,.75),(-1,1.12),(-.5,1.41),(0,1.5),(.5,1.41),(1,1.12),(1.3,.75),(1.5,.01)],material='iron',segments=40)
lathe('Paper powder cartridge','powder',[(-2,1.22),(-1.7,1.4),(1.7,1.4),(2,1.15)],material='wood',segments=32)
lathe('Canister tin','canister',[(-2,1.45),(2,1.45)],material='iron',segments=40)
for z in [-1.9,1.9]:torus('Canister seam '+str(z),'canister',(0,0,z),1.45,.1,material='bronze',seg=40)
lathe('Slow match','wick',[(-1,.13),(1,.13)],material='wood',segments=12)

origins={'wheel_left':[-13.9,-8.5,1],'wheel_right':[13.9,-8.5,1],'barrel':[0,0,0]}
out=[{'name':k,'uuid':str(uuid.uuid5(uuid.NAMESPACE_URL,'cannon/group/'+k)),'origin':origins.get(k,[0,0,0]),'rotation':[0,0,0],'children':ids,'visibility':True,'export':True,'isOpen':False} for k,ids in groups.items()]
tex={'path':'','name':'cannon_materials.png','folder':'','namespace':'','id':'0','particle':False,'render_mode':'default','render_sides':'double','visible':True,'internal':True,'saved':True,'uuid':str(uuid.uuid4()),'width':1024,'height':1024,'uv_width':1024,'uv_height':1024,'source':'data:image/png;base64,'+base64.b64encode((ART/'cannon_materials.png').read_bytes()).decode()}
animations=[]
for name,length,bone,channel,points in [('fire_recoil',.8,'barrel','position',[(0,[0,0,0]),(.08,[0,0,2.7]),(.3,[0,0,1.1]),(.8,[0,0,0])]),('clean_bore',1.6,'ramrod','position',[(0,[0,0,-25]),(.4,[0,0,-13]),(.8,[0,0,-25]),(1.2,[0,0,-13]),(1.6,[0,0,-25])]),('ram_shot',1.6,'ramrod','position',[(0,[0,0,-25]),(.5,[0,0,-12]),(1,[0,0,-12]),(1.6,[0,0,-25])])]:
    group=next(g for g in out if g['name']==bone)
    animations.append({'uuid':str(uuid.uuid4()),'name':name,'loop':'once','length':length,'snapping':20,'animators':{group['uuid']:{'name':bone,'type':'bone','keyframes':[{'channel':channel,'data_points':[dict(zip(['x','y','z'],map(str,value)))],'uuid':str(uuid.uuid4()),'time':t,'interpolation':'linear'} for t,value in points]}}})
model={'meta':{'format_version':'4.10','model_format':'free','box_uv':False},'name':'Gribeauval 12-pounder handheld adaptation','model_identifier':'napoleonic_fantasy','visible_box':[8,8,0],'variable_placeholders':'','resolution':{'width':1024,'height':1024},'elements':els,'outliner':out,'textures':[tex],'animations':animations}
(ART/'gribeauval_12pdr.bbmodel').write_text(json.dumps(model,separators=(',',':')))

# Game models use the same vertices, UVs and animated group boundaries as Blockbench.
total=0
for part,ids in groups.items():
    lines=['# Exported from gribeauval_12pdr.bbmodel','mtllib cannon.mtl','usemtl aged_cannon'];vi=ti=ni=1
    for e in els:
        if e['uuid'] not in ids:continue
        coords={k:np.array(v)/16+.5 for k,v in e['vertices'].items()};keys=list(coords);idx={k:vi+i for i,k in enumerate(keys)}
        lines.append('g '+e['name'].replace(' ','_'));lines.extend('v '+' '.join(f'{a:.7f}' for a in coords[k]) for k in keys);vi+=len(keys)
        norms={k:np.zeros(3) for k in keys}
        for face in e['faces'].values():
            ks=face['vertices'];normal=np.cross(coords[ks[1]]-coords[ks[0]],coords[ks[2]]-coords[ks[0]])
            for k in ks:norms[k]+=normal
        for face in e['faces'].values():
            ks=face['vertices'];ui={}
            for k in ks:
                u,v=face['uv'][k];lines.append(f'vt {u/1024:.7f} {1-v/1024:.7f}');ui[k]=ti;ti+=1
            for j in range(1,len(ks)-1):
                tri=[ks[0],ks[j],ks[j+1]];n=np.cross(coords[tri[1]]-coords[tri[0]],coords[tri[2]]-coords[tri[0]])
                if np.linalg.norm(n)<1e-10:continue
                normals=[]
                for k in tri:
                    t=norms[k] if e['smooth'] else n;length=np.linalg.norm(t);t=t/length if length>1e-10 else n/np.linalg.norm(n)
                    lines.append('vn '+' '.join(f'{a:.7f}' for a in t));normals.append(ni);ni+=1
                lines.append('f '+' '.join(f'{idx[k]}/{ui[k]}/{normal}' for k,normal in zip(tri,normals)));total+=1
    (AS/f'models/item/{part}.obj').write_text('\n'.join(lines)+'\n')
    (AS/f'models/item/{part}.json').write_text(json.dumps({'loader':'forge:obj','model':f'napoleonic_fantasy:models/item/{part}.obj','automatic_culling':False,'shade_quads':True,'flip_v':True,'textures':{'particle':'napoleonic_fantasy:item/cannon_materials'}}))
(AS/'models/item/cannon.mtl').write_text('newmtl aged_cannon\nKa 1 1 1\nKd 1 1 1\nd 1\nmap_Kd napoleonic_fantasy:item/cannon_materials\n')
display={ctx:{'rotation':[0,0,0],'translation':[0,0,0],'scale':[s,s,s]} for ctx,s in [('firstperson_righthand',.48),('firstperson_lefthand',.48),('thirdperson_righthand',.48),('thirdperson_lefthand',.48),('ground',.35),('fixed',.35)]}
display['gui']={'rotation':[18,-35,0],'translation':[0,2,0],'scale':[.29,.29,.29]}
for k in ['thirdperson_righthand','thirdperson_lefthand']:display[k]['rotation']=[-154,180,180]
(AS/'models/item/hand_cannon.json').write_text(json.dumps({'parent':'builtin/entity','textures':{'particle':'napoleonic_fantasy:item/cannon_materials'},'display':display},indent=2))
for item,part,s in [('round_shot','ball',3),('canister_shot','canister',2)]:
    item_model=json.loads((AS/f'models/item/{part}.json').read_text());item_model['display']={ctx:{'rotation':[0,0,0],'translation':[0,0,0],'scale':[s,s,s]} for ctx in ['gui','ground','fixed','firstperson_righthand','firstperson_lefthand','thirdperson_righthand','thirdperson_lefthand']};(AS/f'models/item/{item}.json').write_text(json.dumps(item_model))
shutil.copy2(ART/'cannon_fx.png',AS/'textures/particle/cannon_fx.png')
(AS/'particles/cannon_fx.json').write_text(json.dumps({'textures':['napoleonic_fantasy:cannon_fx']}))
print(f'{len(els)} meshes, {sum(len(e["vertices"]) for e in els)} vertices, {total} triangles; editable Blockbench source and 9 articulated game parts exported.')
