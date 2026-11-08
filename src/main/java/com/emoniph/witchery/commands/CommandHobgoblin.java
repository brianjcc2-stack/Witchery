package com.emoniph.witchery.commands;

import com.emoniph.witchery.entity.EntityGoblin;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.Arrays;
import java.util.List;

public class CommandHobgoblin extends CommandBase {

    @Override
    public String getCommandName() {
        return "hobgoblin";
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("goblin");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/hobgoblin <follow|stay|work|attack|release|inventory> [all] (o <seguir|quieto|trabajar|atacar|liberar|mochila>)";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "follow", "stay", "work", "attack", "release", "inventory",
                    "seguir", "quieto", "trabajar", "atacar", "liberar", "mochila");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("stay") || args[0].equalsIgnoreCase("follow") || args[0].equalsIgnoreCase("work")
                || args[0].equalsIgnoreCase("quieto") || args[0].equalsIgnoreCase("seguir") || args[0].equalsIgnoreCase("trabajar"))) {
            return getListOfStringsMatchingLastWord(args, "all");
        }
        return null;
    }

    private EntityLivingBase getLookedAtEntity(EntityPlayer player, double range) {
        Vec3 vec3 = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
        vec3.yCoord += player.getEyeHeight();
        Vec3 vec31 = player.getLook(1.0F);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);

        EntityLivingBase pointedEntity = null;
        List list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.addCoord(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range).expand(1.0D, 1.0D, 1.0D));
        double d2 = range;

        for (int i = 0; i < list.size(); ++i) {
            net.minecraft.entity.Entity entity = (net.minecraft.entity.Entity)list.get(i);
            if (entity.canBeCollidedWith() && entity instanceof EntityLivingBase) {
                float f1 = entity.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double)f1, (double)f1, (double)f1);
                MovingObjectPosition mop = axisalignedbb.calculateIntercept(vec3, vec32);
                if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0D) {
                        pointedEntity = (EntityLivingBase)entity;
                        d2 = 0.0D;
                    }
                } else if (mop != null) {
                    double d3 = vec3.distanceTo(mop.hitVec);
                    if (d3 < d2 || d2 == 0.0D) {
                        pointedEntity = (EntityLivingBase)entity;
                        d2 = d3;
                    }
                }
            }
        }
        return pointedEntity;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) sender;

        if (args.length == 0) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "=== Control de Hobgoblins ==="));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin work [all]" + EnumChatFormatting.GRAY + " (o trabajar) - Cosecha, replanta y guarda en cofres."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin follow [all]" + EnumChatFormatting.GRAY + " (o seguir) - Te sigue lealmente."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin stay [all]" + EnumChatFormatting.GRAY + " (o quieto) - Se queda en guardia en el lugar."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin attack" + EnumChatFormatting.GRAY + " (o atacar) - Ataca al objetivo que estás mirando."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin inventory" + EnumChatFormatting.GRAY + " (o mochila) - Abre su mochila o equipo (con sneak)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin release" + EnumChatFormatting.GRAY + " (o liberar) - Libera al hobgoblin."));
            return;
        }

        String subCommand = args[0].toLowerCase();
        EntityLivingBase lookedAtEntity = getLookedAtEntity(player, 30.0D);

        if (subCommand.equals("attack") || subCommand.equals("atacar")) {
            if (lookedAtEntity == null) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Debes mirar al enemigo que deseas que ataquen tus hobgoblins."));
                return;
            }

            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(player.posX - 30.0D, player.posY - 30.0D, player.posZ - 30.0D, player.posX + 30.0D, player.posY + 30.0D, player.posZ + 30.0D);
            List<EntityGoblin> goblins = player.worldObj.getEntitiesWithinAABB(EntityGoblin.class, bb);
            
            int attackCount = 0;
            for (EntityGoblin goblin : goblins) {
                if (goblin.isTamed && player.getCommandSenderName().equalsIgnoreCase(goblin.tamedOwnerName) && !goblin.isSitting) {
                    goblin.setAttackTarget(lookedAtEntity);
                    attackCount++;
                }
            }
            
            if (attackCount > 0) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "" + attackCount + " hobgoblins han fijado el objetivo."));
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "No hay hobgoblins domesticados cerca que puedan atacar."));
            }
            return;
        }

        boolean allStay = (subCommand.equals("stay") || subCommand.equals("quieto") || subCommand.equals("esperar")) && (args.length > 1 && args[1].equalsIgnoreCase("all"));
        boolean allFollow = (subCommand.equals("follow") || subCommand.equals("seguir")) && (args.length > 1 && args[1].equalsIgnoreCase("all"));
        boolean allWork = (subCommand.equals("work") || subCommand.equals("trabajar") || subCommand.equals("curro")) && (args.length > 1 && args[1].equalsIgnoreCase("all"));

        if (allStay || allFollow || allWork) {
            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(player.posX - 32.0D, player.posY - 16.0D, player.posZ - 32.0D, player.posX + 32.0D, player.posY + 16.0D, player.posZ + 32.0D);
            List<EntityGoblin> goblins = player.worldObj.getEntitiesWithinAABB(EntityGoblin.class, bb);
            int count = 0;
            for (EntityGoblin g : goblins) {
                if (g.isTamed && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                    if (allStay) {
                        g.isSitting = true;
                        g.isFollowing = false;
                        g.isWorking = false;
                        g.setAttackTarget(null);
                        g.getNavigator().clearPathEntity();
                    } else if (allFollow) {
                        g.isSitting = false;
                        g.isFollowing = true;
                        g.isWorking = false;
                    } else if (allWork) {
                        g.isSitting = false;
                        g.isFollowing = false;
                        g.isWorking = true;
                        g.setAttackTarget(null);
                    }
                    count++;
                }
            }
            if (allStay) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "" + count + " hobgoblins ahora están esperando aquí."));
            } else if (allFollow) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "" + count + " hobgoblins ahora te están siguiendo."));
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "" + count + " hobgoblins ahora están trabajando en la zona (cosecha y minería)."));
            }
            return;
        }

        // For individual commands, find looked-at or nearest hobgoblin
        if (lookedAtEntity == null || !(lookedAtEntity instanceof EntityGoblin)) {
            AxisAlignedBB searchBB = AxisAlignedBB.getBoundingBox(player.posX - 8.0D, player.posY - 4.0D, player.posZ - 8.0D, player.posX + 8.0D, player.posY + 4.0D, player.posZ + 8.0D);
            List<EntityGoblin> nearbyGoblins = player.worldObj.getEntitiesWithinAABB(EntityGoblin.class, searchBB);
            EntityGoblin closest = null;
            double closestDistSq = Double.MAX_VALUE;
            for (EntityGoblin g : nearbyGoblins) {
                if (g.isTamed && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                    double dSq = g.getDistanceSqToEntity(player);
                    if (dSq < closestDistSq) {
                        closestDistSq = dSq;
                        closest = g;
                    }
                }
            }
            if (closest != null) {
                lookedAtEntity = closest;
            }
        }

        if (lookedAtEntity == null || !(lookedAtEntity instanceof EntityGoblin)) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Debes mirar a uno de tus hobgoblins o estar cerca de uno para darle esta orden."));
            return;
        }

        EntityGoblin goblin = (EntityGoblin) lookedAtEntity;

        if (!goblin.isTamed || goblin.tamedOwnerName == null || !goblin.tamedOwnerName.equalsIgnoreCase(player.getCommandSenderName())) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Este hobgoblin no te pertenece. No obedecerá tus órdenes."));
            return;
        }

        if (subCommand.equals("stay") || subCommand.equals("quieto") || subCommand.equals("esperar")) {
            goblin.isSitting = true;
            goblin.isFollowing = false;
            goblin.isWorking = false;
            goblin.setAttackTarget(null);
            goblin.getNavigator().clearPathEntity();
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "El hobgoblin esperará aquí."));
        } else if (subCommand.equals("follow") || subCommand.equals("seguir")) {
            goblin.isSitting = false;
            goblin.isFollowing = true;
            goblin.isWorking = false;
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "El hobgoblin te seguirá y protegerá."));
        } else if (subCommand.equals("work") || subCommand.equals("trabajar") || subCommand.equals("curro")) {
            goblin.isSitting = false;
            goblin.isFollowing = false;
            goblin.isWorking = true;
            goblin.setAttackTarget(null);
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "El hobgoblin ahora está trabajando en la zona (cosecha y minería)."));
        } else if (subCommand.equals("release") || subCommand.equals("liberar")) {
            goblin.isTamed = false;
            goblin.tamedOwnerName = "";
            goblin.isSitting = false;
            goblin.isFollowing = false;
            goblin.isWorking = false;
            goblin.setAttackTarget(null);
            goblin.getNavigator().clearPathEntity();
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Has liberado al hobgoblin."));
        } else if (subCommand.equals("inventory") || subCommand.equals("inv") || subCommand.equals("mochila") || subCommand.equals("inventario")) {
            if (player.isSneaking()) {
                player.displayGUIChest(new com.emoniph.witchery.infusion.infusions.symbols.InventoryMobEquipment(goblin));
            } else {
                player.displayGUIChest(goblin.goblinInventory);
            }
        } else {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Comando desconocido. Opciones: follow, stay, work, attack, release, inventory."));
        }
    }
}
