package com.kahzerx.kahzerxmod.extensions.prankExtension;

import net.minecraft.util.Formatting;

public enum PrankLevel {
    LEVEL0("", Formatting.WHITE, 0, "Este nivel es para aquellas personas que no desean ningún tipo de prank."),
    LEVEL1("①", Formatting.GREEN, 1, "Este nivel es el más bajo y el más gracioso. Están permitidas pranks de 0 - 50 bloques que sean fáciles de quitar (Lana, Madera). Este nivel es para gente que en vez de pranks quiere regalos y sorpresas."),
    LEVEL2("②", Formatting.YELLOW, 2, "Este nivel es una manera más graciosa hacer pranks. Está permitido pranks de 0 - 100 bloques que sean un poco mas difíciles de quitar (Roca, Botones, y 5 bloques de obsidiana). Este nivel está pensado para la gente que quiere hacer pranks poco molestas."),
    LEVEL3("③", Formatting.GOLD, 3, "Este nivel es una manera normal de hacer bromas, están permitidas pranks de 0 - 250 bloques que sean difíciles de quitar. (Arena, Grava, Botones, Palancas, Lava [Sin quemar nada]). Este nivel, está pensado para la gente que quiere empezar a divertirse e intentar molestar a los demás un poco."),
    LEVEL4("④", Formatting.RED, 4, "Este nivel es un poco más serio a la hora de supervivencia. Está permitido las pranks entre 0 - 500 bloques de tu agrado. A partir de este rango, puedes matar a la persona sin motivo pero NO robar sus items más preciados. Este rango, esta pensado para la gente ya un poco más atrevida a la hora de prankear a otros jugadores. NO ES RECOMENDABLE PARA LA GENTE QUE NO QUIERA PERDER SUS ITEMS SIN QUERER."),
    LEVEL5("⑤", Formatting.DARK_RED, 5, "Con este rango, puedes hacer lo que tu quieras, Se puede matar, puedes destrozarle la vida, puedes prankear sin LIMITE. Puedes hacer lo que quieras conmigo. Con este rango esta permitido matar pero SIN ROBAR EL INVENTARIO. ESTE RANGO NO ES RECOMENDABLE PARA LA GENTE QUE NO QUIERA SUFRIR DEMASIADO.");

    private final String identifier;
    private final Formatting formatting;
    private final int ID;
    private final String description;

    PrankLevel(String identifier, Formatting formatting, int ID, String description) {
        this.identifier = identifier;
        this.formatting = formatting;
        this.ID = ID;
        this.description = description;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getID() {
        return ID;
    }

    public Formatting getFormatting() {
        return formatting;
    }

    public String getDescription() {
        return description;
    }

    public static PrankLevel idToLevel(int i) {
        return switch (i) {
            case 1 -> LEVEL1;
            case 2 -> LEVEL2;
            case 3 -> LEVEL3;
            case 4 -> LEVEL4;
            case 5 -> LEVEL5;
            default -> LEVEL0;
        };
    }
}
