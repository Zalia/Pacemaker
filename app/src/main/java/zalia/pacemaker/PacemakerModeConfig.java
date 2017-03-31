package zalia.pacemaker;

import java.io.Serializable;

/**
 * Created by Zalia on 28.03.2017.
 */

public class PacemakerModeConfig implements Serializable{

    private int id;
    private int color;
    private int speed;
    private int rainbowness;
    private int length;
    private double brightness;
    private String split;
    private String heartbeat;

    public PacemakerModeConfig(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getRainbowness() {
        return rainbowness;
    }

    public void setRainbowness(int rainbowness) {
        this.rainbowness = rainbowness;
    }

    public double getBrightness() {
        return brightness;
    }

    public void setBrightness(double brightness) {
        this.brightness = brightness;
    }

    public String getSplit() {
        return split;
    }

    public void setSplit(String split) {
        this.split = split;
    }

    public String getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(String heartbeat) {
        this.heartbeat = heartbeat;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
