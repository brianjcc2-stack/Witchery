package com.emoniph.witchery.commands;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.common.ExtendedPlayer;
import com.emoniph.witchery.common.Shapeshift;
import com.emoniph.witchery.common.SupremeWitchData;
import com.emoniph.witchery.entity.EntityLostSoul;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import com.emoniph.witchery.util.TimeUtil;
import com.emoniph.witchery.util.TransformCreature;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.List;

public class CommandSpirit extends CommandBase {

    @Override
    public String getCommandName() {
        return "spirit";
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("espiritu", "astral");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/spirit <form|subdued|astral|summon|invisible|vault|status|level <0-10>|help>";
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
            return getListOfStringsMatchingLastWord(args, "form", "subdued", "dementor", "auror", "astral", "summon", "invisible", "sigilo", "vault", "relicario", "status", "level", "help", "forma", "humo", "invocar", "proyeccion", "estado", "nivel", "ayuda", "guia");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("subdued") || args[0].equalsIgnoreCase("orbe") || args[0].equalsIgnoreCase("form") || args[0].equalsIgnoreCase("forma"))) {
            return getListOfStringsMatchingLastWord(args, "dementor", "auror", "auto", "human", "humano");
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) sender;
        ExtendedPlayer playerEx = ExtendedPlayer.get(player);
        if (playerEx == null) return;

        boolean isSupreme = SupremeWitchData.isPlayerSupreme(player);
        int lvl = playerEx.getEffectiveSpiritLevel();

        if (args.length == 0) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╔════════════════════════════════════════════╗"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "  ★ MAGIA DEL PLANO ESPIRITUAL ★"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╚════════════════════════════════════════════╝"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Tu Nivel de Espíritu: " + EnumChatFormatting.AQUA + lvl + "/10" + (isSupreme ? EnumChatFormatting.GOLD + " (Bruja Suprema)" : "")));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "/spirit subdued [dementor|auror|auto]" + EnumChatFormatting.GRAY + " - Vuelo de Humo de Harry Potter (Nvl 3)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "/spirit summon" + EnumChatFormatting.GRAY + " - Invoca espíritus guardianes aliados (Req. Nivel 5)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "/spirit invisible" + EnumChatFormatting.GRAY + " - Manto de invisibilidad astral / sigilo (Req. Nivel 6)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "/spirit relicario" + EnumChatFormatting.GRAY + " - Abre tu Relicario del Vacío [54 slots] (Req. Nivel 8)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "/spirit astral" + EnumChatFormatting.GRAY + " - Proyección Astral y Posesión de Criaturas (Req. Nivel 10)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "/spirit status" + EnumChatFormatting.GRAY + " - Consulta tus bendiciones y habilidades espirituales."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "/spirit level <0-10>" + EnumChatFormatting.GRAY + " - Ajusta tu nivel de espíritu."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "/spirit guia" + EnumChatFormatting.GRAY + " - Guía detallada de la senda espiritual."));
            return;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("help") || sub.equals("ayuda") || sub.equals("guia")) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "=== Senda del Espíritu (Desbloqueos y Progresión) ==="));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "--- Límite Terrenal (Cualquier Bruja: Niveles 1–3) ---"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "• Nivel 1-2: " + EnumChatFormatting.WHITE + "Percepción etérea de espectros y sintonía del Mundo de los Sueños."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "• Nivel 3: " + EnumChatFormatting.WHITE + "Vuelo de Humo Dementor/Auror (/spirit subdued) — Vence a una Pesadilla."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "--- Trascendencia Suprema (Exclusivo Bruja Suprema: Niveles 4–10) ---"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "• Nivel 4: " + EnumChatFormatting.WHITE + "Vínculo del Más Allá — Canalización de energía de infusión amplificada."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "• Nivel 5: " + EnumChatFormatting.WHITE + "Danza de Espíritus Guardianes (/spirit summon) — Derrota 3 espectros."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "• Nivel 6: " + EnumChatFormatting.WHITE + "Manto de Sigilo Espectral (/spirit invisible o agacharse con Shift)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "• Nivel 7: " + EnumChatFormatting.WHITE + "Resistencia Etérea (Inmunidad a asfixia y ralentizaciones) — Purga 6 espectros."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "• Nivel 8: " + EnumChatFormatting.WHITE + "Relicario del Vacío (Cofre dimensional 54 slots) — Rito Ladrón de Almas o Demonio."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "• Nivel 9: " + EnumChatFormatting.WHITE + "Paso Etéreo (Atravesar muros sólidos) — Crea un Horrocrux o vence a Cazador/Muerte."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "• Nivel 10: " + EnumChatFormatting.WHITE + "Proyección Astral & Posesión de Criaturas (/spirit astral o 'ex corpus') — Vence a Baba Yaga."));
            return;
        }

        if (sub.equals("form") || sub.equals("forma") || sub.equals("transformar")) {
            if (args.length >= 2) {
                String type = args[1].toLowerCase();
                if (type.equals("subdued") || type.equals("orbe") || type.equals("wisp") || type.equals("subyugado") || type.equals("dementor") || type.equals("auror") || type.equals("humo")) {
                    if (lvl < 3 && !isSupreme) {
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Necesitas nivel de Espíritu 3 o superior para el Vuelo de Humo. Tu nivel: " + lvl));
                        return;
                    }
                    if (type.equals("dementor")) playerEx.setSubduedSpiritStyle(1);
                    else if (type.equals("auror")) playerEx.setSubduedSpiritStyle(2);

                    Shapeshift.INSTANCE.shiftTo(player, TransformCreature.SUBDUED_SPIRIT);
                    player.worldObj.playSoundAtEntity(player, "witchery:mob.spirit.say", 1.0F, 1.2F);
                    player.worldObj.playSoundAtEntity(player, "mob.bat.takeoff", 1.0F, 0.7F);
                    int st = playerEx.getSubduedSpiritStyle();
                    ParticleEffect burstEffect = (st == 2) ? ParticleEffect.CLOUD : ParticleEffect.LARGE_SMOKE;
                    burstEffect.send(SoundEffect.NONE, player.worldObj, player.posX, player.posY + 1.0D, player.posZ, 0.8D, 1.0D, 16);
                    String styleName = st == 1 ? "Dementor (Humo Oscuro)" : (st == 2 ? "Auror (Humo Luminoso)" : "Automático");
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "★ " + EnumChatFormatting.AQUA + "¡Vuelo de Humo Activo [" + EnumChatFormatting.GOLD + styleName + EnumChatFormatting.AQUA + "]!"));
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "• Salta para despegar y usa tu mirada para guiar el vuelo."));
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "• Pulsa " + EnumChatFormatting.YELLOW + "ESPACIO" + EnumChatFormatting.GRAY + " en el aire para hacer sprints rápidos (dashes)."));
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "• Solo te detienes tocando el suelo o picando con Shift."));
                    return;
                } else if (type.equals("human") || type.equals("humano") || type.equals("mortal") || type.equals("normal") || type.equals("none")) {
                    Shapeshift.INSTANCE.shiftTo(player, TransformCreature.NONE);
                    if (!player.isPotionActive(Potion.invisibility)) {
                        player.setInvisible(false);
                    }
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Has regresado a tu forma terrenal."));
                    return;
                }
            }

            TransformCreature current = playerEx.getCreatureType();
            if (current == TransformCreature.SUBDUED_SPIRIT || current == TransformCreature.SPIRIT) {
                Shapeshift.INSTANCE.shiftTo(player, TransformCreature.NONE);
                if (!player.isPotionActive(Potion.invisibility)) {
                    player.setInvisible(false);
                }
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Has regresado a tu forma terrenal."));
            } else {
                if (lvl >= 3 || isSupreme) {
                    Shapeshift.INSTANCE.shiftTo(player, TransformCreature.SUBDUED_SPIRIT);
                    player.worldObj.playSoundAtEntity(player, "witchery:mob.spirit.say", 1.0F, 1.2F);
                    player.worldObj.playSoundAtEntity(player, "mob.bat.takeoff", 1.0F, 0.7F);
                    int st = playerEx.getSubduedSpiritStyle();
                    ParticleEffect burstEffect = (st == 2) ? ParticleEffect.CLOUD : ParticleEffect.LARGE_SMOKE;
                    burstEffect.send(SoundEffect.NONE, player.worldObj, player.posX, player.posY + 1.0D, player.posZ, 0.8D, 1.0D, 16);
                    String styleName = st == 1 ? "Dementor (Humo Oscuro)" : (st == 2 ? "Auror (Humo Luminoso)" : "Automático");
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "★ " + EnumChatFormatting.AQUA + "¡Vuelo de Humo Activo [" + EnumChatFormatting.GOLD + styleName + EnumChatFormatting.AQUA + "]!"));
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "• Salta para despegar y usa tu mirada para guiar el vuelo."));
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "• Pulsa " + EnumChatFormatting.YELLOW + "ESPACIO" + EnumChatFormatting.GRAY + " en el aire para hacer sprints rápidos (dashes)."));
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "• Solo te detienes tocando el suelo o picando con Shift."));
                } else {
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Necesitas al menos nivel de Espíritu 3 para adoptar el Vuelo de Humo. Tu nivel: " + lvl));
                }
            }
            return;
        }

        if (sub.equals("human") || sub.equals("humano") || sub.equals("mortal") || sub.equals("normal") || sub.equals("none")) {
            Shapeshift.INSTANCE.shiftTo(player, TransformCreature.NONE);
            if (!player.isPotionActive(Potion.invisibility)) {
                player.setInvisible(false);
            }
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Has regresado a tu forma terrenal."));
            return;
        }

        if (sub.equals("subdued") || sub.equals("orbe") || sub.equals("wisp") || sub.equals("subyugado") || sub.equals("dementor") || sub.equals("auror") || sub.equals("humo")) {
            if (lvl < 3 && !isSupreme) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Necesitas nivel de Espíritu 3 o superior para el Vuelo de Humo. Tu nivel: " + lvl));
                return;
            }

            int requestedStyle = -1;
            if (sub.equals("dementor")) {
                requestedStyle = 1;
            } else if (sub.equals("auror")) {
                requestedStyle = 2;
            } else if (args.length >= 2) {
                String a = args[1].toLowerCase();
                if (a.equals("dementor") || a.equals("dark") || a.equals("oscuro")) requestedStyle = 1;
                else if (a.equals("auror") || a.equals("light") || a.equals("luz")) requestedStyle = 2;
                else if (a.equals("auto") || a.equals("normal")) requestedStyle = 0;
            }

            if (requestedStyle != -1) {
                playerEx.setSubduedSpiritStyle(requestedStyle);
            }

            if (playerEx.getCreatureType() == TransformCreature.SUBDUED_SPIRIT && requestedStyle == -1) {
                Shapeshift.INSTANCE.shiftTo(player, TransformCreature.NONE);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Has regresado a tu forma terrenal."));
            } else {
                Shapeshift.INSTANCE.shiftTo(player, TransformCreature.SUBDUED_SPIRIT);
                player.worldObj.playSoundAtEntity(player, "witchery:mob.spirit.say", 1.0F, 1.2F);
                player.worldObj.playSoundAtEntity(player, "mob.bat.takeoff", 1.0F, 0.7F);
                ParticleEffect.LARGE_SMOKE.send(SoundEffect.NONE, player.worldObj, player.posX, player.posY + 1.0D, player.posZ, 0.8D, 1.0D, 16);
                int st = playerEx.getSubduedSpiritStyle();
                String styleName = st == 1 ? "Dementor (Humo Oscuro)" : (st == 2 ? "Auror (Humo Luminoso)" : "Automático");
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "★ " + EnumChatFormatting.AQUA + "¡Vuelo de Humo Activo [" + EnumChatFormatting.GOLD + styleName + EnumChatFormatting.AQUA + "]!"));
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "• Salta para despegar y usa tu mirada para guiar el vuelo."));
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "• Pulsa " + EnumChatFormatting.YELLOW + "ESPACIO" + EnumChatFormatting.GRAY + " en el aire para hacer sprints rápidos (dashes)."));
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "• Solo te detienes tocando el suelo o picando con Shift."));
            }
            if ((lvl >= 3 || isSupreme) && playerEx.getCreatureType() == TransformCreature.SUBDUED_SPIRIT) {
                SupremeWitchData.completeWonder(player, SupremeWitchData.WONDER_DESCENSUM);
            }
            return;
        }

        if (sub.equals("summon") || sub.equals("invocar") || sub.equals("attack") || sub.equals("atacar")) {
            if (lvl < 5 && !isSupreme) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Necesitas nivel de Espíritu 5 o superior para invocar espíritus guardianes. Tu nivel: " + lvl));
                return;
            }

            int count = 4;
            EntityLivingBase targetEnemy = null;
            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(player.posX - 16.0D, player.posY - 8.0D, player.posZ - 16.0D, player.posX + 16.0D, player.posY + 8.0D, player.posZ + 16.0D);
            List entities = player.worldObj.getEntitiesWithinAABB(EntityMob.class, aabb);
            if (!entities.isEmpty()) {
                targetEnemy = (EntityLivingBase) entities.get(player.worldObj.rand.nextInt(entities.size()));
            }

            for (int i = 0; i < count; i++) {
                double angle = Math.toRadians((double) i * (360.0D / (double) count));
                double spawnX = player.posX + Math.cos(angle) * 2.5D;
                double spawnY = player.posY + 0.5D;
                double spawnZ = player.posZ + Math.sin(angle) * 2.5D;

                EntityLostSoul soul = (EntityLostSoul) Infusion.spawnCreature(player.worldObj, EntityLostSoul.class, (int) spawnX, (int) spawnY, (int) spawnZ, null, 0, 0, ParticleEffect.INSTANT_SPELL, SoundEffect.RANDOM_POP);
                if (soul != null) {
                    soul.setSummoner(player);
                    soul.setSoulType(i % 3);
                    soul.setTimeToLive(TimeUtil.secsToTicks(50));
                    if (targetEnemy != null) {
                        soul.setAttackTarget(targetEnemy);
                    }
                }
            }

            player.worldObj.playSoundAtEntity(player, "witchery:mob.spirit.say", 1.2F, 0.9F);
            ParticleEffect.PORTAL.send(SoundEffect.RANDOM_ORB, player.worldObj, player.posX, player.posY + 1.0D, player.posZ, 2.0D, 2.0D, 30);
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "¡Has desatado una danza de espíritus subyugados para que luchen por ti!"));
            if (targetEnemy != null) {
                playerEx.tryAdvanceSpiritMilestone(6, "Comandar espíritus guardianes en batalla");
            }
            return;
        }

        if (sub.equals("invisible") || sub.equals("sigilo") || sub.equals("invis")) {
            boolean isAstral = playerEx.isAstralProjecting();
            boolean isSpiritForm = playerEx.getCreatureType() == TransformCreature.SPIRIT || playerEx.getCreatureType() == TransformCreature.SUBDUED_SPIRIT || isAstral;
            if (!isSpiritForm && lvl < 6 && !isSupreme) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "El manto de invisibilidad requiere estar en Proyección Astral o forma espiritual (o Nivel de Espíritu 6+)."));
                return;
            }
            if (player.isPotionActive(Potion.invisibility)) {
                player.removePotionEffect(Potion.invisibility.id);
                player.setInvisible(false);
                player.worldObj.playSoundAtEntity(player, "witchery:mob.spirit.say", 0.8F, 1.3F);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "✦ " + EnumChatFormatting.WHITE + "Has " + EnumChatFormatting.YELLOW + "desactivado" + EnumChatFormatting.WHITE + " el manto de invisibilidad. Tu forma etérea vuelve a ser visible."));
            } else {
                player.addPotionEffect(new net.minecraft.potion.PotionEffect(Potion.invisibility.id, 72000, 0, true));
                player.worldObj.playSoundAtEntity(player, "mob.bat.takeoff", 0.8F, 0.6F);
                ParticleEffect.LARGE_SMOKE.send(SoundEffect.NONE, player.worldObj, player.posX, player.posY + 1.0D, player.posZ, 0.5D, 0.5D, 12);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "✦ " + EnumChatFormatting.LIGHT_PURPLE + "Has " + EnumChatFormatting.GREEN + "activado" + EnumChatFormatting.LIGHT_PURPLE + " el manto de invisibilidad astral. Ahora eres completamente invisible."));
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "(Escribe /spirit invisible de nuevo o sal de la proyección para desactivarlo)."));
            }
            return;
        }

        if (sub.equals("vault") || sub.equals("relicario") || sub.equals("cofre") || sub.equals("boveda")) {
            if (lvl < 8 && !isSupreme) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "El Relicario del Vacío requiere nivel de Espíritu 8 o superior. Tu nivel: " + lvl));
                return;
            }
            player.displayGUIChest(playerEx.getVoidVault());
            SoundEffect.MOB_ENDERMEN_PORTAL.playOnlyTo(player, 0.8F, 1.4F);
            ParticleEffect.PORTAL.send(SoundEffect.NONE, player, 0.5D, 1.0D, 16);
            if (!player.inventory.hasItem(Witchery.Items.VOID_LOCKET)) {
                ItemStack locket = new ItemStack(Witchery.Items.VOID_LOCKET);
                if (!player.inventory.addItemStackToInventory(locket)) {
                    player.dropPlayerItemWithRandomChoice(locket, false);
                }
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "✦ Se te ha entregado el [Relicario del Vacío] en tu inventario."));
            }
            return;
        }

        if (sub.equals("astral") || sub.equals("corpus") || sub.equals("proyeccion")) {
            if (lvl < 10 && !isSupreme) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Necesitas maestría de Espíritu 10 para la Proyección Astral. Tu nivel: " + lvl));
                return;
            }
            ExtendedPlayer.trySayAstralProjection(player, "astral");
            boolean astral = playerEx.isAstralProjecting();
            player.addChatMessage(new ChatComponentText(astral ?
                    EnumChatFormatting.LIGHT_PURPLE + "¡Tu espíritu se ha desprendido de tu cuerpo carnal! Vuela libremente por el plano astral." :
                    EnumChatFormatting.YELLOW + "Tu espíritu se ha reintegrado en tu cuerpo físico."));
            return;
        }

        if (sub.equals("level") || sub.equals("nivel")) {
            if (args.length < 2) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "Nivel actual de Espíritu: " + lvl + "/10"));
                return;
            }
            try {
                int newLvl = Integer.parseInt(args[1]);
                if (newLvl < 0) newLvl = 0;
                if (newLvl > 10) newLvl = 10;
                if (newLvl > 3 && !isSupreme && !player.capabilities.isCreativeMode) {
                    newLvl = 3;
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "El límite espiritual terrenal es Nivel 3. Solo la Bruja Suprema puede trascender a los niveles 4–10."));
                }
                playerEx.setSpiritLevel(newLvl);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Nivel de Espíritu ajustado a: " + newLvl + "/10"));
            } catch (NumberFormatException e) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Uso: /spirit level <0-10>"));
            }
            return;
        }

        if (sub.equals("status") || sub.equals("estado")) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╔════════════════════════════════════════════╗"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "  ★ ESTADO DEL PODER ESPIRITUAL ★"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╚════════════════════════════════════════════╝"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Nivel de Espíritu: " + EnumChatFormatting.AQUA + lvl + "/10" + (isSupreme ? EnumChatFormatting.GOLD + " (Bruja Suprema)" : "")));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Forma activa: " + EnumChatFormatting.WHITE + playerEx.getCreatureType()));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Proyección Astral: " + (playerEx.isAstralProjecting() ? EnumChatFormatting.GREEN + "Activa" : EnumChatFormatting.RED + "Inactiva")));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Habilidades desbloqueadas:"));
            player.addChatMessage(new ChatComponentText((lvl >= 3 || isSupreme ? EnumChatFormatting.GREEN + "  ✔ " : EnumChatFormatting.RED + "  ✖ ") + EnumChatFormatting.WHITE + "Nvl 3: Vuelo de Humo Dementor/Auror (/spirit subdued)"));
            player.addChatMessage(new ChatComponentText((lvl >= 5 || isSupreme ? EnumChatFormatting.GREEN + "  ✔ " : EnumChatFormatting.RED + "  ✖ ") + EnumChatFormatting.WHITE + "Nvl 5: Invocación de Espíritus Guardianes (/spirit summon)"));
            player.addChatMessage(new ChatComponentText((lvl >= 6 || isSupreme ? EnumChatFormatting.GREEN + "  ✔ " : EnumChatFormatting.RED + "  ✖ ") + EnumChatFormatting.WHITE + "Nvl 6: Manto de Sigilo Espectral (/spirit invisible)"));
            player.addChatMessage(new ChatComponentText((lvl >= 7 || isSupreme ? EnumChatFormatting.GREEN + "  ✔ " : EnumChatFormatting.RED + "  ✖ ") + EnumChatFormatting.WHITE + "Nvl 7: Resistencia Etérea (Inmunidad a Asfixia)"));
            player.addChatMessage(new ChatComponentText((lvl >= 8 || isSupreme ? EnumChatFormatting.GREEN + "  ✔ " : EnumChatFormatting.RED + "  ✖ ") + EnumChatFormatting.WHITE + "Nvl 8: Relicario del Vacío [54 slots] (/spirit relicario)"));
            player.addChatMessage(new ChatComponentText((lvl >= 9 || isSupreme ? EnumChatFormatting.GREEN + "  ✔ " : EnumChatFormatting.RED + "  ✖ ") + EnumChatFormatting.WHITE + "Nvl 9: Paso Etéreo (Atravesar Muros)"));
            player.addChatMessage(new ChatComponentText((lvl >= 10 || isSupreme ? EnumChatFormatting.GREEN + "  ✔ " : EnumChatFormatting.RED + "  ✖ ") + EnumChatFormatting.WHITE + "Nvl 10: Proyección Astral y Posesión de Criaturas (/spirit astral)"));
            return;
        }

        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Comando desconocido. Usa: /spirit <form|subdued|summon|invisible|vault|astral|status|level|help>"));
    }
}
