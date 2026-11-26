package fr.junia.got.display;

import fr.junia.got.entity.House;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.swing.JPanel;

/** Dessine la carte, l'état des quatre maisons et les derniers événements à partir d'un StepSnapshot. */
public class WorldView extends JPanel {

    /** Taille d'une case, en pixels. */
    private static final int TILE = 26;
    private static final int MARGIN = 20;
    private static final int TOP = 70;
    private static final int SIDEBAR_WIDTH = 370;
    private static final int CARD_HEIGHT = 80;
    private static final int CARD_GAP = 10;
    private static final int HISTORY_SIZE = 9;

    private static final Color BACKGROUND = new Color(0xFAF7F0);
    private static final Color PARCHMENT = new Color(0xF2EBDC);
    private static final Color GRID = new Color(0xE3DAC7);
    private static final Color BORDER = new Color(0xD9D1C0);
    private static final Color INK = new Color(0x2B2B2B);
    private static final Color MUTED = new Color(0x6E6A62);
    private static final Color ROCK = new Color(0x8C8577);
    private static final Color EXHAUSTED = new Color(0xA19E98);
    private static final Color TRACK = new Color(0xECE6D9);

    private final int columns;
    private final int rows;
    /** Les derniers événements, du plus récent au plus ancien. */
    private final Deque<String> history = new ArrayDeque<>();
    private StepSnapshot snapshot;
    private int lastStep = -1;

    public WorldView(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;
        setPreferredSize(new Dimension(MARGIN * 3 + columns * TILE + SIDEBAR_WIDTH, TOP + rows * TILE + 56));
        setBackground(BACKGROUND);
    }

    /** Couleur d'une maison. */
    public static Color colorOf(House house) {
        return switch (house) {
            case STARK -> new Color(0x5D6D7E);
            case TARGARYEN -> new Color(0xB03A2E);
            case LANNISTER -> new Color(0xD4A20D);
            case WHITE_WALKER -> new Color(0x2E86C1);
        };
    }

    /** Affiche un nouvel état. À appeler depuis le thread de Swing. */
    public void update(StepSnapshot next) {
        if (next.step() != lastStep) {
            for (String event : next.events()) {
                history.addFirst("Pas " + next.step() + " · " + event);
            }
            while (history.size() > HISTORY_SIZE) {
                history.removeLast();
            }
            lastStep = next.step();
        }
        snapshot = next;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (snapshot == null) {
            return;
        }
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        paintHeader(g);
        paintMap(g);
        paintLegend(g);
        paintHouses(g);
        paintHistory(g);
        if (snapshot.result() != null) {
            paintResult(g);
        }
        g.dispose();
    }

    // ----- En-tête -----

    private void paintHeader(Graphics2D g) {
        g.setColor(INK);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        g.drawString("GameOfThrones MAS", MARGIN, 38);
        g.setColor(MUTED);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        g.drawString("Stark + Targaryen contre Lannister + Marcheurs Blancs", MARGIN, 58);

        int right = getWidth() - MARGIN;
        g.setColor(INK);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        drawRight(g, "Pas " + snapshot.step() + " / " + snapshot.maxSteps(), right, 38);
        g.setColor(MUTED);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        drawRight(g, snapshot.activeAgents() + " agents actifs · " + snapshot.totalMessages() + " messages en jeu",
                right, 58);
    }

    // ----- Carte -----

    private void paintMap(Graphics2D g) {
        int width = columns * TILE;
        int height = rows * TILE;
        g.setColor(PARCHMENT);
        g.fillRect(MARGIN, TOP, width, height);

        // SafeZones teintées de la couleur de leur maison
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                House zone = snapshot.zones()[y][x];
                if (zone != null) {
                    g.setColor(withAlpha(colorOf(zone), 50));
                    g.fillRect(MARGIN + x * TILE, TOP + y * TILE, TILE, TILE);
                }
            }
        }

        g.setColor(GRID);
        g.setStroke(new BasicStroke(1f));
        for (int x = 1; x < columns; x++) {
            g.drawLine(MARGIN + x * TILE, TOP, MARGIN + x * TILE, TOP + height);
        }
        for (int y = 1; y < rows; y++) {
            g.drawLine(MARGIN, TOP + y * TILE, MARGIN + width, TOP + y * TILE);
        }

        paintZoneBorders(g);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                paintCell(g, snapshot.cells()[y][x], MARGIN + x * TILE, TOP + y * TILE);
            }
        }

        g.setColor(BORDER);
        g.setStroke(new BasicStroke(1.5f));
        g.drawRect(MARGIN, TOP, width, height);
    }

    /** Contour de chaque SafeZone, dans la couleur de sa maison. */
    private void paintZoneBorders(Graphics2D g) {
        g.setStroke(new BasicStroke(2f));
        for (House house : House.values()) {
            int minX = columns;
            int minY = rows;
            int maxX = -1;
            int maxY = -1;
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < columns; x++) {
                    if (snapshot.zones()[y][x] == house) {
                        minX = Math.min(minX, x);
                        minY = Math.min(minY, y);
                        maxX = Math.max(maxX, x);
                        maxY = Math.max(maxY, y);
                    }
                }
            }
            if (maxX >= 0) {
                g.setColor(withAlpha(colorOf(house), 170));
                g.drawRect(MARGIN + minX * TILE + 1, TOP + minY * TILE + 1,
                        (maxX - minX + 1) * TILE - 2, (maxY - minY + 1) * TILE - 2);
            }
        }
    }

    private void paintCell(Graphics2D g, char symbol, int x, int y) {
        if (symbol == '.') {
            return;
        }
        if (symbol == '#') {
            g.setColor(ROCK);
            g.fillRoundRect(x + 3, y + 3, TILE - 6, TILE - 6, 7, 7);
            g.setColor(ROCK.brighter());
            g.fillRoundRect(x + 6, y + 6, TILE - 15, TILE - 17, 4, 4);
            return;
        }
        if (symbol == 'x') {
            g.setColor(EXHAUSTED);
            g.setStroke(new BasicStroke(2f));
            g.drawOval(x + 5, y + 5, TILE - 10, TILE - 10);
            g.drawLine(x + 9, y + 9, x + TILE - 9, y + TILE - 9);
            g.drawLine(x + TILE - 9, y + 9, x + 9, y + TILE - 9);
            return;
        }
        for (House house : House.values()) {
            if (symbol == house.getAgentSymbol()) {
                paintAgent(g, house, x, y);
                return;
            }
            if (symbol == house.getMasterSymbol()) {
                paintMaster(g, house, x, y);
                return;
            }
        }
    }

    private void paintAgent(Graphics2D g, House house, int x, int y) {
        Color color = colorOf(house);
        g.setColor(color);
        g.fillOval(x + 5, y + 5, TILE - 10, TILE - 10);
        g.setColor(color.darker());
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(x + 5, y + 5, TILE - 10, TILE - 10);
    }

    /** Le Maître : un grand cercle cerclé de noir et de blanc, avec l'initiale de sa maison. */
    private void paintMaster(Graphics2D g, House house, int x, int y) {
        Color color = colorOf(house);
        g.setColor(INK);
        g.fillOval(x - 1, y - 1, TILE + 2, TILE + 2);
        g.setColor(Color.WHITE);
        g.fillOval(x + 1, y + 1, TILE - 2, TILE - 2);
        g.setColor(color);
        g.fillOval(x + 3, y + 3, TILE - 6, TILE - 6);
        g.setColor(house == House.LANNISTER ? INK : Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        drawCentered(g, String.valueOf(house.getMasterSymbol()), x + TILE / 2, y + TILE / 2 + 5);
    }

    private void paintLegend(Graphics2D g) {
        int y = TOP + rows * TILE + 30;
        int x = MARGIN;
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        FontMetrics metrics = g.getFontMetrics();
        for (House house : House.values()) {
            g.setColor(colorOf(house));
            g.fillOval(x, y - 10, 12, 12);
            g.setColor(INK);
            g.drawString(house.getDisplayName(), x + 18, y);
            x += 18 + metrics.stringWidth(house.getDisplayName()) + 22;
        }
        g.setColor(INK);
        g.fillOval(x, y - 11, 14, 14);
        g.setColor(Color.WHITE);
        g.fillOval(x + 2, y - 9, 10, 10);
        g.setColor(MUTED);
        g.fillOval(x + 4, y - 7, 6, 6);
        g.setColor(INK);
        g.drawString("Maître", x + 20, y);
        x += 20 + metrics.stringWidth("Maître") + 22;
        g.setColor(ROCK);
        g.fillRoundRect(x, y - 10, 12, 12, 4, 4);
        g.setColor(INK);
        g.drawString("Rocher", x + 18, y);
        x += 18 + metrics.stringWidth("Rocher") + 22;
        g.setColor(EXHAUSTED);
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(x, y - 10, 12, 12);
        g.setColor(INK);
        g.drawString("Épuisé (obstacle)", x + 18, y);
    }

    // ----- Colonne de droite : maisons et événements -----

    private void paintHouses(Graphics2D g) {
        int x = MARGIN * 2 + columns * TILE;
        int y = TOP;
        int width = SIDEBAR_WIDTH;
        int best = -1;
        boolean tie = false;
        for (StepSnapshot.HouseStats stats : snapshot.houses()) {
            if (stats.score() > best) {
                best = stats.score();
                tie = false;
            } else if (stats.score() == best) {
                tie = true;
            }
        }
        for (StepSnapshot.HouseStats stats : snapshot.houses()) {
            boolean leader = !tie && stats.score() == best && best > 0;
            paintHouseCard(g, stats, leader, x, y, width);
            y += CARD_HEIGHT + CARD_GAP;
        }
    }

    private void paintHouseCard(Graphics2D g, StepSnapshot.HouseStats stats, boolean leader, int x, int y, int width) {
        Color color = colorOf(stats.house());
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, width, CARD_HEIGHT, 12, 12);
        g.setColor(leader ? color : BORDER);
        g.setStroke(new BasicStroke(leader ? 2f : 1f));
        g.drawRoundRect(x, y, width, CARD_HEIGHT, 12, 12);
        g.setColor(color);
        g.fillRoundRect(x, y, 8, CARD_HEIGHT, 8, 8);

        int left = x + 20;
        int right = x + width - 16;
        g.setColor(INK);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        g.drawString(stats.house().getDisplayName(), left, y + 23);
        if (leader) {
            int nameWidth = g.getFontMetrics().stringWidth(stats.house().getDisplayName());
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
            int pillWidth = g.getFontMetrics().stringWidth("EN TÊTE") + 14;
            g.setColor(color);
            g.fillRoundRect(left + nameWidth + 10, y + 10, pillWidth, 17, 17, 17);
            g.setColor(stats.house() == House.LANNISTER ? INK : Color.WHITE);
            g.drawString("EN TÊTE", left + nameWidth + 17, y + 23);
        }
        g.setColor(MUTED);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        drawRight(g, "Maître : " + stats.masterName(), right, y + 23);

        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        g.drawString("Messages du Maître", left, y + 44);
        g.setColor(INK);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        drawRight(g, stats.score() + " / " + snapshot.totalMessages(), right, y + 44);
        int barWidth = right - left;
        g.setColor(TRACK);
        g.fillRoundRect(left, y + 50, barWidth, 8, 8, 8);
        g.setColor(color);
        g.fillRoundRect(left, y + 50, barWidth * stats.score() / Math.max(1, snapshot.totalMessages()), 8, 8, 8);

        g.setColor(MUTED);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        g.drawString(stats.active() + " actifs · " + stats.exhausted() + " épuisé(s) · énergie moyenne "
                + stats.energyPercent() + " %", left, y + 73);
    }

    private void paintHistory(Graphics2D g) {
        int x = MARGIN * 2 + columns * TILE;
        int y = TOP + 4 * (CARD_HEIGHT + CARD_GAP) + 18;
        g.setColor(INK);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        g.drawString("Derniers événements", x, y);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        FontMetrics metrics = g.getFontMetrics();
        y += 22;
        if (history.isEmpty()) {
            g.setColor(MUTED);
            g.drawString("Aucun échange pour l'instant", x, y);
            return;
        }
        for (String event : history) {
            House house = houseOf(event);
            g.setColor(house == null ? MUTED : colorOf(house));
            g.fillOval(x, y - 9, 9, 9);
            g.setColor(INK);
            g.drawString(fit(event, SIDEBAR_WIDTH - 16, metrics), x + 16, y);
            y += 19;
        }
    }

    /** Maison de l'agent qui ouvre l'événement (ex. "Pas 12 · s3 livre..."), ou null. */
    private static House houseOf(String event) {
        int start = event.indexOf("· ");
        if (start < 0 || start + 2 >= event.length()) {
            return null;
        }
        char first = event.charAt(start + 2);
        for (House house : House.values()) {
            if (first == house.getAgentSymbol()) {
                return house;
            }
        }
        return null;
    }

    // ----- Résultat final -----

    private void paintResult(Graphics2D g) {
        String[] lines = snapshot.result().split("\n");
        int width = 470;
        int height = 112;
        int x = MARGIN + (columns * TILE - width) / 2;
        int y = TOP + (rows * TILE - height) / 2;
        g.setColor(new Color(20, 20, 20, 215));
        g.fillRoundRect(x, y, width, height, 18, 18);
        int center = x + width / 2;
        g.setColor(new Color(0xCFCFCF));
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        drawCentered(g, "Fin de la partie", center, y + 28);
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        drawCentered(g, lines[0], center, y + 62);
        if (lines.length > 1) {
            g.setColor(new Color(0xE6E6E6));
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            drawCentered(g, lines[1], center, y + 90);
        }
    }

    // ----- Outils de dessin -----

    private static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    private static void drawRight(Graphics2D g, String text, int right, int baseline) {
        g.drawString(text, right - g.getFontMetrics().stringWidth(text), baseline);
    }

    private static void drawCentered(Graphics2D g, String text, int center, int baseline) {
        g.drawString(text, center - g.getFontMetrics().stringWidth(text) / 2, baseline);
    }

    /** Raccourcit le texte avec « … » s'il dépasse la largeur disponible. */
    private static String fit(String text, int maxWidth, FontMetrics metrics) {
        if (metrics.stringWidth(text) <= maxWidth) {
            return text;
        }
        String shortened = text;
        while (!shortened.isEmpty() && metrics.stringWidth(shortened + "…") > maxWidth) {
            shortened = shortened.substring(0, shortened.length() - 1);
        }
        return shortened + "…";
    }
}
