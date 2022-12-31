package com.kokesoft.challenges.fall2022;

import static java.lang.String.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

import com.kokesoft.challenges.Dijkstra;

class Player {

    static class Table {
        int width, height, turn = 0;
        int myMatter, oppMatter;
        boolean spawned = false;
        Square [][] table;
        
        List<Robot> lastRobots = new ArrayList<>(); // my last turn robots
        List<Robot> robots = new ArrayList<>(); // my robots
        List<Square> squares = new ArrayList<>(); // my squares
        List<Robot> lastOppRobots = new ArrayList<>(); // opponent last turn robots
        List<Robot> oppRobots = new ArrayList<>(); // opponent current robots
        List<Square> oppSquares = new ArrayList<>(); // opponent squares
        Coords foeCenter, myCenter;
        int recyclers; // my recyclers

        Table(Scanner in) {
            width = in.nextInt();
            height = in.nextInt();
            table = new Square[width][height];
        }
        Table(CsvScanner in) throws IOException {
            width = in.nextInt();
            height = in.nextInt();
            table = new Square[width][height];
            for(int y = 0; y<height; y++) {
            	for(int x = 0; x<width; x++) {
            		table[x][y] = new Square(x,y).withScrapAmount(in.nextInt());
            	}
            }
        }
        void readTurn(Scanner in) {
            lastRobots.clear();
            lastRobots.addAll(robots);
            robots.clear();
            lastOppRobots.clear();
            lastOppRobots.addAll(oppRobots);
            oppRobots.clear();
            turn++;
            recyclers = 0;
            myMatter = in.nextInt();
            oppMatter = in.nextInt();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Square s = Square.read(x, y, in);
                    table[x][y] = s;
                    if (Robot.class.isInstance(s)) {
                        if (s.owner>0)
                            robots.add((Robot)s);
                        else
                            oppRobots.add((Robot)s);
                    } else
                    if (s.owner==0)
                        oppSquares.add(s);
                    else
                    if (s.owner==1)
                        squares.add(s);

                    if (s.owner==1 && s.recycler>0)
                        recyclers++;
                }
            }
            foeCenter = foeCenter();
            myCenter = myCenter();
        }
        public Square get(Coords c) {
            if(c.x<0) c.x = 0;
            if(c.x>=width) c.x = width-1;
            if(c.y<0) c.y = 0;
            if(c.y>=height) c.y = height-1;
            return table[c.x][c.y];
        }
        public Square get(int x, int y) {
            return get(new Coords(x,y));
        }
        public Table set(Square s) {
        	Square s0 = get(s);
        	if (s.scrapAmount<0)
        		s.scrapAmount = s0.scrapAmount;
        	if (s.owner<-1)
        		s.owner = s0.owner;
        	if (s.units<0)
        		s.units = s0.units;
        	if (s.canBuild<0)
        		s.canBuild = s0.canBuild;
        	if (s.canSpawn<0)
        		s.canSpawn = s0.canSpawn;
        	if (s.recycler<0)
        		s.recycler = s0.recycler;
        	if (s.inRangeOfRecycler<0)
        		s.inRangeOfRecycler = s0.inRangeOfRecycler;
        	table[s.x][s.y] = s;
            return this;
        }
        
        private String nodeName(int x, int y) {
        	return String.format("(%d,%d)", x, y);
        }
        public Dijkstra.Node loadNode(int x, int y) {
        	Map<String,Dijkstra.Node> nodes = new HashMap<>(width*height);
        	for(int ty = 0; ty<height; ty++) {
        		for(int tx = 0; tx<width; tx++) {
        			if (table[tx][ty].scrapAmount==0)
        				continue;
        			//System.out.println(nodeName(tx,ty));
    				nodes.put(nodeName(tx,ty), new Dijkstra.Node(nodeName(tx,ty)));
        		}
        	}
        	for(int ty = 0; ty<height; ty++) {
        		for(int tx = 0; tx<width; tx++) {
        			if (table[tx][ty].scrapAmount==0)
        				continue;
        			Dijkstra.Node n0 = nodes.get(nodeName(tx, ty));
        			if (tx>0 && table[tx-1][ty].scrapAmount>0) {
        				n0.connect(nodes.get(nodeName(tx-1, ty)), 1);
        			}
        			if (tx<(width-1) && table[tx+1][ty].scrapAmount>0) {
        				n0.connect(nodes.get(nodeName(tx+1, ty)), 1);
        			}
        			if (ty>0 && table[tx][ty-1].scrapAmount>0) {
        				n0.connect(nodes.get(nodeName(tx, ty-1)), 1);
        			}
        			if (ty<(height-1) && table[tx][ty+1].scrapAmount>0) {
        				n0.connect(nodes.get(nodeName(tx, ty+1)), 1);
        			}
        		}
        	}
        	Dijkstra.Node r = nodes.get(nodeName(x, y));
        	return r;
        }
        public int scrapAmount(int x, int y) {
            return table[x][y].isMine() && table[x][y].inRangeOfRecycler==0?table[x][y].scrapAmount:0
                    + (x>0 && table[x-1][y].isMine() && table[x-1][y].inRangeOfRecycler==0?table[x-1][y].scrapAmount:0)
                    + (x<(width-1) && table[x+1][y].isMine() && table[x+1][y].inRangeOfRecycler==0?table[x+1][y].scrapAmount:0)
                    + (y>0 && table[x][y-1].isMine() && table[x][y-1].inRangeOfRecycler==0?table[x][y-1].scrapAmount:0)
                    + (y<(height-1) && table[x][y+1].isMine() && table[x][y+1].inRangeOfRecycler==0?table[x][y+1].scrapAmount:0)
                    ;
        }
        public Coords foeCenter() {
            return center(0);
        }
        public Coords myCenter() {
            return center(1);
        }
        public Coords unconqueredCenter() {
            return center(-1);
        }
        public Coords center(int owner) {
            Coords c = new Coords(0,0);
            int n = 0;
            for(int x = 0; x<width; x++) {
                for(int y = 0; y<height; y++) {
                    if (table[x][y].owner==owner) {
                        c.x += x;
                        c.y += y;
                        n++;
                    }
                }
            }
            if (c.x==0 && c.y==0)
                return new Coords(width/2, height/2);
            return new Coords((int)Math.round(Math.floor(1.0*c.x/n)), (int)Math.round(Math.floor(1.0*c.y/n)));
        }
    }

    static class Coords {
        int x, y;
        Coords(int x, int y) {
            this.x = x;
            this.y = y;
        }
        Coords(Coords c) {
            this.x = c.x;
            this.y = c.y;
        }
        int distance(Coords c) {
            if (c==null)
                return Integer.MAX_VALUE;
            return (x-c.x)*(x-c.x)+(y-c.y)*(y-c.y);
        }
        public boolean equals(Object obj) {
            if (obj==null)
                return false;
            if (!Coords.class.isInstance(obj))
                return false;
            return x==((Coords)obj).x && y==((Coords)obj).y;
        }
        public String toString() {
            return String.format("(%d,%d)", x, y);
        }
    }

    static class Square extends Coords {
        int scrapAmount;
        int owner;
        int units;
        int recycler, canBuild, canSpawn;
        int inRangeOfRecycler;
        static Square read(int x, int y, Scanner in) {
            int scrapAmount = in.nextInt();
            int owner = in.nextInt(); // 1 = me, 0 = foe, -1 = neutral
            int units = in.nextInt();
            int recycler = in.nextInt();
            int canBuild = in.nextInt();
            int canSpawn = in.nextInt();
            int inRangeOfRecycler = in.nextInt();
            if (units>0)
                return new Robot(x,y,scrapAmount,owner,units,recycler,canBuild,canSpawn,inRangeOfRecycler);
            return new Square(x,y,scrapAmount,owner,units,recycler,canBuild,canSpawn,inRangeOfRecycler);
        }
        Square(int x, int y, int scrapAmount, int owner, int units, int recycler, int canBuild, int canSpawn,
                int inRangeOfRecycler) {
            super(x, y);
            this.scrapAmount = scrapAmount;
            this.owner = owner; // 1 = me, 0 = foe, -1 = neutral
            this.units = units;
            this.recycler = recycler;
            this.canBuild = canBuild;
            this.canSpawn = canSpawn;
            this.inRangeOfRecycler = inRangeOfRecycler;
        }
        Square(int x, int y) { super(x,y); }
        public Square withScrapAmount(int scrapAmount) {
        	this.scrapAmount = scrapAmount;
        	return this;
        }
        public boolean isMine() { return owner==1; }
        public boolean isConquerable() { return owner<=0 && scrapAmount>0; }
        public boolean isUnconquered() { return owner==-1 && scrapAmount>0; }
        public boolean isGrass() { return scrapAmount==0; }
        public Coords moveLeft(StringBuilder sb) {
            return moveLeft(1, sb);
        }
        public Coords moveLeft(int units, StringBuilder sb) {
            return move(new Coords(x-1,y), units, sb);
        }
        public Coords moveRight(StringBuilder sb) {
            return moveRight(1, sb);
        }
        public Coords moveRight(int units, StringBuilder sb) {
            return move(new Coords(x+1,y), units, sb);
        }
        public Coords moveUp(StringBuilder sb) {
            return moveUp(1, sb);
        }
        public Coords moveUp(int units, StringBuilder sb) {
            return move(new Coords(x,y-1), units, sb);
        }
        public Coords moveDown(StringBuilder sb) {
            return moveDown(1, sb);
        }
        public Coords moveDown(int units, StringBuilder sb) {
            return move(new Coords(x,y+1), units, sb);
        }
        public Coords move(Coords c, StringBuilder sb) {
            return move(c, 1, sb);
        }
        public Coords move(Coords c, int units, StringBuilder sb) {
            if (c==null)
                return null;
            sb.append(String.format("MOVE %d %d %d %d %d;", units, x, y, c.x, c.y));
            return c;
        }
        public Coords up() { return new Coords(x,y-1); }
        public Coords right() { return new Coords(x+1,y); }
        public Coords down() { return new Coords(x,y+1); }
        public Coords left() { return new Coords(x-1,y); }
    }

    public static class Robot extends Square {
        enum Movement { UP, DOWN, LEFT, RIGHT };
        List<Movement> movements = new ArrayList<>();
        List<Square> previous = new ArrayList<>();
        Coords dest = null;
        Robot(int x, int y, int owner, int units) {
            super(x,y,-1,owner,units,-1,-1,-1,-1);
        }
        Robot(int x, int y, int scrapAmount, int owner, int units, int recycler, int canBuild, int canSpawn,
                int inRangeOfRecycler) {
            super(x, y,scrapAmount,owner,units,recycler,canBuild,canSpawn,inRangeOfRecycler);
        }
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        Table table = new Table(in);
        // game loop
        while (true) {
            table.readTurn(in);
            System.out.println("WAIT;");
        }
    }
}