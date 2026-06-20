package com.emoniph.witchery.commands;

import com.emoniph.witchery.infusion.infusions.symbols.SymbolEffectImperio;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandImperio extends CommandBase {

    @Override
    public String getCommandName() {
        return "imperio";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/imperio <adelante|atras|atacar|cord|inventario|quieto|liberar> [argumentos]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; 
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayer;
    }

    private EntityLivingBase getLookedAtEntity(EntityPlayer player, double range) {
        Vec3 vec3 = player.getPosition(1.0F);
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
            player.addChatMessage(new ChatComponentText("Uso: " + getCommandUsage(sender)));
            return;
        }

        String subCommand = args[0].toLowerCase();

        // 1. Get the entity we are looking at (RayTrace)
        EntityLivingBase lookedAtEntity = getLookedAtEntity(player, 30.0D);

        // Fetch all controlled entities for context
        List<EntityLivingBase> controlledEntities = new ArrayList<EntityLivingBase>();
        for (Map.Entry<EntityLivingBase, EntityPlayer> entry : SymbolEffectImperio.IMPERIO_TARGETS.entrySet()) {
            if (entry.getValue() == player && entry.getKey().isEntityAlive()) {
                controlledEntities.add(entry.getKey());
            }
        }

        if (controlledEntities.isEmpty()) {
            player.addChatMessage(new ChatComponentText("No tienes ninguna criatura bajo tu control mental."));
            return;
        }

        // Logic for ATACAR (Target Enemy)
        if (subCommand.equals("atacar")) {
            if (lookedAtEntity == null) {
                player.addChatMessage(new ChatComponentText("Debes mirar al enemigo al que quieres que tu ejército ataque."));
                return;
            }
            if (controlledEntities.contains(lookedAtEntity)) {
                player.addChatMessage(new ChatComponentText("No puedes ordenarles atacar a un aliado de la mente enjambre."));
                return;
            }
            int attackCount = 0;
            for (EntityLivingBase minion : controlledEntities) {
                if (!SymbolEffectImperio.IMPERIO_STAYING_TARGETS.contains(minion) && minion instanceof net.minecraft.entity.EntityLiving) {
                    ((net.minecraft.entity.EntityLiving) minion).setAttackTarget(lookedAtEntity);
                    attackCount++;
                }
            }
            player.addChatMessage(new ChatComponentText(attackCount + " criaturas han fijado al objetivo."));
            return;
        }

        // The rest of the commands REQUIRE the looked-at entity to be an ALIEN CONTROLLED BY US
        if (lookedAtEntity == null || !controlledEntities.contains(lookedAtEntity)) {
            player.addChatMessage(new ChatComponentText("Debes mirar directamente a una de tus criaturas para darle esta orden."));
            return;
        }

        EntityLivingBase target = lookedAtEntity;

        if (subCommand.equals("adelante") || subCommand.equals("atras")) {
            SymbolEffectImperio.IMPERIO_STAYING_TARGETS.remove(target);
            int steps = 1;
            if (args.length > 1) {
                try { steps = Integer.parseInt(args[1]); } catch (NumberFormatException e) {}
            }
            int multiplier = subCommand.equals("adelante") ? 1 : -1;
            
            if (target instanceof net.minecraft.entity.EntityLiving) {
                double destX = target.posX + target.getLookVec().xCoord * steps * multiplier;
                double destY = target.posY;
                double destZ = target.posZ + target.getLookVec().zCoord * steps * multiplier;
                ((net.minecraft.entity.EntityLiving) target).getNavigator().tryMoveToXYZ(destX, destY, destZ, 1.0D);
            } else if (target instanceof EntityPlayer) {
                target.setPositionAndUpdate(target.posX + target.getLookVec().xCoord * steps * multiplier, target.posY, target.posZ + target.getLookVec().zCoord * steps * multiplier);
            }
            player.addChatMessage(new ChatComponentText("La criatura avanza " + steps + " pasos."));

        } else if (subCommand.equals("cord")) {
            SymbolEffectImperio.IMPERIO_STAYING_TARGETS.remove(target);
            if (args.length < 4) {
                player.addChatMessage(new ChatComponentText("Uso: /imperio cord <x> <y> <z>"));
                return;
            }
            try {
                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);
                int z = Integer.parseInt(args[3]);
                if (target instanceof net.minecraft.entity.EntityLiving) {
                    ((net.minecraft.entity.EntityLiving) target).getNavigator().tryMoveToXYZ(x, y, z, 1.0D);
                } else if (target instanceof EntityPlayer) {
                    target.setPositionAndUpdate(x, y, z);
                }
                player.addChatMessage(new ChatComponentText("Criatura dirigida a " + x + ", " + y + ", " + z));
            } catch (NumberFormatException e) {
                player.addChatMessage(new ChatComponentText("Coordenadas inválidas."));
            }

        } else if (subCommand.equals("quieto")) {
            SymbolEffectImperio.IMPERIO_STAYING_TARGETS.add(target);
            if (target instanceof net.minecraft.entity.EntityLiving) {
                ((net.minecraft.entity.EntityLiving) target).setAttackTarget(null);
                ((net.minecraft.entity.EntityLiving) target).getNavigator().clearPathEntity();
            }
            player.addChatMessage(new ChatComponentText("La criatura esperará aquí estática y no atacará a nadie."));

        } else if (subCommand.equals("liberar")) {
            SymbolEffectImperio.IMPERIO_TARGETS.remove(target);
            SymbolEffectImperio.IMPERIO_STAYING_TARGETS.remove(target);
            target.removePotionEffect(com.emoniph.witchery.Witchery.Potions.PARALYSED.id);
            player.addChatMessage(new ChatComponentText("Has liberado a la criatura de tu control."));

        } else if (subCommand.equals("inventario")) {
            if (target instanceof EntityPlayer) {
                player.displayGUIChest(((EntityPlayer) target).inventory);
            } else {
                player.displayGUIChest(new com.emoniph.witchery.infusion.infusions.symbols.InventoryMobEquipment(target));
            }
        } else {
            player.addChatMessage(new ChatComponentText("Comando desconocido."));
        }
    }
}
