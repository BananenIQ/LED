package main;

import java.io.PrintWriter;
import java.util.ArrayList;

import processing.core.*;

public class LED extends PApplet {

	final boolean fullscreen = false;
	final boolean debug = true;
	final String imgfile = "Bananen.jpg";
	final String posfile = "LED.txt";
	int factor = 5;
	int size_w = 300 * factor;
	int size_h = 300 * factor;
	int distance = 5 * factor;
	int diameter = 5 * factor;
	final int Delay = 50;

	boolean toggleColor;
	boolean collide;
	boolean blink;
	boolean imgload;
	int Case = 0;
	String color;
	PrintWriter output;
	PImage img;
	String[] input;

	ArrayList<PVector> pos = new ArrayList<PVector>();
	ArrayList<Integer> group = new ArrayList<Integer>();

	public static void main(String[] args) {
		PApplet.main("main.LED");
	}

	public void settings() {
		if (fullscreen) {
			fullScreen();
		} else {
			size(size_w, size_h);
		}
		smooth(3);
	}

	public void setup() {
		pos.clear();
		group.clear();
		input = loadStrings(posfile);
		img = loadImage(imgfile);
		img.resize(width, height);
	}

	public void draw() {
		if (blink) {
			if (frameCount % Delay == 0) {
				toggleColor = !toggleColor;
				for (int i = 0; i < pos.size(); i++) {
					createLED(pos.get(i), group.get(i), toggleColor);
				}
			}
		}
		if (imgload) {
			background(img);
		} else {
			background(255);
		}
		for (int i = 0; i < pos.size(); i++) {
			createLED(pos.get(i), group.get(i), toggleColor);
		}

		if (Case == 0) {
			color = "Color: Black [Please change to an other color by pressing (1-8).]";
		}
		textSize(20);
		fill(0);
		text(color, width * 1 / 100, height * 1 / 50);
	}

	public void mousePressed() {
		if (mouseButton == LEFT) {
			if (onCollide()) {
				println("[Collision] sorry, but to close!");
			} else {
				pos.add(new PVector(mouseX, mouseY, diameter));
				createLED(pos.get(pos.size() - 1), Case, false);
				group.add(new Integer(Case));
			}
		} else if (mouseButton == RIGHT) {
			deleteLED();
		}
	}

	public void keyPressed() {

		switch (key) {
		case 'x':
			deleteLED();
			break;
		case DELETE:
			setup();
			break;
		case 'n':
			println("[INFO] You create a new file!");
			output = createWriter(posfile);
			break;
		case 's':
			savePattern();
			break;
		case 'l':
			loadPattern();
			break;
		case 'b':
			blink = !blink;
			break;
		case 'p':
			imgload = !imgload;
			break;
		case 'r':
			replace();
			break;
		case '1':
			color = "Color: RED";
			Case = 1;
			break;
		case '2':
			color = "Color: GREEN";
			Case = 2;
			break;
		case '3':
			color = "Color: BLUE";
			Case = 3;
			break;
		case '4':
			color = "Color: YELLOW";
			Case = 4;
			break;
		case '5':
			color = "Color: ORANGE";
			Case = 5;
			break;
		case ESC:
			exit();
			break;
		default:
			println("[KEY] This key is invalid!");
			break;
		}
	}

	public void createLED(PVector loc, int color, boolean toggleStatus) {
		switch (color) {
		case 0: // black
			fill(0);
			break;
		case 1: // red
			if (toggleStatus) {
				fill(100, 0, 0);
			} else {
				fill(255, 0, 0);
			}
			break;
		case 2: // green
			if (toggleStatus) {
				fill(0, 100, 0);
			} else {
				fill(0, 255, 0);
			}
			break;
		case 3: // blue
			if (toggleStatus) {
				fill(0, 0, 100);
			} else {
				fill(0, 0, 255);
			}
			break;
		case 4: // yellow
			if (toggleStatus) {
				fill(100, 100, 0);
			} else {
				fill(255, 255, 0);
			}
			break;
		case 5: // orange
			if (toggleStatus) {
				fill(100, 60, 20);
			} else {
				fill(255, 153, 51);
			}
			break;
		}
		noStroke();
		ellipse(loc.x, loc.y, loc.z, loc.z);
	}

	public void deleteLED() {
		for (int i = 0; i < pos.size(); i++) {
			if (di(pos.get(i), mouseX, mouseY, diameter / 2)) {
				group.remove(i);
				pos.remove(i);
				if (imgload) {
					background(img);
				} else {
					background(255);
				}
				break;
			}
		}
		for (int i = 0; i < pos.size(); i++) {
			createLED(pos.get(i), group.get(i), false);
		}
	}

	boolean onCollide() {
		for (int i = 0; i < pos.size(); i++) {
			if (!(di(pos.get(i), mouseX, mouseY, distance))) {
				collide = false;
			} else {
				collide = true;
				break;
			}
		}
		return collide;
	}

	public void savePattern() {
		if (!(pos.isEmpty())) {
			output = createWriter("LED.txt");
			for (int i = 0; i < pos.size(); i++) {
				output.println(ceil(pos.get(i).x) / factor + ";" + ceil(pos.get(i).y) / factor + ";"
						+ ceil(pos.get(i).z) / factor + ";" + group.get(i));
			}
			output.flush();
			output.close();
			if (debug) {
				for (int i = 0; i < pos.size(); i++) {
					println("[Data saved]" + " At: " + "[ x: " + pos.get(i).x + " y: " + pos.get(i).y + " diameter: "
							+ pos.get(i).z + " case: " + group.get(i) + "]");
				}
			} else {
				println("[Data saved]");
			}
		} else {
			println("[Data save failed] You can't save empty Patterns");
		}
	}

	public void loadPattern() {
		try {
			setup();
			int index = 0;
			for (int i = 0; i < input.length; i++) {
				if (index < input.length) {
					String[] para = split(input[index], ';');
					if (para.length == 4) {
						int x = Integer.parseInt(para[0]);
						int y = Integer.parseInt(para[1]);
						int r = Integer.parseInt(para[2]);
						String c = (para[3]);
						pos.add(new PVector(x * factor, y * factor, r * factor));
						group.add(new Integer(c));
						createLED(pos.get(index), group.get(index), false);
						index++;
					}
				}
			}
			if (input.length == 0) {
				println("[Data load failed] Fist you must save a pattern");
			} else {
				println("[Data Loaded]");
			}
		} catch (Exception e) {
			println("[ERROR] Please create fist a file or save a pattern");
		}
	}

	void replace() {
		for (int i = 0; i < pos.size(); i++) {
			if (di(pos.get(i), mouseX, mouseY, diameter / 2)) {
				group.set(i, Case);
				if (imgload) {
					background(img);
				} else {
					background(255);
				}
				break;
			}
		}
		for (int i = 0; i < pos.size(); i++) {
			createLED(pos.get(i), group.get(i), false);
		}
	}

	boolean di(PVector p1, int x2, int y2, int maxvalue) {
		int x = (int) (p1.x - x2);
		int y = (int) (p1.y - y2);
		if (x < 0) {
			x *= -1;
		}
		if (y < 0) {
			y *= -1;
		}
		double di = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

		if (di <= maxvalue) {
			return true;
		} else {
			return false;
		}
	}
}