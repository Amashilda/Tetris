package Tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JPanel;

public class MainPanel extends JPanel {

	public static final int COLOR_MIN = 35;
	public static final int COLOR_MAX = 255 - COLOR_MIN;
	private static final int BORDER_WIDTH = 5;
	public static final int COL_COUNT = 10;
	private static final int VISIBLE_ROW_COUNT = 20;
	private static final int HIDDEN_ROW_COUNT = 2;
	public static final int ROW_COUNT = VISIBLE_ROW_COUNT + HIDDEN_ROW_COUNT;
	public static final int TILE_SIZE = 48; //<- ^.^
	public static final int SHADE_WIDTH = 4;
	private static final int CENTER_X = COL_COUNT * TILE_SIZE / 2;
	private static final int CENTER_Y = VISIBLE_ROW_COUNT * TILE_SIZE / 2;
	public static final int PANEL_WIDTH = COL_COUNT * TILE_SIZE + BORDER_WIDTH * 2;
	public static final int PANEL_HEIGHT = VISIBLE_ROW_COUNT * TILE_SIZE + BORDER_WIDTH * 2;
	private static final Font LARGE_FONT = new Font("Times New Roman", Font.BOLD,  70);
	private static final Font SMALL_FONT = new Font("Times New Roman", Font.ITALIC, 30);
	private boolean IS_COLOR_GAME_ENABLED = false;
	
	private Tetris tetris;
	private FiguresType[][] tiles;
	
	
	
	private Image img;
	
	

	public MainPanel(Tetris tetris) {
		this.tetris = tetris;
		this.tiles = new FiguresType[ROW_COUNT][COL_COUNT];
		
		setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		img = Toolkit.getDefaultToolkit().createImage("котэ1.jpg");
		//setBackground(Color.BLACK);
	}

	public void clear() {

		for(int i = 0; i < ROW_COUNT; i++) {
			for(int j = 0; j < COL_COUNT; j++) {
				tiles[i][j] = null;
			}
		}
	}

	public boolean isValidAndEmpty(FiguresType type, int x, int y, int rotation) {
		if(x < -type.getLeftInset(rotation) || x + type.getDimension() - type.getRightInset(rotation) >= COL_COUNT) {
			return false;
		}
		
		if(y < -type.getTopInset(rotation) || y + type.getDimension() - type.getBottomInset(rotation) >= ROW_COUNT) {
			return false;
		}

		for(int col = 0; col < type.getDimension(); col++) {
			for(int row = 0; row < type.getDimension(); row++) {
				if(type.isTile(col, row, rotation) && isOccupied(x + col, y + row)) {
					return false;
				}
			}
		}
		return true;
	}

	public void addPiece(FiguresType type, int x, int y, int rotation) {

		for(int col = 0; col < type.getDimension(); col++) {
			for(int row = 0; row < type.getDimension(); row++) {
				if(type.isTile(col, row, rotation)) {
					setTile(col + x, row + y, type);
				}
			}
		}
	}

	public int checkLines() {
		int completedLines = 0;

		for(int row = 0; row < ROW_COUNT; row++) {
			if(checkLine(row)) {
				completedLines++;
			}
		}
		return completedLines;
	}

	private boolean checkLine(int line) {
			String color = null;
			if (getTile(0,line) != null) {
				color = getTile(0, line).getBaseColor().toString();
			}  else {
				return false;
			}
		
		
		
		
		for(int col = 0; col < COL_COUNT; col++) {
			if(!isOccupied(col, line)) {
				return false;
			}
		if (IS_COLOR_GAME_ENABLED){
			FiguresType tile = null;
			try {
				tile = getTile(col, line);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (tile != null){
				if(!tile.getBaseColor().toString().equals(color)){
					return false;
				}
			}
		}
		
		
		
		}

		for(int row = line - 1; row >= 0; row--) {
			for(int col = 0; col < COL_COUNT; col++) {
				setTile(col, row + 1, getTile(col, row));
			}
		}
		return true;
	}

	private boolean isOccupied(int x, int y) {
		return tiles[y][x] != null;
	}

	private void setTile(int  x, int y, FiguresType type) {
		tiles[y][x] = type;
	}
	
	private FiguresType getTile(int x, int y) {
		return tiles[y][x];
	}
	
	@Override
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		g.drawImage(img, 0, 0, null);
		
		//This helps simplify the positioning of things.
		g.translate(BORDER_WIDTH, BORDER_WIDTH);

		if(tetris.isPaused()) {
			g.setFont(LARGE_FONT);
			g.setColor(Color.BLACK);
			String msg = "PAUSED";
			g.drawString(msg, CENTER_X - g.getFontMetrics().stringWidth(msg) / 2, CENTER_Y);
		} else if(tetris.isNewGame() || tetris.isGameOver()) {
			g.setFont(LARGE_FONT);
			g.setColor(Color.BLACK);

			String msg = tetris.isNewGame() ? "TETRIS" : "GAME OVER";
			g.drawString(msg, CENTER_X - g.getFontMetrics().stringWidth(msg) / 2, 300);
			g.setFont(SMALL_FONT);
			msg = "Press Enter to Play" + (tetris.isNewGame() ? "" : " Again");
			g.drawString(msg, CENTER_X - g.getFontMetrics().stringWidth(msg) / 2, 450);
		} else {

			for(int x = 0; x < COL_COUNT; x++) {
				for(int y = HIDDEN_ROW_COUNT; y < ROW_COUNT; y++) {
					FiguresType tile = getTile(x, y);
					if(tile != null) {
						drawTile(tile, x * TILE_SIZE, (y - HIDDEN_ROW_COUNT) * TILE_SIZE, g);
					}
				}
			}

			FiguresType type = tetris.getPieceType();
			int pieceCol = tetris.getPieceCol();
			int pieceRow = tetris.getPieceRow();
			int rotation = tetris.getPieceRotation();
			
			for(int col = 0; col < type.getDimension(); col++) {
				for(int row = 0; row < type.getDimension(); row++) {
					if(pieceRow + row >= 2 && type.isTile(col, row, rotation)) {
						drawTile(type, (pieceCol + col) * TILE_SIZE, (pieceRow + row - HIDDEN_ROW_COUNT) * TILE_SIZE, g);
					}
				}
			}

			Color base = type.getBaseColor();
			base = new Color(base.getRed(), base.getGreen(), base.getBlue(), 20);
			for(int lowest = pieceRow; lowest < ROW_COUNT; lowest++) {
				if(isValidAndEmpty(type, pieceCol, lowest, rotation)) {					
					continue;
				}
				
				lowest--;
				
				for(int col = 0; col < type.getDimension(); col++) {
					for(int row = 0; row < type.getDimension(); row++) {
						if(lowest + row >= 2 && type.isTile(col, row, rotation)) {
							drawTile(base, base.brighter(), base.darker(), (pieceCol + col) * TILE_SIZE, (lowest + row - HIDDEN_ROW_COUNT) * TILE_SIZE, g);
						}
					}
				}
				
				break;
			}

			g.setColor(Color.BLACK);
			for(int x = 0; x < COL_COUNT; x++) {
				for(int y = 0; y < VISIBLE_ROW_COUNT; y++) {
					g.drawLine(0, y * TILE_SIZE, COL_COUNT * TILE_SIZE, y * TILE_SIZE);
					g.drawLine(x * TILE_SIZE, 0, x * TILE_SIZE, VISIBLE_ROW_COUNT * TILE_SIZE);
				}
			}
		}

		g.setColor(Color.BLACK);
		g.drawRect(0, 0, TILE_SIZE * COL_COUNT, TILE_SIZE * VISIBLE_ROW_COUNT);
	}

	private void drawTile(FiguresType type, int x, int y, Graphics g) {
		drawTile(type.getBaseColor(), type.getLightColor(), type.getDarkColor(), x, y, g);
	}

	private void drawTile(Color base, Color light, Color dark, int x, int y, Graphics g) {
		g.setColor(base);
		g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
		g.setColor(dark);
		g.fillRect(x, y + TILE_SIZE - SHADE_WIDTH, TILE_SIZE, SHADE_WIDTH);
		g.fillRect(x + TILE_SIZE - SHADE_WIDTH, y, SHADE_WIDTH, TILE_SIZE);
		g.setColor(light);
		for(int i = 0; i < SHADE_WIDTH; i++) {
			g.drawLine(x, y + i, x + TILE_SIZE - i - 1, y + i);
			g.drawLine(x + i, y, x + i, y + TILE_SIZE - i - 1);
		}
	}
	
	public void setImageMode(Image img){
		this.img = img;
		repaint();
	}
	
	public boolean getColorMode(){
		return IS_COLOR_GAME_ENABLED;
	}
	
	public void changeColorMode() {
		IS_COLOR_GAME_ENABLED = !IS_COLOR_GAME_ENABLED;
		//magic ^.^
	}

}