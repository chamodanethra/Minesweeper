import java.util.*;

public class Main {
    static final int gridSize = 9;
    static final int scaleY = (int) Math.pow(10, String.valueOf(gridSize).length() + 1);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("How many mines do you want on the field? ");
        int mines = scanner.nextInt();
        int xCord[] = new int[mines];
        int yCord[] = new int[mines];
        ArrayList<Integer> comPosActualMines = new ArrayList<>(mines + 1);
        // comPosActualMines = List of combinedPositions calculated using x,y coordinates of actual mines
        ArrayList<Integer> comPosMarkedMines = new ArrayList<>(mines);
        // comPosMarkedMines = List of combinedPositions calculated using x,y coordinates of marked mines
        ArrayList<Integer> comPosMarkedFrees = new ArrayList<>();
        // comPosMarkedFrees = List of combinedPositions calculated using x,y coordinates of marked mines
        HashMap<Integer, Integer> comPosThreats = new HashMap<>();
        // comPosThreats = Will be used to store combinedPositions and threats count of threat Coordinates
        ArrayList<Integer> comPosUnexplored = new ArrayList<>();
        // comPosUnexplored = Will be used to store combinedPositions of explored-non-threat Coordinates
        Random r = new Random();
        L1:
        for (int i = 0; i < mines; i++) {
            xCord[i] = r.nextInt(gridSize);
            yCord[i] = r.nextInt(gridSize);
            int temp = scaleY * yCord[i] + xCord[i];
            for (int j = 0; j < i; j++) {
                if (temp == comPosActualMines.get(j)) {
                    i--;
                    continue L1;
                }
            }
            comPosActualMines.add(temp);
        }
        comPosActualMines.add(scaleY * gridSize);
        Collections.sort(comPosActualMines);
        int c = 0, k = 0;
        System.out.println(" |123456789|");
        System.out.println("-|---------|");
        while (k < gridSize * gridSize) {
            if (k % gridSize == 0) {
                System.out.print(k / gridSize + 1 + "|");
            }
            if (comPosActualMines.get(c) == scaleY * (k / gridSize) + k % gridSize) {
                System.out.print(".");
                c++;
            } else {
                int threats = 0;
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (i == 0 && j == 0) continue;
                        if (comPosActualMines.subList(0, mines).contains(scaleY * (k / gridSize - i) + k % gridSize + j)) {
                            threats++;
                        }
                    }
                }
                if (threats != 0) {
                    comPosThreats.put(scaleY * (k / gridSize) + k % gridSize, threats);
                }
                System.out.print(".");
            }
            if (k++ % gridSize == gridSize - 1) {
                System.out.println("|");
            }
        }
        System.out.println("-|---------|");
        do {
            System.out.print("Set/unset mines marks or claim a cell as free: ");
            int xCordUser = scanner.nextInt() - 1;
            int yCordUser = scanner.nextInt() - 1;
            String userAction = scanner.next();
            int comPosCurrent = scaleY * yCordUser + xCordUser;
            if (comPosMarkedMines.contains(comPosCurrent)) {
                if (userAction.equals("mine")) {
                    comPosMarkedMines.remove(Integer.valueOf(comPosCurrent));
                    comPosUnexplored.remove(Integer.valueOf(comPosCurrent));
                    printMineField(comPosMarkedMines, comPosThreats, comPosUnexplored, comPosCurrent);
                } else {
                    if (comPosActualMines.subList(0, mines).contains(comPosCurrent)) {
                        printMineField(comPosMarkedMines, comPosThreats, comPosUnexplored, -1);
                        System.out.println("You stepped on a mine and failed!");
                        return;
                    } else {
                        comPosMarkedFrees.add(comPosCurrent);
                        comPosMarkedMines.remove(Integer.valueOf(comPosCurrent));
                        printMineField(comPosMarkedMines, comPosThreats, comPosUnexplored, -1);
                    }
                }
            } else if (comPosMarkedFrees.contains(comPosCurrent)) {
                if (comPosThreats.containsKey(comPosCurrent)) {
                    System.out.println("There is a number here!");
                } else {
                    comPosUnexplored.remove(Integer.valueOf(comPosCurrent));
                    printMineField(comPosMarkedMines, comPosThreats, comPosUnexplored, -1);
                }
            } else {
                if (userAction.equals("mine")) {
                    comPosMarkedMines.add(comPosCurrent);
                    comPosMarkedFrees.remove(Integer.valueOf(comPosCurrent));
                    printMineField(comPosMarkedMines, comPosThreats, comPosUnexplored, -1);
                } else {
                    if (!comPosMarkedFrees.contains(comPosCurrent)) {
                        if (!comPosActualMines.subList(0, mines).contains((comPosCurrent))) {
                            if (!comPosThreats.containsKey(comPosCurrent)) {
                                LinkedList<Integer> tempUnexplored = new LinkedList<>();
                                tempUnexplored.add(comPosCurrent);
                                while (!tempUnexplored.isEmpty()) {
                                    int head = tempUnexplored.remove();
                                    ArrayList<Integer> tempNeighbouring = new ArrayList<>();
                                    for (int i = -1; i <= 1; i++) {
                                        for (int j = -1; j <= 1; j++) {
                                            if (i == 0 && j == 0) continue;
                                            int comPosNeighboring = head + i + scaleY * j;
                                            if (comPosNeighboring < 0 ||
                                                    comPosNeighboring % scaleY != (comPosNeighboring % scaleY) % gridSize ||
                                                    comPosNeighboring % scaleY == scaleY - 1 ||
                                                    comPosNeighboring > (scaleY + 1) * (gridSize - 1)) {
                                                continue;
                                            }
                                            tempNeighbouring.add(comPosNeighboring);
                                        }
                                    }
                                    if (Collections.disjoint(comPosActualMines.subList(0, mines), tempNeighbouring) && !comPosUnexplored.contains(head) && !comPosThreats.containsKey(head)) {
                                        comPosUnexplored.add(head);
                                        tempUnexplored.addAll(tempNeighbouring);
                                        comPosMarkedMines.removeAll(tempNeighbouring);
                                    }
                                }
                            } else {
                                comPosUnexplored.add(comPosCurrent);
                            }
                            printMineField(comPosMarkedMines, comPosThreats, comPosUnexplored, -1);
                        } else {
                            printMineField(comPosMarkedMines, comPosThreats, comPosUnexplored, -1);
                            System.out.println("You stepped on a mine and failed!");
                            return;
                        }
                    }
                }
            }
            Collections.sort(comPosMarkedMines);
        } while (!comPosActualMines.subList(0, mines).equals(comPosMarkedMines) || !Collections.disjoint(comPosActualMines, comPosMarkedFrees));
        System.out.println("Congratulations! You found all the mines!");
    }

    private static void printMineField(ArrayList<Integer> comPosMarkedMines, HashMap<Integer, Integer> comPosThreats, ArrayList<Integer> comPosUnexplored, int comPosCurrent) {
        int k = 0, c = 0;
        System.out.println(" |123456789|");
        System.out.println("-|---------|");
        while (k < gridSize * gridSize) {
            if (k % gridSize == 0) {
                System.out.print(k / gridSize + 1 + "|");
            }
            int comPosCur = scaleY * (k / gridSize) + k % gridSize;
            if (comPosMarkedMines.contains(comPosCur)) {
                System.out.print("*");
            } else if (comPosThreats.containsKey(comPosCur) && comPosCurrent != comPosCur) {
                System.out.print(comPosThreats.get(comPosCur));
            } else if (comPosUnexplored.contains(comPosCur)) {
                System.out.print("/");
            } else {
                System.out.print(".");
            }
            if (k++ % gridSize == gridSize - 1) {
                System.out.println("|");
            }
        }
        System.out.println("-|---------|");
    }
}
