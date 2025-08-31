package com.example.websocket_chat;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class Utilities {
    public static final Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0);
    public static final Color PRIMARY_COLOR = Color.decode("#212121");
    public static final Color SECONDARY_COLOR = Color.decode("#333333");
    public static final Color THIRD_COLOR = Color.decode("#4d4d4d");
    public static final Color ORANGE_COLOR = Color.decode("#f7931a");
    public static final Color TEXT_COLOR = Color.WHITE;
    public static final Color BUTTON_ENABLE = Color.GREEN;
    public static final Color BUTTON_DISABLE = Color.RED;


    public static EmptyBorder addPadding(int top, int left, int bottom, int right) {
        return new EmptyBorder(top, left, bottom, right);
    }

    public static CompoundBorder addPadding(int top, int left, int bottom, int right, Color color) {
        return new CompoundBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, color),
                BorderFactory.createEmptyBorder(top - 1, left - 1, bottom -1 , right - 1));
    }
}
