package me.tobias.game;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Renderer extends JPanel {

    public static final List<Color> colors = new ArrayList<>();
    private static final long serialVersionUID = 1L;

    public Renderer() {
        colors.add(Color.green);
        colors.add(Color.green.darker());
        colors.add(Color.green.darker().darker());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        FlappyBird.game.repaint(g);
    }
}
