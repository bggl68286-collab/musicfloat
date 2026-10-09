package dev.musicfloat.napoleonicfantasy.gametest;
import com.mojang.authlib.GameProfile;
import dev.musicfloat.napoleonicfantasy.*;
import dev.musicfloat.napoleonicfantasy.combat.CannonCombat;
import dev.musicfloat.napoleonicfantasy.entity.Cannonball;
import dev.musicfloat.napoleonicfantasy.item.HandCannonItem;
import dev.musicfloat.napoleonicfantasy.network.CannonNetwork;
import dev.musicfloat.napoleonicfantasy.particle.CannonParticleOptions;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.*;
import net.minecraftforge.gametest.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@GameTestHolder(NapoleonicFantasy.MOD_ID)
@PrefixGameTestTemplate(false)
public final class CannonGameTests {
    private static FakePlayer player(GameTestHelper h,String name){
        var p=FakePlayerFactory.get(h.getLevel(),new GameProfile(UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)),name));
        var pos=h.absolutePos(new BlockPos(2,1,1));p.setPos(pos.getX()+.5,pos.getY(),pos.getZ()+.5);p.setYRot(0);p.setXRot(0);p.getAbilities().instabuild=false;
        p.getInventory().clearContent();p.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(NapoleonicFantasy.CANNON.get()));p.getCooldowns().removeCooldown(NapoleonicFantasy.CANNON.get());p.setShiftKeyDown(false);return p;
    }
    private static Zombie target(GameTestHelper h,BlockPos at){var z=h.spawn(EntityType.ZOMBIE,at);z.setNoAi(true);z.getAttribute(Attributes.MAX_HEALTH).setBaseValue(200);z.getAttribute(Attributes.ARMOR).setBaseValue(0);z.setHealth(200);z.setPersistenceRequired();return z;}
    @GameTest(template="empty",timeoutTicks=20)public static void survivalRequiresPowderAndSelectedShot(GameTestHelper h){
        var p=player(h,"test_missing");var s=p.getMainHandItem();var item=(HandCannonItem)s.getItem();
        h.assertTrue(!item.use(h.getLevel(),p,InteractionHand.MAIN_HAND).getResult().consumesAction(),"Empty inventory must reject loading");
        p.getInventory().add(new ItemStack(Items.GUNPOWDER,2));p.getInventory().add(new ItemStack(NapoleonicFantasy.ROUND_SHOT.get(),2));
        h.assertTrue(item.use(h.getLevel(),p,InteractionHand.MAIN_HAND).getResult().consumesAction(),"Correct ammo should start loading");p.stopUsingItem();h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=20)public static void interruptedLoadingConsumesNothing(GameTestHelper h){
        var p=player(h,"test_cancel");var s=p.getMainHandItem();var item=(HandCannonItem)s.getItem();
        p.getInventory().add(new ItemStack(Items.GUNPOWDER,2));p.getInventory().add(new ItemStack(NapoleonicFantasy.ROUND_SHOT.get(),2));
        item.use(h.getLevel(),p,InteractionHand.MAIN_HAND);item.onUseTick(h.getLevel(),p,s,72000-80);item.releaseUsing(s,h.getLevel(),p,72000-80);
        h.assertTrue(!HandCannonItem.loaded(s)&&!s.getTag().contains("IgniteAt"),"Partial loading must not arm the cannon");
        h.assertTrue(p.getInventory().countItem(Items.GUNPOWDER)==2&&p.getInventory().countItem(NapoleonicFantasy.ROUND_SHOT.get())==2,"Partial loading consumed ammunition");p.stopUsingItem();h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=30)public static void fullLoadIgnitionProjectileDamageAndDurability(GameTestHelper h){
        var p=player(h,"test_live_round");var s=p.getMainHandItem();var item=(HandCannonItem)s.getItem();
        p.getInventory().add(new ItemStack(Items.GUNPOWDER,2));p.getInventory().add(new ItemStack(NapoleonicFantasy.ROUND_SHOT.get(),2));
        Zombie z=target(h,new BlockPos(2,1,7));
        item.use(h.getLevel(),p,InteractionHand.MAIN_HAND);item.onUseTick(h.getLevel(),p,s,72000-HandCannonItem.LOAD_TICKS);
        h.assertTrue(HandCannonItem.loaded(s),"Completed cycle did not load");
        h.assertTrue(p.getInventory().countItem(Items.GUNPOWDER)==1&&p.getInventory().countItem(NapoleonicFantasy.ROUND_SHOT.get())==1,"Load must consume exactly one powder and shot");
        item.releaseUsing(s,h.getLevel(),p,72000-HandCannonItem.LOAD_TICKS);p.stopUsingItem();
        h.assertTrue(HandCannonItem.loaded(s)&&s.getTag().contains("IgniteAt"),"Ignition must delay firing");
        h.runAfterDelay(8,()->item.inventoryTick(s,h.getLevel(),p,0,true));
        h.runAfterDelay(15,()->{
            h.assertTrue(z.getHealth()<140,"Actual projectile did not hit the forward target");
            h.assertTrue(!HandCannonItem.loaded(s)&&s.getDamageValue()==2,"Firing must empty chamber and consume durability");
            h.assertTrue(p.getCooldowns().isOnCooldown(item),"Firing must impose cooldown");h.succeed();
        });
    }
    @GameTest(template="empty",timeoutTicks=20)public static void cannonCannotShootThroughStoneOrBreakTerrain(GameTestHelper h){
        var p=player(h,"test_wall");var s=p.getMainHandItem();
        h.setBlock(new BlockPos(2,2,3),Blocks.STONE);h.setBlock(new BlockPos(2,1,3),Blocks.STONE);
        Zombie z=target(h,new BlockPos(2,1,7));CannonCombat.fire(p,s);
        h.runAfterDelay(10,()->{h.assertTrue(z.getHealth()==200,"Projectile crossed a solid wall");h.assertBlockPresent(Blocks.STONE,new BlockPos(2,2,3));h.succeed();});
    }
    @GameTest(template="empty",timeoutTicks=20)public static void canisterPelletsAccumulateAndDefaultPvpIsProtected(GameTestHelper h){
        var p=player(h,"test_pellets");Zombie z=target(h,new BlockPos(2,1,6));
        for(int i=0;i<2;i++){var b=new Cannonball(NapoleonicFantasy.BALL.get(),h.getLevel());b.setOwner(p);b.setPos(CannonCombat.muzzle(p));b.configure(true,78,8);b.shoot(0,0,1,4.2f,0);h.getLevel().addFreshEntity(b);}
        h.assertTrue(!Cannonball.mayDamage(p,p),"Owner must be protected");
        var other=player(h,"test_protected_player");h.assertTrue(!Cannonball.mayDamage(p,other),"Default configuration must protect other players");
        h.runAfterDelay(10,()->{h.assertTrue(z.getHealth()<=184,"Separate pellets were lost to vanilla hurt immunity; health="+z.getHealth());h.succeed();});
    }
    @GameTest(template="empty",timeoutTicks=20)public static void loadedAmmoCannotSwitchAndHolsteringCancelsIgnition(GameTestHelper h){
        var p=player(h,"test_loaded_lock");var s=p.getMainHandItem();var item=(HandCannonItem)s.getItem();s.getOrCreateTag().putBoolean("Loaded",true);
        p.setShiftKeyDown(true);item.use(h.getLevel(),p,InteractionHand.MAIN_HAND);h.assertTrue(HandCannonItem.mode(s)==0,"Switching ammo must not change a chambered round");p.setShiftKeyDown(false);
        item.releaseUsing(s,h.getLevel(),p,72000-1);item.inventoryTick(s,h.getLevel(),p,0,false);
        h.assertTrue(HandCannonItem.loaded(s)&&!s.getTag().contains("IgniteAt"),"Holstering must cancel ignition without losing the loaded round");h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=20)public static void effectAndParticleSerializationRoundTrip(GameTestHelper h){
        var b=new FriendlyByteBuf(Unpooled.buffer());try{
            var e=new CannonNetwork.Effect(UUID.randomUUID(),0,new Vec3(1,2,3),new Vec3(0,.1,1),true);e.encode(b);h.assertTrue(e.equals(CannonNetwork.Effect.decode(b)),"Effect serialization lost data");
            b.clear();var o=new CannonParticleOptions(1,2.4f,80,.3f);o.writeToNetwork(b);h.assertTrue(o.equals(CannonParticleOptions.DESERIALIZER.fromNetwork(NapoleonicFantasy.FX.get(),b)),"Particle serialization lost data");
        }finally{b.release();}h.succeed();
    }
}
