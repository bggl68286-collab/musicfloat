package dev.musicfloat.napoleonicfantasy.entity;
import dev.musicfloat.napoleonicfantasy.*;
import dev.musicfloat.napoleonicfantasy.network.CannonNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraftforge.network.NetworkHooks;
import java.util.UUID;

public final class Cannonball extends ThrowableItemProjectile {
    private static final EntityDataAccessor<Boolean> PELLET=SynchedEntityData.defineId(Cannonball.class,EntityDataSerializers.BOOLEAN);
    private static final ResourceKey<DamageType> CANISTER_DAMAGE=ResourceKey.create(Registries.DAMAGE_TYPE,new ResourceLocation(NapoleonicFantasy.MOD_ID,"canister"));
    private boolean pellet;private float damage=78;
    public Cannonball(EntityType<? extends Cannonball> type,Level l){super(type,l);}
    @Override protected void defineSynchedData(){super.defineSynchedData();entityData.define(PELLET,false);}
    public boolean isPellet(){return entityData.get(PELLET);}
    public void configure(boolean pellet,float roundDamage,float pelletDamage){this.pellet=pellet;entityData.set(PELLET,pellet);damage=pellet?pelletDamage:roundDamage;}
    @Override protected Item getDefaultItem(){return NapoleonicFantasy.ROUND_SHOT.get();}
    @Override protected float getGravity(){return pellet?.035f:.045f;}
    public static boolean mayDamage(Entity owner,Entity target){return target!=owner&&!(target instanceof Player&&!CannonConfig.DAMAGE_PLAYERS.get())&&(owner==null||!owner.isAlliedTo(target));}
    @Override protected void onHitEntity(EntityHitResult hit){
        super.onHitEntity(hit);
        if(level().isClientSide)return;
        var target=hit.getEntity();Entity owner=getOwner();
        if(mayDamage(owner,target)){
            DamageSource source=pellet?new DamageSource(level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(CANISTER_DAMAGE),this,owner):damageSources().thrown(this,owner);
            if(target.hurt(source,damage)){
                Vec3 d=getDeltaMovement().normalize();target.push(d.x*(pellet?.12:.6),pellet?.05:.15,d.z*(pellet?.12:.6));
            }
        }
        impact(hit.getLocation());
    }
    @Override protected void onHitBlock(BlockHitResult hit){super.onHitBlock(hit);if(!level().isClientSide)impact(hit.getLocation());}
    private void impact(Vec3 p){
        if(!pellet)CannonNetwork.send(getOwner()==null?new UUID(0,0):getOwner().getUUID(),1,p,getDeltaMovement().normalize(),level(),CannonConfig.SOUND.get());
        discard();
    }
    @Override public void tick(){super.tick();if(!level().isClientSide&&(tickCount>(pellet?22:160)||getY()<level().getMinBuildHeight()-16))discard();}
    @Override public void addAdditionalSaveData(CompoundTag t){super.addAdditionalSaveData(t);t.putBoolean("Pellet",pellet);t.putFloat("Damage",damage);}
    @Override public void readAdditionalSaveData(CompoundTag t){super.readAdditionalSaveData(t);pellet=t.getBoolean("Pellet");entityData.set(PELLET,pellet);damage=t.contains("Damage")?t.getFloat("Damage"):78;}
    @Override public Packet<ClientGamePacketListener> getAddEntityPacket(){return NetworkHooks.getEntitySpawningPacket(this);}
}
