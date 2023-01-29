package mazegeneratorgroup; //!! DONT FORGET TO REMOVE !!

import java.util.Random;
import java.util.Collections;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.InputMismatchException;
/**
 * @author  
 *          Emon Rifat Hasan - 1832901
 *          Zawad Wasik Ahmed - 1912485
 *          Muhammad Hariz Bin Hasnan - 1827929
 * Reference : https://www.carterschultz.com/mazelab.html
 * Reference 2 : https://en.wikipedia.org/wiki/Kruskal%27s_algorithm
 * 
 * Things taken from the reference :Kruskal's Algorithm (just the concept), the code is our own interpretation
 *                                  of the algorithm as our reference didn't have any sample code. We think the
 *                                  output looks similar enough to the maze example given in the instructions 
 *                                  page in Google classroom. And the way the maze is formed follows how the 
 *                                  sample video instructed us to do, so we hope this is the right method of executing it.
 * 
 *                                 :TAKE NOTE, the shortest path algorithm doesnt work perfectly, we tried our best sir :(, for example if you
 *                                  wanted a path that begins from 0,0 it will output an error. Another shortcoming of 
 *                                  the algorithm is that sometimes the parth will skip a number for example rather than (3,3) to (3,4)
 *                                  it will instead do (3,3) to (3,5). Lastly if the path leads to coordinates that are double digit such
 *                                  as (10,8), it will instead print as (01,8); although the actual coordinate is correct it is only
 *                                  printed wrong. So the algorithm basically only works if the path chosen is well within the maze, such as (3,4) to (7,8)
 *                                  rather than its edges. This is the best we could do trying to impliment tree data structure as we have never
 *                                  learnt it before hopefully it is enough to get extra marks for early submission.
 */
public class MazegeneratorGroup {
    private final int x; //Final width of maze
    private final int y; //Final length of maze
    private final String[][] mazeWall; //Mazewall array that is represented by 1111 for up,down,right,left
    private final int[][] mazeGroup; //Mazegroup array for merging and checking of cells
    private final boolean[][] mazeVisit; //Mazevisit array for keeping track of visited cells
    Random rand = new Random();
    Scanner input = new Scanner(System.in);
    
    //Constructor to initialize the walls, dimensions as well as the group(for merging later)
    public MazegeneratorGroup(int x, int y){
        int init = 1;
        this.x = x;
        this.y = y;
        mazeWall = new String[this.x][this.y];
        mazeGroup = new int[this.x][this.y];
        mazeVisit = new boolean[this.x][this.y];
        for(int lx = 0; lx < x; lx++){
            for(int ly = 0; ly < y; ly++){
                mazeWall[lx][ly] = "1111";
                mazeGroup[lx][ly] = init;
                init += 1; 
            }
        }
        theMaze(rand.nextInt(x),rand.nextInt(y));
    }
    
    //Enum to help with calculation of maze traversal up,down,right or left.
    private enum DIR{
        U(0, -1, 0), D(2, 1, 0), R(1, 0, 1), L(3, 0, -1);
        
        private final int move;
        private final int dirX;
        private final int dirY;
        private DIR opposite;
        
        private DIR(int move, int dirX, int dirY){
            this.move = move;
            this.dirX = dirX;
            this.dirY = dirY;
        }
        
        static {
            U.opposite = D;
            D.opposite = U;
            R.opposite = L;
            L.opposite = R;
        }
        
        public String changeString(String manipulate, int dir){
            StringBuilder vessel = new StringBuilder(manipulate);
            vessel.setCharAt(dir, '0');
            return vessel.toString();
        }
    }
    
    //To compare that checked cells are within maze
    private static boolean between(int cN, int upper){
        return (cN >= 0) && (cN < upper);
    }
    
    //The actual creation of the maze as well as the tearing down of walls
    public void theMaze(int oX, int oY){
        DIR[] directions = DIR.values();
        Collections.shuffle(Arrays.asList(directions));
        for(DIR e : directions){
            int nX = oX + e.dirX;
            int nY = oY + e.dirY;
            if((between(nX,x)) && (between(nY,y)) && (mazeGroup[oX][oY] != mazeGroup[nX][nY])){
                mazeWall[oX][oY] = e.changeString(mazeWall[oX][oY], e.move);
                mazeWall[nX][nY] = e.changeString(mazeWall[nX][nY], e.opposite.move);
                int compare = mazeGroup[nX][nY];
                for(int i = 0; i < x; i++){
                    for(int j = 0; j < y; j++){
                        if(mazeGroup[i][j] == compare){
                            mazeGroup[i][j] = mazeGroup[oX][oY];
                        }
                    }
                }
                theMaze(rand.nextInt(x),rand.nextInt(y));
            }
        }
    }
    
    //The solver will traverse the whole maze without chageing anything as well as record the cells it has visited(forming a tree structure of parents
    //and children).
    //The tree records are kept within the node arraylist.
    ArrayList<Node> node = new ArrayList<Node>();
    private int nodeCounter = 0;
    public void solveMaze(int sX, int sY){
        if(sX == 0 && sY == 0){
            node.add(new Node(sX,sY,0));
            node.get(0).setOrigin(true);
        }
        int parentIndex = nodeCounter;
        DIR[] direction = DIR.values();
        Collections.shuffle(Arrays.asList(direction));
        for(DIR e : direction){
            int nsX = sX + e.dirX;
            int nsY = sY + e.dirY;
            if(between(nsX,x) && between(nsY,y)){
                if(mazeWall[nsX][nsY].charAt(e.opposite.move) == '0' && mazeVisit[nsX][nsY] == false){   
                    mazeVisit[nsX][nsY] = true;
                    nodeCounter++; node.add(new Node(nsX,nsY,parentIndex));
                    solveMaze(nsX,nsY);
                }
            }
        }
    }
    /*
    After traversing the maze the function below will map as well as keep records of the mapped parents and children making it 
    easier to find a path between two cells
    The concept we used:            (1,1)    (coordinates for example purposes only)
                                    /   \
                                 (2,3)   \
                                 /   \   (3,4)
                             (1,10)  (0,9)
    
                        So lets say we wanted to go from 1,10 to 3,4 we would have to record the path from 1,1 to 0,0
                        and record path 3,4 to 0,0, eventually the two path will have an intersection, which in this 
                        case is 1,1 thus when we combine the path of both until the intersection will be the path.
                        (1,10) to (2,3) to (1,1) plus the path (3,4) to (1,1) but reversed.
    */                                    
    public void path(int startX,int startY,int endX,int endY){
        boolean error = false;
        System.out.println("Enter starting coordinates");
        do{
            try {
                System.out.print("X : ");
                startX = input.nextInt();
                System.out.print(" Y : ");
                startY = input.nextInt();
            }
            catch (InputMismatchException ex){
                System.out.println("Enter only numbers that are within the range of the maze!");
            }
            if(between(startX,x) && between(startY,y)){
                error = true;
            } else System.out.println("Coordinates not within maze"); 
        } while(error == false);
        error = false;
        System.out.println("Enter destination coordinates");
        do{
            try {
                System.out.print("X : ");
                endX = input.nextInt();
                System.out.print(" Y : ");
                endY = input.nextInt();
            }
            catch (InputMismatchException ex){
                System.out.println("Enter only numbers that are within the range of the maze!");
            }
            if(between(endX,x) && between(endY,y)){
                error = true;
            } else System.out.println("Coordinates not within maze");
        }while(error == false);
        
        int startParent = 0, endParent = 0;
        for(Node nodeE : node){
            if(nodeE.x == startX && nodeE.y == startY)
                startParent = nodeE.parent;
            if(nodeE.x == endX && nodeE.y == endY)
                endParent = nodeE.parent;
        }
        
        ArrayList<Integer> indexIntersect = new ArrayList<>();
        ArrayList<Integer> startRecord = new ArrayList<>();
        ArrayList<Integer> endRecord = new ArrayList<>();
        boolean arrayCheck = false;
        while(arrayCheck == false){
            startRecord.add(startParent);
            startParent = node.get(startParent).parent;
            if(node.get(startParent).origin == true)
                arrayCheck = true;
        }
        arrayCheck = false;
        while(arrayCheck == false){
            endRecord.add(endParent);
            endParent = node.get(endParent).parent;
            if(node.get(endParent).origin == true)
                arrayCheck = true;
        }
            for(int start = 0; start < startRecord.size(); start++){
                for(int end = 0; end < endRecord.size(); end++){
                    if(startRecord.get(start).intValue() == endRecord.get(end).intValue()){
                        indexIntersect.add(startRecord.get(start).intValue());
                }
            }
        }
        int print = 0;
        String start = "(" + startX + "," + startY + ")";
        String destination = ")" + endX + "," + endY + "(";
        for(Integer printStart : startRecord){
            start += "(" + node.get(printStart).x + "," + node.get(printStart).y + ")";
            if(printStart.intValue() == indexIntersect.get(0).intValue())
                break;
        }
        for(Integer printEnd : endRecord){
            destination += ")" + node.get(printEnd).x + "," + node.get(printEnd).y + "(";
            if(printEnd.intValue() == indexIntersect.get(0).intValue())
                break;
        }
        
        System.out.println("startrecord :");
        for(Integer print1 : startRecord){
            System.out.println(print1.intValue() + " ");
        }
        System.out.println("endRecord :");
        for(Integer print2 : endRecord){
            System.out.println(print2.intValue() + " ");
        }
        System.out.println("intersect :" + indexIntersect);
        
        StringBuilder reverseString = new StringBuilder(destination);
        reverseString.reverse();
        destination = reverseString.toString();
        
        System.out.println("Shortest Path from (" + startX + "," + startY + ") to (" + endX + "," + endY + ") : " + start + destination);
    }
    
    //To print the maze according the the values of the index of the string values each cell holds, 1 for wall 0 for no wall
    public void printMaze(){
        for(int printX = 0; printX < x; printX++){
            for(int printTop = 0; printTop < y; printTop++){
                System.out.print(mazeWall[printX][printTop].charAt(0) == '0' ? "+   " : "+---");
            }
            System.out.print("+\n");
            for(int printLeft = 0; printLeft < y; printLeft++){
                System.out.print(mazeWall[printX][printLeft].charAt(3) == '0' ? "    " : "|   ");
            }
            System.out.print("|\n");
        }
        for(int printBottom = 0; printBottom < y; printBottom++){
            System.out.print("+---");
        }
        System.out.print("+\n");
    }
    
    public static void main(String[] args) {
        MazegeneratorGroup leMaze = new MazegeneratorGroup(12,12);
        leMaze.printMaze();
        leMaze.solveMaze(0, 0);
        leMaze.path(0, 0, 0, 0);
    }
}

class Node{
    int x;
    int y;
    int parent;
    boolean origin = false;
    
    public Node(int x,int y,int parent){
        this.x = x;
        this.y = y;
        this.parent = parent;
    }

    public void setOrigin(boolean origin) {
        this.origin = origin;
    }
}