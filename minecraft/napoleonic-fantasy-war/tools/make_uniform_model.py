"""One geometry source for Forge armor, 3D inventory icons and editable Blockbench.
Coordinates are vanilla humanoid pixels. All cloth parts use bounded UV tiles.
"""
from pathlib import Path
import json, uuid, base64, random, math
from PIL import Image
ROOT=Path(__file__).resolve().parents[1]
RES=ROOT/'src/main/resources/assets/napoleonic_fantasy'
ART=ROOT/'art/source'; ART.mkdir(parents=True,exist_ok=True)
COLORS={'wool':(23,25,28),'red':(137,23,30),'silver':(192,193,184),'fur':(176,177,165),'grey':(130,131,122),'leather':(26,25,24),'gold':(184,139,65),'bone':(226,225,208),'dark':(8,9,10)}
# A new procedural material atlas. Each cube unwrap fits inside one 64px tile.
im=Image.new('RGBA',(256,256),(0,0,0,0)); rng=random.Random(42)
for i,(mat,color) in enumerate(COLORS.items()):
 x0=i%4*64;y0=i//4*64
 for y in range(64):
  for x in range(64):
   n=rng.randrange(-5,6) if mat in ['wool','fur','grey'] else rng.randrange(-2,3)
   if mat in ['wool','grey']: n+=(x+y)%2
   im.putpixel((x0+x,y0+y),tuple(max(0,min(255,c+n)) for c in color)+(255,))
texture=RES/'textures/models/armor/hussar_materials.png';im.save(texture);im.save(ART/'hussar_materials.png')
parts=[]
def box(slot,bone,name,x,y,z,w,h,d,mat):
 assert min(w,h,d)>0
 parts.append(dict(slot=slot,bone=bone,name=name,xyz=[x,y,z],size=[w,h,d],mat=mat))
# Hat rests above eyebrows. The head root is empty: never render the vanilla head cube.
box('HEAD','head','Shako crown',-4.25,-13,-4.25,8.5,5.65,8.5,'wool')
box('HEAD','head','Crown top seam',-4.35,-13.15,-4.35,8.7,.25,8.7,'leather')
box('HEAD','head','Lower leather band',-4.35,-7.7,-4.35,8.7,.42,8.7,'leather')
box('HEAD','head','Peaked visor',-4.3,-7.35,-5.4,8.6,.24,2.6,'leather')
box('HEAD','head','Red rear panel',.45,-12.95,4.26,3.7,5.2,.08,'red')
box('HEAD','head','Silver cockade',-.42,-13.65,-4.43,.84,1.2,.22,'silver')
box('HEAD','head','Cockade centre',-.2,-13.4,-4.57,.4,.7,.09,'dark')
# Raised skull, eyes and crossed bones, at the front of the shako.
box('HEAD','head','Skull forehead',-.78,-11.7,-4.5,1.56,.85,.22,'bone')
box('HEAD','head','Skull cheek',-.62,-10.9,-4.53,1.24,.38,.22,'bone')
box('HEAD','head','Skull jaw',-.4,-10.54,-4.54,.8,.4,.2,'bone')
for x in [-.53,.16]:box('HEAD','head','Eye socket',x,-11.22,-4.74,.36,.36,.05,'dark')
box('HEAD','head','Nose cavity',-.1,-10.77,-4.76,.2,.24,.05,'dark')
for i in range(9):
 x=-1.13+i*.25
 for direction in [-1,1]:box('HEAD','head','Crossbone',x,-9.75+direction*(i-4)*.11,-4.53,.32,.18,.17,'bone')
for side in [-1,1]:
 box('HEAD','head','Silver side button',side*4.39-.12,-7.8,-.4,.24,.5,.5,'silver')
 for i in range(7):box('HEAD','head','Braided chin cord',side*4.32-.09,-7.25+i*.22,-.3-i*.28,.18,.25,.2,'silver')
# Fitted jacket. Sleeve origin and width differ for classic/slim players at export.
box('CHEST','body','Tailored black dolman',-4.2,-.1,-2.2,8.4,12.15,4.4,'wool')
box('CHEST','body','Collar back',-3.8,-1,1.85,7.6,1.8,.42,'red')
for s in [-1,1]:box('CHEST','body','Collar side',s*2.85-.4,-.7,-1.95,.8,1.5,4,'red')
box('CHEST','body','Collar front',-2.85,0,-2.33,5.7,.85,.22,'red')
box('CHEST','body','Collar silver edge',-2.9,.65,-2.47,5.8,.13,.1,'silver')
for i in range(10):
 y=1.65+i*.72
 for s in [-1,1]:
  x=-2.8 if s<0 else .18
  box('CHEST','body','Silver chest braid',x,y,-2.39,2.62,.13,.16,'silver')
  box('CHEST','body','Braid button',s*2.95-.13,y-.1,-2.48,.26,.31,.19,'silver')
 box('CHEST','body','Central fastening',-.13,y-.07,-2.52,.26,.26,.18,'silver')
box('CHEST','body','Waist sash',-4.27,9.35,-2.28,8.54,1.2,4.56,'wool')
for i in range(4):box('CHEST','body','Sash silver cords',-4.28,9.45+i*.25,-2.42,8.56,.10,.13,'silver')
box('CHEST','body','Leather belt',-4.3,10.7,-2.31,8.6,.6,4.62,'leather')
box('CHEST','body','Belt buckle',-.55,10.62,-2.48,1.1,.76,.22,'gold')
box('CHEST','body','Buckle inset',-.32,10.81,-2.61,.64,.35,.05,'dark')
# Diagonal leather baldric constructed from small stepped segments, close to torso.
for i in range(16):
 box('CHEST','body','Diagonal baldric',3.05-i*.42,.6+i*.49,-2.66,.67,.66,.18,'leather')
box('CHEST','body','Baldric fitting',1.65,2.1,-2.91,.68,.75,.12,'gold')
box('CHEST','body','Sabretache pouch',3.95,8.5,-.25,.85,3.15,2.3,'leather')
box('CHEST','body','Pouch silver piping',4.7,8.6,-.15,.13,2.9,2.1,'silver')
# Fur trimmed pelisse panel draped close over the left upper torso.
box('CHEST','body','Pelisse front panel',2.9,1.2,-2.71,1.3,7.7,.22,'wool')
for i in range(13):
 box('CHEST','body','Pelisse fur edging',4.02,1+i*.6,-2.8,.4,.54,.4,'fur')
for i in range(9):box('CHEST','body','Pelisse silver loops',3.0,1.7+i*.7,-2.97,.95,.12,.12,'silver')
for bone in ['right_arm','left_arm']:
 box('CHEST',bone,'Fitted sleeve',-2.18,-2.08,-2.18,4.36,11.9,4.36,'wool')
 box('CHEST',bone,'Crimson cuff',-2.22,8.2,-2.22,4.44,1.55,4.44,'red')
 box('CHEST',bone,'Cuff silver edge',-2.24,9.65,-2.24,4.48,.15,4.48,'silver')
 box('CHEST',bone,'White glove',-2.15,9.88,-2.15,4.3,2.2,4.3,'bone')
 box('CHEST',bone,'Shoulder red piping',-2.22,-2.18,-2.2,4.44,.16,4.4,'red')
 for i in range(5):
  box('CHEST',bone,'Cuff embroidery',-.7+i*.26,7.35+abs(i-2)*.18,-2.4,.15,.75,.13,'silver')
 if bone=='left_arm':
  for i in range(7):box('CHEST',bone,'Pelisse shoulder fur',-2.32+i*.64,-2.4,-2.34,.61,.42,4.68,'fur')
for bone in ['right_leg','left_leg']:
 box('LEGS',bone,'Fitted grey breeches',-2.12,-.12,-2.12,4.24,11.7,4.24,'grey')
 side=-2.2 if bone=='right_leg' else 2.08
 box('LEGS',bone,'Outer black trouser stripe',side,.1,-.22,.12,11.3,.44,'leather')
 box('LEGS',bone,'Silver trouser seam',side+(-.03 if side<0 else .1),.1,-.07,.03,11.3,.14,'silver')
 box('FEET',bone,'Black ankle boot',-2.22,9.55,-2.26,4.44,2.57,4.52,'leather')
 box('FEET',bone,'Boot toe',-2.2,10.9,-2.95,4.4,1.22,.85,'leather')
 box('FEET',bone,'Boot sole',-2.24,11.95,-3,4.48,.28,5.28,'dark')

def geometry(slim=False):
 result=[]
 for p in parts:
  p={**p,'xyz':p['xyz'][:],'size':p['size'][:]}
  if p['bone'].endswith('_arm'):
   # Centered sleeve geometry -> vanilla arm-local coordinates, with classic and slim width.
   if slim:
    p['xyz'][0]*=.75;p['size'][0]*=.75
   p['xyz'][0]+=-.5 if p['bone']=='right_arm' else .5
   if not slim:p['xyz'][0]+=-.5 if p['bone']=='right_arm' else .5
  result.append(p)
 return result
# Java generated layer factories. Empty root cubes prevent unwanted skin/face boxes.
java=['package dev.musicfloat.napoleonicfantasy.uniform;',
'import net.minecraft.client.model.HumanoidModel;', 'import net.minecraft.client.model.geom.*;', 'import net.minecraft.client.model.geom.builders.*;', 'import net.minecraft.resources.ResourceLocation;', 'import net.minecraft.world.entity.*;',
'public final class SkullHussarArmorModel extends HumanoidModel<LivingEntity> {',
' public SkullHussarArmorModel(ModelPart root){super(root);}',
' public static ModelLayerLocation layer(EquipmentSlot slot, boolean slim){return new ModelLayerLocation(new ResourceLocation("napoleonic_fantasy", "hussar_"+slot.getName()+(slim?"_slim":"_classic")),"main");}',
' public static LayerDefinition create(EquipmentSlot slot, boolean slim){',
'  MeshDefinition mesh=new MeshDefinition(); var root=mesh.getRoot();',
'  for(String name:new String[]{"head","hat","body","right_arm","left_arm","right_leg","left_leg"}) root.addOrReplaceChild(name,CubeListBuilder.create(),PartPose.ZERO);']
for slim in [False,True]:
 java.append('  if(slim == '+str(slim).lower()+'){')
 for i,p in enumerate(geometry(slim)):
  tile=list(COLORS).index(p['mat']);u=tile%4*64+2;v=tile//4*64+2
  coords=','.join(f'{n:.4f}f' for n in p['xyz']+p['size'])
  java.append(f'   if(slot==EquipmentSlot.{p["slot"]})root.getChild("{p["bone"]}").addOrReplaceChild("p{i}",CubeListBuilder.create().texOffs({u},{v}).addBox({coords}),PartPose.ZERO);')
 java.append('  }')
java+=['  return LayerDefinition.create(mesh,256,256);',' }','}']
(ROOT/'src/main/java/dev/musicfloat/napoleonicfantasy/uniform/SkullHussarArmorModel.java').write_text('\n'.join(java))
# Exact same cubes in Blockbench, arranged in a vanilla standing pose, Y up.
pivots={'head':(0,24,0),'body':(0,24,0),'right_arm':(-5,22,0),'left_arm':(5,22,0),'right_leg':(-1.9,12,0),'left_leg':(1.9,12,0)}
for slim in [False,True]:
 elements=[];groups={n:{'name':n,'origin':list(p),'uuid':str(uuid.uuid4()),'children':[]} for n,p in pivots.items()}
 for i,p in enumerate(geometry(slim)):
  x,y,z=p['xyz'];w,h,d=p['size'];px,py,pz=pivots[p['bone']];lo=[px+x,py-y-h,pz+z];hi=[px+x+w,py-y,pz+z+d]
  tile=list(COLORS).index(p['mat']);u=tile%4*64+2;v=tile//4*64+2
  uid=str(uuid.uuid4());uv=[u,v,u+max(w,d),v+h]
  elements.append({'uuid':uid,'type':'cube','name':p['name'],'from':lo,'to':hi,'origin':[px,py,pz],'faces':{s:{'uv':uv,'texture':0} for s in ['north','south','east','west','up','down']}})
  groups[p['bone']]['children'].append(uid)
 model={'meta':{'format_version':'4.10','model_format':'free','box_uv':False},'name':'Prussian skull hussar '+('slim' if slim else 'classic'),'resolution':{'width':256,'height':256},'elements':elements,'outliner':list(groups.values()),'textures':[{'name':'hussar_materials.png','id':'0','uuid':str(uuid.uuid4()),'source':'data:image/png;base64,'+base64.b64encode(texture.read_bytes()).decode(),'width':256,'height':256,'uv_width':256,'uv_height':256}]}
 (ART/('prussian_skull_hussar_'+('slim' if slim else 'uniform')+'.bbmodel')).write_text(json.dumps(model,indent=2))
# Real 3D icons from the actual meshes, independently framed per item.
for slot,suffix in [('HEAD','shako'),('CHEST','jacket'),('LEGS','breeches'),('FEET','boots')]:
 selected=[e for e,p in zip(elements,geometry(True)) if p['slot']==slot]
 lo=[min(e['from'][a] for e in selected) for a in range(3)];hi=[max(e['to'][a] for e in selected) for a in range(3)];scale=12/max(hi[a]-lo[a] for a in range(3));center=[(lo[a]+hi[a])/2 for a in range(3)]
 out=[]
 for e in selected:
  out.append({'from':[(e['from'][a]-center[a])*scale+8 for a in range(3)],'to':[(e['to'][a]-center[a])*scale+8 for a in range(3)],'faces':{k:{'uv':[n/16 for n in f['uv']],'texture':'#material'} for k,f in e['faces'].items()}})
 # Inventory models use a stitched item sprite. Armor layers continue to use
 # the 256px material atlas; referring to it here produces missing-texture
 # warnings because armor textures are not part of the item atlas.
 j={'textures':{'material':f'napoleonic_fantasy:item/prussian_skull_hussar_{suffix}','particle':f'napoleonic_fantasy:item/prussian_skull_hussar_{suffix}'},'elements':out,'display':{'gui':{'rotation':[10,155,0],'scale':[1.08,1.08,1.08]},'ground':{'scale':[.5,.5,.5],'translation':[0,3,0]},'firstperson_righthand':{'rotation':[0,160,0],'scale':[.65,.65,.65]},'thirdperson_righthand':{'rotation':[0,160,0],'scale':[.5,.5,.5]}}}
 (RES/f'models/item/prussian_skull_hussar_{suffix}.json').write_text(json.dumps(j))
(ART/'uniform_geometry.json').write_text(json.dumps(parts,indent=2))
print(f'Exported {len(parts)} cubes, classic/slim armor, four 3D icons and embedded Blockbench sources')
