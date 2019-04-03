package main;

import java.io.PrintWriter;
import java.util.ArrayList;

import processing.core.*;

public class LED extends PApplet {

	final boolean fullscreen = false;
	final boolean debug = true;
	final String imgfile = "Bananen.jpg";
	final String posfile = "LED.txt";
	int factor = 3;
	int size_w = 300 * factor;
	int size_h = 300 * factor;
	int distance = 10 * factor;
	int diameter = 5 * factor;
	final int Delay = 50;

	boolean toggleColor;
	boolean collide;
	boolean blink;
	boolean imgload;

	PrintWriter output;
	PImage img;
	String[] input;

	ArrayList<PVector> pos = new ArrayList<PVector>();

	public static void main(String[] args) {
		PApplet.main("main.LED");
	}

	public void settings() {
		if (fullscreen) {
			fullScreen();
		} else {
			size(size_w, size_h);
		}
		smooth(5);
	}

	public void setup() {
		pos.clear();
		input = loadStrings(posfile);
		img = loadImage(imgfile);
		img.resize(width, height);
	}

	public void draw() {
		if (blink) {
			if (frameCount % Delay == 0) {
				toggleColor = !toggleColor;
				for (int i = 0; i < pos.size(); i++) {
					createLED(pos.get(i), diameter, toggleColor);
				}
			}
		}
		if (imgload) {
			background(img);
		} else {
			background(255);
		}
		for (int i = 0; i < pos.size(); i++) {
			createLED(pos.get(i), diameter, toggleColor);
		}
	}

	public void mousePressed() {
		if (mouseButton == LEFT) {
			if (onCollide()) {
				println("[Collision] sorry, but to close!");
			} else {
				pos.add(new PVector(mouseX, mouseY));
				createLED(pos.get(pos.size() - 1), diameter, false);
			}
		} else if (mouseButton == RIGHT) {
			deleteLED();
		}
	}

	public void keyPressed() {
		if (key == 'x') {
			deleteLED();
		}
		if (key == DELETE) {
			setup();
		}
		if (key == 'n') {
			println("[INFO] You create a new file!");
			output = createWriter(posfile);
		}
		if (key == 's') {
			savePattern();
		}
		if (key == 'l') {
			loadPattern();
		}
		if (key == 'b') {
			blink = !blink;
		}
		if (key == 'p') {
			imgload = !imgload;
		}
	}

	public void createLED(PVector loc, int b, boolean color) {
		if (color) {
			fill(255, 0, 0);
		} else {
			fill(0);
		}
		stroke(0);
		strokeWeight(3);
		ellipse(loc.x, loc.y, b, b);
	}

	public void deleteLED() {
		for (int i = 0; i < pos.size(); i++) {
			if ((dist(pos.get(i).x, pos.get(i).y, mouseX, mouseY) <= diameter / 2)) {
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
			createLED(pos.get(i), diameter, false);
		}
	}

	boolean onCollide() {
		for (int i = 0; i < pos.size(); i++) {
			if ((dist(pos.get(i).x, pos.get(i).y, mouseX, mouseY) >= distance)) {
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
				output.println(
						ceil(pos.get(i).x) / factor + ";" + ceil(pos.get(i).y) / factor + ";" + diameter / factor);
			}
			output.flush();
			output.close();
			if (debug) {
				for (int i = 0; i < pos.size(); i++) {
					println("[Data saved]" + " At: " + "[ x: " + pos.get(i).x + " y: " + pos.get(i).y + " ]");
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
					if (para.length == 3) {
						int x = Integer.parseInt(para[0]);
						int y = Integer.parseInt(para[1]);
						int r = Integer.parseInt(para[2]);
						pos.add(new PVector(x * factor, y * factor));
						createLED(pos.get(index), r * factor, false);
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
}