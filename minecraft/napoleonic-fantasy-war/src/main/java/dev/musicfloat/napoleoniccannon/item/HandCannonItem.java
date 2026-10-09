package dev.musicfloat.napoleonicfantasy.item;

import dev.musicfloat.napoleonicfantasy.*;
import dev.musicfloat.napoleonicfantasy.client.CannonItemExtensions;
import dev.musicfloat.napoleonicfantasy.combat.CannonCombat;
import dev.musicfloat.napoleonicfantasy.network.CannonNetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import java.util.*;
import java.util.function.Consumer;

public final class HandCannonItem extends Item {
    public static final int LOAD_TICKS=116,IGNITION_TICKS=7;
    public HandCannonItem(){super(new Properties().stacksTo(1).durability(640).rarity(Rarity.EPIC).fireResistant());}
    public static boolean loaded(ItemStack s){return s.hasTag()&&s.getTag().getBoolean("Loaded");}
    public static int mode(ItemStack s){return s.hasTag()?Math.floorMod(s.getTag().getInt("AmmoMode"),2):0;}
    public static Item ammo(ItemStack s){return mode(s)==0?NapoleonicFantasy.ROUND_SHOT.get():NapoleonicFantasy.CANISTER_SHOT.get();}
    public static int stage(int ticks){return ticks<32?0:ticks<52?1:ticks<72?2:ticks<108?3:ticks<116?4:5;}
    public static boolean hasAmmo(Player p,ItemStack s){return p.getAbilities().instabuild||(p.getInventory().countItem(Items.GUNPOWDER)>0&&p.getInventory().countItem(ammo(s))>0);}
    private static void consume(Player p,Item item){for(int i=0;i<p.getInventory().getContainerSize();i++){var s=p.getInventory().getItem(i);if(s.is(item)){s.shrink(1);return;}}}
    @Override public InteractionResultHolder<ItemStack> use(Level l,Player p,InteractionHand h){
        var s=p.getItemInHand(h);
        if(h!=InteractionHand.MAIN_HAND)return InteractionResultHolder.fail(s);
        if(p.isShiftKeyDown()){
            if(!l.isClientSide){
                if(loaded(s))p.displayClientMessage(Component.translatable("message.napoleonic_fantasy.loaded_locked"),true);
                else{s.getOrCreateTag().putInt("AmmoMode",1-mode(s));p.displayClientMessage(Component.translatable("message.napoleonic_fantasy.mode",Component.translatable("ammo.napoleonic_fantasy."+mode(s))),true);}
            }return InteractionResultHolder.sidedSuccess(s,l.isClientSide);
        }
        if(p.getCooldowns().isOnCooldown(this)||s.getOrCreateTag().contains("IgniteAt"))return InteractionResultHolder.fail(s);
        if(!loaded(s)&&!hasAmmo(p,s)){
            if(!l.isClientSide)p.displayClientMessage(Component.translatable("message.napoleonic_fantasy.need_ammo",new ItemStack(ammo(s)).getHoverName()),true);
            return InteractionResultHolder.fail(s);
        }
        p.startUsingItem(h);return InteractionResultHolder.consume(s);
    }
    @Override public UseAnim getUseAnimation(ItemStack s){return UseAnim.NONE;}
    @Override public boolean shouldCauseReequipAnimation(ItemStack oldStack,ItemStack newStack,boolean slotChanged){return slotChanged||oldStack.getItem()!=newStack.getItem();}
    @Override public int getUseDuration(ItemStack s){return 72000;}
    public static void sound(Level l,Player p,String name,float volume){if(!l.isClientSide&&CannonConfig.SOUND.get())l.playSound(null,p.blockPosition(),NapoleonicFantasy.SOUND.get(name).get(),SoundSource.PLAYERS,volume,.96f+l.random.nextFloat()*.08f);}
    @Override public void onUseTick(Level l,LivingEntity e,ItemStack s,int remaining){
        if(l.isClientSide||!(e instanceof Player p)||loaded(s))return;
        int t=getUseDuration(s)-remaining;
        switch(t){case 1->sound(l,p,"sponge",.65f);case 33->sound(l,p,"powder",.7f);case 53->sound(l,p,"insert",.75f);case 73->sound(l,p,"ram",.75f);case 109->sound(l,p,"aim",.65f);}
        if(t>=LOAD_TICKS){
            if(!hasAmmo(p,s)){p.stopUsingItem();return;}
            if(!p.getAbilities().instabuild){consume(p,Items.GUNPOWDER);consume(p,ammo(s));}
            s.getOrCreateTag().putBoolean("Loaded",true);
            p.displayClientMessage(Component.translatable("message.napoleonic_fantasy.ready"),true);
        }
    }
    @Override public void releaseUsing(ItemStack s,Level l,LivingEntity e,int remaining){
        if(l.isClientSide||!(e instanceof Player p)||p.getMainHandItem()!=s)return;
        if(!loaded(s)){p.displayClientMessage(Component.translatable("message.napoleonic_fantasy.cancelled"),true);return;}
        s.getOrCreateTag().putLong("IgniteAt",l.getGameTime());p.getCooldowns().addCooldown(this,IGNITION_TICKS+25);
        sound(l,p,"ignite",.9f);CannonNetwork.send(p.getUUID(),2,p.getEyePosition().add(p.getLookAngle().scale(.7)),p.getLookAngle(),l,false);
    }
    @Override public void inventoryTick(ItemStack s,Level l,Entity e,int slot,boolean selected){
        if(l.isClientSide||!(e instanceof Player p)||!s.hasTag())return;
        var tag=s.getTag();
        if(tag.contains("IgniteAt")){
            if(!selected||p.getMainHandItem()!=s){tag.remove("IgniteAt");return;}
            if(l.getGameTime()-tag.getLong("IgniteAt")>=IGNITION_TICKS){
                tag.remove("IgniteAt");
                if(loaded(s)){tag.putBoolean("Loaded",false);tag.putLong("ShotAt",l.getGameTime());CannonCombat.fire(p,s);s.hurtAndBreak(2,p,x->x.broadcastBreakEvent(InteractionHand.MAIN_HAND));}
            }
        }
        if(tag.contains("ShotAt")){
            long t=l.getGameTime()-tag.getLong("ShotAt");
            if(t==11&&selected)sound(l,p,"return",.6f);
            if(t>28)tag.remove("ShotAt");
        }
    }
    @Override public void initializeClient(Consumer<IClientItemExtensions> c){c.accept(new CannonItemExtensions());}
    @Override public void appendHoverText(ItemStack s,Level l,List<Component> list,TooltipFlag f){
        list.add(Component.translatable("tooltip.napoleonic_fantasy.load").withStyle(ChatFormatting.GOLD));
        list.add(Component.translatable("tooltip.napoleonic_fantasy.mode").withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable("tooltip.napoleonic_fantasy.state",Component.translatable(loaded(s)?"state.napoleonic_fantasy.loaded":"state.napoleonic_fantasy.empty"),Component.translatable("ammo.napoleonic_fantasy."+mode(s))).withStyle(ChatFormatting.YELLOW));
    }
}
