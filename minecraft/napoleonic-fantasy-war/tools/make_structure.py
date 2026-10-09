"""A bounded GameTest arena with a floor under every projectile-test target."""
import struct,gzip
from pathlib import Path
def string(s):b=s.encode();return struct.pack('>H',len(b))+b
def tag(t,n,p):return bytes([t])+string(n)+p
def integer(n,v):return tag(3,n,struct.pack('>i',v))
def listing(n,t,v):return tag(9,n,bytes([t])+struct.pack('>i',len(v))+b''.join(v))
def compound(v):return b''.join(v)+b'\x00'
palette=[compound([tag(8,'Name',string('minecraft:stone'))]),compound([tag(8,'Name',string('minecraft:air'))])]
blocks=[compound([integer('state',0 if y==0 else 1),listing('pos',3,[struct.pack('>i',v) for v in [x,y,z]])]) for x in range(12) for z in range(12) for y in range(5)]
root=tag(10,'',compound([integer('DataVersion',3465),listing('size',3,[struct.pack('>i',v) for v in [12,5,12]]),listing('palette',10,palette),listing('blocks',10,blocks),listing('entities',10,[])]))
p=Path(__file__).resolve().parents[1]/'src/main/resources/data/napoleonic_fantasy/structures/empty.nbt';p.write_bytes(gzip.compress(root,mtime=0))
print('12 x 5 x 12 test arena with complete floor and air reset generated.')
