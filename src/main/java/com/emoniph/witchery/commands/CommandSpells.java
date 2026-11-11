package com.emoniph.witchery.commands;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.common.SupremeWitchData;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.infusion.infusions.symbols.EffectRegistry;
import com.emoniph.witchery.infusion.infusions.symbols.SymbolEffect;
import com.emoniph.witchery.util.TimeUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CommandSpells extends CommandBase {

    @Override
    public String getCommandName() {
        return "spells";
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("hechizos", "grimorio");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/spells <combate|defensa|utilidad|oscuras|naturaleza|suprema|buscar <nombre>|aprendidos|estudiar|learnall|guia>";
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
            return getListOfStringsMatchingLastWord(args,
                    "combate", "defensa", "utilidad", "oscuras", "naturaleza", "suprema",
                    "buscar", "info", "aprendidos", "dominados", "estudiar", "study", "learnall", "guia", "help", "ayuda");
        }
        return null;
    }

    private static int getSchoolForEffect(SymbolEffect effect) {
        int id = effect.getEffectID();
        // 6: Suprema / Legendarios
        if (id == 80 || id == 81 || id == 82 || id == 91 || id == 93 || id == 99 || id == 100) {
            return 6;
        }
        // 4: Artes Oscuras / Maldiciones
        if (effect.isCurse() || id == 4 || id == 10 || id == 19 || id == 22 || id == 54 || id == 63 || id == 78 || id == 90 || id == 95) {
            return 4;
        }
        // 2: Defensa / Curación / Control
        if (id == 8 || id == 9 || id == 13 || id == 16 || id == 21 || id == 33 || id == 36 || id == 45 || id == 58 || id == 61 || id == 79 || id == 89 || id == 96 || id == 97 || id == 98 || id == 102) {
            return 2;
        }
        // 5: Naturaleza / Clima / Elementos
        if (id == 2 || id == 14 || id == 17 || id == 27 || id == 42 || id == 47 || id == 49 || id == 53 || id == 73 || id == 86 || id == 87 || id == 88) {
            return 5;
        }
        // 3: Utilidad / Movimiento / Adivinación
        if (id == 1 || id == 3 || id == 5 || id == 12 || id == 26 || id == 31 || id == 39 || id == 43 || id == 51 || id == 52 || id == 55 || id == 56 || id == 60 || id == 62 || id == 74 || id == 75 || id == 76 || id == 77 || id == 84 || id == 85 || id == 94) {
            return 3;
        }
        // 1: Combate / Duelo
        return 1;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) sender;

        List allEffects = EffectRegistry.instance().getEffects();
        int totalSpells = 0;
        int masteredCount = 0;

        for (Object obj : allEffects) {
            SymbolEffect eff = (SymbolEffect) obj;
            if (eff != null && eff.getDefaultStrokes() != null && eff.isVisible(player)) {
                totalSpells++;
                if (SymbolEffect.isSpellMastered(player, eff)) {
                    masteredCount++;
                }
            }
        }

        if (args.length == 0) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╔══════════════════════════════════════════════════╗"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "  ★ LIBRO DE SIMBOLOGÍA Y RAMA MÍSTICA ★"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╚══════════════════════════════════════════════════╝"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Hechizos Dominados: " + EnumChatFormatting.GREEN + masteredCount + "/" + totalSpells + EnumChatFormatting.GRAY + " (+" + (masteredCount * 10) + " pts Maestría)"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Escuelas de Magia (usa /spells <escuela> [página]):"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "  1. /spells combate" + EnumChatFormatting.GRAY + " - Hechizos ofensivos y de duelo."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "  2. /spells defensa" + EnumChatFormatting.GRAY + " - Escudos, curación y contramagia."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "  3. /spells utilidad" + EnumChatFormatting.GRAY + " - Telequinesis, teletransporte y adivinación."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "  4. /spells oscuras" + EnumChatFormatting.GRAY + " - Maldiciones y Artes Oscuras."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_GREEN + "  5. /spells naturaleza" + EnumChatFormatting.GRAY + " - Elementos, agua, viento y clima."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "  6. /spells suprema" + EnumChatFormatting.GRAY + " - Alta magia arcana y hechizos de Bruja Suprema."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Consultas de Simbología:"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "  • /spells buscar <nombre>" + EnumChatFormatting.GRAY + " - Muestra los trazos de un hechizo."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "  • /spells dominados [pág]" + EnumChatFormatting.GRAY + " - Lista los hechizos dominados (+10 pts cada uno)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "  • /spells guia" + EnumChatFormatting.GRAY + " - Cómo funciona la Simbología y la Rama Mística."));
            return;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("guia") || sub.equals("help") || sub.equals("ayuda")) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╔══════════════════════════════════════════════════╗"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "  ★ GUÍA DE SIMBOLOGÍA Y RAMA MÍSTICA ★"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╚══════════════════════════════════════════════════╝"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "1. ¿Cómo se lanzan los hechizos?"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   Equipa la " + EnumChatFormatting.GOLD + "Mystic Branch" + EnumChatFormatting.WHITE + ", mantén clic derecho y mueve el ratón según las flechas (" + EnumChatFormatting.GREEN + "↑ Arriba, ↓ Abajo, ← Izq, → Der" + EnumChatFormatting.WHITE + "). Suelta clic derecho para disparar."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "2. Libro «Brujería: Simbología»:"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • Consulta todas las fórmulas y trazos directamente abriendo tu libro físico «Brujería: Simbología» o usando " + EnumChatFormatting.AQUA + "/spells <escuela>" + EnumChatFormatting.WHITE + "."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "3. Dominio de Hechizos:"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • Al lanzar un hechizo por primera vez con tu varita queda " + EnumChatFormatting.GREEN + "✔ Dominado (+10 Maestría)" + EnumChatFormatting.WHITE + " para tu rango de Bruja Suprema."));
            return;
        }

        if (sub.equals("estudiar") || sub.equals("study")) {
            SymbolEffect.studyBookOfSymbology(player);
            return;
        }

        if (sub.equals("learnall") || sub.equals("aprender_todos") || sub.equals("descubrir_todos")) {
            if (!player.capabilities.isCreativeMode && !SupremeWitchData.isPlayerSupreme(player)) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Solo una Bruja Suprema o jugadora en modo Creativo puede desbloquear todo el Grimorio de golpe."));
                return;
            }
            NBTTagCompound nbtPlayer = Infusion.getNBT(player);
            if (nbtPlayer != null) {
                if (!nbtPlayer.hasKey("WITCSpellBook")) {
                    nbtPlayer.setTag("WITCSpellBook", new NBTTagCompound());
                }
                NBTTagCompound nbtSpells = nbtPlayer.getCompoundTag("WITCSpellBook");
                for (Object obj : allEffects) {
                    SymbolEffect eff = (SymbolEffect) obj;
                    if (eff != null) {
                        nbtSpells.setBoolean("Discovered_" + eff.getEffectID(), true);
                        nbtSpells.setBoolean("Mastered_" + eff.getEffectID(), true);
                        eff.acquireKnowledge(player);
                    }
                }
                Infusion.syncPlayer(player.worldObj, player);
                SupremeWitchData.updateSupremeStatus(player.worldObj);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "✦ ¡Todos los hechizos del Grimorio y del libro Simbología han sido inscritos y dominados!"));
            }
            return;
        }

        if (sub.equals("buscar") || sub.equals("info") || sub.equals("search")) {
            if (args.length < 2) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Uso: /spells buscar <nombre del hechizo>"));
                return;
            }
            StringBuilder qBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                if (i > 1) qBuilder.append(" ");
                qBuilder.append(args[i]);
            }
            String query = qBuilder.toString().toLowerCase().trim();
            int matches = 0;

            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "=== Resultados en el Grimorio para «" + query + "» ==="));
            for (Object obj : allEffects) {
                SymbolEffect eff = (SymbolEffect) obj;
                if (eff == null || eff.getDefaultStrokes() == null) continue;
                String name = eff.getLocalizedName();
                if (name != null && name.toLowerCase().contains(query)) {
                    boolean mastered = SymbolEffect.isSpellMastered(player, eff);
                    String status = mastered ? (EnumChatFormatting.GREEN + "[✔ Dominado]") : (EnumChatFormatting.GRAY + "[Por dominar]");
                    int cdSec = (int) TimeUtil.ticksToSecs(eff.getCooldownTicks());
                    player.addChatMessage(new ChatComponentText(status + " " + EnumChatFormatting.GOLD + name + EnumChatFormatting.GRAY + " (ID " + eff.getEffectID() + " | Coste: " + eff.getBaseChargeCost() + (cdSec > 0 ? " | CD: " + cdSec + "s" : "") + ")"));
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "   Trazos: " + EnumChatFormatting.WHITE + SymbolEffect.formatStrokesCompact(eff.getDefaultStrokes())));
                    matches++;
                    if (matches >= 8) break;
                }
            }
            if (matches == 0) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No se encontró ningún hechizo que coincida con «" + query + "»."));
            }
            return;
        }

        if (sub.equals("aprendidos") || sub.equals("dominados") || sub.equals("mastered")) {
            List<SymbolEffect> learnedList = new ArrayList<SymbolEffect>();
            for (Object obj : allEffects) {
                SymbolEffect eff = (SymbolEffect) obj;
                if (eff != null && eff.getDefaultStrokes() != null && SymbolEffect.isSpellMastered(player, eff)) {
                    learnedList.add(eff);
                }
            }
            int page = 1;
            if (args.length >= 2) {
                try { page = Math.max(1, Integer.parseInt(args[1])); } catch (NumberFormatException ignored) {}
            }
            sendPaginatedSpells(player, "Hechizos Dominados (+10 pts cada uno)", learnedList, page, "/spells dominados");
            return;
        }

        int school = 0;
        String schoolTitle = "";
        if (sub.equals("1") || sub.equals("combate") || sub.equals("duelo")) {
            school = 1;
            schoolTitle = "Escuela I: Combate y Duelo";
        } else if (sub.equals("2") || sub.equals("defensa") || sub.equals("curacion")) {
            school = 2;
            schoolTitle = "Escuela II: Defensa, Escudos y Curación";
        } else if (sub.equals("3") || sub.equals("utilidad") || sub.equals("movimiento")) {
            school = 3;
            schoolTitle = "Escuela III: Utilidad, Telequinesis y Adivinación";
        } else if (sub.equals("4") || sub.equals("oscuras") || sub.equals("maldiciones")) {
            school = 4;
            schoolTitle = "Escuela IV: Artes Oscuras y Maldiciones";
        } else if (sub.equals("5") || sub.equals("naturaleza") || sub.equals("elementos")) {
            school = 5;
            schoolTitle = "Escuela V: Naturaleza, Clima y Elementos";
        } else if (sub.equals("6") || sub.equals("suprema") || sub.equals("legendarios")) {
            school = 6;
            schoolTitle = "Escuela VI: Alta Magia y Bruja Suprema";
        }

        if (school > 0) {
            List<SymbolEffect> schoolSpells = new ArrayList<SymbolEffect>();
            for (Object obj : allEffects) {
                SymbolEffect eff = (SymbolEffect) obj;
                if (eff != null && eff.getDefaultStrokes() != null && getSchoolForEffect(eff) == school) {
                    schoolSpells.add(eff);
                }
            }
            int page = 1;
            if (args.length >= 2) {
                try { page = Math.max(1, Integer.parseInt(args[1])); } catch (NumberFormatException ignored) {}
            }
            sendPaginatedSpells(player, schoolTitle, schoolSpells, page, "/spells " + sub);
            return;
        }

        // Fallback: treat first arg as search query
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Subcomando desconocido. Usa /spells, /spells <combate|defensa|utilidad|oscuras|naturaleza|suprema>, /spells estudiar, o /spells buscar <nombre>."));
    }

    private void sendPaginatedSpells(EntityPlayer player, String title, List<SymbolEffect> list, int page, String baseCmd) {
        int pageSize = 7;
        int totalPages = Math.max(1, (int) Math.ceil((double) list.size() / (double) pageSize));
        if (page > totalPages) page = totalPages;
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, list.size());

        player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "=== " + EnumChatFormatting.GOLD + title + EnumChatFormatting.DARK_PURPLE + " (Pág " + page + "/" + totalPages + ") ==="));
        for (int i = start; i < end; i++) {
            SymbolEffect eff = list.get(i);
            boolean mastered = SymbolEffect.isSpellMastered(player, eff);
            String badge = mastered ? (EnumChatFormatting.GREEN + "✔ ") : (EnumChatFormatting.GRAY + "✦ ");
            String strokesText = EnumChatFormatting.YELLOW + SymbolEffect.formatStrokesCompact(eff.getDefaultStrokes());
            player.addChatMessage(new ChatComponentText(
                    badge + EnumChatFormatting.GOLD + eff.getLocalizedName() +
                    EnumChatFormatting.GRAY + " [Coste: " + eff.getBaseChargeCost() + "] " +
                    EnumChatFormatting.WHITE + "-> " + strokesText
            ));
        }
        if (page < totalPages) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Usa " + EnumChatFormatting.YELLOW + baseCmd + " " + (page + 1) + EnumChatFormatting.GRAY + " para ver la siguiente página."));
        }
    }
}

