import java.awt.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class homework {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BufferedReader br = null;
		FileReader fr = null;
		int length = -1;
		int[][] nursey = null;
		int target = -1;
		String searchType = "";
		try {
			fr = new FileReader("input.txt");
			br = new BufferedReader(fr);
			searchType = new String(br.readLine());
			searchType = searchType.trim();
			length = Integer.parseInt(br.readLine());
			nursey = new int[length][length];
			target = Integer.parseInt(br.readLine());
			for (int i = 0; i < length; i++) {
				String line = br.readLine();
				for (int j = 0; j < length; j++) {
					nursey[i][j] = line.charAt(j) - '0';
				}
			}
			// System.out.println(searchType);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (searchType.equals("DFS")) {
			long start = System.currentTimeMillis();
			if (dfs(nursey, length, 0, 0, target)) {
				printFile(nursey);
			} else {
				printFail();
			}
			System.out.println(System.currentTimeMillis() - start);
			start = System.currentTimeMillis();
		} else if (searchType.equals("BFS")) {
			int[][] result = new int[length][length];
			result = bfs(nursey, target);
			if (result != null) {
				printFile(result);
			} else {
				printFail();
			}
		} else if (searchType.equals("SA")) {
			int[][] result = new int[length][length];
			result = simulateAnnealing(nursey, length, target);
			if (result != null) {
				printFile(result);
			} else {
				printFail();
			}
		}
	}
	
	public static void printFail() {
		try {
			PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
			writer.println("FAIL");
			writer.close();
			System.out.println("finish");
		} catch (IOException e) {
			System.out.println("fail when write into the file");
		}

	}
	public static void printFile(int[][] nursey) {
		int length = nursey.length;
		try {
			PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
			writer.println("OK");
			for (int i = 0; i < length; i++) {
				for (int j = 0; j < length; j++) {
					if (nursey[i][j] > 0) {
						writer.print(nursey[i][j]);
					} else {
						writer.print('0');
					}
				}
				writer.println();
			}
			writer.close();
			System.out.println("finish");
		} catch (IOException e) {
			System.out.println("fail when write into the file");
		}
	}

	public static int[][] bfs(int[][] nursey, int target) {
		int length = nursey.length;
		int col = 0;
		int row = 0;
		Queue<int[][]> queue = new LinkedList<>();
		queue.add(nursey);
		while (queue.size() != 0 && row < length) {
			Queue<int[][]> tempQueue = new LinkedList<>();
			boolean changeRow = false;
			while (queue.size() != 0) {
				int[][] instance = queue.poll();
				tempQueue.add(instance);
				for (int j = col; j < length; j++) {
					if (instance[row][j] == 0) {
						if (!checkPossible(instance, row, j, target - 1)) {
							continue;
						}
						int[][] temp = new int[length][length];
						int sum = 0;
						for (int m = 0; m < length; m++) {
							for (int n = 0; n < length; n++) {
								temp[m][n] = instance[m][n];
								if (instance[m][n] == 1) {
									sum++;
								}
							}
						}
						temp[row][j] = 1;
						if (sum + 1 == target) {
							return temp;
						}
						reviseMatrix(temp, length, row, j, false);
						tempQueue.add(temp);
						if (j == length-1 && queue.size() == 0){
							row++;
							col=0;
							break;
						}
					}
					else {
						if(queue.size()!=0){
							break;
						}
						col++;
						if(col==length){
							changeRow = true;
						}
						break;
					}
				}
			}
			queue = new LinkedList<>(tempQueue);
			if (changeRow) {
				row++;
				col = 0;
			}
		}
		return null;
	}

	public static boolean checkPossible(int[][] nursey, int row, int col, int target) {
		int sum = 0;
		int length = nursey.length;
		for (int i = 0; i <= row; i++) {
			for (int j = 0; j < col; j++) {
				if (nursey[i][j] == 1) {
					sum++;
				}
			}
		}
		if (length * (length - row) - col - 1 + sum < target) {
			return false;
		}
		return true;
	}

	public static boolean dfs(int[][] nursey, int length, int row, int col, int target) {
		if (target == 0) {
			return true;
		}
		if (row == length) {
			return false;
		}

		for (int i = row; i < length; i++) {
			for (int j = col; j < length; j++) {
				if (nursey[i][j] != 0) {
					continue;
				} else {
					reviseMatrix(nursey, length, i, j, false);
					nursey[i][j] = 1;
					boolean result = dfs(nursey, length, i, j + 1, target - 1)
							|| dfs(nursey, length, i + 1, 0, target - 1);
					if (result)
						return result;
					reviseMatrix(nursey, length, i, j, true);
					nursey[i][j] = 0;
				}
			}
		}
		return false;
	}

	// if addVal==true, add value, else decrease value.
	public static void reviseMatrix(int[][] nursey, int length, int i, int j, boolean addVal) {
		// make all the path unavailable which the tongue can reach
		for (int index = i + 1; index < length; index++) {
			if (nursey[index][j] >= 1) {
				break;
			}
			if (nursey[index][j] <= 0) {
				if (addVal == true && nursey[index][j] < 0) {
					nursey[index][j]++;
				} else if (addVal == false) {
					nursey[index][j]--;
				}
			} else {
				break;
			}
		}
		for (int index = i - 1; index >= 0; index--) {
			if (nursey[index][j] >= 1) {
				break;
			}
			if (nursey[index][j] <= 0) {
				if (addVal == true && nursey[index][j] != 0) {
					nursey[index][j]++;
				} else if (addVal == false) {
					nursey[index][j]--;
				}
			} else {
				break;
			}
		}
		for (int index = j + 1; index < length; index++) {
			if (nursey[i][index] >= 1) {
				break;
			}
			if (nursey[i][index] <= 0) {
				if (addVal == true && nursey[i][index] < 0) {
					nursey[i][index]++;
				} else if (addVal == false) {
					nursey[i][index]--;
				}
			} else {
				break;
			}
		}
		for (int index = j - 1; index >= 0; index--) {
			if (nursey[i][index] <= 0) {
				if (addVal == true && nursey[i][index] < 0) {
					nursey[i][index]++;
				} else if (addVal == false) {
					nursey[i][index]--;
				}
			} else {
				break;
			}
		}
		// diagnose
		int p2 = j + 1;
		for (int p1 = i + 1; p1 < length && p2 < length; p1++) {
			if (p2 < 0 || p1 < 0 || p2 >= length || p1 >= length) {
				break;
			}
			if (nursey[p1][p2] >= 1) {
				break;
			}
			if (addVal == true && nursey[p1][p2] < 0) {
				nursey[p1][p2]++;
			} else if (addVal == false && nursey[p1][p2] <= 0) {
				nursey[p1][p2]--;
			}
			p2++;
		}
		p2 = j - 1;
		for (int p1 = i - 1; p1 >= 0 && p2 >= 0; p1--) {
			if (p2 < 0 || p1 < 0 || p2 >= length || p1 >= length) {
				break;
			}
			if (nursey[p1][p2] >= 1) {
				break;
			}
			if (addVal == true && nursey[p1][p2] < 0) {
				nursey[p1][p2]++;
			} else if (addVal == false && nursey[p1][p2] <= 0) {
				nursey[p1][p2]--;
			}
			p2--;
		}
		p2 = j + 1;
		for (int p1 = i - 1; p1 >= 0 && p2 < length; p1--) {
			if (p2 < 0 || p1 < 0 || p2 >= length || p1 >= length) {
				break;
			}
			if (nursey[p1][p2] >= 1) {
				break;
			}
			if (nursey[p1][p2] > 0) {
				break;
			}
			if (addVal == true && nursey[p1][p2] < 0) {
				nursey[p1][p2]++;
			}
			if (addVal == false && nursey[p1][p2] <= 0) {
				nursey[p1][p2]--;
			}
			p2++;
		}
		p2 = j - 1;
		for (int p1 = i + 1; p1 < length && p2 >= 0; p1++) {
			if (p2 < 0 || p1 < 0 || p2 >= length || p1 >= length) {
				break;
			}
			if (nursey[p1][p2] >= 1) {
				break;
			}
			if (nursey[p1][p2] > 0) {
				break;
			}
			if (addVal == true && nursey[p1][p2] < 0) {
				nursey[p1][p2]++;
			} else if (addVal == false && nursey[p1][p2] <= 0) {
				nursey[p1][p2]--;
			}
			p2--;
		}
	}

	public static int[][] f(int[][] map, int count, int x, int y, int r, int c, int cross1, int cross2, int req, int[][] board) {
		 if (count == req) {
			 return board;
		 }
		 if (y == map.length) {
			  x++;
			  y = 0;
			  cross1 >>= 1;
			  cross2 <<= 1;
			  r = 0;
		 }
		 if (x >= map.length) return null;
		 if (map[x][y] != 2) {
		  int y_bit = 1 << y;
		  if (r == 0 && (c & y_bit) == 0 && (cross1 & y_bit)  == 0&& (cross2 & y_bit) == 0) {
		   board[x][y] = 1;
		   if (f(map, count + 1, x, y+1, 1, c | y_bit, cross1 | y_bit, cross2 | y_bit, req, board) != null) return board;
		   board[x][y] = 0;
		  }
		  if (f(map,count, x, y+1, r, c, cross1, cross2, req, board) != null) return board;
		 }
		 else{
		  int y_bit = ~(1 << y);
		  if (f(map, count, x, y + 1, 0, c & y_bit, cross1 & y_bit, cross2 & y_bit, req, board) != null) return board;
		 }
		 return null;
		}

	public static int[][] dfs2(int[][] nursey, int length, int target) {
		 int[][] board = new int[length][length];
		 return f(nursey, 0, 0, 0, 0, 0, 0, 0,target, board);
	}	
	
	public static int[][] simulateAnnealing(int[][] nursey, int length, int target) {
		// initialize
		int count = 0, curCost = 0;
		int[][] curState = new int[length][length];
		for (int i = 0; i < length; ++i)
			for (int j = 0; j < length; ++j)
				curState[i][j] = nursey[i][j];
		Random rand = new Random();
		while (count < target) {
			int x, y;
			do {
				x = rand.nextInt(length);
				y = rand.nextInt(length);
			} while (curState[x][y] != 0);
			curState[x][y] = 1;
			count++;
		}
		// calculate the current cost
		for (int i = 0; i < length; ++i)
			for (int j = 0; j < length; ++j)
				if (curState[i][j] == 1)
					curCost += getCost(curState, i, j, length);
		// iteration
		long startTime = System.currentTimeMillis();
		long end = 280 * 1000;
		while (curCost > 0) {
			// choose one lizard(index)
			long curTime = System.currentTimeMillis() - startTime;
			if (curTime > end)
				break;
			int index = rand.nextInt(target);
			int x = 0, y = 0;
			int xNext, yNext;
			double delta = 0;
			double possibility, threshold;
			int[][] nextState = new int[length][length];
			for (int i = 0; i < length; i++)
				for (int j = 0; j < length; j++)
					nextState[i][j] = curState[i][j];
			for (int i = 0; i < length; ++i) {
				for (int j = 0; j < length; ++j) {
					if (curState[i][j] == 1)
						index--;
					if (index < 0) {
						x = i;
						y = j;
						break;
					}
				}
				if (index < 0)
					break;
			}
			// randomize the next position for this lizard
			do {
				xNext = rand.nextInt(length);
				yNext = rand.nextInt(length);
			} while (curState[xNext][yNext] > 0);
			nextState[xNext][yNext] = 1;
			nextState[x][y] = 0;
			// calculate the delta of two costs
			delta += 2 * getCost(curState, x, y, length);
			delta -= 2 * getCost(nextState, xNext, yNext, length);
			// annealing
			possibility = Math.random();
			threshold = calculateThreshold(delta, curTime, end);
			// if delta > 0, update to nextState, else update to nextState with a probability(threshold)
			if (delta > 0 || possibility < threshold) {
				curState = nextState;
				curCost -= delta;
			}
//			System.out.println(Integer.toString(curCost) + ' ' + Double.toString(delta) + ' '
//					+ Double.toString(threshold) + ' ' + Long.toString(curTime / 1000));
		}
		if (curCost == 0)
			return curState;
		else
			return null;
	}

	public static int getCost(int[][] state, int x, int y, int length) {
		int[][] dirs = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { -1, -1 }, { 1, 1 }, { 1, -1 }, { -1, 1 } };
		int cost = 0;
		for (int[] dir : dirs) {
			int xNext = x + dir[0], yNext = y + dir[1];
			while (xNext >= 0 && xNext < length && yNext >= 0 && yNext < length) {
				if (state[xNext][yNext] == 2)
					break;
				if (state[xNext][yNext] == 1)
					cost++;
				xNext += dir[0];
				yNext += dir[1];
			}
		}
		return cost;
	}

	public static double calculateThreshold(double delta, long current, long end) {
		double temperature = (double) (end - current) / end;
		double E = (delta - 1) / 2;
		return Math.exp(E / (0.1 + temperature));
	}
}
