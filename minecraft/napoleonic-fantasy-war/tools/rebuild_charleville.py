import json, uuid, random, base64
from pathlib import Path
from PIL import Image, ImageDraw
root=Path('/workspace/napoleonic-fantasy-war-forge')
res=root/'src/main/resources/assets/napoleonic_fantasy'; texdir=res/'textures/item'; texdir.mkdir(parents=True,exist_ok=True)
# A hand-painted material atlas: wood, blued steel, brass, lock steel, leather, bone/flint.
im=Image.new('RGBA',(256,256),(0,0,0,0)); d=ImageDraw.Draw(im); rng=random.Random(1777)
materials=[((0,0,63,63),(96,42,18)),((64,0,127,63),(104,111,113)),((128,0,191,63),(181,133,47)),((192,0,255,63),(64,67,66)),((0,64,63,127),(37,25,17)),((64,64,127,127),(196,193,168))]
for box,c in materials:
 d.rectangle(box,fill=c+(255,))
 x0,y0=box[:2]
 for _ in range(160):
  x=x0+rng.randrange(64); y=y0+rng.randrange(64); delta=rng.randrange(-18,19)
  col=tuple(max(0,min(255,v+delta)) for v in c)+(255,)
  d.point((x,y),fill=col)
for y in range(5,62,8): d.line((2,y,61,y+rng.randrange(-2,3)),fill=(145,70,25,255),width=1)
for x in range(70,125,11): d.line((x,5,x+5,58),fill=(184,188,188,255),width=1)
im.save(texdir/'charleville_1777.png')

def cube(name,frm,to,uv=(0,0,16,16),rot=None):
 e={'from':list(frm),'to':list(to),'faces':{s:{'uv':list(uv),'texture':'#material'} for s in ('north','south','east','west','up','down')}}
 if rot: e['rotation']=rot
 e['name']=name; return e
wood=(0,0,4,4); steel=(4,0,8,4); brass=(8,0,12,4); lock=(12,0,16,4); leather=(0,4,4,8); flint=(4,4,8,8)
E=[]
# Stock: stepped/tapered hardwood profile, butt plate and cheek swell.
E += [cube('Butt stock',(0.5,6.1,6.0),(2.1,10.0,10.0),wood), cube('Butt heel',(0.35,8.9,6.2),(1.7,10.35,9.8),wood), cube('Stock wrist',(1.6,5.9,6.25),(4.2,9.3,9.75),wood), cube('Stock comb',(1.2,5.35,6.55),(3.0,6.2,9.45),wood), cube('Cheek piece',(2.05,5.5,5.72),(4.0,7.0,6.1),wood), cube('Lock bed',(3.0,6.3,5.95),(6.8,8.8,10.05),wood), cube('Forearm',(5.3,6.55,6.25),(10.1,9.45,9.75),wood), cube('Forearm tip',(9.2,6.75,6.45),(11.25,9.25,9.55),wood)]
# Brass butt plate, trigger guard, escutcheon and ramrod pipes.
E += [cube('Brass butt plate',(0.15,6.4,6.3),(0.55,10.05,9.7),brass), cube('Butt toe',(0.2,9.75,6.4),(1.65,10.25,9.6),brass), cube('Trigger guard',(2.35,9.0,6.45),(4.75,10.0,9.55),brass), cube('Trigger guard bow',(2.8,9.55,6.2),(4.3,10.2,6.85),brass), cube('Side plate',(3.15,6.0,5.65),(4.0,6.55,6.05),brass), cube('Rear ramrod pipe',(6.2,9.0,9.2),(6.75,9.55,9.7),brass), cube('Front ramrod pipe',(9.35,8.95,9.15),(9.9,9.5,9.7),brass)]
# Barrel and three barrel bands, each built from core + collar for readable silhouette.
E += [cube('Barrel breech',(7.1,6.55,6.55),(9.5,8.95,8.95),steel), cube('Barrel tube',(9.1,6.82,6.82),(14.75,8.68,8.68),steel), cube('Muzzle swell',(14.55,6.55,6.55),(15.55,8.95,8.95),steel), cube('Rear barrel band',(6.75,6.25,6.25),(7.2,9.25,9.25),brass), cube('Middle barrel band',(9.65,6.35,6.35),(10.08,9.15,9.15),brass), cube('Front barrel band',(12.45,6.4,6.4),(12.9,9.1,9.1),brass), cube('Muzzle rim',(15.15,6.35,6.35),(15.8,9.15,9.15),brass)]
# Lock mechanism on the visible side (north face): plate, pan, frizzen, cock, flint and screws.
E += [cube('Lock plate',(3.35,5.55,5.35),(6.35,8.45,5.85),lock), cube('Tumbler housing',(4.05,5.0,5.15),(5.15,6.0,5.55),lock), cube('Frizzen',(5.45,4.9,5.05),(6.0,6.8,5.55),steel), cube('Frizzen foot',(5.25,6.55,5.05),(5.8,7.25,5.55),steel), cube('Powder pan',(4.75,6.25,4.95),(5.4,6.7,5.45),brass), cube('Cock arm',(3.8,4.15,5.1),(4.35,5.85,5.55),steel), cube('Cock jaw',(3.5,3.85,5.05),(4.8,4.35,5.55),steel), cube('Flint',(3.85,3.6,5.0),(4.5,4.0,5.6),flint), cube('Lock screw',(4.55,7.1,4.85),(4.95,7.5,5.1),brass), cube('Lock screw rear',(5.55,7.6,4.85),(5.95,8.0,5.1),brass), cube('Trigger',(4.15,8.15,5.1),(4.45,9.3,5.5),steel)]
# Ramrod, visible under the forearm, with metal tip.
E += [cube('Ramrod wood',(6.3,9.35,9.48),(14.8,9.75,9.72),wood), cube('Ramrod tip',(14.55,9.28,9.38),(15.25,9.82,9.82),steel), cube('Ramrod button',(5.95,9.28,9.35),(6.45,9.82,9.85),brass)]
# Socket bayonet attachment and long triangular blade. It is an override model toggled by CustomModelData.
base=E.copy()
Ebay=E+[cube('Bayonet socket',(14.0,6.65,6.65),(15.6,9.0,9.0),steel),cube('Bayonet collar',(14.25,6.35,6.35),(14.7,9.3,9.3),brass),cube('Bayonet blade',(15.1,7.3,7.3),(16.0,8.3,8.3),flint),cube('Bayonet point',(15.9,7.55,7.55),(16.7,8.05,8.05),flint)]
def model(elements,bayonet=False):
 d={'textures':{'material':'napoleonic_fantasy:item/charleville_1777','particle':'napoleonic_fantasy:item/charleville_1777'},'elements':elements,'display':{'gui':{'rotation':[8,140,0],'scale':[1.25,1.25,1.25]},'ground':{'scale':[.7,.7,.7]},'firstperson_righthand':{'rotation':[0,90,0],'translation':[0,1,0],'scale':[.7,.7,.7]},'thirdperson_righthand':{'rotation':[0,90,0],'scale':[.65,.65,.65]}}}
 if not bayonet:d['overrides']=[{'predicate':{'custom_model_data':1},'model':'napoleonic_fantasy:item/charleville_1777_bayonet'}]
 return d
(res/'models/item/charleville_1777.json').write_text(json.dumps(model(base),indent=2))
(res/'models/item/charleville_1777_bayonet.json').write_text(json.dumps(model(Ebay,True),indent=2))
# Blockbench source for the detachable-bayonet mesh.
ids=[]; be=[]
for e in Ebay:
 u=str(uuid.uuid4());ids.append(u); be.append({'uuid':u,'type':'cube','name':e['name'],'from':e['from'],'to':e['to'],'origin':[8,8,8],'faces':{s:{'uv':[0,0,16,16],'texture':0} for s in ('north','south','east','west','up','down')}})
bb={'meta':{'format_version':'4.10','model_format':'free','box_uv':False},'name':'Charleville Modele 1777 detailed musket','resolution':{'width':256,'height':256},'elements':be,'outliner':[{'name':'charleville_1777','origin':[8,8,8],'uuid':str(uuid.uuid4()),'children':ids}],'textures':[{'name':'charleville_1777.png','id':'0','uuid':str(uuid.uuid4()),'source':'data:image/png;base64,'+base64.b64encode((texdir/'charleville_1777.png').read_bytes()).decode(),'width':256,'height':256,'uv_width':256,'uv_height':256}]}
(root/'art/source/charleville_1777.bbmodel').write_text(json.dumps(bb,indent=2))
print('detailed cubes',len(base),len(Ebay))
