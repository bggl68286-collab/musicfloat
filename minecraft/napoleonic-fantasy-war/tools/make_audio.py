"""Original layered Foley and black-powder reports, synthesized offline; no voice recordings."""
from pathlib import Path
import json, subprocess, wave
import numpy as np
from scipy.signal import butter, sosfilt

ROOT=Path(__file__).resolve().parents[1];OUT=ROOT/'src/main/resources/assets/napoleonic_fantasy/sounds';OUT.mkdir(parents=True,exist_ok=True)
SRC=ROOT/'art/audio';SRC.mkdir(parents=True,exist_ok=True)
SR=44100;rng=np.random.default_rng(1815)
def noise(n,low=50,high=6000):
    return sosfilt(butter(3,[low,high],btype='bandpass',fs=SR,output='sos'),rng.normal(0,1,n))
def impulse(t,at,decay=25):return (t>=at)*np.exp(-np.maximum(0,t-at)*decay)
def save(name,x):
    x=np.nan_to_num(x);x=np.tanh(x*1.4);x*=.92/max(.92,np.max(np.abs(x)));x[:250]*=np.linspace(0,1,250);x[-2000:]*=np.linspace(1,0,2000)
    p=SRC/f'{name}.wav'
    with wave.open(str(p),'wb') as f:f.setnchannels(1);f.setsampwidth(2);f.setframerate(SR);f.writeframes(np.int16(x*32767).tobytes())
    subprocess.run(['ffmpeg','-v','error','-y','-i',str(p),'-c:a','libvorbis','-q:a','5',str(OUT/f'{name}.ogg')],check=True)
    return {'name':f'napoleonic_fantasy:{name}','stream':False,'attenuation_distance':80 if name=='report' else 16}
sounds={}
for name,length in [('sponge',1.55),('powder',.7),('insert',.65),('ram',1.35),('aim',.55),('ignite',.34),('report',3.8),('return',1.0),('impact',1.8),('cartridge',.42),('ramrod',1.05),('flintlock',.28),('musket_report',3.2),('bayonet_mount',.48),('bayonet_thrust',.62)]:
    t=np.arange(int(SR*length))/SR;n=len(t)
    if name in ('report','musket_report'):
        phase=2*np.pi*(63*t-14*t*t);body=np.sin(phase)*np.exp(-t*3.8)+.45*np.sin(2*np.pi*31*t)*np.exp(-t*2.4)
        crack=noise(n,350,12000)*np.exp(-t*32)*2.8
        smoke=noise(n,55,650)*np.exp(-t*2.4)*1.05
        x=(body+crack+smoke)*(1.5 if name=='report' else 1.25)
        dry=x.copy()
        for delay,gain in [(.13,.26),(.27,.17),(.44,.11),(.69,.07)]:
            d=int(delay*SR);x[d:]+=dry[:-d]*gain
    elif name=='cartridge':
        paper=noise(n,900,6800)*sum(np.exp(-((t-at)/.025)**2) for at in [.08,.22,.34])*.32
        x=paper+noise(n,90,500)*np.exp(-t*9)*.06
    elif name=='ramrod':
        knocks=sum((noise(n,70,1500)*.32+np.sin(2*np.pi*118*t)*.12)*impulse(t,a,24) for a in [.16,.52,.84])
        x=knocks+noise(n,180,2400)*np.sin(np.pi*t/length)**2*.1
    elif name=='flintlock':
        x=noise(n,1100,10500)*impulse(t,.06,75)*.48+noise(n,180,1300)*impulse(t,.11,38)*.18
    elif name=='bayonet_mount':
        x=noise(n,1500,10000)*impulse(t,.12,24)*.35+noise(n,80,1200)*impulse(t,.3,16)*.2
    elif name=='bayonet_thrust':
        x=noise(n,700,9000)*np.sin(np.pi*t/length)**2*.25+noise(n,80,700)*impulse(t,.42,18)*.16
    elif name=='sponge':
        envelope=sum(np.exp(-((t-at)/.115)**2) for at in [.2,.55,.9,1.24]);x=noise(n,220,4800)*envelope*.32+np.sin(2*np.pi*(160*t+13*t*t))*envelope*.04
    elif name=='powder':x=noise(n,800,7000)*np.sin(np.pi*np.clip(t/length,0,1))**2*.23+.12*noise(n,90,800)*impulse(t,.42,30)
    elif name=='insert':x=noise(n,70,950)*impulse(t,.16,16)*.5+np.sin(2*np.pi*260*t)*impulse(t,.16,22)*.22
    elif name=='ram':
        x=sum((noise(n,80,1500)*.4+np.sin(2*np.pi*145*t)*.19)*impulse(t,a,22) for a in [.28,.79])+noise(n,450,3000)*np.sin(np.pi*t/length)**2*.07
    elif name=='aim':x=noise(n,120,1800)*np.sin(np.pi*t/length)**2*.18+np.sin(2*np.pi*(340*t+27*np.sin(t*14)))*np.sin(np.pi*t/length)**2*.06
    elif name=='ignite':x=noise(n,900,11000)*np.clip(t/length,0,1)*.3+noise(n,90,2000)*impulse(t,.08,60)*.24
    elif name=='return':x=noise(n,70,1800)*np.sin(np.pi*t/length)**2*.22+np.sin(2*np.pi*(240*t+22*np.sin(t*8)))*np.sin(np.pi*t/length)**2*.035
    else:x=noise(n,60,2400)*np.exp(-t*6)*.7+np.sin(2*np.pi*85*t)*np.exp(-t*8)*.35+noise(n,800,8000)*np.exp(-t*9)*.17
    sounds[name]={'subtitle':f'subtitles.napoleonic_fantasy.{name}','sounds':[save(name,x)]}
(OUT.parent/'sounds.json').write_text(json.dumps(sounds,indent=2))
print('Created 15 original mono Vorbis Foley/report assets and lossless source masters.')
