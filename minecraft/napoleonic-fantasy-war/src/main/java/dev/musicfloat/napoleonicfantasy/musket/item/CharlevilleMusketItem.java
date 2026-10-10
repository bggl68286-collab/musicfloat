package dev.musicfloat.napoleonicfantasy.musket.item;

import dev.musicfloat.napoleonicfantasy.NapoleonicFantasy;
import dev.musicfloat.napoleonicfantasy.musket.combat.MusketCombat;
import dev.musicfloat.napoleonicfantasy.musket.client.MusketItemExtensions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import java.util.List;
import java.util.function.Consumer;

public final class CharlevilleMusketItem extends Item {
    public static final int LOAD_TICKS = 100;
    public CharlevilleMusketItem() { super(new Properties().stacksTo(1).durability(320)); }
    public static boolean loaded(ItemStack stack) { return stack.hasTag() && stack.getTag().getBoolean("Loaded"); }
    public static boolean bayonet(ItemStack stack) { return stack.hasTag() && stack.getTag().getBoolean("Bayonet"); }
    private static void sound(Level level, Player player, String key, float volume) {
        if (!level.isClientSide) level.playSound(null, player.blockPosition(), NapoleonicFantasy.SOUND.get(key).get(), SoundSource.PLAYERS, volume, .95f + level.random.nextFloat() * .1f);
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND || player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(stack);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                boolean next = !bayonet(stack);
                stack.getOrCreateTag().putBoolean("Bayonet", next);
                stack.getOrCreateTag().putInt("CustomModelData", next ? 1 : 0);
                sound(level, player, "bayonet_mount", .55f);
                player.displayClientMessage(Component.translatable(next ? "message.napoleonic_fantasy.bayonet_on" : "message.napoleonic_fantasy.bayonet_off"), true);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        if (bayonet(stack)) { player.startUsingItem(hand); return InteractionResultHolder.consume(stack); }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }
    @Override public int getUseDuration(ItemStack stack) { return 72000; }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.NONE; }
    @Override public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        if (level.isClientSide || !(entity instanceof Player player) || bayonet(stack) || loaded(stack)) return;
        int tick = getUseDuration(stack) - remaining;
        if (tick == 1) sound(level, player, "cartridge", .55f);
        if (tick == 25) sound(level, player, "powder", .65f);
        if (tick == 52) sound(level, player, "ramrod", .7f);
        if (tick == 84) sound(level, player, "flintlock", .55f);
        if (tick >= LOAD_TICKS) {
            stack.getOrCreateTag().putBoolean("Loaded", true);
            player.displayClientMessage(Component.translatable("message.napoleonic_fantasy.musket_ready"), true);
        }
    }
    @Override public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int remaining) {
        if (level.isClientSide || !(entity instanceof Player player) || player.getMainHandItem() != stack) return;
        if (bayonet(stack)) { MusketCombat.thrust(player); player.swing(InteractionHand.MAIN_HAND, true); return; }
        if (!loaded(stack)) { player.displayClientMessage(Component.translatable("message.napoleonic_fantasy.musket_cancelled"), true); return; }
        stack.getOrCreateTag().putBoolean("Loaded", false);
        sound(level, player, "flintlock", .8f);
        MusketCombat.fire(player);
        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        player.swing(InteractionHand.MAIN_HAND, true);
    }
    @Override public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player && bayonet(stack)) MusketCombat.thrust(player);
        return super.hurtEnemy(stack, target, attacker);
    }
    @Override public void appendHoverText(ItemStack stack, Level level, List<Component> lines, net.minecraft.world.item.TooltipFlag flag) {
        lines.add(Component.translatable("tooltip.napoleonic_fantasy.musket_load").withStyle(ChatFormatting.GOLD));
        lines.add(Component.translatable("tooltip.napoleonic_fantasy.musket_bayonet").withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable(loaded(stack) ? "state.napoleonic_fantasy.musket_loaded" : "state.napoleonic_fantasy.musket_empty").withStyle(ChatFormatting.YELLOW));
    }
    @Override public void initializeClient(Consumer<IClientItemExtensions> consumer) { consumer.accept(new MusketItemExtensions()); }
}
