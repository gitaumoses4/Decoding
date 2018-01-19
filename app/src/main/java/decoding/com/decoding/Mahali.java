package decoding.com.decoding;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mahali {

    @SerializedName("position")
    @Expose
    private List<Double> position = null;
    @SerializedName("speed")
    @Expose
    private String speed;

    public List<Double> getPosition() {
        return position;
    }

    public void setPosition(List<Double> position) {
        this.position = position;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String toString() {
        return "Position: " + position + " Speed: " + speed;
    }

}