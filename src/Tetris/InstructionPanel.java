package Tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JPanel;

public class InstructionPanel extends JPanel {
	
	private static final int TILE_SIZE = MainPanel.TILE_SIZE >> 1;
	private static final int SHADE_WIDTH = MainPanel.SHADE_WIDTH >> 1;
	private static final int TILE_COUNT = 5;
	private static final int SQUARE_CENTER_X = 130;
	private static final int SQUARE_CENTER_Y = 65;
	private static final int SQUARE_SIZE = (TILE_SIZE * TILE_COUNT >> 1);
	private static final int SMALL_INSET = 20;
	private static final int LARGE_INSET = 25;
	private static final int STATS_INSET = 175;
	private static final int CONTROLS_INSET = 300;
	private static final int TEXT_STRIDE = 25;
	private static final Font SMALL_FONT = new Font("Times New Roman", Font.BOLD, 20);
	private static final Font LARGE_FONT = new Font("Times New Roman", Font.BOLD, 23);
	private static final Color DRAW_COLOR = new Color(128, 192, 128);
	private Tetris tetris;

	private Image img;
	
	public InstructionPanel(Tetris tetris) {
		this.tetris = tetris;
		
		setPreferredSize(new Dimension(200, MainPanel.PANEL_HEIGHT));
		//setBackground(Color.BLACK);
		img = Toolkit.getDefaultToolkit().createImage("êîòý2.jpg");
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawImage(img, 0, 0, null);
		
		//g.setColor(DRAW_COLOR);
		
		int offset;
		
		g.setFont(LARGE_FONT);
		g.drawString("Stats", SMALL_INSET, offset = STATS_INSET + 190);
		g.setFont(SMALL_FONT);
		g.drawString("Level: " + tetris.getLevel(), LARGE_INSET, offset += TEXT_STRIDE);
		g.drawString("Score: " + tetris.getScore(), LARGE_INSET, offset += TEXT_STRIDE);

		g.setFont(LARGE_FONT);
		g.drawString("Controls", SMALL_INSET, offset = CONTROLS_INSET + 190);
		g.setFont(SMALL_FONT);
		g.drawString("A - Move Left", LARGE_INSET, offset += TEXT_STRIDE);
		g.drawString("D - Move Right", LARGE_INSET, offset += TEXT_STRIDE);
		g.drawString("Q - Rotate Left", LARGE_INSET, offset += TEXT_STRIDE);
		g.drawString("E - Rotate Right", LARGE_INSET, offset += TEXT_STRIDE);
		g.drawString("S - Drop Down", LARGE_INSET, offset += TEXT_STRIDE);
		g.drawString("X - Pause", LARGE_INSET, offset += TEXT_STRIDE);
		g.drawString("C - Ñhange Regime", LARGE_INSET, offset += TEXT_STRIDE);

		g.setFont(LARGE_FONT);
		g.drawString("Next Piece:", SMALL_INSET, 50);
		g.drawRect(SQUARE_CENTER_X - SQUARE_SIZE, SQUARE_CENTER_Y - SQUARE_SIZE + 50, SQUARE_SIZE * 2, SQUARE_SIZE * 2);

		g.drawString("Next next Piece:", SMALL_INSET, 210);
		g.drawRect(SQUARE_CENTER_X - SQUARE_SIZE, SQUARE_CENTER_Y - SQUARE_SIZE + 210, SQUARE_SIZE * 2, SQUARE_SIZE * 2);

		
		FiguresType type = tetris.getNextPieceType();
		if(!tetris.isGameOver() && type != null) {
			
			int cols = type.getCols();
			int rows = type.getRows();
			int dimension = type.getDimension();
		

			int startX = (SQUARE_CENTER_X - (cols * TILE_SIZE / 2));
			int startY = (SQUARE_CENTER_Y - (rows * TILE_SIZE / 2)+50);
	
			int top = type.getTopInset(0);
			int left = type.getLeftInset(0);
	
			for(int row = 0; row < dimension; row++) {
				for(int col = 0; col < dimension; col++) {
					if(type.isTile(col, row, 0)) {
						drawTile(type, startX + ((col - left) * TILE_SIZE), startY + ((row - top) * TILE_SIZE), g);
					}
				}
			}
		}
		
//		g.setFont(LARGE_FONT);
//		g.drawString("Next next Piece:", SMALL_INSET, 210);
//		g.drawRect(SQUARE_CENTER_X - SQUARE_SIZE, SQUARE_CENTER_Y - SQUARE_SIZE + 210, SQUARE_SIZE * 2, SQUARE_SIZE * 2);

		FiguresType type2 = tetris.getNextNextPieceType();
		if(!tetris.isGameOver() && type2 != null) {
			
			int cols = type2.getCols();
			int rows = type2.getRows();
			int dimension = type2.getDimension();
		

			int startX = (SQUARE_CENTER_X - (cols * TILE_SIZE / 2));
			int startY = (SQUARE_CENTER_Y - (rows * TILE_SIZE / 2)) + 210;
	
			int top = type2.getTopInset(0);
			int left = type2.getLeftInset(0);
	
			for(int row = 0; row < dimension; row++) {
				for(int col = 0; col < dimension; col++) {
					if(type2.isTile(col, row, 0)) {
						drawTile(type2, startX + ((col - left) * TILE_SIZE), startY + ((row - top) * TILE_SIZE), g);
					}
				}
			}
		}
	}

	private void drawTile(FiguresType type, int x, int y, Graphics g) {
		
		g.setColor(type.getBaseColor());
		//g.setColor(Color.BLACK);
		g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
		//g.setColor(Color.BLACK);
		g.setColor(type.getDarkColor());
		g.fillRect(x, y + TILE_SIZE - SHADE_WIDTH, TILE_SIZE, SHADE_WIDTH);
		g.fillRect(x + TILE_SIZE - SHADE_WIDTH, y, SHADE_WIDTH, TILE_SIZE);
		g.setColor(type.getLightColor());
		//g.setColor(Color.BLACK);
		for(int i = 0; i < SHADE_WIDTH; i++) {
			g.drawLine(x, y + i, x + TILE_SIZE - i - 1, y + i);
			g.drawLine(x + i, y, x + i, y + TILE_SIZE - i - 1);
		}
	}
	
	public void setImageMode(Image img){
		this.img = img;
		repaint();
	}
		
}