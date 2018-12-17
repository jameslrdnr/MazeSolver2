import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Stack;

public class MazeFileParser {

    public static void main(String[] args){
        //ImageParser imgParse = new ImageParser("TestPic.gif");
        /*for(String s : args) {
            RunnableMain runner = new RunnableMain(s);
            runner.callSolver("AStar");
        }*/
        MazeGenerator mazeGen = new MazeGenerator(10, 10);
    }

}

class RunnableMain{

    ArrayList<ArrayList<Character>> wallMap;
    String fileLoc;

    public RunnableMain(String loc){
        fileLoc = loc;

        init();
    }

    private void init(){
        wallMap = new ImageParser(fileLoc).getWallMap();
    }

    public void callSolver(String type){

        File outputfile = new File(String.format("out/%s - Output",  type + " : " + fileLoc));

        switch (type){
            case "RightSolve":
                //call and create the solver
                RightSolver solverR = new RightSolver(wallMap);
                //create the output image of the program
                try {
                    ImageIO.write(createFinalWallMapImg(solverR.getWallMap(), solverR.getNodeStack()), "gif", outputfile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                printWallMap(solverR.getWallMap());
                break;
            case "AStar":
                //call and create the solver
                AStarSolver solverStar = new AStarSolver(wallMap);
                //create the output image of the program
                try {
                    ImageIO.write(createFinalWallMapImg(solverStar.getWallMap(), AStarToStackConverter(solverStar.getClosedList())), "gif", outputfile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    //testing
    private void printWallMap(ArrayList<ArrayList<Character>> wMap){
        for(int y = 0; y < wMap.size(); y++){
            for(int x = 0; x < wMap.size(); x++){
                System.out.print(wMap.get(x).get(y));
            }
            System.out.println();
        }
    }

    //create a new image file and save it (have color gradient) with right solve
    private BufferedImage createFinalWallMapImg(ArrayList<ArrayList<Character>> wMap, Stack<Node> stack){
        int currentPos = 1;
        BufferedImage finalImg = new BufferedImage(wMap.size(), wMap.get(0).size(), BufferedImage.TYPE_INT_ARGB);

        double delta = (255.0 / stack.size());
        int colorHolder = 0;

        for(int y = 0; y < wMap.size(); y++){
            for(int x = 0; x < wMap.size(); x++){
                if(wMap.get(x).get(y) == '@') {
                    Node curr = findNodeInList(stack, x, y);
                    //create the color gradients
                    colorHolder = (255<<24);
                    colorHolder = colorHolder | ((int)(delta * curr.getNum())<<16);
                    colorHolder = colorHolder | ((int)(255 - (delta * curr.getNum())));
                    finalImg.setRGB(x, y, colorHolder);
                }
                else if(wMap.get(x).get(y) == '%' || wMap.get(x).get(y) == ' '){
                    //adds a white open space
                    finalImg.setRGB(x, y, ((255<<24) + 16777215));
                }
                else{
                    //adds a black space for everything else
                    finalImg.setRGB(x, y, (255<<24));
                }
            }
        }

        return finalImg;
    }

    //converter from AStar solver
    private Stack<Node> AStarToStackConverter(ArrayList<Node> input){
        Stack<Node> tempS = new Stack<Node>();
        Node tempN = input.get(input.size() - 1);
        while(tempN != null){
            tempS.add(0, tempN);

            tempN = tempN.getParent();
        }
        return tempS;
    }

    private Node findNodeInList(Stack<Node> s, int xP, int yP){
        for (Node n : s){
            if (n.getxPos() == xP && n.getyPos() == yP)
                return n;
        }
        return null;
    }

}

//HAVE EVERY POINT BE A NODE THAT HAS A SAVED CHAR, REMAKE WALL MAP TO BE WITH NODES AND HAVE NULL NODES ACT AS SPACES, OTHER NODES WILL BE WALLS

class ImageParser{

    String fileLoc;
    Raster img;
    ArrayList<ArrayList<Character>> wallMap = new ArrayList<ArrayList<Character>>();

    public ImageParser(String loc){
        fileLoc = loc;

        init();
    }

    private void init(){
        //open the image
        URL url = getClass().getResource(fileLoc);
        try{
            img = ImageIO.read(new File(url.getPath())).getData();
        }catch (IOException e){
            e.printStackTrace();
        }

        //create the wallMap
        for (int x = 2; x < img.getWidth() - 2; x++){
            wallMap.add(new ArrayList<Character>());
            for (int y = 2; y < img.getHeight() - 2; y++){
                if(img.getSample(x, y, 0) == 0){
                    wallMap.get(x - 2).add('#');
                }else{
                    //sets the entry and exit to the '@' symbol
                    if(x == 2)
                        wallMap.get(x - 2).add('@');
                    else
                        wallMap.get(x - 2).add(' ');
                }
            }
        }

        printWallMap(wallMap);

    }

    public ArrayList<ArrayList<Character>> getWallMap() {
        return wallMap;
    }

    //testing
    private void printWallMap(ArrayList<ArrayList<Character>> wMap){
        for(int y = 0; y < wMap.size(); y++){
            for(int x = 0; x < wMap.size(); x++){
                if(wMap.get(x).get(y) == '%')
                    System.out.print(' ');
                else
                    System.out.print(wMap.get(x).get(y));
            }
            System.out.println();
        }
    }

}

class Node{

    private char dir;
    private int xPos, yPos, num;
    private double cost, pathCost, estFinishCost;
    private Node parent;

    public Node(int xPos, int yPos, char dir, int num){
        this.xPos = xPos;
        this.yPos = yPos;
        this.dir = dir;
        this.num = num;
    }
    public Node(Node p, int num, int xPos, int yPos, double pathCost, double estFinishCost){
        this.parent = p;
        this.num = num;
        this.xPos = xPos;
        this.yPos = yPos;
        this.pathCost = pathCost;
        this.estFinishCost = estFinishCost;
        this.cost = this.pathCost + this.estFinishCost;
    }

    public char getDir() {
        return dir;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public int getNum() {
        return num;
    }

    public double getCost() {
        return cost;
    }

    public Node getParent() {
        return parent;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getPathCost() {
        return pathCost;
    }

    public void setPathCost(double pathCost) {
        this.pathCost = pathCost;
    }

    public double getEstFinishCost() {
        return estFinishCost;
    }

    public void setEstFinishCost(double estFinishCost) {
        this.estFinishCost = estFinishCost;
    }
}

//SOLVERS
//-----------------------------

class RightSolver{

    //vars
    ArrayList<ArrayList<Character>> wallMap;
    Stack<Node> nodeStack = new Stack<Node>();
    int x, y;

    public RightSolver(ArrayList<ArrayList<Character>> map){
        wallMap = map;

        init();
    }

    public void init(){
        //start top left of map not on walls
        x = 0;
        y = 1;

        mainRunnable();
    }

    public void mainRunnable(){
        //add the initial start point
        nodeStack.push(new Node(x, y, 'e', 1));
        Node currentNode = nodeStack.peek();
        //while not in bottom right of map
        while(currentNode.getxPos() != wallMap.size() - 1){
            //this actually makes tones of sense, can always assign the correct node even if in dead end, just don't add anything to stack
            //add a current pos
            //check the directions around current pos
            switch (currentNode.getDir()){
                case 'e':
                    if (checkS(currentNode)){
                        nodeStack.push(new Node(currentNode.getxPos(), currentNode.getyPos() + 1, 's', nodeStack.size()));
                    }
                    else if (checkE(currentNode)){
                        nodeStack.push(new Node(currentNode.getxPos() + 1, currentNode.getyPos(), 'e', nodeStack.size()));
                    }
                    else if (checkN(currentNode)){
                        nodeStack.push(new Node(currentNode.getxPos(), currentNode.getyPos() - 1, 'n', nodeStack.size()));
                    }
                    break;
                case 's':
                    if (checkW(currentNode)){
                        nodeStack.push(new Node(currentNode.getxPos() - 1, currentNode.getyPos(), 'w', nodeStack.size()));
                    }
                    else if (checkS(currentNode)){
                        nodeStack.push(new Node(currentNode.getxPos(), currentNode.getyPos() + 1, 's', nodeStack.size()));
                    }
                    else if (checkE(currentNode)){
                        nodeStack.push(new Node(currentNode.getxPos() + 1, currentNode.getyPos(), 'e', nodeStack.size()));
                    }
                    break;
                case 'w':
                    if (checkN(currentNode)){
                       nodeStack.push(new Node(currentNode.getxPos(), currentNode.getyPos() - 1, 'n', nodeStack.size()));
                    }
                    if (checkW(currentNode)){
                        nodeStack.push(new Node(currentNode.getxPos() - 1, currentNode.getyPos(), 'w', nodeStack.size()));
                    }
                    else if (checkS(currentNode)){
                        nodeStack.push(new Node(currentNode.getxPos(), currentNode.getyPos() + 1, 's', nodeStack.size()));
                    }
                    break;
                case 'n':
                    if (checkE(currentNode)){
                        nodeStack.push(new Node(currentNode.getxPos() + 1, currentNode.getyPos(), 'e', nodeStack.size()));
                    }
                    else if (checkN(currentNode)){
                        nodeStack.push(new Node(currentNode.getxPos(), currentNode.getyPos() - 1, 'n', nodeStack.size()));
                    }
                    else if (checkW(currentNode)){
                        nodeStack.push(new Node(currentNode.getxPos() - 1, currentNode.getyPos(), 'w', nodeStack.size()));
                    }
                    break;
            }

            //check to see if you beat the maze
            //make currentnode the next ordered node
            if (currentNode != nodeStack.peek())
                currentNode = nodeStack.peek();
            else{
                wallMap.get(currentNode.getxPos()).set(currentNode.getyPos(), '%');
                if(nodeStack.size() != 1)
                    nodeStack.pop();
                currentNode = nodeStack.peek();
            }

            //edit wallmap
            wallMap.get(currentNode.getxPos()).set(currentNode.getyPos(), '@');
        }
        //set the final point into a node and make it an @
    }

    private boolean checkE(Node n){
        if (wallMap.get(n.getxPos() + 1).get(n.getyPos()) == ' ')
            return true;
        return false;
    }
    private boolean checkS(Node n){
        if (wallMap.get(n.getxPos()).get(n.getyPos() + 1) == ' ')
            return true;
        return false;
    }
    private boolean checkW(Node n){
        if (wallMap.get(n.getxPos() - 1).get(n.getyPos()) == ' ')
            return true;
        return false;
    }
    private boolean checkN(Node n){
        if (wallMap.get(n.getxPos()).get(n.getyPos() - 1) == ' ')
            return true;
        return false;
    }

    public ArrayList<ArrayList<Character>> getWallMap(){
        return wallMap;
    }

    public Stack<Node> getNodeStack(){
        return nodeStack;
    }
}

class AStarSolver{

    //vars
    ArrayList<ArrayList<Character>> wallMap;
    ArrayList<Node> openList;
    ArrayList<Node> closedList;
    int endX, endY;
    //start pos of first node
    int x = 1, y = 1;
    boolean finished;

    public AStarSolver(ArrayList<ArrayList<Character>> map){
        wallMap = map;

        init();
    }

    private void init(){

        endX = wallMap.size() - 1;
        endY = wallMap.get(0).size() - 1;
        finished = false;
        openList = new ArrayList<Node>();
        closedList = new ArrayList<Node>();
        closedList.add(new Node(null, 0, 0, 1, 0, Math.sqrt((wallMap.size() ^ 2) + (wallMap.get(0).size() ^ 2))));
        openList.add(new Node(closedList.get(0), 1, x, y, 0, Math.sqrt((wallMap.size() ^ 2) + (wallMap.get(0).size() ^ 2))));
        wallMap.get(0).set(1, '#');
        mainRunnable();
    }

    private void mainRunnable(){
        boolean shouldSkip = false;
        Node currentNode = openList.get(0);

        while(openList.size() != 0 && finished == false){
            //get the node with the lowest cost and set it as the current node
            for (Node n : openList){
                if(currentNode == null)
                    currentNode = n;
                else if (n.getCost() < currentNode.getCost()){
                    currentNode = n;
                }
            }

            wallMap.get(currentNode.getxPos()).set(currentNode.getyPos(), '%');

            //create the neighboring nodes and add them to the open list
            for (x = currentNode.getxPos() - 1; x <= currentNode.getxPos() + 1; x++){
                for (y = currentNode.getyPos() - 1; y <= currentNode.getyPos() + 1; y++){

//                    System.out.print(String.format("\n(%s, %s) Node : (%s, %s))", x, y, currentNode.getxPos(), currentNode.getyPos()));
//                    System.out.print(String.format(""+ Math.sqrt(Math.pow((currentNode.getxPos() - x),2) + Math.pow((currentNode.getyPos() - y),2))));
                    //make sure the node isnt a wall, then continue if its not
                    if(wallMap.get(x).get(y) == '#' /*|| wallMap.get(x).get(y) == '%'*/) {
//                        System.out.print(" - Skipped for wall");
                        continue;
                    }
                    //dont create nodes on the corners
                    else if(Math.sqrt(Math.pow((currentNode.getxPos() - x),2) + Math.pow((currentNode.getyPos() - y),2)) != 1.0){
//                        System.out.print(" - Skipped for corner");
                        continue;
                    }
                    //dont create a mimic node
                    else{
                        Node tempN;
                        tempN = new Node(currentNode, currentNode.getNum() + 1, x, y, Math.sqrt(Math.pow((currentNode.getxPos() - x),2) + Math.pow((currentNode.getyPos() - y),2)),
                                Math.sqrt(Math.pow((endX - currentNode.getxPos()), 2) + Math.pow((endY - currentNode.getyPos()),2)));
                        if (tempN.getxPos() == wallMap.size() - 1 && tempN.getyPos() == wallMap.get(0).size() - 2){
                            finished = true;
                        }
                        for (Node n : openList) {
                            //if a new succesor node has same x and y and less cost add to list as option
                            if (tempN.getxPos() == n.getxPos() && tempN.getyPos() == n.getyPos()) {
                                //checks to see if the succesor has a bigger cost
                                if(tempN.getCost() >= n.getCost()) {
                                    shouldSkip = true;
//                                    System.out.print(" - Skipped for openList");
                                }
                            }
                        }

                        if(shouldSkip != true)
                            for(Node n : closedList){
                                //if a new succesor node has same x and y and less cost add to list as option
                                if (tempN.getxPos() == n.getxPos() && tempN.getyPos() == n.getyPos()) {
                                    //checks to see if the succesor has a bigger cost
                                    if(tempN.getCost() >= n.getCost()) {
                                        shouldSkip = true;
//                                        System.out.print(" - Skipped for closedList");
                                    }
                                }
                            }

                        if(shouldSkip != true)
                            openList.add(tempN);

                        shouldSkip = false;
                    }
                }
            }
            closedList.add(currentNode);
            openList.remove(currentNode);
            //add final node if you reached that point to closed list as well
            if(finished){
                closedList.add(new Node(currentNode, currentNode.getNum() + 1, currentNode.getxPos() + 1, currentNode.getyPos(), Math.sqrt(Math.pow((currentNode.getxPos() + 1),2) + Math.pow((currentNode.getyPos()),2)),
                        Math.sqrt(Math.pow((endX - currentNode.getxPos()), 2) + Math.pow((endY - currentNode.getyPos()),2))));
            }
            currentNode = null;
//            System.out.print("\nMade it to remove : " + openList.size()+"\n");
        }

        //set final solver pathway to @ symbols
        Node tempN = closedList.get(closedList.size() - 1);
        //first node returns null as parent
        while(tempN != null){
            wallMap.get(tempN.getxPos()).set(tempN.getyPos(), '@');
            tempN = tempN.getParent();
        }
        printWallMap(getWallMap());
    }

    public ArrayList<ArrayList<Character>> getWallMap(){
        return wallMap;
    }

    public Stack<Node> getNodeStack(){
        Stack<Node> temp = new Stack<Node>();
        for (Node n : closedList){
            temp.add(n);
        }
        return temp;
    }

    private void printWallMap(ArrayList<ArrayList<Character>> wMap){
        for(int y = 0; y < wMap.size(); y++){
            for(int x = 0; x < wMap.size(); x++){
                System.out.print(wMap.get(x).get(y));
            }
            System.out.println();
        }
    }

    public ArrayList<Node> getClosedList() {
        return closedList;
    }
}