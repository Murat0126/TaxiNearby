package models;

import org.json.JSONException;
import org.json.JSONObject;
import com.yandex.mapkit.geometry.Point;

public class Drivers{

    public static Point convertToPoint(JSONObject jsonObject){
        Point point;
        try {
            double lat = jsonObject.getDouble("lat");
            double lon = jsonObject.getDouble("lon");
            point = new Point(lat, lon);
        } catch (JSONException e) {
            e.printStackTrace();
            point = new Point();
        }

        return point;
    }
}