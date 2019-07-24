package comp1206.sushi.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class GeoCode 
{
    private static final String GEO_CODE_SERVER = "http://api.postcodes.io/postcodes/";

    private static String getLocation(String code)
    {
        String address = buildUrl(code);

        String content = null;

        try
        {
            URL url = new URL(address);

            InputStream stream = url.openStream();

            try
            {
                int available = stream.available();

                byte[] bytes = new byte[available];

                stream.read(bytes);

                content = new String(bytes);
            }
            finally
            {
                stream.close();
            }

            return (String) content.toString();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private static String buildUrl(String code)
    {
        StringBuilder builder = new StringBuilder();

        builder.append(GEO_CODE_SERVER);

        builder.append(code.replaceAll(" ",""));
        return builder.toString();
    }
    
    
  public static String getLat(String store) {
    	String response = getLocation(store);
    	for(String s: response.split(",")){
    		if(s.contains("latitude")) {
    			return (s.split(":")[1]);
    		}
    	}
    	
    	
		return null;
    	
    }
    
  public static String getLong(String store) {
    	String response = getLocation(store);
    	for(String s: response.split(",")){
    		if(s.contains("longitude")) {
    			return (s.split(":")[1]);
    		}
    	}
    	
		return null;
    	
    }
     
     
     public static double haversine(double lat1, double lng1, double lat2, double lng2) {
    	    int r = 6371;
    	    double dLat = Math.toRadians(lat2 - lat1);
    	    double dLon = Math.toRadians(lng2 - lng1);
    	    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    	       Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) 
    	      * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    	    double d = r * c;
    	    return d;
    	}
}

