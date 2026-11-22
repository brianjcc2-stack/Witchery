package com.emoniph.witchery.commands;

import com.emoniph.witchery.common.SupremeWitchData;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.infusion.infusions.symbols.EffectRegistry;
import com.emoniph.witchery.infusion.infusions.symbols.SymbolEffect;
import com.emoniph.witchery.util.TimeUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CommandSpells extends CommandBase {

    @Override
    public String getCommandName() {
        return "spells";
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("hechizos", "grimorio", "hechizo", "spell");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/spells <nombre_hechizo|todos|combate|defensa|utilidad|oscuras|naturaleza|suprema|consultar <nombre>|dominados|guia>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    private static String normalizeText(String s) {
        if (s == null) return "";
        String norm = Normalizer.normalize(s, Normalizer.Form.NFD);
        return norm.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase().trim();
    }

    private static List<String> getSpellTabTokens() {
        Set<String> tokens = new LinkedHashSet<String>();
        for (Object obj : EffectRegistry.instance().getEffects()) {
            SymbolEffect eff = (SymbolEffect) obj;
            if (eff == null || eff.getDefaultStrokes() == null) continue;
            String rawKey = eff.getUnlocalisedName();
            if (rawKey != null && rawKey.startsWith("witchery.pott.")) {
                tokens.add(rawKey.substring("witchery.pott.".length()));
            }
            String esName = eff.getSpanishName();
            if (esName != null && !esName.isEmpty()) {
                int paren = esName.indexOf('(');
                String clean = (paren > 0 ? esName.substring(0, paren) : esName).trim();
                tokens.add(clean.toLowerCase().replace(' ', '_'));
            }
        }
        return new ArrayList<String>(tokens);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<String>(Arrays.asList(
                    "consultar", "buscar", "todos", "libro",
                    "combate", "defensa", "utilidad", "oscuras", "naturaleza", "suprema",
                    "dominados", "aprendidos", "estudiar", "learnall", "guia"
            ));
            options.addAll(getSpellTabTokens());
            return getListOfStringsMatchingLastWord(args, options.toArray(new String[0]));
        } else if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("buscar") || sub.equals("consultar") || sub.equals("info") || sub.equals("search") || sub.equals("ver")) {
                List<String> spellTokens = getSpellTabTokens();
                return getListOfStringsMatchingLastWord(args, spellTokens.toArray(new String[0]));
            }
        }
        return null;
    }

    public static int getSchoolForEffect(SymbolEffect effect) {
        int id = effect.getEffectID();
        // 6: Alta Magia / Bruja Suprema / Legendarios
        if (id == 29 || id == 34 || id == 42 || id == 53 || id == 56 || id == 64 || id == 65 || id == 79 || id == 99 || id == 103) {
            return 6;
        }
        // 4: Artes Oscuras / Maldiciones
        if (id == 4 || id == 9 || id == 20 || id == 39 || id == 40 || id == 41 || id == 43 || id == 44 || id == 45 || id == 46 || id == 54 || id == 72) {
            return 4;
        }
        // 2: Defensa / Curación / Control
        if (id == 5 || id == 7 || id == 8 || id == 12 || id == 13 || id == 19 || id == 31 || id == 33 || id == 35 || id == 58 || id == 59 || id == 62 || id == 63 || id == 83) {
            return 2;
        }
        // 5: Naturaleza / Clima / Elementos
        if (id == 2 || id == 21 || id == 23 || id == 25 || id == 27 || id == 37 || id == 60 || id == 74 || id == 81 || id == 82) {
            return 5;
        }
        // 3: Utilidad / Movimiento / Adivinación
        if (id == 1 || id == 3 || id == 6 || id == 10 || id == 14 || id == 16 || id == 18 || id == 22 || id == 26 || id == 48 || id == 49 || id == 50 || id == 51 || id == 52 || id == 55 || id == 73 || id == 75 || id == 76 || id == 77 || id == 78 || id == 84 || id == 85 || id == 86) {
            return 3;
        }
        // 1: Combate / Duelo (Asis, Expelliarmus, Diffindo, Descendo, Flipendo, Ictus, Confringo, Arania Exumai, Expulso, Stupefy, Bombarda, Reducto)
        return 1;
    }

    public static String getSchoolName(int school) {
        switch (school) {
            case 1: return "Combate y Duelo";
            case 2: return "Defensa, Curación y Control";
            case 3: return "Utilidad, Movimiento y Adivinación";
            case 4: return "Artes Oscuras y Maldiciones";
            case 5: return "Naturaleza, Clima y Elementos";
            case 6: return "Alta Magia y Bruja Suprema";
            default: return "Magia Arcana";
        }
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

        int curTier = 1;
        if (masteredCount >= 16) curTier = 4;
        else if (masteredCount >= 10) curTier = 3;
        else if (masteredCount >= 4) curTier = 2;
        int nextReq = curTier == 1 ? 4 : (curTier == 2 ? 10 : 16);

        if (args.length == 0) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╔══════════════════════════════════════════════════╗"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "  ★ LIBRO DE SIMBOLOGÍA Y RAMA MÍSTICA ★"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╚══════════════════════════════════════════════════╝"));
            String tierTitle = curTier == 4 ? "Tier IV (Artes Oscuras)" : (curTier == 3 ? "Tier III (Adepto)" : (curTier == 2 ? "Tier II (Iniciado)" : "Tier I (Aprendiz)"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Rango Arcano: " + EnumChatFormatting.AQUA + tierTitle + EnumChatFormatting.GRAY + " | Dominados: " + EnumChatFormatting.GREEN + masteredCount + "/" + totalSpells + EnumChatFormatting.GRAY + " (+" + (masteredCount * 10) + " pts)"));
            if (curTier < 4) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "  ✦ Siguiente rango (Tier " + (curTier + 1) + "): " + EnumChatFormatting.WHITE + (nextReq - masteredCount) + " hechizo(s) más por dominar."));
            }
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "Consulta Rápida de un Hechizo (sin memorizar):"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "  • /spells <nombre o ID>" + EnumChatFormatting.GRAY + " (ej: " + EnumChatFormatting.WHITE + "/spells accio" + EnumChatFormatting.GRAY + ", " + EnumChatFormatting.WHITE + "/hechizos lumos" + EnumChatFormatting.GRAY + ", " + EnumChatFormatting.WHITE + "/spells escoba" + EnumChatFormatting.GRAY + ")"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "  • /spells todos [pág]" + EnumChatFormatting.GRAY + " - Lista todos los hechizos en el mismo orden del Libro."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Escuelas de Magia (usa /spells <escuela> [página]):"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "  1. /spells combate" + EnumChatFormatting.GRAY + " - Hechizos ofensivos y de duelo."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "  2. /spells defensa" + EnumChatFormatting.GRAY + " - Escudos, curación y control."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "  3. /spells utilidad" + EnumChatFormatting.GRAY + " - Telequinesis, transporte y adivinación."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "  4. /spells oscuras" + EnumChatFormatting.GRAY + " - Maldiciones y Artes Oscuras."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_GREEN + "  5. /spells naturaleza" + EnumChatFormatting.GRAY + " - Elementos, agua, animales y clima."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "  6. /spells suprema" + EnumChatFormatting.GRAY + " - Alta magia arcana y hechizos avanzados."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "  • /spells dominados [pág]" + EnumChatFormatting.GRAY + " | " + EnumChatFormatting.WHITE + "/spells guia"));
            return;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("guia") || sub.equals("help") || sub.equals("ayuda")) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╔══════════════════════════════════════════════════╗"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "  ★ GUÍA DE SIMBOLOGÍA Y RAMA MÍSTICA ★"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╚══════════════════════════════════════════════════╝"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "1. ¿Cómo se lanzan los hechizos?"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   Equipa la " + EnumChatFormatting.GOLD + "Rama Mística" + EnumChatFormatting.WHITE + ", mantén clic derecho y mueve el ratón en el orden exacto de los pasos (" + EnumChatFormatting.GREEN + "1.↑ 2.↓ 3.← 4.→" + EnumChatFormatting.WHITE + "). Suelta clic derecho para lanzar."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "2. ¿Cómo consultar un hechizo si no lo recuerdas?"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   Escribe " + EnumChatFormatting.GREEN + "/spells <nombre>" + EnumChatFormatting.WHITE + " o " + EnumChatFormatting.GREEN + "/hechizos <nombre>" + EnumChatFormatting.WHITE + " (puedes usar la tecla " + EnumChatFormatting.GOLD + "TAB" + EnumChatFormatting.WHITE + " para autocompletar) y verás su efecto y el orden paso a paso de sus trazos."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "3. Dominio y Sobrecarga (Shift):"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   Lanzar un hechizo 3 veces lo marca como " + EnumChatFormatting.GREEN + "✔ Dominado (+10 Maestría)" + EnumChatFormatting.WHITE + ". Mientras canalizas un hechizo dominado, pulsa " + EnumChatFormatting.AQUA + "Shift" + EnumChatFormatting.WHITE + " para sobrecargarlo a Nivel II o III."));
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

        if (sub.equals("todos") || sub.equals("libro") || sub.equals("all")) {
            List<SymbolEffect> bookOrder = new ArrayList<SymbolEffect>();
            for (Object obj : allEffects) {
                SymbolEffect eff = (SymbolEffect) obj;
                if (eff != null && eff.getDefaultStrokes() != null && eff.isVisible(player)) {
                    bookOrder.add(eff);
                }
            }
            int page = 1;
            if (args.length >= 2) {
                try { page = Math.max(1, Integer.parseInt(args[1])); } catch (NumberFormatException ignored) {}
            }
            sendPaginatedSpells(player, "Libro de Simbología (Orden del Libro)", bookOrder, page, "/spells todos");
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

        if (school > 0 && (args.length == 1 || isInteger(args[1]))) {
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

        // Consulta o búsqueda de un hechizo: "/spells buscar <nombre>", "/spells consultar <nombre>", o directamente "/spells <nombre o ID>"
        int startIdx = 0;
        if (sub.equals("buscar") || sub.equals("consultar") || sub.equals("info") || sub.equals("search") || sub.equals("ver")) {
            if (args.length < 2) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Uso: /spells consultar <nombre o ID del hechizo> (también puedes escribir directamente /spells <nombre>)."));
                return;
            }
            startIdx = 1;
        }

        StringBuilder qBuilder = new StringBuilder();
        for (int i = startIdx; i < args.length; i++) {
            if (i > startIdx) qBuilder.append(" ");
            qBuilder.append(args[i]);
        }
        String rawQuery = qBuilder.toString().trim();
        lookupAndSendSpellDetails(player, allEffects, rawQuery);
    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void lookupAndSendSpellDetails(EntityPlayer player, List allEffects, String rawQuery) {
        String query = normalizeText(rawQuery).replace('_', ' ');
        int queryId = -1;
        if (isInteger(rawQuery)) {
            queryId = Integer.parseInt(rawQuery);
        }

        List<SymbolEffect> exactMatches = new ArrayList<SymbolEffect>();
        List<SymbolEffect> partialMatches = new ArrayList<SymbolEffect>();

        for (Object obj : allEffects) {
            SymbolEffect eff = (SymbolEffect) obj;
            if (eff == null || eff.getDefaultStrokes() == null) continue;

            if (queryId > 0 && eff.getEffectID() == queryId) {
                exactMatches.add(eff);
                continue;
            }

            String esName = normalizeText(eff.getSpanishName()).replace('_', ' ');
            String locName = normalizeText(eff.getLocalizedName()).replace('_', ' ');
            String keyName = eff.getUnlocalisedName() != null
                    ? normalizeText(eff.getUnlocalisedName().replace("witchery.pott.", "")).replace('_', ' ')
                    : "";
            String infoEs = normalizeText(eff.getSpanishInfo());

            if (esName.equals(query) || locName.equals(query) || keyName.equals(query)) {
                exactMatches.add(eff);
                if (query.equals("flagrate")) {
                    // Incluir toda la familia Flagrate cuando se consulta "flagrate"
                    for (Object o2 : allEffects) {
                        SymbolEffect e2 = (SymbolEffect) o2;
                        if (e2 != null && e2 != eff && SymbolEffect.isFlagrateFamily(e2.getEffectID())) {
                            exactMatches.add(e2);
                        }
                    }
                }
            } else if (esName.contains(query) || locName.contains(query) || keyName.contains(query) || infoEs.contains(query)) {
                partialMatches.add(eff);
            }
        }

        List<SymbolEffect> results = !exactMatches.isEmpty() ? exactMatches : partialMatches;
        SymbolEffect.sortByComplexity(results);

        if (results.isEmpty()) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No se encontró ningún hechizo que coincida con «" + rawQuery + "». Usa " + EnumChatFormatting.YELLOW + "/spells todos" + EnumChatFormatting.RED + " para ver la lista completa."));
            return;
        }

        // Si hay hasta 4 coincidencias (ej. los 4 Flagrate), mostramos la ficha completa detallada
        if (results.size() <= 4) {
            for (SymbolEffect eff : results) {
                sendDetailedSpellCard(player, eff);
            }
        } else {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "=== Hechizos encontrados para «" + rawQuery + "» (" + results.size() + ") ==="));
            int shown = 0;
            for (SymbolEffect eff : results) {
                sendCompactSpellLine(player, eff);
                shown++;
                if (shown >= 8) {
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Escribe el nombre más específico (ej. " + EnumChatFormatting.YELLOW + "/spells " + results.get(0).getSpanishName() + EnumChatFormatting.GRAY + ") para ver su ficha completa."));
                    break;
                }
            }
        }
    }

    private void sendDetailedSpellCard(EntityPlayer player, SymbolEffect eff) {
        boolean mastered = SymbolEffect.isSpellMastered(player, eff);
        int castCount = 0;
        NBTTagCompound nbtPlayer = Infusion.getNBT(player);
        if (nbtPlayer != null && nbtPlayer.hasKey("WITCSpellBook")) {
            castCount = nbtPlayer.getCompoundTag("WITCSpellBook").getInteger("CastCount_" + eff.getEffectID());
        }
        String status = mastered
                ? (EnumChatFormatting.GREEN + "✔ Dominado (+10 pts)")
                : (EnumChatFormatting.YELLOW + "✦ Por dominar (" + Math.min(castCount, 3) + "/3 lanzamientos)");

        int tier = eff.getSpellTier();
        int reqMastered = eff.getRequiredMasteryCount();
        int playerMastered = SymbolEffect.getMasteredSpellCount(player);
        boolean tierUnlocked = player.capabilities.isCreativeMode || SupremeWitchData.isPlayerSupreme(player) || playerMastered >= reqMastered;
        EnumChatFormatting tierColor = tierUnlocked ? EnumChatFormatting.AQUA : EnumChatFormatting.RED;
        String tierText = "Tier " + tier + (reqMastered > 0 ? " (Req. " + reqMastered + " dom.)" : " (Inicial)");

        int cdSec = (int) TimeUtil.ticksToSecs(eff.getCooldownTicks());
        int school = getSchoolForEffect(eff);

        player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╔══════════════════════════════════════════════════╗"));
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "  ✦ " + EnumChatFormatting.BOLD + eff.getSpanishName() + EnumChatFormatting.RESET + EnumChatFormatting.GRAY + " [ID #" + eff.getEffectID() + " | " + getSchoolName(school) + "]"));
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "  Estado: " + status + EnumChatFormatting.GRAY + " | Rango: " + tierColor + tierText));
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "  Coste base: " + EnumChatFormatting.WHITE + eff.getBaseChargeCost() + " energía" + (cdSec > 0 ? EnumChatFormatting.GRAY + " | Enfriamiento: " + EnumChatFormatting.WHITE + cdSec + "s" : "") + (eff.requiresInfernalInfusion() ? EnumChatFormatting.RED + " | [Requiere Infusión Infernal]" : "")));
        String info = eff.getSpanishInfo();
        if (info != null && !info.isEmpty()) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "  Efecto: " + EnumChatFormatting.WHITE + info));
        }
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "  Trazos: " + EnumChatFormatting.AQUA + EnumChatFormatting.BOLD + SymbolEffect.formatStrokesCompact(eff.getDefaultStrokes())));
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "╚══════════════════════════════════════════════════╝"));
    }

    private void sendCompactSpellLine(EntityPlayer player, SymbolEffect eff) {
        int curTier = 1;
        int masteredCount = SymbolEffect.getMasteredSpellCount(player);
        if (masteredCount >= 16) curTier = 4;
        else if (masteredCount >= 10) curTier = 3;
        else if (masteredCount >= 4) curTier = 2;

        boolean mastered = SymbolEffect.isSpellMastered(player, eff);
        String badge = mastered ? (EnumChatFormatting.GREEN + "✔ ") : (EnumChatFormatting.GRAY + "✦ ");
        int tier = eff.getSpellTier();
        String tierBadge = (curTier >= tier ? EnumChatFormatting.AQUA : EnumChatFormatting.RED) + "[T" + tier + "] ";
        String strokesText = EnumChatFormatting.YELLOW + SymbolEffect.formatStrokesCompact(eff.getDefaultStrokes());
        player.addChatMessage(new ChatComponentText(
                badge + tierBadge + EnumChatFormatting.GOLD + eff.getSpanishName() +
                EnumChatFormatting.GRAY + " [Coste:" + eff.getBaseChargeCost() + "] " +
                EnumChatFormatting.WHITE + "➔ " + strokesText
        ));
    }

    private void sendPaginatedSpells(EntityPlayer player, String title, List<SymbolEffect> list, int page, String baseCmd) {
        SymbolEffect.sortByComplexity(list);
        int pageSize = 7;
        int totalPages = Math.max(1, (int) Math.ceil((double) list.size() / (double) pageSize));
        if (page > totalPages) page = totalPages;
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, list.size());

        player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "=== " + EnumChatFormatting.GOLD + title + EnumChatFormatting.DARK_PURPLE + " (Pág " + page + "/" + totalPages + ") ==="));

        for (int i = start; i < end; i++) {
            sendCompactSpellLine(player, list.get(i));
        }
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Tip: Escribe " + EnumChatFormatting.GREEN + "/spells <nombre>" + EnumChatFormatting.GRAY + " para ver el efecto y pasos detallados de un hechizo."));
        if (page < totalPages) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Usa " + EnumChatFormatting.YELLOW + baseCmd + " " + (page + 1) + EnumChatFormatting.GRAY + " para ver la siguiente página."));
        }
    }
}
