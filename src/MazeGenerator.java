import java.util.ArrayList;

public class MazeGenerator {


    boolean[][] mazeOutlineMap;

    public MazeGenerator(int length, int height){

        mazeOutlineMap = createMazeWilsonAlgorithm(length, height, 0);

    }

    private boolean[][] createMazeWilsonAlgorithm(int length, int width, int removedPieces){

        ArrayList<NodeMazeGen> currentPath = new ArrayList<NodeMazeGen>();
        boolean currentPathFinished = false, mazeComplete = false;
        //false means there is a wall present
        boolean[][] map = new boolean[length][width];

        NodeMazeGen startNode = getRandomNode(map.length, map[0].length);
        map[startNode.getX()][startNode.getY()] = true;

        while(mazeComplete != true){

            NodeMazeGen currentNode = getRandomUnmappedNode(map);

            while(currentPathFinished != true){

            }

        }

        return map;

    }

    private NodeMazeGen generateNextNode(boolean[][] map, NodeMazeGen currentNode){

        char[] options = new char[4];
        options[0] = 'u'; options[1] = 'd'; options[2] = 'l'; options[3] = 'r';

        switch(options[(int)Math.random() * 4]){
            case 'u':
                break;
            case 'd':
                break;
            case 'l':
                break;
            case 'r':
                break;
        }

    }

    private NodeMazeGen getRandomNode(int length, int width){
        return new NodeMazeGen( (int)(Math.random() * length), (int)(Math.random() * width));
    }

    private NodeMazeGen getRandomUnmappedNode(boolean[][] map){

        NodeMazeGen tempNode = getRandomNode(map.length, map[0].length);

        while(!map[tempNode.getX()][tempNode.getY()]){
            tempNode = getRandomNode(map.length, map[0].length);
        }

        return tempNode;

    }

    public void printBooleanArrayMap(boolean[][] map){
        for (boolean[] row : map) {
            for(boolean col : row){
                System.out.print(col + ",");
            }
            System.out.println();
        }
    }


    private boolean[][] addOpenSpaceBorderToMap(boolean[][] map){
        //note this is not actually needed until saving the final image, wait till then to avoid confusion
        //note: this does not add the entrance or the exit, neither can be determined until the maze is generated
        for (int x = 0; x < map.length; x++){
            for ( int y = 0; y < map[0].length; y++){
                if((x < 2 || x > map.length - 3) || (y < 2 || y > map[0].length - 3)){
                    map[x][y] = true;
                }
            }
        }

        return map;

    }

}

class NodeMazeGen {

    private boolean finished = false;
    private int x, y;
    private char dir;

    public NodeMazeGen(int x, int y){
        this.x = x;
        this.y = y;
    }

    public NodeMazeGen(int x, int y, char dir){
        this.x = x;
        this.y = y;
        this.dir = dir;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public char getDir() {
        return dir;
    }

    public void setDir(char dir) {
        this.dir = dir;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

}
