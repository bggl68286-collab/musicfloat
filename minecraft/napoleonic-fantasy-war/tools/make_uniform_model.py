import json, os, uuid, shutil
OUT='/workspace/napoleonic-fantasy-war-forge/art/source'
os.makedirs(OUT,exist_ok=True)
tex='prussian_skull_hussar_atlas.png'
shutil.copy('/workspace/generated_images/exec-22608524-9b00-48c9-9dbe-39b38e4785e0.png',OUT+'/'+tex)
els=[]; groups=[]
def cube(name,fr,to,uv=(0,0,16,16),group=None):
    x0,y0,z0=fr; x1,y1,z1=to; u0,v0,u1,v1=uv
    faces={}
    for side in ('north','south','east','west','up','down'):
        faces[side]={'uv':[u0,v0,u1,v1],'texture':0}
    e={'name':name,'from':[x0,y0,z0],'to':[x1,y1,z1],'autouv':0,'color':0,'origin':[0,0,0],'rotation':[0,0,0],'faces':faces,'type':'cube','uuid':str(uuid.uuid4())}
    els.append(e)
    if group is not None: group.append(e['uuid'])
def group(name):
    g={'name':name,'origin':[0,0,0],'color':0,'uuid':str(uuid.uuid4()),'children':[]}; groups.append(g); return g['children']
head=group('Shako and skull plate'); cube('Shako crown',[-5,-11,-5],[5,-3,5],(0,0,32,32),head); cube('Brim',[-6,-4.5,-6],[6,-3.4,6],(32,0,32,16),head); cube('Skull insignia',[-2.2,-8.3,-6.1],[2.2,-4.8,-5.8],(64,0,16,16),head); cube('Plume',[3,-10.5,-1],[4,-5.5,1],(80,0,8,24),head)
body=group('Fitted officer jacket'); cube('Tailored torso',[-4.4,-3,-2.5],[4.4,8,2.5],(0,32,32,64),body); cube('Red collar',[-4.5,-4.5,-2.8],[4.5,-2.5,-1.8],(32,32,32,40),body); cube('Silver braid panel',[-3.6,-2.2,-3],[3.6,6.2,-2.6],(64,32,96,64),body); cube('Fur shoulder left',[-7,-3,-2.3],[-4,0,2.3],(96,0,112,32),body); cube('Fur shoulder right',[4,-3,-2.3],[7,0,2.3],(96,0,112,32),body); cube('Waist pouch',[2,3.5,-3.4],[5,7,-2.5],(112,32,128,48),body)
for side in (-1,1):
    arm=group(('Left' if side<0 else 'Right')+' sleeve and cuff'); lo,hi=sorted((side*4,side*7)); cube('Sleeve',(lo,-2,-2.2),(hi,9,2.2),(0,64,32,96),arm); cube('Red cuff',(lo,8.3,-2.3),(hi+0.1,10.5,2.3),(32,64,64,80),arm)
legs=group('Close fitting breeches and boots'); cube('Trousers left',[-4,-0.5,-2.1],[-0.2,12,2.1],(0,80,32,112),legs); cube('Trousers right',[0.2,-0.5,-2.1],[4,12,2.1],(32,80,64,112),legs); cube('Boots left',[-4.1,9.5,-2.4],[-0.1,14.5,2.4],(64,80,96,112),legs); cube('Boots right',[0.1,9.5,-2.4],[4.1,14.5,2.4],(96,80,128,112),legs)
model={'meta':{'format_version':'5.0','model_format':'free','box_uv':False},'name':'prussian_skull_hussar_uniform','model_identifier':'napoleonic_fantasy_uniform','visible_box':[8,8,0],'resolution':{'width':128,'height':128},'elements':els,'outliner':groups,'textures':[{'name':tex,'id':str(uuid.uuid4()),'path':'art/source/'+tex,'folder':'','namespace':'','uv_width':1536,'uv_height':1024,'particle':False,'use_as_default':True}]}
json.dump(model,open(OUT+'/prussian_skull_hussar_uniform.bbmodel','w'),ensure_ascii=False,indent=2)
print('elements',len(els),'groups',len(groups))
