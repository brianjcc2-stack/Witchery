/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.monster.EntityWitch
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 */
package com.emoniph.witchery.item;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.entity.EntityCovenWitch;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.item.ItemBase;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.api.IAllowItem;
import mods.battlegear2.api.IOffhandListener;
import mods.battlegear2.api.IOffhandWield;
import mods.battlegear2.api.IUsableItem;
import mods.battlegear2.api.PlayerEventChild;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

@Optional.InterfaceList({
    @Optional.Interface(iface = "mods.battlegear2.api.IUsableItem", modid = "battlegear2", striprefs = true),
    @Optional.Interface(iface = "mods.battlegear2.api.IOffhandWield", modid = "battlegear2", striprefs = true),
    @Optional.Interface(iface = "mods.battlegear2.api.IAllowItem", modid = "battlegear2", striprefs = true),
    @Optional.Interface(iface = "mods.battlegear2.api.IOffhandListener", modid = "battlegear2", striprefs = true)
})
public class ItemWitchHand
extends ItemBase implements IUsableItem, IOffhandWield, IAllowItem, IOffhandListener {
    public ItemWitchHand() {
        this.setMaxStackSize(1);
        this.setFull3D();
    }

    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.uncommon;
    }

    public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
        if (entity instanceof EntityPlayer) {
            Infusion.Registry.instance().get((EntityPlayer)entity).onUpdate(itemstack, world, (EntityPlayer)entity, par4, par5);
        }
    }

    public boolean onLeftClickEntity(ItemStack itemstack, EntityPlayer player, Entity entity) {
        Infusion.Registry.instance().get(player).onLeftClickEntity(itemstack, player.worldObj, player, entity);
        return true;
    }

    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
        player.setItemInUse(itemstack, this.getMaxItemUseDuration(itemstack));
        return itemstack;
    }

    public int getMaxItemUseDuration(ItemStack itemstack) {
        return 400;
    }

    public void onUsingTick(ItemStack itemstack, EntityPlayer player, int countdown) {
        Infusion.Registry.instance().get(player).onUsingItemTick(itemstack, player.worldObj, player, countdown);
    }

    public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityPlayer player, int countdown) {
        if (world.isRemote || !Infusion.isOnCooldown(world, itemstack)) {
            Infusion.Registry.instance().get(player).onPlayerStoppedUsing(itemstack, world, player, countdown);
        }
    }

    @Optional.Method(modid = "battlegear2")
    @Override
    public boolean isUsedOverAttack(ItemStack itemStack) {
        return true;
    }

    @Optional.Method(modid = "battlegear2")
    @Override
    public boolean isOffhandWieldable(ItemStack offhandStack, EntityPlayer wielder) {
        return true;
    }

    @Optional.Method(modid = "battlegear2")
    @Override
    public boolean allowOffhand(ItemStack mainhand, ItemStack offhand) {
        return true;
    }

    @Optional.Method(modid = "battlegear2")
    @Override
    public Event.Result onClickBlock(PlayerInteractEvent event, ItemStack mainhandStack, ItemStack offhandStack, boolean isOffhand) {
        return isOffhand ? Event.Result.ALLOW : Event.Result.DEFAULT;
    }

    @Optional.Method(modid = "battlegear2")
    @Override
    public Event.Result onClickAir(EntityPlayer player, ItemStack mainhandStack, ItemStack offhandStack) {
        return Event.Result.ALLOW;
    }

    @Optional.Method(modid = "battlegear2")
    @Override
    public void onAttackEntity(PlayerEventChild.OffhandAttackEvent event, boolean isOffhand) {
        if (isOffhand) {
            event.swingOffhand = false;
            event.shouldAttack = false;
            event.cancelParent = false;
        }
    }

    public static class EventHooks {
        @SubscribeEvent
        public void onLivingDeath(LivingDeathEvent event) {
            Entity entitySource;
            if (!event.entityLiving.worldObj.isRemote && (event.entityLiving instanceof EntityWitch || event.entityLiving instanceof EntityCovenWitch) && (entitySource = event.source.getSourceOfDamage()) != null && entitySource instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer)entitySource;
                boolean hasArthana = Witchery.modHooks.getHeldOrOffhandItem(player, Witchery.Items.ARTHANA) != null || Witchery.modHooks.getHeldOrOffhandItem(player, Witchery.Items.WITCH_HAND) != null;
                double d = player.worldObj.rand.nextDouble();
                double d2 = hasArthana ? 0.5 : 0.33;
                if (d < d2) {
                    ItemStack itemstack = new ItemStack(Witchery.Items.WITCH_HAND);
                    EntityItem entityItem = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, itemstack);
                    event.entityLiving.worldObj.spawnEntityInWorld((Entity)entityItem);
                }
            }
        }
    }
}

