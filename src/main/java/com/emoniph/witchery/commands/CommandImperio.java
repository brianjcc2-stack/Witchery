package com.emoniph.witchery.commands;

import com.emoniph.witchery.infusion.infusions.symbols.InventoryMobEquipment;
import com.emoniph.witchery.infusion.infusions.symbols.SymbolEffectImperio;
import com.emoniph.witchery.util.ChatUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.List;
import java.util.Map;

public class CommandImperio extends CommandBase {

    @Override
    public String getCommandName() {
        return "imperio";
    }

    @Override
    public List getCommandAliases() {
        return java.util.Arrays.asList("imperius");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "control", "follow", "stay", "attack", "inventory", "forward", "backward", "coord", "release",
                    "seguir", "quieto", "atacar", "liberar", "adelante", "atras");
        }
        return null;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // Accessible to all players
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayer;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.witchery.imperio.usage";
    }

    private EntityLiving getControlledMob(EntityPlayer player) {
        for (Map.Entry<EntityLivingBase, EntityPlayer> entry : SymbolEffectImperio.IMPERIO_TARGETS.entrySet()) {
            if (entry.getValue() == player && entry.getKey() instanceof EntityLiving && entry.getKey().isEntityAlive()) {
                return (EntityLiving) entry.getKey();
            }
        }

        // Check if player is pointing at a living entity within 16 blocks
        Entity pointed = getEntityLookedAt(player, 16.0D);
        if (pointed instanceof EntityLiving && pointed.isEntityAlive()) {
            SymbolEffectImperio.IMPERIO_TARGETS.put((EntityLiving) pointed, player);
            return (EntityLiving) pointed;
        }

        // Fallback: look for nearest mob within 8 blocks
        List<EntityLiving> nearby = player.worldObj.getEntitiesWithinAABB(EntityLiving.class,
                player.boundingBox.expand(8.0D, 4.0D, 8.0D));
        EntityLiving closest = null;
        double minDist = Double.MAX_VALUE;
        for (EntityLiving mob : nearby) {
            if (mob.isEntityAlive()) {
                double d = mob.getDistanceSqToEntity(player);
                if (d < minDist) {
                    minDist = d;
                    closest = mob;
                }
            }
        }
        if (closest != null) {
            SymbolEffectImperio.IMPERIO_TARGETS.put(closest, player);
            return closest;
        }

        return null;
    }

    private Entity getEntityLookedAt(EntityPlayer player, double reach) {
        Vec3 eyePos = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3 lookVec = player.getLook(1.0F);
        Vec3 reachVec = eyePos.addVector(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach);

        List list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player,
                player.boundingBox.addCoord(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach).expand(1.0D, 1.0D, 1.0D));
        Entity pointedEntity = null;
        double minDistance = reach;

        for (Object obj : list) {
            Entity entity = (Entity) obj;
            if (entity.canBeCollidedWith()) {
                float borderSize = entity.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity.boundingBox.expand(borderSize, borderSize, borderSize);
                MovingObjectPosition mop = axisalignedbb.calculateIntercept(eyePos, reachVec);

                if (axisalignedbb.isVecInside(eyePos)) {
                    if (0.0D < minDistance || minDistance == 0.0D) {
                        pointedEntity = entity;
                        minDistance = 0.0D;
                    }
                } else if (mop != null) {
                    double distToTarget = eyePos.distanceTo(mop.hitVec);
                    if (distToTarget < minDistance || minDistance == 0.0D) {
                        pointedEntity = entity;
                        minDistance = distToTarget;
                    }
                }
            }
        }
        return pointedEntity;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) sender;

        if (args.length == 0) {
            ChatUtil.sendPlain(player, EnumChatFormatting.DARK_PURPLE + "=== Control de Imperio ===");
            ChatUtil.sendPlain(player, EnumChatFormatting.LIGHT_PURPLE + "/imperio control" + EnumChatFormatting.GRAY + " - Toma control de la criatura que miras.");
            ChatUtil.sendPlain(player, EnumChatFormatting.LIGHT_PURPLE + "/imperio follow" + EnumChatFormatting.GRAY + " - La criatura te sigue fielmente.");
            ChatUtil.sendPlain(player, EnumChatFormatting.LIGHT_PURPLE + "/imperio stay" + EnumChatFormatting.GRAY + " - La criatura se queda quieta en su sitio.");
            ChatUtil.sendPlain(player, EnumChatFormatting.LIGHT_PURPLE + "/imperio attack" + EnumChatFormatting.GRAY + " - Ordena atacar al objetivo que estás mirando.");
            ChatUtil.sendPlain(player, EnumChatFormatting.LIGHT_PURPLE + "/imperio inventory" + EnumChatFormatting.GRAY + " - Abre el inventario de equipamiento de la criatura.");
            ChatUtil.sendPlain(player, EnumChatFormatting.LIGHT_PURPLE + "/imperio forward <bloques>" + EnumChatFormatting.GRAY + " - La mueve hacia adelante.");
            ChatUtil.sendPlain(player, EnumChatFormatting.LIGHT_PURPLE + "/imperio release" + EnumChatFormatting.GRAY + " - Libera a la criatura.");
            return;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("control")) {
            Entity pointed = getEntityLookedAt(player, 24.0D);
            if (pointed instanceof EntityLiving && pointed.isEntityAlive()) {
                SymbolEffectImperio.IMPERIO_TARGETS.put((EntityLiving) pointed, player);
                ChatUtil.sendPlain(player, EnumChatFormatting.GREEN + "¡Ahora estás controlando a: " + pointed.getCommandSenderName() + "!");
            } else {
                ChatUtil.sendPlain(player, EnumChatFormatting.RED + "No estás mirando a ninguna criatura válida para controlar.");
            }
            return;
        }

        EntityLiving creature = getControlledMob(player);

        if (creature == null) {
            ChatUtil.sendPlain(player, EnumChatFormatting.RED + "No tienes ninguna criatura bajo Imperio. Mira a una y usa /imperio control.");
            return;
        }

        if (sub.equals("release") || sub.equals("liberar")) {
            SymbolEffectImperio.IMPERIO_TARGETS.remove(creature);
            SymbolEffectImperio.IMPERIO_STAYING_TARGETS.remove(creature);
            if (creature.getNavigator() != null) {
                creature.getNavigator().clearPathEntity();
            }
            ChatUtil.sendPlain(player, EnumChatFormatting.YELLOW + "Has liberado a " + creature.getCommandSenderName() + " de tu control mental.");
            return;
        }

        if (sub.equals("stay") || sub.equals("esperar") || sub.equals("quieto")) {
            SymbolEffectImperio.IMPERIO_STAYING_TARGETS.add(creature);
            if (creature.getNavigator() != null) {
                creature.getNavigator().clearPathEntity();
            }
            creature.setAttackTarget(null);
            ChatUtil.sendPlain(player, EnumChatFormatting.GOLD + creature.getCommandSenderName() + " ahora esperará aquí pacíficamente.");
            return;
        }

        if (sub.equals("follow") || sub.equals("seguir")) {
            SymbolEffectImperio.IMPERIO_STAYING_TARGETS.remove(creature);
            if (creature.getNavigator() != null) {
                creature.getNavigator().tryMoveToEntityLiving(player, 1.3D);
            }
            ChatUtil.sendPlain(player, EnumChatFormatting.AQUA + creature.getCommandSenderName() + " ahora te está siguiendo.");
            return;
        }

        if (sub.equals("inventory") || sub.equals("inv") || sub.equals("equip")) {
            InventoryMobEquipment inv = new InventoryMobEquipment(creature);
            player.displayGUIChest(inv);
            return;
        }

        if (sub.equals("attack") || sub.equals("atacar")) {
            SymbolEffectImperio.IMPERIO_STAYING_TARGETS.remove(creature);
            Entity target = getEntityLookedAt(player, 32.0D);
            if (target instanceof EntityLivingBase && target != creature && target != player) {
                creature.setAttackTarget((EntityLivingBase) target);
                if (creature.getNavigator() != null) {
                    creature.getNavigator().tryMoveToEntityLiving(target, 1.25D);
                }
                ChatUtil.sendPlain(player, EnumChatFormatting.RED + creature.getCommandSenderName() + " está atacando a " + target.getCommandSenderName() + "!");
            } else {
                ChatUtil.sendPlain(player, EnumChatFormatting.RED + "Mira a una entidad viva para ordenarle atacar.");
            }
            return;
        }

        if (sub.equals("forward") || sub.equals("adelante")) {
            SymbolEffectImperio.IMPERIO_STAYING_TARGETS.remove(creature);
            double dist = 5.0D;
            if (args.length > 1) {
                try {
                    dist = Double.parseDouble(args[1]);
                } catch (NumberFormatException ignored) {}
            }
            float yaw = creature.rotationYawHead;
            double dx = -MathHelper.sin(yaw * (float) Math.PI / 180.0F) * dist;
            double dz = MathHelper.cos(yaw * (float) Math.PI / 180.0F) * dist;
            if (creature.getNavigator() != null) {
                creature.getNavigator().tryMoveToXYZ(creature.posX + dx, creature.posY, creature.posZ + dz, 1.2D);
            }
            ChatUtil.sendPlain(player, EnumChatFormatting.GREEN + creature.getCommandSenderName() + " avanzando " + dist + " bloques.");
            return;
        }

        if (sub.equals("backward") || sub.equals("atras")) {
            SymbolEffectImperio.IMPERIO_STAYING_TARGETS.remove(creature);
            double dist = 5.0D;
            if (args.length > 1) {
                try {
                    dist = Double.parseDouble(args[1]);
                } catch (NumberFormatException ignored) {}
            }
            float yaw = creature.rotationYawHead;
            double dx = MathHelper.sin(yaw * (float) Math.PI / 180.0F) * dist;
            double dz = -MathHelper.cos(yaw * (float) Math.PI / 180.0F) * dist;
            if (creature.getNavigator() != null) {
                creature.getNavigator().tryMoveToXYZ(creature.posX + dx, creature.posY, creature.posZ + dz, 1.2D);
            }
            ChatUtil.sendPlain(player, EnumChatFormatting.GREEN + creature.getCommandSenderName() + " retrocediendo " + dist + " bloques.");
            return;
        }

        if (sub.equals("coord") || sub.equals("ir")) {
            SymbolEffectImperio.IMPERIO_STAYING_TARGETS.remove(creature);
            if (args.length >= 4) {
                try {
                    double x = Double.parseDouble(args[1]);
                    double y = Double.parseDouble(args[2]);
                    double z = Double.parseDouble(args[3]);
                    if (creature.getNavigator() != null) {
                        creature.getNavigator().tryMoveToXYZ(x, y, z, 1.2D);
                    }
                    ChatUtil.sendPlain(player, EnumChatFormatting.GREEN + creature.getCommandSenderName() + " marchando hacia (" + (int)x + ", " + (int)y + ", " + (int)z + ").");
                    return;
                } catch (NumberFormatException ignored) {}
            }
            ChatUtil.sendPlain(player, EnumChatFormatting.RED + "Uso: /imperio coord <x> <y> <z>");
            return;
        }

        ChatUtil.sendPlain(player, EnumChatFormatting.RED + "Subcomando desconocido. Usa /imperio para ver los comandos.");
    }
}
