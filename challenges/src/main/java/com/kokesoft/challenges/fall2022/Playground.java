package com.kokesoft.challenges.fall2022;

import static java.lang.String.format;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Playground {
	final int BLUE = 1;
	final int RED = 2;
	
	int width, height, turn = 0;
	int matterBlue = 10, matterRed = 10;
	int turnOfPlayer = BLUE;
	Square table[][];
	
	static class Square {
		int scrapAmount;
		int owner = 0;
		int units;
		transient int foeUnits;
		int recycler;
		int canBuild;
		int canSpawn;
		int inRangeOfRecycler;
	}
	
	@SuppressWarnings("serial")
	public static class GameException extends Exception {
		public GameException() { super(); }
		public GameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}
		public GameException(String message, Throwable cause) {
			super(message, cause);
		}
		public GameException(String message) {
			super(message);
		}
		public GameException(Throwable cause) {
			super(cause);
		}
	}
	@SuppressWarnings("serial")
	public static class CantBuildAtGrassException extends GameException {
		public CantBuildAtGrassException(int x, int y) {
			super(format("can't build at %d,%d because it's grass", x, y));
		}
	}
	
	static abstract class Command {
		Playground p;
		int turnOfPlayer;
		public Command(Playground p, int turnOfPlayer) {
			this.p = p;
			this.turnOfPlayer = turnOfPlayer;
		}
		public abstract boolean execute() throws GameException;
		public void basicTests(int x, int y, String msg) throws GameException {
	    	if (x<0 || y<0 || x>=p.width || y>=p.height)
	    		throw new GameException(format("%s %d,%d because it is out of limits",msg,x,y));
			if (p.table[x][y].owner!=turnOfPlayer)
	    		throw new GameException(format("%s %d,%d because is not yours",msg,x,y));
		}
	}
	
	static class Build extends Command {
		int x, y;
		public Build(Playground p, int turnOfPlayer, int x, int y) {
			super(p, turnOfPlayer);
			this.x = x;
			this.y = y;
		}

		public boolean execute() throws GameException {
			basicTests(x, y, "can't build at");
			if (p.table[x][y].scrapAmount==0)
				throw new CantBuildAtGrassException(x, y);
			return true;
		}
	}

	static class Spawn extends Command {
		int units, x, y;
		public Spawn(Playground p, int turnOfPlayer, int units, int x, int y) {
			super(p, turnOfPlayer);
			this.units = units;
			this.x = x;
			this.y = y;
		}
		public boolean execute() throws GameException {
			basicTests(x, y, "can't spawn at");
			return true;
		}
	}
	
	static class Move extends  Command {
		int units, x0, y0, x1, y1;
		public Move(Playground p, int turnOfPlayer, int units, int x0, int y0, int x1, int y1) {
			super(p, turnOfPlayer);
			this.units = units;
			this.x0 = x0;
			this.y0 = y0;
			this.x1 = x1;
			this.y1 = y1;
		}

		public boolean execute() throws GameException {
			basicTests(x0, y0, "can't move from");
	    	if (x1<0 || y1<0 || x1>=p.width || y1>=p.height)
	    		throw new GameException(format("can't move to to %d,%d because it is out of limits",x1,y1));
	    	if (p.table[x0][y0].units<units) {
	    		System.err.println(format("can't move %d units at %d,%d because there are not enough: all %d units will be moved",units,x0,y0,p.table[x0][y0].units));
	    		units = p.table[x0][y0].units;
	    	}
			if (x0==x1 && y0==y1) {
	    		System.err.println(format("moved %d,%d to the same place",x0,y0));
				return false;
			}
	    	int dx = x1-x0, dy = y1-y0;
	    	if (Tools.abs(dx)>Tools.abs(dy)) {
	    		x1 = x0+dx/Tools.abs(dx);
	    		y1 = y0;
	    	} else {
	    		x1 = x0;
	    		y1 = y0+dy/Tools.abs(dy);
	    	}
	    	if (p.table[x1][y1].scrapAmount==0) {
	        	if (Tools.abs(dx)>Tools.abs(dy)) {
	        		x1 = x0;
	        		y1 = y0+dy/Tools.abs(dy);
	        	} else {
	        		x1 = x0+dx/Tools.abs(dx);
	        		y1 = y0;
	        	}
	    	}
	    	if (p.table[x1][y1].scrapAmount==0) {
	    		return false;
	    	}
	    	p.table[x0][y0].units -= units;
	    	if (p.table[x1][y1].owner>=0 && p.table[x1][y1].owner!=turnOfPlayer) {
	    		p.table[x1][y1].foeUnits += units;
	    	} else {
	    		p.table[x1][y1].units += units;
	    	}
			return true;
		}
	}
	
	public Playground(int width, int height) {
		this.width = width;
		this.height = height;
		table = new Square[width][height];
	}
	
    public Playground(CsvScanner in) throws IOException {
        width = in.nextInt();
        height = in.nextInt();
        table = new Square[width][height];
        for(int y = 0; y<height; y++) {
        	for(int x = 0; x<width; x++) {
        		table[x][y] = new Square();
        		table[x][y].scrapAmount = in.nextInt();
        	}
        }
    }

    public InputStream serialize() {
    	try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
    		serialize(baos);
    		return new ByteArrayInputStream(baos.toByteArray());
    	} catch(IOException e) {
    		throw new RuntimeException(e);
    	}
    }

    void serialize(OutputStream os) {
    	try(PrintStream ps = new PrintStream(os)) {
    		if (turn==0) {
    			ps.print(width); ps.print(' ');
    			ps.print(height); ps.print(' ');
    		}
    		int otherPlayer = 0;
    		switch(turnOfPlayer) {
    		case BLUE:
    			ps.print(matterBlue); ps.print(' '); ps.print(matterRed);
    			otherPlayer = RED;
    			break;
    		case RED:
    			ps.print(matterRed); ps.print(' '); ps.print(matterBlue);
    			otherPlayer = BLUE;
    			break;
    		}
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                	ps.print(' '); ps.print(table[x][y].scrapAmount);
                	ps.print(' '); ps.print(table[x][y].owner==turnOfPlayer?1:table[x][y].owner==otherPlayer?0:-1);
                	ps.print(' '); ps.print(table[x][y].units);
                	ps.print(' '); ps.print(table[x][y].recycler);
                	ps.print(' '); ps.print(table[x][y].canBuild);
                	ps.print(' '); ps.print(table[x][y].canSpawn);
                	ps.print(' '); ps.print(table[x][y].inRangeOfRecycler);
                }
            }
    	}
    }
    
    public void set(int x, int y, int scrapAmount) {
    	if (x<0 || y<0 || x>=width || y>=height)
    		throw new RuntimeException(format("%d,%d is out of borders", x, y));
    	table[x][y].scrapAmount = scrapAmount;
    }
    
    public void set(String ... strings) {
    	if (strings==null || strings.length!=height)
    		throw new RuntimeException(format("expected %d strings", height));
    	for(int y = 0; y<height; y++) {
    		if (strings[y]==null || !strings[y].matches(format("\\d{%d}", width)))
        		throw new RuntimeException(format("expected %d strings of %d digits", height, width));
        	for(int x = 0; x<width; x++) {
        		if (table[x][y]==null)
        			table[x][y] = new Square();
        		table[x][y].scrapAmount = strings[y].charAt(x)-'0';
        	}
    	}
    	
    }
    
    public void adquireBlue(int x, int y) {
    	table[x][y].owner = BLUE;
    }
    public void createBlueRobot(int x, int y) {
    	table[x][y].owner = BLUE;
    	table[x][y].units++;
    }

    public void adquireRed(int x, int y) {
    	table[x][y].owner = RED;
    }
    public void createRedRobot(int x, int y) {
    	table[x][y].owner = RED;
    	table[x][y].units++;
    }
    
    int abs(int x) {
    	return x<0?-x:x;
    }
    
    public void play(String commands) {
    	play(commands, null);
    }
    public void play(String commands, List<GameException> e) {
    	try(ByteArrayInputStream is = new ByteArrayInputStream(commands.getBytes())) {
    		play(is, e);
    	} catch(IOException te) {
    		throw new RuntimeException(te);
    	}
    }
    public void play(InputStream is) {
    	play(is, null);
    }
    public void play(InputStream is, List<GameException> e) {
    	List<Build> builds = new ArrayList<>();
    	List<Spawn> spawns = new ArrayList<>();
    	List<Move> moves = new ArrayList<>();
    	try(Scanner sc = new Scanner(is)) {
    		String next = null;
    		while(true) {
	    		if (next==null) {
	    			try {
	    				next = sc.next();
	    			} catch(NoSuchElementException te) {
	    				break;
	    			}
	    		}
	    		if ("WAIT".equalsIgnoreCase(next)
	    				|| "WAIT;".equalsIgnoreCase(next)) {
	    			next = null;
	    			break;
	    		}
	    		if ("BUILD".equalsIgnoreCase(next)) {
	    			next = null;
	    			int atx = sc.nextInt();
	    			String saty = sc.next();
	    			int aty;
	    			if (saty.contains(";")) {
	    				String [] tp = saty.split(";");
	    				aty = Integer.parseInt(tp[0]);
	    				if (tp.length>1)
	    					next = tp[1];
	    			} else
	    				aty = Integer.parseInt(saty);
	    			builds.add(new Build(this, turnOfPlayer, atx, aty));
	    		}
	    		if ("SPAWN".equalsIgnoreCase(next)) {
	    			next = null;
	    			int units = sc.nextInt();
	    			int atx = sc.nextInt();
	    			String saty = sc.next();
	    			int aty;
	    			if (saty.contains(";")) {
	    				String [] tp = saty.split(";");
	    				aty = Integer.parseInt(tp[0]);
	    				if (tp.length>1)
	    					next = tp[1];
	    			} else
	    				aty = Integer.parseInt(saty);
	    			spawns.add(new Spawn(this, turnOfPlayer, units, atx, aty));
	    		}
	    		if ("MOVE".equalsIgnoreCase(next)) {
	    			next = null;
	    			int units = sc.nextInt();
	    			int x0 = sc.nextInt();
	    			int y0 = sc.nextInt();
	    			int x1 = sc.nextInt();
	    			String sy1 = sc.next();
	    			int y1;
	    			if (sy1.contains(";")) {
	    				String [] tp = sy1.split(";");
	    				y1 = Integer.parseInt(tp[0]);
	    				if (tp.length>1)
	    					next = tp[1];
	    			} else
	    				y1 = Integer.parseInt(sy1);
	    			moves.add(new Move(this, turnOfPlayer, units, x0, y0, x1, y1));
	    		}
    		}
    	}
    	for(Build build: builds) {
    		try {
    			build.execute();
    		} catch(GameException te) {
    			if (e!=null)
    				e.add(te);
    			else
    				System.err.println(te);
    		}
    	}
    	for(Spawn spawn: spawns) {
    		try {
    			spawn.execute();
    		} catch(GameException te) {
    			if (e!=null)
    				e.add(te);
    			else
    				System.err.println(te);
    		}
    	}
    	for(Move move: moves) {
    		try {
    			move.execute();
    		} catch(GameException te) {
    			if (e!=null)
    				e.add(te);
    			else
    				System.err.println(te);
    		}
    	}
		for(int x = 0; x<width; x++) {
			for(int y = 0; y<height; y++) {
				table[x][y].units -= table[x][y].foeUnits;
				if (table[x][y].units<0) {
					table[x][y].owner = turnOfPlayer;
					table[x][y].units = -table[x][y].units;
				}
				table[x][y].foeUnits = 0;
			}
		}
		
    	matterBlue += 10;
    	matterRed += 10;
    	if (turnOfPlayer==RED) {
    		turnOfPlayer = BLUE;
    		turn++;
    	} else {
    		turnOfPlayer = RED;
    	}
    }
    
	
}
