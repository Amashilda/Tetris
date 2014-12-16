package Tetris;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JFrame;

public class Tetris extends JFrame {

	private static final long FRAME_TIME = 1000L / 50L;
	private static final int TYPE_COUNT = FiguresType.values().length;
	private MainPanel board;
	private InstructionPanel side;
	private boolean isPaused;
	private boolean isNewGame;
	private boolean isGameOver;
	private int level;
	private int score;
	private Random random;
	private Time logicTimer;
	private FiguresType currentType;
	private FiguresType nextType;
	private FiguresType nextNextType;
	private int currentCol;
	private int currentRow;
	private int currentRotation;
	private int dropCooldown;
	private float gameSpeed;

	private Tetris() {
		super("Tetris");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		this.board = new MainPanel(this);
		this.side = new InstructionPanel(this);
		add(board, BorderLayout.CENTER);
		add(side, BorderLayout.EAST);

		addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {

				switch (e.getKeyCode()) {

				case KeyEvent.VK_S:
					if (!isPaused && dropCooldown == 0) {
						logicTimer.setCyclesPerSecond(25.0f);
					}
					break;

				case KeyEvent.VK_A:
					if (!isPaused
							&& board.isValidAndEmpty(currentType,
									currentCol - 1, currentRow, currentRotation)) {
						currentCol--;
					}
					break;

				case KeyEvent.VK_D:
					if (!isPaused
							&& board.isValidAndEmpty(currentType,
									currentCol + 1, currentRow, currentRotation)) {
						currentCol++;
					}
					break;

				case KeyEvent.VK_Q:
					if (!isPaused) {
						rotatePiece((currentRotation == 0) ? 3
								: currentRotation - 1);
					}
					break;

				case KeyEvent.VK_E:
					if (!isPaused) {
						rotatePiece((currentRotation == 3) ? 0
								: currentRotation + 1);
					}
					break;

				case KeyEvent.VK_X:
					if (!isGameOver && !isNewGame) {
						isPaused = !isPaused;
						logicTimer.setPaused(isPaused);
					}
					break;

				case KeyEvent.VK_ENTER:
					if (isGameOver || isNewGame) {
						resetGame();
					}
					break;

				case KeyEvent.VK_C:
					board.changeColorMode();

					Image img1 = Toolkit.getDefaultToolkit().createImage(
							"котэ1.jpg");
					Image img2 = Toolkit.getDefaultToolkit().createImage(
							"котэ2.jpg");
					if (board.getColorMode()) {
						img2 = Toolkit.getDefaultToolkit().createImage(
								"dog2.jpg");
						img1 = Toolkit.getDefaultToolkit().createImage(
								"dog1.jpg");
						// Image img1 =
						// Toolkit.getDefaultToolkit().createImage("котэ2.jpg");
						// board.setImageMode(img1);
						// Image img2 =
						// Toolkit.getDefaultToolkit().createImage("котэ1.jpg");
						// side.setImageMode(img2);
						// maaagic =___=""

					}
					board.setImageMode(img1);
					side.setImageMode(img2);

					break;

				}
			}

			@Override
			public void keyReleased(KeyEvent e) {

				switch (e.getKeyCode()) {

				case KeyEvent.VK_S:
					logicTimer.setCyclesPerSecond(gameSpeed);
					logicTimer.reset();
					break;
				}

			}

		});

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void startGame() {

		this.random = new Random();
		this.isNewGame = true;
		this.gameSpeed = 1.0f;

		this.logicTimer = new Time(gameSpeed);
		logicTimer.setPaused(true);

		while (true) {
			// Get the time that the frame started.
			long start = System.nanoTime();

			// Update the logic timer.
			logicTimer.update();

			if (logicTimer.hasElapsedCycle()) {
				updateGame();
			}

			if (dropCooldown > 0) {
				dropCooldown--;
			}

			renderGame();

			long delta = (System.nanoTime() - start) / 1000000L;
			if (delta < FRAME_TIME) {
				try {
					Thread.sleep(FRAME_TIME - delta);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void updateGame() {

		if (board.isValidAndEmpty(currentType, currentCol, currentRow + 1,
				currentRotation)) {
			// Increment the current row if it's safe to do so.
			currentRow++;
		} else {

			board.addPiece(currentType, currentCol, currentRow, currentRotation);

			int cleared = board.checkLines();
			if (cleared > 0) {
				score += 50 << cleared;
			}

			gameSpeed += 0.035f;
			logicTimer.setCyclesPerSecond(gameSpeed);
			logicTimer.reset();
			dropCooldown = 25;
			level = (int) (gameSpeed * 1.70f);
			spawnPiece();
		}
	}

	private void renderGame() {
		board.repaint();
		side.repaint();
	}

	private void resetGame() {
		this.level = 1;
		this.score = 0;
		this.gameSpeed = 1.0f;
		if( this.nextNextType != null) {
			this.nextType = nextNextType;
		} else {
			this.nextType = FiguresType.values()[random.nextInt(TYPE_COUNT)];
		}
		this.nextNextType = FiguresType.values()[random.nextInt(TYPE_COUNT)];
		this.isNewGame = false;
		this.isGameOver = false;
		board.clear();
		logicTimer.reset();
		logicTimer.setCyclesPerSecond(gameSpeed);
		spawnPiece();
	}

	private void spawnPiece() {

		this.currentType = nextType;
		this.currentCol = currentType.getSpawnColumn();
		this.currentRow = currentType.getSpawnRow();
		this.currentRotation = 0;
		
		if( this.nextNextType != null) {
			this.nextType = nextNextType;
		} else {
			this.nextType = FiguresType.values()[random.nextInt(TYPE_COUNT)];
		}
		this.nextNextType = FiguresType.values()[random.nextInt(TYPE_COUNT)];

		if (!board.isValidAndEmpty(currentType, currentCol, currentRow,
				currentRotation)) {
			this.isGameOver = true;
			logicTimer.setPaused(true);
		}
	}

	private void rotatePiece(int newRotation) {

		int newColumn = currentCol;
		int newRow = currentRow;

		int left = currentType.getLeftInset(newRotation);
		int right = currentType.getRightInset(newRotation);
		int top = currentType.getTopInset(newRotation);
		int bottom = currentType.getBottomInset(newRotation);

		if (currentCol < -left) {
			newColumn -= currentCol - left;
		} else if (currentCol + currentType.getDimension() - right >= MainPanel.COL_COUNT) {
			newColumn -= (currentCol + currentType.getDimension() - right)
					- MainPanel.COL_COUNT + 1;
		}

		if (currentRow < -top) {
			newRow -= currentRow - top;
		} else if (currentRow + currentType.getDimension() - bottom >= MainPanel.ROW_COUNT) {
			newRow -= (currentRow + currentType.getDimension() - bottom)
					- MainPanel.ROW_COUNT + 1;
		}

		if (board.isValidAndEmpty(currentType, newColumn, newRow, newRotation)) {
			currentRotation = newRotation;
			currentRow = newRow;
			currentCol = newColumn;
		}
	}

	public boolean isPaused() {
		return isPaused;
	}

	public boolean isGameOver() {
		return isGameOver;
	}

	public boolean isNewGame() {
		return isNewGame;
	}

	public int getScore() {
		return score;
	}

	public int getLevel() {
		return level;
	}

	public FiguresType getPieceType() {
		return currentType;
	}

	public FiguresType getNextPieceType() {
		return nextType;
	}

	public int getPieceCol() {
		return currentCol;
	}

	public int getPieceRow() {
		return currentRow;
	}

	public int getPieceRotation() {
		return currentRotation;
	}
	
	public FiguresType getNextNextPieceType() {
		return nextNextType;
	}

	public static void main(String[] args) {
		Tetris tetris = new Tetris();
		tetris.startGame();
	}
}