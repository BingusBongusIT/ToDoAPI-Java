package org.BingusBongus.JSON;

/**
 * Class to handle conversion between json objects and parameters
 */
public class JSON
{
    /**
     * This function takes in a json object as String and returns the value
     * of the json parameter specified in param.
     * @param json
     * @param param
     * @return
     */
    public static String getPramFromJson(String json, String param)
    {
        String val = null;

        //Check if parameter is valid
        if(!json.contains(param))
            return null;

        json.replace("{" , "").replace("}", "");
        String[] params = json.split(",");

        for(int i = 0; i < params.length; i++)
        {
            if(params[i].contains(param))
            {
                val = params[i].split("\"")[3];
            }
        }

        return val;
    }
}
