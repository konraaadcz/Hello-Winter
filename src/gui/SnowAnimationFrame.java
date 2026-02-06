package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnowAnimationFrame extends JFrame {

    private final SnowPanel snowPanel;

    public SnowAnimationFrame() {
        setTitle("Hello Winter");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        snowPanel = new SnowPanel();
        add(snowPanel);

        Timer timer = new Timer(16, e -> snowPanel.updateSnowflakes());
        timer.start();
    }

    private static class SnowPanel extends JPanel {

        private final List<Snowflake> snowflakes = new ArrayList<>();
        private final Random random = new Random();

       
        private double wind = 0;
        private double windChange = 0.01;

        public SnowPanel() {
            setBackground(new Color(10, 10, 50));
            generateSnowflakes(50);
        }

        private void generateSnowflakes(int count) {
            snowflakes.clear();
            for (int i = 0; i < count; i++) {
                snowflakes.add(Snowflake.random(random, getWidth(), getHeight()));
            }
        }

        public void updateSnowflakes() {
           
            wind += (random.nextDouble() - 0.5) * windChange;
            wind = Math.max(-1.5, Math.min(1.5, wind));

            for (Snowflake flake : snowflakes) {
                flake.y += flake.speed;
                flake.x += wind * flake.drift;

                flake.rotation += flake.rotationSpeed;

                if (flake.y > getHeight() + flake.size) {
                    flake.reset(random, getWidth());
                }
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(20, 20, 60),
                    0, getHeight(), new Color(0, 0, 30)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            for (Snowflake snowflake : snowflakes) {
                AffineTransform old = g2d.getTransform();
                g2d.translate(snowflake.x, snowflake.y);
                g2d.rotate(Math.toRadians(snowflake.rotation));
                drawSnowflake(g2d, snowflake.size);
                g2d.setTransform(old);
            }

            g2d.setFont(new Font("Serif", Font.BOLD, 48));
            g2d.setColor(new Color(255, 215, 0));
            g2d.drawString("Hello Winter!", 250, 300);
        }

        private void drawSnowflake(Graphics2D g2d, int size) {
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(1.5f));

            for (int i = 0; i < 6; i++) {
                g2d.drawLine(0, 0, size / 2, size);
                g2d.drawLine(size / 2, size, size / 3, size - 8);
                g2d.drawLine(size / 2, size, size - 5, size - 8);
                g2d.rotate(Math.toRadians(60));
            }
        }

        private static class Snowflake {
            int x, y;
            int speed;
            int size;
            double rotation;
            double rotationSpeed;
            double drift;

            private Snowflake(int x, int y, int speed, int size, double drift) {
                this.x = x;
                this.y = y;
                this.speed = speed;
                this.size = size;
                this.drift = drift;
                this.rotation = 0;
                this.rotationSpeed = (Math.random() - 0.5) * 2;
            }

            static Snowflake random(Random r, int width, int height) {
                return new Snowflake(
                        r.nextInt(Math.max(width, 1)),
                        r.nextInt(Math.max(height, 1)),
                        r.nextInt(3) + 1,
                        r.nextInt(10) + 15,
                        r.nextDouble() * 0.8 + 0.2
                );
            }

            void reset(Random r, int width) {
                y = -size;
                x = r.nextInt(Math.max(width, 1));
                speed = r.nextInt(3) + 1;
            }
        }
    }
}
